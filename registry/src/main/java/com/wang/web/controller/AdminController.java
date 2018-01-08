package com.wang.web.controller;

import static org.springframework.web.bind.annotation.RequestMethod.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wang.center.factory.AdminCenterFactory;
import com.wang.registry.cluster.ClusterNode;
import com.wang.registry.config.EnableResultWrap;
import com.wang.registry.config.ErrorCode;
import com.wang.registry.server.RegistryServer;

/**
 * @author wangju
 *
 */
@Controller
public class AdminController {

	@Autowired
	private RegistryServer registryServer;

	@RequestMapping(value = "/admin/services", method = GET)
	@EnableResultWrap
	@ResponseBody
	public Object itemsByPage(@RequestParam("page") int page) {
		return AdminCenterFactory.getInstance().getAdminCenter().getItems(page);
	}

	@RequestMapping(value = "/admin/cluster", method = GET)
	@EnableResultWrap
	@ResponseBody
	public Object cluster() {
		return registryServer.getCluster().nodes();
	}

	@RequestMapping(value = "/admin/cluster/replication", method = POST)
	@EnableResultWrap
	@ResponseBody
	public Object replication(@RequestBody ClusterNode node) throws Exception {
		if (node.isMaster()) {
			throw new Exception(String.format("[%s | %s | %s]", ErrorCode.ERR_REFUSE_OPERATOR.getCode(),
					ErrorCode.ERR_REFUSE_OPERATOR.getErrMsg(), "复制只能在slave节点上操作"));
		}
		registryServer.getCluster().replication();
		return null;
	}
}
