package com.rs;

import java.io.IOException;
import java.util.concurrent.Executors;

import com.rs.cache.Cache;
import com.rs.cores.BlockingExecutorService;
import com.rs.cores.CoresManager;
import com.rs.game.World;
import com.rs.game.dialogue.DialogueEventRepository;
import com.rs.game.map.MapBuilder;
import com.rs.game.npc.combat.NPCCombatDispatcher;
import com.rs.game.player.content.FriendChatsManager;
import com.rs.game.player.controllers.ControllerHandler;
import com.rs.net.Huffman;
import com.rs.net.ServerChannelHandler;
import com.rs.net.host.HostListType;
import com.rs.net.host.HostManager;
import com.rs.net.packets.PacketDispatcher;
import com.rs.plugin.CommandDispatcher;
import com.rs.plugin.InventoryDispatcher;
import com.rs.plugin.NPCDispatcher;
import com.rs.plugin.ObjectDispatcher;
import com.rs.plugin.RSInterfaceDispatcher;
import com.rs.utilities.json.GsonHandler;
import com.rs.utilities.json.impl.MobDropTableLoader;
import com.rs.utilities.loaders.Censor;
import com.rs.utilities.loaders.EquipData;
import com.rs.utilities.loaders.ItemBonuses;
import com.rs.utilities.loaders.MapArchiveKeys;
import com.rs.utilities.loaders.MapAreas;
import com.rs.utilities.loaders.MusicHints;
import com.rs.utilities.loaders.NPCBonuses;
import com.rs.utilities.loaders.NPCCombatDefinitionsL;
import com.rs.utilities.loaders.ShopsHandler;

import lombok.Getter;

/**
 *
 * @author Tyluur <itstyluur@gmail.com>
 * @author Dennis
 * @since Feb 27, 2014
 */
public class GameLoader {

	public GameLoader() {
		load();
	}

	/**
	 * The instance of the loader
	 */
	@Getter
	private static final GameLoader LOADER = new GameLoader();

	/**
	 * An executor service which handles background loading tasks.
	 */
	@Getter
	private final BlockingExecutorService backgroundLoader = new BlockingExecutorService(
			Executors.newCachedThreadPool());

	/**
	 * Loads everything here
	 *
	 * @throws IOException
	 */
	public void load() {
		/** Setting the server clock time */
		try {
			Cache.init();
			CoresManager.init();
			World.init();
		} catch (IOException e) {
			e.printStackTrace();
		}
		getBackgroundLoader().submit(() -> {
			ServerChannelHandler.init();
		});
		getBackgroundLoader().submit(() -> {
			Huffman.init();
			MapArchiveKeys.init();
			MapAreas.init();
			MapBuilder.init();
			return null;
		});
		getBackgroundLoader().submit(() -> {
			EquipData.init();
			ItemBonuses.init();
			Censor.init();
			NPCCombatDefinitionsL.init();
			NPCBonuses.init();
			return null;
		});
		getBackgroundLoader().submit(() -> {
			MusicHints.init();
			ShopsHandler.init();
			return null;
		});
		getBackgroundLoader().submit(() -> {
			ControllerHandler.init();
			DialogueEventRepository.init();
			FriendChatsManager.init();
			World.init();
			return null;
		});
		getBackgroundLoader().submit(() -> {
			HostManager.deserialize(HostListType.STARTER_RECEIVED);
			HostManager.deserialize(HostListType.BANNED_IP);
			HostManager.deserialize(HostListType.MUTED_IP);
		});
		getBackgroundLoader().submit(() -> {
			GsonHandler.initialize();
			new MobDropTableLoader().load();
			RSInterfaceDispatcher.load();
			InventoryDispatcher.load();
			ObjectDispatcher.load();
			CommandDispatcher.load();
			NPCDispatcher.load();
			NPCCombatDispatcher.load();
			PacketDispatcher.load();
			return null;
		});
	}
}