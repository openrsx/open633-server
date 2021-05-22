package com.rs.game.player;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.rs.utils.Utils;

/**
 * All personal variables of the Player shall be stored here for easier access.
 * 
 * @author Dennis
 *
 */
public final class PlayerDetails {

	/**
	 * Constructs a new Player's details
	 */
	public PlayerDetails() {
		pouches = new byte[4];
		creationDate = Utils.currentTimeMillis();
		runEnergy = 100;
		allowChatEffects = true;
		mouseButtons = true;
		profanityFilter = true;
		warriorPoints = new double[6];
		charges = new ChargesManager();
		ownedObjectsManagerKeys = new LinkedList<String>();
		passwordList = new ArrayList<String>();
		ipList = new ArrayList<String>();
	}

	/**
	 * Represents a Players password for login
	 */
	private String password;

	/**
	 * Gets the Players password
	 * 
	 * @return
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Sets the Players password
	 * 
	 * @param password
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * The amount of authority this player has over others.
	 */
	public Rights rights = Rights.PLAYER;

	/**
	 * Gets the amount of authority this player has over others.
	 * 
	 * @return the authority this player has.
	 */
	public Rights getRights() {
		return rights;
	}

	/**
	 * Sets the value for {@link Player#rights}.
	 * 
	 * @param rights the new value to set.
	 */
	public void setRights(Rights rights) {
		this.rights = rights;
	}

	/**
	 * An array of Runecrafting pouches that possibly contain values.
	 */
	private byte[] pouches;

	/**
	 * Gets the Runecrafting pouches
	 * 
	 * @return pouches
	 */
	public byte[] getPouches() {
		return pouches;
	}

	private long displayTime;

	/**
	 * The length of a Player being Muted (Unable to chat)
	 */
	private long muted;

	/**
	 * Length of the Player being Jailed (stuck in a remote area)
	 */
	private long jailed;

	/**
	 * Gets the Muted length
	 * 
	 * @return length
	 */
	public long getMuted() {
		return muted;
	}

	/**
	 * Sets the Players Mute time
	 * 
	 * @param muted
	 */
	public void setMuted(long muted) {
		this.muted = muted;
	}

	/**
	 * Gets the Jailed length
	 * 
	 * @return
	 */
	public long getJailed() {
		return jailed;
	}

	/**
	 * Sets the Players Jail length
	 * 
	 * @param jailed
	 */
	public void setJailed(long jailed) {
		this.jailed = jailed;
	}

	/**
	 * Adds Players display time
	 * 
	 * @param i
	 */
	public void addDisplayTime(long i) {
		this.displayTime = i + Utils.currentTimeMillis();
	}

	/**
	 * Gets the Players display time
	 * 
	 * @return
	 */
	public long getDisplayTime() {
		return displayTime;
	}

	/**
	 * Represents the last known IP from the Player
	 */
	private String lastIP;

	/**
	 * Sets the last known IP to the Player
	 * 
	 * @param lastIP
	 */
	public void setLastIP(String lastIP) {
		this.lastIP = lastIP;
	}

	/**
	 * Gets the last known IP
	 * 
	 * @return ip
	 */
	public String getLastIP() {
		return lastIP;
	}

	/**
	 * Represents if a Player is filtering out their chatbox messages
	 */
	private boolean filterGame;
	/**
	 * Represents if the Player has their experience locked
	 */
	private boolean xpLocked;

	/**
	 * Gets the Filter game status from a Player
	 * 
	 * @return filter
	 */
	public boolean isFilterGame() {
		return filterGame;
	}

	/**
	 * Sets the Filter game status for a Player
	 * 
	 * @param filterGame
	 */
	public void setFilterGame(boolean filterGame) {
		this.filterGame = filterGame;
	}

	/**
	 * Gets the experience locked status for a Player
	 * 
	 * @return
	 */
	public boolean isXpLocked() {
		return xpLocked;
	}

	/**
	 * Sets the experienced lock status for a Player
	 * 
	 * @param locked
	 */
	public void setXpLocked(boolean locked) {
		this.xpLocked = locked;
	}

	/**
	 * An array of possible changes to the Max Cape customization
	 */
	private int[] maxedCapeCustomized;
	/**
	 * An array of possible changes to the Completionist Cape customization
	 */
	private int[] completionistCapeCustomized;

