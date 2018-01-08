package com.wang.registry.cluster;

import com.wang.registry.server.RegistryServer;

/**
 * @author wangju
 *
 */
public class ClusterHeartBeatTask implements Runnable {

	private RegistryServer registryServer;

	public ClusterHeartBeatTask(RegistryServer registryServer) {
		this.registryServer = registryServer;
	}

	@Override
	public void run() {
		registryServer.getCluster().heartBeatHello();
	}

}
