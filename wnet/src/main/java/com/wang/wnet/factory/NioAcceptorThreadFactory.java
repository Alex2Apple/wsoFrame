package com.wang.wnet.factory;

import com.wang.wnet.nio.NioAcceptor;

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
