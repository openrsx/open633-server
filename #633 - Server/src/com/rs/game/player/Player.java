package com.rs.game.player;

import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.alex.utils.VarsManager;
import com.rs.GameConstants;
import com.rs.game.Entity;
import com.rs.game.EntityType;
import com.rs.game.dialogue.DialogueEventListener;
import com.rs.game.map.Region;
import com.rs.game.map.World;
import com.rs.game.npc.familiar.Familiar;
import com.rs.game.npc.others.Pet;
import com.rs.game.player.actions.ActionManager;
import com.rs.game.player.content.FriendChatsManager;
import com.rs.game.player.content.MusicsManager;
import com.rs.game.player.content.Notes;
import com.rs.game.player.content.PriceCheckManager;
import com.rs.game.player.content.pet.PetManager;
import com.rs.game.player.controller.Controller;
import com.rs.game.player.controller.ControllerHandler;
import com.rs.game.player.spells.passive.PassiveSpellDispatcher;
import com.rs.game.player.type.CombatEffect;
import com.rs.game.route.CoordsEvent;
import com.rs.game.route.RouteEvent;
import com.rs.game.task.impl.CombatEffectTask;
import com.rs.game.task.impl.SkillActionTask;
import com.rs.net.IsaacKeyPair;
import com.rs.net.LogicPacket;
import com.rs.net.Session;
import com.rs.net.encoders.WorldPacketsEncoder;
import com.rs.net.encoders.other.HintIconsManager;
import com.rs.net.host.HostListType;
import com.rs.net.host.HostManager;
import com.rs.utilities.Logger;
import com.rs.utilities.Utility;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Data;
import lombok.EqualsAndHashCode;
import skills.Skills;

