package com.wang.registry.center;

import com.wang.registry.model.URLs;
import com.wang.registry.server.RegistryServer;

/**
 * @author wangju
 *
 */
public interface AdminCenter {

	URLs getItems(int page);

	void setRegistryServer(RegistryServer registryServer);
}
