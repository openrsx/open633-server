package com.rs.game.player.controllers;

import java.util.HashMap;

import com.rs.utilities.Logger;
import com.rs.utilities.Utils;

import lombok.SneakyThrows;

public class ControllerHandler {

	private static final HashMap<Object, Class<Controller>> handledControlers = new HashMap<Object, Class<Controller>>();

	public static final void init() {
		try {
			@SuppressWarnings("unchecked")
			Class<Controller>[] regular = Utils.getClasses("com.rs.game.player.controllers");
			for (Class<Controller> c : regular) {
				if (c.isAnonymousClass()) // next
					continue;
				handledControlers.put(c.getSimpleName(), c);
			}
			
		} catch (Throwable e) {
			Logger.handle(e);
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