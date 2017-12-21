package com.wang.wnet.handler;

/**
 * @author wangju
 *
 */
public interface NioHandler {

	/**
	 * @param data
	 */
	void handle(byte[] data);
}
