package com.rs.game.dialogue;

import java.util.HashMap;

import com.rs.game.player.Player;
import com.rs.utilities.Utils;

import lombok.SneakyThrows;

public class DialogueEventRepository {
	
	public static final Class<? extends DialogueEventListener> getListener(String key, Player player, Object... args){
		return handledDialogues.get(key);
	}

	private static final HashMap<Object, Class<? extends DialogueEventListener>> handledDialogues = new HashMap<Object, Class<? extends DialogueEventListener>>();
	
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
