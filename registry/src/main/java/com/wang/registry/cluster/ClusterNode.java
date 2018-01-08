package com.wang.registry.cluster;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author wangju
 *
 */
public class ClusterNode {

	private String host;

	private int port;

	private boolean isMaster;

	private boolean isValid;

	private long timestamp;

	private int timeout;

	private AtomicBoolean updated;

	public ClusterNode() {
		this(null, 0);
	}

	public ClusterNode(String host, int port) {
		this.host = host;
		this.port = port;
		this.timestamp = System.currentTimeMillis();
		this.updated = new AtomicBoolean(false);
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public boolean isMaster() {
		return isMaster;
	}

	public void setMaster(boolean isMaster) {
		this.isMaster = isMaster;
	}

	public boolean isValid() {
		return isValid;
	}

	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public boolean isUpdated() {
		return updated.get();
	}

	public void setUpdated(boolean updated) {
		this.updated.set(updated);
	}
}
