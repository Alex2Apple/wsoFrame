package com.wang.center.factory;

import com.wang.center.impl.DefaultAdminCenterImpl;
import com.wang.registry.center.AdminCenter;

/**
 * @author wangju
 *
 */
public class AdminCenterFactory {
	private static final AdminCenterFactory instance = new AdminCenterFactory();
	private static AdminCenter adminCenter;

	private AdminCenterFactory() {
	}

	public static AdminCenterFactory getInstance() {
		return instance;
	}

	public AdminCenter getAdminCenter() {
		if (adminCenter == null) {
			synchronized (adminCenter) {
				if (adminCenter == null) {
					adminCenter = new DefaultAdminCenterImpl();
				}
			}
		}
		return adminCenter;
	}
}
