package com.rs.game.npc;

import java.util.ArrayList;
import java.util.List;

import com.rs.cache.loaders.NPCDefinitions;
import com.rs.game.Entity;
import com.rs.game.Graphics;
import com.rs.game.Hit;
import com.rs.game.World;
import com.rs.game.WorldTile;
import com.rs.game.npc.combat.NPCCombat;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.npc.corp.CorporealBeast;
import com.rs.game.npc.dragons.KingBlackDragon;
import com.rs.game.npc.familiar.Familiar;
import com.rs.game.npc.godwars.GodWarMinion;
import com.rs.game.npc.godwars.GodWarsBosses;
import com.rs.game.npc.godwars.armadyl.GodwarsArmadylFaction;
import com.rs.game.npc.godwars.armadyl.KreeArra;
import com.rs.game.npc.godwars.bandos.GeneralGraardor;
import com.rs.game.npc.godwars.bandos.GodwarsBandosFaction;
import com.rs.game.npc.godwars.saradomin.CommanderZilyana;
import com.rs.game.npc.godwars.saradomin.GodwarsSaradominFaction;
import com.rs.game.npc.godwars.zammorak.GodwarsZammorakFaction;
import com.rs.game.npc.godwars.zammorak.KrilTstsaroth;
import com.rs.game.npc.others.AbyssalDemon;
import com.rs.game.npc.others.BanditCampBandits;
import com.rs.game.npc.others.Bork;
import com.rs.game.npc.others.Jadinko;
import com.rs.game.npc.others.KalphiteQueen;
import com.rs.game.npc.others.Kurask;
import com.rs.game.npc.others.LivingRock;
import com.rs.game.npc.others.Revenant;
import com.rs.game.npc.others.RockCrabs;
import com.rs.game.npc.others.Sheep;
import com.rs.game.npc.others.Strykewyrm;
import com.rs.game.npc.others.TormentedDemon;
import com.rs.game.npc.others.Werewolf;
import com.rs.game.player.Player;
import com.rs.game.player.controllers.Wilderness;
import com.rs.game.route.RouteFinder;
import com.rs.game.route.strategy.FixedTileStrategy;
import com.rs.game.task.Task;
import com.rs.utilities.Utils;
import com.rs.utilities.loaders.MapAreas;
import com.rs.utilities.loaders.NPCBonuses;
import com.rs.utilities.loaders.NPCCombatDefinitionsL;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.SneakyThrows;

@Data
@EqualsAndHashCode(callSuper = false)
@NonNull
public class NPC extends Entity {

	public static byte NORMAL_WALK = 0x2, WATER_WALK = 0x4, FLY_WALK = 0x8;

	private short id;
	private WorldTile respawnTile;
	private byte mapAreaNameHash;
	private boolean canBeAttackFromOutOfArea;
	private byte walkType;
	private short[] bonuses = NPCBonuses.getBonuses(id); // 0 stab, 1 slash, 2 crush,3 mage, 4 range, 5 stab // def, blahblah till 9
	private boolean spawned;
	private transient NPCCombat combat;
	public WorldTile forceWalk;

	private long lastAttackedByTarget;
	private boolean cantInteract;
	private short capDamage;
	private short lureDelay;
	private boolean cantFollowUnderCombat;
	private boolean forceAgressive;
	private byte forceTargetDistance;
	private boolean forceFollowClose;
	private boolean forceMultiAttacked;
	private boolean noDistanceCheck;
	private boolean intelligentRouteFinder;

	// npc masks
	private transient Transformation nextTransformation;
	// name changing masks
	private String name;
	private transient boolean changedName;
	private short combatLevel;
	private transient boolean changedCombatLevel;
	private transient boolean locked;
	private transient double dropRateFactor;

	public NPC(short id, WorldTile tile, byte mapAreaNameHash, boolean canBeAttackFromOutOfArea) {
		this(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, false);
	}

