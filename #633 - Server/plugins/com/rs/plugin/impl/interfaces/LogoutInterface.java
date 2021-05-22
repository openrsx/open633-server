package com.rs.plugin.impl.interfaces;

import com.rs.game.player.Player;
import com.rs.plugin.listener.RSInterface;
import com.rs.plugin.wrapper.RSInterfaceSignature;

@RSInterfaceSignature(interfaceId = {182})
public class LogoutInterface implements RSInterface {

	@Override
	public void execute(Player player, int interfaceId, int componentId, int packetId, byte slotId, int slotId2) throws Exception {
		if (player.getInterfaceManager().containsInventoryInter()){
			System.out.println("failed cont invy inter");
			return;
		}
		if (player.hasFinished())
			return;
		player.logout(componentId != 10);
	}
}