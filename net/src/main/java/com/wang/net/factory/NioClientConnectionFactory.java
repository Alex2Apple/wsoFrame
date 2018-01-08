package com.wang.net.factory;

import java.io.IOException;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

import com.wang.net.buffer.BufferQueue;
import com.wang.net.nio.NioClientConnection;
import com.wang.net.nio.NioConnector;

/**
 * @author wangju
 *
 */
public class NioClientConnectionFactory {
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

	/**
	 * If success, return socket channel, otherwise, throws a IOException
	 * 
	 * @return
	 * @throws IOException
	 */
	private static SocketChannel openSocketChannel() throws IOException {
		SocketChannel socketChannel = null;
		try {
			socketChannel = SocketChannel.open();
			socketChannel.configureBlocking(false);
			Socket socket = socketChannel.socket();
			socket.setTcpNoDelay(true);
			socket.setKeepAlive(false);
			socket.setReceiveBufferSize(getRecvBufferSize());
			socket.setSendBufferSize(getRecvBufferSize());

		} catch (IOException e) {
			closeChannel(socketChannel);
			throw e;
		}

		return socketChannel;
	}

	public static NioClientConnection make(String host, int port) throws IOException {
		NioClientConnection conn = new NioClientConnection(openSocketChannel(), host, port);
		conn.setHandlers(new ArrayList<>());
		conn.setPacket(DefaultPacketFactory.getInstance().create());
		conn.setWriteBufferQueue(new BufferQueue());
		return conn;
	}

	public static void postConnect(NioClientConnection conn, NioConnector connector) {
		connector.getToConnQueue().offer(conn);
	}

	private static void closeChannel(SocketChannel socketChannel) {
		if (socketChannel == null) {
			return;
		}

		Socket socket = socketChannel.socket();
		if (socket != null) {
			try {
				socket.close();
			} catch (Exception e) {

			}
		}

		try {
			socketChannel.close();
		} catch (Exception e) {

		}
	}

}
