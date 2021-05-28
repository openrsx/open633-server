package com.rs.cores;

import com.rs.Settings;
import com.rs.game.World;
import com.rs.utils.Logger;
import com.rs.utils.Utils;

public final class WorldThread extends Thread {

	public static volatile long WORLD_CYCLE;

	protected WorldThread() {
		setPriority(Thread.MAX_PRIORITY);
		setName("World Thread");
	}

	@Override
	public final void run() {
		while (!CoresManager.shutdown) {
			WORLD_CYCLE++;
			long currentTime = Utils.currentTimeMillis();
			World.get().taskManager.sequence();
			
			try {
				World.players().forEach(player -> player.processEntity());
				World.npcs().forEach(npc -> npc.processEntity());
			} catch (Throwable e) {
				Logger.handle(e);
			}
			try {
				World.players().forEach(player -> player.processEntityUpdate());
				World.npcs().forEach(npc -> npc.processEntityUpdate());
			} catch (Throwable e) {
				Logger.handle(e);
			}
			try {
				World.players().forEach(player -> {
					player.getPackets().sendLocalPlayersUpdate();
					player.getPackets().sendLocalNPCsUpdate();
				});
			} catch (Throwable e) {
				Logger.handle(e);
			}
			try {
				World.players().forEach(player -> player.resetMasks());
				World.npcs().forEach(npc -> npc.resetMasks());
			} catch (Throwable e) {
				Logger.handle(e);
			}
			long sleepTime = Settings.WORLD_CYCLE_TIME + currentTime - Utils.currentTimeMillis();
			if (sleepTime <= 0)
				continue;
			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				Logger.handle(e);
			}
		}
	}
}