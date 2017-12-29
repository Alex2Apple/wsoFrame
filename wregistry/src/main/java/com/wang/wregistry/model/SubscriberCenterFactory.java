package com.wang.wregistry.model;

/**
 * @author wangju
 *
 */
public class SubscriberCenterFactory {
	private static final SubscriberCenterFactory instance = new SubscriberCenterFactory();
	private static SubscriberCenter subscriberCenter;

	private SubscriberCenterFactory() {
	}

	public static SubscriberCenterFactory getInstance() {
		return instance;
	}

	public SubscriberCenter getSubscriberCenter() {
		if (subscriberCenter == null) {
			synchronized (subscriberCenter) {
				if (subscriberCenter == null) {
					subscriberCenter = new DefaultSubscriberCenterImpl();
				}
			}
		}
		return subscriberCenter;
	}
}