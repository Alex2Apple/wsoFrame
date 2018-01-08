package com.wang.registry.model;

import java.util.List;

import com.wang.registry.config.RegistryConstants;

/**
 * @author wangju
 *
 */
public class SubscriberMetaData {
	private long timestamp;

	private boolean taken;

	private List<URL> items;

	public SubscriberMetaData(List<URL> items) {
		this.items = items;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public boolean isTaken() {
		return taken;
	}

	public void setTaken(boolean taken) {
		this.taken = taken;
	}

	public List<URL> getItems() {
		return items;
	}

	public void setItems(List<URL> items) {
		this.items = items;
	}

	public boolean isIdleTimeout() {
		return System.currentTimeMillis() > timestamp + RegistryConstants.DEFAULT_IDLE_TIMEOUT;
	}
}
