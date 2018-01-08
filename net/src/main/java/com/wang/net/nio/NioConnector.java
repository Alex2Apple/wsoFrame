package com.wang.net.nio;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

/**
 * @author wangju
 *
 */
public class NioConnector implements Runnable {
	private static final Logger LOGGER = Logger.getLogger(NioConnector.class);

	private final String name;
	private final Selector selector;
	private final BlockingQueue<NioClientConnection> toConnQueue;
	private final NioReactor reactor;

	private long hasConnected;

	public NioConnector(String name, NioReactor reactor) throws IOException {
		this.name = name;
		this.selector = Selector.open();
		this.reactor = reactor;
		this.toConnQueue = new LinkedBlockingQueue<>();
	}

	public String getName() {
		return name;
	}

	public BlockingQueue<NioClientConnection> getToConnQueue() {
		return toConnQueue;
	}

	@Override
	public void run() {
		final Selector selector = this.selector;
		for (;;) {
			try {
				selector.select(1000L);
				connect(selector);

				Set<SelectionKey> keys = selector.selectedKeys();
				try {
					for (SelectionKey key : keys) {
						Object att = key.attachment();
						if (att == null || !key.isValid() || !key.isConnectable()) {
							key.cancel();
						}
						finishConnect(key, (NioClientConnection) att);
					}
				} finally {
					keys.clear();
				}

			} catch (Exception e) {
				LOGGER.warn(getName(), e);
			}
		}
	}

	private void finishConnect(SelectionKey key, NioClientConnection conn) {
		final NioClientConnection c = conn;
		try {
			if (c.finishConnect()) {
				clearSelectionKey(key);
				c.setReactor(reactor);
				postRegister(c);
				++hasConnected;
			}
		} catch (Exception e) {
			clearSelectionKey(key);
			LOGGER.warn(getName(), e);
		}
	}

	private void postRegister(AbstractNioConnection con) {
		reactor.getConnQueue().offer(con);
		reactor.getSelector().wakeup();
	}

	private void connect(Selector selector) {
		NioClientConnection conn = null;
		while ((conn = toConnQueue.poll()) != null) {
			try {
				conn.connect(selector);
			} catch (Exception e) {
				LOGGER.warn(getName(), e);
			}
		}
	}

	private void clearSelectionKey(SelectionKey key) {
		if (key.isValid()) {
			key.attach(null);
			key.cancel();
		}
	}

	public long getHasConnected() {
		return hasConnected;
	}
}
