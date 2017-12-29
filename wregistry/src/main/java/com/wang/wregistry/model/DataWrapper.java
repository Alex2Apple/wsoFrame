package com.wang.wregistry.model;

import java.util.List;
import java.util.Map;

import com.wang.wregistry.model.item.Item;

/**
 * @author wangju
 *
 */
public class DataWrapper {

	/**
	 * interface_name->map(host)->list(Item)
	 */
	private Map<String, Map<String, List<Item>>> interfaceMap;

	public DataWrapper(Map<String, Map<String, List<Item>>> interfaceMap) {
		this.interfaceMap = interfaceMap;
	}

	public Map<String, Map<String, List<Item>>> getInterfaceMap() {
		return interfaceMap;
	}

	public void setInterfaceMap(Map<String, Map<String, List<Item>>> interfaceMap) {
		this.interfaceMap = interfaceMap;
	}
}