/**
 * Represents a Player & all of their attributes
 * @author Dennis
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class Player extends Entity {

	/**
	 * The Player's Username
	 */
	private transient String username;
	
	/**
	 * The Player's Session
	 */
	private transient Session session;
	
	/**
	 * The Client Region Map Loading state
	 */
	private transient boolean clientLoadedMapRegion;
	
	/**
	 * Display Mode Type (Fixed, Resize, Fullscreen)
	 */
	private transient byte displayMode;
	
	/**
	 * Client Screen Width
	 */
	private transient short screenWidth;
	
	/**
	 * Client Screen Height
	 */
	private transient short screenHeight;
	
	/**
	 * Represents a Movement type
	 */
	private transient byte temporaryMovementType;
	
	/**
	 * Should we update the Movement state
	 */
	private transient boolean updateMovementType;
	
	/**
	 * Has the Player started their {@link #session}
	 */
	private transient boolean started;
	
	/**
	 * Is the Player's {@link #session} currently Running
	 */
	private transient boolean running;
	
	/**
	 * The type of Resting state of a Player
	 * Example: Not, Sitting, idle
	 */
	private transient byte resting;
	
	/**
	 * Can the Player engage in PVP combat
	 */
	private transient boolean canPvp;
	
	/**
	 * Does the Player have permission to Trade others
	 */
	private transient boolean cantTrade;
	
	/**
	 * Represents a Runnable event that takes place when closing an interface
	 */
	private transient Runnable closeInterfacesEvent;
	
	/**
	 * The last known time length of the last message a Player sends
	 */
	private transient long lastPublicMessage;
	
	/**
	 * The Item switching cache (Switches for PVE/PVP)
	 */
	private transient ObjectArrayList<Byte> switchItemCache;
	
	/**
	 * Does the Player have their Equipping/Removing disabled
	 */
	private transient boolean disableEquip;
	
	/**
	 * Does the Player become invulnerable to any damage.
	 */
	private transient boolean invulnerable;
	
	/**
	 * Is the Player finishing their {@link #session}
	 */
	private transient boolean finishing;
	
	/**
	 * Represents a Player's Interface management system
	 */
	private transient InterfaceManager interfaceManager;
	
	/**
	 * Represents a Player's Hint Icon management system
	 */
	private transient HintIconsManager hintIconsManager;
	
	/**
	 * Represents a Action management system
	 */
	private transient ActionManager actionManager;
	
	/**
	 * Represents a Player's Price Checker's system
	 */
	private transient PriceCheckManager priceCheckManager;
	
	/**
	 * Represents a Player's Route (movement) management system
	 */
	private transient RouteEvent routeEvent;
	
	/**
	 * Represents a Player's current Friends Chat (FC)
	 */
	private transient FriendChatsManager currentFriendChat;
	
	/**
	 * Represents a Player's Trade system
	 */
	private transient Trade trade;
	
	/**
	 * Designed to help prevent Packet Injection
	 */
	private transient IsaacKeyPair isaacKeyPair;
	
	/**
	 * Represents a Player's Pet
	 */
	private transient Pet pet;
	
	/**
	 * Represents a Player's Vars management system
	 */
	private transient VarsManager varsManager;
	
	/**
	 * Represents a Player's coordinate (movement) management system
	 */
	private transient CoordsEvent coordsEvent;
	
	/**
	 * Represents a Player's current Region
	 */
	private transient Region region;
	
	/**
	 * Represents a Player's last Emote delay (used for various things)
	 */
	private transient long nextEmoteEnd;
	
	/**
	 * Represents a Player's Passive Spell management system
	 */
	private transient PassiveSpellDispatcher spellDispatcher;
	
	/**
	 * Represents a Player's queue logic packets listing
	 */
	private transient ConcurrentLinkedQueue<LogicPacket> logicPackets;
	
	/**
	 * The current skill action that is going on for this player.
	 */
	private Optional<SkillActionTask> skillAction = Optional.empty();
	
	/**
	 * Personal details & information stored for a Player
	 */
	private PlayerDetails details;
	
	/**
	 * Represents a Player's appearance management system
	 */
	private Appearance appearance;
	
	/**
	 * Represents a Player's inventory management system
	 */
	private Inventory inventory;
	
	/**
	 * Represents a Player's Equipment management system
	 */
	private Equipment equipment;
	
	/**
	 * Represents a Player's Skills management system
	 */
	private Skills skills;
	
	/**
	 * Represents a Player's Combat Definitions management system
	 */
	private CombatDefinitions combatDefinitions;
	
	/**
	 * Represents a Player's Prayer management system
	 */
	private Prayer prayer;
	
	/**
	 * Represents a Player's Bank management system
	 */
	private Bank bank;
	
	/**
	 * Represents a Player's Music management system
	 */
	private MusicsManager musicsManager;
	
	/**
	 * Represents a Player's Notes management system
	 */
	private Notes notes;
	
	/**
	 * Represents a Player's Friends Ignore management system
	 */
	private FriendsIgnores friendsIgnores;
	
	/**
	 * Represents a Player's Familiar (Summoning) management system
	 */
	private Familiar familiar;
	
	/**
	 * Represents a Player's Pet management system
	 */
	private PetManager petManager;
	
	/**
	 * The current Controller this Player is in.
	 */
	private Optional<Controller> currentController = Optional.empty();

	/**
	 * Constructs a new Player
	 * @param password
	 */
	public Player(String password) {
		super(GameConstants.START_PLAYER_LOCATION, EntityType.PLAYER);
		setHitpoints(100);
		setAppearance(new Appearance());
		setInventory(new Inventory());
		setEquipment(new Equipment());
		setSkills(new Skills());
		setCombatDefinitions(new CombatDefinitions());
		setPrayer(new Prayer());
		setBank(new Bank());
		setMusicsManager(new MusicsManager());
		setNotes(new Notes());
		setFriendsIgnores(new FriendsIgnores());
		setPetManager(new PetManager());
		setDetails(new PlayerDetails());
		setSpellDispatcher(new PassiveSpellDispatcher());
		getDetails().setPassword(password);
		setCurrentController(Optional.empty());
	}

	/**
	 * Logs In & creates a session with the game server
	 * @param session
	 * @param username
	 * @param displayMode
	 * @param screenWidth
	 * @param screenHeight
	 * @param isaacKeyPair
	 */
	public void init(Session session, String username, byte displayMode,
			short screenWidth, short screenHeight, IsaacKeyPair isaacKeyPair) {
		if (getDetails() == null)
			setDetails(new PlayerDetails());
		if (getPetManager() == null)
			setPetManager(new PetManager());
		if (getNotes() == null)
			setNotes(new Notes());
		setSession(session);
		setUsername(username);
		setDisplayMode(displayMode);
		setScreenWidth(screenWidth);
		setScreenHeight(screenHeight);
		setIsaacKeyPair(isaacKeyPair);
		setInterfaceManager(new InterfaceManager(this));
		setHintIconsManager(new HintIconsManager(this));
		setPriceCheckManager(new PriceCheckManager(this));
		setLocalPlayerUpdate(new LocalPlayerUpdate(this));
		setLocalNPCUpdate(new LocalNPCUpdate(this));
		setActionManager(new ActionManager(this));
		setTrade(new Trade(this));
		setVarsManager(new VarsManager(this));
		setSpellDispatcher(new PassiveSpellDispatcher());
		getAppearance().setPlayer(this);
		getInventory().setPlayer(this);
		getEquipment().setPlayer(this);
		getSkills().setPlayer(this);
		getCombatDefinitions().setPlayer(this);
		getPrayer().setPlayer(this);
		getBank().setPlayer(this);
		getMusicsManager().setPlayer(this);
		getNotes().setPlayer(this);
		getFriendsIgnores().setPlayer(this);
		getDetails().getCharges().setPlayer(this);
		getPetManager().setPlayer(this);
		setDirection((byte) Utility.getFaceDirection(0, -1));
		setTemporaryMovementType((byte) -1);
		setLogicPackets(new ConcurrentLinkedQueue<LogicPacket>());
		setSwitchItemCache(new ObjectArrayList<Byte>());
		setCurrentController(getCurrentController());
		initEntity();
		World.addPlayer(this);
		updateEntityRegion(this);
		if (GameConstants.DEBUG)
			Logger.log(this, "Initiated player: " + username + ", pass: "
					+ getDetails().getPassword());
		getSession().updateIPnPass(this);
	}

	/**
	 * Starts ingame rendering, etc..
	 */
	public void start() {
		Logger.globalLog(username, session.getIP(), new String(" has logged in."));
		loadMapRegions();
		setStarted(true);
		login();
		if (isDead())
			sendDeath(Optional.empty());
	}

	/**
	 * Resets a Player's Attributes
	 */
	@Override
	public void reset(boolean attributes) {
		super.reset(attributes);
		getInterfaceManager().refreshHitPoints();
		getHintIconsManager().removeAll();
		getSkills().restoreSkills();
		getCombatDefinitions().resetSpecialAttack();
		getPrayer().reset();
		getCombatDefinitions().resetSpells(true);
		setResting((byte) 0);
		getDetails().getPoisonImmunity().set(0);
		getDetails().setAntifireDetails(Optional.empty());
		getDetails().setRunEnergy((byte) 100);
		getAppearance().generateAppearenceData();
	}
	
	/**
	 * Handles the current Map Region state
	 */
	@Override
	public void loadMapRegions() {
		boolean wasAtDynamicRegion = isAtDynamicRegion();
		super.loadMapRegions();
		setClientLoadedMapRegion(false);
		if (isAtDynamicRegion()) {
			getPackets().sendDynamicGameScene(!isStarted());
			if (!wasAtDynamicRegion)
				getLocalNPCUpdate().reset();
		} else {
			getPackets().sendGameScene(!isStarted());
			if (wasAtDynamicRegion)
				getLocalNPCUpdate().reset();
		}
		getDetails().setForceNextMapLoadRefresh(false);
	}

	/**
	 * Processes the Entities state
	 */
	@Override
	public void processEntity() {
		if (isDead())
			return;
		super.processEntity();
		getSession().processLogicPackets(this);
		if (getCoordsEvent() != null && getCoordsEvent().processEvent(this))
			setCoordsEvent(null);
		if (getRouteEvent() != null && getRouteEvent().processEvent(this))
			setRouteEvent(null);
		getActionManager().process();
		getPrayer().processPrayer();
		ControllerHandler.executeVoid(this, controller -> controller.process(this));
		getDetails().getCharges().process();
		if (getMusicsManager().musicEnded())
			getMusicsManager().replayMusic();
	}

	/**
	 * Checks if the Player needs to update their Appearance masks
	 */
	@Override
	public boolean needMasksUpdate() {
		return super.needMasksUpdate() || getTemporaryMovementType() != -1
				|| isUpdateMovementType();
	}

	/**
	 * Updates a Player's Run (movement) state 
	 */
	@Override
	public void setRun(boolean run) {
		if (run != isRun()) {
			super.setRun(run);
			setUpdateMovementType(true);
			getInterfaceManager().sendRunButtonConfig();
		}
	}

	/**
	 * Sends important information & data for login for the player to see
	 */
	public void login() {
		getDetails().setLastIP(getSession().getIP());
		getInterfaceManager().sendInterfaces();
		getPackets().sendRunEnergy().sendGameBarStages().sendGameMessage("Welcome to " + GameConstants.SERVER_NAME + ".");
		getInterfaceManager().sendRunButtonConfig();
		CombatEffect.values().parallelStream().filter(effects -> effects.onLogin(this)).forEach(effect -> World.get().submit(new CombatEffectTask(this, effect)));
		GameConstants.STAFF.entrySet().parallelStream().filter(p -> getUsername().equalsIgnoreCase(p.getKey())).forEach(staff -> getDetails().setRights(staff.getValue()));
		getInterfaceManager().sendDefaultPlayersOptions();
		checkMultiArea();
		getInventory().init();
		getEquipment().checkItems();
		getEquipment().init();
		getSkills().init();
		getCombatDefinitions().init();
		getPrayer().init();
		getFriendsIgnores().init();
		getInterfaceManager().refreshHitPoints();
		getPrayer().refreshPrayerPoints();
		getMusicsManager().init();
		getNotes().init();
		if (getFamiliar() != null)
			getFamiliar().respawnFamiliar(this);
		else
			getPetManager().init();
		setRunning(true);
		setUpdateMovementType(true);
		getAppearance().generateAppearenceData();
		ControllerHandler.executeVoid(this, controller -> controller.login(this));
		OwnedObjectManager.linkKeys(this);
		
		if (!HostManager.contains(getUsername(), HostListType.STARTER_RECEIVED)) {
			GameConstants.STATER_KIT.forEach(getInventory()::addItem);
			HostManager.add(this, HostListType.STARTER_RECEIVED, true);
			World.sendWorldMessage("[New Player] " + getDisplayName() + " has just joined " + GameConstants.SERVER_NAME);
		}
	}

	/**
	 * Checks the Multi-zone state of a Player
	 */
	@Override
	public void checkMultiArea() {
		if (!isStarted())
			return;
		boolean isAtMultiArea = isForceMultiArea() ? true : World
				.isMultiArea(this);
		if (isAtMultiArea && !isMultiArea()) {
			setMultiArea(isAtMultiArea);
			getPackets().sendGlobalConfig(616, 1);
		} else if (!isAtMultiArea && isMultiArea()) {
			setMultiArea(isAtMultiArea);
			getPackets().sendGlobalConfig(616, 0);
		}
	}

	/**
	 * Finishes the Player's Session
	 */
	@Override
	public void finish() {
		getSession().finish(this, 0);
	}

	/**
	 * Gets the Message Icon (Crown/Icon) for chat messages, interface displaying
	 * @return
	 */
	public int getMessageIcon() {
		return getDetails().getRights() == Rights.ADMINISTRATOR ? 2 : getDetails().getRights() == Rights.MODERATOR ? 1 : 0;
	}

	/**
	 * Gets the Player's Encoder Packets
	 * @return
	 */
	public WorldPacketsEncoder getPackets() {
		return getSession().getWorldPackets();

	}

	/**
	 * Drains the Run enery when the Player is Running
	 */
	public void drainRunEnergy() {
		setRunEnergy(getDetails().getRunEnergy() - 1);
	}

	/**
	 * Sets the Player's Run enegery to a specific amount
	 * @param runEnergy
	 */
	public void setRunEnergy(int runEnergy) {
		if (runEnergy < 0)
			runEnergy = 0;
		else if (runEnergy > 100)
			runEnergy = 100;
		getDetails().setRunEnergy((byte) runEnergy);
		getPackets().sendRunEnergy();
	}

	/**
	 * Checks the state of the Player's Resting state
	 * @return state
	 */
	public boolean isResting() {
		return getResting() != 0;
	}

	/**
	 * Handles an incoming Hit to the Player
	 */
	@Override
	public void handleIngoingHit(final Hit hit) {
		PlayerCombat.handleIncomingHit(this, hit);
	}
	
	/**
	 * Represents a Player's Death by various sources
	 * (Player, NPC, or neither)
	 */
	@Override
	public void sendDeath(Optional<Entity> source) {
		World.get().submit(new PlayerDeath(this));
	}

	/**
	 * Adds Logic Packets to a queue
	 * @param packet
	 */
	public void addLogicPacketToQueue(LogicPacket packet) {
		getLogicPackets().stream().filter(type -> type.getId() == packet.getId()).forEach(logical -> getLogicPackets().remove(logical));
		getLogicPackets().add(packet);
	}
	
	/**
	 * Formats the Player's username for a nicer display use
	 * @return
	 */
	public String getDisplayName() {
		return Utility.formatPlayerNameForDisplay(getUsername());
	}
	
	/**
	 * Submits & executes a Dialogue event
	 * @param listener
	 */
	public void dialog(DialogueEventListener listener){
		getAttributes().getAttributes().put("dialogue_event", listener.begin());
	}
	
	/**
	 * Gets a Dialogue event
	 */
	public DialogueEventListener dialog(){
		DialogueEventListener listener = (DialogueEventListener) getAttributes().getAttributes().get("dialogue_event");
		return listener;
	}
}