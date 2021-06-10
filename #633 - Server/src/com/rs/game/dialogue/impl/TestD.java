package com.rs.game.dialogue.impl;

import com.rs.game.dialogue.DialogueEventListener;
import com.rs.game.npc.NPC;
import com.rs.game.player.Player;

public class TestD extends DialogueEventListener {

	public TestD(Player player, NPC npc) {
		super(player, npc);
	}

	@Override
	public void start() {
		mes("yo sup i'm a regular message");
		player(happy, "lol okay");
		option("Yes", () -> {
			System.out.println("hi");
		}, "No", () -> {
			System.out.println("bye");
		}, "maybe", () -> {
			System.out.println("bye");
		}, "idk", () -> {
			System.out.println("byess");
		});

		item(1050, "I'm a santa hat mate.");
	}
}