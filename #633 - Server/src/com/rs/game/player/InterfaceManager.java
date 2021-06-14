package com.rs.game.player;

import com.rs.GameConstants;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import lombok.Data;

@Data
public class InterfaceManager {

	public static final int FIXED_WINDOW_ID = 548;
	public static final int RESIZABLE_WINDOW_ID = 746;
	public static final int CHAT_BOX_COMPONENT = 13;
	public static final int FIXED_SCREEN_COMPONENT_ID = 18;
	public static final int RESIZABLE_SCREEN_COMPONENT_ID = 8;
	public static final int FIXED_INV_COMPONENT_ID = 197;
	public static final int RESIZABLE_INV_COMPONENT_ID = 83;
	private final Player player;

	private static final int FIXED_TAB_OFFSET = 201;
	private static final int RESIZABLE_TAB_OFFSET = 87;

	private final class Tab {

		public static final int ATTACK = 0;
		public static final int SKILLS = 1;
		public static final int QUEST = 2;
		public static final int ACHIEVEMENT = 3;
		public static final int INVENTORY = 4;
		public static final int EQUIPMENT = 5;
		public static final int PRAYER = 6;
		public static final int MAGIC = 7;
		@SuppressWarnings("unused")
		public static final int SUMMONING = 8;// also tasks? 1056 TODO find
		public static final int FRIENDS = 9;
		public static final int IGNORES = 10;
		public static final int CLAN = 11;
		public static final int SETTINGS = 12;
		public static final int EMOTES = 13;
		public static final int MUSIC = 14;
		public static final int NOTES = 15;
		public static final int LOGOUT = 18;
	}

	// TODO CLOSE interface tab ids
	Object2ObjectArrayMap<Integer, Integer> openedinterfaces = new Object2ObjectArrayMap<>();
	

	private boolean resizableScreen;
	private int rootInterface;

	public InterfaceManager(Player player) {
		this.player = player;
	}

	public InterfaceManager(Player player, boolean resizableScreen, int rootInterface) {
		this.player = player;
		this.resizableScreen = resizableScreen;
		this.rootInterface = rootInterface;
	}

	public void setWindowInterface(int componentId, int interfaceId) {
		setInterface(true, isResizableScreen() ? RESIZABLE_WINDOW_ID : FIXED_WINDOW_ID, componentId, interfaceId);
	}

	public void removeWindowInterface(int componentId) {
		removeInterfaceByParent(isResizableScreen() ? RESIZABLE_WINDOW_ID : FIXED_WINDOW_ID, componentId);
	}

	public void sendChatBoxInterface(int interfaceId) {
		setInterface(true, 752, CHAT_BOX_COMPONENT, interfaceId);
	}

	public void closeChatBoxInterface() {
		removeInterfaceByParent(752, CHAT_BOX_COMPONENT);
	}

	public boolean containsChatBoxInter() {
		return containsInterfaceAtParent(752, CHAT_BOX_COMPONENT);
	}

	public void setOverlay(int interfaceId, boolean fullScreen) {
		setWindowInterface(isResizableScreen() ? fullScreen ? 1 : 11 : 0, interfaceId);
	}

	public void removeOverlay(boolean fullScreen) {
		removeWindowInterface(isResizableScreen() ? fullScreen ? 1 : 11 : 0);
	}

	public void sendInterface(int interfaceId) {
		setInterface(false, isResizableScreen() ? RESIZABLE_WINDOW_ID : FIXED_WINDOW_ID,
				isResizableScreen() ? RESIZABLE_SCREEN_COMPONENT_ID : FIXED_SCREEN_COMPONENT_ID, interfaceId);
	}

	public void sendInterface(boolean clickThrough, int interfaceId) {
		setInterface(clickThrough, isResizableScreen() ? RESIZABLE_WINDOW_ID : FIXED_WINDOW_ID,
				isResizableScreen() ? RESIZABLE_SCREEN_COMPONENT_ID : FIXED_SCREEN_COMPONENT_ID, interfaceId);
	}

	public void sendInventoryInterface(int interfaceId) {
		setInterface(false, isResizableScreen() ? RESIZABLE_WINDOW_ID : FIXED_WINDOW_ID,
				isResizableScreen() ? RESIZABLE_INV_COMPONENT_ID : FIXED_INV_COMPONENT_ID, interfaceId);
	}

