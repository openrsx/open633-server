package com.rs.game.npc.godwars.saradomin;

import java.util.concurrent.TimeUnit;

import com.rs.cores.CoresManager;
import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.World;
import com.rs.game.WorldTile;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.npc.godwars.GodWarsBosses;
import com.rs.game.task.Task;

public class CommanderZilyana extends NPC {

	public CommanderZilyana(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea,
			boolean spawned) {
		super((short) id, tile, (byte)mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		setIntelligentRouteFinder(true);
	}

	/*
	 * gotta override else setRespawnTask override doesnt work
	 */
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
		final NPC npc = this;
		CoresManager.slowExecutor.schedule(new Runnable() {
			@Override
			public void run() {
				try {
					setFinished(false);
					World.addNPC(npc);
					npc.setLastRegionId((short) 0);
					updateEntityRegion(npc);
					loadMapRegions();
					checkMultiArea();
					GodWarsBosses.respawnSaradominMinions();
				} catch (Exception e) {
					e.printStackTrace();
				} catch (Error e) {
					e.printStackTrace();
				}
			}
		}, getCombatDefinitions().getRespawnDelay() * 600, TimeUnit.MILLISECONDS);
	}

}
