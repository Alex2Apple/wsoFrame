package com.wang.registry.config;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wangju
 *
 */
public class ErrorCodeWrapper extends ResultWrapper {
	private String detail;

	public ErrorCodeWrapper(int code, String message, String detail) {
		setCode(code);
		setMessage(message);
		this.detail = detail;
	}

	@Override
	public Object wrapper() {
		Map<String, Object> obj = new HashMap<>();
		obj.put(RegistryConstants.RESULT_CODE_KEY, getCode());
		obj.put(RegistryConstants.RESULT_MESSAGE_KEY, getMessage());
		obj.put(RegistryConstants.ERROR_RESULT_ATTACH_KEY, detail);
		return obj;
	}
}
