package com.rs.plugin.impl.npcs;

import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.plugin.listener.NPCType;
import com.rs.plugin.wrapper.NPCSignature;

@NPCSignature(name = {"Man"}, npcId = {})
public class Man implements NPCType {

	
	@Override
	public void execute(Player player, NPC npc, int option) throws Exception {
		System.out.println("Hey there, my name is " + npc.getDefinitions().getName());
	}
}