package com.wang.wnet.factory;

import com.wang.wnet.protocol.DefaultPacket;
import com.wang.wnet.protocol.Packet;

/**
 * @author wangju
 *
 */
public class DefaultPacketFactory implements PacketFactory {

	private static final DefaultPacketFactory instance = new DefaultPacketFactory();

	private DefaultPacketFactory() {

	}

	public static DefaultPacketFactory getInstance() {
		return instance;
	}

	@Override
	public Packet create() {
		return new DefaultPacket();
	}

}
