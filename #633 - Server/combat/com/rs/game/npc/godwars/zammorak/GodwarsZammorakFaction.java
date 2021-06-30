package com.rs.game.npc.godwars.zammorak;

import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.World;
import com.rs.game.WorldTile;
import com.rs.game.item.Item;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.player.Player;
import com.rs.game.player.controllers.Controller;
import com.rs.game.player.controllers.GodWars;
import com.rs.game.task.Task;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class GodwarsZammorakFaction extends NPC {

	public GodwarsZammorakFaction(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea,
			boolean spawned) {
		super((short) id, tile, (byte) mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
	}

	@Override
	public ObjectArrayList<Entity> getPossibleTargets() {
		if (!withinDistance(new WorldTile(2881, 5306, 0), 200))
			return super.getPossibleTargets();
		else {
			ObjectArrayList<Entity> targets = getPossibleTargets(true, true);
			ObjectArrayList<Entity> targetsCleaned = new ObjectArrayList<Entity>();
			for (Entity t : targets) {
				if (t instanceof GodwarsZammorakFaction || (t.isPlayer() && hasGodItem((Player) t)))
					continue;
				targetsCleaned.add(t);
			}
			return targetsCleaned;
		}
	}

	public static boolean hasGodItem(Player player) {
		for (Item item : player.getEquipment().getItems().getItems()) {
			if (item == null || item.getId() == -1)
				continue; // shouldn't happen
			String name = item.getDefinitions().getName().toLowerCase();
			if (name.contains("zamorak coif") || name.contains("zamorak mitre") || name.contains("zamorak full helm")
					|| name.contains("zamorak halo") || name.contains("torva full helm") || name.contains("pernix cowl")
					|| name.contains("virtus mask"))
				return true;
			else if (name.contains("zamorak cape") || name.contains("zamorak cloak"))
				return true;
			else if (name.contains("unholy symbol") || name.contains("zamorak stole"))
				return true;
			else if (name.contains("illuminated unholy book") || name.contains("unholy book")
					|| name.contains("zamorak kiteshield"))
				return true;
			else if (name.contains("zamorak arrows"))
				return true;
			else if (name.contains("zamorak godsword") || name.contains("zamorakian spear")
					|| name.contains("zamorak staff") || name.contains("zamorak crozier")
					|| name.contains("zaryte Bow"))
				return true;
			else if (name.contains("zamorak d'hide") || name.contains("zamorak platebody")
					|| name.contains("torva platebody") || name.contains("pernix body")
					|| name.contains("virtus robe top"))
				return true;
			else if (name.contains("zamorak robe") || name.contains("zamorak robe bottom ")
					|| name.contains("zamorak chaps") || name.contains("zamorak platelegs")
					|| name.contains("zamorak plateskirt") || name.contains("torva platelegs")
					|| name.contains("pernix chaps") || name.contains("virtus robe legs"))
				return true;
			else if (name.contains("zamorak vambraces"))
				return true;
		}
		return false;
	}

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
					source.ifPlayer(player -> {
						Controller controler = player.getControllerManager().getController();
						if (controler != null && controler instanceof GodWars) {
							GodWars godControler = (GodWars) controler;
							godControler.incrementKillCount(3);
						}
					});
					drop();
					reset();
					setLocation(getRespawnTile());
					finish();
					if (!isSpawned())
						setRespawnTask();
					this.cancel();
				}
				loop++;
				this.cancel();
			}
		});
	}
}
