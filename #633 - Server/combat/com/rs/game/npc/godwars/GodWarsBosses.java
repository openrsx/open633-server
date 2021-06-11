package com.rs.game.npc.godwars;

public final class GodWarsBosses {

	public static final GodWarMinion[] graardorMinions = new GodWarMinion[3];
	public static final GodWarMinion[] commanderMinions = new GodWarMinion[3];
	public static final GodWarMinion[] zamorakMinions = new GodWarMinion[3];
	public static final GodWarMinion[] armadylMinions = new GodWarMinion[3];

	public static void respawnBandosMinions() {
		for (GodWarMinion minion : graardorMinions) {
			if (minion.isFinished())
				minion.respawn();
		}
	}

	public static void respawnSaradominMinions() {
		for (GodWarMinion minion : commanderMinions) {
			if (minion.isFinished())
				minion.respawn();
		}
	}

	public static void respawnZammyMinions() {
		for (GodWarMinion minion : zamorakMinions) {
			if (minion.isFinished())
				minion.respawn();
		}
	}

	public static void respawnArmadylMinions() {
		for (GodWarMinion minion : armadylMinions) {
			if (minion.isFinished())
				minion.respawn();
		}
	}

	private GodWarsBosses() {

	}
}
