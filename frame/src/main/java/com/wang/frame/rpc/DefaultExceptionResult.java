package com.wang.frame.rpc;

/**
 * @author wangju
 *
 */
public class DefaultExceptionResult implements Result {

	private Throwable throwable;

	public DefaultExceptionResult(Throwable throwable) {
		this.throwable = throwable;
	}

	@Override
	public Object getValue() {
		return null;
	}

	@Override
	public Throwable getException() {
		return throwable;
	}

	@Override
	public boolean isThrowExcepion() {
		return true;
	}

}
