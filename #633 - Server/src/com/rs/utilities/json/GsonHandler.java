package com.rs.utilities.json;

import java.util.ArrayList;
import java.util.List;

import com.rs.utilities.json.impl.NPCAutoSpawn;
import com.rs.utilities.json.impl.ObjectSpawnLoader;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import lombok.SneakyThrows;

/**
 * @author Tyluur <itstyluur@gmail.com>
 * @since Mar 1, 2014
 */
public class GsonHandler {

	/**
	 * Initializes all json loaders
	 */
	@SneakyThrows(Exception.class)
	public static void initialize() {
		try {
			addJsonLoaders();
		} catch (InstantiationException | IllegalAccessException e1) {
			e1.printStackTrace();
		}
		for (GsonLoader<?> loader : CLASSES) {
			loader.initialize();
		}
		LOADED = Boolean.TRUE;
	}

	/**
	 * Waits for the json loaders to be loaded
	 */
	public static void waitForLoad() {
		while (!LOADED) {
			System.out.flush();
		}
	}

	/**
	 * Adds all json loaders to the map
	 * 
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public static void addJsonLoaders() throws InstantiationException, IllegalAccessException {
		CLASSES.add(NPCAutoSpawn.class.newInstance());
		CLASSES.add(ObjectSpawnLoader.class.newInstance());
	}

	/**
	 * Gets a {@link #JsonLoader} by the class
	 * 
	 * @param <T>
	 */
	@SuppressWarnings("unchecked")
	@SneakyThrows(Exception.class)
	public static <T> T getJsonLoader(Class<?> clazz) {
		GsonLoader<?> loader = CACHED_LOADERS.get(clazz.getSimpleName());
		if (loader != null) {
			return (T) loader;
		} else {
			for (GsonLoader<?> listLoader : CLASSES) {
				if (listLoader.getClass().getSimpleName().equals(clazz.getSimpleName())) {
					CACHED_LOADERS.put(listLoader.getClass().getSimpleName(), listLoader);
					return (T) listLoader;
				}
			}
		}
		return null;
	}

	/**
	 * If all loaders have loaded
	 */
	public static boolean LOADED = Boolean.FALSE;

	/** The cached loaders */
	private static Object2ObjectArrayMap<String, GsonLoader<?>> CACHED_LOADERS = new Object2ObjectArrayMap<String, GsonLoader<?>>();

	/**
	 * Adds all of the loaders to the map
	 */
	private static final List<GsonLoader<?>> CLASSES = new ArrayList<GsonLoader<?>>();
}
