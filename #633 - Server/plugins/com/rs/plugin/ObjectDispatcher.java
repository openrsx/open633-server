package com.rs.plugin;

import java.lang.annotation.Annotation;
import java.lang.annotation.IncompleteAnnotationException;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import com.rs.GameConstants;
import com.rs.cache.loaders.ObjectDefinitions;
import com.rs.game.GameObject;
import com.rs.game.item.Item;
import com.rs.game.player.Player;
import com.rs.game.route.RouteEvent;
import com.rs.plugin.listener.ObjectType;
import com.rs.plugin.wrapper.ObjectSignature;
import com.rs.utilities.Utility;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import lombok.SneakyThrows;

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
	 * 
	 * @param player the player executing the Objects.
	 * @param parts  the string which represents a Objects.
	 */
	@SneakyThrows(Exception.class)
	public static void execute(Player player, GameObject object, int optionId) {
		Optional<ObjectType> objects = getObject(object, object.getId());

		if (!objects.isPresent()) {
			player.getPackets().sendGameMessage("Object: " + object.getId() + " is not handled yet.");
			return;
		}
		objects.get().execute(player, object, optionId);
	}

	/**
	 * Gets a Objects which matches the {@code identifier}.
	 * 
	 * @param identifier the identifier to check for matches.
	 * @return an Optional with the found value, {@link Optional#empty} otherwise.
	 */
	private static Optional<ObjectType> getObject(GameObject object, int objectId) {
		for (Entry<ObjectSignature, ObjectType> objects : OBJECTS.entrySet()) {
			if (isObjetId(objects.getValue(), objectId) || isObjectNamed(objects.getValue(), object)) {
				return Optional.of(objects.getValue());
			}
		}
		return Optional.empty();
	}

	/**
	 * Checks if the the Object Id matches the signature
	 * 
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
	 * 
	 * @param object
	 * @param objectId
	 * @return
	 */
	private static boolean isObjectNamed(ObjectType object, GameObject worldObject) {
		Annotation annotation = object.getClass().getAnnotation(ObjectSignature.class);
		ObjectSignature signature = (ObjectSignature) annotation;
		return Arrays.stream(signature.name())
				.anyMatch(objectName -> worldObject.getDefinitions().getName().contains(objectName));
	}

	/**
	 * Loads all the Objects into the {@link #OBJECTS} list.
	 * <p>
	 * </p>
	 * <b>Method should only be called once on start-up.</b>
	 */
	public static void load() {
		List<ObjectType> objectTypes = Utility.getClassesInDirectory("com.rs.plugin.impl.objects").stream()
				.map(clazz -> (ObjectType) clazz).collect(Collectors.toList());

		for (ObjectType object : objectTypes) {
			if (object.getClass().getAnnotation(ObjectSignature.class) == null) {
				throw new IncompleteAnnotationException(ObjectSignature.class,
						object.getClass().getName() + " has no annotation.");
			}
			OBJECTS.put(object.getClass().getAnnotation(ObjectSignature.class), object);
		}
	}

	/**
	 * Reloads all the Objects into the {@link #OBJECTS} list.
	 * <p>
	 * </p>
	 * <b>This method can be invoked on run-time to clear all the commands in the
	 * list and add them back in a dynamic fashion.</b>
	 */
	public static void reload() {
		OBJECTS.clear();
		load();
	}

	@SuppressWarnings("unused")
	public static void handleItemOnObject(final Player player, final GameObject object, final int interfaceId,
			final Item item) {
		final int itemId = item.getId();
		final ObjectDefinitions objectDef = object.getDefinitions();
		player.setRouteEvent(new RouteEvent(object, () ->  {
			player.faceObject(object);
			if (GameConstants.DEBUG)
				System.out.println("Item on object: " + object.getId());
		}, false));
	}
}