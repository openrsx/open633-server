package com.rs.game.player.content;

import com.rs.game.player.Player;

/*
 * doesnt let it be extended
 */
public final class Commands {

	/*
	 * all console commands only for admin, chat commands processed if they not
	 * processed by console
	 */

	/**
	 * returns if command was processed
	 */
	public static boolean processCommand(Player player, String command, boolean console, boolean clientCommand) {
		if (command.length() == 0) // if they used ::(nothing) theres no command
			return false;
		String[] cmd = command.split(" ");
		if (cmd.length == 0)
			return false;
		// TODO: redo this
		return false;
	}
}