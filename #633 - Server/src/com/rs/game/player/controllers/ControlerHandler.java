package com.rs.game.player.controllers;

import java.util.HashMap;

import com.rs.utils.Logger;
import com.rs.utils.Utils;

public class ControlerHandler {

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

	public static final Controller getControler(Object key) {
		if (key instanceof Controller)
			return (Controller) key;
		Class<Controller> classC = handledControlers.get(key);
		if (classC == null)
			return null;
		try {
			return classC.newInstance();
		} catch (Throwable e) {
			Logger.handle(e);
		}
		return null;
	}
}
