package com.wang.frame.registry;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

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
import com.wang.frame.model.URL;
import com.wang.frame.registry.RegistryCluster.HostMetaData;

/**
 * @author wangju
 *
 */
public class RegistryNode implements RegistryService {

	private ThreadLocal<HostMetaData> localMeta = new ThreadLocal<>();

	protected static final Logger LOGGER = Logger.getLogger(RegistryCluster.class);
	private Map<String, HealthRecord> failRecord = new ConcurrentHashMap<>();

	private final String BATCH_REGISTRY_MAPPING = "/registry/batch";
	private final String SINGLE_REGISTRY_MAPPING = "/registry";
	private final String SINGLE_UNREGISTRY_MAPPING = "/unregistry";
	private final String REGISTRY_HEARTBEAT_MAPPING = "/registry/heartbeat";
	private final String BATCH_SUBSCRIBE_MAPPING = "/subscribe/batch";
	private final String SINGLE_SUBSCRIBE_MAPPING = "/subscribe";
	private final String SINGLE_UNSUBSCRIBE_MAPPING = "/unsubscribe";
	private final String SUBSCRIBE_HEARTBEAT_MAPPING = "/subscribe/heartbeat";
	private final String SUBSCRIBE_REFRESH_MAPPING = "/subscribe/refresh";

	@Override
	public void register(URL url) {
		Object rsp = httpInvoke(SINGLE_REGISTRY_MAPPING, false, url);
		responseCheck(rsp);
	}

	@Override
	public void unregister(URL url) {
		Object rsp = httpInvoke(SINGLE_UNREGISTRY_MAPPING, false, url);
		responseCheck(rsp);
	}

	@Override
	public Object subscribe(URL url) {
		Object rsp = httpInvoke(SINGLE_SUBSCRIBE_MAPPING, false, url);
		Object obj = responseCheck(rsp);
		updateCache(obj);
		return obj;
	}

	@Override
	public void unsubscribe(URL url) {
		Object rsp = httpInvoke(SINGLE_UNSUBSCRIBE_MAPPING, false, url);
		responseCheck(rsp);
	}

	@Override
	public void heartBeat(URL url) {
		if (url.isProvider()) {
			Object rsp = httpInvoke(REGISTRY_HEARTBEAT_MAPPING, true, null);
			responseCheck(rsp);
		} else {
			Object rsp = httpInvoke(SUBSCRIBE_HEARTBEAT_MAPPING, true, null);
			Object obj = responseCheck(rsp);
			if (obj != null) {
				HeartBeatMetaData meta = (HeartBeatMetaData) rsp;
				if (meta.isUpdated() && meta.getLocation() != null && !meta.getLocation().isEmpty()) {
					notifyListener(url);
				}
			}
		}
	}

	@Override
	public void notifyListener(URL url) {
		Object rsp = httpInvoke(SUBSCRIBE_REFRESH_MAPPING, true, null);
		updateCache(responseCheck(rsp));
	}

	protected void setRegistryNodeMetaData(HostMetaData meta) {
		localMeta.set(meta);
	}

	protected void updateCache(Object newValue) {

	}

	protected void disableNode(String host, int port) {

	}

	private Object httpInvoke(String mapping, boolean get, Object attach) {
		String url = "http://" + localMeta.get().getHost() + ":" + localMeta.get().getPort() + mapping;
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

	@SuppressWarnings("unchecked")
	private Object responseCheck(Object rsp) {
		String key = localMeta.get().getHost() + "_" + localMeta.get().getPort();
		if (rsp == null) {
			LOGGER.warn("request error");

			if (failRecord.containsKey(key)) {
				failRecord.get(key).incrFailCount();
				if (!failRecord.get(key).isHealth()) {
					disableNode(localMeta.get().getHost(), localMeta.get().getPort());
				}
			} else {
				failRecord.put(key, new HealthRecord());
			}
			return null;
		}
		failRecord.get(key).reset();

		Map<String, Object> obj = (Map<String, Object>) rsp;
		if (obj.containsKey("code") && Integer.valueOf(obj.get("code").toString()) != 0) {
			LOGGER.warn(obj.toString());
			return null;
		}

		if (obj.containsKey("data")) {
			return obj.get("data");
		}
		return null;
	}

	private class HealthRecord {
		private static final int DEFAULT_TIME_WINDOWS_MAX = 60 * 1000000; // 60s
		private static final int DEFAULT_CONTINUE_FAIL_MAX = 3; // 最大连续失败次数
		private long timestamp;
		private AtomicInteger fail;

		public void incrFailCount() {
			fail.incrementAndGet();
		}

		public void reset() {
			timestamp = System.currentTimeMillis();
			fail.set(0);
		}

		public boolean isHealth() {
			return System.currentTimeMillis() < timestamp + DEFAULT_TIME_WINDOWS_MAX
					&& fail.get() > DEFAULT_CONTINUE_FAIL_MAX;
		}
	}

	private class HeartBeatMetaData {
		private String lastTime;

		private String location;

		private boolean updated;

		public String getLastTime() {
			return lastTime;
		}

		public void setLastTime(String lastTime) {
			this.lastTime = lastTime;
		}

		public String getLocation() {
			return location;
		}

		public void setLocation(String location) {
			this.location = location;
		}

		public boolean isUpdated() {
			return updated;
		}

		public void setUpdated(boolean updated) {
			this.updated = updated;
		}
	}
}
