package com.rs.game.player;

import java.io.File;

import com.rs.utils.json.GSONParser;

public class AccountCreation {

	public static Player loadPlayer(String username) {
		return (Player) GSONParser.load("data/characters/" + username + ".json", Player.class);
	}

	public static void savePlayer(Player player) {
		GSONParser.save(player, "data/characters/" + player.getDisplayName() + ".json", Player.class);
	}

	public static boolean exists(String username) {
		return new File("data/characters/" + username + ".json").exists();
	}

}
