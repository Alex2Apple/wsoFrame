package com.wang.wnet.factory;

import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

import com.wang.wnet.buffer.BufferQueue;
import com.wang.wnet.nio.NioServerConnection;

/**
 * @author wangju
 *
 */
public class NioServerConnectionFactory {
	private final static int DEFAULT_RECV_BUFFER_SIZE = 1024 * 4;
	private final static int DEFAULT_SEND_BUFFER_SIZE = 1024 * 16;

	private int recvBufferSize;
	private int sendBufferSize;

	public void setRecvBufferSize(int size) {
		this.recvBufferSize = size;
	}

	public int getRecvBufferSize() {
		return (recvBufferSize == 0) ? DEFAULT_RECV_BUFFER_SIZE : recvBufferSize;
	}

	public void setSendBufferSize(int size) {
		this.sendBufferSize = size;
	}

	public int getSendBufferSize() {
		return (sendBufferSize == 0) ? DEFAULT_SEND_BUFFER_SIZE : sendBufferSize;
	}

	public NioServerConnection make(SocketChannel socketChannel) throws Exception {
		socketChannel.configureBlocking(false);

		// TODO NioWorker延迟加载
		NioServerConnection con = new NioServerConnection(socketChannel);
		con.setPacket(DefaultPacketFactory.getInstance().create());
		con.setHandlers(new ArrayList<>());

		Socket socket = socketChannel.socket();
		socket.setReceiveBufferSize(getRecvBufferSize());
		socket.setSendBufferSize(getSendBufferSize());
		socket.setTcpNoDelay(true);
		socket.setKeepAlive(true);

		con.setWriteBufferQueue(new BufferQueue());

		return con;
	}

}
