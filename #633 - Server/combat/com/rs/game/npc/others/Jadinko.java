package com.rs.game.npc.others;

import java.util.Optional;

import com.rs.game.Entity;
import com.rs.game.map.WorldTile;
import com.rs.game.npc.NPC;

public class Jadinko extends NPC {

	public Jadinko(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea) {
		super((short) id, tile, (byte) mapAreaNameHash, canBeAttackFromOutOfArea);
	}

	@Override
	public void sendDeath(Optional<Entity> source) {
		super.sendDeath(source);
		if (source.get().isPlayer()) {
//	    Player player = (Player) source;
//	    player.setFavorPoints((getId() == 13820 ? 3 : getId() == 13821 ? 7 : 10) + player.getFavorPoints());
		}
	}
}