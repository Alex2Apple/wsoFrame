package com.wang.net.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Selector;

import com.wang.net.error.ErrorCode;

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
	 * @return
	 * @throws IOException
	 */
	void read() throws IOException;

	/**
	 * 写字节流
	 * 
	 * @param data
	 */
	void write(byte[] data);

	/**
	 * 写数据到队列排队, 待写入channel
	 * 
	 * @param byteBuffer
	 */
	void write(ByteBuffer byteBuffer);

	/**
	 * 可写事件就绪时写入数据
	 * 
	 * @throws IOException
	 */
	void writeByEvent() throws IOException;

	/**
	 * @return
	 * @throws IOException
	 */
	boolean close() throws IOException;

	/**
	 * @param data
	 * @param in
	 * @return
	 */
	void handle(byte[] data, boolean in);

	/**
	 * @param errorCode
	 * @param t
	 */
	void error(ErrorCode errorCode, Throwable t);

	/**
	 * @param requestId
	 */
	void setRequestId(String requestId);

	/**
	 * @return
	 */
	String getRequestId();
}
