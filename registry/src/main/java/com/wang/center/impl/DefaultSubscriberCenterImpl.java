package com.wang.center.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;

import com.alibaba.fastjson.JSON;
import com.wang.registry.center.SubscriberCenter;
import com.wang.registry.model.SubscriberMetaData;
import com.wang.registry.model.URL;
import com.wang.registry.model.URLMainEntity;
import com.wang.registry.server.RegistryServer;

/**
 * @author wangju
 *
 */
public class DefaultSubscriberCenterImpl implements SubscriberCenter {

	private RegistryServer registryServer;

	@Override
	public void setRegistryServer(RegistryServer registryServer) {
		this.registryServer = registryServer;
	}

	@Override
	public List<URL> subcribe(final List<URL> items, final String subscriber) {
		List<URL> result = new ArrayList<>();

		for (URL item : items) {
			URLMainEntity entity = JSON.parseObject(JSON.toJSONString(item.getParameters()), URLMainEntity.class);
			if (!registryServer.getDataSource().getInterfaceMap().containsKey(entity.getService())) {
				continue;
			}
			registryServer.getDataSource().getConsumerHostMap().putIfAbsent(entity.getService(), new HashSet<>());
			result.addAll(registryServer.getDataSource().getInterfaceMap().get(entity.getService()));
			registryServer.getDataSource().getConsumerHostMap().get(entity.getService()).add(subscriber);
		}

		return result;
	}

	@Override
	public void unsubscribe(final List<URL> items, final String subscriber) {
		if (items.isEmpty()) {
			return;
		}

		for (URL item : items) {
			URLMainEntity entity = JSON.parseObject(JSON.toJSONString(item.getParameters()), URLMainEntity.class);
			if (registryServer.getDataSource().getConsumerHostMap().containsKey(entity.getService())) {
				registryServer.getDataSource().getConsumerHostMap().get(entity.getService()).remove(subscriber);
			}
		}
	}

	@Override
	public void notifyPrepare(final List<URL> items) {
		if (items.isEmpty()) {
			return;
		}

		for (URL item : items) {
			URLMainEntity entity = JSON.parseObject(JSON.toJSONString(item.getParameters()), URLMainEntity.class);
			if (registryServer.getDataSource().getConsumerHostMap().containsKey(entity.getService())) {
				for (String h : registryServer.getDataSource().getConsumerHostMap().get(entity.getService())) {
					if (!registryServer.getDataSource().getUpdateHostMap().containsKey(h)) {
						SubscriberMetaData meta = new SubscriberMetaData(new ArrayList<>());
						registryServer.getDataSource().getUpdateHostMap().put(h, meta);
					}
					registryServer.getDataSource().getUpdateHostMap().get(h).getItems()
							.addAll(registryServer.getDataSource().getInterfaceMap().get(entity.getService()));
					registryServer.getDataSource().getUpdateHostMap().get(h).setTimestamp(System.currentTimeMillis());
				}
			}
		}
	}

	@Override
	public boolean notifyExecute(final String subscriber) {
		if (!registryServer.getDataSource().getUpdateHostMap().containsKey(subscriber)) {
			return false;
		}
		registryServer.getDataSource().getUpdateHostMap().get(subscriber).setTimestamp(System.currentTimeMillis());
		return !registryServer.getDataSource().getUpdateHostMap().get(subscriber).isTaken();
	}

	@Override
	public List<URL> notifyFinish(final String subscriber) {
		if (!registryServer.getDataSource().getUpdateHostMap().containsKey(subscriber)) {
			return new ArrayList<>();
		}
		registryServer.getDataSource().getUpdateHostMap().get(subscriber).setTaken(true);
		return registryServer.getDataSource().getUpdateHostMap().get(subscriber).getItems();
	}

	@Override
	public void clear() {
		for (Entry<String, SubscriberMetaData> entry : registryServer.getDataSource().getUpdateHostMap().entrySet()) {
			if (entry.getValue().isTaken() && entry.getValue().isIdleTimeout()) {
				registryServer.getDataSource().getUpdateHostMap().remove(entry.getKey());
			}
		}
	}
}
