package com.rs.net.packets.logic.impl;

import com.rs.GameConstants;
import com.rs.game.World;
import com.rs.game.WorldObject;
import com.rs.game.WorldTile;
import com.rs.game.player.Player;
import com.rs.game.route.RouteEvent;
import com.rs.io.InputStream;
import com.rs.net.packets.logic.LogicPacket;
import com.rs.net.packets.logic.LogicPacketSignature;
import com.rs.plugin.ObjectDispatcher;

@LogicPacketSignature(packetId = 75, packetSize = 7, description = "First click packet")
public class ObjectClickPacket implements LogicPacket {

	@Override
	public void execute(Player player, InputStream input) {
		int x = input.readUnsignedShortLE();
		int y = input.readUnsignedShortLE();
		boolean forceRun = input.readUnsignedByte128() == 1;
		int id = input.readUnsignedShortLE();

		if (GameConstants.DEBUG)
			System.out.println("id " + id + " x " + x + " y " + y + " run? " + forceRun);
		final WorldTile tile = new WorldTile(x, y, player.getPlane());

		final int regionId = tile.getRegionId();

		if (!player.getMapRegionsIds().contains(regionId)) {
			player.getPackets().sendGameMessage("map doesnt contains region");
			return;
		}
		WorldObject mapObject = World.getObjectWithId(tile, id);
		if (mapObject == null) {
			return;
		}
		if (player.isDead() || player.getMovement().isLocked()) {
			return;
		}
		if (mapObject.getId() != id) {
			return;
		}
		final WorldObject worldObject = mapObject;

		player.stopAll();
		if (forceRun)
			player.setRun(forceRun);

		player.setRouteEvent(new RouteEvent(worldObject, new Runnable() {
			@Override
			public void run() {
				player.stopAll();
				player.faceObject(worldObject);
				ObjectDispatcher.execute(player, worldObject, 1);
			}
		}, false));
	}
}