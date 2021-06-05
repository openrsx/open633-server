package com.rs.plugin.impl.interfaces;

import com.rs.game.player.Player;
import com.rs.net.decoders.WorldPacketsDecoder;
import com.rs.plugin.listener.RSInterface;
import com.rs.plugin.wrapper.RSInterfaceSignature;

@RSInterfaceSignature(interfaceId = { 271, 749 })
public class PrayerInterface implements RSInterface {

	/**
	 * TODO: Fix number decrementing & orb updating
	 */
	@Override
	public void execute(Player player, int interfaceId, int componentId, int packetId, byte slotId, int slotId2)
			throws Exception {
		if (interfaceId == 271) {
			if (componentId == 8 || componentId == 42) {
				player.getPrayer().switchPrayer(slotId);
			} else if (componentId == 43 && player.getPrayer().isUsingQuickPrayer())
				player.getPrayer().switchSettingQuickPrayer();
		}
		if (interfaceId == 749) {
			if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET) // activate
				player.getPrayer().switchQuickPrayers();
			else if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET) // switch
				player.getPrayer()
						.switchSettingQuickPrayer();
		}
	}
}