package npc.combat.impl;

import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.Graphics;
import com.rs.game.player.Player;
import com.rs.utils.Utils;

import npc.NPC;
import npc.combat.CombatScript;
import npc.combat.NPCCombatDefinitions;

public class TokHaarKetDillCombat extends CombatScript {

    @Override
    public Object[] getKeys() {
	return new Object[] { "TokHaar-Ket-Dill" };
    }

    @Override
    public int attack(NPC npc, Entity target) {
	NPCCombatDefinitions defs = npc.getCombatDefinitions();
	if (Utils.random(6) == 0) {
	    delayHit(npc, 0, target, getRegularHit(npc, Utils.random(defs.getMaxHit() + 1)));
	    target.setNextGraphics(new Graphics(2999));
	    if (target instanceof Player) {
		Player playerTarget = (Player) target;
		playerTarget.getPackets().sendGameMessage("The TokHaar-Ket-Dill slams it's tail to the ground.");
	    }
	} else {
	    delayHit(npc, 0, target, getMeleeHit(npc, getRandomMaxHit(npc, defs.getMaxHit(), defs.getAttackStyle(), target)));
	}
	npc.setNextAnimation(new Animation(defs.getAttackEmote()));
	return defs.getAttackDelay();
    }
}
