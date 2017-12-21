package com.wang.wnet.nio;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import com.wang.wnet.handler.NioHandler;

/**
 * @author wangju
 *
 */
public class NioWorkerGroup {
	private static final Logger LOGGER = Logger.getLogger(NioWorkerGroup.class);

	private ArrayList<NioWorker> nioWorkers;
	private static final int DEFAULT_WORKER_NUM = Runtime.getRuntime().availableProcessors();
	private static final int MAX_WORKER_NUM = Runtime.getRuntime().availableProcessors() * 2;
	private static final int DEFAULT_BUFFER_POOL_SIZE = 1024 * 1024 * 16; // 每个worker的buffer pool
	private static final int DEFAULT_BUFFET_CHUNK_SIZE = 1024 * 16;

	private String workerGroupName;
	private final int maxWorkers;
	private List<NioHandler> handlers = new ArrayList<>();

	public NioWorkerGroup(String name) {
		this(name, DEFAULT_WORKER_NUM, DEFAULT_BUFFER_POOL_SIZE, DEFAULT_BUFFET_CHUNK_SIZE);
	}

	public NioWorkerGroup(String name, int num) {
		this(name, num, DEFAULT_BUFFER_POOL_SIZE, DEFAULT_BUFFET_CHUNK_SIZE);
	}

	public NioWorkerGroup(String name, int num, int poolSize, int chunkSize) {
		this.workerGroupName = name;
		this.maxWorkers = num < 0 ? DEFAULT_WORKER_NUM : (num > MAX_WORKER_NUM ? MAX_WORKER_NUM : num);
		for (int i = 0; i < this.maxWorkers; i++) {
			nioWorkers.add(new NioWorker(this, poolSize, chunkSize));
		}
	}

	public ArrayList<NioWorker> getNioWorkers() {
		return nioWorkers;
	}

	public String getWorkerGroupName() {
		return workerGroupName;
	}

	public List<NioHandler> getHandlers() {
		return handlers;
	}

	public void addHandlerAtFirst(NioHandler handler) {
		handlers.add(0, handler);
	}

	public void addHandlerAtLast(NioHandler handler) {
		handlers.add(handlers.size(), handler);
	}

	public void startUp() {
		ExecutorService exService = Executors.newFixedThreadPool(this.maxWorkers);
		for (NioWorker nioWorker : nioWorkers) {
			exService.execute(nioWorker);
		}

		LOGGER.info(String.format("worker group %s startup", getWorkerGroupName()));
	}

}