	public final void sendInterfaces() {
		setResizableScreen(player.getDisplayMode() == 2 || player.getDisplayMode() == 3 ? true : false);
		sendGameInterfaces();
		player.getCombatDefinitions().sendUnlockAttackStylesButtons();
		player.getMusicsManager().unlockMusicPlayer();
		player.getInventory().unlockInventoryOptions();
		player.getPrayer().unlockPrayerBookButtons();
		if (player.getFamiliar() != null && player.isRunning())
			player.getFamiliar().unlock();
		player.getControllerManager().sendInterfaces();
	}

	public boolean containsReplacedChatBoxInter() {
		return containsInterfaceAtParent(752, 11);
	}

	public void replaceRealChatBoxInterface(int interfaceId) {
		setInterface(true, 752, 11, interfaceId);
	}

	public void closeReplacedRealChatBoxInterface() {
		removeInterfaceByParent(752, 11);
	}

	public void setDefaultRootInterface() {
		setRootInterface(isResizableScreen() ? 746 : 548, false);
	}

	public void sendGameInterfaces() {
		setDefaultRootInterface();
		sendOrbs();
		sendChatOptions();
		sendChatBox();
		sendPMChatArea();
		sendCombatStyles();
		sendAchievement();
		sendSkills();
//		sendQuest();
		sendInventory();
		sendEquipment();
		sendPrayerBook();
		sendMagicBook();
		sendSettings();
		sendEmotes();
		sendMusic();
//		sendNotes();
		sendIgnores();
		sendFriends();
		sendClanChat();
		sendLogout();
		setInterface(true, 752, 9, 137); // chatbox
	}

	public void sendOrbs() {
		setWindowInterface(isResizableScreen() ? 172 : 181, 748);// hp
		setWindowInterface(isResizableScreen() ? 173 : 183, 749);// pray
		setWindowInterface(isResizableScreen() ? 174 : 184, 750);// run
		setWindowInterface(isResizableScreen() ? 175 : 186, 747);// summon
	}

	public void sendChatBox() {
		setWindowInterface(isResizableScreen() ? 69 : 190, 752);
	}

	public void sendPMChatArea() {
		setWindowInterface(isResizableScreen() ? 70 : 191, 754);
	}

	public void sendChatOptions() {
		setWindowInterface(isResizableScreen() ? 15 : 67, 751);
	}

	public void sendLogout() {
		setWindowInterface(isResizableScreen() ? RESIZABLE_TAB_OFFSET + Tab.LOGOUT : FIXED_TAB_OFFSET + Tab.LOGOUT,
				182);
	}

	public void sendQuest() {
		setWindowInterface(isResizableScreen() ? RESIZABLE_TAB_OFFSET + Tab.QUEST : FIXED_TAB_OFFSET + Tab.QUEST, 190);
	}

	public void sendFriends() {
		setWindowInterface(isResizableScreen() ? RESIZABLE_TAB_OFFSET + Tab.FRIENDS : FIXED_TAB_OFFSET + Tab.FRIENDS,
				550);
	}

	public void sendClanChat() {
		setWindowInterface(isResizableScreen() ? RESIZABLE_TAB_OFFSET + Tab.CLAN : FIXED_TAB_OFFSET + Tab.CLAN, 589);
	}

	public void sendIgnores() {
		setWindowInterface(isResizableScreen() ? RESIZABLE_TAB_OFFSET + Tab.IGNORES : FIXED_TAB_OFFSET + Tab.IGNORES,
				551);
	}

	public void sendMusic() {
		setWindowInterface(isResizableScreen() ? RESIZABLE_TAB_OFFSET + Tab.MUSIC : FIXED_TAB_OFFSET + Tab.MUSIC, 187);
	}

	public void sendNotes() {
		setWindowInterface(isResizableScreen() ? RESIZABLE_TAB_OFFSET + Tab.NOTES : FIXED_TAB_OFFSET + Tab.NOTES, 34);
	}

	public void sendEquipment() {
		setWindowInterface(
				isResizableScreen() ? RESIZABLE_TAB_OFFSET + Tab.EQUIPMENT : FIXED_TAB_OFFSET + Tab.EQUIPMENT, 387);
	}

	public void closeEquipment() {
		removeWindowInterface(isResizableScreen() ? 116 : 176);
	}

	public void sendInventory() {
		setWindowInterface(
				isResizableScreen() ? RESIZABLE_TAB_OFFSET + Tab.INVENTORY : FIXED_TAB_OFFSET + Tab.INVENTORY,
				Inventory.INVENTORY_INTERFACE);
	}

