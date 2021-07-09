package com.rs.plugin.impl.commands;

import com.rs.game.map.World;
import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.game.player.Rights;
import com.rs.plugin.listener.Command;
import com.rs.plugin.wrapper.CommandSignature;

@CommandSignature(alias = {"npc"}, rights = {Rights.ADMINISTRATOR}, syntax = "Spawns a npc with the specified ID")
public final class NPCCommandPlugin implements Command {
	
	@Override
	public void execute(Player player, String[] cmd, String command) throws Exception {
		try {
			World.addNPC(new NPC(Short.parseShort(cmd[1]), player, (byte) -1, true,
					true));
			return;
		} catch (NumberFormatException e) {
			player.getPackets().sendPanelBoxMessage(
					"Use: ::npc id(Integer)");
		}
	}
}