package com.rs.game.npc.others;

import java.util.concurrent.TimeUnit;

import com.rs.cores.CoresManager;
import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.World;
import com.rs.game.WorldTile;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.player.Player;
import com.rs.game.task.Task;
import com.rs.utilities.Logger;
import com.rs.utilities.Utils;

public class LivingRock extends NPC {

	private Entity source;
	private long deathTime;

	public LivingRock(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea, boolean spawned) {
		super((short) id, tile, (byte) mapAreaNameHash, canBeAttackFromOutOfArea);
	}

	@Override
	public void sendDeath(final Entity source) {
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
					transformIntoRemains(source);
					this.cancel();
				}
				loop++;
				this.cancel();
			}
		});
	}

	public void transformIntoRemains(Entity source) {
		this.source = source;
		deathTime = Utils.currentTimeMillis();
		final short remainsId = (short) (getId() + 5);
		setNextNPCTransformation(remainsId);
		setWalkType((byte) 0);
		CoresManager.slowExecutor.schedule(new Runnable() {
			@Override
			public void run() {
				try {
					if (remainsId == getId())
						takeRemains();
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, 3, TimeUnit.MINUTES);

	}

	public boolean canMine(Player player) {
		return Utils.currentTimeMillis() - deathTime > 60000 || player == source;
	}

	public void takeRemains() {
		setId((short) (getId() - 5));
		setLocation(getRespawnTile());
		setWalkType(NORMAL_WALK);
		finish();
		if (!isSpawned())
			setRespawnTask();
	}
}