package com.rs.game.player.controllers;

import com.rs.utilities.Utility;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import lombok.SneakyThrows;

public class ControllerHandler {

	static Object2ObjectArrayMap<Object, Class<Controller>> handledControlers = new Object2ObjectArrayMap<>();

	@SneakyThrows(Throwable.class)
	public static final void init() {
		@SuppressWarnings("unchecked")
		Class<Controller>[] regular = Utility.getClasses("com.rs.game.player.controllers");
		for (Class<Controller> c : regular) {
			if (c.isAnonymousClass()) // next
				continue;
			handledControlers.put(c.getSimpleName(), c);
		}
	}

	@SneakyThrows(Throwable.class)
	public static final Controller getController(Object key) {
		if (key instanceof Controller)
			return (Controller) key;
		Class<Controller> classC = handledControlers.get(key);
		if (classC == null)
			return null;
		return classC.newInstance();
	}
}