package com.rs.game.npc.godwars;

import java.util.Optional;

import com.rs.game.Entity;
import com.rs.game.map.World;
import com.rs.game.map.WorldTile;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.task.Task;
import com.rs.net.encoders.other.Animation;

public class GodWarMinion extends NPC {

	int ticks = 10;

	public GodWarMinion(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea,
			boolean spawned) {
		super((short) id, tile, (byte) mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
	}

	@Override
	public void processNPC() {
		super.processNPC();
		if (getCombat().underCombat()) {
			if (ticks-- == 0) {
				ticks = 10;
				getCombat().removeTarget();
			}
		}
	}

	/*
	 * gotta override else setRespawnTask override doesnt work
	 */
	@Override
	public void sendDeath(Optional<Entity> source) {
		final NPCCombatDefinitions defs = getCombatDefinitions();
		resetWalkSteps();
		getCombat().removeTarget();
		setNextAnimation(null);
		World.get().submit(new Task(1) {
			int loop;

			@Override
			protected void execute() {
				if (loop == 0) {
					setNextAnimation(new Animation(defs.getDeathAnim()));
				} else if (loop >= defs.getDeathDelay()) {
					drop();
					reset();
					setLocation(getRespawnTile());
					finish();
					setRespawnTask();
					this.cancel();
				}
				loop++;
				this.cancel();
			}
		});
	}

	@Override
	public void setRespawnTask() {
		if (!isFinished()) {
			reset();
			setLocation(getRespawnTile());
			finish();
		}
	}

	public void respawn() {
		setFinished(false);
		World.addNPC(this);
		setLastRegionId((short) 0);
		updateEntityRegion(this);
		loadMapRegions();
		checkMultiArea();
	}

}
