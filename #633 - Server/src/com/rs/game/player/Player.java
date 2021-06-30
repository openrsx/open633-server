package com.rs.game.player;

import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

import com.alex.utils.VarsManager;
import com.rs.GameConstants;
import com.rs.game.Entity;
import com.rs.game.EntityMovement;
import com.rs.game.EntityType;
import com.rs.game.HintIconsManager;
import com.rs.game.Hit;
import com.rs.game.World;
import com.rs.game.dialogue.DialogueEventListener;
import com.rs.game.map.Region;
import com.rs.game.npc.familiar.Familiar;
import com.rs.game.npc.others.Pet;
import com.rs.game.player.actions.ActionManager;
import com.rs.game.player.content.FriendChatsManager;
import com.rs.game.player.content.MusicsManager;
import com.rs.game.player.content.Notes;
import com.rs.game.player.content.PriceCheckManager;
import com.rs.game.player.content.pet.PetManager;
import com.rs.game.player.controllers.ControllerManager;
import com.rs.game.player.spells.passive.PassiveSpellDispatcher;
import com.rs.game.player.type.CombatEffect;
import com.rs.game.route.CoordsEvent;
import com.rs.game.route.RouteEvent;
import com.rs.game.task.Task;
import com.rs.game.task.impl.CombatEffectTask;
import com.rs.game.task.impl.SkillActionTask;
import com.rs.net.IsaacKeyPair;
import com.rs.net.LogicPacket;
import com.rs.net.Session;
import com.rs.net.encoders.WorldPacketsEncoder;
import com.rs.net.host.HostListType;
import com.rs.net.host.HostManager;
import com.rs.utilities.Logger;
import com.rs.utilities.Utility;

import it.unimi.dsi.fastutil.objects.ObjectArrayFIFOQueue;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Data;
import lombok.EqualsAndHashCode;
import skills.Skills;

@Data
@EqualsAndHashCode(callSuper=false)
public class Player extends Entity {

	private transient String username;
	private transient Session session;
	private transient boolean clientLoadedMapRegion;
	private transient byte displayMode;
	private transient short screenWidth;
	private transient short screenHeight;
	private transient byte temporaryMovementType;
	private transient boolean updateMovementType;
	private transient boolean started;
	private transient boolean running;
	private transient byte resting;
	private transient boolean canPvp;
	private transient boolean cantTrade;
	private transient Runnable closeInterfacesEvent;
	private transient long lastPublicMessage;
	private transient ObjectArrayList<Byte> switchItemCache;
	private transient boolean disableEquip;
	private transient boolean invulnerable;
	private transient boolean finishing;
	
	private transient InterfaceManager interfaceManager;
	private transient HintIconsManager hintIconsManager;
	private transient ActionManager actionManager;
	private transient PriceCheckManager priceCheckManager;
	private transient RouteEvent routeEvent;
	private transient FriendChatsManager currentFriendChat;
	private transient Trade trade;
	private transient IsaacKeyPair isaacKeyPair;
	private transient Pet pet;
	private transient VarsManager varsManager;
	private transient CoordsEvent coordsEvent; 
	private transient Region region;
	private transient long nextEmoteEnd;
	private transient PassiveSpellDispatcher spellDispatcher;
	private transient ConcurrentLinkedQueue<LogicPacket> logicPackets;
	private transient EntityMovement movement;
	
	/**
	 * The current skill action that is going on for this player.
	 */
	private Optional<SkillActionTask> skillAction = Optional.empty();
	
