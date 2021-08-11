package com.rs.game.dialogue;

import com.rs.game.player.Player;
import com.rs.utilities.LogUtility;
import com.rs.utilities.Utility;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.SneakyThrows;

public class DialogueEventRepository {

    @SuppressWarnings("unchecked")
    @SneakyThrows(Exception.class)
    public static void init() {
        Class<DialogueEventListener>[] regular = Utility.getClasses("com.rs.game.dialogue.impl");
        for (Class<DialogueEventListener> c : regular) {
            if (c.isAnonymousClass()) // next
                continue;
            DIALOGUE_MAP.put(c.getSimpleName(), c);
        }
        LogUtility.log(LogUtility.LogType.INFO, "Registered " + DIALOGUE_MAP.size() + " dialogues.");
    }

    public static Class<? extends DialogueEventListener> getListener(String key, Player player, Object... args) {
        return DIALOGUE_MAP.get(key);
    }

    private static final Object2ObjectOpenHashMap<Object, Class<? extends DialogueEventListener>> DIALOGUE_MAP = new Object2ObjectOpenHashMap<>();
}
