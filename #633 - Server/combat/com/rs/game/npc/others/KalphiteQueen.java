package com.rs.game.npc.others;

import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.Graphics;
import com.rs.game.World;
import com.rs.game.WorldTile;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.task.Task;

public class KalphiteQueen extends NPC {

	public KalphiteQueen(short id, WorldTile tile, byte mapAreaNameHash, boolean canBeAttackFromOutOfArea,
			boolean spawned) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		setLureDelay((short) 0);
		setForceAgressive(true);
	}

	@Override
	public void sendDeath(Entity source) {
		final NPCCombatDefinitions defs = getCombatDefinitions();
		resetWalkSteps();
		getCombat().removeTarget();
		setNextAnimation(null);
		World.get().submit(new Task(1) {
			int loop;
			@Override
			protected void execute() {
				if (loop == 0) {
					setNextAnimation(new Animation(defs.getDeathEmote()));
				} else if (loop >= defs.getDeathDelay()) {
					if (getId() == 1158) {
						setCantInteract(true);
						transformIntoNPC((short) 1160);
						setNextGraphics(new Graphics(1055));
						setNextAnimation(new Animation(6270));
						World.get().submit(new Task(5) {
							@Override
							protected void execute() {
								reset();
								setCantInteract(false);
								this.cancel();
							}
						});
					} else {
						drop();
						reset();
						setLocation(getRespawnTile());
						finish();
						if (!isSpawned())
							setRespawnTask();
						transformIntoNPC((short) 1158);
					}
					this.cancel();
				}
				loop++;
			}
		});
	}
}