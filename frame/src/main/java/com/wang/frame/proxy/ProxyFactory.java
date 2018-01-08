package com.wang.frame.proxy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.wang.frame.model.URL;
import com.wang.frame.rpc.DefaultExceptionResult;
import com.wang.frame.rpc.DefaultResult;
import com.wang.frame.rpc.Filter;
import com.wang.frame.rpc.Invocation;
import com.wang.frame.rpc.InvokeContext;
import com.wang.frame.rpc.Invoker;

public class ProxyFactory {

	@SuppressWarnings("unchecked")
	public static <T> T getProxy(AbstractInvationHandler h) {
		Class<?> clazz = h.getClassType();

		return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[] { clazz }, h);
	}

	@SuppressWarnings("unchecked")
	public static <T> T getInvoker(T proxy, final Invocation<?> invocation, final InvokeContext invokeContext) {
		return (T) new Invoker<T>() {

			@Override
			public void init() {

			}

			@Override
			public Class<T> getInterface() {
				return (Class<T>) invokeContext.getService();
			}

			@Override
			public Object invoke(InvokeContext context) {
				Object proxy = invocation.getInvoker(invokeContext.getService());
				try {
					Method method = invokeContext.getService().getMethod(invokeContext.getMethodName(),
							invokeContext.getParameterTypes());
					return new DefaultResult(method.invoke(proxy, invokeContext.getParameters()));
				} catch (NoSuchMethodException e) {
					return new DefaultExceptionResult(e);
				} catch (SecurityException e) {
					return new DefaultExceptionResult(e);
				} catch (IllegalAccessException e) {
					return new DefaultExceptionResult(e);
				} catch (IllegalArgumentException e) {
					return new DefaultExceptionResult(e);
				} catch (InvocationTargetException e) {
					return new DefaultExceptionResult(e);
				}
			}

			@Override
			public Invoker<T> addFilter(Filter<T> filter, Invoker<T> invoker) {
				return this;
			}

			@Override
			public void destroy() {

			}

			@Override
			public URL getUrl() {
				return null;
			}
		};
	}
}
