package com.wang.frame.registry;

import com.wang.frame.model.URL;

/**
 * @author wangju
 *
 */
public interface RegistryService {

	void register(URL url);

	void unregister(URL url);

	Object subscribe(URL url);

	void unsubscribe(URL url);

	void heartBeat(URL url);

	void notifyListener(URL url);
}
