package com.rs.game.player.controllers;

import java.util.HashMap;

import com.rs.game.minigames.BrimhavenAgility;
import com.rs.game.minigames.WarriorsGuild;
import com.rs.game.minigames.duel.DuelArena;
import com.rs.game.minigames.duel.DuelControler;
import com.rs.utils.Logger;

public class ControlerHandler {

	private static final HashMap<Object, Class<? extends Controller>> handledControlers = new HashMap<Object, Class<? extends Controller>>();

	@SuppressWarnings("unchecked")
	public static final void init() {
		try {
			Class<Controller> value1 = (Class<Controller>) Class.forName(Wilderness.class.getCanonicalName());
			handledControlers.put("Wilderness", value1);
			Class<Controller> value2 = (Class<Controller>) Class.forName(Kalaboss.class.getCanonicalName());
			handledControlers.put("Kalaboss", value2);
			Class<Controller> value4 = (Class<Controller>) Class.forName(GodWars.class.getCanonicalName());
			handledControlers.put("GodWars", value4);
			Class<Controller> value5 = (Class<Controller>) Class.forName(ZGDControler.class.getCanonicalName());
			handledControlers.put("ZGDControler", value5);
			Class<Controller> value9 = (Class<Controller>) Class.forName(DuelArena.class.getCanonicalName());
			handledControlers.put("DuelArena", value9);
			Class<Controller> value10 = (Class<Controller>) Class.forName(DuelControler.class.getCanonicalName());
			handledControlers.put("DuelControler", value10);
			Class<Controller> value11 = (Class<Controller>) Class.forName(CorpBeastControler.class.getCanonicalName());
			handledControlers.put("CorpBeastControler", value11);
			Class<Controller> value14 = (Class<Controller>) Class.forName(DTControler.class.getCanonicalName());
			handledControlers.put("DTControler", value14);
			Class<Controller> value15 = (Class<Controller>) Class.forName(JailControler.class.getCanonicalName());
			handledControlers.put("JailControler", value15);
			handledControlers.put("NomadsRequiem",
					(Class<Controller>) Class.forName(NomadsRequiem.class.getCanonicalName()));
			handledControlers.put("BorkControler",
					(Class<Controller>) Class.forName(BorkControler.class.getCanonicalName()));
			handledControlers.put("BrimhavenAgility",
					(Class<Controller>) Class.forName(BrimhavenAgility.class.getCanonicalName()));
			handledControlers.put("FightCavesControler",
					(Class<Controller>) Class.forName(FightCaves.class.getCanonicalName()));
			handledControlers.put("FightKilnControler",
					(Class<Controller>) Class.forName(FightKiln.class.getCanonicalName()));
			handledControlers.put("Barrows", (Class<Controller>) Class.forName(Barrows.class.getCanonicalName()));
			handledControlers.put("QueenBlackDragonControler",
					(Class<Controller>) Class.forName(QueenBlackDragonController.class.getCanonicalName()));
			handledControlers.put("CrucibleControler",
					(Class<Controller>) Class.forName(CrucibleControler.class.getCanonicalName()));
			handledControlers.put("RuneEssenceController",
					(Class<Controller>) Class.forName(RuneEssenceController.class.getCanonicalName()));
			handledControlers.put("TerrorDogsTarnsLairController",
					(Class<Controller>) Class.forName(TerrorDogsTarnsLairController.class.getCanonicalName()));
			handledControlers.put("WarriorsGuild",
					(Class<Controller>) Class.forName(WarriorsGuild.class.getCanonicalName()));
			handledControlers.put("JadinkoLair",
					(Class<Controller>) Class.forName(JadinkoLair.class.getCanonicalName()));
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
