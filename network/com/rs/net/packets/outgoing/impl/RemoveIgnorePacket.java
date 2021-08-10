package com.rs.net.packets.outgoing.impl;

import com.rs.game.player.Player;
import com.rs.io.InputStream;
import com.rs.net.packets.outgoing.OutgoingPacket;
import com.rs.net.packets.outgoing.OutgoingPacketSignature;

//TODO: Convert Packet
@OutgoingPacketSignature(packetId = -1, description = "Represents removing another Player from the Player's Ignore List")
public class RemoveIgnorePacket implements OutgoingPacket {

	@Override
	public void execute(Player player, InputStream stream) {
		if (!player.isStarted())
			return;
		player.getFriendsIgnores().removeIgnore(stream.readString());
	}
}