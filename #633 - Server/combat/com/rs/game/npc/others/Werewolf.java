package com.rs.game.npc.others;

import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.World;
import com.rs.game.WorldTile;
import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.game.task.Task;
import com.rs.utils.Utils;

public class Werewolf extends NPC {

	private short realId;

	public Werewolf(short id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea, boolean spawned) {
		super((short) id, tile, (byte) mapAreaNameHash, canBeAttackFromOutOfArea);
		realId = id;
	}

	public boolean hasWolfbane(Entity target) {
		if (target instanceof NPC)
			return false;
		return ((Player) target).getEquipment().getWeaponId() == 2952;
	}

	@Override
	public void processNPC() {
		if (isDead() || isCantInteract())
			return;
		if (getCombat().underCombat() && getId() == realId && Utils.random(5) == 0) {
			final Entity target = getCombat().getTarget();
			if (!hasWolfbane(target)) {
				setNextAnimation(new Animation(6554));
				setCantInteract(true);
				World.get().submit(new Task(1) {
					@Override
					protected void execute() {
						setNextNPCTransformation((short) (realId - 20));
						setNextAnimation(new Animation(-1));
						setCantInteract(false);
						setTarget(target);
						this.cancel();
					}
				});
			}
		}
		super.processNPC();
	}

	@Override
	public void reset() {
		setNPC(realId);
		super.reset();
	}

}
