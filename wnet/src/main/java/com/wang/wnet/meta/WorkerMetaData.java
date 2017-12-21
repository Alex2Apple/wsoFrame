package com.wang.wnet.meta;

import com.wang.wnet.nio.NioConnection;

/**
 * @author wangju
 *
 */
public class WorkerMetaData {

	private NioConnection connection;

	private int readyOps;

	public WorkerMetaData(NioConnection connection, int readyOps) {
		this.connection = connection;
		this.readyOps = readyOps;
	}

	public NioConnection getConnection() {
		return connection;
	}

	public void setConnection(NioConnection connection) {
		this.connection = connection;
	}

	public int getReadyOps() {
		return readyOps;
	}

	public void setReadyOps(int readyOps) {
		this.readyOps = readyOps;
	}

}
