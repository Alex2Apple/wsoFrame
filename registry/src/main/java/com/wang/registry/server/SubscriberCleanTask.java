package com.wang.registry.server;

import com.wang.registry.center.SubscriberCenter;

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
		subscriberCenter.clear();
	}
}
