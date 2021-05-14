package npc.combat.impl;

import com.rs.game.Animation;
import com.rs.game.Entity;

import npc.NPC;
import npc.combat.CombatScript;
import npc.combat.NPCCombatDefinitions;

public class MagicalMeleeCombat extends CombatScript {

    @Override
    public Object[] getKeys() {
	return new Object[] {"Jelly", "Bloodveld"};
    }

    @Override
    public int attack(NPC npc, Entity target) {
	NPCCombatDefinitions def = npc.getCombatDefinitions();
	delayHit(npc, 0, target, getMeleeHit(npc, getRandomMaxHit(npc, npc.getMaxHit(), NPCCombatDefinitions.MAGE, target)));
	npc.setNextAnimation(new Animation(def.getAttackEmote()));
	return def.getAttackDelay();
    }
}
