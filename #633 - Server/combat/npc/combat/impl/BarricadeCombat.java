package npc.combat.impl;

import com.rs.game.Entity;

import npc.NPC;
import npc.combat.CombatScript;

public class BarricadeCombat extends CombatScript {

    @Override
    public Object[] getKeys() {
	return new Object[] { "Barricade" };
    }

    /*
     * empty
     */
    @Override
    public int attack(NPC npc, Entity target) {
	return 0;
    }

}
