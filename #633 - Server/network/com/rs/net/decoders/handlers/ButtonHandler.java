package com.rs.net.decoders.handlers;

import java.util.HashMap;
import java.util.TimerTask;

import com.rs.Settings;
import com.rs.cores.CoresManager;
import com.rs.game.item.Item;
import com.rs.game.player.CombatDefinitions;
import com.rs.game.player.Equipment;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.game.player.content.ItemConstants;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.io.InputStream;
import com.rs.utils.Logger;
import com.rs.utils.Utils;

public class ButtonHandler {

	public static void handleButtons(final Player player, InputStream stream,
			final int packetId) {
		int interfaceHash = stream.readInt();
		int interfaceId = interfaceHash >> 16;
		if (Utils.getInterfaceDefinitionsSize() <= interfaceId) {
			// hack, or server error or client error
			// player.getSession().getChannel().close();
			if (Settings.DEBUG) {
				System.out.println("BLOCK 1 " + packetId + "," + interfaceId
						+ "," + (interfaceHash & 0xFFFF));
			}
			return;
		}
		// cant use inter while locked, temporarly
		if (player.isDead() || player.isLocked()
				|| !player.getInterfaceManager().containsInterface(interfaceId)) {
			if (Settings.DEBUG) {
				System.out.println("BLOCK 2 " + packetId + "," + interfaceId
						+ "," + (interfaceHash & 0xFFFF));
			}
			return;
		}
		final int componentId = interfaceHash - (interfaceId << 16);
		if (componentId != 65535
				&& Utils.getInterfaceDefinitionsComponentsSize(interfaceId) <= componentId) {
			// hack, or server error or client error
			// player.getSession().getChannel().close();
			if (Settings.DEBUG) {
				System.out.println("BLOCK 3 " + packetId + "," + interfaceId
						+ "," + componentId);
			}
			return;
		}
		final int slotId2 = stream.readUnsignedShortLE128();// item slot?
		final int slotId = stream.readUnsignedShortLE128();
		if (Settings.DEBUG) {
			System.out.println(packetId + "," + interfaceId + "," + componentId
					+ "," + slotId + "," + slotId2);
		}

		if (Settings.DEBUG)
			Logger.log("ButtonHandler", "InterfaceId " + interfaceId
					+ ", componentId " + componentId + ", slotId " + slotId
					+ ", slotId2 " + slotId2 + ", PacketId: " + packetId);
	}

	public static void sendRemove(Player player, int slotId) {
		if (slotId >= 15)
			return;
		player.stopAll(false, false);
		Item item = player.getEquipment().getItem(slotId);
		if (item == null
				|| !player.getInventory().addItem(item.getId(),
						item.getAmount()))
			return;
		player.getEquipment().getItems().set(slotId, null);
		player.getEquipment().refresh(slotId);
		player.getAppearence().generateAppearenceData();
//		if (Runecrafting.isTiara(item.getId()))
//			player.getVarsManager().sendVar(491, 0);
		if (slotId == 3)
			player.getCombatDefinitions().desecreaseSpecialAttack(0);
	}

