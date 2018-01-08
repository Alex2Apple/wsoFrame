package com.wang.net.nio;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.wang.net.meta.WorkerMetaData;

/**
 * @author wangju
 *
 */
public class NioReactor implements Runnable {
	private static final Logger LOGGER = Logger.getLogger(NioReactor.class);

	private final String name;
	private List<NioWorkerGroup> nioWorkerGroups;

	private final BlockingQueue<AbstractNioConnection> connQueue;
	private final Selector selector;
	private List<AbstractNioConnection> connections;

	public NioReactor(String name, List<NioWorkerGroup> nioWorkerGroups) throws IOException {
		this.name = name;
		this.nioWorkerGroups = nioWorkerGroups;
		this.connQueue = new LinkedBlockingQueue<>();
		this.selector = Selector.open();
		this.connections = new ArrayList<>();
		Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(new Runnable() {

			@Override
			public void run() {
				for (AbstractNioConnection con : connections) {
					if (con.isIdleConnection() && con.close()) {
						LOGGER.info("connection is idle, close it!");
					}
				}
			}
		}, 60, 180, TimeUnit.SECONDS);
	}

	public Selector getSelector() {
		return selector;
	}

	@Override
	public void run() {
		final Selector selector = this.selector;
		for (;;) {
			try {
				selector.select(1000L);
				register(selector);

				Set<SelectionKey> keys = selector.selectedKeys();
				try {
					for (SelectionKey key : keys) {
						Object att = key.attachment();
						if (!key.isValid() || att == null) {
							key.cancel();
							continue;
						}

						int readyOps = key.readyOps();
						if ((readyOps & (SelectionKey.OP_READ | SelectionKey.OP_WRITE)) == 0) {
							key.cancel();
							continue;
						}
						postNioWorker((AbstractNioConnection) att, readyOps);
					}
				} finally {
					keys.clear();
				}

			} catch (Exception e) {
				LOGGER.warn(getName(), e);
			}
		}
	}

	public String getName() {
		return name;
	}

	public BlockingQueue<AbstractNioConnection> getConnQueue() {
		return connQueue;
	}

	private void register(Selector selector) {
		AbstractNioConnection con = null;
		while ((con = connQueue.poll()) != null) {
			try {
				con.register(selector);
				connections.add(con);
			} catch (Exception e) {
				LOGGER.warn("register connection error", e);
			}
		}
	}

	private void postNioWorker(AbstractNioConnection con, int readyOps) {
		try {
			NioWorker nioWorker = pickNioWorker(con);
			if (con.getHandlers().size() == 0) {
				con.setHandlers(nioWorker.getHandlers());
			}
			WorkerMetaData meta = new WorkerMetaData(con, readyOps);
			nioWorker.getReactorConQueue().offer(meta);
		} catch (Exception e) {
			LOGGER.warn("post connection to worker error", e);
		}
	}

	private NioWorker pickNioWorker(AbstractNioConnection con) {
		NioWorker nioWorker = null;

		int groupIndex = -1;
		if (con.getNioWorker() == null
				|| (groupIndex = nioWorkerGroups.indexOf(con.getNioWorker().getNioWorkerGroup())) == -1) {
			Random random = new Random();
			NioWorkerGroup randGroup = nioWorkerGroups.get(random.nextInt(nioWorkerGroups.size()));
			nioWorker = randGroup.getNioWorkers().get(random.nextInt(randGroup.getNioWorkers().size()));
			con.setNioWorker(nioWorker);
		} else {
			int workerIndex = nioWorkerGroups.get(groupIndex).getNioWorkers().indexOf(con.getNioWorker());
			if (workerIndex == -1) {
				con.close();
			}
			nioWorker = con.getNioWorker();
		}

		return nioWorker;
	}
}
