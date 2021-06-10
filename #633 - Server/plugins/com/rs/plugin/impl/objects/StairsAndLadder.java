package com.rs.plugin.impl.objects;

import com.rs.game.WorldObject;
import com.rs.game.WorldTile;
import com.rs.game.dialogue.DialogueEventListener;
import com.rs.game.player.Player;
import com.rs.game.player.content.TeleportType;
import com.rs.plugin.listener.ObjectType;
import com.rs.plugin.wrapper.ObjectSignature;

@ObjectSignature(objectId = {}, name = {"Staircase", "Ladder"})
public class StairsAndLadder implements ObjectType {

	@Override
	public void execute(Player player, WorldObject object, int optionId) throws Exception {
		//if special else do this
		if (object.getDefinitions().getOption(optionId).equalsIgnoreCase("Climb"))
			player.dialog(new DialogueEventListener(player) {
				@Override
				public void start() {
					option("Go-Up", () -> {
						player.move(true,new WorldTile(player.getX(), player.getY(), player.getPlane() + 1), TeleportType.BLANK);
					},
					"Go-Down", () -> {
						player.move(true,new WorldTile(player.getX(), player.getY(), player.getPlane() - 1), TeleportType.BLANK);
					});
				}
			});
		else if (object.getDefinitions().getOption(optionId).equalsIgnoreCase("Climb-up"))
			player.move(true, new WorldTile(player.getX(), player.getY(), player.getPlane() + 1), TeleportType.BLANK);
		else if (object.getDefinitions().getOption(optionId).equalsIgnoreCase("Climb-down"))
			player.move(true, new WorldTile(player.getX(), player.getY(), player.getPlane() - 1), TeleportType.BLANK);
	}
}