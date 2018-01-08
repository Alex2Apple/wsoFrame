package com.wang.frame.bean;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * @author wangju
 *
 */
public class ReferencerBean<T> extends ReferencerConfig<T> implements InitializingBean, FactoryBean<T>, DisposableBean {

	public ReferencerBean(Referencer referencer) {
		super(referencer);
	}

	@Override
	public void destroy() throws Exception {
		super.destroy();
	}

	@Override
	public T getObject() throws Exception {
		return get();
	}

	@Override
	public Class<?> getObjectType() {
		return getType();
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		init();
	}

}
