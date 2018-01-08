package com.wang.registry.center;

import java.util.List;

import com.wang.registry.model.URL;
import com.wang.registry.server.RegistryServer;

/**
 * @author wangju
 *
 */
public interface SubscriberCenter {

	List<URL> subcribe(final List<URL> items, final String subscriber);

	void unsubscribe(final List<URL> items, final String subscriber);

	void notifyPrepare(final List<URL> items);

	boolean notifyExecute(final String subscriber);

	List<URL> notifyFinish(final String subscriber);

	void clear();

	void setRegistryServer(RegistryServer registryServer);
}
