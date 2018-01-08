package com.wang.registry.center;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.wang.registry.config.RegistryConstants;
import com.wang.registry.model.ProviderMetaData;
import com.wang.registry.model.SubscriberMetaData;
import com.wang.registry.model.URL;
import com.wang.registry.model.URLMainEntity;
import com.wang.registry.server.RegistryServer;

/**
 * @author wangju
 *
 */
public abstract class AbstractDataSource implements DataSource {

	private static final Logger LOGGER = Logger.getLogger(AbstractDataSource.class);

	private AtomicBoolean health;

	private RegistryServer registryServer;

	private List<URL> urls;

	private Repository repository;

	/**
	 * service->[URL ...]
	 */
	private Map<String, List<URL>> interfaceMap;

	/**
	 * provider->(RegistryMetaData)
	 */
	private Map<String, ProviderMetaData> providerHostMap;

	/**
	 * service->set(subscriber)
	 */
	private Map<String, Set<String>> consumerHostMap;

	/**
	 * subscriber->(SubscriberMetaData)
	 */
	private Map<String, SubscriberMetaData> updateHostMap;

	public AbstractDataSource() {
		health = new AtomicBoolean(false);
		this.interfaceMap = new ConcurrentHashMap<>(RegistryConstants.DEFAULT_INTERFACE_NUM);
		this.providerHostMap = new ConcurrentHashMap<>(RegistryConstants.DEFATULT_PROVIDER_HOST);
		this.consumerHostMap = new ConcurrentHashMap<>(RegistryConstants.DEFAULT_INTERFACE_NUM);
		this.updateHostMap = new ConcurrentHashMap<>(RegistryConstants.DEFAULT_SUBSCRIBER_HOST);
	}

	public boolean isHealth() {
		return health.get();
	}

	public void setHealth(boolean health) {
		this.health.set(health);
	}

	public RegistryServer getRegistryServer() {
		return registryServer;
	}

	public void setRegistryServer(RegistryServer registryServer) {
		this.registryServer = registryServer;
	}

	public Map<String, List<URL>> getInterfaceMap() {
		return interfaceMap;
	}

	public void setInterfaceMap(Map<String, List<URL>> interfaceMap) {
		this.interfaceMap = interfaceMap;
	}

	public List<URL> getUrls() {
		return urls;
	}

	public void setUrls(List<URL> urls) {
		this.urls = urls;
	}

	public Repository getRepository() {
		return repository;
	}

	@Override
	public void setRepository(Repository repository) {
		this.repository = repository;
	}

	public Map<String, ProviderMetaData> getProviderHostMap() {
		return providerHostMap;
	}

	public void setProviderHostMap(Map<String, ProviderMetaData> providerHostMap) {
		this.providerHostMap = providerHostMap;
	}

	public Map<String, Set<String>> getConsumerHostMap() {
		return consumerHostMap;
	}

	public void setConsumerHostMap(Map<String, Set<String>> consumerHostMap) {
		this.consumerHostMap = consumerHostMap;
	}

	public Map<String, SubscriberMetaData> getUpdateHostMap() {
		return updateHostMap;
	}

	public void setUpdateHostMap(Map<String, SubscriberMetaData> updateHostMap) {
		this.updateHostMap = updateHostMap;
	}

	@Override
	public void load() throws Exception {
		registryServer.getCluster().recover();
		health.compareAndSet(false, true);
		LOGGER.info("load finished");
	}

	@Override
	public void save() throws Exception {
		LOGGER.info("save finished");
	}

	public void add(final List<URL> src) {
		if (src.isEmpty()) {
			return;
		}

		// 更新数据源
		for (URL item : src) {
			URLMainEntity entity = JSON.parseObject(JSON.toJSONString(item.getParameters()), URLMainEntity.class);
			if (!interfaceMap.containsKey(entity.getService())) {
				interfaceMap.put(entity.getService(), new ArrayList<>());
				interfaceMap.get(entity.getService()).add(item);
				continue;
			}

			Map<String, URL> re = new HashMap<>();
			synchronized (this) {
				List<URL> loop = interfaceMap.get(entity.getService());
				for (URL url : loop) {
					URLMainEntity e = JSON.parseObject(JSON.toJSONString(url.getParameters()), URLMainEntity.class);
					re.putIfAbsent(e.getService() + "_" + url.getHost(), url);
				}
			}
			re.put(entity.getService() + "_" + item.getHost(), item);
			interfaceMap.put(entity.getService(), new ArrayList<>(re.values()));
		}

		// 通知订阅的消费者
		notifySubscriber(src);
	}

	public void update(final List<URL> src) {
		add(src);
	}

	public void delete(final List<URL> src) {
		if (src.isEmpty()) {
			return;
		}

		// 更新数据源
		for (URL item : src) {
			URLMainEntity entity = JSON.parseObject(JSON.toJSONString(item.getParameters()), URLMainEntity.class);
			if (interfaceMap.containsKey(entity.getService())) {
				synchronized (this) {
					List<URL> loop = interfaceMap.get(entity.getService());
					for (URL url : loop) {
						URLMainEntity e = JSON.parseObject(JSON.toJSONString(url.getParameters()), URLMainEntity.class);
						if (item.getHost().equals(url.getHost()) && entity.getService().equals(e.getService())) {
							loop.remove(url);
						}
					}
				}
			}
		}

		// 通知订阅的消费者
		notifySubscriber(src);
	}

	@SuppressWarnings("unchecked")
	public void replication(Map<String, Object> src) {
		interfaceMap = (Map<String, List<URL>>) src.get("interfaceMap");
		providerHostMap = (Map<String, ProviderMetaData>) src.get("providerHostMap");
		consumerHostMap = (Map<String, Set<String>>) src.get("consumerHostMap");
		updateHostMap = (Map<String, SubscriberMetaData>) src.get("updateHostMap");
	}

	private void notifySubscriber(final List<URL> src) {
		// 通知订阅的消费者
		registryServer.getExecutorService().execute(new Runnable() {
			@Override
			public void run() {
				registryServer.getSubscriberCenter().notifyPrepare(src);
			}
		});
	}

	public List<URL> build(final List<URL> items) {
		Set<String> deleteRepeatSet = new HashSet<>();
		List<URL> result = new ArrayList<>();
		for (URL item : items) {
			URLMainEntity entity = JSON.parseObject(JSON.toJSONString(item.getParameters()), URLMainEntity.class);
			if (deleteRepeatSet.contains(entity.getService() + "_" + item.getHost())) {
				continue;
			}
			deleteRepeatSet.add(entity.getService() + "_" + item.getHost());
			result.add(item);
		}
		return result;
	}

	public void refreshUrls() {
		List<URL> loop = new ArrayList<>();
		for (Entry<String, List<URL>> entry : interfaceMap.entrySet()) {
			loop.addAll(entry.getValue());
		}
		urls = loop;
	}
}
