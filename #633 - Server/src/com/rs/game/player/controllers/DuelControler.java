package com.rs.game.player.controllers;

import com.rs.game.WorldTile;
import com.rs.game.minigames.duel.DuelRules;
import com.rs.game.player.Player;

public class DuelControler extends Controller {

	@Override
	public void start() {
		sendInterfaces();
		player.getAppearance().generateAppearenceData();
		player.getPackets().sendPlayerOption("Challenge", 1, false);
		moved();
	}

	@Override
	public boolean login() {
		start();
		return false;
	}

	@Override
	public boolean logout() {
		return false;
	}

	@Override
	public void forceClose() {
		remove();
	}

	@Override
	public boolean processMagicTeleport(WorldTile toTile) {
		return true;
	}

	@Override
	public boolean processItemTeleport(WorldTile toTile) {
		return true;
	}

	@Override
	public void magicTeleported(int type) {
		removeControler();
		remove();
	}

	@Override
	public void moved() {
		if (!isAtDuelArena(player)) {
			removeControler();
			remove();
		}
	}

	@Override
	public boolean canPlayerOption1(final Player target) {
		player.stopAll();
		if (target.getInterfaceManager().containsScreenInter() || target.isLocked()) {
			player.getPackets().sendGameMessage("The other player is busy.");
			return false;
		}
		if (target.getTemporaryAttributes().get("DuelChallenged") == player) {
			player.getControllerManager().removeControlerWithoutCheck();
			target.getControllerManager().removeControlerWithoutCheck();
			target.getTemporaryAttributes().remove("DuelChallenged");
			player.setLastDuelRules(new DuelRules(player, target));
			target.setLastDuelRules(new DuelRules(target, player));
			player.getControllerManager().startControler("DuelArena", target,
					target.getTemporaryAttributes().get("DuelFriendly"));
			target.getControllerManager().startControler("DuelArena", player,
					target.getTemporaryAttributes().remove("DuelFriendly"));
			return false;
		}
		player.getTemporaryAttributes().put("DuelTarget", target);
		player.getInterfaceManager().sendInterface(640);
		player.getTemporaryAttributes().put("WillDuelFriendly", true);
		player.getVarsManager().sendVar(283, 67108864);
		return false;
	}

	public static void challenge(Player player) {
		player.getInterfaceManager().closeInterfaces();
		Boolean friendly = (Boolean) player.getTemporaryAttributes().remove("WillDuelFriendly");
		if (friendly == null)
			return;
		Player target = (Player) player.getTemporaryAttributes().remove("DuelTarget");
		if (target == null || target.hasFinished() || !target.withinDistance(player, 14)
				|| !(target.getControllerManager().getController() instanceof DuelControler)) {
			player.getPackets()
					.sendGameMessage("Unable to find " + (target == null ? "your target" : target.getDisplayName()));
			return;
		}
		player.getTemporaryAttributes().put("DuelChallenged", target);
		player.getTemporaryAttributes().put("DuelFriendly", friendly);
		player.getPackets().sendGameMessage("Sending " + target.getDisplayName() + " a request...");
		target.getPackets().sendDuelChallengeRequestMessage(player, friendly);
	}

	public void remove() {
		player.getInterfaceManager().removeOverlay(false);
		player.getAppearance().generateAppearenceData();
		player.getPackets().sendPlayerOption("null", 1, false);
	}

	@Override
	public void sendInterfaces() {
		if (isAtDuelArena(player))
			player.getInterfaceManager().setOverlay(638, false);
	}

	public static boolean isAtDuelArena(Player player) {
		return player.withinArea(3341, 3265, 3387, 3281);
	}
}
