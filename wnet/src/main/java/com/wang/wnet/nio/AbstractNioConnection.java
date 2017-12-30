package com.wang.wnet.nio;

import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

import com.wang.wnet.buffer.BufferQueue;
import com.wang.wnet.error.ErrorCode;
import com.wang.wnet.handler.NioHandler;
import com.wang.wnet.protocol.Packet;

/**
 * @author wangju
 *
 */
public abstract class AbstractNioConnection implements NioConnection {
	private static final Logger LOGGER = Logger.getLogger(AbstractNioConnection.class);

	protected static final long DEFAULT_IDLE_TIMEOUT = 180 * 1000000; // 连接默认空闲时间, 单位微秒

	private final SocketChannel socketChannel;
	private ByteBuffer readByteBuffer;
	private BufferQueue writeBufferQueue;

	private NioWorker nioWorker;

	private boolean isRegisted;
	private AtomicBoolean isClosed;
	private SelectionKey selectionKey;
	private Lock keyLock;

	private NioReactor reactor;
	private Lock reactorLock;

	private Packet packet;
	private int readBufferOffset;

	private List<NioHandler> handlers;

	private long lastReadTime;
	private long lastWriteTime;

	public AbstractNioConnection(SocketChannel socketChannel) {
		this.socketChannel = socketChannel;
		this.isClosed = new AtomicBoolean(false);
		this.keyLock = new ReentrantLock();
		this.reactorLock = new ReentrantLock();
	}

	public NioWorker getNioWorker() {
		return nioWorker;
	}

	public void setNioWorker(NioWorker nioWorker) {
		this.nioWorker = nioWorker;
	}

	public SocketChannel getSocketChannel() {
		return socketChannel;
	}

	public BufferQueue getWriteBufferQueue() {
		return writeBufferQueue;
	}

	public void setWriteBufferQueue(BufferQueue writeBufferQueue) {
		this.writeBufferQueue = writeBufferQueue;
	}

	public ByteBuffer getReadByteBuffer() {
		return readByteBuffer;
	}

	public void setReadByteBuffer(ByteBuffer readByteBuffer) {
		this.readByteBuffer = readByteBuffer;
	}

	public NioReactor getReactor() {
		return reactor;
	}

	public void setReactor(NioReactor reactor) {
		this.reactor = reactor;
	}

	public List<NioHandler> getHandlers() {
		return handlers;
	}

	public void setHandlers(List<NioHandler> handlers) {
		this.handlers = handlers;
	}

	public Packet getPacket() {
		return packet;
	}

	public void setPacket(Packet packet) {
		this.packet = packet;
	}

	public long getLastReadTime() {
		return lastReadTime;
	}

	public long getLastWriteTime() {
		return lastWriteTime;
	}

	@Override
	public void register(Selector selector) throws IOException {
		try {
			this.selectionKey = socketChannel.register(selector, SelectionKey.OP_READ, this);
			isRegisted = true;
		} finally {
			if (isClosed.get()) {
				clearSelectionKeys();
			}
		}
	}

	@Override
	public void read() throws IOException {
		if (readByteBuffer == null) {
			readByteBuffer = nioWorker.getBufferPool().allocate();
		}

		this.lastReadTime = System.currentTimeMillis();

		ByteBuffer buffer = readByteBuffer;
		int hasRead = socketChannel.read(buffer);
		if (hasRead == -1) {
			throw new EOFException();
		}

		int offset = readBufferOffset;
		int length = 0;
		int position = buffer.position();
		for (;;) {
			length = packet.getPacketLength(buffer, offset);
			if (packet.isPacketHeader(buffer, offset) && length == -1) {
				// 数据长度还未达到计算包长且是包头
				if (!buffer.hasRemaining()) {
					adjustReadBuffer(buffer, offset);
				}
				break;
			}

			if (position > offset + length) {
				// 能够处理一个完整数据包
				buffer.position(offset);
				packet.make(buffer, length);
				handle(packet.getBody(), true);
				packet.clear();
				offset += length;
				if (offset == position) {
					// 数据正好处理完成
					readBufferOffset = 0;
					buffer.clear();
					break;
				} else {
					// 还有剩余数据未处理
					readBufferOffset = offset;
					buffer.position(position);
					continue;
				}
			} else {
				// 数据包还不完整, 待下次处理
				if (!readByteBuffer.hasRemaining()) {
					adjustReadBuffer(buffer, offset);
				}
				break;
			}
		}

	}

