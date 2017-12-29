package com.wang.wregistry.config;

/**
 * @author wangju
 *
 */
public enum ErrorCode {
	ERR_INIT(1000, "init");

	private int code;
	private String errMsg;

	ErrorCode(int code, String msg) {
		this.setCode(code);
		this.setErrMsg(msg);
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}

	@Override
	public String toString() {
		return "[" + code + "|" + errMsg + "]";
	}
}
