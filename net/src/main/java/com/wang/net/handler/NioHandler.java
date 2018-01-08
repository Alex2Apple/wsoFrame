package com.wang.net.handler;

import com.wang.net.nio.NioConnection;

/**
 * @author wangju
 *
 */
public interface NioHandler {

	/**
	 * 输入或输出处理 保留使用 in true out false
	 * 
	 * @return
	 */
	boolean inOrOut();

	/**
	 * @param data
	 * @return
	 */
	byte[] handle(byte[] data, NioConnection connection);
}
