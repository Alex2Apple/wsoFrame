package com.wang.wregistry.model;

import java.util.List;

import com.wang.wregistry.model.item.Item;

/**
 * @author wangju
 *
 */
public interface SubscriberCenter {

	void setDataSource(AbstractDataSource source);

	void subcribe(String interfaceName, String host);

	void notifyPrepare(final DataWrapper dataWrapper);

	boolean notifyExecute(String host);

	List<Item> notifyFinish(String host);

	void notifyClear();
}
