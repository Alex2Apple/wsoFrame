package com.wang.frame.rpc;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.springframework.util.Assert;

import com.wang.frame.model.URL;
import com.wang.frame.registry.RegistryCluster;
import com.wang.frame.remote.RelayClient;

/**
 * @author wangju
 *
 * @param <T>
 */
public class DefaultInvoker<T> implements Invoker<T> {

	private static final Logger LOGGER = Logger.getLogger(DefaultInvoker.class);

	private static final RegistryCluster registryCluster = RegistryCluster.create();

	private Class<T> clazz;
	private List<URL> urls;
	private int timeout;

	public DefaultInvoker(Class<T> clazz, int timeout) {
		this.clazz = clazz;
		this.timeout = timeout;
	}

	@Override
	public Class<T> getInterface() {
		return clazz;
	}

	@Override
	public Object invoke(InvokeContext context) {
		URL url = getUrl();
		url.addParameter("timeout", timeout);
		url.addParameter("methodName", context.getMethodName());
		url.addParameter("parameterTypes", context.getParameterTypes());
		url.addParameter("parameters", context.getParameters());

		RelayClient client = new RelayClient();
		client.connect(url);
		client.send(url);

		try {
			return new DefaultResult(client.receive(url));
		} catch (Exception e) {
			return new DefaultExceptionResult(e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public URL getUrl() {
		List<URL> u = new ArrayList<>();
		for (URL url : urls) {
			if (!url.isDeleted()) {
				u.add(url);
			}
		}

		if (!u.isEmpty()) {
			return selectURL(u);
		} else {
			LOGGER.warn(clazz.getName() + ": has not active invoker any! re-subscribe");
			synchronized (urls) {
				urls = (List<URL>) registryCluster.subscribe(URL.build().addParameter("service", clazz.getName()));
			}
			Assert.notEmpty(urls, String.format("The subscribed urls must be not empty for %s", clazz.getName()));
			return selectURL(urls);
		}
	}

	@SuppressWarnings("unchecked")
	public void init() {
		URL url = URL.build().addParameter("service", clazz.getName());
		urls = (List<URL>) registryCluster.subscribe(url);
		Assert.notEmpty(urls, String.format("The subscribed urls must be not empty for %s", clazz.getName()));
	}

	@Override
	public void destroy() {
		Executors.newSingleThreadExecutor().execute(new Runnable() {

			@Override
			public void run() {
				for (URL url : urls) {
					registryCluster.unsubscribe(url);
				}
			}
		});
	}

	@Override
	public Invoker<T> addFilter(Filter<T> filter, Invoker<T> invoker) {
		return new Invoker<T>() {

			@Override
			public void init() {

			}

			@Override
			public Class<T> getInterface() {
				return invoker.getInterface();
			}

			@Override
			public Object invoke(InvokeContext context) {
				return filter.invoke(invoker, context);
			}

			@Override
			public Invoker<T> addFilter(Filter<T> filter, Invoker<T> invoker) {
				return invoker.addFilter(filter, this);
			}

			@Override
			public void destroy() {
				invoker.destroy();
			}

			@Override
			public URL getUrl() {
				return invoker.getUrl();
			}
		};
	}

	private URL selectURL(List<URL> urls) {
		Random random = new Random(urls.size());
		return urls.get(random.nextInt());
	}
}
