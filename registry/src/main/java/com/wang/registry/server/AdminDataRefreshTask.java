package com.wang.registry.server;

import com.wang.registry.center.AbstractDataSource;

/**
 * @author wangju
 *
 */
public class AdminDataRefreshTask implements Runnable {

	private AbstractDataSource dataSource;

	public AdminDataRefreshTask(final AbstractDataSource source) {
		this.dataSource = source;
	}

	@Override
	public void run() {
		dataSource.refreshUrls();
	}

}