	/**
	 * Gets the Completionist Cape customization array
	 * 
	 * @return completionistCapeCustomized
	 */
	public int[] getCompletionistCapeCustomized() {
		return completionistCapeCustomized;
	}

	/**
	 * Sets the Completionist Cape array
	 * 
	 * @param skillcapeCustomized
	 */
	public void setCompletionistCapeCustomized(int[] skillcapeCustomized) {
		this.completionistCapeCustomized = skillcapeCustomized;
	}

	/**
	 * Gets the Max Cape customization array
	 * 
	 * @return
	 */
	public int[] getMaxedCapeCustomized() {
		return maxedCapeCustomized;
	}

	/**
	 * Sets the Completionist Cape array
	 * 
	 * @param maxedCapeCustomized
	 */
	public void setMaxedCapeCustomized(int[] maxedCapeCustomized) {
		this.maxedCapeCustomized = maxedCapeCustomized;
	}

	/**
	 * Represents if the Player should be using older item models to display
	 */
	public boolean oldItemsLook;

	/**
	 * Gets the
	 * 
	 * @return
	 */
	public boolean isOldItemsLook() {
		return oldItemsLook;
	}

	/**
	 * Represents the default Yell color for a Player
	 */
	public String yellColor = "ff0000";

	/**
	 * Gets the Yell color of a Player
	 * 
	 * @return
	 */
	public String getYellColor() {
		return yellColor;
	}

	/**
	 * Sets the Yell color for a Player
	 * 
	 * @param yellColor
	 */
	public void setYellColor(String yellColor) {
		this.yellColor = yellColor;
	}

	/**
	 * The creation date
	 */
	private long creationDate;

	/**
	 * Gets the creation date
	 * 
	 * @return date
	 */
	public long getCreationDate() {
		return creationDate;
	}

	/**
	 * The Run Engery amount a Player has
	 */
	private byte runEnergy;

	/**
	 * Gets the Run Energy
	 * 
	 * @return
	 */
	public byte getRunEnergy() {
		return runEnergy;
	}

	/**
	 * Sets the Players Run Energy amount
	 * 
	 * @param runEnergy
	 */
	public void setRunEnergy(byte runEnergy) {
		this.runEnergy = runEnergy;
	}

	/**
	 * TODO: Add documentation to rest of these imports. Documentation is super
	 * important so take your time to explain their functions so others can learn!
	 */
	private boolean allowChatEffects;
	private boolean acceptAid;
	private boolean mouseButtons;
	private boolean profanityFilter;
	private byte privateChatSetup;
	private byte friendChatSetup;
	private byte clanChatSetup;
	private byte guestChatSetup;
	private boolean forceNextMapLoadRefresh;
	private long poisonImmune; // to redo
	private long fireImmune; // to redo
	private double[] warriorPoints;

	public boolean isAllowChatEffects() {
		return allowChatEffects;
	}

	public void setAllowChatEffects(boolean allowChatEffects) {
		this.allowChatEffects = allowChatEffects;
	}

	public boolean isAcceptAid() {
		return acceptAid;
	}

	public void setAcceptAid(boolean acceptAid) {
		this.acceptAid = acceptAid;
	}

	public boolean isMouseButtons() {
		return mouseButtons;
	}

	public void setMouseButtons(boolean mouseButtons) {
		this.mouseButtons = mouseButtons;
	}

	public boolean isProfanityFilter() {
		return profanityFilter;
	}

	public void setProfanityFilter(boolean profanityFilter) {
		this.profanityFilter = profanityFilter;
	}

	public byte getPrivateChatSetup() {
		return privateChatSetup;
	}

	public void setPrivateChatSetup(byte privateChatSetup) {
		this.privateChatSetup = privateChatSetup;
	}

	public byte getFriendChatSetup() {
		return friendChatSetup;
	}

	public void setFriendChatSetup(byte friendChatSetup) {
		this.friendChatSetup = friendChatSetup;
	}

	public byte getClanChatSetup() {
		return clanChatSetup;
	}

	public void setClanChatSetup(byte clanChatSetup) {
		this.clanChatSetup = clanChatSetup;
	}

	public byte getGuestChatSetup() {
		return guestChatSetup;
	}

	public void setGuestChatSetup(byte guestChatSetup) {
		this.guestChatSetup = guestChatSetup;
	}

	public boolean isForceNextMapLoadRefresh() {
		return forceNextMapLoadRefresh;
	}