	/*
	 * creates and adds npc
	 */
	public NPC(short id, WorldTile tile, byte mapAreaNameHash, boolean canBeAttackFromOutOfArea, boolean spawned) {
		super(tile);
		this.id = id;
		this.respawnTile = new WorldTile(tile);
		this.mapAreaNameHash = mapAreaNameHash;
		this.canBeAttackFromOutOfArea = canBeAttackFromOutOfArea;
		this.spawned = spawned;
		combatLevel = -1;
		setHitpoints(getMaxHitpoints());
		setDirection(getRespawnDirection());
		// int walkType = t(id);
		setWalkType(getDefinitions().walkMask);
		combat = new NPCCombat(this);
		capDamage = -1;
		lureDelay = 12000;
		// npc is inited on creating instance
		initEntity();
		World.addNPC(this);
		updateEntityRegion(this);
		// npc is started on creating instance
		loadMapRegions();
		checkMultiArea();
	}
	
	@Override
	public boolean needMasksUpdate() {
		return super.needMasksUpdate() || nextTransformation != null || getCustomName() != null
				|| getCustomCombatLevel() >= 0 /*
												 * * changedName
												 */;
	}

	public void setNextNPCTransformation(short id) {
		setNPC(id);
		nextTransformation = new Transformation(id);
		if (getCustomCombatLevel() != -1)
			changedCombatLevel = true;
		if (getCustomName() != null)
			changedName = true;
	}

	public void setNPC(short id) {
		this.id = id;
	}

	@Override
	public void resetMasks() {
		super.resetMasks();
		nextTransformation = null;
		changedCombatLevel = false;
		changedName = false;

	}

	public NPCDefinitions getDefinitions() {
		return NPCDefinitions.getNPCDefinitions(id);
	}

	public NPCCombatDefinitions getCombatDefinitions() {
		return NPCCombatDefinitionsL.getNPCCombatDefinitions(id);
	}

	@Override
	public int getMaxHitpoints() {
		return getCombatDefinitions().getHitpoints();
	}
	
	public void processNPC() {
		if (isDead() || locked)
			return;
		if (!combat.process()) {
			if (!isForceWalking()) {
				if (!cantInteract) {
					if (!checkAgressivity()) {
						if (getFreezeDelay() < Utils.currentTimeMillis()) {
							if (!hasWalkSteps() && (walkType & NORMAL_WALK) != 0) {
								boolean can = false;
								for (int i = 0; i < 2; i++) {
									if (Math.random() * 1000.0 < 100.0) {
										can = true;
										break;
									}
								}

								if (can) {
									int moveX = (int) Math.round(Math.random() * 10.0 - 5.0);
									int moveY = (int) Math.round(Math.random() * 10.0 - 5.0);
									resetWalkSteps();
									if (getMapAreaNameHash() != -1) {
										if (!MapAreas.isAtArea(getMapAreaNameHash(), this)) {
											forceWalkRespawnTile();
											return;
										}
										// fly walk noclips for now, nothing
										// uses it anyway
										addWalkSteps(getX() + moveX, getY() + moveY, 5, (walkType & FLY_WALK) == 0);
									} else
										addWalkSteps(respawnTile.getX() + moveX, respawnTile.getY() + moveY, 5,
												(walkType & FLY_WALK) == 0);
								}

							}
						}
					}
				}
			}
		}
		if (isForceWalking()) {
			if (getFreezeDelay() < Utils.currentTimeMillis()) {
				if (getX() != forceWalk.getX() || getY() != forceWalk.getY()) {
					if (!hasWalkSteps()) {
						int steps = RouteFinder.findRoute(RouteFinder.WALK_ROUTEFINDER, getX(), getY(), getPlane(),
								getSize(), new FixedTileStrategy(forceWalk.getX(), forceWalk.getY()), true);
						int[] bufferX = RouteFinder.getLastPathBufferX();
						int[] bufferY = RouteFinder.getLastPathBufferY();
						for (int i = steps - 1; i >= 0; i--) {
							if (!addWalkSteps(bufferX[i], bufferY[i], 25, true))
								break;
						}
					}
					if (!hasWalkSteps()) { // failing finding route
						setNextWorldTile(new WorldTile(forceWalk));
						forceWalk = null;
					}
				} else
					forceWalk = null;
			}
		}
	}

