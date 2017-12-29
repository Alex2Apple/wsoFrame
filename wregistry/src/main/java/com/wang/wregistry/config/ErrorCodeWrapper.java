package com.wang.wregistry.config;

/**
 * @author wangju
 *
 */
public class ErrorCodeWrapper {
	private int code;
	private String message;
	private String detail;

	public ErrorCodeWrapper(ErrorCode error, String detail) {
		this.code = error.getCode();
		this.message = error.getErrMsg();
		this.detail = detail;
	}

	@Override
	public String toString() {
		return "[" + code + "|" + message + "|" + detail + "]";
	}
}