	public void closeInventory() {
		removeWindowInterface(isResizableScreen() ? 115 : 175);
	}

	public void closeSkills() {
		removeWindowInterface(isResizableScreen() ? 113 : 206);
	}

	public void closeCombatStyles() {
		removeWindowInterface(isResizableScreen() ? 111 : 204);
	}

	public void closeTaskSystem() {
		removeWindowInterface(isResizableScreen() ? 112 : 205);
	}

	public void sendCombatStyles() {
		setWindowInterface(isResizableScreen() ? RESIZABLE_TAB_OFFSET + Tab.ATTACK : FIXED_TAB_OFFSET + Tab.ATTACK,
				884);
	}

	public void sendAchievement() {
		setWindowInterface(
				isResizableScreen() ? RESIZABLE_TAB_OFFSET + Tab.ACHIEVEMENT : FIXED_TAB_OFFSET + Tab.ACHIEVEMENT, 259);
	}

	public void sendSkills() {
		setWindowInterface(isResizableScreen() ? RESIZABLE_TAB_OFFSET + Tab.SKILLS : FIXED_TAB_OFFSET + Tab.SKILLS,
				320);
	}

	public void sendSettings() {
		sendSettings(261);
	}

	public void sendSettings(int interfaceId) {
		setWindowInterface(isResizableScreen() ? RESIZABLE_TAB_OFFSET + Tab.SETTINGS : FIXED_TAB_OFFSET + Tab.SETTINGS,
				interfaceId);
	}

	public void sendPrayerBook() {
		setWindowInterface(isResizableScreen() ? RESIZABLE_TAB_OFFSET + Tab.PRAYER : FIXED_TAB_OFFSET + Tab.PRAYER,
				271);
	}

	public void closePrayerBook() {
		removeWindowInterface(isResizableScreen() ? 117 : 210);
	}

	public void sendMagicBook() {
		setWindowInterface(isResizableScreen() ? RESIZABLE_TAB_OFFSET + Tab.MAGIC : FIXED_TAB_OFFSET + Tab.MAGIC,
				player.getCombatDefinitions().getSpellBook());
	}

	public void closeMagicBook() {
		removeWindowInterface(isResizableScreen() ? 118 : 211);
	}

	public void sendEmotes() {
		setWindowInterface(isResizableScreen() ? RESIZABLE_TAB_OFFSET + Tab.EMOTES : FIXED_TAB_OFFSET + Tab.EMOTES,
				464);
	}

	public void closeEmotes() {
		removeWindowInterface(isResizableScreen() ? 124 : 217);
	}

	public void sendDefaultPlayersOptions() {
		player.getPackets().sendPlayerOption("Follow", 2, false);
		player.getPackets().sendPlayerOption("Trade with", 4, false);
		player.getPackets().sendPlayerOption("Req Assist", 5, false);
	}
	
	public void setInterface(boolean clickThrought, int parentInterfaceId, int parentInterfaceComponentId,
			int interfaceId) {
		if (GameConstants.DEBUG) {
			if (parentInterfaceId != rootInterface && !containsInterface(parentInterfaceId))
				System.out.println("The parent interface isnt setted so where are u trying to set it? "
						+ parentInterfaceId + ", " + parentInterfaceComponentId + ", " + interfaceId);
		}
		int parentUID = getComponentUId(parentInterfaceId, parentInterfaceComponentId);
		Integer oldInterface = getOpenedinterfaces().get(parentUID);
		if (oldInterface != null)
			clearChilds(oldInterface);
		getOpenedinterfaces().put(parentUID, interfaceId);
		player.getPackets().sendInterface(clickThrought, parentUID, interfaceId);
	}

	public void removeInterfaceByParent(int parentInterfaceId, int parentInterfaceComponentId) {
		removeInterfaceByParent(getComponentUId(parentInterfaceId, parentInterfaceComponentId));
	}

	public void removeInterfaceByParent(int parentUID) {
		Integer removedInterface = getOpenedinterfaces().remove(parentUID);
		if (removedInterface != null) {
			clearChilds(removedInterface);
			player.getPackets().closeInterface(parentUID);
		}
	}

	private void clearChilds(int parentInterfaceId) {
		for (int key : getOpenedinterfaces().keySet()) {
			if (key >> 16 == parentInterfaceId)
				getOpenedinterfaces().remove(key);
		}
	}

	public void removeInterface(int interfaceId) {
		int parentUID = getInterfaceParentId(interfaceId);
		if (parentUID == -1)
			return;
		removeInterfaceByParent(parentUID);
	}

