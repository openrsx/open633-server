package com.rs.game.player.controllers;

import com.rs.game.Animation;
import com.rs.game.World;
import com.rs.game.WorldObject;
import com.rs.game.WorldTile;
import com.rs.game.player.Player;
import com.rs.game.task.Task;
import com.rs.utils.Utils;

public class JailControler extends Controller {

	@Override
	public void start() {
		if (player.getDetails().getJailed() > Utils.currentTimeMillis())
			player.sendRandomJail(player);
	}

	@Override
	public void process() {
		if (player.getDetails().getJailed() <= Utils.currentTimeMillis()) {
			player.getControllerManager().getController().removeControler();
			player.getPackets().sendGameMessage("Your account has been unjailed.", true);
			player.setNextWorldTile(new WorldTile(2677, 10379, 0));
		}
	}

	public static void stopControler(Player p) {
		p.getControllerManager().getController().removeControler();
	}

	@Override
	public boolean sendDeath() {
		World.get().submit(new Task(1) {
			int loop;
			@Override
			protected void execute() {
				player.stopAll();
				if (loop == 0) {
					player.setNextAnimation(new Animation(836));
				} else if (loop == 1) {
					player.getPackets().sendGameMessage("Oh dear, you have died.");
				} else if (loop == 3) {
					player.setNextAnimation(new Animation(-1));
					player.reset();
					player.setCanPvp(false);
					player.sendRandomJail(player);
					player.getControllerManager().startControler("JailControler");
					player.unlock();
				}
				loop++;
				this.cancel();
			}
		});
		return true;
	}

	@Override
	public boolean login() {

		return false;
	}

	@Override
	public boolean logout() {

		return false;
	}

	@Override
	public boolean processMagicTeleport(WorldTile toTile) {
		player.getPackets().sendGameMessage("You are currently jailed for your delinquent acts.");
		return false;
	}

	@Override
	public boolean processItemTeleport(WorldTile toTile) {
		player.getPackets().sendGameMessage("You are currently jailed for your delinquent acts.");
		return false;
	}

	@Override
	public boolean processObjectClick1(WorldObject object) {
		player.getPackets().sendGameMessage("You cannot do any activities while being jailed.");
		return false;
	}

}
