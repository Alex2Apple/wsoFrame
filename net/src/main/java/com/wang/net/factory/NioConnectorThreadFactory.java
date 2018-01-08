package com.wang.net.factory;

import com.wang.net.nio.NioConnector;

/**
 * @author wangju
 *
 */
public class NioConnectorThreadFactory extends AbstractWorkerFactory {
	private static final NioConnectorThreadFactory instance = new NioConnectorThreadFactory();

	private NioConnectorThreadFactory() {

	}

	public NioConnectorThreadFactory getInstance() {
		return instance;
	}

	public Thread newThread(NioConnector nioConnector) {
		Thread thread = super.newThread(nioConnector);
		thread.setName(nioConnector.getName());
		return thread;
	}
}
