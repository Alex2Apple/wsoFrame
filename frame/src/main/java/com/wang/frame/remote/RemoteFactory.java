package com.wang.frame.remote;

import java.io.IOException;

import com.wang.frame.model.Address;
import com.wang.frame.model.URL;
import com.wang.frame.rpc.Invocation;

/**
 * @author wangju
 *
 */
public class RemoteFactory {

	public static Server bind(Address address, Invocation<?> invocation) throws IOException {
		Server server = new DefaultServer(address, invocation);
		server.open();
		return server;
	}

	public static Client connect(URL url) {
		Client client = new DefaultClient();
		client.connect(url);
		return client;
	}
}
