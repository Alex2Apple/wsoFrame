package com.wang.net.nio;

import java.nio.channels.SocketChannel;

/**
 * @author wangju
 *
 */
public class NioServerConnection extends AbstractNioConnection {

	private String remoteHost;
	private int remotePort;

	private String localHost;
	private int localPort;

	private long idleTimeout;

	public NioServerConnection(SocketChannel socketChannel) {
		super(socketChannel);
		this.remoteHost = socketChannel.socket().getInetAddress().getHostAddress();
		this.remotePort = socketChannel.socket().getPort();
		this.localHost = socketChannel.socket().getLocalAddress().getHostAddress();
		this.localPort = socketChannel.socket().getLocalPort();
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

	@Override
	public boolean isIdleConnection() {
		return System.currentTimeMillis() > Math.max(getLastReadTime(), getLastWriteTime()) + idleTimeout;
	}
}
