package com.wang.net.factory;

import com.wang.net.protocol.Packet;

/**
 * @author wangju
 *
 */
public interface PacketFactory {

	/**
	 * @return
	 */
	Packet create();
}
