package com.rs.plugin.impl.interfaces;

import com.rs.game.item.Item;
import com.rs.game.player.CombatDefinitions;
import com.rs.game.player.Equipment;
import com.rs.game.player.Player;
import com.rs.plugin.RSInterfaceDispatcher;
import com.rs.plugin.listener.RSInterface;
import com.rs.plugin.wrapper.RSInterfaceSignature;
import com.rs.utils.ItemBonuses;

@RSInterfaceSignature(interfaceId = { 667 })
public class EquipmentBonusesInterface implements RSInterface {

	@Override
	public void execute(Player player, int interfaceId, int componentId, int packetId, byte slotId, int slotId2) throws Exception {
		if (packetId == 11) {
			if (slotId == 3)
				RSInterfaceDispatcher.sendRemove(player, Equipment.SLOT_WEAPON);
			if (slotId == 0)
				RSInterfaceDispatcher.sendRemove(player, Equipment.SLOT_HAT);
			if (slotId == 1)
				RSInterfaceDispatcher.sendRemove(player, Equipment.SLOT_CAPE);
			if (slotId == 2)
				RSInterfaceDispatcher.sendRemove(player, Equipment.SLOT_AMULET);
			if (slotId == 13)
				RSInterfaceDispatcher.sendRemove(player, Equipment.SLOT_ARROWS);
			if (slotId == 4)
				RSInterfaceDispatcher.sendRemove(player, Equipment.SLOT_CHEST);
			if (slotId == 7)
				RSInterfaceDispatcher.sendRemove(player, Equipment.SLOT_LEGS);
			if (slotId == 9)
				RSInterfaceDispatcher.sendRemove(player, Equipment.SLOT_HANDS);
			if (slotId == 10)
				RSInterfaceDispatcher.sendRemove(player, Equipment.SLOT_FEET);
			if (slotId == 12)
				RSInterfaceDispatcher.sendRemove(player, Equipment.SLOT_RING);
			if (slotId == 5)
				RSInterfaceDispatcher.sendRemove(player, Equipment.SLOT_SHIELD);
		}
		if (packetId == 74 && componentId == 7) {
			if (slotId >= player.getInventory().getItemsContainerSize())
				return;
			Item item = player.getEquipment().getItem(slotId);
			if (item == null)
				return;
			System.out.println("?");
			sendItemStats(player, item);	
		}
	}
	
	public static void sendItemStats(final Player player, Item item) {
		StringBuilder b = new StringBuilder();
		if (item.getId() == 772)
			return;
		boolean hasBonuses = ItemBonuses.getItemBonuses(item.getId()) != null;
		for (int i = 0; i < 17; i++) {
			int bonus = hasBonuses ? ItemBonuses.getItemBonuses(item.getId())[i] : 0;
			String label = CombatDefinitions.BONUS_LABELS[i];
			String sign = bonus > 0 ? "+" : "";
			if (bonus == 16) {
				continue;
			}
			b.append(label + ": " + (sign + bonus) + ((label == "Magic Damage" || label == "Absorb Melee"
					|| label == "Absorb Magic" || label == "Absorb Ranged") ? "%" : "") + "<br>");
		}
		player.getPackets().sendIComponentText(667, 63, "Stats for " + item.getName());
		player.getPackets().sendHideIComponent(667, 51, false);
		
		/**
		 * TODO: Fix string not displaying, done.
		 */
		player.getPackets().sendGlobalString(324, b.toString());
		
//		player.setCloseInterfacesEvent(new Runnable() {
//			@Override
//			public void run() {
//				player.getPackets().sendGlobalString(321, "");
//				player.getPackets().sendGlobalString(324, "");
//				player.getPackets().sendHideIComponent(667, 51, true);
//			}
//		});
	}
}