	public void setForceNextMapLoadRefresh(boolean forceNextMapLoadRefresh) {
		this.forceNextMapLoadRefresh = forceNextMapLoadRefresh;
	}

	public long getPoisonImmune() {
		return poisonImmune;
	}

	public void setPoisonImmune(long poisonImmune) {
		this.poisonImmune = poisonImmune;
	}

	public long getFireImmune() {
		return fireImmune;
	}

	public void setFireImmune(long fireImmune) {
		this.fireImmune = fireImmune;
	}

	public double[] getWarriorPoints() {
		return warriorPoints;
	}

	public void setPouches(byte[] pouches) {
		this.pouches = pouches;
	}

	public void setDisplayTime(long displayTime) {
		this.displayTime = displayTime;
	}

	public void setOldItemsLook(boolean oldItemsLook) {
		this.oldItemsLook = oldItemsLook;
	}

	public void setCreationDate(long creationDate) {
		this.creationDate = creationDate;
	}

	public long getBanned() {
		return banned;
	}

	public void setBanned(long banned) {
		this.banned = banned;
	}

	public boolean isPermBanned() {
		return permBanned;
	}

	public void setPermBanned(boolean permBanned) {
		this.permBanned = permBanned;
	}

	public boolean isYellOff() {
		return yellOff;
	}

	public void setYellOff(boolean yellOff) {
		this.yellOff = yellOff;
	}

	public byte getPublicStatus() {
		return publicStatus;
	}

	public void setPublicStatus(byte publicStatus) {
		this.publicStatus = publicStatus;
	}

	public byte getClanStatus() {
		return clanStatus;
	}

	public void setClanStatus(byte clanStatus) {
		this.clanStatus = clanStatus;
	}

	public byte getTradeStatus() {
		return tradeStatus;
	}

	public void setTradeStatus(byte tradeStatus) {
		this.tradeStatus = tradeStatus;
	}

	public byte getAssistStatus() {
		return assistStatus;
	}

	public void setAssistStatus(byte assistStatus) {
		this.assistStatus = assistStatus;
	}

	public ArrayList<String> getPasswordList() {
		return passwordList;
	}

	public void setPasswordList(ArrayList<String> passwordList) {
		this.passwordList = passwordList;
	}

	public ArrayList<String> getIpList() {
		return ipList;
	}

	public void setIpList(ArrayList<String> ipList) {
		this.ipList = ipList;
	}

	public ChargesManager getCharges() {
		return charges;
	}

	public void setCharges(ChargesManager charges) {
		this.charges = charges;
	}

	public String getCurrentFriendChatOwner() {
		return currentFriendChatOwner;
	}

	public void setCurrentFriendChatOwner(String currentFriendChatOwner) {
		this.currentFriendChatOwner = currentFriendChatOwner;
	}

	public String getClanName() {
		return clanName;
	}

	public void setClanName(String clanName) {
		this.clanName = clanName;
	}

	public boolean isConnectedClanChannel() {
		return connectedClanChannel;
	}

	public void setConnectedClanChannel(boolean connectedClanChannel) {
		this.connectedClanChannel = connectedClanChannel;
	}

	public byte getSummoningLeftClickOption() {
		return summoningLeftClickOption;
	}

	public void setSummoningLeftClickOption(byte summoningLeftClickOption) {
		this.summoningLeftClickOption = summoningLeftClickOption;
	}

	public List<String> getOwnedObjectsManagerKeys() {
		return ownedObjectsManagerKeys;
	}

	public void setOwnedObjectsManagerKeys(List<String> ownedObjectsManagerKeys) {
		this.ownedObjectsManagerKeys = ownedObjectsManagerKeys;
	}

	public void setWarriorPoints(double[] warriorPoints) {
		this.warriorPoints = warriorPoints;
	}

	private long banned;
	private boolean permBanned;
	private boolean yellOff;
	// game bar status
	private byte publicStatus;
	private byte clanStatus;
	private byte tradeStatus;
	private byte assistStatus;

	// Used for storing recent ips and password
	private ArrayList<String> passwordList = new ArrayList<String>();
	private ArrayList<String> ipList = new ArrayList<String>();

	private ChargesManager charges;

	private String currentFriendChatOwner;
	private String clanName;// , guestClanChat;
	private boolean connectedClanChannel;

	private byte summoningLeftClickOption;
	private List<String> ownedObjectsManagerKeys;
}