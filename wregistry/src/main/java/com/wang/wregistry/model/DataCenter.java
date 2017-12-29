package com.wang.wregistry.model;

import java.util.List;

import com.wang.wregistry.model.item.Item;
import com.wang.wregistry.model.item.Items;

/**
 * @author wangju
 *
 */
public interface DataCenter {

	Items getItems(int page) throws Exception;

	List<Item> getItems(String interfaceName) throws Exception;

	void setDataSource(AbstractDataSource source);
}
