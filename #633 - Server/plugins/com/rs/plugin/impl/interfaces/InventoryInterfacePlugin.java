package com.rs.plugin.impl.interfaces;

import java.util.HashMap;
import java.util.List;

import com.rs.GameConstants;
import com.rs.cache.io.InputStream;
import com.rs.cores.WorldThread;
import com.rs.game.World;
import com.rs.game.WorldTile;
import com.rs.game.item.FloorItem;
import com.rs.game.item.Item;
import com.rs.game.item.ItemConstants;
import com.rs.game.npc.NPC;
import com.rs.game.npc.familiar.Familiar.SpecialAttack;
import com.rs.game.npc.others.Pet;
import com.rs.game.player.Equipment;
import com.rs.game.player.Inventory;
import com.rs.game.player.Player;
import com.rs.game.player.content.Foods;
import com.rs.game.player.content.Pots;
import com.rs.game.route.CoordsEvent;
import com.rs.game.task.Task;
import com.rs.net.decoders.WorldPacketsDecoder;
import com.rs.plugin.InventoryDispatcher;
import com.rs.plugin.RSInterfaceDispatcher;
import com.rs.plugin.listener.RSInterface;
import com.rs.plugin.wrapper.RSInterfaceSignature;
import com.rs.utilities.Logger;
import com.rs.utilities.Utils;

import skills.Skills;

@RSInterfaceSignature(interfaceId = {149})
public class InventoryInterfacePlugin implements RSInterface {

	@SuppressWarnings("unused")
	@Override
	public void execute(Player player, int interfaceId, int componentId, int packetId, byte slotId, int slotId2) throws Exception {
		if (componentId == 0) {
			if (slotId > 27 || player.getInterfaceManager().containsInventoryInter())
				return;
			Item item = player.getInventory().getItem(slotId);
			if (item == null || item.getId() != slotId2)
				return;
			
			switch(packetId) {
			case WorldPacketsDecoder.ACTION_BUTTON1_PACKET:
				long time = Utils.currentTimeMillis();
//				if ( player.getLockDelay() >= time || player.getEmotesManager().getNextEmoteEnd() >= time)
//					return;
				player.stopAll(false);
				if (Foods.eat(player, item, slotId))
					return;
				if (Pots.pot(player, item, slotId))
					return;
				InventoryDispatcher.execute(player, item, 1);
				break;
			case WorldPacketsDecoder.ACTION_BUTTON2_PACKET:
				if (player.isDisableEquip())
					return;
				long passedTime = Utils.currentTimeMillis() - WorldThread.WORLD_CYCLE;
				if (player.getSwitchItemCache().isEmpty()) {
					player.getSwitchItemCache().add(slotId);
					World.get().submit(new Task(passedTime >= 600 ? 0 : passedTime > 330 ? 1 : 0) {
						
						@Override
						protected void execute() {
							List<Byte> slots = player.getSwitchItemCache();
							int[] slot = new int[slots.size()];
							for (int i = 0; i < slot.length; i++)
								slot[i] = slots.get(i);
							player.getSwitchItemCache().clear();
							RSInterfaceDispatcher.sendWear(player, slot);
							player.stopAll(false, true, false);
							this.cancel();
						}
					});
				} else if (!player.getSwitchItemCache().contains(slotId)) {
					player.getSwitchItemCache().add(slotId);
				}
				InventoryDispatcher.execute(player, item, 2);
				break;
			case WorldPacketsDecoder.ACTION_BUTTON3_PACKET:
				InventoryDispatcher.execute(player, item, 3);
				break;
			case WorldPacketsDecoder.ACTION_BUTTON4_PACKET:
				InventoryDispatcher.execute(player, item, 4);
				break;
			case WorldPacketsDecoder.ACTION_BUTTON5_PACKET:
				InventoryDispatcher.execute(player, item, 5);
				break;
			case WorldPacketsDecoder.ACTION_BUTTON6_PACKET:
				InventoryDispatcher.execute(player, item, 6);
				break;
			case WorldPacketsDecoder.ACTION_BUTTON7_PACKET:
				long dropTime = Utils.currentTimeMillis();
//				if (player.getLockDelay() >= dropTime || player.getEmotesManager().getNextEmoteEnd() >= dropTime)
//					return;
				if (!player.getControllerManager().canDropItem(item))
					return;
				player.stopAll(false);
				
				if (item.getDefinitions().isOverSized()) {
					player.getPackets().sendGameMessage("The item appears to be oversized.");
					player.getInventory().deleteItem(item);
					return;
				}

//				if (item.getDefinitions().isDestroyItem()) {
//					DestroyItemD.INSTANCE.sendChatInterface(player, item);
//					return;
//				}
				if (player.getPetManager().spawnPet(item.getId(), true)) {
					return;
				}
				InventoryDispatcher.execute(player, item, 7);
				player.getInventory().deleteItem(slotId, item);
				if (player.getDetails().getCharges().degradeCompletly(item))
					return;
				FloorItem.createGroundItem(item, new WorldTile(player), player, false, 180, true);
				player.getPackets().sendSound(2739, 0, 1);
				break;
			case 81:
				player.getInventory().sendExamine(slotId);
				InventoryDispatcher.execute(player, item, 8);
				break;
			}
		}
	}
	
