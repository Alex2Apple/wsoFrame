package com.wang.wnet.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Selector;

import com.wang.wnet.error.ErrorCode;

/**
 * @author wangju
 *
 */
public interface NioConnection {

	/**
	 * @param selector
	 * @throws IOException
	 */
	void register(Selector selector) throws IOException;

	/**
	 * @throws IOException
	 */
	void read() throws IOException;

	/**
	 * 写数据到队列排队, 待写入channel
	 * 
	 * @param byteBuffer
	 */
	void write(ByteBuffer byteBuffer);

	/**
	 * 从队列写入数据到channel
	 * 
	 * @throws IOException
	 */
	void writeFromQueue() throws IOException;

	/**
	 * @return
	 * @throws IOException
	 */
	boolean close() throws IOException;

	/**
	 * @param data
	 * @param in
	 */
	void handle(byte[] data, boolean in);

	/**
	 * @param errorCode
	 * @param t
	 */
	void error(ErrorCode errorCode, Throwable t);
}
