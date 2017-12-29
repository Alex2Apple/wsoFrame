package com.wang.wregistry.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import com.wang.wregistry.model.item.Item;
import com.wang.wregistry.model.item.Items;
import com.wang.wregistry.util.PageHelperUtil;

/**
 * @author wangju
 *
 */
public class DefaultDataCenterImpl implements DataCenter {

	private AbstractDataSource dataSource;

	@Override
	public Items getItems(int page) throws Exception {
		Items items = new Items();
		items.setPage(page);

		int offset = PageHelperUtil.page2Offset(page);
		int limit = PageHelperUtil.DEFAULT_PAGE_SIZE;
		items.setItems(dataSource.getItemsForList().subList(offset, offset + limit));
		offset += limit;
		items.setHasNext(dataSource.getItemsForList().size() > offset);

		return items;
	}

	@Override
	public List<Item> getItems(String interfaceName) throws Exception {
		if (!dataSource.getDataWrapper().getInterfaceMap().containsKey(interfaceName)) {
			return new ArrayList<>();
		}
		List<Item> items = new ArrayList<>();
		for (Entry<String, List<Item>> entry : dataSource.getDataWrapper().getInterfaceMap().get(interfaceName)
				.entrySet()) {
			items.addAll(entry.getValue());
		}

		return items;
	}

	@Override
	public void setDataSource(AbstractDataSource source) {
		this.dataSource = source;
	}
}
