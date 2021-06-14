package com.rs.game.dialogue;

import com.rs.game.player.Player;
import com.rs.utilities.Utils;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import lombok.SneakyThrows;

public class DialogueEventRepository {
	
	public static final Class<? extends DialogueEventListener> getListener(String key, Player player, Object... args){
		return handledDialogues.get(key);
	}

	private static Object2ObjectArrayMap<Object, Class<? extends DialogueEventListener>> handledDialogues = new Object2ObjectArrayMap<>();

	@SneakyThrows(Exception.class)
	public static final void init() {
		@SuppressWarnings("unchecked")
		Class<DialogueEventListener>[] regular = Utils.getClasses("com.rs.game.dialogue.impl");
		for (Class<DialogueEventListener> c : regular) {
			if (c.isAnonymousClass()) // next
				continue;
			handledDialogues.put(c.getSimpleName(), c);
		}
	}
}
