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
import com.rs.game.npc.global.GenericNPCDispatcher;
import com.rs.game.player.content.FriendChatsManager;
import com.rs.game.player.spells.passive.PassiveSpellDispatcher;
import com.rs.net.Huffman;
import com.rs.net.ServerChannelHandler;
import com.rs.net.host.HostListType;
import com.rs.net.host.HostManager;
import com.rs.net.packets.logic.LogicPacketDispatcher;
import com.rs.net.packets.outgoing.OutgoingPacketDispatcher;
import com.rs.plugin.CommandPluginDispatcher;
import com.rs.plugin.InventoryPluginDispatcher;
import com.rs.plugin.NPCPluginDispatcher;
import com.rs.plugin.ObjectPluginDispatcher;
import com.rs.plugin.RSInterfacePluginDispatcher;
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
import lombok.SneakyThrows;

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
	private final BlockingExecutorService backgroundLoader = new BlockingExecutorService(Executors.newCachedThreadPool());

	/**
	 * Loads everything here
	 *
	 * @throws IOException
	 */
	@SneakyThrows(IOException.class)
	public void load() {
		Cache.init();
		CoresManager.init();
		World.init();
		getBackgroundLoader().submit(() -> {
			World.get().startAsync().awaitRunning();
			ServerChannelHandler.init();
			Huffman.init();
			MapArchiveKeys.init();
			MapAreas.init();
			MapBuilder.init();
		});
		getBackgroundLoader().submit(() -> {
			EquipData.init();
			ItemBonuses.init();
			Censor.init();
			NPCCombatDefinitionsL.init();
			NPCBonuses.init();
		});
		getBackgroundLoader().submit(() -> {
			MusicHints.init();
			ShopsHandler.init();
			GsonHandler.initialize();
			new MobDropTableLoader().load();
		});
		getBackgroundLoader().submit(() -> {
			DialogueEventRepository.init();
			FriendChatsManager.init();
		});
		getBackgroundLoader().submit(() -> {
			HostManager.deserialize(HostListType.STARTER_RECEIVED);
			HostManager.deserialize(HostListType.BANNED_IP);
			HostManager.deserialize(HostListType.MUTED_IP);
		});
		getBackgroundLoader().submit(() -> {
			RSInterfacePluginDispatcher.load();
			InventoryPluginDispatcher.load();
			ObjectPluginDispatcher.load();
			CommandPluginDispatcher.load();
			NPCPluginDispatcher.load();
			NPCCombatDispatcher.load();
			LogicPacketDispatcher.load();
			OutgoingPacketDispatcher.load();
			GenericNPCDispatcher.load();
			PassiveSpellDispatcher.load();
		});
	}
}