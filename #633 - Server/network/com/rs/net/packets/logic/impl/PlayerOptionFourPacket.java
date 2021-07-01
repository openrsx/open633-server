package com.rs.net.packets.logic.impl;

import com.rs.game.World;
import com.rs.game.player.Player;
import com.rs.game.player.controller.ControllerHandler;
import com.rs.game.route.RouteEvent;
import com.rs.io.InputStream;
import com.rs.net.packets.logic.LogicPacket;
import com.rs.net.packets.logic.LogicPacketSignature;

@LogicPacketSignature(packetId = 51, packetSize = 3, description = "The Fourth menu option for a Player")
public class PlayerOptionFourPacket implements LogicPacket {

	@Override
	public void execute(Player player, InputStream stream) {
		int playerIndex = stream.readUnsignedShortLE128();
		boolean forceRun = stream.readUnsignedByte() == 1;
		final Player p2 = World.getPlayers().get(playerIndex);
		if (p2 == null || p2 == player || p2.isDead() || p2.isFinished()
				|| !player.getMapRegionsIds().contains(p2.getRegionId()))
			return;
		if (player.getMovement().isLocked())
			return;
		if (forceRun)
			player.setRun(forceRun);
		player.setRouteEvent(new RouteEvent(p2, () -> {
			if (!ControllerHandler.execute(player, controller -> controller.canPlayerOption4(p2))) {
				return;
			}
			player.getMovement().stopAll(player);
			if (player.isCantTrade() || player.getCurrentController().isPresent()) {
				player.getPackets().sendGameMessage("You are busy.");
				return;
			}
			if (p2.getInterfaceManager().containsScreenInter() || p2.isCantTrade()
					|| p2.getCurrentController().isPresent()

					|| p2.getMovement().isLocked()) {
				player.getPackets().sendGameMessage("The other player is busy.");
				return;
			}
			if (!p2.withinDistance(player, 14)) {
				player.getPackets().sendGameMessage("Unable to find target " + p2.getDisplayName());
				return;
			}
			if (p2.getTemporaryAttributes().get("TradeTarget") == player) {
				p2.getTemporaryAttributes().remove("TradeTarget");
				player.getTrade().openTrade(p2);
				p2.getTrade().openTrade(player);
				return;
			}
			player.getTemporaryAttributes().put("TradeTarget", p2);
			player.getPackets().sendGameMessage("Sending " + p2.getDisplayName() + " a request...");
			p2.getPackets().sendTradeRequestMessage(player);
		}));
	}
}