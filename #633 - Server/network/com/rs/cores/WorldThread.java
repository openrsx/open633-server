package com.rs.cores;

import com.rs.GameConstants;
import com.rs.game.World;
import com.rs.utilities.Utils;

import lombok.SneakyThrows;

public final class WorldThread extends Thread {

	public static volatile long WORLD_CYCLE;

	protected WorldThread() {
		setPriority(Thread.MAX_PRIORITY);
		setName("World Thread");
	}

	@Override
	@SneakyThrows(Throwable.class)
	public final void run() {
		while (!CoresManager.shutdown) {
			WORLD_CYCLE++;
			long currentTime = Utils.currentTimeMillis();
			
			World.get().getTaskManager().sequence();
			
			World.players().forEach(player -> player.processEntity());
			World.npcs().forEach(npc -> npc.processEntity());
			
			World.players().forEach(player -> player.processEntityUpdate());
			World.npcs().forEach(npc -> npc.processEntityUpdate());

			World.players().forEach(player -> {
				player.getPackets().sendLocalPlayersUpdate();
				player.getPackets().sendLocalNPCsUpdate();
			});
			
			World.players().forEach(player -> player.resetMasks());
			World.npcs().forEach(npc -> npc.resetMasks());
			
			long sleepTime = GameConstants.WORLD_CYCLE_TIME + currentTime - Utils.currentTimeMillis();
			if (sleepTime <= 0)
				continue;
			
			Thread.sleep(sleepTime);
		}
	}
}