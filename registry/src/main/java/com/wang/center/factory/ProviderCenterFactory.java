package com.wang.center.factory;

import com.wang.center.impl.DefaultProviderCenterImpl;
import com.wang.registry.center.ProviderCenter;

/**
 * @author wangju
 *
 */
public class ProviderCenterFactory {
	private static final ProviderCenterFactory instance = new ProviderCenterFactory();
	private static ProviderCenter providerCenter;

	private ProviderCenterFactory() {
	}

	public static ProviderCenterFactory getInstance() {
		return instance;
	}

	public ProviderCenter getProviderCenter() {
		if (providerCenter == null) {
			synchronized (providerCenter) {
				if (providerCenter == null) {
					providerCenter = new DefaultProviderCenterImpl();
				}
			}
		}
		return providerCenter;
	}
}