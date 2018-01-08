package com.wang.registry.model;

import com.wang.registry.cluster.ClusterNode;

/**
 * @author wangju
 *
 */
public class ClusterRecoverCheck extends HeartBeat {

	private ClusterNode node;

	public ClusterNode getNode() {
		return node;
	}

	public void setNode(ClusterNode node) {
		this.node = node;
	}
}
