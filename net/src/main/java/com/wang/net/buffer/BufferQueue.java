package com.wang.net.buffer;

import java.nio.ByteBuffer;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author wangju
 *
 */
public class BufferQueue {

	private Queue<ByteBuffer> queue;
	private ByteBuffer left;

	public BufferQueue() {
		this.queue = new LinkedBlockingQueue<>();
	}

	public Queue<ByteBuffer> getQueue() {
		return queue;
	}

	public void setQueue(Queue<ByteBuffer> queue) {
		this.queue = queue;
	}

	public ByteBuffer getLeft() {
		return left;
	}

	public void setLeft(ByteBuffer left) {
		this.left = left;
	}
}
