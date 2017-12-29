package com.wang.wregistry.model;

import java.util.List;

import com.wang.wregistry.model.item.Item;

/**
 * @author wangju
 *
 */
public interface RegistryCenter {

	void registry(final List<Item> items);

	void unregistry(String host);

	void heartBeat(String host);

	void clear();

	void setDataSource(AbstractDataSource source);
}
