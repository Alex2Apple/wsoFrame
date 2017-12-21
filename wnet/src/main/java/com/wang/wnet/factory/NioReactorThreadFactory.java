package com.wang.wnet.factory;

import com.wang.wnet.nio.NioReactor;

/**
 * @author wangju
 *
 */
public class NioReactorThreadFactory extends AbstractWorkerFactory {
	private static final NioReactorThreadFactory instance = new NioReactorThreadFactory();

	private NioReactorThreadFactory() {

	}

	public NioReactorThreadFactory getInstance() {
		return instance;
	}

	public Thread newThread(NioReactor nioReactor) {
		Thread thread = super.newThread(nioReactor);
		thread.setName(nioReactor.getName());
		return thread;
	}
}