	public void setRootInterface(int rootInterface, boolean gc) {
		this.rootInterface = rootInterface;
		player.getPackets().sendRootInterface(rootInterface, gc ? 3 : 0);
	}

	public static int getComponentUId(int interfaceId, int componentId) {
		return interfaceId << 16 | componentId;
	}

	public int getInterfaceParentId(int interfaceId) {
		if (interfaceId == rootInterface)
			return -1;
		for (int key : getOpenedinterfaces().keySet()) {
			int value = getOpenedinterfaces().get(key);
			if (value == interfaceId)
				return key;
		}
		return -1;
	}

	public boolean containsInterfaceAtParent(int parentInterfaceId, int parentInterfaceComponentId) {
		return getOpenedinterfaces().containsKey(getComponentUId(parentInterfaceId, parentInterfaceComponentId));
	}

	public boolean containsInterface(int interfaceId) {
		if (interfaceId == rootInterface)
			return true;
		for (int value : getOpenedinterfaces().values())
			if (value == interfaceId)
				return true;
		return false;
	}

	public boolean containsWindowInterfaceAtParent(int componentId) {
		return containsInterfaceAtParent(isResizableScreen() ? RESIZABLE_WINDOW_ID : FIXED_WINDOW_ID, componentId);
	}

	public boolean containsScreenInter() {
		return containsWindowInterfaceAtParent(
				isResizableScreen() ? RESIZABLE_SCREEN_COMPONENT_ID : FIXED_SCREEN_COMPONENT_ID);
	}

	public void removeScreenInterface() {
		removeWindowInterface(isResizableScreen() ? RESIZABLE_SCREEN_COMPONENT_ID : FIXED_SCREEN_COMPONENT_ID);
	}

	public boolean containsInventoryInter() {
		return containsWindowInterfaceAtParent(
				isResizableScreen() ? RESIZABLE_INV_COMPONENT_ID : FIXED_INV_COMPONENT_ID);
	}

	public void removeInventoryInterface() {
		removeWindowInterface(isResizableScreen() ? RESIZABLE_INV_COMPONENT_ID : FIXED_INV_COMPONENT_ID);
	}

	public void setFadingInterface(int backgroundInterface) {
		setWindowInterface(isResizableScreen() ? 12 : 11, backgroundInterface);
	}

	public void closeFadingInterface() {
		removeWindowInterface(isResizableScreen() ? 12 : 11);
	}

	public void setScreenInterface(int backgroundInterface, int interfaceId) {
		removeScreenInterface();
		setWindowInterface(isResizableScreen() ? 40 : 200, backgroundInterface);
		setWindowInterface(isResizableScreen() ? 41 : 201, interfaceId);

		player.setCloseInterfacesEvent(new Runnable() {
			@Override
			public void run() {
				removeWindowInterface(isResizableScreen() ? 40 : 200);
				removeWindowInterface(isResizableScreen() ? 41 : 201);
			}
		});
	}

	public void gazeOrbOfOculus() {
		setRootInterface(475, false);
		setInterface(true, 475, 57, 751);
		setInterface(true, 475, 55, 752);
		player.setCloseInterfacesEvent(new Runnable() {
			@Override
			public void run() {
				setDefaultRootInterface();
				player.getPackets().sendResetCamera();
			}
		});
	}

	/*
	 * returns lastGameTab
	 */
	public int openGameTab(int tabId) {
		player.getPackets().sendGlobalConfig(168, tabId);
		int lastTab = 4;
		return lastTab;
	}

	public void closeInterface(int one, int two) {
		player.getPackets().closeInterface(isResizableScreen() ? two : one);
	}
	
	public void closeInterfaces() {
		if (player.getInterfaceManager().containsScreenInter())
			player.getInterfaceManager().removeScreenInterface();
		if (player.getInterfaceManager().containsInventoryInter())
			player.getInterfaceManager().removeInventoryInterface();
		if (player.dialog() != null)
			player.dialog().complete();
		if (player.getCloseInterfacesEvent() != null) {
			player.getCloseInterfacesEvent().run();
			player.setCloseInterfacesEvent(null);
		}
	}
	
	public void sendRunButtonConfig() {
		player.getVarsManager().sendVar(173,
				player.getResting() == 1 ? 3 : player.getResting() == 2 ? 4 : player.isRun() ? 1 : 0);
	}
	
	public void refreshHitPoints() {
		player.getVarsManager().sendVarBit(7198, player.getHitpoints());
	}
}