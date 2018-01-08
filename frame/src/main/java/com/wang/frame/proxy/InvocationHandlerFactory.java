package com.wang.frame.proxy;

import java.lang.reflect.Method;

import com.wang.frame.rpc.InvokeContext;
import com.wang.frame.rpc.Invoker;

public class InvocationHandlerFactory {

	public static <T> AbstractInvationHandler make(final Class<?> clazz, final Invoker<T> invoker) {

		AbstractInvationHandler h = new AbstractInvationHandler(clazz) {

			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				if (method.getDeclaringClass() == Object.class) {
					return method.invoke(proxy, args);
				}

				if ("toString".equals(method.getName())) {
					return invoker.toString();
				}
				if ("hashCode".equals(method.getName())) {
					return invoker.hashCode();
				}
				if ("equals".equals(method.getName())) {
					return invoker.getInterface().getName().equals(clazz.getName());
				}

				System.out.println("proxy: " + clazz.getName());
				return invoker.invoke(new InvokeContext(method.getDeclaringClass(), method.getName(),
						method.getParameterTypes(), args));
			}
		};

		return h;
	}
}
