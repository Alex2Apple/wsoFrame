package com.wang.frame.rpc;

/**
 * @author wangju
 *
 */
public class DefaultResult implements Result {

	private Object value;

	public DefaultResult(Object value) {
		this.value = value;
	}

	@Override
	public Object getValue() {
		return value;
	}

	@Override
	public Throwable getException() {
		return null;
	}

	@Override
	public boolean isThrowExcepion() {
		return false;
	}

}
