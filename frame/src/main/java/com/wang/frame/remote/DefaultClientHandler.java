package com.wang.frame.remote;

import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wang.frame.model.URL;
import com.wang.frame.proxy.ProxyFactory;
import com.wang.frame.remote.DefaultClient.ConnectionMetaData;
import com.wang.frame.rpc.Invocation;
import com.wang.frame.rpc.InvokeContext;
import com.wang.frame.rpc.Invoker;
import com.wang.frame.rpc.Result;

public class DefaultClientHandler implements HandlerAdapter {

	private Map<String, ConnectionMetaData> queues;

	public DefaultClientHandler(Map<String, ConnectionMetaData> queue) {
		this.queues = queue;
	}

	public boolean inOrOut() {
		return true;
	}

	public void handle(byte[] data, NioConnection connection) {
		JSONObject obj = JSON.parseObject(data, JSONObject.class);
		if (queues.containsKey(connection.getRequestId())) {
			queues.get(connection.getRequestId()).getQueue.offer(obj);
		}
	}
}
