package com.rs.net.packets.outgoing;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface OutgoingPacketSignature {

	/**
	 * The Packet ID
	 * @return id
	 */
	int packetId();
	
	/**
	 * The payload size of the Packet being sent
	 * @return size
	 */
	int packetSize();
	
	/**
	 * A Simple description of what the packet is designed for
	 * @return description
	 */
	String description();
}