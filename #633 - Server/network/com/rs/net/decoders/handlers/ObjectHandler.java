package com.rs.net.decoders.handlers;

import com.rs.Settings;
import com.rs.cache.loaders.ObjectDefinitions;
import com.rs.game.World;
import com.rs.game.WorldObject;
import com.rs.game.WorldTile;
import com.rs.game.item.Item;
import com.rs.game.player.Player;
import com.rs.game.route.RouteEvent;
import com.rs.io.InputStream;
import com.rs.utils.Utils;

public final class ObjectHandler {

	public static void handleOption(final Player player, InputStream stream,
			int option) {
		if (!player.hasStarted() || !player.clientHasLoadedMapRegion()
				|| player.isDead())
			return;
		if (player.isLocked()
				|| player.getEmotesManager().getNextEmoteEnd() >= Utils
						.currentTimeMillis())
			return;

		/**
		 * This order matters, like "H", then "e, "l," "l", "o".
		 * Otherwise it won't make sense. So keep in mind never to 
		 * change this order.
		 */
		int x = stream.readUnsignedShortLE();
        int y = stream.readUnsignedShortLE();
        boolean forceRun = stream.readUnsignedByte128() == 1;
        final int id = stream.readUnsignedShortLE();
        
        if (Settings.DEBUG)
        	System.out.println("id " + id +" x " + x + " y " + y + " run? " + forceRun);
		final WorldTile tile = new WorldTile(x, y, player.getPlane());
		
		WorldObject mapObject = World.getObjectWithId(tile, id);
		if (mapObject == null || mapObject.getId() != id)
			return;
		
		final WorldObject object = mapObject;
		
		player.stopAll();
		if (forceRun)
			player.setRun(forceRun);
		switch (option) {
		case 1:
			handleOption1(player, object);
			break;
		case 2:
			handleOption2(player, object);
			break;
		case 3:
			handleOption3(player, object);
			break;
		case 4:
			handleOption4(player, object);
			break;
		case 5:
			handleOption5(player, object);
			break;
		case -1: //No option is registered, maybe a packet is sent or something.
			handleOptionExamine(player, stream, object);
			break;
		}
	}

	private static void handleOption1(final Player player, final WorldObject object) {
		final ObjectDefinitions objectDef = object.getDefinitions();
		final int id = object.getId();
		final int x = object.getX();
		final int y = object.getY();
		if (id == 67044) {
			player.setRouteEvent(new RouteEvent(object, new Runnable() {
				@Override
				public void run() {
					// unreachable objects exception
					player.faceObject(object);
					player.useStairs(-1, new WorldTile(2924, 3408, 0), 0, 1);
				}
			}, true));
			return;
		}
		if (object.getDefinitions().name.equalsIgnoreCase("Staircase"))
			System.out.println("HEY");
	}

	private static void handleOption2(final Player player,
			final WorldObject object) {
		final ObjectDefinitions objectDef = object.getDefinitions();
		final int id = object.getId();
	}

	private static void handleOption3(final Player player,
			final WorldObject object) {
		final ObjectDefinitions objectDef = object.getDefinitions();
		final int id = object.getId();
	}

	private static void handleOption4(final Player player,
			final WorldObject object) {
		final ObjectDefinitions objectDef = object.getDefinitions();
		final int id = object.getId();
	
	}

	private static void handleOption5(final Player player,
			final WorldObject object) {
		final ObjectDefinitions objectDef = object.getDefinitions();
		final int id = object.getId();

	}

	private static void handleOptionExamine(final Player player, InputStream stream, WorldObject object) {
		System.out.println("?");
		
		int x = stream.readUnsignedShortLE();
        int y = stream.readUnsignedShortLE();
        boolean forceRun = stream.readUnsignedByte128() == 1;
        final int id = stream.readUnsignedShortLE();
        
		player.getPackets().sendGameMessage("It's a " + ObjectDefinitions.getObjectDefinitions(id).name + ".");
		player.getPackets().sendResetMinimapFlag();
	}
	
	public static void handleItemOnObject(final Player player,
			final WorldObject object, final int interfaceId, final Item item) {
		final int itemId = item.getId();
		final ObjectDefinitions objectDef = object.getDefinitions();
	}
}
