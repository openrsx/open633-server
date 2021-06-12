package com.rs.net.packets.impl;

import com.rs.GameConstants;
import com.rs.game.World;
import com.rs.game.WorldObject;
import com.rs.game.WorldTile;
import com.rs.game.player.Player;
import com.rs.game.route.RouteEvent;
import com.rs.io.InputStream;
import com.rs.net.packets.OutgoingPacket;
import com.rs.net.packets.PacketSignature;
import com.rs.plugin.ObjectDispatcher;

@PacketSignature(packetId = 75, packetSize = 7, description = "First click packet")
public class ObjectClickPacket implements OutgoingPacket {

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
		
		//Theving stalls has an issue reading X/Y coordinates, rest objects are fine so far.
		if (!player.getMapRegionsIds().contains(regionId)) {
			player.getPackets().sendGameMessage("map doesnt contains region");
			return;
		}
		WorldObject mapObject = World.getObjectWithId(tile, id);
		if (mapObject == null) {
			return;
		}
		if (player.isDead() || player.isLocked()) {
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