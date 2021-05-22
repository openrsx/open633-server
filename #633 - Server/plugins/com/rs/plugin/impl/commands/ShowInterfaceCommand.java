package com.rs.plugin.impl.commands;

import com.rs.game.player.Player;
import com.rs.game.player.Rights;
import com.rs.plugin.listener.Command;
import com.rs.plugin.wrapper.CommandSignature;

@CommandSignature(alias = {"int"}, rights = {Rights.ADMINISTRATOR}, syntax = "Displays an interface")
public final class ShowInterfaceCommand implements Command {
	
	@Override
	public void execute(Player player, String[] cmd, String command) throws Exception {
		if (cmd.length < 2) {
			player.getPackets().sendPanelBoxMessage("Use: ::inter interfaceId");
			return;
		}
		try {
			player.getInterfaceManager().sendInterface(Integer.valueOf(cmd[1]));
		} catch (NumberFormatException e) {
			player.getPackets().sendPanelBoxMessage("Use: ::inter interfaceId");
		}
	}
}