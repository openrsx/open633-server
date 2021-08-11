package com.rs.game.npc.combat;

import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.impl.DefaultCombat;
import com.rs.game.player.Player;
import com.rs.plugin.listener.NPCType;
import com.rs.plugin.wrapper.NPCSignature;
import com.rs.utilities.LogUtility;
import com.rs.utilities.ReflectionUtils;
import io.vavr.control.Try;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.SneakyThrows;

import java.lang.annotation.Annotation;
import java.lang.annotation.IncompleteAnnotationException;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;

/**
 * TODO: Redo Drops
 *
 * @author Dennis
 */
public final class NPCCombatDispatcher {

    /**
     * The object map which contains all the mob on the world.
     */
    private static final Object2ObjectOpenHashMap<MobCombatSignature, MobCombatInterface> COMBATANTS = new Object2ObjectOpenHashMap<>();

    private int mobValue = 600;

    /**
     * Executes the specified mob if it's registered.
     *
     * @param player the player executing the mob.
     * @throws Exception
     */
    @SneakyThrows(Exception.class)
    public int customDelay(Player player, NPC npc) {
        Optional<MobCombatInterface> mobCombat = getMobCombatant(npc);
        if (!mobCombat.isPresent()) {
            DefaultCombat defaultScript = new DefaultCombat();
            return defaultScript.execute(player, npc);
        }
        mobCombat.ifPresent(value -> Try.run(() -> mobValue = value.execute(player, npc)));
        return mobValue;
    }

    /**
     * Gets a mob which matches the {@code identifier}.
     *
     * @return an Optional with the found value, {@link Optional#empty} otherwise.
     */
    private Optional<MobCombatInterface> getMobCombatant(NPC mob) {
        for (Entry<MobCombatSignature, MobCombatInterface> MobCombatInterface : COMBATANTS.entrySet()) {
            if (isMobId(MobCombatInterface.getValue(), mob.getId()) || isMobNamed(MobCombatInterface.getValue(), mob)) {
                return Optional.of(MobCombatInterface.getValue());
            }
        }
        return Optional.empty();
    }

    private boolean isMobId(MobCombatInterface MobCombatInterface, int mobId) {
        Annotation annotation = MobCombatInterface.getClass().getAnnotation(MobCombatSignature.class);
        MobCombatSignature signature = (MobCombatSignature) annotation;
        return Arrays.stream(signature.mobId()).anyMatch(id -> mobId == id);
    }

    /**
     * Checks if the the NPC Name matches the signature
     *
     * @return
     */
    private boolean isMobNamed(MobCombatInterface mobType, NPC mob) {
        Annotation annotation = mobType.getClass().getAnnotation(MobCombatSignature.class);
        MobCombatSignature signature = (MobCombatSignature) annotation;
        return Arrays.stream(signature.mobName()).anyMatch(mobName -> mob.getDefinitions().getName().equalsIgnoreCase(mobName));
    }

    /**
     * Loads all the mob into the {@link #COMBATANTS} list.
     * <p></p>
     * <b>Method should only be called once on start-up.</b>
     */
    public static void load() {
        List<MobCombatInterface> mobTypes = ReflectionUtils.getImplementersOf(MobCombatInterface.class);
        mobTypes.forEach(npc -> COMBATANTS.put(npc.getClass().getAnnotation(MobCombatSignature.class), npc));

        LogUtility.log(LogUtility.LogType.INFO, "Registered " + COMBATANTS.size() + " mob combatants.");
    }

    /**
     * Reloads all the mob into the {@link #COMBATANTS} list.
     * <p></p>
     * <b>This method can be invoked on run-time to clear all the commands in the list
     * and add them back in a dynamic fashion.</b>
     */
    public static void reload() {
        COMBATANTS.clear();
        load();
    }
}