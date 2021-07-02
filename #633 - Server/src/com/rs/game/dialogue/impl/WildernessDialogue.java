package com.rs.game.dialogue.impl;

import com.rs.game.Animation;
import com.rs.game.ForceMovement;
import com.rs.game.GameObject;
import com.rs.game.World;
import com.rs.game.WorldTile;
import com.rs.game.dialogue.DialogueEventListener;
import com.rs.game.player.Player;
import com.rs.game.player.controller.impl.WildernessController;
import com.rs.game.task.Task;

/**
 * TODO: Fix Object minor facing issue
 * @author Dennis
 *
 */
public class WildernessDialogue extends DialogueEventListener {

	private final GameObject ditch;
	
	public WildernessDialogue(Player player, Object object) {
		super(player, object);
		this.ditch = (GameObject) object;
	}

	@Override
	public void start() {
		option("Enter the Wilderness", () -> {
			player.getMovement().stopAll(player);
			player.getMovement().lock();
			player.setNextAnimation(new Animation(6132));
			final WorldTile toTile = new WorldTile(
					ditch.getRotation() == 3 || ditch.getRotation() == 1 ? ditch.getX() - 1 : player.getX(),
					ditch.getRotation() == 0 || ditch.getRotation() == 2 ? ditch.getY() + 2 : player.getY(),
					ditch.getPlane());
			player.setNextForceMovement(new ForceMovement(new WorldTile(player), toTile, 1, 2,
					ditch.getRotation() == 0 || ditch.getRotation() == 2 ? ForceMovement.NORTH : ForceMovement.WEST));
			World.get().submit(new Task(2) {
				@Override
				protected void execute() {
					player.setNextWorldTile(toTile);
					player.setNextFaceWorldTile(toTile);
					new WildernessController().start(player);
					player.resetReceivedDamage();
					player.getMovement().unlock();
					this.cancel();
				}
			});
		}, "Nevermind", () -> {
			player.getInterfaceManager().closeChatBoxInterface();
		});
	}
}