	public static void handleItemOnItem(final Player player, InputStream stream) {
		int toSlot = stream.readShortLE128();
		int fromSlot = stream.readShortLE();
		int itemUsedWithId = stream.readShortLE128();
		int interfaceId2 = stream.readIntLE() >> 16;
		int interfaceId = stream.readIntV2() >> 16;
		int itemUsedId = stream.readShortLE();
		
		if (GameConstants.DEBUG)
			System.out.println(String.format("fromInter: %s, toInter: %s, fromSlot: %s, toSlot %s, item1: %s, item2: %s", interfaceId, interfaceId2, fromSlot, toSlot, itemUsedId, itemUsedWithId));
		
		//fromInter: 44498944, toInter: 44498944, fromSlot: 11694, toSlot 0, item1: 14484, item2: 8
		
		if ((interfaceId2 == 747 || interfaceId2 == 662) && interfaceId == Inventory.INVENTORY_INTERFACE) {
			if (player.getFamiliar() != null) {
				player.getFamiliar().setSpecial(true);
				if (player.getFamiliar().getSpecialAttack() == SpecialAttack.ITEM) {
					if (player.getFamiliar().hasSpecialOn())
						player.getFamiliar().submitSpecial(toSlot);
				}
			}
			return;
		}
		if (interfaceId == Inventory.INVENTORY_INTERFACE && interfaceId == interfaceId2
				&& !player.getInterfaceManager().containsInventoryInter()) {
			if (toSlot >= 28 || fromSlot >= 28)
				return;
			Item usedWith = player.getInventory().getItem(toSlot);
			Item itemUsed = player.getInventory().getItem(fromSlot);
			if (itemUsed == null || usedWith == null || itemUsed.getId() != itemUsedId
					|| usedWith.getId() != itemUsedWithId)
				return;
			player.stopAll();
			
			if (GameConstants.DEBUG)
				Logger.log("ItemHandler", "Used:" + itemUsed.getId() + ", With:" + usedWith.getId());
		}
	}

	public static void handleItemOnNPC(final Player player, final NPC npc, final Item item) {
		if (item == null) {
			return;
		}
		player.setCoordsEvent(new CoordsEvent(npc, new Runnable() {
			@Override
			public void run() {
				if (!player.getInventory().containsItem(item.getId(), item.getAmount())) {
					return;
				}
				if (npc instanceof Pet) {
					player.faceEntity(npc);
					player.getPetManager().eat(item.getId(), (Pet) npc);
					return;
				}
			}
		}, npc.getSize()));
	}
	public static void sendWear(Player player, int[] slotIds) {
		if (player.isFinished() || player.isDead())
			return;
		boolean worn = false;
		Item[] copy = player.getInventory().getItems().getItemsCopy();
		for (int slotId : slotIds) {
			Item item = player.getInventory().getItem(slotId);
			if (item == null)
				continue;
			if (sendWear2(player, slotId, item.getId()))
				worn = true;
		}
		player.getInventory().refreshItems(copy);
		if (worn) {
			player.getAppearance().generateAppearenceData();
			player.getPackets().sendSound(2240, 0, 1);
		}
	}
	
