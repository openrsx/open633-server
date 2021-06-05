package com.rs.game.player;

import java.util.Optional;

import com.rs.GameConstants;
import com.rs.game.Animation;
import com.rs.game.WorldTile;
import com.rs.game.player.controllers.Wilderness;
import com.rs.game.task.impl.ActorDeathTask;
import com.rs.net.host.HostManager;

import skills.Skills;

public class PlayerDeath extends ActorDeathTask<Player> {

	public PlayerDeath(Player actor) {
		super(actor);
	}

	@Override
	public void preDeath() {
		if (!getActor().getControllerManager().sendDeath())
			return;
		getActor().lock();
		getActor().setNextAnimation(new Animation(836));
	}

	@Override
	public void death() {
		if (getActor().getPoisonDamage().get() > 0) {
			getActor().getPoisonDamage().set(0);
			getActor().getPackets().sendGlobalConfig(102, 0);
		}
		getActor().getDetails().setAntifireDetails(Optional.empty());	
		getActor().getDetails().getSkullTimer().set(0);
		getActor().getDetails().getWatchMap().get("TOLERANCE").reset();
		getActor().stopAll();
		if (getActor().getFamiliar() != null)
			getActor().getFamiliar().sendDeath(getActor());
	}

	@Override
	public void postDeath() {
		getActor().getPackets().sendMusicEffect(90);
		getActor().getPackets().sendGameMessage("Oh dear, you have died.");
		getActor().setNextWorldTile(new WorldTile(GameConstants.START_PLAYER_LOCATION));
		getActor().setNextAnimation(new Animation(-1));
		getActor().heal(getActor().getMaxHitpoints());
		final int maxPrayer = getActor().getSkills().getLevelForXp(Skills.PRAYER) * 10;
		getActor().getPrayer().restorePrayer(maxPrayer);
		getActor().setNextAnimation(new Animation(-1));
		getActor().unlock();
		getActor().getCombatDefinitions().resetSpecialAttack();
		getActor().getPrayer().closeAllPrayers();
		getActor().setRunEnergy(100);
		
		if (getActor() instanceof Player) {
			Player killer = (Player) getActor();
			killer.setAttackedByDelay(4);
			if(HostManager.same(getActor(), killer)) {
				killer.getPackets().sendGameMessage("You don't receive any points because you and " + getActor().getDisplayName() + " are connected from the same network.");
				return;
			}
			if (getActor().getControllerManager().getController() instanceof Wilderness) {
				if (getActor().getControllerManager().getController() != null) {
					getActor().sendItemsOnDeath(killer);
				}
			}
		}
	}
}