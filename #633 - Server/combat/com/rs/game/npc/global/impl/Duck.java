package com.rs.game.npc.global.impl;

import com.rs.game.Entity;
import com.rs.game.npc.NPC;
import com.rs.game.npc.global.GenericNPC;
import com.rs.game.npc.global.GenericNPCSignature;
import com.rs.utilities.RandomUtils;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

@GenericNPCSignature(npcId = 2693, canBeAttackFromOutOfArea = true, isSpawned = false)
public class Duck extends GenericNPC {
	
	@Override
	public void process(NPC npc) {
		if (RandomUtils.percentageChance(1))
			System.out.println("YEAH BOI");
	}

	@Override
	public ObjectArrayList<Entity> getPossibleTargets(NPC npc) {
		return npc.getPossibleTargets();
	}
}