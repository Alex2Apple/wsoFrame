package com.wang.frame.remote;

import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.wang.frame.model.URL;

/**
 * @author wangju
 *
 */
public class RelayClient implements Relay {

	private static final Logger LOGGER = Logger.getLogger(Relay.class);

	private Client client;

	public void connect(URL url) {
		client = RemoteFactory.connect(url);
	}

	public void send(URL url) {
		client.send(url);
	}

	public Object receive(URL url) throws InterruptedException {
		long timeout = TimeUnit.MICROSECONDS.convert(Integer.valueOf(url.getParameters().get("timeout").toString()),
				TimeUnit.SECONDS);
		return client.receive(url, timeout);
	}
}
