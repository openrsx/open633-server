package com.rs.game.task.impl;

import com.rs.game.map.World;
import com.rs.game.task.Task;
import com.rs.utilities.RandomUtils;

import skills.Skills;

public final class RestoreSkillTask extends Task {

	/**
	 * Creates a new {@link RestoreSkillTask}.
	 */
	public RestoreSkillTask() {
		super(30, false);
	}

	@Override
	public void execute() {
		World.players().forEach(player -> {
			int ammountTimes = player.getPrayer().usingPrayer(0, 8) ? 2 : 1;
			if (player.isResting())
				ammountTimes += 1;
			boolean berserker = player.getPrayer().usingPrayer(1, 5);
			b: for (int skill = 0; skill < 25; skill++) {
				if (skill == Skills.SUMMONING)
					continue b;
				c: for (int time = 0; time < ammountTimes; time++) {
					int currentLevel = player.getSkills().getLevel(skill);
					int normalLevel = player.getSkills().getLevelForXp(skill);
					if (currentLevel > normalLevel && time == 0) {
						if (skill == Skills.ATTACK || skill == Skills.STRENGTH || skill == Skills.DEFENCE
								|| skill == Skills.RANGE || skill == Skills.MAGIC) {
							if (berserker && RandomUtils.random(100) <= 15)
								continue c;
						}
						player.getSkills().set(skill, currentLevel - 1);
					} else if (currentLevel < normalLevel)
						player.getSkills().set(skill, currentLevel + 1);
					else
						break c;
				}
			}
		});
	}

	@Override
	public void onCancel() {
		World.get().submit(new RestoreSkillTask());
	}
}