package com.rs.net.packets.outgoing.impl;

import com.rs.game.World;
import com.rs.game.player.Player;
import com.rs.game.player.actions.PlayerFollow;
import com.rs.io.InputStream;
import com.rs.net.packets.outgoing.OutgoingPacket;
import com.rs.net.packets.outgoing.OutgoingPacketSignature;

@OutgoingPacketSignature(packetId = 76, packetSize = 3, description = "The Second menu option for a Player")
public class PlayerOptionTwoPacket implements OutgoingPacket {

	@Override
	public void execute(Player player, InputStream stream) {
		if (!player.isStarted() || !player.isClientLoadedMapRegion() || player.isDead())
			return;
		int playerIndex = stream.readUnsignedShort();
		boolean forceRun = stream.readUnsignedByte() == 1;
		Player p2 = World.getPlayers().get(playerIndex);
		if (p2 == null || p2 == player || p2.isDead() || p2.isFinished()
				|| !player.getMapRegionsIds().contains(p2.getRegionId()))
			return;
		if (player.isLocked())
			return;
		if (!player.getControllerManager().canPlayerOption2(p2))
			return;
		if (forceRun)
			player.setRun(forceRun);
		player.stopAll();
		player.getActionManager().setAction(new PlayerFollow(p2));
	}
}