package com.wang.net.error;

/**
 * @author wangju
 *
 */
public enum ErrorCode {
	ERR_INIT("system error", 1000);

	private String msg;
	private int code;

	ErrorCode(String msg, int code) {
		this.msg = msg;
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public int getCode() {
		return code;
	}

	@Override
	public String toString() {
		return "[" + msg + "|" + code + "]";
	}
}
