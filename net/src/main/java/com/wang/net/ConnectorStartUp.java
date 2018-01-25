package com.wang.net;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;

import com.wang.net.factory.NioClientConnectionFactory;
import com.wang.net.nio.NioClientConnection;
import com.wang.net.nio.NioConnection;
import com.wang.net.nio.NioConnector;
import com.wang.net.nio.NioReactor;
import com.wang.net.nio.NioWorkerGroup;

/**
 * @author wangju
 *
 */
public class ConnectorStartUp {

	private static final Logger LOGGER = Logger.getLogger(ConnectorStartUp.class);
	private static final ConnectorStartUp instance = new ConnectorStartUp();

	private static String name = "WSOFV1";
	private volatile NioWorkerGroup nioWorkerGroup;
	private volatile NioConnector nioConnector;
	private AtomicBoolean started;

	private ConnectorStartUp() {

	}

	public static ConnectorStartUp getInstance() {
		return instance;
	}

	public synchronized NioWorkerGroup createWorkerGroup(String nameAttach) {
		name = makeName(nameAttach);
		if (nioWorkerGroup == null) {
			nioWorkerGroup = new NioWorkerGroup(makeName("WorkerGroup_Client"));
		}

		return nioWorkerGroup;
	}

	public synchronized void start() throws IOException {
		if (nioWorkerGroup == null) {
			throw new IOException("worker group hasn't be created!");
		}
		if (started.get()) {
			return;
		}
		nioWorkerGroup.start();
		List<NioWorkerGroup> groups = new ArrayList<>();
		groups.add(nioWorkerGroup);
		NioReactor nioReactor = new NioReactor(makeName("NioReactor_Client"), groups);
		new Thread(nioReactor, nioReactor.getName()).start();
		LOGGER.info(nioReactor.getName() + " has started");

		nioConnector = new NioConnector(makeName("NioConnector"), nioReactor);
		new Thread(nioConnector, nioConnector.getName()).start();
		LOGGER.info(nioConnector.getName() + " has started");

		started.set(true);
	}

	private static String makeName(String n) {
		return name + (n.isEmpty() ? "" : "_" + n);
	}

	public NioConnection connect(String host, int port) throws IOException {
		NioClientConnection con = NioClientConnectionFactory.make(host, port);
		NioClientConnectionFactory.postConnect(con, nioConnector);
		return con;
	}
}
