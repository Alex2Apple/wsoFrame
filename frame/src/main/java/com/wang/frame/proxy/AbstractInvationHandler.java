package com.wang.frame.proxy;

import java.lang.reflect.InvocationHandler;

public abstract class AbstractInvationHandler implements InvocationHandler {

	private Class<?> clazz;

	public Class<?> getClassType() {
		return clazz;
	}

	public AbstractInvationHandler(Class<?> classType) {
		this.clazz = classType;
	}
}
