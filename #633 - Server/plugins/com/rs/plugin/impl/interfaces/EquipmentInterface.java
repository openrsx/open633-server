package com.rs.plugin.impl.interfaces;

import com.rs.game.player.Player;
import com.rs.plugin.RSInterfaceDispatcher;
import com.rs.plugin.listener.RSInterface;
import com.rs.plugin.wrapper.RSInterfaceSignature;

@RSInterfaceSignature(interfaceId = { 387 })
public class EquipmentInterface implements RSInterface {

	@Override
	public void execute(Player player, int interfaceId, int componentId, int packetId, byte slotId, int slotId2)
			throws Exception {
		if (player.getInterfaceManager().containsInventoryInter())
			return;
		player.stopAll();

		if (componentId == 42) {
			if (player.getInterfaceManager().containsScreenInter() || player.isLocked()) {
				player.getPackets()
						.sendGameMessage("Please finish what you're doing before opening the price checker.");
				return;
			}
			player.stopAll();
			player.getPriceCheckManager().openPriceCheck();

		} else if (componentId == 39) {
			RSInterfaceDispatcher.openEquipmentBonuses(player, false);
		}
//		else if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET)
//				RSInterfaceDispatcher.sendRemove(player, Equipment.SLOT_AMULET);
//			else if (packetId == WorldPacketsDecoder.ACTION_BUTTON8_PACKET)
//				player.getEquipment().sendExamine(Equipment.SLOT_AMULET);
//		
//		 else if (componentId == 17) {
//			if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET) {
//				int weaponId = player.getEquipment().getWeaponId();
//				if (weaponId == 20171 || weaponId == 20173) { // zaryte
//																// bow
//					player.getCharges()
//							.checkCharges(
//									player.getEquipment().getItem(3)
//											.getName()
//											+ ": has ## shots remaining.",
//									weaponId);
//				} else if (weaponId == 15484)
//					player.getInterfaceManager().gazeOrbOfOculus();
////				else if (weaponId == 14057) // broomstick
////					SorceressGarden.teleportToSocreressGarden(player, true);
//			} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET)
//				RSInterfaceDispatcher.sendRemove(player, Equipment.SLOT_WEAPON);
//			else if (packetId == WorldPacketsDecoder.ACTION_BUTTON8_PACKET)
//				player.getEquipment().sendExamine(Equipment.SLOT_WEAPON);
//		} else if (componentId == 20) {
//			if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET)
//				RSInterfaceDispatcher.sendRemove(player, Equipment.SLOT_CHEST);
//			else if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET) {
//				int chestId = player.getEquipment().getChestId();
//				if (ItemConstants.canCheckCharges(chestId))
//					player.getCharges().checkPercentage(
//							player.getEquipment()
//									.getItem(Equipment.SLOT_CHEST)
//									.getName()
//									+ ": has ##% charge remaining.",
//							chestId, false);
//			} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON8_PACKET)
//				player.getEquipment().sendExamine(Equipment.SLOT_CHEST);
//		} else if (componentId == 23) {
//			if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET)
//				RSInterfaceDispatcher.sendRemove(player, Equipment.SLOT_SHIELD);
//			else if (packetId == WorldPacketsDecoder.ACTION_BUTTON8_PACKET)
//				player.getEquipment().sendExamine(Equipment.SLOT_SHIELD);
//
//		} else if (componentId == 26) {
//			if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET)
//				RSInterfaceDispatcher.sendRemove(player, Equipment.SLOT_LEGS);
//			else if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET) {
//				int legsId = player.getEquipment().getLegsId();
//				if (ItemConstants.canCheckCharges(legsId))
//					player.getCharges().checkPercentage(
//							player.getEquipment()
//									.getItem(Equipment.SLOT_LEGS).getName()
//									+ ": has ##% charge remaining.",
//							legsId, false);
//			} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON8_PACKET)
//				player.getEquipment().sendExamine(Equipment.SLOT_LEGS);
//		} else if (componentId == 29) {
//			if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET)
//				RSInterfaceDispatcher.sendRemove(player, Equipment.SLOT_HANDS);
//			else if (packetId == WorldPacketsDecoder.ACTION_BUTTON8_PACKET)
//				player.getEquipment().sendExamine(Equipment.SLOT_HANDS);
//		} else if (componentId == 32) {
//			if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET)
//				RSInterfaceDispatcher.sendRemove(player, Equipment.SLOT_FEET);
//			else if (packetId == WorldPacketsDecoder.ACTION_BUTTON8_PACKET)
//				player.getEquipment().sendExamine(Equipment.SLOT_FEET);
//		} else if (componentId == 35) {
//			if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET)
//				RSInterfaceDispatcher.sendRemove(player, Equipment.SLOT_RING);
//			else if (packetId == WorldPacketsDecoder.ACTION_BUTTON8_PACKET)
//				player.getEquipment().sendExamine(Equipment.SLOT_RING);
//		} else if (componentId == 38) {
//			if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET)
//				RSInterfaceDispatcher.sendRemove(player, Equipment.SLOT_ARROWS);
//			else if (packetId == WorldPacketsDecoder.ACTION_BUTTON8_PACKET)
//				player.getEquipment().sendExamine(Equipment.SLOT_ARROWS);
//		} else if (componentId == 45) {
//			player.stopAll();
//			RSInterfaceDispatcher.openItemsKeptOnDeath(player);
//		} else if (componentId == 41) {
//			player.stopAll();
//			player.getInterfaceManager().sendInterface(1178);
//		} else if (componentId == 39) {
//			RSInterfaceDispatcher.openEquipmentBonuses(player, false);
//		}
	}
}