	public static boolean sendWear(Player player, int slotId, int itemId) {
		player.stopAll(false, false);
		Item item = player.getInventory().getItem(slotId);
		if (item == null || item.getId() != itemId)
			return false;
		if (item.getDefinitions().isNoted()
				|| !item.getDefinitions().isWearItem(
						player.getAppearence().isMale())) {
			player.getPackets().sendGameMessage("You can't wear that.");
			return true;
		}
		int targetSlot = Equipment.getItemSlot(itemId);
		if (targetSlot == -1) {
			player.getPackets().sendGameMessage("You can't wear that.");
			return true;
		}
		if (!ItemConstants.canWear(item, player))
			return true;
		boolean isTwoHandedWeapon = targetSlot == 3
				&& Equipment.isTwoHandedWeapon(item);
		if (isTwoHandedWeapon && !player.getInventory().hasFreeSlots()
				&& player.getEquipment().hasShield()) {
			player.getPackets().sendGameMessage(
					"Not enough free space in your inventory.");
			return true;
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
					if (hasRequiriments) {
						player.getPackets()
								.sendGameMessage(
										"You are not high enough level to use this item.");
					}
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
			return true;
		if (!player.getControlerManager().canEquip(targetSlot, itemId))
			return false;
		player.stopAll(false, false);
		player.getInventory().deleteItem(slotId, item);
		if (targetSlot == 3) {
			if (isTwoHandedWeapon && player.getEquipment().getItem(5) != null) {
				if (!player.getInventory().addItem(
						player.getEquipment().getItem(5).getId(),
						player.getEquipment().getItem(5).getAmount())) {
					player.getInventory().getItems().set(slotId, item);
					player.getInventory().refresh(slotId);
					return true;
				}
				player.getEquipment().getItems().set(5, null);
			}
		} else if (targetSlot == 5) {
			if (player.getEquipment().getItem(3) != null
					&& Equipment.isTwoHandedWeapon(player.getEquipment()
							.getItem(3))) {
				if (!player.getInventory().addItem(
						player.getEquipment().getItem(3).getId(),
						player.getEquipment().getItem(3).getAmount())) {
					player.getInventory().getItems().set(slotId, item);
					player.getInventory().refresh(slotId);
					return true;
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
				player.getInventory().refresh(slotId);
			} else
				player.getInventory().addItem(
						new Item(player.getEquipment().getItem(targetSlot)
								.getId(), player.getEquipment()
								.getItem(targetSlot).getAmount()));
			player.getEquipment().getItems().set(targetSlot, null);
		}
		if (targetSlot == Equipment.SLOT_AURA)
			player.getAuraManager().removeAura();
		int oldAmt = 0;
		if (player.getEquipment().getItem(targetSlot) != null) {
			oldAmt = player.getEquipment().getItem(targetSlot).getAmount();
		}
		Item item2 = new Item(itemId, oldAmt + item.getAmount());
		player.getEquipment().getItems().set(targetSlot, item2);
		player.getEquipment().refresh(targetSlot,
				targetSlot == 3 ? 5 : targetSlot == 3 ? 0 : 3);
		player.getAppearence().generateAppearenceData();
		player.getPackets().sendSound(2240, 0, 1);
		if (targetSlot == 3)
			player.getCombatDefinitions().desecreaseSpecialAttack(0);
		player.getCharges().wear(targetSlot);
		return true;
	}

	public static boolean sendWear2(Player player, int slotId, int itemId) {
		if (player.hasFinished() || player.isDead())
			return false;
		player.stopAll(false, false);
		Item item = player.getInventory().getItem(slotId);
		if (item == null || item.getId() != itemId)
			return false;
		if (item.getDefinitions().isNoted()
				|| !item.getDefinitions().isWearItem(
						player.getAppearence().isMale()) && itemId != 4084) {
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
		if (!player.getControlerManager().canEquip(targetSlot, itemId))
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
		if (targetSlot == Equipment.SLOT_AURA)
			player.getAuraManager().removeAura();
		int oldAmt = 0;
		if (player.getEquipment().getItem(targetSlot) != null) {
			oldAmt = player.getEquipment().getItem(targetSlot).getAmount();
		}
		Item item2 = new Item(itemId, oldAmt + item.getAmount());
		player.getEquipment().getItems().set(targetSlot, item2);
		player.getEquipment().refresh(targetSlot,
				targetSlot == 3 ? 5 : targetSlot == 3 ? 0 : 3);
		if (targetSlot == 3)
			player.getCombatDefinitions().desecreaseSpecialAttack(0);
		player.getCharges().wear(targetSlot);
		return true;
	}

	public static void submitSpecialRequest(final Player player) {
		CoresManager.fastExecutor.schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					WorldTasksManager.schedule(new WorldTask() {

						@Override
						public void run() {
							if (player.isDead())
								return;
							player.getCombatDefinitions()
									.switchUsingSpecialAttack();
						}
					}, 0);
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, 300);
	}

	public static void sendWear(Player player, int[] slotIds) {
		if (player.hasFinished() || player.isDead())
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
			player.getAppearence().generateAppearenceData();
			player.getPackets().sendSound(2240, 0, 1);
		}
	}

	public static void openItemsKeptOnDeath(Player player) {
		player.getInterfaceManager().sendInterface(17);
		sendItemsKeptOnDeath(player, false);
	}

	public static void sendItemsKeptOnDeath(Player player, boolean wilderness) {
//		boolean skulled = player.hasSkull();
//		Integer[][] slots = GraveStone.getItemSlotsKeptOnDeath(player,
//				wilderness, skulled, player.getPrayer().isProtectingItem());
//		Item[][] items = GraveStone.getItemsKeptOnDeath(player, slots);
//		long riskedWealth = 0;
//		long carriedWealth = 0;
////		for (Item item : items[1])
////			carriedWealth = riskedWealth += GrandExchange
////					.getPrice(item.getId()) * item.getAmount();
////		for (Item item : items[0])
////			carriedWealth += GrandExchange.getPrice(item.getId())
////					* item.getAmount();
//		if (slots[0].length > 0) {
//			for (int i = 0; i < slots[0].length; i++)
//				player.getVarsManager().sendVarBit(9222 + i, slots[0][i]);
//			player.getVarsManager().sendVarBit(9227, slots[0].length);
//		} else {
//			player.getVarsManager().sendVarBit(9222, -1);
//			player.getVarsManager().sendVarBit(9227, 1);
//		}
//		player.getVarsManager().sendVarBit(9226, wilderness ? 1 : 0);
//		player.getVarsManager().sendVarBit(9229, skulled ? 1 : 0);
//		StringBuffer text = new StringBuffer();
//		text.append("The number of items kept on").append("<br>")
//				.append("death is normally 3.").append("<br>").append("<br>")
//				.append("<br>");
//		if (wilderness) {
//			text.append("Your gravestone will not").append("<br>")
//					.append("appear.");
//		} else {
//			int time = GraveStone.getMaximumTicks(player.getGraveStone());
//			int seconds = (int) (time * 0.6);
//			int minutes = seconds / 60;
//			seconds -= minutes * 60;
//
//			text.append("Gravestone:")
//					.append("<br>")
//					.append(ClientScriptMap.getMap(1099).getStringValue(
//							player.getGraveStone()))
//					.append("<br>")
//					.append("<br>")
//					.append("Initial duration:")
//					.append("<br>")
//					.append(minutes + ":" + (seconds < 10 ? "0" : "") + seconds)
//					.append("<br>");
//		}
//		text.append("<br>")
//				.append("<br>")
//				.append("Carried wealth:")
//				.append("<br>")
//				.append(carriedWealth > Integer.MAX_VALUE ? "Too high!" : Utils
//						.getFormattedNumber((int) carriedWealth))
//				.append("<br>")
//				.append("<br>")
//				.append("Risked wealth:")
//				.append("<br>")
//				.append(riskedWealth > Integer.MAX_VALUE ? "Too high!" : Utils
//						.getFormattedNumber((int) riskedWealth)).append("<br>")
//				.append("<br>");
//		if (wilderness) {
//			text.append("Your hub will be set to:").append("<br>")
//					.append("Edgeville.");
//		} else {
//			text.append("Current hub: "
//					+ ClientScriptMap.getMap(3792).getStringValue(
//							DeathEvent.getCurrentHub(player)));
//		}
//		player.getPackets().sendGlobalString(352, text.toString());
	}

	public static void openEquipmentBonuses(final Player player, boolean banking) {
		player.stopAll();
		player.getInterfaceManager().sendInventoryInterface(670);
		player.getInterfaceManager().sendInterface(667);
		player.getVarsManager().sendVarBit(4894, banking ? 1 : 0);
		player.getPackets().sendRunScript(787, 1);
		player.getPackets().sendItems(93, player.getInventory().getItems());
		player.getPackets().sendInterSetItemsOptionsScript(670, 0, 93, 4, 7,
				"Equip", "Compare", "Stats", "Examine");
		player.getPackets().sendUnlockIComponentOptionSlots(670, 0, 0, 27, 0,
				1, 2, 3);
		player.getPackets().sendIComponentSettings(667, 7, 0, 14, 1538);
		refreshEquipBonuses(player);
		if (banking) {
			player.getTemporaryAttributtes().put("Banking", Boolean.TRUE);
			player.setCloseInterfacesEvent(new Runnable() {
				@Override
				public void run() {
					player.getTemporaryAttributtes().remove("Banking");
					player.getVarsManager().sendVarBit(4894, 0);
				}
			});
		}
	}

	private static String equipmentBonusText(Player player, String msg,
			int bonusId) {
		int bonus = player.getCombatDefinitions().getBonuses()[bonusId];
		if (bonus < 0)
			return msg.replace("+", "") + "" + bonus;
		return msg + "" + bonus; // only use if it requires it to be negative.

	}

	public static void refreshEquipBonuses(Player player) {

		player.getPackets().sendIComponentText(667, 31,
				equipmentBonusText(player, "Slash +", 0));
		player.getPackets().sendIComponentText(667, 32,
				equipmentBonusText(player, "Slash: +", 1));
		player.getPackets().sendIComponentText(667, 33,
				equipmentBonusText(player, "Crush: +", 2));
		player.getPackets().sendIComponentText(667, 34,
				equipmentBonusText(player, "Magic: +", 3));
		player.getPackets().sendIComponentText(667, 35,
				equipmentBonusText(player, "Range: +", 4));
		player.getPackets().sendIComponentText(667, 36,
				equipmentBonusText(player, "Stab: +", 5));
		player.getPackets().sendIComponentText(667, 37,
				equipmentBonusText(player, "Slash: +", 6));
		player.getPackets().sendIComponentText(667, 38,
				equipmentBonusText(player, "Crush: +", 7));
		player.getPackets().sendIComponentText(667, 39,
				equipmentBonusText(player, "Magic: +", 8));
		player.getPackets().sendIComponentText(667, 40,
				equipmentBonusText(player, "Range: +", 9));
		player.getPackets().sendIComponentText(667, 41,
				equipmentBonusText(player, "Summoning: +", 10));
		player.getPackets()
				.sendIComponentText(
						667,
						42,
						"Absorb Melee: +"
								+ player.getCombatDefinitions().getBonuses()[CombatDefinitions.ABSORB_MELEE]
								+ "%");
		player.getPackets()
				.sendIComponentText(
						667,
						43,
						"Absorb Magic: +"
								+ player.getCombatDefinitions().getBonuses()[CombatDefinitions.ABSORB_MAGIC]
								+ "%");
		player.getPackets()
				.sendIComponentText(
						667,
						44,
						"Absorb Ranged: +"
								+ player.getCombatDefinitions().getBonuses()[CombatDefinitions.ABSORB_RANGE]
								+ "%");
		player.getPackets().sendIComponentText(667, 45,
				"Strength: " + player.getCombatDefinitions().getBonuses()[14]);
		player.getPackets()
				.sendIComponentText(
						667,
						46,
						"Ranged Str: "
								+ player.getCombatDefinitions().getBonuses()[15]);
		player.getPackets().sendIComponentText(667, 47,
				equipmentBonusText(player, "Prayer: +", 16));
		player.getPackets().sendIComponentText(
				667,
				48,
				"Magic Damage: +"
						+ player.getCombatDefinitions().getBonuses()[17] + "%");
	}

	public static void openSkillGuide(Player player) {
		player.getInterfaceManager().setScreenInterface(317, 1218);
		player.getInterfaceManager().setInterface(false, 1218, 1, 1217); // seems
		// to
		// fix
	}
}
