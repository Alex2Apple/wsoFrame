package com.wang.wnet.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Set;

import org.apache.log4j.Logger;

import com.wang.wnet.factory.NioServerConnectionFactory;

/**
 * @author wangju
 *
 */
public class NioAcceptor implements Runnable {
	private static final Logger LOGGER = Logger.getLogger(NioAcceptor.class);
	private static final int MAX_ACCEPT_BACKLOG = 7;

	private final String name;
	private final int port;
	private final Selector selector;
	private final ServerSocketChannel serverSocketChannel;
	private final NioServerConnectionFactory connFactory;
	private final NioReactor reactor;

	private long hasAccept;

	public NioAcceptor(String name, int port, int backlog, NioServerConnectionFactory factory, NioReactor reactor)
			throws IOException {
		this.name = name;
		this.port = port;
		this.selector = Selector.open();
		this.serverSocketChannel = ServerSocketChannel.open();
		this.serverSocketChannel.bind(new InetSocketAddress(port),
				backlog > MAX_ACCEPT_BACKLOG ? MAX_ACCEPT_BACKLOG : backlog);
		this.serverSocketChannel.configureBlocking(false);
		this.serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
		this.connFactory = factory;
		this.reactor = reactor;
	}

	public String getName() {
		return name;
	}

	public int getPort() {
		return port;
	}

	public long getHasAccept() {
		return hasAccept;
	}

	@Override
	public void run() {
		final Selector selector = this.selector;

		for (;;) {
			try {
				selector.select(1000L);
				Set<SelectionKey> keys = selector.selectedKeys();
				try {
					for (SelectionKey key : keys) {
						if (key.isValid() && key.isAcceptable()) {
							accept();
						} else {
							key.cancel();
						}
					}
				} finally {
					keys.clear();
				}
			} catch (Exception e) {
				LOGGER.warn(getName(), e);
			}
		}
	}

	private void accept() {
		SocketChannel socketChannel = null;
		try {
			++hasAccept;
			socketChannel = serverSocketChannel.accept();
			AbstractNioConnection con = connFactory.make(socketChannel);
			postRegister(con);
		} catch (Exception e) {
			closeChannel(socketChannel);
			LOGGER.warn(getName(), e);
		}
	}

	private void postRegister(AbstractNioConnection con) {
		reactor.getConnQueue().offer(con);
		reactor.getSelector().wakeup();
	}

	private void closeChannel(SocketChannel socketChannel) {
		if (socketChannel == null) {
			return;
		}

		Socket socket = socketChannel.socket();
		if (socket != null) {
			try {
				socket.close();
			} catch (Exception e) {
				LOGGER.warn("accept exception, close socket error", e);
			}
		}

		try {
			socketChannel.close();
		} catch (Exception e) {
			LOGGER.warn("accept exception, close channel error", e);
		}
	}

}