	@Override
	public void processEntity() {
		super.processEntity();
		processNPC();
	}

	public byte getRespawnDirection() {
		NPCDefinitions definitions = getDefinitions();
		if (definitions.anInt853 << 32 != 0 && definitions.respawnDirection > 0 && definitions.respawnDirection <= 8)
			return (byte) ((4 + definitions.respawnDirection) << 11);
		return 0;
	}

	public void sendSoulSplit(final Hit hit, final Entity user) {
		final NPC target = this;
		if (hit.getDamage() > 0)
			World.sendProjectile(user, this, 2263, 11, 11, 20, 5, 0, 0);
		user.heal(hit.getDamage() / 5);
		World.get().submit(new Task(1) {
			@Override
			protected void execute() {
				setNextGraphics(new Graphics(2264));
				if (hit.getDamage() > 0)
					World.sendProjectile(target, user, 2263, 11, 11, 20, 5, 0, 0);
				this.cancel();
			}
		});
	}

	@Override
	public void handleIngoingHit(final Hit hit) {
		getCombatDefinitions().handleIngoingHit(this, hit);
	}

	@Override
	public void reset() {
		super.reset();
		setDirection(getRespawnDirection());
		getCombat().reset();
		setForceWalk(null);
	}

	@Override
	public void finish() {
		if (isFinished())
			return;
		setFinished(true);
		updateEntityRegion(this);
		World.removeNPC(this);
	}

	@SneakyThrows(Throwable.class)
	public void setRespawnTask() {
		if (!isFinished()) {
			reset();
			setLocation(getRespawnTile());
			finish();
		}
		World.get().submit(new Task(getCombatDefinitions().getRespawnDelay()) {
			@Override
			protected void execute() {
				spawn();
				this.cancel();
			}
		});
	}

	public void spawn() {
		setFinished(false);
		World.addNPC(this);
		setLastRegionId((short) 0);
		updateEntityRegion(this);
		loadMapRegions();
		checkMultiArea();
	}
	
	@Override
	public void sendDeath(final Entity source) {
		World.get().submit(new NPCDeath(this));
	}

	@SneakyThrows(Exception.class)
	public void drop() {
		Player killer = getMostDamageReceivedSourcePlayer();
		if (killer == null)
			return;
		DropManager.dropItems(killer, this);
	}

	@Override
	public int getSize() {
		return getDefinitions().getSize();
	}
	
	@Override
	public double getMagePrayerMultiplier() {
		return 0;
	}

	@Override
	public double getRangePrayerMultiplier() {
		return 0;
	}

	@Override
	public double getMeleePrayerMultiplier() {
		return 0;
	}

	@Override
	public void setAttackedBy(Entity target) {
		super.setAttackedBy(target);
		if (target == combat.getTarget() && !(combat.getTarget() instanceof Familiar))
			lastAttackedByTarget = Utils.currentTimeMillis();
	}

	public boolean canBeAttackedByAutoRelatie() {
		return Utils.currentTimeMillis() - lastAttackedByTarget > lureDelay;
	}

	public boolean isForceWalking() {
		return forceWalk != null;
	}

	public void setTarget(Entity entity) {
		if (isForceWalking()) // if force walk not gonna get target
			return;
		combat.setTarget(entity);
		lastAttackedByTarget = Utils.currentTimeMillis();
	}

	public void forceWalkRespawnTile() {
		setForceWalk(respawnTile);
	}

	public void setForceWalk(WorldTile tile) {
		resetWalkSteps();
		forceWalk = tile;
	}

