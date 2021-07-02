package com.rs.plugin.impl.objects;

import com.rs.game.Animation;
import com.rs.game.ForceMovement;
import com.rs.game.GameObject;
import com.rs.game.World;
import com.rs.game.WorldTile;
import com.rs.game.dialogue.impl.WildernessDialogue;
import com.rs.game.player.Player;
import com.rs.game.task.Task;
import com.rs.plugin.listener.ObjectType;
import com.rs.plugin.wrapper.ObjectSignature;

@ObjectSignature(objectId = {}, name = { "Wilderness wall" })
public class WildernessDitch implements ObjectType {

	@Override
	public void execute(Player player, GameObject object, int optionId) throws Exception {
		if (((object.getRotation() == 0 || object.getRotation() == 2) && player.getY() < object.getY())
				|| (object.getRotation() == 1 || object.getRotation() == 3) && player.getX() > object.getX()) {
			player.dialog(new WildernessDialogue(player, object));
			return;
		} else {
			player.getMovement().lock();
			player.setNextAnimation(new Animation(6132));
			final WorldTile toTile = new WorldTile(
					object.getRotation() == 1 || object.getRotation() == 3 ? object.getX() + 1 : player.getX(),
					object.getRotation() == 0 || object.getRotation() == 2 ? object.getY() - 1 : player.getY(),
					object.getPlane());
			player.setNextForceMovement(new ForceMovement(new WorldTile(player), toTile, 1, 2,
					object.getRotation() == 0 || object.getRotation() == 2 ? ForceMovement.SOUTH : ForceMovement.EAST));
			World.get().submit(new Task(2) {
				@Override
				protected void execute() {
					player.setNextWorldTile(toTile);
					player.faceObject(object);
					player.getMovement().unlock();
					this.cancel();
				}
			});
		}
	}
}