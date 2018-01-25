package com.wang.frame.remote;

import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wang.frame.remote.DefaultClient.ConnectionMetaData;

import com.wang.net.nio.NioConnection;

public class DefaultClientHandler implements HandlerAdapter {

	private Map<String, ConnectionMetaData> queues;

	public DefaultClientHandler(Map<String, ConnectionMetaData> queue) {
		this.queues = queue;
	}

	public boolean inOrOut() {
		return true;
	}

	@Override
	public byte[] handle(byte[] data, NioConnection connection) {
		JSONObject obj = JSON.parseObject(data, JSONObject.class);
		if (queues.containsKey(connection.getRequestId())) {
			queues.get(connection.getRequestId()).getQueue().offer(obj);
		}
		return data;
	}
}
