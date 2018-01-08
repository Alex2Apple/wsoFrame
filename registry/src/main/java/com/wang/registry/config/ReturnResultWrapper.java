package com.wang.registry.config;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wangju
 *
 */
public class ReturnResultWrapper extends ResultWrapper {
	private Object data;

	public ReturnResultWrapper() {
		this(null);
	}

	public ReturnResultWrapper(Object data) {
		setCode(RegistryConstants.DEFAULT_RETURN_RESULT_CODE);
		setMessage(RegistryConstants.DEFAULT_RETURN_RESULT_MESSAGE);
		this.data = data;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	@Override
	public Object wrapper() {
		Map<String, Object> obj = new HashMap<>();
		obj.put(RegistryConstants.RESULT_CODE_KEY, getCode());
		obj.put(RegistryConstants.RESULT_MESSAGE_KEY, getMessage());
		if (data != null) {
			obj.put(RegistryConstants.RETURN_RESULT_ATTACH_KEY, data);
		}

		return obj;
	}

}
