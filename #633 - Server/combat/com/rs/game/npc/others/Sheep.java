package com.rs.game.npc.others;

import com.rs.game.WorldTile;
import com.rs.game.npc.NPC;

public class Sheep extends NPC {

	short ticks, origonalId;

	public Sheep(short id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea) {
		super((short) id, tile, (byte) mapAreaNameHash, canBeAttackFromOutOfArea);
		origonalId = id;
	}

	public void processNPC() {
		super.processNPC();
		if (getId() != origonalId) {
			if (ticks++ == 60) {
				setNextNPCTransformation(origonalId);
				ticks = 0;
			}
		}
	}
}
