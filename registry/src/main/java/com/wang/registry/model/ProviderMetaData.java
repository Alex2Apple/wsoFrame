package com.wang.registry.model;

import java.util.Set;

import com.wang.registry.config.RegistryConstants;

/**
 * @author wangju
 *
 */
public class ProviderMetaData {
	private long timestamp;

	private Set<String> interfaceSet;

	public ProviderMetaData(Set<String> s) {
		this.interfaceSet = s;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setInterfaceSet(Set<String> interfaceSet) {
		this.interfaceSet = interfaceSet;
	}

	public Set<String> getInterfaceSet() {
		return interfaceSet;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public boolean isIdleTimeout() {
		return System.currentTimeMillis() > timestamp + RegistryConstants.DEFAULT_IDLE_TIMEOUT;
	}
}
