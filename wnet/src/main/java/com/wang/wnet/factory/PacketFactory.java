package com.wang.wnet.factory;

import com.wang.wnet.protocol.Packet;

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
