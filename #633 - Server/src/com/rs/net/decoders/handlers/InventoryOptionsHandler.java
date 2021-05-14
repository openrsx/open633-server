package com.rs.net.decoders.handlers;

import com.rs.Settings;
import com.rs.game.World;
import com.rs.game.WorldTile;
import com.rs.game.item.Item;
import com.rs.game.npc.familiar.Familiar.SpecialAttack;
import com.rs.game.player.Inventory;
import com.rs.game.player.Player;
import com.rs.io.InputStream;
import com.rs.utils.Logger;

public class InventoryOptionsHandler {

	public static void handleItemOption2(final Player player, final int slotId,
			final int itemId, Item item) {
		if (player.isLocked() || player.getEmotesManager().isDoingEmote())
			return;
	}

	public static void handleItemOption1(final Player player, final int slotId,
			final int itemId, Item item) {
		if (player.isLocked() || player.getEmotesManager().isDoingEmote())
			return;
		player.stopAll(false);

		if (Settings.DEBUG)
			Logger.log("ItemHandler", "Item Select:" + itemId + ", Slot Id:"
					+ slotId);
	}

	/*
	 * returns the other
	 */
	public static Item contains(int id1, Item item1, Item item2) {
		if (item1.getId() == id1)
			return item2;
		if (item2.getId() == id1)
			return item1;
		return null;
	}

	public static boolean contains(int id1, int id2, Item... items) {
		boolean containsId1 = false;
		boolean containsId2 = false;
		for (Item item : items) {
			if (item.getId() == id1)
				containsId1 = true;
			else if (item.getId() == id2)
				containsId2 = true;
		}
		return containsId1 && containsId2;
	}

	public static void handleInterfaceOnInterface(final Player player,
			InputStream stream) {
		int usedWithId = stream.readShort();
		int toSlot = stream.readUnsignedShortLE128();
		int interfaceId = stream.readUnsignedShort();
		int interfaceComponent = stream.readUnsignedShort();
		int interfaceId2 = stream.readInt() >> 16;
		int fromSlot = stream.readUnsignedShort();
		int itemUsedId = stream.readUnsignedShortLE128();
		if ((interfaceId == 747 || interfaceId == 662)
				&& interfaceId2 == Inventory.INVENTORY_INTERFACE) {
			if (player.getFamiliar() != null) {
				player.getFamiliar().setSpecial(true);
				if (player.getFamiliar().getSpecialAttack() == SpecialAttack.ITEM) {
					if (player.getFamiliar().hasSpecialOn())
						player.getFamiliar().submitSpecial(toSlot);
				}
			}
			return;
		}
		if (interfaceId == Inventory.INVENTORY_INTERFACE
				&& interfaceId == interfaceId2
				&& !player.getInterfaceManager().containsInventoryInter()) {
			if (toSlot >= 28 || fromSlot >= 28 || toSlot == fromSlot)
				return;
			Item usedWith = player.getInventory().getItem(toSlot);
			Item itemUsed = player.getInventory().getItem(fromSlot);
			if (itemUsed == null || usedWith == null
					|| itemUsed.getId() != itemUsedId
					|| usedWith.getId() != usedWithId)
				return;
			if (player.isLocked() || player.getEmotesManager().isDoingEmote())
				return;
			player.stopAll();
			if (!player.getControlerManager().canUseItemOnItem(itemUsed,
					usedWith))
				return;
			
		}
		if (Settings.DEBUG)
			Logger.log("ItemHandler", "ItemOnItem " + usedWithId + ", "
					+ toSlot + ", " + interfaceId + ", " + interfaceComponent
					+ ", " + fromSlot + ", " + itemUsedId);
	}

	public static void handleItemOption3(Player player, int slotId, int itemId,
			Item item) {
		if (player.isLocked() || player.getEmotesManager().isDoingEmote())
			return;
		player.stopAll(false);
		
	}

	public static void handleItemOption4(Player player, int slotId, int itemId,
			Item item) {
		if (Settings.DEBUG)
			System.out.println("Option 4");
	}

	public static void handleItemOption5(Player player, int slotId, int itemId,
			Item item) {
		if (Settings.DEBUG)
			System.out.println("Option 5");
	}

	public static void handleItemOption6(Player player, int slotId, int itemId,
			Item item) {
		if (player.isLocked() || player.getEmotesManager().isDoingEmote())
			return;
		player.stopAll(false);
		if (player.getToolbelt().addItem(slotId, item))
			return;
		
	}

	public static void handleItemOption7(Player player, int slotId, int itemId,
			Item item) {
		if (player.isLocked() || player.getEmotesManager().isDoingEmote())
			return;
		if (!player.getControlerManager().canDropItem(item))
			return;
		player.stopAll(false);
		if (item.getDefinitions().isDestroyItem()) {
			player.getDialogueManager().startDialogue("DestroyItemOption",
					slotId, item);
			return;
		}
		if (player.getPetManager().spawnPet(itemId, true))
			return;
		if (player.isStarter()) {
			player.getPackets()
					.sendGameMessage(
							"You can't drop for the first half hour after creating account.");
			return;
		}
		player.getInventory().deleteItem(slotId, item);
		if (player.getCharges().degradeCompletly(item))
			return;
		World.addGroundItem(item, new WorldTile(player), player, true, 60);
		Logger.globalLog(player.getUsername(), player.getSession().getIP(),
				new String(" has dropped item [ id: " + item.getId()
						+ ", amount: " + item.getAmount() + " ]."));
		player.getPackets().sendSound(2739, 0, 1);
	}

	public static void handleItemOption8(Player player, int slotId, int itemId,
			Item item) {
		player.getInventory().sendExamine(slotId);
	}
}
