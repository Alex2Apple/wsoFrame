package com.wang.wnet.protocol;

import java.nio.ByteBuffer;

/**
 * @author wangju
 *
 */
public class DefaultPacket extends Packet {

	private static final byte[] BEGIN_LIMITER = "\n\n\n\n".getBytes();

	@Override
	public void make(byte[] body) {
		writeBytes(BEGIN_LIMITER);
		writeInt(getHeaderLength() + body.length);
		writeBytes(body);
	}

	@Override
	public void make(ByteBuffer buffer, int length) {
		byte[] dest = new byte[packet.length + length];
		if (packet.length != 0) {
			System.arraycopy(packet, 0, dest, 0, packet.length);
		}
		buffer.get(dest, packet.length, length);

		packet = dest;
	}

	@Override
	public byte[] getBody() {
		int packetLength = getPacketLength();
		if (packetLength <= packet.length && packetLength > getHeaderLength()) {
			byte[] body = new byte[packetLength - getHeaderLength()];
			System.arraycopy(packet, getHeaderLength(), body, 0, body.length);
			return body;
		}

		return new byte[0];
	}

	@Override
	public boolean checkPacketComplete() {
		int pktLength = getPacketLength();
		if (pktLength <= packet.length && pktLength != -1) {
			return true;
		}

		return false;
	}

	@Override
	public int getPacketLength() {
		if (packet.length == 0) {
			return 0;
		}

		if (packet.length < getHeaderLength()) {
			return -1;
		}

		int index = BEGIN_LIMITER.length;
		int value = (packet[index++] << 24) & 0xff;
		value |= (packet[index++] << 16) & 0xff;
		value |= (packet[index++] << 8) & 0xff;
		value |= packet[index++] & 0xff;
		return value;
	}

	@Override
	public int getHeaderLength() {
		return 4 + BEGIN_LIMITER.length;
	}

	@Override
	public int getPacketLength(ByteBuffer buffer, int offset) {
		if (buffer.position() < getHeaderLength() + offset) {
			return -1;
		}

		int index = BEGIN_LIMITER.length + offset;
		int value = (buffer.get(index++) << 24) & 0xff;
		value |= (buffer.get(index++) << 16) & 0xff;
		value |= (buffer.get(index++) << 8) & 0xff;
		value |= buffer.get(index++) & 0xff;
		return value;
	}

	@Override
	public boolean isPacketHeader(ByteBuffer buffer, int offset) {
		if (buffer.position() < getHeaderLength() + offset) {
			return false;
		}

		byte[] begin = new byte[BEGIN_LIMITER.length];
		buffer.get(begin, 0, BEGIN_LIMITER.length);
		if (begin.toString().equals(BEGIN_LIMITER.toString())) {
			return true;
		}

		return false;
	}
}
