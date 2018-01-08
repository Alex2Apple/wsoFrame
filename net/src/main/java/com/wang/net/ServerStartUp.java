package com.wang.net;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;

import com.wang.net.nio.NioAcceptor;
import com.wang.net.nio.NioReactor;
import com.wang.net.nio.NioWorkerGroup;

/**
 * @author wangju
 *
 */
public class ServerStartUp {

	private static final Logger LOGGER = Logger.getLogger(ServerStartUp.class);

	private static String name = "WSOFV1";
	private volatile NioWorkerGroup nioWorkerGroup;
	private AtomicBoolean started;

	public synchronized NioWorkerGroup createWorkerGroup(String nameAttach) {
		name = makeName(nameAttach);
		if (nioWorkerGroup == null) {
			nioWorkerGroup = new NioWorkerGroup(makeName("WorkerGroup_Client"));
		}

		return nioWorkerGroup;
	}

	public synchronized void start(int port) throws IOException {
		if (nioWorkerGroup == null) {
			throw new IOException("worker group hasn't be created!");
		}

		if (started.get()) {
			return;
		}

		nioWorkerGroup.start();
		List<NioWorkerGroup> groups = new ArrayList<>();
		groups.add(nioWorkerGroup);
		NioReactor nioReactor = new NioReactor(makeName("NioReactor_Server"), groups);
		new Thread(nioReactor, nioReactor.getName()).start();
		LOGGER.info(nioReactor.getName() + " has started");

		NioAcceptor nioAcceptor = new NioAcceptor(makeName("NioAcceptor"), port, nioReactor);
		new Thread(nioAcceptor, nioAcceptor.getName()).start();
		LOGGER.info(nioAcceptor.getName() + " has started");

		started.set(true);
	}

	private static String makeName(String n) {
		return name + (n.isEmpty() ? "" : "_" + n);
	}
}
