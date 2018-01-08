package com.wang.frame.bean;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

import com.wang.frame.model.MethodConfig;
import com.wang.frame.model.URL;
import com.wang.frame.registry.RegistryCluster;
import com.wang.frame.remote.RelayServer;
import com.wang.frame.rpc.Invocation;

/**
 * @author wangju
 *
 */
public class ProviderConfig<T> {

	public static Logger LOGGER = Logger.getLogger(ProviderConfig.class);
	private static final RegistryCluster registryCluster = RegistryCluster.create();
	private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
	private final Invocation<T> invocation = new Invocation<T>();

	private Provider provider;

	private String id;

	private Class<?> service;

	private String host;

	private int port;

	private String appName;

	private String version;

	private String protocol;

	private volatile boolean destroyed;
	private volatile boolean exported;
	private volatile boolean cancelExported;
	private T ref;

	public ProviderConfig(Provider provider) {
		this.provider = provider;
	}

	public Provider getProvider() {
		return provider;
	}

	public void setProvider(Provider provider) {
		this.provider = provider;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Class<?> getService() {
		return service;
	}

	public void setService(Class<?> service) {
		this.service = service;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public Invocation<T> getInvocation() {
		return invocation;
	}

	public boolean isDelay() {
		return provider.delay();
	}

	protected void destroy() throws Exception {
		if (destroyed) {
			return;
		}
		destroyed = true;
		CancelExport();
	}

	protected boolean isExported() {
		return exported;
	}

	protected boolean isCancelExported() {
		return cancelExported;
	}

	protected void export() throws IOException {
		if (isExported() || isCancelExported()) {
			return;
		}

		if (isDelay()) {
			/**
			 * 延迟执行暴露服务
			 */
			scheduledExecutorService.schedule(new Runnable() {

				@Override
				public void run() {
					try {
						doExport();
					} catch (IOException e) {
						LOGGER.error("export service error!", e);
						e.printStackTrace();
					}
				}
			}, provider.delayTime(), TimeUnit.SECONDS);

			return;
		}
		doExport();
	}

	private synchronized void doExport() throws IOException {
		exported = true;

		Method[] methods = service.getDeclaredMethods();
		List<MethodConfig> methodConfigs = new ArrayList<>();
		for (Method method : methods) {
			MethodConfig mf = new MethodConfig();
			for (Class<?> clazz : method.getParameterTypes()) {
				mf.getParameters().add(clazz);
			}
			mf.setMethodName(method.getName());
			mf.setParameters(new ArrayList<>());
			methodConfigs.add(mf);
		}

		// 注册到注册中心
		URL url = URL.build().setHost(host).setPort(port).setProtocol(protocol).addParameter("service", service)
				.addParameter("version", version).addParameter("appName", appName).addParameter("id", id)
				.addParameter("method", methodConfigs);
		registryCluster.register(url);

		createServer(url);
	}

	/**
	 * @param applicationContext
	 */
	protected void setApplicationContext(ApplicationContext applicationContext) {
		invocation.setAppName(appName);
		invocation.setApplicationContext(applicationContext);
		invocation.setRef(ref);
	}

	private void CancelExport() {
		cancelExported = true;

		URL url = URL.build().setHost(host).setPort(port).setProtocol(protocol).addParameter("service", service)
				.addParameter("version", version);
		registryCluster.unregister(url);

		exported = false;
	}

	private void createServer(URL url) throws IOException {
		RelayServer.getInstance().createServer(url, invocation);
	}
}
