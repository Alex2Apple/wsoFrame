package com.wang.wregistry.server;

import com.wang.wregistry.model.SubscriberCenter;

/**
 * @author wangju
 *
 */
public class SubscriberCleanTask implements Runnable {
	private SubscriberCenter subscriberCenter;

	public SubscriberCleanTask(final SubscriberCenter subscriberCenter) {
		this.subscriberCenter = subscriberCenter;
	}

	@Override
	public void run() {
		while (true) {
			subscriberCenter.notifyClear();
		}
	}
}
