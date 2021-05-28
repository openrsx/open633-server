package com.rs.game;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.rs.cache.loaders.ObjectDefinitions;
import com.rs.cores.CoresManager;
import com.rs.game.item.FloorItem;
import com.rs.game.item.Item;
import com.rs.game.player.Player;
import com.rs.utils.Logger;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class WorldObject extends WorldTile {

	private int id;
	private int type;
	private int rotation;
	private int life;

	public WorldObject(int id, int type, int rotation, WorldTile tile) {
		super(tile.getX(), tile.getY(), tile.getPlane());
		this.id = id;
		this.type = type;
		this.rotation = rotation;
		this.life = 1;
	}

	public WorldObject(int id, int type, int rotation, int x, int y, int plane) {
		super(x, y, plane);
		this.id = id;
		this.type = type;
		this.rotation = rotation;
		this.life = 1;
	}

	public WorldObject(int id, int type, int rotation, int x, int y, int plane, int life) {
		super(x, y, plane);
		this.id = id;
		this.type = type;
		this.rotation = rotation;
		this.life = life;
	}

	public WorldObject(WorldObject object) {
		super(object.getX(), object.getY(), object.getPlane());
		this.id = object.id;
		this.type = object.type;
		this.rotation = object.rotation;
		this.life = object.life;
	}

	public void decrementObjectLife() {
		this.life--;
	}

	public ObjectDefinitions getDefinitions() {
		return ObjectDefinitions.getObjectDefinitions(id);
	}
	
	public static final boolean isSpawnedObject(WorldObject object) {
		return World.getRegion(object.getRegionId()).getSpawnedObjects().contains(object);
	}

	public static final void spawnObject(WorldObject object) {
		World.getRegion(object.getRegionId()).spawnObject(object, object.getPlane(), object.getXInRegion(),
				object.getYInRegion(), false);
	}

	public static final void unclipTile(WorldTile tile) {
		World.getRegion(tile.getRegionId()).unclip(tile.getPlane(), tile.getXInRegion(), tile.getYInRegion());
	}

	public static final void removeObject(WorldObject object) {
		World.getRegion(object.getRegionId()).removeObject(object, object.getPlane(), object.getXInRegion(),
				object.getYInRegion());
	}

	public static final void spawnObjectTemporary(final WorldObject object, long time) {
		spawnObject(object);
		CoresManager.slowExecutor.schedule(new Runnable() {
			@Override
			public void run() {
				try {
					if (!isSpawnedObject(object))
						return;
					removeObject(object);
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}

		}, time, TimeUnit.MILLISECONDS);
	}

	public static final boolean removeObjectTemporary(final WorldObject object, long time) {
		removeObject(object);
		CoresManager.slowExecutor.schedule(new Runnable() {
			@Override
			public void run() {
				try {
					spawnObject(object);
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}

		}, time, TimeUnit.MILLISECONDS);
		return true;
	}

	public static final void spawnTempGroundObject(final WorldObject object, final int replaceId, long time) {
		spawnObject(object);
		CoresManager.slowExecutor.schedule(new Runnable() {
			@Override
			public void run() {
				try {
					removeObject(object);
					//seems weird.
					FloorItem.createGroundItem(new Item(replaceId), object, null, false, 180, true);
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, time, TimeUnit.MILLISECONDS);
	}

	public static final WorldObject getStandartObject(WorldTile tile) {
		return World.getRegion(tile.getRegionId()).getStandartObject(tile.getPlane(), tile.getXInRegion(),
				tile.getYInRegion());
	}

	public static final WorldObject getObjectWithType(WorldTile tile, int type) {
		return World.getRegion(tile.getRegionId()).getObjectWithType(tile.getPlane(), tile.getXInRegion(),
				tile.getYInRegion(), type);
	}

	public static final WorldObject getObjectWithSlot(WorldTile tile, int slot) {
		return World.getRegion(tile.getRegionId()).getObjectWithSlot(tile.getPlane(), tile.getXInRegion(),
				tile.getYInRegion(), slot);
	}

	public static final boolean containsObjectWithId(WorldTile tile, int id) {
		return World.getRegion(tile.getRegionId()).containsObjectWithId(tile.getPlane(), tile.getXInRegion(),
				tile.getYInRegion(), id);
	}

	public static final WorldObject getObjectWithId(WorldTile tile, int id) {
		return World.getRegion(tile.getRegionId()).getObjectWithId(tile.getPlane(), tile.getXInRegion(), tile.getYInRegion(),
				id);
	}

	public static final void sendObjectAnimation(WorldObject object, Animation animation) {
		sendObjectAnimation(null, object, animation);
	}

	public static final void sendObjectAnimation(Entity creator, WorldObject object, Animation animation) {
		if (creator == null) {
			World.players().filter(p -> p.withinDistance(object)).forEach(player -> player.getPackets().sendObjectAnimation(object, animation));
		} else {
			for (int regionId : creator.getMapRegionsIds()) {
				List<Integer> playersIndexes = World.getRegion(regionId).getPlayerIndexes();
				if (playersIndexes == null)
					continue;
				for (Integer playerIndex : playersIndexes) {
					Player player = World.getPlayers().get(playerIndex);
					if (player == null || !player.isStarted() || player.hasFinished()
							|| !player.withinDistance(object))
						continue;
					player.getPackets().sendObjectAnimation(object, animation);
				}
			}
		}
	}
}