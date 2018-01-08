package com.wang.registry.model;

import java.io.Serializable;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class URL implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1356191343545206769L;

	private String host;

	private int port;

	private String path;

	private String protocol;

	private Map<String, Object> parameters;

	private transient boolean deleted;

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

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public Map<String, Object> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, Object> parameters) {
		this.parameters = parameters;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
}
