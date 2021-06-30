package com.rs.game.npc.others;

import com.rs.game.Animation;
import com.rs.game.World;
import com.rs.game.WorldTile;
import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.game.task.Task;
import com.rs.utilities.Utility;

public class Strykewyrm extends NPC {

	private short stompId;

	public Strykewyrm(short id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea) {
		super((short) id, tile, (byte) mapAreaNameHash, canBeAttackFromOutOfArea);
		stompId = id;
	}

	@Override
	public void processNPC() {
		super.processNPC();
		if (isDead())
			return;
		if (getId() != stompId && !isCantInteract() && !getCombat().underCombat()) {
			setNextAnimation(new Animation(12796));
			setCantInteract(true);
			World.get().submit(new Task(1) {
				@Override
				protected void execute() {
					setNextNPCTransformation(stompId);
					World.get().submit(new Task(1) {
						@Override
						protected void execute() {
							setCantInteract(false);
							this.cancel();
						}
					});
					this.cancel();
				}
			});
		}
	}

	@Override
	public void reset() {
		setId(stompId);
		super.reset();
	}

	public int getStompId() {
		return stompId;
	}

	public static void handleStomping(final Player player, final NPC npc) {
		if (npc.isCantInteract())
			return;
		if (!npc.isMultiArea() || !player.isMultiArea()) {
			if (player.getAttackedBy() != npc && player.getAttackedByDelay() > Utility.currentTimeMillis()) {
				player.getPackets().sendGameMessage("You are already in combat.");
				return;
			}
			if (npc.getAttackedBy() != player && npc.getAttackedByDelay() > Utility.currentTimeMillis()) {
				if (npc.getAttackedBy().isNPC()) {
					npc.setAttackedBy(player); // changes enemy to player,
					// player has priority over
					// npc on single areas
				} else {
					player.getPackets().sendGameMessage("That npc is already in combat.");
					return;
				}
			}
		}
		switch (npc.getId()) {
		case 9462:
			if (player.getSkills().getLevel(18) < 93) {
				player.getPackets().sendGameMessage("You need at least a slayer level of 93 to fight this.");
				return;
			}
			break;
		case 9464:
			if (player.getSkills().getLevel(18) < 77) {
				player.getPackets().sendGameMessage("You need at least a slayer level of 77 to fight this.");
				return;
			}
			break;
		case 9466:
			if (player.getSkills().getLevel(18) < 73) {
				player.getPackets().sendGameMessage("You need at least a slayer level of 73 to fight this.");
				return;
			}
			break;
		default:
			return;
		}
		player.setNextAnimation(new Animation(4278));
		player.getMovement().lock(2);
		npc.setCantInteract(true);
		World.get().submit(new Task(1) {
			@Override
			protected void execute() {
				npc.setNextAnimation(new Animation(12795));
				npc.setNextNPCTransformation((short) (((Strykewyrm) npc).stompId + 1));
				this.cancel();
				World.get().submit(new Task(1) {
					@Override
					protected void execute() {
						npc.setTarget(player);
						npc.setAttackedBy(player);
						npc.setCantInteract(false);
						this.cancel();
					}
				});
				this.cancel();
			}
		});
	}

}
