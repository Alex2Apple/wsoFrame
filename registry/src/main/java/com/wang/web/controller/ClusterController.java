package com.wang.web.controller;

import static org.springframework.web.bind.annotation.RequestMethod.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wang.registry.config.EnableResultWrap;
import com.wang.registry.config.RegistryConstants;
import com.wang.registry.server.RegistryServer;

/**
 * @author wangju
 *
 */
@Controller
public class ClusterController {

	@Autowired
	private RegistryServer registryServer;

	@RequestMapping(value = RegistryConstants.CLUSTER_HEARTBEAT_HELLO_MAPPING, method = GET)
	@ResponseBody
	public Object heartBeat() {
		return registryServer.getCluster().heartBeatAck();
	}

	@RequestMapping(value = RegistryConstants.CLUSTER_REPLICATION_MAPPING, method = GET)
	@ResponseBody
	@EnableResultWrap
	public Object replication() {
		return registryServer.getCluster().replicationAck();
	}

	@RequestMapping(value = RegistryConstants.CLUSTER_RECOVER_CHECK_MAPPING, method = GET)
	@ResponseBody
	public Object recoverCheck() {
		return registryServer.getCluster().recoverCoCheckAck();
	}
}
