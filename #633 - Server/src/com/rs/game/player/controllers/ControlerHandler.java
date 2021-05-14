package com.rs.game.player.controllers;

import java.util.HashMap;

import com.rs.game.minigames.WarriorsGuild;
import com.rs.game.minigames.duel.DuelArena;
import com.rs.utils.Logger;

public class ControlerHandler {

	private static final HashMap<Object, Class<? extends Controller>> handledControlers = new HashMap<Object, Class<? extends Controller>>();

	@SuppressWarnings("unchecked")
	public static final void init() {
		try {
			Class<Controller> value1 = (Class<Controller>) Class.forName(Wilderness.class.getCanonicalName());
			handledControlers.put("Wilderness", value1);
			Class<Controller> value4 = (Class<Controller>) Class.forName(GodWars.class.getCanonicalName());
			handledControlers.put("GodWars", value4);
			Class<Controller> value5 = (Class<Controller>) Class.forName(ZGDControler.class.getCanonicalName());
			handledControlers.put("ZGDControler", value5);
			Class<Controller> value9 = (Class<Controller>) Class.forName(DuelArena.class.getCanonicalName());
			handledControlers.put("DuelArena", value9);
			Class<Controller> value10 = (Class<Controller>) Class.forName(DuelControler.class.getCanonicalName());
			handledControlers.put("DuelControler", value10);
			Class<Controller> value15 = (Class<Controller>) Class.forName(JailControler.class.getCanonicalName());
			handledControlers.put("JailControler", value15);
			handledControlers.put("QueenBlackDragonControler",
					(Class<Controller>) Class.forName(QueenBlackDragonController.class.getCanonicalName()));
			handledControlers.put("WarriorsGuild",
					(Class<Controller>) Class.forName(WarriorsGuild.class.getCanonicalName()));
			// handledControlers.put("SlaughterFieldsControler",
			// (Class<Controller>)
			// Class.forName(SlaughterFieldsControler.class.getCanonicalName()));
		} catch (Throwable e) {
			Logger.handle(e);
		}
	}

	public static final void reload() {
		handledControlers.clear();
		init();
	}

	public static final Controller getControler(Object key) {
		if (key instanceof Controller)
			return (Controller) key;
		Class<? extends Controller> classC = handledControlers.get(key);
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
