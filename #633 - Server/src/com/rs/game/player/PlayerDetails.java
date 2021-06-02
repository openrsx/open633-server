package com.rs.game.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import com.rs.game.player.content.ChargesManager;
import com.rs.game.player.type.impl.AntifireDetails;
import com.rs.utils.MutableNumber;
import com.rs.utils.Stopwatch;
import com.rs.utils.Utils;

import lombok.Data;

/**
 * All personal variables of the Player shall be stored here for easier access.
 * 
 * @author Dennis
 *
 */
@Data
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
		if (charges == null)
			charges = new ChargesManager();
		ownedObjectsManagerKeys = new LinkedList<String>();
		passwordList = new ArrayList<String>();
		ipList = new ArrayList<String>();
		if (watchMap == null)
			watchMap = new HashMap<>();
		
	}

	/**
	 * Represents a Players password for login
	 */
	private String password;
	
	/**
	 * The amount of authority this player has over others.
	 */
	public Rights rights = Rights.PLAYER;

	/**
	 * An array of Runecrafting pouches that possibly contain values.
	 */
	private byte[] pouches;

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
	 * Adds Players display time
	 * 
	 * @param i
	 */
	public void addDisplayTime(long i) {
		this.displayTime = i + Utils.currentTimeMillis();
	}
	
	/**
	 * Represents the last known IP from the Player
	 */
	private String lastIP;

	/**
	 * Represents if a Player is filtering out their chatbox messages
	 */
	private boolean filterGame;
	/**
	 * Represents if the Player has their experience locked
	 */
	private boolean xpLocked;

	/**
	 * An array of possible changes to the Max Cape customization
	 */
	private int[] maxedCapeCustomized;
	/**
	 * An array of possible changes to the Completionist Cape customization
	 */
	private int[] completionistCapeCustomized;

	/**
	 * Represents if the Player should be using older item models to display
	 */
	public boolean oldItemsLook;

	/**
	 * Represents the default Yell color for a Player
	 */
	public String yellColor = "ff0000";

	/**
	 * The creation date
	 */
	private long creationDate;

	/**
	 * The Run Engery amount a Player has
	 */
	private byte runEnergy;
	
	/**
	 * A collection of Stopwatches
	 */
	public HashMap<String, Stopwatch> watchMap = new HashMap<>();

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
	
	
	private final MutableNumber poisonImmunity = new MutableNumber(), skullTimer = new MutableNumber(), teleBlockDelay = new MutableNumber(), prayerDelay = new MutableNumber();
	

	/**
	 * Holds an optional wrapped inside the Antifire details.
	 */
	private Optional<AntifireDetails> antifireDetails = Optional.empty();
	
	/**
	 * Sets the new anti-fire instance for this class directly.
	 * @param details the anti-fire instance to set.
	 */
	public void setAntifireDetail(AntifireDetails details) {
		setAntifireDetails(details == null ? Optional.empty() : Optional.of(details));
	}
	
	/**
	 * Populates the {@link #watchMap}
	 */
	{
		watchMap.put("FOOD", new Stopwatch());
		watchMap.put("DRINKS", new Stopwatch());
		watchMap.put("TOLERANCE", new Stopwatch());
	}

	private byte skullId;
}