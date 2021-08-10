package com.rs.net.packets.outgoing.impl;

import com.rs.game.player.Player;
import com.rs.game.player.content.FriendChatsManager;
import com.rs.io.InputStream;
import com.rs.net.packets.outgoing.OutgoingPacket;
import com.rs.net.packets.outgoing.OutgoingPacketSignature;

@OutgoingPacketSignature(packetId = 1, description = "Represents a Player joining a Friend's Chat")
public class JoinFriendsChatPacket implements OutgoingPacket {

	@Override
	public void execute(Player player, InputStream stream) {
		if (!player.isStarted())
			return;
		FriendChatsManager.joinChat(stream.readString(), player);
	}
}