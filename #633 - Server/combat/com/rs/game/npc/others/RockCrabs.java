package com.rs.game.npc.others;

import com.rs.game.Entity;
import com.rs.game.map.WorldTile;
import com.rs.game.npc.NPC;

public class RockCrabs extends NPC {

	private short realId;

	public RockCrabs(short id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea, boolean spawned) {
		super((short) id, tile, (byte) mapAreaNameHash, canBeAttackFromOutOfArea);
		realId = id;
		setForceAgressive(true); // to ignore combat lvl
	}

	@Override
	public void setTarget(Entity entity) {
		if (realId == getId()) {
			this.setNextNPCTransformation((short) (realId - 1));
			setHitpoints(getMaxHitpoints()); // rock/bulders have no hp
		}
		super.setTarget(entity);
	}

	@Override
	public void reset() {
		setId(realId);
		super.reset();
	}

}
