package com.wang.registry.server;

import com.wang.registry.center.ProviderCenter;

/**
 * @author wangju
 *
 */
public class ProviderCleanTask implements Runnable {
	private ProviderCenter providerCenter;

	public ProviderCleanTask(final ProviderCenter providerCenter) {
		this.providerCenter = providerCenter;
	}

	@Override
	public void run() {
		providerCenter.clear();
	}
}
