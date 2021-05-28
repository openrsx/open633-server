package com.rs.plugin.impl.interfaces;

import com.rs.game.player.Equipment;
import com.rs.game.player.Player;
import com.rs.plugin.RSInterfaceDispatcher;
import com.rs.plugin.listener.RSInterface;
import com.rs.plugin.wrapper.RSInterfaceSignature;

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
	}
}