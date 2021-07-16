package com.rs.game.npc;

import java.util.Optional;

import com.rs.cache.loaders.NPCDefinitions;
import com.rs.game.Entity;
import com.rs.game.EntityType;
import com.rs.game.map.World;
import com.rs.game.map.WorldTile;
import com.rs.game.npc.combat.NPCCombat;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.npc.corp.CorporealBeast;
import com.rs.game.npc.dragons.KingBlackDragon;
import com.rs.game.npc.familiar.Familiar;
import com.rs.game.npc.global.GenericNPCDispatcher;
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
import com.rs.game.player.Hit;
import com.rs.game.player.Player;
import com.rs.game.player.controller.impl.WildernessController;
import com.rs.game.route.RouteFinder;
import com.rs.game.route.strategy.FixedTileStrategy;
import com.rs.game.task.Task;
import com.rs.net.encoders.other.Graphics;
import com.rs.utilities.RandomUtils;
import com.rs.utilities.Utility;
import com.rs.utilities.loaders.NPCBonuses;
import com.rs.utilities.loaders.NPCCombatDefinitionsL;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
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
	private short[] bonuses = NPCBonuses.getBonuses(id); // 0 stab, 1 slash, 2 crush,3 mage, 4 range, 5 stab // def,
															// blahblah till 9
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
	private transient GenericNPCDispatcher genericNPC;

	// npc masks
	private transient Transformation nextTransformation;

	public NPC(short id, WorldTile tile, byte mapAreaNameHash, boolean canBeAttackFromOutOfArea) {
		this(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, false);
	}

	public NPC(short id, WorldTile tile) {
		super(tile, EntityType.NPC);
		new NPC(id, tile, (byte) -1, false);
	}

	/*
	 * creates and adds npc
	 */
	public NPC(short id, WorldTile tile, byte mapAreaNameHash, boolean canBeAttackFromOutOfArea, boolean spawned) {
		super(tile, EntityType.NPC);
		setId(id);
		setRespawnTile(new WorldTile(tile));
		setMapAreaNameHash(mapAreaNameHash);
		setCanBeAttackFromOutOfArea(canBeAttackFromOutOfArea);
		setSpawned(spawned);
		setHitpoints(getMaxHitpoints());
		setDirection(getRespawnDirection());
		setWalkType(getDefinitions().getWalkMask());
		setCombat(new NPCCombat(this));
		setCapDamage((short) -1);
		setLureDelay((short) 12000);
		initEntity();
		World.addNPC(this);
		updateEntityRegion(this);
		loadMapRegions();
		checkMultiArea();
		setGenericNPC(new GenericNPCDispatcher());
		getGenericNPC().setAttributes(this);
	}

	@Override
	public boolean needMasksUpdate() {
		return super.needMasksUpdate() || getNextTransformation() != null;
	}

	public void setNextNPCTransformation(short id) {
		setId(id);
		setNextTransformation(new Transformation(id));
	}

	@Override
	public void resetMasks() {
		super.resetMasks();
		setNextTransformation(null);
	}

	public NPCDefinitions getDefinitions() {
		return NPCDefinitions.getNPCDefinitions(getId());
	}

	public NPCCombatDefinitions getCombatDefinitions() {
		return NPCCombatDefinitionsL.getNPCCombatDefinitions(getId());
	}

	public void processNPC() {
		if (isDead() || getMovement().isLocked())
			return;
		if (!getCombat().process() || !isForceWalking() || !isCantInteract() || !checkAgressivity()) {
			if (getMovement().getFreezeDelay() < Utility.currentTimeMillis()) {
				if (!hasWalkSteps() && (getWalkType() & NORMAL_WALK) != 0) {
					boolean can = false;
					if (RandomUtils.inclusive(2) == 0) {
						can = RandomUtils.percentageChance(30);
					}
					if (can) {
						int moveX = (int) Math.round(Math.random() * 10.0 - 5.0);
						int moveY = (int) Math.round(Math.random() * 10.0 - 5.0);
						resetWalkSteps();
						if (getMapAreaNameHash() != -1) {
							addWalkSteps(getX() + moveX, getY() + moveY, 5, (getWalkType() & FLY_WALK) == 0);
						} else
							addWalkSteps(getRespawnTile().getX() + moveX, getRespawnTile().getY() + moveY, 5,
									(getWalkType() & FLY_WALK) == 0);
					}
				}
			}
		}
		if (isForceWalking()) {
			if (getMovement().getFreezeDelay() < Utility.currentTimeMillis()) {
				if (getX() != getForceWalk().getX() || getY() != getForceWalk().getY()) {
					if (!hasWalkSteps()) {
						int steps = RouteFinder.findRoute(RouteFinder.WALK_ROUTEFINDER, getX(), getY(), getPlane(),
								getSize(), new FixedTileStrategy(getForceWalk().getX(), getForceWalk().getY()), true);
						int[] bufferX = RouteFinder.getLastPathBufferX();
						int[] bufferY = RouteFinder.getLastPathBufferY();
						for (int i = steps - 1; i >= 0; i--) {
							if (!addWalkSteps(bufferX[i], bufferY[i], 25, true))
								break;
						}
					}
					if (!hasWalkSteps()) {
						safeForceMoveTile(new WorldTile(getForceWalk()));
						setForceWalk(null);
					}
				} else
					setForceWalk(null);
			}
		}
	}

	@Override
	public void processEntity() {
		super.processEntity();
		processNPC();
		getGenericNPC().process(this);
	}

	public byte getRespawnDirection() {
		NPCDefinitions definitions = getDefinitions();
		if (definitions.getAnInt853() << 32 != 0 && definitions.getRespawnDirection() > 0
				&& definitions.getRespawnDirection() <= 8)
			return (byte) ((4 + definitions.getRespawnDirection()) << 11);
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
		getGenericNPC().handleIngoingHit(this, hit);
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
		getGenericNPC().setRespawnTask(this);
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
	public void sendDeath(Optional<Entity> source) {
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
	public void setAttackedBy(Entity target) {
		super.setAttackedBy(target);
		if (target == getCombat().getTarget() && !(getCombat().getTarget() instanceof Familiar))
			setLastAttackedByTarget(Utility.currentTimeMillis());
	}

	public boolean canBeAttackedByAutoRelatie() {
		return Utility.currentTimeMillis() - getLastAttackedByTarget() > getLureDelay();
	}

	public boolean isForceWalking() {
		return forceWalk != null;
	}

	public void setTarget(Entity entity) {
		if (isForceWalking())
			return;
		getCombat().setTarget(entity);
		setLastAttackedByTarget(Utility.currentTimeMillis());
	}

	public void forceWalkRespawnTile() {
		setForceWalk(getRespawnTile());
	}

	public void setForceWalk(WorldTile tile) {
		resetWalkSteps();
		setNextWorldTile(tile);
	}

	public ObjectArrayList<Entity> getPossibleTargets(boolean checkNPCs, boolean checkPlayers) {
		int size = getSize();
		int agroRatio = 32;
		ObjectArrayList<Entity> possibleTarget = new ObjectArrayList<Entity>();
		for (int regionId : getMapRegionsIds()) {
			if (checkPlayers) {
				ObjectArrayList<Short> playerIndexes = World.getRegion(regionId).getPlayersIndexes();
				if (playerIndexes != null) {
					for (int playerIndex : playerIndexes) {
						Player player = World.getPlayers().get(playerIndex);
						if (player.isDead() || player.isFinished() || !player.isRunning()
								|| player.getAppearance().isHidePlayer()
								|| !Utility.isOnRange(getX(), getY(), size, player.getX(), player.getY(),
										player.getSize(),
										getForceTargetDistance() > 0 ? getForceTargetDistance() : agroRatio)
								|| (!isForceMultiAttacked() && (!isMultiArea() || !player.isMultiArea())
										&& (player.getAttackedBy() != this
												&& (player.getAttackedByDelay() > Utility.currentTimeMillis()
														|| player.getFindTargetDelay() > Utility.currentTimeMillis())))
								|| !clipedProjectile(player, false)
								|| (!isForceAgressive() && !WildernessController.isAtWild(this) && player.getSkills()
										.getCombatLevelWithSummoning() >= getDefinitions().getCombatLevel() * 2))
							continue;
						possibleTarget.add(player);
					}
				}
			}
			if (checkNPCs) {
				ObjectArrayList<Short> npcsIndexes = World.getRegion(regionId).getNpcsIndexes();
				if (npcsIndexes != null) {
					for (int npcIndex : npcsIndexes) {
						NPC npc = World.getNPCs().get(npcIndex);
						if (npc == this || npc.isDead() || npc.isFinished()
								|| !Utility.isOnRange(getX(), getY(), size, npc.getX(), npc.getY(), npc.getSize(),
										getForceTargetDistance() > 0 ? getForceTargetDistance() : agroRatio)
								|| !npc.getDefinitions().hasAttackOption()
								|| ((!isMultiArea() || !npc.isMultiArea()) && npc.getAttackedBy() != this
										&& npc.getAttackedByDelay() > Utility.currentTimeMillis())
								|| !clipedProjectile(npc, false))
							continue;
						possibleTarget.add(npc);
					}
				}
			}
		}
		return possibleTarget;
	}

	public ObjectArrayList<Entity> getPossibleTargets() {
		getGenericNPC().possibleTargets(this);
		return getPossibleTargets(false, true);
	}

	public boolean checkAgressivity() {
		if (getDefinitions().hasAttackOption()) {
			if (!isForceAgressive()) {
				NPCCombatDefinitions defs = getCombatDefinitions();
				if (defs.getAgressivenessType() == NPCCombatDefinitions.PASSIVE)
					return false;
			}
		}
		ObjectArrayList<Entity> possibleTarget = getPossibleTargets();
		if (!possibleTarget.isEmpty()) {
			Entity target = possibleTarget.get(RandomUtils.random(possibleTarget.size() - 1));
			setTarget(target);
			target.setAttackedBy(target);
			target.setFindTargetDelay(Utility.currentTimeMillis() + 10000);
			return true;
		}
		return false;
	}

	public void setCantInteract(boolean cantInteract) {
		setCantInteract(cantInteract);
		if (cantInteract)
			getCombat().reset();
	}

	@Override
	public String toString() {
		return getDefinitions().getName() + " - " + id + " - " + getX() + " " + getY() + " " + getPlane();
	}

	public void transformIntoNPC(short id) {
		setId(id);
		setNextTransformation(new Transformation(id));
	}

	/**
	 * TODO: REDO ALL THIS TO THE NEW SYSTEM
	 * @param id
	 * @param tile
	 * @param mapAreaNameHash
	 * @param canBeAttackFromOutOfArea
	 * @param spawned
	 * @return
	 */
	public static final NPC spawnNPC(short id, WorldTile tile, byte mapAreaNameHash, boolean canBeAttackFromOutOfArea,
			boolean spawned) {

		NPC npcType = null;

		if (id == 1926 || id == 1931)
			npcType = new BanditCampBandits(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id == 7134)
			npcType = new Bork(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id >= 8832 && id <= 8834)
			npcType = new LivingRock(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id >= 13465 && id <= 13481)
			npcType = new Revenant(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id == 1158 || id == 1160)
			npcType = new KalphiteQueen(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id == 6261 || id == 6263 || id == 6265)
			npcType = GodWarsBosses.graardorMinions[(id - 6261) / 2] = new GodWarMinion(id, tile, mapAreaNameHash,
					canBeAttackFromOutOfArea, spawned);
		else if (id == 6260)
			npcType = new GeneralGraardor(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id == 6222)
			npcType = new KreeArra(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id == 6223 || id == 6225 || id == 6227 || id == 6081)
			npcType = GodWarsBosses.armadylMinions[(id - 6223) / 2] = new GodWarMinion(id, tile, mapAreaNameHash,
					canBeAttackFromOutOfArea, spawned);
		else if (id == 6203)
			npcType = new KrilTstsaroth(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id == 6204 || id == 6206 || id == 6208)
			npcType = GodWarsBosses.zamorakMinions[(id - 6204) / 2] = new GodWarMinion(id, tile, mapAreaNameHash,
					canBeAttackFromOutOfArea, spawned);
		else if (id == 6248 || id == 6250 || id == 6252)
			npcType = GodWarsBosses.commanderMinions[(id - 6248) / 2] = new GodWarMinion(id, tile, mapAreaNameHash,
					canBeAttackFromOutOfArea, spawned);
		else if (id == 6247)
			npcType = new CommanderZilyana(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id >= 6210 && id <= 6221)
			npcType = new GodwarsZammorakFaction(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id >= 6254 && id <= 6259)
			npcType = new GodwarsSaradominFaction(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id >= 6268 && id <= 6283)
			npcType = new GodwarsBandosFaction(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id >= 6228 && id <= 6246)
			npcType = new GodwarsArmadylFaction(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id == 1615)
			npcType = new AbyssalDemon(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
		else if (id == 50 || id == 2642)
			npcType = new KingBlackDragon(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id >= 9462 && id <= 9467)
			npcType = new Strykewyrm(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
		else if (id >= 6026 && id <= 6045)
			npcType = new Werewolf(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id == 1266 || id == 1268 || id == 2453 || id == 2886)
			npcType = new RockCrabs(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id == 8133)
			npcType = new CorporealBeast(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);

		else if (id == 1282) {
			npcType = new NPC(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
			npcType.getMovement().lock(Short.MAX_VALUE);
		} else if (id == 43 || (id >= 5156 && id <= 5164) || id == 5156 || id == 1765)
			npcType = new Sheep(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);

		else if (id == 8349 || id == 8450 || id == 8451)
			npcType = new TormentedDemon(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id == 1609 || id == 1610)
			npcType = new Kurask(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
		else if (id == 13820 || id == 13821 || id == 13822)
			npcType = new Jadinko(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
		else if (id == 1131 || id == 1132 || id == 1133 || id == 1134) {
			npcType = new NPC(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
			npcType.setForceAgressive(true);
		} else {
			npcType = new NPC(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
			npcType = new GenericNPCDispatcher().create(npcType);
		}
		return npcType;
	}

	public static final NPC spawnNPC(short id, WorldTile tile, byte mapAreaNameHash, boolean canBeAttackFromOutOfArea) {
		return spawnNPC(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, false);
	}
}