package com.wang.registry.config;

/**
 * @author wangju
 *
 */
public enum ErrorCode {
	ERR_SYSTEM_INTERNAL(1000, "系统内部错误"), ERR_REFUSE_OPERATOR(1001, "拒绝操作"), ERR_UNKNOWN(1002, "未知异常");

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
}
