package npc.combat.impl;

import com.rs.game.Entity;

import npc.NPC;
import npc.combat.CombatScript;
import npc.combat.NPCCombatDefinitions;

public class HatiCombat extends CombatScript {

    @Override
    public Object[] getKeys() {
	return new Object[] { "Hati" };
    }

    @Override
    public int attack(NPC npc, Entity target) {
	final NPCCombatDefinitions defs = npc.getCombatDefinitions();

	return defs.getAttackDelay();
    }
}
