package com.rs.plugin.impl.commands;

import com.rs.game.WorldTile;
import com.rs.game.player.Player;
import com.rs.game.player.Rights;
import com.rs.game.player.content.TeleportType;
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
		// Just some example usages for you guys.
//		player.move(new WorldTile(3222, 3222, 0) , TeleportType.LEVER);
//		player.move(new WorldTile(3222, 3222, 0) , TeleportType.LEVER, p -> {
//			p.getPackets().sendGameMessage("Hey, I appear after the event is over");
//			System.out.println("Calling 2 different things here, or as many as i'd like.");
//		});
		player.move(new WorldTile(3222, 3222, 0) , TeleportType.LEVER, p -> System.out.println("Can also be used in 1 functional line " + p.getDisplayName()));
	}
}