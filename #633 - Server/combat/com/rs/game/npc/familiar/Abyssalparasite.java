package com.rs.game.npc.familiar;

import com.rs.game.Entity;
import com.rs.game.map.World;
import com.rs.game.map.WorldTile;
import com.rs.game.player.Hit;
import com.rs.game.player.Player;
import com.rs.game.player.Hit.HitLook;
import com.rs.game.player.content.Summoning.Pouch;
import com.rs.game.task.Task;
import com.rs.net.encoders.other.Animation;
import com.rs.net.encoders.other.Graphics;
import com.rs.utilities.RandomUtils;

public class Abyssalparasite extends Familiar {

	private static final long serialVersionUID = 7051216741726595486L;

	public Abyssalparasite(Player owner, Pouch pouch, WorldTile tile, int mapAreaNameHash,
			boolean canBeAttackFromOutOfArea) {
		super(owner, pouch, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
	}

	@Override
	public String getSpecialName() {
		return "Abyssal drain";
	}

	@Override
	public String getSpecialDescription() {
		return "Lowers an opponent's prayer with a magic attack.";
	}

	@Override
	public int getBOBSize() {
		return 7;
	}

	@Override
	public int getSpecialAmount() {
		return 3;
	}

	@Override
	public SpecialAttack getSpecialAttack() {
		return SpecialAttack.ENTITY;
	}

	@Override
	public boolean submitSpecial(Object object) {
		final Entity target = (Entity) object;
		final int damage = RandomUtils.random(100);
		setNextAnimation(new Animation(7675));
		setNextGraphics(new Graphics(1422));
		World.sendProjectile(this, target, 1423, 34, 16, 30, 35, 16, 0);
		target.ifPlayer(player -> player.getPrayer().drainPrayer(damage / 2));
		World.get().submit(new Task(2) {
			@Override
			protected void execute() {
				target.applyHit(new Hit(getOwner(), damage, HitLook.MAGIC_DAMAGE));
				this.cancel();
			}
		});
		return false;
	}
}
