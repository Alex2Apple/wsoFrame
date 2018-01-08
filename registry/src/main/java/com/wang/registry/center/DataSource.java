package com.wang.registry.center;

/**
 * @author wangju
 *
 */
public interface DataSource {

	void load() throws Exception;

	void save() throws Exception;

	void setRepository(Repository repository);
}