	@Override
	public void write(ByteBuffer byteBuffer) {
		if (isClosed.get()) {
			return;
		}

		if (!isRegisted) {
			close();
		}

		try {
			writeBufferQueue.getQueue().offer(byteBuffer);
			enableWriteEvent();
		} catch (Exception e) {
			LOGGER.warn("connection write buffer to buffer-queue error", e);
		}
	}

	@Override
	public void writeByEvent() throws IOException {
		try {
			if (!doWrite() && writeBufferQueue.getQueue().isEmpty()) {
				disableWriteEvent();
			}
		} finally {

		}
	}

	private void disableWriteEvent() throws IOException {
		final Lock lock = reactorLock;
		lock.lock();
		try {
			int interestOps = selectionKey.interestOps() & ~SelectionKey.OP_WRITE;
			socketChannel.register(reactor.getSelector(), interestOps, this);
			reactor.getSelector().wakeup();
		} finally {
			lock.unlock();
		}
	}

	private void enableWriteEvent() throws IOException {
		final Lock lock = reactorLock;
		lock.lock();
		try {
			int interestOps = selectionKey.interestOps() & SelectionKey.OP_WRITE;
			socketChannel.register(reactor.getSelector(), interestOps, this);
			reactor.getSelector().wakeup();
		} finally {
			lock.unlock();
		}
	}

	private boolean doWrite() throws IOException {
		lastWriteTime = System.currentTimeMillis();

		// 先写上次遗留的数据
		ByteBuffer buffer = writeBufferQueue.getLeft();
		if (buffer != null) {
			socketChannel.write(buffer);
			if (buffer.hasRemaining()) {
				return true;
			}
			writeBufferQueue.setLeft(null);
			recycle(buffer);
		}

		buffer = writeBufferQueue.getQueue().poll();
		if (buffer == null) {
			return false;
		}

		if (buffer.position() == 0) {
			// 没有数据的buffer
			close();
			recycle(buffer);
			return false;
		}

		buffer.flip();
		socketChannel.write(buffer);
		// 没有写完, 下次再写
		if (buffer.hasRemaining()) {
			writeBufferQueue.setLeft(buffer);
			return true;
		} else {
			recycle(buffer);
		}

		return false;
	}

	private ByteBuffer adjustReadBuffer(ByteBuffer buffer, int offset) {
		if (offset == 0) {
			// TODO 是否需要限制下最大尺寸
			ByteBuffer newBuffer = ByteBuffer.allocate(buffer.capacity() << 1);

			buffer.position(offset);
			newBuffer.put(buffer);

			recycle(buffer);
			readByteBuffer = newBuffer;
			return newBuffer;
		} else {
			// 空间不够，压缩缓存
			buffer.position(offset);
			buffer.compact();
			readBufferOffset = 0;
			return buffer;
		}
	}

	public void recycle(ByteBuffer buffer) {
		nioWorker.getBufferPool().recycle(buffer);
	}

	@Override
	public boolean close() {
		if (isClosed.get()) {
			return false;
		}
		if (closeChannel()) {
			return isClosed.compareAndSet(false, true);
		}
		return false;
	}

	@Override
	public void handle(byte[] data, boolean in) {
		if (getHandlers() == null || getHandlers().isEmpty()) {
			return;
		}
		for (NioHandler handler : getHandlers()) {
			if (handler.inOrOut() == in) {
				data = handler.handle(data);
			}
		}
	}

	@Override
	public void error(ErrorCode errorCode, Throwable t) {
		LOGGER.error(String.format("connection error: %s", errorCode), t);
	}

	private boolean closeChannel() {
		clearSelectionKeys();

		SocketChannel channel = this.socketChannel;
		if (channel == null) {
			return true;
		}

		boolean isSocketClosed = false;
		Socket socket = channel.socket();
		if (socket != null) {
			try {
				socket.close();
				isSocketClosed = true;
			} catch (Exception e) {

			}
		}
		try {
			channel.close();
		} catch (Exception e) {

		}

		return isSocketClosed && !channel.isOpen();
	}

	private void clearSelectionKeys() {
		final Lock lock = keyLock;
		lock.lock();
		try {
			SelectionKey key = selectionKey;
			if (key != null && key.isValid()) {
				key.attach(null);
				key.cancel();
			}
		} finally {
			lock.unlock();
		}
	}

	abstract public boolean isIdleConnection();
}
