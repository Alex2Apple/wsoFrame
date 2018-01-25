package com.wang.frame.remote;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.wang.frame.model.URL;
import com.wang.net.ConnectorStartUp;
import com.wang.net.nio.NioConnection;

/**
 * @author wangju
 *
 */
public class DefaultClient implements Client {
	private static final Logger LOGGER = Logger.getLogger(DefaultClient.class);

	private static final Map<String, ConnectionMetaData> response = new ConcurrentHashMap<>();
	private static final AtomicLong requestIds = new AtomicLong(System.currentTimeMillis());
	private static final ScheduledExecutorService scheduledExecutorService = Executors
			.newSingleThreadScheduledExecutor();
	private static final int DEFAULT_MAX_IDLE_TIME = 100 * 1000000; // 默认最大空闲时间100s
	private NioConnection con;
	private String requestId;

	static {
		scheduledExecutorService.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				for (Entry<String, ConnectionMetaData> entry : response.entrySet()) {
					if (entry.getValue().getTimestamp() + DEFAULT_MAX_IDLE_TIME < System.currentTimeMillis()) {
						response.remove(entry.getKey());
					}
				}
			}
		}, 30, 60, TimeUnit.SECONDS);
	}

	public DefaultClient() {

	}

	@Override
	public void connect(URL url) {
		requestId = String.valueOf(requestIds.incrementAndGet());
		response.put(requestId, new ConnectionMetaData());
        ConnectorStartUp connectorStartUp = ConnectorStartUp.getInstance();
		connectorStartUp.createWorkerGroup("").addHandlerAtLast(new DefaultClientHandler(response));
		try {
			con = connectorStartUp.connect(url.getHost(), url.getPort());
		} catch (IOException e) {
			LOGGER.error(String.format("connect %s:%s error", url.getHost(), url.getPort()), e);
		}
		con.setRequestId(requestId);
	}

	public void send(URL url) {
		con.write(JSON.toJSONString(url).getBytes(Charset.forName("UTF-8")));
	}

	@Override
	public void disconnect() {

	}

	@Override
	public Object receive(URL url, long timeout) throws InterruptedException {
		return response.get(requestId).getQueue().poll(timeout, TimeUnit.MICROSECONDS);
	}

	public class ConnectionMetaData {
		private long timestamp;
		private BlockingQueue<Object> queue;

		public ConnectionMetaData() {
			this.timestamp = System.currentTimeMillis();
			this.queue = new ArrayBlockingQueue<>(1);
		}

		public long getTimestamp() {
			return timestamp;
		}

		public void setTimestamp(long timestamp) {
			this.timestamp = timestamp;
		}

		public BlockingQueue<Object> getQueue() {
			return queue;
		}

		public void setQueue(BlockingQueue<Object> queue) {
			this.queue = queue;
		}
	}

}
