package com.rs.net.packets.outgoing.impl;

import com.rs.game.player.Player;
import com.rs.io.InputStream;
import com.rs.net.packets.outgoing.OutgoingPacket;
import com.rs.net.packets.outgoing.OutgoingPacketSignature;
import com.rs.plugin.NPCDispatcher;

@OutgoingPacketSignature(packetId = 27, packetSize = 3, description = "The Third menu option for a NPC")
public class NPCThirdClickPacket implements OutgoingPacket {

	@Override
	public void execute(Player player, InputStream stream) {
		NPCDispatcher.executeMobInteraction(player, stream, 3);
	}
}