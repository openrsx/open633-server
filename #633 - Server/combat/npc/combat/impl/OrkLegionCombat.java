package npc.combat.impl;

import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.ForceTalk;
import com.rs.utils.Utils;

import npc.NPC;
import npc.combat.CombatScript;
import npc.combat.NPCCombatDefinitions;

public class OrkLegionCombat extends CombatScript {

    @Override
    public Object[] getKeys() {
	return new Object[] { "Ork legion" };
    }

    public String[] messages = { "For Bork!", "Die Human!", "To the attack!", "All together now!" };

    @Override
    public int attack(NPC npc, Entity target) {
	final NPCCombatDefinitions cdef = npc.getCombatDefinitions();
	npc.setNextAnimation(new Animation(cdef.getAttackEmote()));
	if (Utils.getRandom(3) == 0)
	    npc.setNextForceTalk(new ForceTalk(messages[Utils.getRandom(messages.length > 3 ? 3 : 0)]));
	delayHit(npc, 0, target, getMeleeHit(npc, cdef.getMaxHit()));
	return cdef.getAttackDelay();
    }

}
