package npc.combat.impl;

import com.rs.game.Animation;
import com.rs.game.Entity;

import npc.NPC;
import npc.combat.CombatScript;
import npc.combat.NPCCombatDefinitions;

public class DharokCombat extends CombatScript {

    @Override
    public Object[] getKeys() {
	return new Object[] { 2026 };
    }

    @Override
    public int attack(NPC npc, Entity target) {
	final NPCCombatDefinitions defs = npc.getCombatDefinitions();
	npc.setNextAnimation(new Animation(defs.getAttackEmote()));
	int damage = getRandomMaxHit(npc, defs.getMaxHit(), NPCCombatDefinitions.MELEE, target);
	if (damage != 0) {
	    double perc = 1 - (npc.getHitpoints() / npc.getMaxHitpoints());
	    damage += perc * 380;
	}
	delayHit(npc, 0, target, getMeleeHit(npc, damage));
	return defs.getAttackDelay();
    }
}
