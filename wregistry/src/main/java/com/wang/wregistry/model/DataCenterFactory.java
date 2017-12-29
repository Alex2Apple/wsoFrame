package com.wang.wregistry.model;

/**
 * @author wangju
 *
 */
public class DataCenterFactory {
	private static final DataCenterFactory instance = new DataCenterFactory();
	private static DataCenter dataCenter;

	private DataCenterFactory() {
	}

	public static DataCenterFactory getInstance() {
		return instance;
	}

	public DataCenter getDataCenter() {
		if (dataCenter == null) {
			synchronized (dataCenter) {
				if (dataCenter == null) {
					dataCenter = new DefaultDataCenterImpl();
				}
			}
		}
		return dataCenter;
	}
}
