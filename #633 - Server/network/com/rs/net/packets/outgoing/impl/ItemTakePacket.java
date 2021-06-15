package com.rs.net.packets.outgoing.impl;

import com.rs.game.World;
import com.rs.game.WorldTile;
import com.rs.game.item.FloorItem;
import com.rs.game.player.Player;
import com.rs.game.route.RouteEvent;
import com.rs.io.InputStream;
import com.rs.net.packets.outgoing.OutgoingPacket;
import com.rs.net.packets.outgoing.OutgoingPacketSignature;
import com.rs.utilities.Logger;

@OutgoingPacketSignature(packetId = 30, packetSize = 7, description = "Takes an Item from the Ground tile")
public class ItemTakePacket implements OutgoingPacket {

	@Override
	public void execute(Player player, InputStream stream) {
		if (!player.isStarted() || !player.isClientLoadedMapRegion() || player.isDead())
			return;
		if (player.isLocked())
			return;
		int x = stream.readUnsignedShort128();
		final int id = stream.readUnsignedShort();
		int y = stream.readUnsignedShortLE128();
		boolean forceRun = stream.readByte() == 1;
		final WorldTile tile = new WorldTile(x, y, player.getPlane());
		final int regionId = tile.getRegionId();
		if (!player.getMapRegionsIds().contains(regionId))
			return;
		final FloorItem item = World.getRegion(regionId).getGroundItem(id, tile, player);
		if (item == null || !player.getControllerManager().canTakeItem(item))
			return;
		if (forceRun)
			player.setRun(forceRun);
		player.stopAll();
		player.setRouteEvent(new RouteEvent(item, new Runnable() {
			@Override
			public void run() {
				final FloorItem item = World.getRegion(regionId).getGroundItem(id, tile, player);
				if (item == null)
					return;
				// player.setNextFaceWorldTile(tile);
				if (FloorItem.removeGroundItem(player, item))
					Logger.globalLog(player.getUsername(), player.getSession().getIP(),
							new String(" has picked up item [ id: " + item.getId() + ", amount: " + item.getAmount()
									+ " ] originally owned to "
									+ (item.getOwner() == null ? "no owner" : item.getOwner()) + "."));
			}
		}));
	}
}