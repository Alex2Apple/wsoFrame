package com.wang.wregistry.model;

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