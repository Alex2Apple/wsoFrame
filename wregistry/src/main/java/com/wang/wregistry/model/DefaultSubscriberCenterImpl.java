package com.wang.wregistry.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.wang.wregistry.model.item.Item;

/**
 * @author wangju
 *
 */
public class DefaultSubscriberCenterImpl implements SubscriberCenter, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2421287951886203498L;

	private static final int DEFAULT_INTERFACE_NUM = 200;
	private static final int DEFAULT_SUBSCRIBER_HOST = 20;

	private static final long DEFAULT_IDLE_TIMEOUT = 5 * 60 * 1000000; // 空闲时间5分钟, 单位微秒

	private AbstractDataSource source;

	/**
	 * interface_name->set(host)
	 */
	private Map<String, Set<String>> consumerHostMap;

	/**
	 * host->(SubscriberMetaData)
	 */
	private Map<String, SubscriberMetaData> updateHostMap;

	public DefaultSubscriberCenterImpl() {
		this.consumerHostMap = new ConcurrentHashMap<>(DEFAULT_INTERFACE_NUM);
		this.updateHostMap = new ConcurrentHashMap<>(DEFAULT_SUBSCRIBER_HOST);
	}

	@Override
	public void setDataSource(AbstractDataSource source) {
		this.source = source;
	}

	public Map<String, Set<String>> getConsumerHostMap() {
		return consumerHostMap;
	}

	public Map<String, SubscriberMetaData> getUpdateHostMap() {
		return updateHostMap;
	}

	@Override
	public void subcribe(String interfaceName, String host) {
		consumerHostMap.putIfAbsent(interfaceName, new HashSet<>());
		consumerHostMap.get(interfaceName).add(host);
	}

	@Override
	public void notifyPrepare(final DataWrapper dataWrapper) {
		Map<String, Map<String, List<Item>>> sMap = dataWrapper.getInterfaceMap();
		for (Entry<String, Map<String, List<Item>>> entry : sMap.entrySet()) {
			if (consumerHostMap.containsKey(entry.getKey())) {
				for (String h : consumerHostMap.get(entry.getKey())) {
					if (!updateHostMap.containsKey(h)) {
						SubscriberMetaData meta = new SubscriberMetaData(new ArrayList<>());
						updateHostMap.put(h, meta);
					}
					Iterator<List<Item>> iterator = source.getDataWrapper().getInterfaceMap().get(entry.getKey())
							.values().iterator();
					while (iterator.hasNext()) {
						updateHostMap.get(h).getItems().addAll(iterator.next());
					}
					updateHostMap.get(h).setTimestamp(System.currentTimeMillis());
				}
			}
		}
	}

	@Override
	public boolean notifyExecute(String host) {
		if (!updateHostMap.containsKey(host)) {
			return false;
		}
		updateHostMap.get(host).setTimestamp(System.currentTimeMillis());
		return !updateHostMap.get(host).isTaken();
	}

	@Override
	public List<Item> notifyFinish(String host) {
		if (!updateHostMap.containsKey(host)) {
			return new ArrayList<>();
		}
		return updateHostMap.get(host).getItems();
	}

	@Override
	public void notifyClear() {
		for (Entry<String, SubscriberMetaData> entry : updateHostMap.entrySet()) {
			if (entry.getValue().isTaken() && entry.getValue().isIdleTimeout()) {
				updateHostMap.remove(entry.getKey());
			}
		}
	}

	public class SubscriberMetaData {
		private long timestamp;

		private boolean taken;

		private List<Item> items;

		public SubscriberMetaData(List<Item> items) {
			this.items = items;
		}

		public long getTimestamp() {
			return timestamp;
		}

		public void setTimestamp(long timestamp) {
			this.timestamp = timestamp;
		}

		public boolean isTaken() {
			return taken;
		}

		public void setTaken(boolean taken) {
			this.taken = taken;
		}

		public List<Item> getItems() {
			return items;
		}

		public void setItems(List<Item> items) {
			this.items = items;
		}

		public boolean isIdleTimeout() {
			return System.currentTimeMillis() > timestamp + DEFAULT_IDLE_TIMEOUT;
		}
	}
}