	/**
	 * Personal details & information stored for a Player
	 */
	private PlayerDetails details;
	private Appearance appearance;
	private Inventory inventory;
	private Equipment equipment;
	private Skills skills;
	private CombatDefinitions combatDefinitions;
	private Prayer prayer;
	private Bank bank;
	private ControllerManager controllerManager;
	private MusicsManager musicsManager;
	private Notes notes;
	private FriendsIgnores friendsIgnores;
	private Familiar familiar;
	private PetManager petManager;

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
		setControllerManager(new ControllerManager());
		setMusicsManager(new MusicsManager());
		setNotes(new Notes());
		setFriendsIgnores(new FriendsIgnores());
		setPetManager(new PetManager());
		setDetails(new PlayerDetails());
		setSpellDispatcher(new PassiveSpellDispatcher());
		getDetails().setPassword(password);
	}

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
		getControllerManager().setPlayer(this);
		getMusicsManager().setPlayer(this);
		getNotes().setPlayer(this);
		getFriendsIgnores().setPlayer(this);
		getDetails().getCharges().setPlayer(this);
		getPetManager().setPlayer(this);
		setDirection((byte) Utility.getFaceDirection(0, -1));
		setTemporaryMovementType((byte) -1);
		setLogicPackets(new ConcurrentLinkedQueue<LogicPacket>());
		setSwitchItemCache(new ObjectArrayList<Byte>());
		initEntity();
		World.addPlayer(this);
		updateEntityRegion(this);
		if (GameConstants.DEBUG)
			Logger.log(this, "Initiated player: " + username + ", pass: "
					+ getDetails().getPassword());
		getSession().updateIPnPass(this);
	}

	public void start() {
		Logger.globalLog(username, session.getIP(), new String(" has logged in."));
		loadMapRegions();
		setStarted(true);
		login();
		if (isDead())
			sendDeath(Optional.empty());
	}

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

	@Override
	public void reset() {
		reset(true);
	}
	
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

	@Override
	public void processEntityUpdate() {
		super.processEntityUpdate();
	}

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
		getControllerManager().process();
		getDetails().getCharges().process();
		if (getMusicsManager().musicEnded())
			getMusicsManager().replayMusic();
	}

	@Override
	public void processReceivedHits() {
		if (getMovement().isLocked())
			return;
		super.processReceivedHits();
	}

	@Override
	public boolean needMasksUpdate() {
		return super.needMasksUpdate() || getTemporaryMovementType() != -1
				|| isUpdateMovementType();
	}

	@Override
	public void resetMasks() {
		super.resetMasks();
		setTemporaryMovementType((byte) -1);
		setUpdateMovementType(false);
		if (!isClientLoadedMapRegion()) {
			// load objects and items here
			setClientLoadedMapRegion(true);
			World.getRegion(getRegionId()).refreshSpawnedObjects(this);
			World.getRegion(getRegionId()).refreshSpawnedItems(this);
		}
	}

	@Override
	public void setRun(boolean run) {
		if (run != isRun()) {
			super.setRun(run);
			setUpdateMovementType(true);
			getInterfaceManager().sendRunButtonConfig();
		}
	}

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
		getControllerManager().login();
		OwnedObjectManager.linkKeys(this);
		
		if (!HostManager.contains(getUsername(), HostListType.STARTER_RECEIVED)) {
			GameConstants.STATER_KIT.forEach(getInventory()::addItem);
			HostManager.add(this, HostListType.STARTER_RECEIVED, true);
			World.sendWorldMessage("[New Player] " + getDisplayName() + " has just joined " + GameConstants.SERVER_NAME, canPvp);
		}
	}

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

	@Override
	public void finish() {
		getSession().finish(this, 0);
	}

	@Override
	public boolean restoreHitPoints() {
		return super.restoreHitPoints();
	}

	@Override
	public void removeHitpoints(ObjectArrayFIFOQueue<Hit> hit) {
		super.removeHitpoints(hit);
	}

	@Override
	public int getMaxHitpoints() {
		return getSkills().getLevel(Skills.HITPOINTS) * 10
				+ getEquipment().getEquipmentHpIncrease();
	}

	public int getMessageIcon() {
		return getDetails().getRights() == Rights.ADMINISTRATOR ? 2 : getDetails().getRights() == Rights.MODERATOR ? 1 : 0;
	}

	public WorldPacketsEncoder getPackets() {
		return getSession().getWorldPackets();

	}

	public void drainRunEnergy() {
		setRunEnergy(getDetails().getRunEnergy() - 1);
	}

	public void setRunEnergy(int runEnergy) {
		if (runEnergy < 0)
			runEnergy = 0;
		else if (runEnergy > 100)
			runEnergy = 100;
		getDetails().setRunEnergy((byte) runEnergy);
		getPackets().sendRunEnergy();
	}

	public boolean isResting() {
		return getResting() != 0;
	}

	@Override
	public double getMagePrayerMultiplier() {
		return 0.6;
	}

	@Override
	public double getRangePrayerMultiplier() {
		return 0.6;
	}

	@Override
	public double getMeleePrayerMultiplier() {
		return 0.6;
	}

	@Override
	public void handleIngoingHit(final Hit hit) {
		PlayerCombat.handleIncomingHit(this, hit);
	}
	
	@Override
	public void sendDeath(Optional<Entity> source) {
		World.get().submit(new PlayerDeath(this));
	}

	@Override
	public int getSize() {
		return getAppearance().getSize();
	}

	@Override
	public void heal(int ammount, int extra) {
		super.heal(ammount, extra);
	}

	public void addLogicPacketToQueue(LogicPacket packet) {
		getLogicPackets().stream().filter(type -> type.getId() == packet.getId()).forEach(logical -> getLogicPackets().remove(logical));
		getLogicPackets().add(packet);
	}
	
	public String getDisplayName() {
		return Utility.formatPlayerNameForDisplay(getUsername());
	}

	/**
	 * Sends a delayed task for this player.
	 */
	public void task(int delay, Consumer<Player> action) {
		Player player = this;
		new Task(delay, false) {
			@Override
			protected void execute() {
				action.accept(player);
				cancel();
			}
		}.submit();
	}
	
	public void dialog(DialogueEventListener listener){
		getTemporaryAttributes().put("dialogue_event", listener.begin());
	}
	
	public DialogueEventListener dialog(){
		DialogueEventListener listener = (DialogueEventListener) getTemporaryAttributes().get("dialogue_event");
		return listener;
	}
}