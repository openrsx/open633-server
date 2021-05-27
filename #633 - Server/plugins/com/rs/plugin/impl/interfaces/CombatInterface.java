package com.rs.plugin.impl.interfaces;

import com.rs.game.player.Player;
import com.rs.plugin.RSInterfaceDispatcher;
import com.rs.plugin.listener.RSInterface;
import com.rs.plugin.wrapper.RSInterfaceSignature;

@RSInterfaceSignature(interfaceId = {884})
public class CombatInterface implements RSInterface {

	@Override
	public void execute(Player player, int interfaceId, int componentId, int packetId, byte slotId, int slotId2) throws Exception {
		if (componentId == 4) {
			int weaponId = player.getEquipment().getWeaponId();
			if (player.getCombatDefinitions().hasInstantSpecial(weaponId)) {
				player.getCombatDefinitions().performInstantSpecial(player, weaponId);
				return;
			}
			RSInterfaceDispatcher.submitSpecialRequest(player);
		} else if (componentId >= 11 && componentId <= 14)
			player.getCombatDefinitions().setAttackStyle(componentId - 11);
		else if (componentId == 15)
			player.getCombatDefinitions().switchAutoRelatie();
	}
}