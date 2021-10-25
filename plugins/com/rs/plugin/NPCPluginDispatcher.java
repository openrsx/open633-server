package com.rs.plugin;

import com.rs.game.item.Item;
import com.rs.game.map.World;
import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.game.player.controller.ControllerHandler;
import com.rs.game.route.RouteEvent;
import com.rs.io.InputStream;
import com.rs.plugin.listener.NPCType;
import com.rs.plugin.wrapper.NPCSignature;
import com.rs.utilities.ReflectionUtils;
import io.vavr.control.Try;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;

/**
 * @author Dennis
 */
public class NPCPluginDispatcher {

    /**
     * The NPCS map which contains all the NPCS on the world.
     */
    private static final Object2ObjectOpenHashMap<NPCSignature, NPCType> MOBS = new Object2ObjectOpenHashMap<>();

    /**
     * Executes the specified NPCS if it's registered.
     *
     * @param player the player executing the NPCS.
     * @param parts  the string which represents a NPCS.
     */
    public static void execute(Player player, NPC npc, int option) {
        getMob(npc, npc.getId()).ifPresent(mob -> Try.run(() -> mob.execute(player, npc, option)));
    }

    /**
     * Gets a NPCS which matches the {@code identifier}.
     *
     * @param identifier the identifier to check for matches.
     * @return an Optional with the found value, {@link Optional#empty} otherwise.
     */
    private static Optional<NPCType> getMob(NPC mob, int npcId) {
        for (Entry<NPCSignature, NPCType> mobType : MOBS.entrySet()) {
            if (isNPCId(mobType.getValue(), npcId) || isMobNamed(mobType.getValue(), mob)) {
                return Optional.of(mobType.getValue());
            }
        }
        return Optional.empty();
    }

    /**
     * Checks if the the NPCS Id matches the signature
     *
     * @param mob
     * @param npcId
     * @return
     */
    private static boolean isNPCId(NPCType mob, int npcId) {
        Annotation annotation = mob.getClass().getAnnotation(NPCSignature.class);
        NPCSignature signature = (NPCSignature) annotation;
        return Arrays.stream(signature.npcId()).anyMatch(id -> npcId == id);
    }

    /**
     * Checks if the the NPC Name matches the signature
     *
     * @param mobType
     * @param objectId
     * @return
     */
    private static boolean isMobNamed(NPCType mobType, NPC mob) {
        Annotation annotation = mobType.getClass().getAnnotation(NPCSignature.class);
        NPCSignature signature = (NPCSignature) annotation;
        return Arrays.stream(signature.name()).anyMatch(mobName -> mob.getDefinitions().getName().contains(mobName));
    }

    /**
     * Loads all the NPCS into the {@link #MOBS} list.
     * <p></p>
     * <b>Method should only be called once on start-up.</b>
     */
    public static void load() {
        List<NPCType> mobTypes = ReflectionUtils.getImplementersOf(NPCType.class);
        mobTypes.forEach(npc -> MOBS.put(npc.getClass().getAnnotation(NPCSignature.class), npc));
    }

    /**
     * Reloads all the NPCS into the {@link #MOBS} list.
     * <p></p>
     * <b>This method can be invoked on run-time to clear all the NPCS in the list
     * and add them back in a dynamic fashion.</b>
     */
    public static void reload() {
        MOBS.clear();
        load();
    }

    @SuppressWarnings("unused")
    private static boolean forceRun;
    private static int npcIndex;

    public static void executeMobInteraction(final Player player, InputStream stream, int optionId) {
        if (optionId == -1)
            npcIndex = stream.readUnsignedShort();
        else if (optionId == 1) {
            npcIndex = stream.readUnsignedShort();
            forceRun = stream.readByte() == 1;
        } else if (optionId == 2) {
            npcIndex = stream.readUnsignedShortLE128();
            forceRun = stream.readByte() == 1;
        } else if (optionId == 3) {
            forceRun = stream.readByte() == 1;
            npcIndex = stream.readUnsignedShortLE();
        } else if (optionId == 4) {
            forceRun = stream.readByteC() == 1;
            npcIndex = stream.readUnsignedShort();
        }
        final NPC npc = World.getNPCs().get(npcIndex);
        if (npc == null || npc.isCantInteract() || npc.isDead() || npc.isFinished()
                || !player.getMapRegionsIds().contains(npc.getRegionId()) || player.getMovement().isLocked())
            return;
        player.setRouteEvent(new RouteEvent(npc, () -> {
            if (!ControllerHandler.getController(player).isPresent()) {
                switch (optionId) {
                    case 1:
                        ControllerHandler.execute(player, controller -> controller.processNPCClick1(player, npc));
                        break;
                    case 2:
                        ControllerHandler.execute(player, controller -> controller.processNPCClick2(player, npc));
                        break;
                    case 3:
                        ControllerHandler.execute(player, controller -> controller.processNPCClick3(player, npc));
                        break;
                    case 4:
                        ControllerHandler.execute(player, controller -> controller.processNPCClick4(player, npc));
                        break;
                }
                return;
            }
            NPCPluginDispatcher.execute(player, npc, optionId);
        }, npc.getDefinitions().name.contains("Banker") || npc.getDefinitions().name.contains("banker")));

    }

    public static void handleItemOnNPC(final Player player, final NPC npc, final Item item) {
        if (item == null)
            return;
    }
}