package com.wang.frame.rpc;

import com.wang.frame.model.URL;

/**
 * @author wangju
 *
 * @param <T>
 */
public interface Invoker<T> {

	void init();

	Class<T> getInterface();

	Object invoke(InvokeContext context);

	Invoker<T> addFilter(Filter<T> filter, Invoker<T> invoker);

	void destroy();

	URL getUrl();
}
