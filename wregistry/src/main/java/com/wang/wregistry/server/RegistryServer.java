package com.wang.wregistry.server;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.wang.wregistry.model.AbstractDataSource;
import com.wang.wregistry.model.DataCenter;
import com.wang.wregistry.model.DataCenterFactory;
import com.wang.wregistry.model.DataSourceFactory;
import com.wang.wregistry.model.RegistryCenter;
import com.wang.wregistry.model.RegistryCenterFactory;
import com.wang.wregistry.model.SubscriberCenter;
import com.wang.wregistry.model.SubscriberCenterFactory;

/**
 * @author wangju
 * 
 */
@Component
public class RegistryServer implements ApplicationListener<ContextRefreshedEvent> {
	private static final int DEFAULT_THREAD_NUM = 4;
	private static final int DEFAILT_SCHEDULE_THREAD_NUM = 2;
	private static final int DEFAULT_SCHEDULE_DEDAY = 5 * 60;
	private static final int DEFAULT_SCHEDULE_PERIOD = 5 * 60 + 30;

	private ExecutorService executorService;
	private ScheduledExecutorService scheduledExecutorService;

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		if (event.getApplicationContext().getParent() == null) {
			executorService = Executors.newFixedThreadPool(DEFAULT_THREAD_NUM);
			scheduledExecutorService = Executors.newScheduledThreadPool(DEFAILT_SCHEDULE_THREAD_NUM);

			AbstractDataSource dataSource = DataSourceFactory.getInstance().getDataSource();
			RegistryCenter registryCenter = RegistryCenterFactory.getInstance().getRegistryCenter();
			SubscriberCenter subscriberCenter = SubscriberCenterFactory.getInstance().getSubscriberCenter();
			DataCenter dataCenter = DataCenterFactory.getInstance().getDataCenter();
			dataSource.setRegistryServer(this);
			dataSource.setRegistryCenter(registryCenter);
			dataSource.setSubscriberCenter(subscriberCenter);
			registryCenter.setDataSource(dataSource);
			subscriberCenter.setDataSource(dataSource);
			dataCenter.setDataSource(dataSource);

			try {
				dataSource.load();
			} catch (Exception e) {
				e.printStackTrace();
			}
			dataSource.refreshItemsList();

			scheduledExecutorService.scheduleAtFixedRate(new RegisterCleanTask(registryCenter), DEFAULT_SCHEDULE_DEDAY,
					DEFAULT_SCHEDULE_PERIOD, TimeUnit.SECONDS);
			scheduledExecutorService.scheduleAtFixedRate(new SubscriberCleanTask(subscriberCenter),
					DEFAULT_SCHEDULE_DEDAY + 30, DEFAULT_SCHEDULE_PERIOD, TimeUnit.SECONDS);
		}
	}

	public ExecutorService getExecutorService() {
		return executorService;
	}

	public void setExecutorService(ExecutorService executorService) {
		this.executorService = executorService;
	}

	/*
	 * @PostConstruct private void init() {
	 * 
	 * }
	 */
}
