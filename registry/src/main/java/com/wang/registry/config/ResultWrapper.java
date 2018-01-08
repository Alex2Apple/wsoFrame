package com.wang.registry.config;

/**
 * @author wangju
 *
 */
public abstract class ResultWrapper {

	private int code;

	private String message;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	abstract public Object wrapper();
}
