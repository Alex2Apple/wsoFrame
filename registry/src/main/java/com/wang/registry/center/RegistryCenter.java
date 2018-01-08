package com.wang.registry.center;

import java.util.List;

import com.wang.registry.model.URL;
import com.wang.registry.server.RegistryServer;

/**
 * @author wangju
 *
 */
public interface RegistryCenter {

	void registry(final List<URL> urls);

	void registry(final URL url);

	void unregistry(final URL url);

	List<URL> subscribe(final List<URL> urls, final String subscriber);

	List<URL> subscribe(final URL url, final String subscriber);

	void unsubscribe(final URL url, final String subscriber);

	List<URL> notifyListener(final String subscriber);

	boolean heartBeat(String host, boolean isProvider);

	void setRegistryServer(RegistryServer registryServer);
}
