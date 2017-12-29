package com.wang.wregistry.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.wang.wregistry.model.item.Item;
import com.wang.wregistry.server.RegistryServer;
import com.wang.wregistry.util.MapKeyUtil;

/**
 * @author wangju
 *
 */
public abstract class AbstractDataSource implements DataSource, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7840350561910956261L;

	private static final int DEFAULT_INTERFACE_NUM = 100;

	private RegistryServer registryServer;

	private DataWrapper dataWrapper;

	private RegistryCenter registryCenter;

	private SubscriberCenter subscriberCenter;

	private List<Item> itemsForList;

	private Repository repository;

	public AbstractDataSource() {
		this.dataWrapper = new DataWrapper(new ConcurrentHashMap<>(DEFAULT_INTERFACE_NUM));
	}

	public DataWrapper getDataWrapper() {
		return dataWrapper;
	}

	public void setDataWrapper(DataWrapper dataWrapper) {
		this.dataWrapper = dataWrapper;
	}

	public RegistryCenter getRegistryCenter() {
		return registryCenter;
	}

	public void setRegistryCenter(RegistryCenter registryCenter) {
		this.registryCenter = registryCenter;
	}

	public SubscriberCenter getSubscriberCenter() {
		return subscriberCenter;
	}

	public void setSubscriberCenter(SubscriberCenter subscriberCenter) {
		this.subscriberCenter = subscriberCenter;
	}

	public RegistryServer getRegistryServer() {
		return registryServer;
	}

	public void setRegistryServer(RegistryServer registryServer) {
		this.registryServer = registryServer;
	}

	public List<Item> getItemsForList() {
		return itemsForList;
	}

	public void setItemsForList(List<Item> itemsForList) {
		this.itemsForList = itemsForList;
	}

	public Repository getRepository() {
		return repository;
	}

	@Override
	public void setRepository(Repository repository) {
		this.repository = repository;
	}

	@Override
	public void load() throws Exception {
		// TODO
	}

	@Override
	public void save() throws Exception {
		// TODO
	}

	@Override
	public void update(final List<Item> items) {
		if (items.isEmpty()) {
			return;
		}

		// 更新数据源
		Map<String, Map<String, List<Item>>> temp = build(items);
		for (Entry<String, Map<String, List<Item>>> entry : temp.entrySet()) {
			if (!dataWrapper.getInterfaceMap().containsKey(entry.getKey())) {
				dataWrapper.getInterfaceMap().put(entry.getKey(), entry.getValue());
			} else {
				dataWrapper.getInterfaceMap().get(entry.getKey()).putAll(entry.getValue());
			}
		}

		// 注册提供者
		registryServer.getExecutorService().execute(new Runnable() {
			@Override
			public void run() {
				registryCenter.registry(items);
			}
		});

		final DataWrapper transport = new DataWrapper(temp);
		// 通知订阅的消费者
		registryServer.getExecutorService().execute(new Runnable() {
			@Override
			public void run() {
				subscriberCenter.notifyPrepare(transport);
			}
		});
	}

	public Map<String, Map<String, List<Item>>> build(final List<Item> items) {
		// 先将链表构建为map, 便于后续的去重和覆盖
		Set<String> deleteRepeatSet = new HashSet<>(); // 临时用于版本号去重, 降低数据结构复杂度
		// interface_name->map(host)->list(Item)
		Map<String, Map<String, List<Item>>> temp = new HashMap<>();
		for (Item item : items) {
			if (deleteRepeatSet
					.contains(MapKeyUtil.makeKey(item.getInterfaceName(), item.getHost(), item.getVersion()))) {
				continue;
			}
			deleteRepeatSet.add(MapKeyUtil.makeKey(item.getInterfaceName(), item.getHost(), item.getVersion()));

			Map<String, List<Item>> loop;
			if (!temp.containsKey(item.getInterfaceName())) {
				loop = new HashMap<>();
				temp.put(item.getInterfaceName(), loop);
			} else {
				loop = temp.get(item.getInterfaceName());
			}
			List<Item> m;
			if (!loop.containsKey(item.getHost())) {
				m = new ArrayList<>();
				loop.put(item.getHost(), m);
			} else {
				m = loop.get(item.getHost());
			}
			m.add(item);
		}
		return temp;
	}

	public void refreshItemsList() {
		for (Entry<String, Map<String, List<Item>>> entry : dataWrapper.getInterfaceMap().entrySet()) {
			Iterator<List<Item>> iterator = entry.getValue().values().iterator();
			while (iterator.hasNext()) {
				itemsForList.addAll(iterator.next());
			}
		}
	}
}
