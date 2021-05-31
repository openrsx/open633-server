package com.rs.plugin.impl.commands;

import com.rs.game.player.Player;
import com.rs.game.player.Rights;
import com.rs.plugin.listener.Command;
import com.rs.plugin.wrapper.CommandSignature;

/**
 * This is just a dummy command to re-use
 * for whatever testing needed.
 * @author Dennis
 *
 */
@CommandSignature(alias = {"test"}, rights = {Rights.PLAYER}, syntax = "Test a Command")
public final class TestCommand implements Command {
	
	@Override
	public void execute(Player player, String[] cmd, String command) throws Exception {
//		player.getInterfaceManager().sendChatBoxInterface(Integer.valueOf(cmd[1]));
		player.getInterfaceManager().sendChatBoxInterface(64);
		player.getPackets().sendIComponentText(64, 3, "hey");
		player.getPackets().sendIComponentText(64, 4, "I'm a dialogue line");
		player.getPackets().sendEntityOnIComponent(false, 1, 64, 2);
	}
}