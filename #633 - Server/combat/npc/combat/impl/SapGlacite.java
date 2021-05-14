package npc.combat.impl;

import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.player.Player;

import npc.NPC;
import npc.combat.CombatScript;
import npc.combat.NPCCombatDefinitions;

public class SapGlacite extends CombatScript {

    @Override
    public Object[] getKeys() {
	return new Object[] { 14303 };
    }

    @Override
    public int attack(NPC npc, Entity target) {
	NPCCombatDefinitions defs = npc.getCombatDefinitions();
	if (target instanceof Player) {
	    Player player = (Player) target;
	    player.getPrayer().drainPrayer((int) (player.getPrayer().getPrayerpoints() * .05));
	}
	npc.setNextAnimation(new Animation(defs.getAttackEmote()));
	delayHit(npc, 0, target, getMeleeHit(npc, getRandomMaxHit(npc, defs.getMaxHit(), defs.getAttackStyle(), target)));
	return defs.getAttackDelay();
    }
}
