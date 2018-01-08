package com.wang.registry.server;

import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.wang.center.factory.AdminCenterFactory;
import com.wang.center.factory.DataSourceFactory;
import com.wang.center.factory.ProviderCenterFactory;
import com.wang.center.factory.RegistryCenterFactory;
import com.wang.center.factory.SubscriberCenterFactory;
import com.wang.registry.center.AbstractDataSource;
import com.wang.registry.center.AdminCenter;
import com.wang.registry.center.ProviderCenter;
import com.wang.registry.center.RegistryCenter;
import com.wang.registry.center.SubscriberCenter;
import com.wang.registry.cluster.Cluster;
import com.wang.registry.cluster.ClusterHeartBeatTask;
import com.wang.registry.cluster.ClusterNode;
import com.wang.registry.config.RegistryConstants;
import com.wang.registry.util.RegistryCenterUtil;

/**
 * @author wangju
 * 
 */
@Component
public class RegistryServer implements ApplicationListener<ContextRefreshedEvent> {

	private static final Logger LOGGER = Logger.getLogger(RegistryServer.class);

	private ExecutorService executorService;
	private ScheduledExecutorService scheduledExecutorService;

	private AbstractDataSource dataSource;
	private RegistryCenter registryCenter;
	private ProviderCenter providerCenter;
	private SubscriberCenter subscriberCenter;
	private AdminCenter adminCenter;

	private Cluster cluster;

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		if (event.getApplicationContext().getParent() == null) {
			initCluster();
			initServer();
		}
	}

	private void initCluster() {
		try {
			Properties properties = RegistryCenterUtil.readProperties(RegistryConstants.SERVER_CONFIG_FILE);

			String[] mn = properties.getProperty(RegistryConstants.CLUSTER_MSATER_NODE_KEY).split(":");
			if (mn.length != 2) {
				throw new IllegalArgumentException(
						String.format("%s's value must be #host#:#port#", RegistryConstants.CLUSTER_MSATER_NODE_KEY));
			}
			ClusterNode master = new ClusterNode();
			master.setHost(mn[0]);
			master.setPort(Integer.valueOf(mn[1]));
			master.setMaster(true);
			master.setValid(true);

			String[] sn = properties.getProperty(RegistryConstants.CLUSTER_SLAVES_NODE_KEY).split(":");
			if (sn.length != 2) {
				throw new IllegalArgumentException(
						String.format("%s's value must be #host#:#port#", RegistryConstants.CLUSTER_SLAVES_NODE_KEY));
			}
			ClusterNode slave = new ClusterNode();
			slave.setHost(sn[0]);
			slave.setPort(Integer.valueOf(sn[1]));
			slave.setMaster(false);
			slave.setValid(true);

			cluster = new Cluster(master, slave);
			if (RegistryCenterUtil.isLocalHost(mn[0])) {
				cluster.setMyself(master);
			} else if (RegistryCenterUtil.isLocalHost(sn[0])) {
				cluster.setMyself(slave);
			} else {
				throw new IllegalArgumentException("the specified host does not matched the local host");
			}
		} catch (Exception e) {
			LOGGER.error("init cluster error!", e);
		}
	}

	private void initServer() {
		executorService = Executors.newFixedThreadPool(RegistryConstants.DEFAULT_THREAD_NUM);
		scheduledExecutorService = Executors.newScheduledThreadPool(RegistryConstants.DEFAILT_SCHEDULE_THREAD_NUM);

		AbstractDataSource dataSource = DataSourceFactory.getInstance().getDataSource();
		RegistryCenter registryCenter = RegistryCenterFactory.getInstance().getRegistryCenter();
		ProviderCenter providerCenter = ProviderCenterFactory.getInstance().getProviderCenter();
		SubscriberCenter subscriberCenter = SubscriberCenterFactory.getInstance().getSubscriberCenter();
		AdminCenter adminCenter = AdminCenterFactory.getInstance().getAdminCenter();
		dataSource.setRegistryServer(this);
		registryCenter.setRegistryServer(this);
		providerCenter.setRegistryServer(this);
		subscriberCenter.setRegistryServer(this);
		adminCenter.setRegistryServer(this);
		cluster.setRegistryServer(this);
		try {
			dataSource.load();
		} catch (Exception e) {
			LOGGER.error("init server error!", e);
		}

		scheduledExecutorService.scheduleAtFixedRate(new ProviderCleanTask(providerCenter),
				RegistryConstants.DEFAULT_SCHEDULE_DEDAY, RegistryConstants.DEFAULT_SCHEDULE_PERIOD, TimeUnit.SECONDS);
		scheduledExecutorService.scheduleAtFixedRate(new SubscriberCleanTask(subscriberCenter),
				RegistryConstants.DEFAULT_SCHEDULE_DEDAY + 30, RegistryConstants.DEFAULT_SCHEDULE_PERIOD,
				TimeUnit.SECONDS);
		scheduledExecutorService.scheduleAtFixedRate(new AdminDataRefreshTask(dataSource), 0,
				RegistryConstants.DEFAULT_REFRESH_PERIOD, TimeUnit.SECONDS);

		if (!cluster.getMyself().isMaster()) {
			scheduledExecutorService.scheduleAtFixedRate(new ClusterHeartBeatTask(this), 30,
					RegistryConstants.DEFAULT_CLUSTER_HEARTBEAT_PERIOD, TimeUnit.SECONDS);
		}
	}

	public AbstractDataSource getDataSource() {
		return dataSource;
	}

	public RegistryCenter getRegistryCenter() {
		return registryCenter;
	}

	public ProviderCenter getProviderCenter() {
		return providerCenter;
	}

	public SubscriberCenter getSubscriberCenter() {
		return subscriberCenter;
	}

	public AdminCenter getAdminCenter() {
		return adminCenter;
	}

	public Cluster getCluster() {
		return cluster;
	}

	public ExecutorService getExecutorService() {
		return executorService;
	}

	public ScheduledExecutorService getScheduledExecutorService() {
		return scheduledExecutorService;
	}
}
