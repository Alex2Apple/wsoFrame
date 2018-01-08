package com.wang.frame.rpc;

/**
 * @author wangju
 *
 */
public class InvokeContext {

	private Class<?> service;
	private String methodName;
	private Class<?>[] parameterTypes;
	private Object[] parameters;

	public InvokeContext(Class<?> clazz, String methodName, Class<?>[] types, Object[] args) {
		this.service = clazz;
		this.methodName = methodName;
		this.parameterTypes = types;
		this.parameters = args;
	}

	public Class<?> getService() {
		return service;
	}

	public void setService(Class<?> service) {
		this.service = service;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public Class<?>[] getParameterTypes() {
		return parameterTypes;
	}

	public void setParameterTypes(Class<?>[] parameterTypes) {
		this.parameterTypes = parameterTypes;
	}

	public Object[] getParameters() {
		return parameters;
	}

	public void setParameters(Object[] parameters) {
		this.parameters = parameters;
	}
}
