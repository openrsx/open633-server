package com.rs.net.packets.outgoing.impl;

import java.lang.annotation.Annotation;
import java.util.Arrays;

import com.rs.game.player.Player;
import com.rs.io.InputStream;
import com.rs.net.packets.outgoing.OutgoingPacket;
import com.rs.net.packets.outgoing.OutgoingPacketSignature;
import com.rs.plugin.RSInterfaceDispatcher;

@OutgoingPacketSignature(packetId = {11, 29, 9, 32, 72, 19, 12, 31, 18, 74}, description = "Represents a Action Button event type")
public class ActionButtonPacket implements OutgoingPacket {

	@Override
	public void execute(Player player, InputStream stream) {
		Annotation annotation = ActionButtonPacket.class.getAnnotation(OutgoingPacketSignature.class);
		OutgoingPacketSignature signature = (OutgoingPacketSignature) annotation;
		Arrays.stream(signature.packetId()).forEach(packet -> RSInterfaceDispatcher.handleButtons(player, stream, packet));
	}
}