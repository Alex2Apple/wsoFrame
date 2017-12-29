package com.wang.wregistry.model;

import java.util.List;

import com.wang.wregistry.model.item.Item;

/**
 * @author wangju
 *
 */
public interface DataSource {

	void load() throws Exception;

	void save() throws Exception;

	void update(final List<Item> items) throws Exception;

	void setRepository(Repository repository);
}
