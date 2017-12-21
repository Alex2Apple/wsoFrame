package com.wang.wnet.buffer;

import java.nio.ByteBuffer;

/**
 * @author wangju
 *
 */
public class BufferPool {

	private final int chunkSize;
	private final ByteBuffer[] items;

	private int count;
	private int putPos;
	private int takePos;

	public BufferPool(int poolSize, int chunkSize) {
		this.chunkSize = chunkSize;

		int capacity = (int) Math.ceil(poolSize / chunkSize);
		items = new ByteBuffer[capacity];
		for (int i = 0; i < capacity; i++) {
			put(create(chunkSize));
		}
	}

	public int getCapacity() {
		return items.length;
	}

	public int getSize() {
		return count;
	}

	public ByteBuffer allocate() {
		ByteBuffer buffer = null;

		synchronized (this) {
			buffer = (count != 0) ? take() : null;
		}

		if (buffer == null) {
			return create(chunkSize);
		} else {
			return buffer;
		}
	}

	public void recycle(ByteBuffer buffer) {
		if (buffer == null || buffer.capacity() > chunkSize) {
			return;
		}

		synchronized (this) {
			if(count < items.length) {
				buffer.clear();
				put(buffer);
			}
		}
	}

	private void put(ByteBuffer buffer) {
		items[putPos] = buffer;
		putPos = circleInc(putPos);
		++count;
	}

	private ByteBuffer take() {
		final ByteBuffer[] items = this.items;
		ByteBuffer item = items[takePos];
		takePos = circleInc(takePos);
		--count;

		return item;
	}

	private ByteBuffer create(int chunkSize) {
		return ByteBuffer.allocate(chunkSize);
	}

	private int circleInc(int pos) {
		return (++pos == items.length) ? 0 : pos;
	}
}
