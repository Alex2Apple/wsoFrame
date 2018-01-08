package com.wang.frame.registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.util.Assert;

import com.alibaba.fastjson.JSON;
import com.wang.frame.config.FrameConstants;
import com.wang.frame.model.URLEntity;
import com.wang.frame.model.URL;
import com.wang.frame.util.FrameHelperUtil;

/**
 * @author wangju
 *
 */
public class RegistryCluster extends RegistryNode {

	private static final int DEFAULT_HEART_BEAT_DELAY = 180; // 3分钟
	private ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
	private Map<String, List<URL>> urlCache = new ConcurrentHashMap<>();
	private HostMetaData mHost;
	private List<HostMetaData> sHosts;
	private ReentrantLock hLock = new ReentrantLock();

	private RegistryCluster() {
		init();
	}

	public static RegistryCluster create() {
		return new RegistryCluster();
	}

	/**
	 * 
	 */
	private void init() {
		String master = FrameHelperUtil.readProperty(FrameConstants.REGISTRY_CLUSTER_MASTER_KEY,
				FrameConstants.FRAME_PROPERTIES_FILE);
		String slave = FrameHelperUtil.readProperty(FrameConstants.REGISTRY_CLUSTER_SLAVE_KEY,
				FrameConstants.FRAME_PROPERTIES_FILE);

		Assert.notNull(master, "registry.cluster.master's value must be not null");
		Assert.notNull(slave, "registry.cluster.slave's value must be not null");
		Assert.hasText(master, "registry.cluster.master's value must be not empty");
		Assert.hasText(slave, "registry.cluster.slave's value must be not empty");

		master = master.split(",")[0];
		slave = slave.split(",")[0];

		String[] ms = master.split(":");
		String[] ss = slave.split(":");
		Assert.isTrue(ms.length == 2, "registry.cluster.master's value must be #ip#:#port#");
		Assert.isTrue(ms.length == 2, "registry.cluster.slave's value must be #ip#:#port#");

		mHost = new HostMetaData(ms[0], Integer.valueOf(ms[1]));
		sHosts = new ArrayList<>();
		sHosts.add(new HostMetaData(ss[0], Integer.valueOf(ss[1])));

		scheduledExecutorService.scheduleWithFixedDelay(new Runnable() {

			/*
			 * 提供者侧与注册中心的心跳
			 */
			@Override
			public void run() {
				HostMetaData meta = selectHost();
				URL url = URL.build().setHost(meta.host).setPort(meta.port).setProvider(true);
				heartBeat(url);
			}
		}, 60, DEFAULT_HEART_BEAT_DELAY, TimeUnit.SECONDS);

		scheduledExecutorService.scheduleWithFixedDelay(new Runnable() {

			/*
			 * 消费者侧与注册中心的心跳
			 */
			@Override
			public void run() {
				HostMetaData meta = selectHost();
				URL url = URL.build().setHost(meta.host).setPort(meta.port).setProvider(false);
				heartBeat(url);
			}
		}, 60, DEFAULT_HEART_BEAT_DELAY, TimeUnit.SECONDS);
	}

	@Override
	public void register(URL url) {
		HostMetaData meta = selectHost();
		url.setHost(meta.host);
		url.setPort(meta.port);
		setRegistryNodeMetaData(meta);
		super.register(url);
	}

	@Override
	public void unregister(URL url) {
		HostMetaData meta = selectHost();
		url.setHost(meta.host);
		url.setPort(meta.port);
		setRegistryNodeMetaData(meta);
		super.unregister(url);
	}

	@Override
	public Object subscribe(URL url) {
		String serviceKey = (JSON.parseObject(JSON.toJSONString(url.getParameters()), URLEntity.class).getService());
		if (urlCache.containsKey(serviceKey)) {
			return urlCache.get(serviceKey);
		}

		HostMetaData meta = selectHost();
		url.setHost(meta.host);
		url.setPort(meta.port);
		setRegistryNodeMetaData(meta);
		super.subscribe(url);
		return urlCache.get(serviceKey);
	}

	@Override
	public void unsubscribe(URL url) {
		String serviceKey = (JSON.parseObject(JSON.toJSONString(url.getParameters()), URLEntity.class).getService());
		urlCache.remove(serviceKey);

		HostMetaData meta = selectHost();
		url.setHost(meta.host).setPort(meta.port);
		setRegistryNodeMetaData(meta);
		super.unsubscribe(url);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void updateCache(Object newValue) {
		if (newValue == null) {
			return;
		}
		List<URL> list = (List<URL>) newValue;
		Map<String, List<URL>> n = new HashMap<>();
		for (URL url : list) {
			URLEntity entity = JSON.parseObject(JSON.toJSONString(url.getParameters()), URLEntity.class);
			if (!n.containsKey(entity.getService())) {
				n.put(entity.getService(), new ArrayList<>());
			}
			n.get(entity.getService()).add(url);
		}

		for (Entry<String, List<URL>> entry : n.entrySet()) {
			urlCache.put(entry.getKey(), entry.getValue());
		}
	}

	@Override
	protected void disableNode(String host, int port) {
		final Lock lock = hLock;
		lock.lock();
		try {
			if (host.equals(mHost.host) && port == mHost.port) {
				mHost.setValid(false);
			} else {
				for (HostMetaData meta : sHosts) {
					if (host.equals(meta.host) && port == meta.port) {
						meta.setValid(false);
						break;
					}
				}
			}
		} finally {
			lock.unlock();
		}
	}

	private HostMetaData selectHost() {
		final Lock lock = hLock;
		lock.lock();
		try {
			if (mHost.isValid()) {
				return mHost;
			}

			List<HostMetaData> t = new ArrayList<>();
			for (HostMetaData h : sHosts) {
				if (h.isValid()) {
					t.add(h);
				}
			}
			Assert.notEmpty(t, "Haven't valid registry node in the cluster");

			Random r = new Random(t.size());
			return t.get(r.nextInt());
		} finally {
			lock.unlock();
		}
	}

	public class HostMetaData {
		private String host;
		private int port;
		private boolean valid;

		private HostMetaData(String host, int port) {
			this.host = host;
			this.port = port;
			this.valid = true;
		}

		public boolean isValid() {
			return valid;
		}

		public void setValid(boolean valid) {
			this.valid = valid;
		}

		public String getHost() {
			return host;
		}

		public int getPort() {
			return port;
		}
	}
}
