package com.rs.net.packets.outgoing.impl;

import com.rs.GameConstants;
import com.rs.game.item.Item;
import com.rs.game.npc.familiar.Familiar.SpecialAttack;
import com.rs.game.player.Inventory;
import com.rs.game.player.Player;
import com.rs.io.InputStream;
import com.rs.net.packets.outgoing.OutgoingPacket;
import com.rs.net.packets.outgoing.OutgoingPacketSignature;
import com.rs.utilities.Logger;

@OutgoingPacketSignature(packetId = 33, description = "Represents an Interface being used on another Interface")
public class InterfaceOnInterfacePacket implements OutgoingPacket {

	@Override
	public void execute(Player player, InputStream stream) {
		int usedWithId = stream.readShort();
		int toSlot = stream.readUnsignedShortLE128();
		int interfaceId = stream.readUnsignedShort();
		int interfaceComponent = stream.readUnsignedShort();
		int interfaceId2 = stream.readInt() >> 16;
		int fromSlot = stream.readUnsignedShort();
		int itemUsedId = stream.readUnsignedShortLE128();
		if ((interfaceId == 747 || interfaceId == 662) && interfaceId2 == Inventory.INVENTORY_INTERFACE) {
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
			if (toSlot >= 28 || fromSlot >= 28 || toSlot == fromSlot)
				return;
			Item usedWith = player.getInventory().getItem(toSlot);
			Item itemUsed = player.getInventory().getItem(fromSlot);
			if (itemUsed == null || usedWith == null || itemUsed.getId() != itemUsedId
					|| usedWith.getId() != usedWithId)
				return;
			if (player.getMovement().isLocked()/* || player.getEmotesManager().isDoingEmote() */)
				return;
			player.stopAll();
			if (!player.getControllerManager().canUseItemOnItem(itemUsed, usedWith))
				return;

		}
		if (GameConstants.DEBUG)
			Logger.log("ItemHandler", "ItemOnItem " + usedWithId + ", " + toSlot + ", " + interfaceId + ", "
					+ interfaceComponent + ", " + fromSlot + ", " + itemUsedId);
	}
}