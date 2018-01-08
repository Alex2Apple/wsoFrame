package com.wang.frame.rpc;

public interface Filter<T> {

	Object invoke(Invoker<T> invoker, InvokeContext context);
}
