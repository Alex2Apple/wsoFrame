package com.wang.center.impl;

import java.util.ArrayList;
import java.util.List;

import com.wang.registry.center.RegistryCenter;
import com.wang.registry.model.URL;
import com.wang.registry.server.RegistryServer;

/**
 * @author wangju
 *
 */
public class DefaultRegistryCenterImpl implements RegistryCenter {

	private RegistryServer registryServer;

	@Override
	public void setRegistryServer(RegistryServer registryServer) {
		this.registryServer = registryServer;
	}

	@Override
	public void registry(final List<URL> urls) {
		registryServer.getProviderCenter().registry(urls);
		registryServer.getCluster().getMyself().setUpdated(true);
	}

	@Override
	public void registry(final URL url) {
		List<URL> urls = new ArrayList<>();
		urls.add(url);
		registry(urls);
	}

	@Override
	public void unregistry(final URL url) {
		List<URL> urls = new ArrayList<>();
		urls.add(url);
		registryServer.getProviderCenter().unregistry(urls);
		registryServer.getCluster().getMyself().setUpdated(true);
	}

	@Override
	public List<URL> subscribe(final List<URL> urls, final String subscriber) {
		List<URL> items = registryServer.getSubscriberCenter().subcribe(urls, subscriber);
		registryServer.getCluster().getMyself().setUpdated(true);
		return items;
	}

	@Override
	public List<URL> subscribe(final URL url, final String subscriber) {
		List<URL> urls = new ArrayList<>();
		urls.add(url);
		return subscribe(urls, subscriber);
	}

	@Override
	public void unsubscribe(final URL url, final String subscriber) {
		List<URL> urls = new ArrayList<>();
		urls.add(url);
		registryServer.getSubscriberCenter().unsubscribe(urls, subscriber);
		registryServer.getCluster().getMyself().setUpdated(true);
	}

	@Override
	public List<URL> notifyListener(final String subscriber) {
		List<URL> items = registryServer.getSubscriberCenter().notifyFinish(subscriber);
		registryServer.getCluster().getMyself().setUpdated(true);
		return items;
	}

	@Override
	public boolean heartBeat(String host, boolean isProvider) {
		if (isProvider) {
			registryServer.getProviderCenter().heartBeat(host);
			return true;
		} else {
			return registryServer.getSubscriberCenter().notifyExecute(host);
		}
	}

}
