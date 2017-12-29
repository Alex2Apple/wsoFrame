package com.wang.wnet.handler;

/**
 * @author wangju
 *
 */
public interface NioHandler {

	/**
	 * @return true in, false out
	 */
	boolean inOrOut();

	/**
	 * @param data
	 * @return
	 */
	byte[] handle(byte[] data);
}
