package com.wang.registry.cluster;

import com.wang.registry.model.ClusterHeartBeat;

/**
 * @author wangju
 *
 */
public interface RegistryCluster {

	void heartBeatHello();

	ClusterHeartBeat heartBeatAck();

	void replication();

	Object replicationAck();

	void recover();
}
