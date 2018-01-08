package com.wang.registry.center;

import java.util.List;

import com.wang.registry.model.URL;
import com.wang.registry.server.RegistryServer;

/**
 * @author wangju
 *
 */
public interface ProviderCenter {

	void registry(final List<URL> items);

	void unregistry(final List<URL> items);

	void heartBeat(String provider);

	void clear();

	void setRegistryServer(RegistryServer registryServer);
}
