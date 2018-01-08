package com.wang.net.factory;

import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

import com.wang.net.buffer.BufferQueue;
import com.wang.net.nio.NioServerConnection;

/**
 * @author wangju
 *
 */
public class NioServerConnectionFactory {
	private final static int DEFAULT_RECV_BUFFER_SIZE = 1024 * 4;
	private final static int DEFAULT_SEND_BUFFER_SIZE = 1024 * 16;

	private static int recvBufferSize;
	private static int sendBufferSize;

	public static void setRecvBufferSize(int size) {
		recvBufferSize = size;
	}

	public static int getRecvBufferSize() {
		return (recvBufferSize == 0) ? DEFAULT_RECV_BUFFER_SIZE : recvBufferSize;
	}

	public static void setSendBufferSize(int size) {
		sendBufferSize = size;
	}

	public static int getSendBufferSize() {
		return (sendBufferSize == 0) ? DEFAULT_SEND_BUFFER_SIZE : sendBufferSize;
	}

	public static NioServerConnection make(SocketChannel socketChannel) throws Exception {
		socketChannel.configureBlocking(false);

		// NioWorker延迟加载
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
