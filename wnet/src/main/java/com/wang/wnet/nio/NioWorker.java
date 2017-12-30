package com.wang.wnet.nio;

import java.nio.channels.SelectionKey;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

import com.wang.wnet.buffer.BufferPool;
import com.wang.wnet.handler.NioHandler;
import com.wang.wnet.meta.WorkerMetaData;

/**
 * @author wangju
 *
 */
public class NioWorker implements Runnable {
	private static final Logger LOGGER = Logger.getLogger(NioWorker.class);

	private BufferPool bufferPool;
	private NioWorkerGroup nioWorkerGroup;
	private List<NioHandler> handlers;

	private BlockingQueue<WorkerMetaData> reactorConQueue;

	public NioWorker(NioWorkerGroup nioWorkerGroup, int poolSize, int chunkSize) {
		this.nioWorkerGroup = nioWorkerGroup;
		this.bufferPool = new BufferPool(poolSize, chunkSize);
		this.reactorConQueue = new LinkedBlockingQueue<>();
		this.handlers = nioWorkerGroup.getHandlers();
	}

	public BufferPool getBufferPool() {
		return bufferPool;
	}

	public void setBufferPool(BufferPool bufferPool) {
		this.bufferPool = bufferPool;
	}

	public NioWorkerGroup getNioWorkerGroup() {
		return nioWorkerGroup;
	}

	public void setNioWorkerGroup(NioWorkerGroup nioWorkerGroup) {
		this.nioWorkerGroup = nioWorkerGroup;
	}

	public List<NioHandler> getHandlers() {
		return handlers;
	}

	public BlockingQueue<WorkerMetaData> getReactorConQueue() {
		return reactorConQueue;
	}

	public void setReactorConQueue(BlockingQueue<WorkerMetaData> reactorConQueue) {
		this.reactorConQueue = reactorConQueue;
	}

	@Override
	public void run() {
		for (;;) {
			try {
				WorkerMetaData meta = null;
				if ((meta = reactorConQueue.poll()) != null) {
					if ((meta.getReadyOps() & SelectionKey.OP_READ) != 0) {
						meta.getConnection().read();
					} else if ((meta.getReadyOps() & SelectionKey.OP_WRITE) != 0) {
						meta.getConnection().writeByEvent();
					}
				}
			} catch (Exception e) {
				LOGGER.warn("worker error", e);
			}
		}
	}
}
