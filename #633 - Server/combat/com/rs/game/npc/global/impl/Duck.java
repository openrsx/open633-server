package com.rs.game.npc.global.impl;

import com.rs.game.Entity;
import com.rs.game.npc.NPC;
import com.rs.game.npc.global.GenericNPC;
import com.rs.game.npc.global.GenericNPCSignature;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

@GenericNPCSignature(npcId = 2693, canBeAttackFromOutOfArea = true, isSpawned = false)
public class Duck extends GenericNPC {
	
	@Override
	public void setAttributes(NPC npc) { }
	
	@Override
	public ObjectArrayList<Entity> getPossibleTargets(NPC npc) {
		return npc.getPossibleTargets();
	}
}