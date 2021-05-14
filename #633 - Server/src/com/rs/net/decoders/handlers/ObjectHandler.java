package com.rs.net.decoders.handlers;

import com.rs.Settings;
import com.rs.cache.loaders.ObjectDefinitions;
import com.rs.game.Animation;
import com.rs.game.World;
import com.rs.game.WorldObject;
import com.rs.game.WorldTile;
import com.rs.game.item.Item;
import com.rs.game.player.Player;
import com.rs.game.player.RouteEvent;
import com.rs.game.player.Skills;
import com.rs.io.InputStream;
import com.rs.utils.Logger;
import com.rs.utils.Utils;

public final class ObjectHandler {

	private ObjectHandler() {

	}

	public static void handleOption(final Player player, InputStream stream,
			int option) {
		if (!player.hasStarted() || !player.clientHasLoadedMapRegion()
				|| player.isDead())
			return;
		if (player.isLocked()
				|| player.getEmotesManager().getNextEmoteEnd() >= Utils
						.currentTimeMillis())
			return;
		boolean forceRun = stream.readUnsignedByte128() == 1;
		final int id = stream.readIntLE();
		int x = stream.readUnsignedShortLE();
		int y = stream.readUnsignedShortLE128();
		final WorldTile tile = new WorldTile(x, y, player.getPlane());
		final int regionId = tile.getRegionId();
		player.getPackets().sendMessage(0, "First option click packet! (Tile: " + tile.getX() + " " + tile.getY() + " " + regionId + ")", player);
		if (player.getMapRegionsIds().contains(regionId)) {
			System.out.println("no");
			return;
		}
			
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
		case -1:
			handleOptionExamine(player, object);
			break;
		}
	}

	private static void handleOption1(final Player player,
			final WorldObject object) {
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
	}

	public static void renewSummoningPoints(Player player) {
		int summonLevel = player.getSkills().getLevelForXp(Skills.SUMMONING);
		if (player.getSkills().getLevel(Skills.SUMMONING) < summonLevel) {
			player.lock(3);
			player.setNextAnimation(new Animation(8502));
			player.getSkills().set(Skills.SUMMONING, summonLevel);
			player.getPackets().sendGameMessage(
					"You have recharged your Summoning points.", true);
		} else
			player.getPackets().sendGameMessage(
					"You already have full Summoning points.");
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

	private static void handleOptionExamine(final Player player,
			final WorldObject object) {
		player.getPackets().sendObjectMessage(0, 15263739, object,
				"It's a " + object.getDefinitions().name + ".");
		player.getPackets().sendResetMinimapFlag();
		if (Settings.DEBUG)
			Logger.log(
					"ObjectHandler",
					"examined object id : " + object.getId() + ", "
							+ object.getX() + ", " + object.getY() + ", "
							+ object.getPlane() + ", " + object.getType()
							+ ", " + object.getRotation() + ", "
							+ object.getDefinitions().name);
	}

	private static void slashWeb(Player player, WorldObject object) {
		if (Utils.getRandom(1) == 0) {
			World.spawnObjectTemporary(new WorldObject(object.getId() + 1,
					object.getType(), object.getRotation(), object.getX(),
					object.getY(), object.getPlane()), 60000);
			player.getPackets().sendGameMessage("You slash through the web!");
		} else
			player.getPackets().sendGameMessage(
					"You fail to cut through the web.");
	}
	
	public static void handleItemOnObject(final Player player,
			final WorldObject object, final int interfaceId, final Item item) {
		final int itemId = item.getId();
		final ObjectDefinitions objectDef = object.getDefinitions();
	}
}