	public boolean hasForceWalk() {
		return forceWalk != null;
	}

	public ArrayList<Entity> getPossibleTargets(boolean checkNPCs, boolean checkPlayers) {
		int size = getSize();
		int agroRatio = 32;
		ArrayList<Entity> possibleTarget = new ArrayList<Entity>();
		for (int regionId : getMapRegionsIds()) {
			if (checkPlayers) {
				List<Integer> playerIndexes = World.getRegion(regionId).getPlayerIndexes();
				if (playerIndexes != null) {
					for (int playerIndex : playerIndexes) {
						Player player = World.getPlayers().get(playerIndex);
						if (player.isDead() || player.isFinished() || !player.isRunning()
								|| player.getAppearance().isHidden()
								|| !Utils.isOnRange(getX(), getY(), size, player.getX(), player.getY(),
										player.getSize(), forceTargetDistance > 0 ? forceTargetDistance : agroRatio)
								|| (!forceMultiAttacked && (!isMultiArea() || !player.isMultiArea())
										&& (player.getAttackedBy() != this
												&& (player.getAttackedByDelay() > Utils.currentTimeMillis()
														|| player.getFindTargetDelay() > Utils.currentTimeMillis())))
								|| !clipedProjectile(player, false) || (!forceAgressive && !Wilderness.isAtWild(this)
										&& player.getSkills().getCombatLevelWithSummoning() >= getCombatLevel() * 2))
							continue;
						possibleTarget.add(player);
					}
				}
			}
			if (checkNPCs) {
				List<Integer> npcsIndexes = World.getRegion(regionId).getNPCsIndexes();
				if (npcsIndexes != null) {
					for (int npcIndex : npcsIndexes) {
						NPC npc = World.getNPCs().get(npcIndex);
						if (npc == this || npc.isDead() || npc.isFinished()
								|| !Utils.isOnRange(getX(), getY(), size, npc.getX(), npc.getY(), npc.getSize(),
										forceTargetDistance > 0 ? forceTargetDistance : agroRatio)
								|| !npc.getDefinitions().hasAttackOption()
								|| ((!isMultiArea() || !npc.isMultiArea()) && npc.getAttackedBy() != this
										&& npc.getAttackedByDelay() > Utils.currentTimeMillis())
								|| !clipedProjectile(npc, false))
							continue;
						possibleTarget.add(npc);
					}
				}
			}
		}
		return possibleTarget;
	}

	public ArrayList<Entity> getPossibleTargets() {
		return getPossibleTargets(false, true);
	}

	public boolean checkAgressivity() {
		if (!(Wilderness.isAtWild(this) && getDefinitions().hasAttackOption())) {
			if (!forceAgressive) {
				NPCCombatDefinitions defs = getCombatDefinitions();
				if (defs.getAgressivenessType() == NPCCombatDefinitions.PASSIVE)
					return false;
			}
		}
		ArrayList<Entity> possibleTarget = getPossibleTargets();
		if (!possibleTarget.isEmpty()) {
			Entity target = possibleTarget.get(Utils.random(possibleTarget.size()));
			setTarget(target);
			target.setAttackedBy(target);
			target.setFindTargetDelay(Utils.currentTimeMillis() + 10000);
			return true;
		}
		return false;
	}

	public void setCantInteract(boolean cantInteract) {
		this.cantInteract = cantInteract;
		if (cantInteract)
			combat.reset();
	}

	@Override
	public String toString() {
		return getDefinitions().getName() + " - " + id + " - " + getX() + " " + getY() + " " + getPlane();
	}

	public String getCustomName() {
		return name;
	}

	public void setName(String string) {
		this.name = getDefinitions().getName().equals(string) ? null : string;
		changedName = true;
	}

	public int getCustomCombatLevel() {
		return combatLevel;
	}

	public short getCombatLevel() {
		return (short) (combatLevel >= 0 ? combatLevel : getDefinitions().combatLevel);
	}

