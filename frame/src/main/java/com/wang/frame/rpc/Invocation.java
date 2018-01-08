package com.wang.frame.rpc;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

/**
 * @author wangju
 * @param <T>
 *
 */
public class Invocation<T> {

	private static final Logger LOGGER = Logger.getLogger(Invocation.class);
	private String appName;
	private ApplicationContext applicationContext;
	private T ref;

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getAppName() {
		return appName;
	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public T getRef() {
		return ref;
	}

	public void setRef(T ref) {
		this.ref = ref;
	}

	@SuppressWarnings("unchecked")
	public Object getInvoker(Class<?> clazz) {
		ref = (T) applicationContext.getBean(clazz);
		return ref;
	}

}
