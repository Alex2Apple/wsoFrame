package com.wang.wregistry.model;

/**
 * @author wangju
 *
 */
public class DataSourceFactory {
	private static final DataSourceFactory instance = new DataSourceFactory();
	private static AbstractDataSource dataSource;

	private DataSourceFactory() {

	}

	public static DataSourceFactory getInstance() {
		return instance;
	}

	public AbstractDataSource getDataSource() {
		if (dataSource == null) {
			synchronized (dataSource) {
				if (dataSource == null) {
					dataSource = new DefaultDataSourceImpl();
				}
			}
		}

		return dataSource;
	}
}
