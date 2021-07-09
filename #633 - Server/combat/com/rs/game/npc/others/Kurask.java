package com.rs.game.npc.others;

import com.rs.game.map.WorldTile;
import com.rs.game.npc.NPC;
import com.rs.game.player.Hit;
import com.rs.game.player.Player;
import com.rs.game.player.PlayerCombat;

public class Kurask extends NPC {

	public Kurask(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea) {
		super((short) id, tile, (byte) mapAreaNameHash, canBeAttackFromOutOfArea);
	}

	@Override
	public void handleIngoingHit(Hit hit) {
		if (hit.getSource().isPlayer()) {
			Player player = (Player) hit.getSource();
			if (!(player.getEquipment().getWeaponId() == 13290 || player.getEquipment().getWeaponId() == 4158)
					&& !(PlayerCombat.isRanging(player) == 2 && (player.getEquipment().getAmmoId() == 13280
							|| player.getEquipment().getAmmoId() == 4160)))
				hit.setDamage(0);
		}
		super.handleIngoingHit(hit);
	}
}