	public static boolean sendWear2(Player player, int slotId, int itemId) {
		if (player.isFinished() || player.isDead())
			return false;
		player.stopAll(false, false);
		Item item = player.getInventory().getItem(slotId);
		if (item == null || item.getId() != itemId)
			return false;
		if (item.getDefinitions().isNoted()
				|| !item.getDefinitions().isWearItem(
						player.getAppearance().isMale()) && itemId != 4084) {
			player.getPackets().sendGameMessage("You can't wear that.");
			return false;
		}
		int targetSlot = Equipment.getItemSlot(itemId);
		if (itemId == 4084)
			targetSlot = 3;
		if (targetSlot == -1) {
			player.getPackets().sendGameMessage("You can't wear that.");
			return false;
		}
		if (!ItemConstants.canWear(item, player))
			return false;
		boolean isTwoHandedWeapon = targetSlot == 3
				&& Equipment.isTwoHandedWeapon(item);
		if (isTwoHandedWeapon && !player.getInventory().hasFreeSlots()
				&& player.getEquipment().hasShield()) {
			player.getPackets().sendGameMessage(
					"Not enough free space in your inventory.");
			return false;
		}
		HashMap<Integer, Integer> requiriments = item.getDefinitions()
				.getWearingSkillRequiriments();
		boolean hasRequiriments = true;
		if (requiriments != null) {
			for (int skillId : requiriments.keySet()) {
				if (skillId > 24 || skillId < 0)
					continue;
				int level = requiriments.get(skillId);
				if (level < 0 || level > 120)
					continue;
				if (player.getSkills().getLevelForXp(skillId) < level) {
					if (hasRequiriments)
						player.getPackets()
								.sendGameMessage(
										"You are not high enough level to use this item.");
					hasRequiriments = false;
					String name = Skills.SKILL_NAME[skillId].toLowerCase();
					player.getPackets().sendGameMessage(
							"You need to have a"
									+ (name.startsWith("a") ? "n" : "") + " "
									+ name + " level of " + level + ".");
				}

			}
		}
		if (!hasRequiriments)
			return false;
		if (!player.getControllerManager().canEquip(targetSlot, itemId))
			return false;
		player.getInventory().getItems().remove(slotId, item);
		if (targetSlot == 3) {
			if (isTwoHandedWeapon && player.getEquipment().getItem(5) != null) {
				if (!player.getInventory().getItems()
						.add(player.getEquipment().getItem(5))) {
					player.getInventory().getItems().set(slotId, item);
					return false;
				}
				player.getEquipment().getItems().set(5, null);
			}
		} else if (targetSlot == 5) {
			if (player.getEquipment().getItem(3) != null
					&& Equipment.isTwoHandedWeapon(player.getEquipment()
							.getItem(3))) {
				if (!player.getInventory().getItems()
						.add(player.getEquipment().getItem(3))) {
					player.getInventory().getItems().set(slotId, item);
					return false;
				}
				player.getEquipment().getItems().set(3, null);
			}

		}
		if (player.getEquipment().getItem(targetSlot) != null
				&& (itemId != player.getEquipment().getItem(targetSlot).getId() || !item
						.getDefinitions().isStackable())) {
			if (player.getInventory().getItems().get(slotId) == null) {
				player.getInventory()
						.getItems()
						.set(slotId,
								new Item(player.getEquipment()
										.getItem(targetSlot).getId(), player
										.getEquipment().getItem(targetSlot)
										.getAmount()));
			} else
				player.getInventory()
						.getItems()
						.add(new Item(player.getEquipment().getItem(targetSlot)
								.getId(), player.getEquipment()
								.getItem(targetSlot).getAmount()));
			player.getEquipment().getItems().set(targetSlot, null);
		}
		int oldAmt = 0;
		if (player.getEquipment().getItem(targetSlot) != null) {
			oldAmt = player.getEquipment().getItem(targetSlot).getAmount();
		}
		Item item2 = new Item(itemId, oldAmt + item.getAmount());
		player.getEquipment().getItems().set(targetSlot, item2);
		player.getEquipment().refresh(targetSlot,
				targetSlot == 3 ? 5 : targetSlot == 3 ? 0 : 3);
		if (targetSlot == 3)
			player.getCombatDefinitions().decreaseSpecialAttack(0);
//		player.getDetails().getCharges().wear(targetSlot);
		return true;
	}
}