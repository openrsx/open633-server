package com.rs.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import com.rs.game.player.Player;

public class SerializableFilesManager {

	private static final String ACCOUNTS_PATH = "data/characters/";
	private static final String RECOVER_PATH = "data/charactersBackup/";
	private static final String CLAN_PATH = "data/clans/";
	private static final String GE_OFFERS = "data/grandExchangeOffers.ser";
	private static final String GE_OFFERS_HISTORY = "data/grandExchangeOffersTrack.ser";
	private static final String GE_PRICES = "data/grandExchangePrices.ser";
	private static final String DISPLAY_NAMES = "data/displayNames.ser";
	private static final String DT_RANKS = "data/dtRanks.ser";
	private static final String IP_BANS = "data/bannedIPS.ser";
	private static final String IP_MUTES = "data/mutedIPS.ser";
	private static final String PK_RANKS = "data/pkRanks.ser";

	private SerializableFilesManager() {
		throw new Error();
	}

	public synchronized static Player loadPlayer(String username) {
		try {
			return (Player) loadSerializedFile(new File(ACCOUNTS_PATH
					+ username + ".p"));
		} catch (Throwable e) {
			Logger.handle(e);
		}
		try {
			Logger.log("SerializableFilesManager", "Recovering account: "
					+ username);
			return (Player) loadSerializedFile(new File(RECOVER_PATH + username
					+ ".p"));
		} catch (Throwable e) {
			Logger.handle(e);
		}
		return null;
	}

	public synchronized static final boolean containsPlayer(String username) {
		return new File(ACCOUNTS_PATH + username + ".p").exists();
	}

	public synchronized static void savePlayer(Player player) {
		try {
			storeSerializableClass(player,
					new File(ACCOUNTS_PATH + player.getUsername() + ".p"));
		} catch (ConcurrentModificationException e) {
			// happens because saving and logging out same time
		} catch (Throwable e) {
			Logger.handle(e);
		}
	}

	public synchronized static boolean containsClan(String name) {
		return new File(CLAN_PATH + name + ".c").exists();
	}

	public static synchronized void saveGEPrices(
			HashMap<Integer, Integer> prices) {
		try {
			SerializableFilesManager.storeSerializableClass(prices, new File(
					GE_PRICES));
		} catch (Throwable t) {
			Logger.handle(t);
		}
	}

	@SuppressWarnings("unchecked")
	public static synchronized ArrayList<String> loadDisplayNames() {
		File file = new File(DISPLAY_NAMES);
		if (file.exists()) {
			try {
				return (ArrayList<String>) SerializableFilesManager
						.loadSerializedFile(file);
			} catch (Throwable e) {
				Logger.handle(e);
			}
		}
		return new ArrayList<String>();
	}

	public static synchronized void saveDisplayNames(ArrayList<String> names) {
		try {
			SerializableFilesManager.storeSerializableClass(names, new File(
					DISPLAY_NAMES));
		} catch (Throwable t) {
			Logger.handle(t);
		}
	}

	public static synchronized void saveIPBans(CopyOnWriteArrayList<String> bans) {
		try {
			SerializableFilesManager.storeSerializableClass(bans, new File(
					IP_BANS));
		} catch (Throwable t) {
			Logger.handle(t);
		}
	}

	@SuppressWarnings("unchecked")
	public static synchronized CopyOnWriteArrayList<String> loadIPBans() {
		if (fileExists(IP_BANS)) {
			try {
				return (CopyOnWriteArrayList<String>) SerializableFilesManager
						.loadSerializedFile(new File(IP_BANS));
			} catch (Throwable t) {
				Logger.handle(t);
				return null;
			}
		} else {
			return new CopyOnWriteArrayList<String>();
		}
	}

	public static synchronized void saveIPMutes(
			CopyOnWriteArrayList<String> bans) {
		try {
			SerializableFilesManager.storeSerializableClass(bans, new File(
					IP_MUTES));
		} catch (Throwable t) {
			Logger.handle(t);
		}
	}

	@SuppressWarnings("unchecked")
	public static synchronized CopyOnWriteArrayList<String> loadIPMutes() {
		if (fileExists(IP_MUTES)) {
			try {
				return (CopyOnWriteArrayList<String>) SerializableFilesManager
						.loadSerializedFile(new File(IP_MUTES));
			} catch (Throwable t) {
				Logger.handle(t);
				return null;
			}
		} else {
			return new CopyOnWriteArrayList<String>();
		}
	}

	public synchronized static String[] getAllPlayers() {
		return new File(ACCOUNTS_PATH).list(); // TODO
	}

	public static final Object loadSerializedFile(File f) throws IOException,
			ClassNotFoundException {
		if (!f.exists()) {
			return null;
		}
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(f));
		Object object = in.readObject();
		in.close();
		return object;
	}

	public static final void storeSerializableClass(Serializable o, File f)
			throws IOException {

		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(f));
		out.writeObject(o);
		out.close();
	}

	public static boolean fileExists(String dir) {
		return new File(dir).exists();
	}

}