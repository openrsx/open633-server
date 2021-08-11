package com.rs.plugin;

import com.rs.game.item.Item;
import com.rs.game.player.Player;
import com.rs.plugin.listener.InventoryType;
import com.rs.plugin.wrapper.InventoryWrapper;
import com.rs.utilities.ReflectionUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;

/**
 * @author Dennis
 */
public final class InventoryPluginDispatcher {

    /**
     * The object map which contains all the interface on the world.
     */
    private static final Object2ObjectOpenHashMap<InventoryWrapper, InventoryType> ITEMS = new Object2ObjectOpenHashMap<>();

    /**
     * Executes the specified Item if it's registered.
     *
     * @param player the player executing the item.
     * @param parts  the string which represents a item.
     */
    public static void execute(Player player, Item item, int optionId) {
        getItem(item.getId()).ifPresent(specifiedItem -> {
            try {
                specifiedItem.execute(player, item, optionId);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        });
    }

    /**
     * Gets a Item which matches the {@code identifier}.
     *
     * @param identifier the identifier to check for matches.
     * @return an Optional with the found value, {@link Optional#empty} otherwise.
     */
    private static Optional<InventoryType> getItem(int itemId) {
        for (Entry<InventoryWrapper, InventoryType> InventoryType : ITEMS.entrySet()) {
            if (isCorrectItem(InventoryType.getValue(), itemId)) {
                return Optional.of(InventoryType.getValue());
            }
        }
        return Optional.empty();
    }

    private static boolean isCorrectItem(InventoryType InventoryType, int interfaceId) {
        Annotation annotation = InventoryType.getClass().getAnnotation(InventoryWrapper.class);
        InventoryWrapper signature = (InventoryWrapper) annotation;
        return Arrays.stream(signature.itemId()).anyMatch(right -> interfaceId == right);
    }

    /**
     * Loads all the Items into the {@link #ITEMS} list.
     * <p></p>
     * <b>Method should only be called once on start-up.</b>
     */
    public static void load() {
        List<InventoryType> inventoryItem = ReflectionUtils.getImplementersOf(InventoryType.class);
        inventoryItem.forEach(id -> ITEMS.put(id.getClass().getAnnotation(InventoryWrapper.class), id));
    }

    /**
     * Reloads all the Items into the {@link #ITEMS} list.
     * <p></p>
     * <b>This method can be invoked on run-time to clear all the commands in the list
     * and add them back in a dynamic fashion.</b>
     */
    public static void reload() {
        ITEMS.clear();
        load();
    }
}