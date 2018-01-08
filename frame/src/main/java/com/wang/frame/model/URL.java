package com.wang.frame.model;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wangju
 *
 */
public class URL {

	private String host;

	private int port;

	private String path;

	private String protocol;

	/**
	 * service
	 * version
	 * appName
	 * id
	 * method
	 */
	private Map<String, Object> parameters;

	private boolean provider;

	private boolean deleted;

	public String getHost() {
		return host;
	}

	public URL setHost(String host) {
		this.host = host;
		return this;
	}

	public int getPort() {
		return port;
	}

	public URL setPort(int port) {
		this.port = port;
		return this;
	}

	public String getPath() {
		return path;
	}

	public URL setPath(String path) {
		this.path = path;
		return this;
	}

	public String getProtocol() {
		return protocol;
	}

	public URL setProtocol(String protocol) {
		this.protocol = protocol;
		return this;
	}

	public Map<String, Object> getParameters() {
		return parameters;
	}

	public URL setParameters(Map<String, Object> parameters) {
		this.parameters = parameters;
		return this;
	}

	public boolean isProvider() {
		return provider;
	}

	public URL setProvider(boolean provider) {
		this.provider = provider;
		return this;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public URL setDeleted(boolean deleted) {
		this.deleted = deleted;
		return this;
	}

	public URL addParameter(String key, Object value) {
		if (parameters == null) {
			parameters = new HashMap<>();
		}
		parameters.put(key, value);
		return this;
	}

	public URL removeParameter(String key) {
		if (parameters != null) {
			parameters.remove(key);
		}
		return this;
	}

	public static URL build() {
		return new URL();
	}
}
