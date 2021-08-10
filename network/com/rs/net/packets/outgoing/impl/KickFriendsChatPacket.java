package com.rs.net.packets.outgoing.impl;

import com.rs.game.player.Player;
import com.rs.game.player.content.FriendChatsManager;
import com.rs.io.InputStream;
import com.rs.net.packets.outgoing.OutgoingPacket;
import com.rs.net.packets.outgoing.OutgoingPacketSignature;
import com.rs.utilities.Utility;

@OutgoingPacketSignature(packetId = 64, description = "Represents an event where a Player is being kicked from a Friend's Chat")
public class KickFriendsChatPacket implements OutgoingPacket {

	@Override
	public void execute(Player player, InputStream stream) {
		if (!player.isStarted())
			return;
		player.setLastPublicMessage(Utility.currentTimeMillis() + 1000);
		FriendChatsManager fcManager = new FriendChatsManager(player);
		fcManager.kickPlayerFromFriendsChannel(player, stream.readString());
	}
}