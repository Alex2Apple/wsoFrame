package com.wang.frame.remote;

import java.nio.charset.Charset;

import com.alibaba.fastjson.JSON;
import com.wang.frame.model.URL;
import com.wang.frame.proxy.ProxyFactory;
import com.wang.frame.rpc.Invocation;
import com.wang.frame.rpc.InvokeContext;
import com.wang.frame.rpc.Invoker;
import com.wang.frame.rpc.Result;
import com.wang.net.nio.NioConnection;

public class DefaultServerHandler implements HandlerAdapter {

	private Invocation<?> invocation;

	public DefaultServerHandler(Invocation<?> invocation) {
		this.invocation = invocation;
	}

	public boolean inOrOut() {
		return true;
	}

	public byte[] handle(byte[] data, NioConnection connection) {
		URL url = JSON.parseObject(data, URL.class);
		InvokeContext invokeContext = JSON.parseObject(JSON.toJSONString(url.getParameters()), InvokeContext.class);
		Invoker<?> invoker = (Invoker<?>) ProxyFactory.getInvoker(invocation.getRef(), invocation, invokeContext);
		Result result = (Result) invoker.invoke(invokeContext);
		byte[] w = JSON.toJSONString(result).getBytes(Charset.forName("UTF-8"));
		connection.write(w);
		return w;
	}
}
