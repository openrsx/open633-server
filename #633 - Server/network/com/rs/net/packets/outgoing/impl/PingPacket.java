package com.rs.net.packets.outgoing.impl;

import com.rs.game.player.Player;
import com.rs.io.InputStream;
import com.rs.net.packets.outgoing.OutgoingPacket;
import com.rs.net.packets.outgoing.OutgoingPacketSignature;

@OutgoingPacketSignature(packetId = 39, description = "Represents a Player's Ping connection to the Game server")
public class PingPacket implements OutgoingPacket {

	@Override
	public void execute(Player player, InputStream stream) {
		
	}
}