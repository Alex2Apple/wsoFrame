package com.wang.frame.remote;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.wang.frame.model.Address;
import com.wang.frame.model.URL;
import com.wang.frame.rpc.Invocation;

/**
 * @author wangju
 *
 */
public class RelayServer implements Relay {

	public static final Logger LOGGER = Logger.getLogger(RelayServer.class);
	private static final RelayServer instance = new RelayServer();
	private final Map<String, Object> serverMap = new ConcurrentHashMap<>();
	private Invocation<?> invocation;

	private RelayServer() {

	}

	public static RelayServer getInstance() {
		return instance;
	}

	public void createServer(URL url, Invocation<?> invocation) throws IOException {
		synchronized (instance) {
			if (invocation == null) {
				this.invocation = invocation;
			}
		}

		openServer(url);
	}

	private void openServer(URL url) throws IOException {
		String key = String.valueOf(url.getPort());
		if (!serverMap.containsKey(key)) {
			Server server = RemoteFactory.bind(new Address(url.getHost(), url.getPort()), invocation);
			serverMap.put(key, server);
		}
	}
}
