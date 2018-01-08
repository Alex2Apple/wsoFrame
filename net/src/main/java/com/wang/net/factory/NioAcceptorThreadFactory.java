package com.wang.net.factory;

import com.wang.net.nio.NioAcceptor;

/**
 * @author wangju
 *
 */
public class NioAcceptorThreadFactory extends AbstractWorkerFactory {
	private static final NioAcceptorThreadFactory instance = new NioAcceptorThreadFactory();

	private NioAcceptorThreadFactory() {

	}

	public static NioAcceptorThreadFactory getInstance() {
		return instance;
	}

	public Thread newThread(NioAcceptor nioAcceptor) {
		Thread thread = super.newThread(nioAcceptor);
		thread.setName(nioAcceptor.getName());

		return thread;
	}
}
