package com.wang.wnet.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * @author wangju
 *
 */
public class NioClientConnection extends AbstractNioConnection {
	private String remoteHost;
	private int remotePort;

	private String localHost;
	private int localPort;

	private long idleTimeout;

	private boolean isConnected;

	public NioClientConnection(SocketChannel socketChannel, String host, int port) {
		super(socketChannel);
		this.remoteHost = host;
		this.remotePort = port;
		this.idleTimeout = DEFAULT_IDLE_TIMEOUT;
	}

	public String getRemoteHost() {
		return remoteHost;
	}

	public int getRemotePort() {
		return remotePort;
	}

	public String getLocalHost() {
		return localHost;
	}

	public int getLocalPort() {
		return localPort;
	}

	public void setIdleTimeout(long idleTimeout) {
		this.idleTimeout = idleTimeout;
	}

	public long getIdleTimeout() {
		return idleTimeout;
	}

	public boolean isConnected() {
		return isConnected;
	}

	public void connect(Selector selector) throws IOException {
		final SocketChannel channel = getSocketChannel();
		channel.register(selector, SelectionKey.OP_READ, this);
		channel.connect(new InetSocketAddress(remoteHost, remotePort));
	}

	public boolean finishConnect() throws IOException {
		final SocketChannel channel = getSocketChannel();
		if (channel.isConnectionPending()) {
			channel.finishConnect();
			isConnected = true;
			localPort = channel.socket().getLocalPort();
			localHost = channel.socket().getLocalAddress().getHostAddress();
			return true;
		}
		return false;
	}

	@Override
	public boolean isIdleConnection() {
		return System.currentTimeMillis() > Math.max(getLastReadTime(), getLastWriteTime()) + idleTimeout;
	}
}