	public String getName() {
		return name != null ? name : getDefinitions().getName();
	}

	public void setCombatLevel(short level) {
		combatLevel = getDefinitions().combatLevel == level ? -1 : level;
		changedCombatLevel = true;
	}

	public boolean withinDistance(Player tile, int distance) {
		return super.withinDistance(tile, distance);
	}
	
	public void transformIntoNPC(short id) {
		setNPC(id);
		nextTransformation = new Transformation(id);
	}
	
	public static final NPC spawnNPC(short id, WorldTile tile, byte mapAreaNameHash, boolean canBeAttackFromOutOfArea,
			boolean spawned) {
		NPC n = null;

		if (id == 1926 || id == 1931)
			n = new BanditCampBandits(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id == 7134)
			n = new Bork(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id >= 8832 && id <= 8834)
			n = new LivingRock(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id >= 13465 && id <= 13481)
			n = new Revenant(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id == 1158 || id == 1160)
			n = new KalphiteQueen(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id == 6261 || id == 6263 || id == 6265)
			n = GodWarsBosses.graardorMinions[(id - 6261) / 2] = new GodWarMinion(id, tile, mapAreaNameHash,
					canBeAttackFromOutOfArea, spawned);
		else if (id == 6260)
			n = new GeneralGraardor(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id == 6222)
			n = new KreeArra(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id == 6223 || id == 6225 || id == 6227 || id == 6081)
			n = GodWarsBosses.armadylMinions[(id - 6223) / 2] = new GodWarMinion(id, tile, mapAreaNameHash,
					canBeAttackFromOutOfArea, spawned);
		else if (id == 6203)
			n = new KrilTstsaroth(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id == 6204 || id == 6206 || id == 6208)
			n = GodWarsBosses.zamorakMinions[(id - 6204) / 2] = new GodWarMinion(id, tile, mapAreaNameHash,
					canBeAttackFromOutOfArea, spawned);
		else if (id == 6248 || id == 6250 || id == 6252)
			n = GodWarsBosses.commanderMinions[(id - 6248) / 2] = new GodWarMinion(id, tile, mapAreaNameHash,
					canBeAttackFromOutOfArea, spawned);
		else if (id == 6247)
			n = new CommanderZilyana(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id >= 6210 && id <= 6221)
			n = new GodwarsZammorakFaction(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id >= 6254 && id <= 6259)
			n = new GodwarsSaradominFaction(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id >= 6268 && id <= 6283)
			n = new GodwarsBandosFaction(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id >= 6228 && id <= 6246)
			n = new GodwarsArmadylFaction(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id == 1615)
			n = new AbyssalDemon(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
		else if (id == 50 || id == 2642)
			n = new KingBlackDragon(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id >= 9462 && id <= 9467)
			n = new Strykewyrm(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
		else if (id >= 6026 && id <= 6045)
			n = new Werewolf(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id == 1266 || id == 1268 || id == 2453 || id == 2886)
			n = new RockCrabs(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id == 8133)
			n = new CorporealBeast(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);

		else if (id == 1282) {
			n = new NPC(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
			n.setLocked(true);
		} else if (id == 43 || (id >= 5156 && id <= 5164) || id == 5156 || id == 1765)
			n = new Sheep(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);

		else if (id == 8349 || id == 8450 || id == 8451)
			n = new TormentedDemon(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id == 1609 || id == 1610)
			n = new Kurask(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
		else if (id == 13820 || id == 13821 || id == 13822)
			n = new Jadinko(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
		else if (id == 1131 || id == 1132 || id == 1133 || id == 1134) {
			n = new NPC(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
			n.setForceAgressive(true);
		} else
			n = new NPC(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		return n;
	}

	public static final NPC spawnNPC(short id, WorldTile tile, byte mapAreaNameHash, boolean canBeAttackFromOutOfArea) {
		return spawnNPC(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, false);
	}
}