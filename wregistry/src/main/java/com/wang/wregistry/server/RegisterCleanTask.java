package com.wang.wregistry.server;

import com.wang.wregistry.model.RegistryCenter;

/**
 * @author wangju
 *
 */
public class RegisterCleanTask implements Runnable {
	private RegistryCenter registerCenter;

	public RegisterCleanTask(final RegistryCenter registerCenter) {
		this.registerCenter = registerCenter;
	}

	@Override
	public void run() {
		while (true) {
			registerCenter.clear();
		}
	}
}
