package com.wang.net.protocol;

import java.nio.ByteBuffer;

/**
 * @author wangju
 *
 */
public abstract class Packet {
	protected byte[] packet;

	private int position;

	public Packet() {
		this.packet = new byte[0];
	}

	public byte[] toBytes() {
		return packet;
	}

	abstract public byte[] getBody();

	abstract public boolean checkPacketComplete();

	abstract public void make(byte[] body);

	abstract public void make(ByteBuffer buffer, int length);

	abstract public int getHeaderLength();

	abstract public int getPacketLength();

	abstract public int getPacketLength(ByteBuffer buffer, int offset);

	abstract public boolean isPacketHeader(ByteBuffer buffer, int offset);

	protected int getPosition() {
		return position;
	}

	protected void setPosition(int position) {
		this.position = position;
	}

	private void checkPosition() throws IndexOutOfBoundsException {
		if (position > packet.length) {
			throw new IndexOutOfBoundsException();
		}
	}

	public void clear() {
		packet = new byte[0];
		position = 0;
	}

	/**
	 * BIG_ENDIAN
	 * 
	 * @param value
	 */
	public void writeInt(int value) throws IndexOutOfBoundsException {
		checkPosition();

		if (packet.length < position + 4) {
			byte[] dest = new byte[packet.length + 4];
			System.arraycopy(packet, 0, dest, 0, packet.length);
			packet = dest;
		}

		packet[position++] = (byte) ((value >> 24) & 0xFF);
		packet[position++] = (byte) ((value >> 16) & 0xFF);
		packet[position++] = (byte) ((value >> 8) & 0xFF);
		packet[position++] = (byte) (value & 0xFF);
	}

	public void writeBytes(byte[] b) throws IndexOutOfBoundsException {
		if (b.length == 0) {
			return;
		}
		checkPosition();

		if (packet.length < position + b.length) {
			byte[] dest = new byte[packet.length + b.length];
			System.arraycopy(packet, 0, dest, 0, packet.length);
			packet = dest;
		}

		System.arraycopy(b, 0, packet, position, b.length);
		setPosition(position + b.length);
	}

	/**
	 * BIG_ENDIAN
	 * 
	 * @return
	 */
	public int readInt() throws IndexOutOfBoundsException {
		if (position + 4 > packet.length) {
			throw new IndexOutOfBoundsException();
		}

		int value = (packet[position++] << 24) & 0xff;
		value |= (packet[position++] << 16) & 0xff;
		value |= (packet[position++] << 8) & 0xff;
		value |= packet[position++] & 0xff;
		return value;
	}

	public byte[] readBytes(int len) throws IndexOutOfBoundsException {
		if (len <= 0) {
			return new byte[0];
		}

		if (position + len > packet.length) {
			throw new IndexOutOfBoundsException();
		}

		byte[] dest = new byte[len];
		System.arraycopy(packet, position, dest, 0, len);
		setPosition(position + len);
		return dest;
	}
}
