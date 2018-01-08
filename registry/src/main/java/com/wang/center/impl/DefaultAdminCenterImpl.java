package com.wang.center.impl;

import com.wang.registry.center.AdminCenter;
import com.wang.registry.model.URLs;
import com.wang.registry.server.RegistryServer;
import com.wang.registry.util.PageHelperUtil;

/**
 * @author wangju
 *
 */
public class DefaultAdminCenterImpl implements AdminCenter {

	private RegistryServer registryServer;

	@Override
	public URLs getItems(int page) {
		URLs items = new URLs();
		items.setPage(page);

		int offset = PageHelperUtil.page2Offset(page);
		int limit = PageHelperUtil.DEFAULT_PAGE_SIZE;
		items.setItems(registryServer.getDataSource().getUrls().subList(offset, offset + limit));
		offset += limit;
		items.setHasNext(registryServer.getDataSource().getUrls().size() > offset);

		return items;
	}

	@Override
	public void setRegistryServer(RegistryServer registryServer) {
		this.registryServer = registryServer;
	}
}
