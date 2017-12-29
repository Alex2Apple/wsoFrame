package com.wang.wregistry.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.wang.wregistry.model.item.Item;

/**
 * @author wangju
 *
 */
public class DefaultRegistryCenterImpl implements RegistryCenter, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8700142823427137727L;

	private static final int DEFATULT_PROVIDER_HOST = 10;

	private static final long DEFAULT_IDLE_TIMEOUT = 5 * 60 * 1000000; // 空闲时间5分钟, 单位微秒

	private AbstractDataSource source;
	/**
	 * host->(RegistryMetaData)
	 */
	private Map<String, RegistryMetaData> providerHostMap;

	public DefaultRegistryCenterImpl() {
		this.providerHostMap = new ConcurrentHashMap<>(DEFATULT_PROVIDER_HOST);
	}

	@Override
	public void setDataSource(AbstractDataSource source) {
		this.source = source;
	}

	public Map<String, RegistryMetaData> getProviderHostMap() {
		return providerHostMap;
	}

	@Override
	public void registry(final List<Item> items) {
		Map<String, Map<String, List<Item>>> sMap = source.build(items);
		for (Entry<String, Map<String, List<Item>>> entry : sMap.entrySet()) {
			for (Entry<String, List<Item>> e : entry.getValue().entrySet()) {
				RegistryMetaData meta;
				if (!providerHostMap.containsKey(e.getKey())) {
					meta = new RegistryMetaData(new HashSet<>());
					providerHostMap.put(e.getKey(), meta);
				}
				providerHostMap.get(e.getKey()).setTimestamp(System.currentTimeMillis());
				providerHostMap.get(e.getKey()).getInterfaceSet().add(entry.getKey());
			}
		}
	}

	@Override
	public void unregistry(String host) {
		if (!providerHostMap.containsKey(host) || providerHostMap.get(host).getInterfaceSet().isEmpty()) {
			return;
		}

		for (String interfaceName : providerHostMap.get(host).getInterfaceSet()) {
			if (!source.getDataWrapper().getInterfaceMap().containsKey(interfaceName)) {
				continue;
			}
			source.getDataWrapper().getInterfaceMap().get(interfaceName).remove(host);
		}
	}

	@Override
	public void heartBeat(String host) {
		if (!providerHostMap.containsKey(host)) {
			return;
		}
		providerHostMap.get(host).setTimestamp(System.currentTimeMillis());
	}

	@Override
	public void clear() {
		final DataWrapper transport = new DataWrapper(new ConcurrentHashMap<>());
		for (Entry<String, RegistryMetaData> entry : providerHostMap.entrySet()) {
			if (entry.getValue().isIdleTimeout()) {
				providerHostMap.remove(entry.getKey());
				for (String inferfaceName : entry.getValue().getInterfaceSet()) {
					if (!source.getDataWrapper().getInterfaceMap().containsKey(inferfaceName)) {
						continue;
					}
					source.getDataWrapper().getInterfaceMap().get(inferfaceName).remove(entry.getKey());
					transport.getInterfaceMap().put(inferfaceName,
							source.getDataWrapper().getInterfaceMap().get(inferfaceName));
				}
			}
		}
		if (!transport.getInterfaceMap().isEmpty()) {
			source.getRegistryServer().getExecutorService().execute(new Runnable() {
				@Override
				public void run() {
					source.getSubscriberCenter().notifyPrepare(transport);
				}
			});
		}
	}

	public class RegistryMetaData {
		private long timestamp;

		private Set<String> interfaceSet;

		public RegistryMetaData(Set<String> s) {
			this.interfaceSet = s;
		}

		public Set<String> getInterfaceSet() {
			return interfaceSet;
		}

		public void setInterfaceSet(Set<String> interfaceSet) {
			this.interfaceSet = interfaceSet;
		}

		public long getTimestamp() {
			return timestamp;
		}

		public void setTimestamp(long timestamp) {
			this.timestamp = timestamp;
		}

		public boolean isIdleTimeout() {
			return System.currentTimeMillis() > timestamp + DEFAULT_IDLE_TIMEOUT;
		}
	}
}
