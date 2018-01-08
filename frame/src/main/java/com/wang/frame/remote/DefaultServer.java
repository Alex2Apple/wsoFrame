package com.wang.frame.remote;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.wang.frame.model.Address;
import com.wang.frame.rpc.Invocation;
import com.wang.net.ServerStartUp;

/**
 * @author wangju
 *
 */
public class DefaultServer implements Server {

	private static final Logger LOGGER = Logger.getLogger(DefaultServer.class);

	private int port;
	private Invocation<?> invocation;

	public DefaultServer(Address address, Invocation<?> invocation) {
		this.port = address.getPort();
		this.invocation = invocation;
	}

	@Override
	public void open() throws IOException {
		ServerStartUp server = new ServerStartUp();
		server.createWorkerGroup(invocation.getAppName()).addHandlerAtLast(new DefaultServerHandler(invocation));
		server.start(port);
	}

	@Override
	public void close() {

	}

}
