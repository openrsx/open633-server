package com.rs.plugin.impl.interfaces;

import com.rs.game.player.Player;
import com.rs.game.player.content.PlayerLook;
import com.rs.plugin.listener.RSInterface;
import com.rs.plugin.wrapper.RSInterfaceSignature;

@RSInterfaceSignature(interfaceId = 1028)
public class CharacterCustomizingInterface implements RSInterface {

	@Override
	public void execute(Player player, int interfaceId, int componentId, int packetId, byte slotId, int slotId2)
			throws Exception {
		if (componentId == 117 && packetId == 11)
			PlayerLook.handleCharacterCustomizingButtons(player, componentId,
					slotId);
	}
}