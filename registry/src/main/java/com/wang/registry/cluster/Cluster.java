package com.wang.registry.cluster;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.CharsetUtils;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.wang.registry.config.RegistryConstants;
import com.wang.registry.config.ReturnResultWrapper;
import com.wang.registry.model.ClusterHeartBeat;
import com.wang.registry.model.ClusterRecoverCheck;
import com.wang.registry.server.RegistryServer;

/**
 * @author wangju
 *
 */
public class Cluster implements RegistryCluster {
	private static final Logger LOGGER = Logger.getLogger(Cluster.class);

	private RegistryServer registryServer;

	private ClusterNode master;

	private ClusterNode myself;

	private ClusterNode slave;

	public Cluster(ClusterNode master, ClusterNode slave) {
		this.master = master;
		this.slave = slave;
	}

	public ClusterNode getMaster() {
		return master;
	}

	public void setMaster(ClusterNode master) {
		this.master = master;
	}

	public ClusterNode getMyself() {
		return myself;
	}

	public void setMyself(ClusterNode myself) {
		this.myself = myself;
	}

	public ClusterNode getSlave() {
		return slave;
	}

	public void setSlave(ClusterNode slave) {
		this.slave = slave;
	}

	public void setRegistryServer(RegistryServer registryServer) {
		this.registryServer = registryServer;
	}

	public List<ClusterNode> nodes() {
		List<ClusterNode> nodes = new ArrayList<>();
		nodes.add(master);
		nodes.add(slave);
		return nodes;
	}

	@Override
	public void heartBeatHello() {
		// slave给master发心跳包
		if (!myself.isMaster()) {
			ClusterHeartBeat clusterHeartBeat = (ClusterHeartBeat) httpInvoke(master,
					RegistryConstants.CLUSTER_HEARTBEAT_HELLO_MAPPING, true, null);
			if (clusterHeartBeat != null) {
				long timestamp = System.currentTimeMillis();
				myself.setTimestamp(timestamp);
				master.setTimestamp(timestamp);
				if (clusterHeartBeat.isUpdated()) {
					LOGGER.warn("heartBeatHello: master-slave switched!");
					registryServer.getExecutorService().execute(new Runnable() {

						@Override
						public void run() {
							replication();
						}
					});
				}
			} else {
				master.setTimeout(master.getTimeout() + 1);
				LOGGER.error("heartBeatHello: timeout!");
			}
		}
	}

	@Override
	public ClusterHeartBeat heartBeatAck() {
		// master给slave回复心跳包
		long timestamp = System.currentTimeMillis();
		myself.setTimestamp(timestamp);
		slave.setTimestamp(timestamp);
		ClusterHeartBeat ack = new ClusterHeartBeat();
		ack.setLastTime(new Date());
		ack.setUpdated(myself.isUpdated());

		return ack;
	}

	private Object httpInvoke(ClusterNode node, String mapping, boolean get, Object attach) {
		String url = "http://" + node.getHost() + ":" + node.getPort() + mapping;
		CloseableHttpClient httpClient = HttpClients.createMinimal();
		try {
			CloseableHttpResponse response;
			if (!get) {
				HttpPost httpPost = new HttpPost(url);
				if (attach != null) {
					StringEntity entity = new StringEntity(JSON.toJSONString(attach), CharsetUtils.get("utf-8"));
					entity.setContentType("application/json");
					httpPost.setEntity((HttpEntity) attach);
				}
				response = httpClient.execute(httpPost);
			} else {
				HttpGet httpGet = new HttpGet(url);
				response = httpClient.execute(httpGet);
			}

			try {
				// 只处理200状态的
				int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode == 200) {
					return JSON.parse(EntityUtils.toString(response.getEntity()));
				} else if (statusCode > 200 && statusCode < 400) {
					LOGGER.warn("http response: " + statusCode);
					return null;
				} else {
					throw new Exception("status code" + statusCode);
				}

			} catch (Exception e) {
				LOGGER.error("http request error!", e);
			} finally {
				response.close();
			}
		} catch (IOException e) {
			LOGGER.error("http request error!", e);
		} finally {
			try {
				httpClient.close();
			} catch (IOException e) {
				LOGGER.error("http client close error!", e);
			}
		}

		return null;
	}

	@Override
	public void replication() {
		doReplication(master);
	}

	@SuppressWarnings("unchecked")
	private void doReplication(ClusterNode node) {
		ReturnResultWrapper result = (ReturnResultWrapper) httpInvoke(node,
				RegistryConstants.CLUSTER_REPLICATION_MAPPING, true, null);
		if (result != null && result.getCode() == RegistryConstants.DEFAULT_RETURN_RESULT_CODE
				&& result.getData() != null) {
			registryServer.getDataSource().replication((Map<String, Object>) result.getData());
		} else {
			LOGGER.error("replication error");
		}
	}

	@Override
	public Object replicationAck() {
		Map<String, Object> ack = new HashMap<>();
		myself.setUpdated(false);
		ack.put("interfaceMap", registryServer.getDataSource().getInterfaceMap());
		ack.put("providerHostMap", registryServer.getDataSource().getProviderHostMap());
		ack.put("consumerHostMap", registryServer.getDataSource().getConsumerHostMap());
		ack.put("updateHostMap", registryServer.getDataSource().getUpdateHostMap());
		return ack;
	}

	public Object recoverCoCheckAck() {
		// master启动恢复, 给slave发送检测包, 看自己是否是宕机后启动
		// slave给master回复包
		ClusterRecoverCheck ack = new ClusterRecoverCheck();
		ack.setLastTime(new Date());
		ack.setUpdated(myself.isUpdated());
		ack.setNode(master);
		master.setTimeout(0);

		return ack;
	}

	@Override
	public void recover() {
		ClusterRecoverCheck ack = (ClusterRecoverCheck) httpInvoke(slave,
				RegistryConstants.CLUSTER_RECOVER_CHECK_MAPPING, true, null);
		if (ack.getNode().getTimeout() > 0 && ack.isUpdated()) {
			doReplication(slave);
		}
	}

}
