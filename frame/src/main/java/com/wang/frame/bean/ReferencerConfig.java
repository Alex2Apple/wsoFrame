package com.wang.frame.bean;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.wang.frame.proxy.InvocationHandlerFactory;
import com.wang.frame.proxy.ProxyFactory;
import com.wang.frame.rpc.DefaultInvoker;
import com.wang.frame.rpc.Filter;
import com.wang.frame.rpc.Invoker;

/**
 * @author wangju
 *
 */
public class ReferencerConfig<T> {

	public static final Logger LOGGER = Logger.getLogger(ReferencerConfig.class);

	private Map<Class<?>, T> proxyMap = new ConcurrentHashMap<>();

	private Referencer referencer;

	private String id;

	private String serviceName;

	private Class<?> service;

	private String version;

	private boolean force;

	private int timeout;

	private boolean generic;

	private boolean lazy;

	private T ref;

	private List<String> filters;

	private Invoker<T> invoker;

	private volatile boolean destroyed;

	public ReferencerConfig(Referencer referencer) {
		this.referencer = referencer;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public Class<?> getService() {
		return service;
	}

	public void setService(Class<?> service) {
		this.service = service;
	}

	public Referencer getReferencer() {
		return referencer;
	}

	public void setReferencer(Referencer referencer) {
		this.referencer = referencer;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public boolean isForce() {
		return force;
	}

	public void setForce(boolean force) {
		this.force = force;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public boolean isGeneric() {
		return generic;
	}

	public void setGeneric(boolean generic) {
		this.generic = generic;
	}

	public boolean isLazy() {
		return lazy;
	}

	public void setLazy(boolean lazy) {
		this.lazy = lazy;
	}

	public T getRef() {
		return ref;
	}

	public void setRef(T ref) {
		this.ref = ref;
	}

	public List<String> getFilters() {
		return filters;
	}

	public void setFilters(List<String> filters) {
		this.filters = filters;
	}

	public Class<?> getType() {
		return service;
	}

	public T get() {
		if (proxyMap.containsKey(service)) {
			ref = proxyMap.get(service);
		} else {
			ref = ProxyFactory.getProxy(InvocationHandlerFactory.make(service, invoker));
			proxyMap.put(service, ref);
		}

		return ref;
	}

	protected void destroy() throws Exception {
		if (destroyed) {
			return;
		}
		destroyed = true;

		invoker.destroy();
		proxyMap.clear();
		LOGGER.info("referencer bean has be destroyed for " + service.getName());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void init() {
		invoker = new DefaultInvoker(service, timeout);
		invoker.init();

		for (String fName : filters) {
			Filter<T> filter = lookupByName(fName);
			if (filter != null) {
				invoker = addFilter(filter, invoker);
			}
		}

		if (!isLazy()) {
			get();
		}
		LOGGER.info("referencer bean's init has finished for " + service.getName());
	}

	private Invoker<T> addFilter(Filter<T> filter, Invoker<T> invoker) {
		return invoker.addFilter(filter, invoker);
	}

	private Filter<T> lookupByName(String name) {
		return null; // TODO
	}
}
