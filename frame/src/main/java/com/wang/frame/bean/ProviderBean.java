package com.wang.frame.bean;

import java.io.IOException;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

public class ProviderBean<T> extends ProviderConfig<T> implements ApplicationContextAware, InitializingBean,
		ApplicationListener<ContextRefreshedEvent>, DisposableBean {

	private ApplicationContext applicationContext;

	public ProviderBean(Provider provider) {
		super(provider);
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		try {
			export();
		} catch (IOException e) {
			LOGGER.error("export service error!", e);
			e.printStackTrace();
		}
	}

	@Override
	public void destroy() throws Exception {
		super.destroy();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		export();
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
		super.setApplicationContext(applicationContext);
	}
}
