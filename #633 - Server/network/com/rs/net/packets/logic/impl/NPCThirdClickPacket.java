package com.rs.net.packets.logic.impl;

import com.rs.game.player.Player;
import com.rs.io.InputStream;
import com.rs.net.packets.logic.LogicPacket;
import com.rs.net.packets.logic.LogicPacketSignature;
import com.rs.plugin.NPCDispatcher;

@LogicPacketSignature(packetId = 27, packetSize = 3, description = "The Third menu option for a NPC")
public class NPCThirdClickPacket implements LogicPacket {

	@Override
	public void execute(Player player, InputStream stream) {
		NPCDispatcher.executeMobInteraction(player, stream, 3);
	}
}