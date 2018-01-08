package com.wang.center.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;

import com.alibaba.fastjson.JSON;
import com.wang.registry.center.ProviderCenter;
import com.wang.registry.model.ProviderMetaData;
import com.wang.registry.model.URL;
import com.wang.registry.model.URLMainEntity;
import com.wang.registry.server.RegistryServer;

/**
 * @author wangju
 *
 */
public class DefaultProviderCenterImpl implements ProviderCenter {

	private RegistryServer registryServer;

	@Override
	public void setRegistryServer(RegistryServer registryServer) {
		this.registryServer = registryServer;
	}

	@Override
	public void registry(final List<URL> items) {
		if (items.isEmpty()) {
			return;
		}

		List<URL> src = registryServer.getDataSource().build(items);
		for (URL item : src) {
			if (!registryServer.getDataSource().getProviderHostMap().containsKey(item.getHost())) {
				registryServer.getDataSource().getProviderHostMap().put(item.getHost(),
						new ProviderMetaData(new HashSet<>()));
			}
			registryServer.getDataSource().getProviderHostMap().get(item.getHost())
					.setTimestamp(System.currentTimeMillis());
			registryServer.getDataSource().getProviderHostMap().get(item.getHost()).getInterfaceSet()
					.add(JSON.parseObject((JSON.toJSONString(item.getParameters())), URLMainEntity.class).getService());
		}

		registryServer.getExecutorService().execute(new Runnable() {
			@Override
			public void run() {
				registryServer.getDataSource().add(items);
			}
		});
	}

	@Override
	public void unregistry(final List<URL> items) {
		if (items.isEmpty()) {
			return;
		}

		List<URL> src = registryServer.getDataSource().build(items);
		for (URL url : src) {
			String host = url.getHost();
			if (!registryServer.getDataSource().getProviderHostMap().containsKey(host)
					|| registryServer.getDataSource().getProviderHostMap().get(host).getInterfaceSet().isEmpty()) {
				continue;
			}
			registryServer.getDataSource().getProviderHostMap().get(host).getInterfaceSet()
					.remove((JSON.parseObject(JSON.toJSONString(url.getParameters()), URLMainEntity.class)).getService());
		}

		registryServer.getExecutorService().execute(new Runnable() {
			@Override
			public void run() {
				registryServer.getDataSource().delete(src);
			}
		});
	}

	@Override
	public void heartBeat(String provider) {
		if (!registryServer.getDataSource().getProviderHostMap().containsKey(provider)) {
			return;
		}
		registryServer.getDataSource().getProviderHostMap().get(provider).setTimestamp(System.currentTimeMillis());
	}

	@Override
	public void clear() {
		final List<URL> items = new ArrayList<>();
		for (Entry<String, ProviderMetaData> entry : registryServer.getDataSource().getProviderHostMap().entrySet()) {
			if (entry.getValue().isIdleTimeout()) {
				for (String inferfaceName : entry.getValue().getInterfaceSet()) {
					if (registryServer.getDataSource().getInterfaceMap().containsKey(inferfaceName)) {
						items.addAll(registryServer.getDataSource().getInterfaceMap().get(inferfaceName));
					}
				}
				registryServer.getDataSource().getProviderHostMap().remove(entry.getKey());
			}
		}
		if (!items.isEmpty()) {
			registryServer.getDataSource().getRegistryServer().getExecutorService().execute(new Runnable() {
				@Override
				public void run() {
					registryServer.getDataSource().delete(items);
				}
			});
		}
	}
}
