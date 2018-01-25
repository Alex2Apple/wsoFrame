package com.wang.frame.remote;

import com.wang.frame.model.URL;

/**
 * @author wangju
 *
 */
public interface Client {

	void connect(URL url);

	void send(URL url);

	Object receive(URL url, long timeout) throws InterruptedException;

	void disconnect();
}
