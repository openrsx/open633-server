package com.rs.net.encoders.other;

import java.util.List;

import com.rs.game.World;
import com.rs.game.player.Player;

import lombok.Getter;

public class PublicChatMessage extends ChatMessage {

	@Getter
	private int effects;

	public PublicChatMessage(String message, int effects) {
		super(message);
		this.effects = effects;
	}

	public void sendPublicChatMessage(Player player, PublicChatMessage message) {
		for (int regionId : player.getMapRegionsIds()) {
			List<Integer> playersIndexes = World.getRegion(regionId)
					.getPlayerIndexes();
			if (playersIndexes == null)
				continue;
			for (Integer playerIndex : playersIndexes) {
				Player p = World.getPlayers().get(playerIndex);
				if (p == null
						|| !p.isStarted()
						|| p.isFinished()
						|| p.getLocalPlayerUpdate().getLocalPlayers()[player.getIndex()] == null)
					continue;
				p.getPackets().sendPublicMessage(player, message);
			}
		}
	}
}