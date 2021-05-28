package com.rs.plugin;

import java.lang.annotation.Annotation;
import java.lang.annotation.IncompleteAnnotationException;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import com.rs.Settings;
import com.rs.cache.loaders.ObjectDefinitions;
import com.rs.game.WorldObject;
import com.rs.game.WorldTile;
import com.rs.game.item.Item;
import com.rs.game.player.Player;
import com.rs.game.route.RouteEvent;
import com.rs.io.InputStream;
import com.rs.plugin.listener.ObjectType;
import com.rs.plugin.wrapper.ObjectSignature;
import com.rs.utils.Logger;
import com.rs.utils.Utils;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;

/**
 * @author Dennis
 */
public final class ObjectDispatcher {
	
	/**
	 * The object map which contains all the Objects on the world.
	 */
	private static final Object2ObjectArrayMap<ObjectSignature, ObjectType> OBJECTS = new Object2ObjectArrayMap<>();
	
	/**
	 * Executes the specified Objects if it's registered.
	 * @param player the player executing the Objects.
	 * @param parts the string which represents a Objects.
	 */
	public static void execute(Player player, WorldObject object, int optionId) {
		Optional<ObjectType> objects = getObject(object, object.getId());
		
		if(!objects.isPresent()) {
			player.getPackets().sendGameMessage("Object: " + object.getId() + " is not handled yet.");
			return;
		}
		try {
			objects.get().execute(player, object, optionId);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets a Objects which matches the {@code identifier}.
	 * @param identifier the identifier to check for matches.
	 * @return an Optional with the found value, {@link Optional#empty} otherwise.
	 */
	private static Optional<ObjectType> getObject(WorldObject object, int objectId) {
		for(Entry<ObjectSignature, ObjectType> objects : OBJECTS.entrySet()) {
			if (isObjetId(objects.getValue(), objectId) || isObjectNamed(objects.getValue(), object)) {
				return Optional.of(objects.getValue());
			}
		}
		return Optional.empty();
	}
	
	/**
	 * Checks if the the Object Id matches the signature
	 * @param object
	 * @param objectId
	 * @return
	 */
	private static boolean isObjetId(ObjectType object, int objectId) {
		Annotation annotation = object.getClass().getAnnotation(ObjectSignature.class);
		ObjectSignature signature = (ObjectSignature) annotation;
		return Arrays.stream(signature.objectId()).anyMatch(right -> objectId == right);
	}
	
	/**
	 * Checks if the the Object Name matches the signature
	 * @param object
	 * @param objectId
	 * @return
	 */
	private static boolean isObjectNamed(ObjectType object, WorldObject worldObject) {
		Annotation annotation = object.getClass().getAnnotation(ObjectSignature.class);
		ObjectSignature signature = (ObjectSignature) annotation;
		return Arrays.stream(signature.name()).anyMatch(objectName -> worldObject.getDefinitions().getName().contains(objectName));
	}
	
	/**
	 * Loads all the Objects into the {@link #OBJECTS} list.
	 * <p></p>
	 * <b>Method should only be called once on start-up.</b>
	 */
	public static void load() {
		List<ObjectType> objectTypes = Utils.getClassesInDirectory("com.rs.plugin.impl.objects").stream().map(clazz -> (ObjectType) clazz).collect(Collectors.toList());
		
		for(ObjectType object : objectTypes) {
			if(object.getClass().getAnnotation(ObjectSignature.class) == null) {
				throw new IncompleteAnnotationException(ObjectSignature.class, object.getClass().getName() + " has no annotation.");
			}
			OBJECTS.put(object.getClass().getAnnotation(ObjectSignature.class), object);
		}
	}
	
	/**
	 * Reloads all the Objects into the {@link #OBJECTS} list.
	 * <p></p>
	 * <b>This method can be invoked on run-time to clear all the commands in the list
	 * and add them back in a dynamic fashion.</b>
	 */
	public static void reload() {
		OBJECTS.clear();
		load();
	}
	
	public static void handleOption(final Player player, InputStream stream, int option) {
		if (!player.isStarted() || !player.isClientLoadedMapRegion()
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
		
		WorldObject mapObject = WorldObject.getObjectWithId(tile, id);
		if (mapObject == null || mapObject.getId() != id)
			return;
		
		final WorldObject object = mapObject;
		
		player.stopAll();
		if (forceRun)
			player.setRun(forceRun);
		
		if (option == -1) {
			handleOptionExamine(player, object);
			return;
		}


		player.setRouteEvent(new RouteEvent(object, new Runnable() {
			@Override
			public void run() {
				player.stopAll();
				player.faceObject(object);
				ObjectDispatcher.execute(player, object, option);
			}
		}, false));
	}

	public static void handleOptionExamine(final Player player, final WorldObject object) {
		if (Settings.DEBUG) {
			int offsetX = object.getX() - player.getX();
			int offsetY = object.getY() - player.getY();
			System.out.println("Offsets" + offsetX + " , " + offsetY);
		}
		player.getPackets().sendGameMessage("It's an " + object.getDefinitions().name + ".");
		if (Settings.DEBUG)
			if (Settings.DEBUG)

				Logger.log("ObjectHandler",
						"examined object id : " + object.getId() + ", " + object.getX() + ", " + object.getY() + ", "
								+ object.getPlane() + ", " + object.getType() + ", " + object.getRotation() + ", "
								+ object.getDefinitions().name);
	}
	
	@SuppressWarnings("unused")
	public static void handleItemOnObject(final Player player, final WorldObject object, final int interfaceId,
			final Item item) {
		final int itemId = item.getId();
		final ObjectDefinitions objectDef = object.getDefinitions();
		player.setRouteEvent(new RouteEvent(object, new Runnable() {
			@Override
			public void run() {
				player.faceObject(object);
				
					if (Settings.DEBUG)
						System.out.println("Item on object: " + object.getId());
				}
			
		}, false));
	}
}