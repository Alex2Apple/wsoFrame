package com.wang.center.factory;

import com.wang.center.impl.DefaultRegistryCenterImpl;
import com.wang.registry.center.RegistryCenter;

/**
 * @author wangju
 *
 */
public class RegistryCenterFactory {
	private static final RegistryCenterFactory instance = new RegistryCenterFactory();
	private static RegistryCenter registryCenter;

	private RegistryCenterFactory() {
	}

	public static RegistryCenterFactory getInstance() {
		return instance;
	}

	public RegistryCenter getRegistryCenter() {
		if (registryCenter == null) {
			synchronized (registryCenter) {
				if (registryCenter == null) {
					registryCenter = new DefaultRegistryCenterImpl();
				}
			}
		}
		return registryCenter;
	}
}