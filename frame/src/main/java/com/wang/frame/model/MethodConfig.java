package com.wang.frame.model;

import java.util.List;

/**
 * @author wangju
 *
 */
public class MethodConfig {

	private String methodName;

	private List<Class<?>> parameters;

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public List<Class<?>> getParameters() {
		return parameters;
	}

	public void setParameters(List<Class<?>> parameters) {
		this.parameters = parameters;
	}
}
