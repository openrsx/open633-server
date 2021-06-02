package com.rs;

import java.util.concurrent.TimeUnit;

import com.alex.store.Index;
import com.rs.cache.Cache;
import com.rs.cache.loaders.ItemDefinitions;
import com.rs.cache.loaders.NPCDefinitions;
import com.rs.cache.loaders.ObjectDefinitions;
import com.rs.cores.CoresManager;
import com.rs.game.Region;
import com.rs.game.World;
import com.rs.game.map.MapBuilder;
import com.rs.net.ServerChannelHandler;
import com.rs.utils.Logger;
import com.rs.utils.Utils;

public class Launcher {

	public static void main(String[] args) throws Exception {
		GameProperties.get().load();
		
		if (args.length < 3) {
			System.out
					.println("USE: guimode(boolean) debug(boolean) hosted(boolean)");
			return;
		}
		GameConstants.HOSTED = Boolean.parseBoolean(args[2]);
		GameConstants.DEBUG = Boolean.parseBoolean(args[1]);
		long currentTime = Utils.currentTimeMillis();
		
		GameLoader.getLOADER().getBackgroundLoader().waitForPendingTasks().shutdown();
		
		Logger.log("Launcher", "Server took "
				+ (Utils.currentTimeMillis() - currentTime)
				+ " milli seconds to launch.");
		addCleanMemoryTask();
	}

	private static void addCleanMemoryTask() {
		CoresManager.slowExecutor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				try {
					cleanMemory(Runtime.getRuntime().freeMemory() < GameConstants.MIN_FREE_MEM_ALLOWED);
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, 0, 10, TimeUnit.MINUTES);
	}

	public static void cleanMemory(boolean force) {
		if (force) {
			ItemDefinitions.clearItemsDefinitions();
			NPCDefinitions.clearNPCDefinitions();
			ObjectDefinitions.clearObjectDefinitions();
			skip: for (Region region : World.getRegions().values()) {
				for (int regionId : MapBuilder.FORCE_LOAD_REGIONS)
					if (regionId == region.getRegionId())
						continue skip;
				region.unloadMap();
			}
		}
		for (Index index : Cache.STORE.getIndexes()) {
			if (index == null)
				continue;

			index.resetCachedFiles();
		}
		System.gc();
	}

	public static void shutdown() {
		try {
			closeServices();
		} finally {
			System.exit(0);
		}
	}

	public static void closeServices() {
		ServerChannelHandler.shutdown();
		CoresManager.shutdown();
	}
}