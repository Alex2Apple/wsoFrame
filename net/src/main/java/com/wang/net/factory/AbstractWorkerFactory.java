package com.wang.net.factory;

import java.util.concurrent.ThreadFactory;

/**
 * @author wangju
 *
 */
public abstract class AbstractWorkerFactory implements ThreadFactory {

	@Override
	public Thread newThread(Runnable r) {
		return new Thread(r);
	}

}
