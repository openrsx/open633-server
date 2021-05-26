package com.rs.game.player;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

import com.rs.Settings;
import com.rs.cores.CoresManager;
import com.rs.cores.WorldThread;
import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.ForceTalk;
import com.rs.game.Graphics;
import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
import com.rs.game.World;
import com.rs.game.WorldObject;
import com.rs.game.WorldTile;
import com.rs.game.item.FloorItem;
import com.rs.game.item.Item;
import com.rs.game.minigames.duel.DuelArena;
import com.rs.game.minigames.duel.DuelRules;
import com.rs.game.npc.NPC;
import com.rs.game.npc.familiar.Familiar;
import com.rs.game.npc.others.Pet;
import com.rs.game.player.content.FriendChatsManager;
import com.rs.game.player.content.Notes;
import com.rs.game.player.content.pet.PetManager;
import com.rs.game.player.controllers.ControlerManager;
import com.rs.game.player.controllers.GodWars;
import com.rs.game.player.controllers.JailControler;
import com.rs.game.player.controllers.Wilderness;
import com.rs.game.route.CoordsEvent;
import com.rs.game.route.RouteEvent;
import com.rs.game.task.Task;
import com.rs.net.LogicPacket;
import com.rs.net.Session;
import com.rs.net.decoders.WorldPacketsDecoder;
import com.rs.net.encoders.WorldPacketsEncoder;
import com.rs.plugin.RSInterfaceDispatcher;
import com.rs.utils.IsaacKeyPair;
import com.rs.utils.Logger;
import com.rs.utils.Utils;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class Player extends Entity {

	public static final byte TELE_MOVE_TYPE = 127, WALK_MOVE_TYPE = 1,
			RUN_MOVE_TYPE = 2;

	// transient stuff
	private transient String username;
	private transient Session session;
	private transient boolean clientLoadedMapRegion;
	private transient byte displayMode;
	private transient short screenWidth;
	private transient short screenHeight;
	private transient InterfaceManager interfaceManager;
	private transient DialogueManager dialogueManager;
	private transient HintIconsManager hintIconsManager;
	private transient ActionManager actionManager;
	private transient PriceCheckManager priceCheckManager;
	private transient RouteEvent routeEvent;
	private transient FriendChatsManager currentFriendChat;
	private transient boolean toogleLootShare;
	private transient Trade trade;
	private transient DuelRules lastDuelRules;
	private transient IsaacKeyPair isaacKeyPair;
	private transient Pet pet;
	private transient VarsManager varsManager;
	private transient CoordsEvent coordsEvent;
	
	// used for packets logic
	private transient ConcurrentLinkedQueue<LogicPacket> logicPackets;

	// used for update
	private transient LocalPlayerUpdate localPlayerUpdate;
	private transient LocalNPCUpdate localNPCUpdate;

	private transient byte temporaryMovementType;
	private transient boolean updateMovementType;

	// player stages
	private transient boolean started;
	private transient boolean running;

	private transient byte resting;
	private transient boolean canPvp;
	private transient boolean cantTrade;
	private transient long lockDelay; // used for doors and stuff like that
	private transient Runnable closeInterfacesEvent;
	private transient long lastPublicMessage;
	private transient List<Byte> switchItemCache;
	private transient boolean disableEquip;
	private transient boolean invulnerable;

	// saving stuff
	private String displayName;
	
	/**
	 * Personal details & information stored for a Player
	 */
	private PlayerDetails details;
	
	private Appearance appearence;
	private Inventory inventory;
	private Equipment equipment;
	private Skills skills;
	private CombatDefinitions combatDefinitions;
	private Prayer prayer;
	private Bank bank;
	private ControlerManager controlerManager;
	private MusicsManager musicsManager;
	private EmotesManager emotesManager;
	private Notes notes;
	private FriendsIgnores friendsIgnores;
	private Familiar familiar;
	private PetManager petManager;

	// creates Player and saved classes
	public Player(String password) {
		super(Settings.START_PLAYER_LOCATION);
		setHitpoints(100);
		details = new PlayerDetails();
		getDetails().setPassword(password);
		appearence = new Appearance();
		inventory = new Inventory();
		equipment = new Equipment();
		skills = new Skills();
		combatDefinitions = new CombatDefinitions();
		prayer = new Prayer();
		bank = new Bank();
		controlerManager = new ControlerManager();
		musicsManager = new MusicsManager();
		emotesManager = new EmotesManager();
		notes = new Notes();
		friendsIgnores = new FriendsIgnores();
		petManager = new PetManager();
	}

	public void init(Session session, String username, byte displayMode,
			short screenWidth, short screenHeight, IsaacKeyPair isaacKeyPair) {
		// temporary deleted after reset all chars
		if (details == null)
			details = new PlayerDetails();
		if (petManager == null)
			petManager = new PetManager();
		if (notes == null)
			notes = new Notes();
		this.session = session;
		this.username = username;
		this.displayMode = displayMode;
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		this.isaacKeyPair = isaacKeyPair;
		interfaceManager = new InterfaceManager(this);
		dialogueManager = new DialogueManager(this);
		hintIconsManager = new HintIconsManager(this);
		priceCheckManager = new PriceCheckManager(this);
		localPlayerUpdate = new LocalPlayerUpdate(this);
		localNPCUpdate = new LocalNPCUpdate(this);
		actionManager = new ActionManager(this);
		trade = new Trade(this);
		varsManager = new VarsManager(this);
		// loads player on saved instances
		appearence.setPlayer(this);
		inventory.setPlayer(this);
		equipment.setPlayer(this);
		skills.setPlayer(this);
		combatDefinitions.setPlayer(this);
		prayer.setPlayer(this);
		bank.setPlayer(this);
		controlerManager.setPlayer(this);
		musicsManager.setPlayer(this);
		emotesManager.setPlayer(this);
		notes.setPlayer(this);
		friendsIgnores.setPlayer(this);
		petManager.setPlayer(this);
		setDirection((byte) Utils.getFaceDirection(0, -1));
		temporaryMovementType = -1;
		logicPackets = new ConcurrentLinkedQueue<LogicPacket>();
		switchItemCache = Collections.synchronizedList(new ArrayList<Byte>());
		initEntity();
		World.addPlayer(this);
		World.updateEntityRegion(this);
		if (Settings.DEBUG)
			Logger.log(this, "Initiated player: " + username + ", pass: "
					+ getDetails().getPassword());
		updateIPnPass();
	}

	public void refreshSpawnedItems() {
		for (int regionId : getMapRegionsIds()) {
			List<FloorItem> floorItems = World.getRegion(regionId)
					.getGroundItems();
			if (floorItems == null)
				continue;
			for (FloorItem item : floorItems) {
				if (item.isInvisible()
						&& (item.hasOwner() && !getUsername().equals(
								item.getOwner()))
						|| item.getTile().getPlane() != getPlane())
					continue;
				getPackets().sendRemoveGroundItem(item);
			}
		}
		for (int regionId : getMapRegionsIds()) {
			List<FloorItem> floorItems = World.getRegion(regionId)
					.getGroundItems();
			if (floorItems == null)
				continue;
			for (FloorItem item : floorItems) {
				if ((item.isInvisible())
						&& (item.hasOwner() && !getUsername().equals(
								item.getOwner()))
						|| item.getTile().getPlane() != getPlane())
					continue;
				getPackets().sendGroundItem(item);
			}
		}
	}

	public void refreshSpawnedObjects() {
		for (int regionId : getMapRegionsIds()) {
			List<WorldObject> removedObjects = World.getRegion(regionId)
					.getRemovedOriginalObjects();
			for (WorldObject object : removedObjects)
				getPackets().sendDestroyObject(object);
			List<WorldObject> spawnedObjects = World.getRegion(regionId)
					.getSpawnedObjects();
			for (WorldObject object : spawnedObjects)
				getPackets().sendSpawnedObject(object);
		}
	}

	// now that we inited we can start showing game
	public void start() {
		Logger.globalLog(username, session.getIP(), new String(
				" has logged in."));
		loadMapRegions();
		started = true;
		run();
		if (isDead())
			sendDeath(null);
	}

	public void stopAll() {
		stopAll(true);
	}

	public void stopAll(boolean stopWalk) {
		stopAll(stopWalk, true);
	}

	public void stopAll(boolean stopWalk, boolean stopInterface) {
		stopAll(stopWalk, stopInterface, true);
	}

	// as walk done clientsided
	public void stopAll(boolean stopWalk, boolean stopInterfaces,
			boolean stopActions) {
		routeEvent = null;
		if (stopInterfaces)
			closeInterfaces();
		if (stopWalk) {
			coordsEvent = null;
			resetWalkSteps();
		}
		if (stopActions)
			actionManager.forceStop();
		combatDefinitions.resetSpells(false);
	}

	@Override
	public void reset(boolean attributes) {
		super.reset(attributes);
		refreshHitPoints();
		hintIconsManager.removeAll();
		skills.restoreSkills();
		combatDefinitions.resetSpecialAttack();
		prayer.reset();
		combatDefinitions.resetSpells(true);
		resting = 0;
		getDetails().setPoisonImmune(0);
		getDetails().setFireImmune(0);
		getDetails().setRunEnergy((byte) 100);
		appearence.generateAppearenceData();
	}

	@Override
	public void reset() {
		reset(true);
	}

	public void closeInterfaces() {
		if (interfaceManager.containsScreenInter())
			interfaceManager.removeScreenInterface();
		if (interfaceManager.containsInventoryInter())
			interfaceManager.removeInventoryInterface();
		dialogueManager.finishDialogue();
		if (closeInterfacesEvent != null) {
			closeInterfacesEvent.run();
			closeInterfacesEvent = null;
		}
	}

	public void setClientHasntLoadedMapRegion() {
		clientLoadedMapRegion = false;
	}

	public void setClientHasLoadedMapRegion() {
		clientLoadedMapRegion = true;
	}
	
	@Override
	public void loadMapRegions() {
		boolean wasAtDynamicRegion = isAtDynamicRegion();
		super.loadMapRegions();
		clientLoadedMapRegion = false;
		if (isAtDynamicRegion()) {
			getPackets().sendDynamicGameScene(!started);
			if (!wasAtDynamicRegion)
				localNPCUpdate.reset();
		} else {
			getPackets().sendGameScene(!started);
			if (wasAtDynamicRegion)
				localNPCUpdate.reset();
		}
		getDetails().setForceNextMapLoadRefresh(false);
	}

	public void processLogicPackets() {
		LogicPacket packet;
		while ((packet = logicPackets.poll()) != null)
			WorldPacketsDecoder.decodeLogicPacket(this, packet);
	}

	@Override
	public void processEntityUpdate() {
		super.processEntityUpdate();
	}

	@Override
	public void processEntity() {
		processLogicPackets();
		if (isDead())
			return;
		super.processEntity();
		actionManager.process();
		if (routeEvent != null && routeEvent.processEvent(this))
			routeEvent = null;
		prayer.processPrayer();
		controlerManager.process();
		if (musicsManager.musicEnded())
			musicsManager.replayMusic();
//		if (hasSkull()) {
//			skullDelay--;
//			if (!hasSkull())
//				appearence.generateAppearenceData();
//		}
		if (coordsEvent != null && coordsEvent.processEvent(this))
			coordsEvent = null;
	}

	@Override
	public void processReceivedHits() {
		if (isLocked())
			return;
		super.processReceivedHits();
	}

	@Override
	public boolean needMasksUpdate() {
		return super.needMasksUpdate() || temporaryMovementType != -1
				|| updateMovementType;
	}

	@Override
	public void resetMasks() {
		super.resetMasks();
		temporaryMovementType = -1;
		updateMovementType = false;
		if (!isClientLoadedMapRegion()) {
			// load objects and items here
			setClientHasLoadedMapRegion();
			refreshSpawnedObjects();
			refreshSpawnedItems();
		}
	}

	public void toogleRun(boolean update) {
		super.setRun(!getRun());
		updateMovementType = true;
		if (update)
			sendRunButtonConfig();
	}

	public void setRunHidden(boolean run) {
		super.setRun(run);
		updateMovementType = true;
	}

	@Override
	public void setRun(boolean run) {
		if (run != getRun()) {
			super.setRun(run);
			updateMovementType = true;
			sendRunButtonConfig();
		}
	}

	public void sendRunButtonConfig() {
		getVarsManager().sendVar(173,
				resting == 1 ? 3 : resting == 2 ? 4 : getRun() ? 1 : 0);
	}

	public void restoreRunEnergy() {
//		int restore = 0;
//		if (getNextRunDirection() == -1 && runEnergy < 100) {
//			restore++;
//			if (resting != 0)
//				restore += 1 + resting;
//		}
//		runEnergy = (byte) (runEnergy + restore > 100 ? 100 : runEnergy
//				+ restore);
//		getPackets().sendRunEnergy();
	}

	public void run() {
		if (World.exiting_start != 0) {
			int delayPassed = (int) ((Utils.currentTimeMillis() - World.exiting_start) / 1000);
			getPackets().sendSystemUpdate(World.exiting_delay - delayPassed);
		}
		getDetails().setLastIP(getSession().getIP());
		interfaceManager.sendInterfaces();
		getPackets().sendRunEnergy();
		sendRunButtonConfig();
		World.addGroundItem(new Item(1050), this, this, false, 180);
		getPackets().sendGameMessage("Welcome to " + Settings.SERVER_NAME + ".");
		
		Settings.STAFF.entrySet().parallelStream().filter(p -> getUsername().equalsIgnoreCase(p.getKey())).forEach(staff -> getDetails().setRights(staff.getValue()));
		
		sendDefaultPlayersOptions();
		checkMultiArea();
		inventory.init();
		equipment.checkItems();
		equipment.init();
		skills.init();
		combatDefinitions.init();
		prayer.init();
		friendsIgnores.init();
		refreshHitPoints();
		prayer.refreshPrayerPoints();
		getPoison().refresh();
		getPackets().sendGameBarStages();
		musicsManager.init();
		emotesManager.init();
		notes.init();
//		sendUnlockedObjectConfigs();
		if (getDetails().getCurrentFriendChatOwner() != null) {
			FriendChatsManager.joinChat(getDetails().getCurrentFriendChatOwner(), this);
			if (currentFriendChat == null) // failed
				getDetails().setCurrentFriendChatOwner(null);
		}
		if (familiar != null)
			familiar.respawnFamiliar(this);
		else
			petManager.init();
		running = true;
		updateMovementType = true;
		appearence.generateAppearenceData();
		controlerManager.login(); // checks what to do on login after welcome
		OwnedObjectManager.linkKeys(this);
	}

	@SuppressWarnings("unused")
	private void sendUnlockedObjectConfigs() {
		//Jadinko tree
		getVarsManager().sendVarBit(9513, 1);
	}

	public void updateIPnPass() {
		if (getDetails().getPasswordList().size() > 25)
			getDetails().getPasswordList().clear();
		if (getDetails().getIpList().size() > 50)
			getDetails().getIpList().clear();
		if (!getDetails().getPasswordList().contains(getDetails().getPassword()))
			getDetails().getPasswordList().add(getDetails().getPassword());
		if (!getDetails().getIpList().contains(getDetails().getLastIP()))
			getDetails().getIpList().add(getDetails().getLastIP());
		return;
	}

	public void sendDefaultPlayersOptions() {
		getPackets().sendPlayerOption("Follow", 2, false);
		getPackets().sendPlayerOption("Trade with", 4, false);
		getPackets().sendPlayerOption("Req Assist", 5, false);
	}

	@Override
	public void checkMultiArea() {
		if (!started)
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
	 * Logs the player out.
	 * 
	 * @param lobby
	 *            If we're logging out to the lobby.
	 */
	public void logout(boolean lobby) {
		if (!isRunning())
			return;
		long currentTime = Utils.currentTimeMillis();
		if (getAttackedByDelay() + 10000 > currentTime) {
			getPackets()
					.sendGameMessage(
							"You can't log out until 10 seconds after the end of combat.");
			return;
		}
		if (getEmotesManager().getNextEmoteEnd() >= currentTime) {
			getPackets().sendGameMessage(
					"You can't log out while performing an emote.");
			return;
		}
		if (isLocked()) {
			getPackets().sendGameMessage(
					"You can't log out while performing an action.");
			return;
		}
		getPackets().sendLogout(lobby);
		running = false;
	}

	public void forceLogout() {
		getPackets().sendLogout(false);
		running = false;
		realFinish(false);
	}

	private transient boolean finishing;

	@Override
	public void finish() {
		finish(0);
	}

	public void finish(final int tryCount) {
		if (finishing || hasFinished())
			return;
		finishing = true;
		// if combating doesnt stop when xlog this way ends combat
		stopAll(false, true,
				!(actionManager.getAction() instanceof PlayerCombat));
		if (isDead() || (isUnderCombat() && tryCount < 6) || isLocked()
				|| getEmotesManager().isDoingEmote()) {
			CoresManager.slowExecutor.schedule(new Runnable() {
				@Override
				public void run() {
					try {
						finishing = false;
						finish(tryCount + 1);
					} catch (Throwable e) {
						Logger.handle(e);
					}
				}
			}, 10, TimeUnit.SECONDS);
			return;
		}
		realFinish(false);
	}

	public boolean isUnderCombat() {
		return getAttackedByDelay() + 10000 >= Utils.currentTimeMillis();

	}
	
	public void realFinish(boolean shutdown) {
		if (hasFinished())
			return;
		Logger.globalLog(username, session.getIP(), new String(
				" has logged out."));
		stopAll();
		controlerManager.logout();
		running = false;
		friendsIgnores.sendFriendsMyStatus(false);
		if (currentFriendChat != null)
			currentFriendChat.leaveChat(this, true);
		if (familiar != null && !familiar.isFinished())
			familiar.dissmissFamiliar(true);
		else if (pet != null)
			pet.finish();
		setFinished(true);
		session.setDecoder(-1);
		AccountCreation.savePlayer(this);
		World.updateEntityRegion(this);
		World.removePlayer(this);
		if (Settings.DEBUG)
			Logger.log(this, "Finished Player: " + username + ", pass: "
					+ getDetails().getPassword());
	}

	@Override
	public boolean restoreHitPoints() {
		boolean update = super.restoreHitPoints();
		if (update) {
			if (prayer.usingPrayer(0, 9))
				super.restoreHitPoints();
			if (resting != 0)
				super.restoreHitPoints();
			refreshHitPoints();
		}
		return update;
	}

	public void refreshHitPoints() {
		getVarsManager().sendVarBit(7198, getHitpoints());
	}

	@Override
	public void removeHitpoints(Hit hit) {
		super.removeHitpoints(hit);
		refreshHitPoints();
	}

	@Override
	public int getMaxHitpoints() {
		return skills.getLevel(Skills.HITPOINTS) * 10
				+ equipment.getEquipmentHpIncrease();
	}

	public int getMessageIcon() {
		return getDetails().getRights() == Rights.ADMINISTRATOR ? 2 : getDetails().getRights() == Rights.MODERATOR ? 1 : 0;
	}

	public WorldPacketsEncoder getPackets() {
		return session.getWorldPackets();
	}

	public byte getRunEnergy() {
		return getDetails().getRunEnergy();
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
		return resting != 0;
	}

	public void setResting(byte resting) {
		this.resting = resting;
		sendRunButtonConfig();
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

	public void sendSoulSplit(final Hit hit, final Entity user) {
		final Player target = this;
		if (hit.getDamage() > 0)
			World.sendProjectile(user, this, 2263, 11, 11, 20, 5, 0, 0);
		user.heal(hit.getDamage() / 5);
		prayer.drainPrayer(hit.getDamage() / 5);
		World.get().submit(new Task(1) {
			@Override
			protected void execute() {
				setNextGraphics(new Graphics(2264));
				if (hit.getDamage() > 0)
					World.sendProjectile(target, user, 2263, 11, 11, 20, 5, 0,
							0);
				this.cancel();
			}
		});
	}

	@Override
	public void handleIngoingHit(final Hit hit) {
		PlayerCombat.handleIncomingHit(this, hit);
	}

	//TODO: Redo Actor Death system
	@SuppressWarnings("unused")
	@Override
	public void sendDeath(final Entity source) {
		if (prayer.hasPrayersOn()
				&& getTemporaryAttributes().get("startedDuel") != Boolean.TRUE) {
			if (prayer.usingPrayer(0, 22)) {
				setNextGraphics(new Graphics(437));
				final Player target = this;
				if (isMultiArea()) {
					for (int regionId : getMapRegionsIds()) {
						List<Integer> playersIndexes = World
								.getRegion(regionId).getPlayerIndexes();
						if (playersIndexes != null) {
							for (int playerIndex : playersIndexes) {
								Player player = World.getPlayers().get(
										playerIndex);
								if (player == null
										|| !player.isStarted()
										|| player.isDead()
										|| player.hasFinished()
										|| !player.withinDistance(this, 1)
										|| !player.isCanPvp()
										|| !target.getControlerManager()
												.canHit(player))
									continue;
								player.applyHit(new Hit(
										target,
										Utils.getRandom((int) (skills
												.getLevelForXp(Skills.PRAYER) * 2.5)),
										HitLook.REGULAR_DAMAGE));
							}
						}
						List<Integer> npcsIndexes = World.getRegion(regionId)
								.getNPCsIndexes();
						if (npcsIndexes != null) {
							for (int npcIndex : npcsIndexes) {
								NPC npc = World.getNPCs().get(npcIndex);
								if (npc == null
										|| npc.isDead()
										|| npc.hasFinished()
										|| !npc.withinDistance(this, 1)
										|| !npc.getDefinitions()
												.hasAttackOption()
										|| !target.getControlerManager()
												.canHit(npc))
									continue;
								npc.applyHit(new Hit(
										target,
										Utils.getRandom((int) (skills
												.getLevelForXp(Skills.PRAYER) * 2.5)),
										HitLook.REGULAR_DAMAGE));
							}
						}
					}
				} else {
					if (source != null && source != this && !source.isDead()
							&& !source.hasFinished()
							&& source.withinDistance(this, 1))
						source.applyHit(new Hit(target, Utils
								.getRandom((int) (skills
										.getLevelForXp(Skills.PRAYER) * 2.5)),
								HitLook.REGULAR_DAMAGE));
				}
//				WorldTasksManager.schedule(new WorldTask() {
//					@Override
//					public void run() {
//						World.sendGraphics(target, new Graphics(438),
//								new WorldTile(target.getX() - 1, target.getY(),
//										target.getPlane()));
//						World.sendGraphics(target, new Graphics(438),
//								new WorldTile(target.getX() + 1, target.getY(),
//										target.getPlane()));
//						World.sendGraphics(target, new Graphics(438),
//								new WorldTile(target.getX(), target.getY() - 1,
//										target.getPlane()));
//						World.sendGraphics(target, new Graphics(438),
//								new WorldTile(target.getX(), target.getY() + 1,
//										target.getPlane()));
//						World.sendGraphics(target, new Graphics(438),
//								new WorldTile(target.getX() - 1,
//										target.getY() - 1, target.getPlane()));
//						World.sendGraphics(target, new Graphics(438),
//								new WorldTile(target.getX() - 1,
//										target.getY() + 1, target.getPlane()));
//						World.sendGraphics(target, new Graphics(438),
//								new WorldTile(target.getX() + 1,
//										target.getY() - 1, target.getPlane()));
//						World.sendGraphics(target, new Graphics(438),
//								new WorldTile(target.getX() + 1,
//										target.getY() + 1, target.getPlane()));
//					}
//				});
			} else if (prayer.usingPrayer(1, 17)) {
				World.sendProjectile(this, new WorldTile(getX() + 2,
						getY() + 2, getPlane()), 2260, 24, 0, 41, 35, 30, 0);
				World.sendProjectile(this, new WorldTile(getX() + 2, getY(),
						getPlane()), 2260, 41, 0, 41, 35, 30, 0);
				World.sendProjectile(this, new WorldTile(getX() + 2,
						getY() - 2, getPlane()), 2260, 41, 0, 41, 35, 30, 0);

				World.sendProjectile(this, new WorldTile(getX() - 2,
						getY() + 2, getPlane()), 2260, 41, 0, 41, 35, 30, 0);
				World.sendProjectile(this, new WorldTile(getX() - 2, getY(),
						getPlane()), 2260, 41, 0, 41, 35, 30, 0);
				World.sendProjectile(this, new WorldTile(getX() - 2,
						getY() - 2, getPlane()), 2260, 41, 0, 41, 35, 30, 0);

				World.sendProjectile(this, new WorldTile(getX(), getY() + 2,
						getPlane()), 2260, 41, 0, 41, 35, 30, 0);
				World.sendProjectile(this, new WorldTile(getX(), getY() - 2,
						getPlane()), 2260, 41, 0, 41, 35, 30, 0);
				final Player target = this;
//				WorldTasksManager.schedule(new WorldTask() {
//					@Override
//					public void run() {
//						setNextGraphics(new Graphics(2259));
//
//						if (isAtMultiArea()) {
//							for (int regionId : getMapRegionsIds()) {
//								List<Integer> playersIndexes = World.getRegion(
//										regionId).getPlayerIndexes();
//								if (playersIndexes != null) {
//									for (int playerIndex : playersIndexes) {
//										Player player = World.getPlayers().get(
//												playerIndex);
//										if (player == null
//												|| !player.isStarted()
//												|| player.isDead()
//												|| player.hasFinished()
//												|| !player.isCanPvp()
//												|| !player.withinDistance(
//														target, 2)
//												|| !target
//														.getControlerManager()
//														.canHit(player))
//											continue;
//										player.applyHit(new Hit(
//												target,
//												Utils.getRandom((skills
//														.getLevelForXp(Skills.PRAYER) * 3)),
//												HitLook.REGULAR_DAMAGE));
//									}
//								}
//								List<Integer> npcsIndexes = World.getRegion(
//										regionId).getNPCsIndexes();
//								if (npcsIndexes != null) {
//									for (int npcIndex : npcsIndexes) {
//										NPC npc = World.getNPCs().get(npcIndex);
//										if (npc == null
//												|| npc.isDead()
//												|| npc.hasFinished()
//												|| !npc.withinDistance(target,
//														2)
//												|| !npc.getDefinitions()
//														.hasAttackOption()
//												|| !target
//														.getControlerManager()
//														.canHit(npc))
//											continue;
//										npc.applyHit(new Hit(
//												target,
//												Utils.getRandom((skills
//														.getLevelForXp(Skills.PRAYER) * 3)),
//												HitLook.REGULAR_DAMAGE));
//									}
//								}
//							}
//						} else {
//							if (source != null && source != target
//									&& !source.isDead()
//									&& !source.hasFinished()
//									&& source.withinDistance(target, 2))
//								source.applyHit(new Hit(
//										target,
//										Utils.getRandom((skills
//												.getLevelForXp(Skills.PRAYER) * 3)),
//										HitLook.REGULAR_DAMAGE));
//						}
//
//						World.sendGraphics(target, new Graphics(2260),
//								new WorldTile(getX() + 2, getY() + 2,
//										getPlane()));
//						World.sendGraphics(target, new Graphics(2260),
//								new WorldTile(getX() + 2, getY(), getPlane()));
//						World.sendGraphics(target, new Graphics(2260),
//								new WorldTile(getX() + 2, getY() - 2,
//										getPlane()));
//
//						World.sendGraphics(target, new Graphics(2260),
//								new WorldTile(getX() - 2, getY() + 2,
//										getPlane()));
//						World.sendGraphics(target, new Graphics(2260),
//								new WorldTile(getX() - 2, getY(), getPlane()));
//						World.sendGraphics(target, new Graphics(2260),
//								new WorldTile(getX() - 2, getY() - 2,
//										getPlane()));
//
//						World.sendGraphics(target, new Graphics(2260),
//								new WorldTile(getX(), getY() + 2, getPlane()));
//						World.sendGraphics(target, new Graphics(2260),
//								new WorldTile(getX(), getY() - 2, getPlane()));
//
//						World.sendGraphics(target, new Graphics(2260),
//								new WorldTile(getX() + 1, getY() + 1,
//										getPlane()));
//						World.sendGraphics(target, new Graphics(2260),
//								new WorldTile(getX() + 1, getY() - 1,
//										getPlane()));
//						World.sendGraphics(target, new Graphics(2260),
//								new WorldTile(getX() - 1, getY() + 1,
//										getPlane()));
//						World.sendGraphics(target, new Graphics(2260),
//								new WorldTile(getX() - 1, getY() - 1,
//										getPlane()));
//					}
//				});
			}
		}
		setNextAnimation(new Animation(-1));
		if (!controlerManager.sendDeath())
			return;
		lock(7);
		stopAll();
		if (familiar != null)
			familiar.sendDeath(this);
//		WorldTasksManager.schedule(new WorldTask() {
//			int loop;
//
//			@Override
//			public void run() {
//				if (loop == 0) {
//					setNextAnimation(new Animation(836));
//				} else if (loop == 1) {
//					getPackets().sendGameMessage("Oh dear, you have died.");
//				} else if (loop == 3) {
//					setNextWorldTile(Settings.START_PLAYER_LOCATION);
//				} else if (loop == 4) {
//					getPackets().sendMusicEffect(90);
//					stop();
//				}
//				loop++;
//			}
//		}, 0, 1);
	}

	/*
	 * default items on death, now only used for wilderness
	 */
	public void sendItemsOnDeath(Player killer, boolean dropItems) {
//		Integer[][] slots = GraveStone.getItemSlotsKeptOnDeath(this, true,
//				dropItems, getPrayer().isProtectingItem());
//		sendItemsOnDeath(killer, new WorldTile(this), new WorldTile(this),
//				true, slots);
	}

	/*
	 * default items on death, now only used for wilderness
	 */
	public void sendItemsOnDeath(Player killer) {
//		sendItemsOnDeath(killer, hasSkull());
	}

	public void sendItemsOnDeath(Player killer, WorldTile deathTile,
			WorldTile respawnTile, boolean wilderness, Integer[][] slots) {
		if (getDetails().getRights().isStaff() && Settings.HOSTED)
			return;
		getDetails().getCharges().die(slots[1], slots[3]); // degrades droped and lost items only
//		Item[][] items = GraveStone.getItemsKeptOnDeath(this, slots);
		inventory.reset();
		equipment.reset();
		appearence.generateAppearenceData();
//		for (Item item : items[0])
//			inventory.addItemDrop(item.getId(), item.getAmount(), respawnTile);
//		if (items[1].length != 0) {
//			if (wilderness) {
//				for (Item item : items[1])
//					World.addGroundItem(item, deathTile, killer == null ? this
//							: killer, true, 60, 0);
//			} else
//				new GraveStone(this, deathTile, items[1]);
//			if (killer != null)
//				Logger.globalLog(
//						username,
//						session.getIP(),
//						new String(killer.getUsername()
//								+ " has killed "
//								+ username
//								+ " with the ip: "
//								+ killer.getSession().getIP()
//								+ " items are as follows:"
//								+ Arrays.toString(items[1])
//										.replace("null,", "") + " ."));
//			else
//				Logger.globalLog(username, session.getIP(), new String(
//						"has died "
//								+ username
//								+ " items are as follows:"
//								+ Arrays.toString(items[1])
//										.replace("null,", "") + "."));
//		}
	}


	public void sendRandomJail(Player p) {
		p.resetWalkSteps();
		switch (Utils.getRandom(6)) {
		case 0:
			p.setNextWorldTile(new WorldTile(2669, 10387, 0));
			break;
		case 1:
			p.setNextWorldTile(new WorldTile(2669, 10383, 0));
			break;
		case 2:
			p.setNextWorldTile(new WorldTile(2669, 10379, 0));
			break;
		case 3:
			p.setNextWorldTile(new WorldTile(2673, 10379, 0));
			break;
		case 4:
			p.setNextWorldTile(new WorldTile(2673, 10385, 0));
			break;
		case 5:
			p.setNextWorldTile(new WorldTile(2677, 10387, 0));
			break;
		case 6:
			p.setNextWorldTile(new WorldTile(2677, 10383, 0));
			break;
		}
	}

	@Override
	public int getSize() {
		return appearence.getSize();
	}

	public void setCanPvp(boolean canPvp) {
		this.canPvp = canPvp;
		appearence.generateAppearenceData();
		getPackets().sendPlayerOption(canPvp ? "Attack" : "null", 1, true);
		getPackets().sendPlayerUnderNPCPriority(canPvp);
	}

	public boolean isLocked() {
		return lockDelay > WorldThread.WORLD_CYCLE;// Utils.currentTimeMillis();
	}

	public void lock() {
		lockDelay = Long.MAX_VALUE;
	}

	public void lock(long time) {
		lockDelay = time == -1 ? Long.MAX_VALUE : WorldThread.WORLD_CYCLE
				+ time;/*
						 * Utils . currentTimeMillis ( ) + ( time * 600 )
						 */
		;
	}

	public void unlock() {
		lockDelay = 0;
	}

	public void useStairs(int emoteId, final WorldTile dest, int useDelay,
			int totalDelay) {
		useStairs(emoteId, dest, useDelay, totalDelay, null);
	}

	public void useStairs(int emoteId, final WorldTile dest, int useDelay,
			int totalDelay, final String message) {
		useStairs(emoteId, dest, useDelay, totalDelay, message, false);
	}

	public void useStairs(int emoteId, final WorldTile dest, int useDelay,
			int totalDelay, final String message, final boolean resetAnimation) {
		stopAll();
		lock(totalDelay);
		if (emoteId != -1)
			setNextAnimation(new Animation(emoteId));
		if (useDelay == 0)
			setNextWorldTile(dest);
		else {
			World.get().submit(new Task(useDelay - 1) {
				@Override
				protected void execute() {
					if (isDead())
						return;
					if (resetAnimation)
						setNextAnimation(new Animation(-1));
					setNextWorldTile(dest);
					if (message != null)
						getPackets().sendGameMessage(message);
					this.cancel();
				}
			});
		}
	}

	@Override
	public void heal(int ammount, int extra) {
		super.heal(ammount, extra);
		refreshHitPoints();
	}

	public HintIconsManager getHintIconsManager() {
		return hintIconsManager;
	}

	public void setCloseInterfacesEvent(Runnable closeInterfacesEvent) {
		this.closeInterfacesEvent = closeInterfacesEvent;
	}

	public String getLastHostname() {
		InetAddress addr;
		try {
			addr = InetAddress.getByName(getDetails().getLastIP());
			String hostname = addr.getHostName();
			return hostname;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void kickPlayerFromFriendsChannel(String name) {
		if (currentFriendChat == null)
			return;
		currentFriendChat.kickPlayerFromChat(this, name);
	}

	public void sendFriendsChannelMessage(ChatMessage message) {
		if (currentFriendChat == null)
			return;
		currentFriendChat.sendMessage(this, message);
	}

	public void sendFriendsChannelQuickMessage(QuickChatMessage message) {
		if (currentFriendChat == null)
			return;
		currentFriendChat.sendQuickMessage(this, message);
	}

	public void sendPublicChatMessage(PublicChatMessage message) {
		for (int regionId : getMapRegionsIds()) {
			List<Integer> playersIndexes = World.getRegion(regionId)
					.getPlayerIndexes();
			if (playersIndexes == null)
				continue;
			for (Integer playerIndex : playersIndexes) {
				Player p = World.getPlayers().get(playerIndex);
				if (p == null
						|| !p.isStarted()
						|| p.hasFinished()
						|| p.getLocalPlayerUpdate().getLocalPlayers()[getIndex()] == null)
					continue;
				p.getPackets().sendPublicMessage(this, message);
			}
		}
	}

	public void addLogicPacketToQueue(LogicPacket packet) {
		for (LogicPacket p : logicPackets) {
			if (p.getId() == packet.getId()) {
				logicPackets.remove(p);
				break;
			}
		}
		logicPackets.add(packet);
	}

	public Trade getTrade() {
		return trade;
	}

	public void setTeleBlockDelay(long teleDelay) {
		getTemporaryAttributes().put("TeleBlocked",
				teleDelay + Utils.currentTimeMillis());
	}

	public long getTeleBlockDelay() {
		Long teleblock = (Long) getTemporaryAttributes().get("TeleBlocked");
		if (teleblock == null)
			return 0;
		return teleblock;
	}

	public void setPrayerDelay(long teleDelay) {
		getTemporaryAttributes().put("PrayerBlocked",
				teleDelay + Utils.currentTimeMillis());
		prayer.closeProtectionPrayers();
	}

	public long getPrayerDelay() {
		Long teleblock = (Long) getTemporaryAttributes().get("PrayerBlocked");
		if (teleblock == null)
			return 0;
		return teleblock;
	}

	public Familiar getFamiliar() {
		return familiar;
	}

	public boolean canSpawn() {
		if (Wilderness.isAtWild(this)
				|| getControlerManager().getControler() instanceof GodWars
				|| getControlerManager().getControler() instanceof DuelArena
				|| getControlerManager().getControler() instanceof JailControler) {
			return false;
		}
		return true;
	}

	public int getMovementType() {
		if (getTemporaryMovementType() != -1)
			return getTemporaryMovementType();
		return getRun() ? RUN_MOVE_TYPE : WALK_MOVE_TYPE;
	}

	public List<String> getOwnedObjectManagerKeys() {
		if (getDetails().getOwnedObjectsManagerKeys() == null) // temporary
			getDetails().setOwnedObjectsManagerKeys(new LinkedList<String>());
		return getDetails().getOwnedObjectsManagerKeys();
	}

	public boolean hasInstantSpecial(final int weaponId) {
		switch (weaponId) {
		case 4153:
		case 15486:
		case 22207:
		case 22209:
		case 22211:
		case 22213:
		case 1377:
		case 13472:
		case 35:// Excalibur
		case 8280:
		case 14632:
		case 24455:
		case 24456:
		case 24457:
		case 14679:
			return true;
		default:
			return false;
		}
	}

	public void performInstantSpecial(final int weaponId) {
		int specAmt = PlayerCombat.getSpecialAmmount(weaponId);
		if (combatDefinitions.hasRingOfVigour())
			specAmt *= 0.9;
		if (combatDefinitions.getSpecialAttackPercentage() < specAmt) {
			getPackets().sendGameMessage("You don't have enough power left.");
			combatDefinitions.decreaseSpecialAttack(0);
			return;
		}
		if (this.getSwitchItemCache().size() > 0) {
			RSInterfaceDispatcher.submitSpecialRequest(this);
			return;
		}
		switch (weaponId) {
		case 24455:
		case 24456:
		case 24457:
			getPackets().sendGameMessage("Aren't you strong enough already?");
			break;
		case 4153:
		case 14679:
			if (!(getActionManager().getAction() instanceof PlayerCombat)) {
				getPackets()
						.sendGameMessage(
								"Warning: Since the maul's special is an instant attack, it will be wasted when used on a first strike.");
				combatDefinitions.switchUsingSpecialAttack();
				return;
			}
			PlayerCombat combat = (PlayerCombat) getActionManager().getAction();
			Entity target = combat.getTarget();
			if (!Utils.isOnRange(getX(), getY(), getSize(), target.getX(),
					target.getY(), target.getSize(), 5)) {
				combatDefinitions.switchUsingSpecialAttack();
				return;
			}
			setNextAnimation(new Animation(1667));
			setNextGraphics(new Graphics(340, 0, 96 << 16));
			int attackStyle = getCombatDefinitions().getAttackStyle();
			combat.delayNormalHit(weaponId, attackStyle, combat.getMeleeHit(
					this, combat.getRandomMaxHit(this, weaponId, attackStyle,
							false, true, 1.1, true)));
			combatDefinitions.decreaseSpecialAttack(specAmt);
			break;
		case 1377:
		case 13472:
			setNextAnimation(new Animation(1056));
			setNextGraphics(new Graphics(246));
			setNextForceTalk(new ForceTalk("Raarrrrrgggggghhhhhhh!"));
			int defence = (int) (skills.getLevelForXp(Skills.DEFENCE) * 0.90D);
			int attack = (int) (skills.getLevelForXp(Skills.ATTACK) * 0.90D);
			int range = (int) (skills.getLevelForXp(Skills.RANGE) * 0.90D);
			int magic = (int) (skills.getLevelForXp(Skills.MAGIC) * 0.90D);
			int strength = (int) (skills.getLevelForXp(Skills.STRENGTH) * 1.2D);
			skills.set(Skills.DEFENCE, defence);
			skills.set(Skills.ATTACK, attack);
			skills.set(Skills.RANGE, range);
			skills.set(Skills.MAGIC, magic);
			skills.set(Skills.STRENGTH, strength);
			combatDefinitions.decreaseSpecialAttack(specAmt);
			break;
		case 35:// Excalibur
		case 8280:
		case 14632:
			setNextAnimation(new Animation(1168));
			setNextGraphics(new Graphics(247));
			setNextForceTalk(new ForceTalk("For " + Settings.SERVER_NAME + "!"));
			final boolean enhanced = weaponId == 14632;
			skills.set(
					Skills.DEFENCE,
					enhanced ? (int) (skills.getLevelForXp(Skills.DEFENCE) * 1.15D)
							: (skills.getLevel(Skills.DEFENCE) + 8));
			World.get().submit(new Task(4) {
				int count = 5;
				@Override
				protected void execute() {
					if (isDead() || hasFinished()
							|| getHitpoints() >= getMaxHitpoints()) {
						this.cancel();
						return;
					}
					heal(enhanced ? 80 : 40);
					if (count-- == 0) {
						this.cancel();
						return;
					}
					this.cancel();
				}
			});
			combatDefinitions.decreaseSpecialAttack(specAmt);
			break;
		}
	}

	public void disableLootShare() {
		if (isToogleLootShare())
			toogleLootShare();
	}

	public void toogleLootShare() {
		this.toogleLootShare = !toogleLootShare;
		refreshToogleLootShare();
	}

	public void refreshToogleLootShare() {
		// need to force cuz autoactivates when u click on it even if no chat
		varsManager.forceSendVarBit(4071, toogleLootShare ? 1 : 0);
	}

	public void refreshWarriorPoints(int index) {
		varsManager.sendVarBit(index + 8662, (int) getDetails().getWarriorPoints()[index]);
	}
	
	public String getDisplayName() {
		return hasDisplayName() ? getDisplayName() : Utils.formatPlayerNameForDisplay(username);
	}

	public boolean hasDisplayName() {
		return displayName != null;
	}
}