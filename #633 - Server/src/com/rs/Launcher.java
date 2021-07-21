package com.rs;

import com.alex.store.Index;
import com.rs.cache.Cache;
import com.rs.cache.loaders.ItemDefinitions;
import com.rs.cache.loaders.NPCDefinitions;
import com.rs.cache.loaders.ObjectDefinitions;
import com.rs.cores.CoresManager;
import com.rs.game.map.MapBuilder;
import com.rs.game.map.Region;
import com.rs.game.map.World;
import com.rs.net.ServerChannelHandler;
import com.rs.utilities.LogUtility;
import com.rs.utilities.Utility;
import com.rs.utilities.LogUtility.LogType;

import lombok.SneakyThrows;

public class Launcher {

	public static void main(String[] args) throws Exception {
		GameProperties.getGameProperties().load();
		
		if (args.length < 3) {
			System.out
					.println("USE: guimode(boolean) debug(boolean) hosted(boolean)");
			return;
		}
		GameConstants.HOSTED = Boolean.parseBoolean(args[2]);
		GameConstants.DEBUG = Boolean.parseBoolean(args[1]);
		long currentTime = Utility.currentTimeMillis();
		
		GameLoader.getLOADER().getBackgroundLoader().waitForPendingTasks().shutdown();
		  
		LogUtility.log(LogType.INFO, "Server took "
				+ (Utility.currentTimeMillis() - currentTime)
				+ " milli seconds to launch.");
		addCleanMemoryTask();
	}

	@SneakyThrows(Throwable.class)
	private static void addCleanMemoryTask() {
		CoresManager.schedule(() -> {
			cleanMemory(Runtime.getRuntime().freeMemory() < GameConstants.MIN_FREE_MEM_ALLOWED);
		}, 10);
	}

	public static void cleanMemory(boolean force) {
		if (force) {
			ItemDefinitions.clearItemsDefinitions();
			NPCDefinitions.clearNPCDefinitions();
			ObjectDefinitions.clearObjectDefinitions();
			for (Region region : World.getRegions().values()) {
				for (int regionId : MapBuilder.FORCE_LOAD_REGIONS)
					if (regionId == region.getRegionId())
						continue;
				region.unloadMap();
			}
		}
		for (Index index : Cache.STORE.getIndexes()) {
			if (index == null)
				continue;

			index.resetCachedFiles();
		}
		System.gc();
		LogUtility.log(LogType.INFO, "Game Server memory has been cleaned " + (force ? "force: true:" : "force: false"));
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