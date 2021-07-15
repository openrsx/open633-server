package com.rs.game;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

import com.rs.GameConstants;
import com.rs.game.map.WorldTile;
import com.rs.game.player.content.TeleportType;
import com.rs.game.task.LinkedTaskSequence;
import com.rs.utilities.Utility;

import lombok.Data;

/**
 * Represents a state of movement for an Entity
 * @author Dennis
 */
@Data
public class EntityMovement {
	
	/**
	 * Represents an Entity
	 */
	private final Entity entity;
	
	/**
	 * Constructs a new movement for an Entity
	 */
	protected EntityMovement(Entity entity) {
		this.entity = entity;
		walkSteps = new ConcurrentLinkedQueue<Object[]>();
	}
	
	/**
	 * A collection of queue'd walking steps done via Entity
	 */
	private transient ConcurrentLinkedQueue<Object[]> walkSteps;
	
	/**
	 * The Lock delay for an Entity
	 */
	private transient long lockDelay;
	
	/**
	 * The Frozen block delay for an Entity
	 */
	private transient long frozenBlocked;

	/**
	 * Is the Entity locked?
	 * @return state
	 */
	public boolean isLocked() {
		return getLockDelay() > GameConstants.WORLD_CYCLE_MS;// Utils.currentTimeMillis();
	}

	/**
	 * Locks the Entity
	 */
	public void lock() {
		setLockDelay(Long.MAX_VALUE);
	}

	/**
	 * Specifies a Lock timer
	 * @param time
	 */
	public void lock(long time) {
		setLockDelay(time == -1 ? Long.MAX_VALUE : GameConstants.WORLD_CYCLE_MS + time);
	}

	/**
	 * Unlocks the Entity from their lock state
	 */
	public void unlock() {
		setLockDelay(0);
	}
	
	/**
	 * Represents a Frozen state of Delay for an Entity
	 */
	private transient long freezeDelay;
	
	/**
	 * Checks if the Entity is frozen
	 * @return
	 */
	public boolean isFrozen() {
		return getFreezeDelay() >= Utility.currentTimeMillis();
	}

	/**
	 * Adds a Frozen Blocked delay to the Entity
	 * @param time
	 */
	public void addFrozenBlockedDelay(int time) {
		setFrozenBlocked(time + Utility.currentTimeMillis());
	}

	/**
	 * Adds a Freeze delay state to the Entity
	 * @param time
	 */
	public void addFreezeDelay(long time) {
		addFreezeDelay(time, false);
	}

	/**
	 * Adds a Freeze delay state with optional message to an Entity
	 * @param time
	 * @param entangleMessage
	 */
	public void addFreezeDelay(long time, boolean entangleMessage) {
		long currentTime = Utility.currentTimeMillis();
		if (currentTime > getFreezeDelay()) {
			if (getEntity().isPlayer()) {
				if (!entangleMessage)
					getEntity().toPlayer().getPackets().sendGameMessage("You have been frozen.");
				if (getEntity().toPlayer().getCurrentController().isPresent())
					time /= 2;
			}
		}
		getEntity().resetWalkSteps();
		setFreezeDelay(time + currentTime);
	}

	/**
	 * Queue Teleport type handling with Consumer support
	 * @param destination
	 * @param type
	 * @param player
	 */
	public void move(boolean instant, WorldTile destination, TeleportType type, Consumer<Entity> consumer) {
		lock();
		LinkedTaskSequence seq = new LinkedTaskSequence(instant ? 0 : 1, instant);
		seq.connect(1, () -> {
			type.getStartAnimation().ifPresent(entity::setNextAnimation);
			type.getStartGraphic().ifPresent(entity::setNextGraphics);
		}).connect(type.getEndDelay(), () -> {
			type.getEndAnimation().ifPresent(entity::setNextAnimation);
			type.getEndGraphic().ifPresent(entity::setNextGraphics);
			entity.setNextWorldTile(destination);
			consumer.accept(entity);
			unlock();
		}).start();
	}
	
	/**
	 * Queue Teleport type handling
	 * @param destination
	 * @param type
	 * @param entity
	 */
	public void move(boolean instant, WorldTile destination, TeleportType type) {
		lock();
		LinkedTaskSequence seq = new LinkedTaskSequence(instant ? 0 : 1, instant);
		seq.connect(1, () -> {
			type.getStartAnimation().ifPresent(entity::setNextAnimation);
			type.getStartGraphic().ifPresent(entity::setNextGraphics);
		}).connect(type.getEndDelay(), () -> {
			type.getEndAnimation().ifPresent(entity::setNextAnimation);
			type.getEndGraphic().ifPresent(entity::setNextGraphics);
			entity.setNextWorldTile(destination);
			unlock();
		}).start();
	}
	
	/**
	 * Movement Type states
	 */
	private final byte TELE_MOVE_TYPE = 127, WALK_MOVE_TYPE = 1, RUN_MOVE_TYPE = 2;
	
	/**
	 * Stops a Player with additional specified attributes.
	 * @param player
	 */
	public void stopAll() {
		stopAll(true);
	}

	/**
	 * Stops a Player with additional specified attributes.
	 * @param player
	 * @param stopWalk
	 */
	public void stopAll(boolean stopWalk) {
		stopAll(stopWalk, true);
	}

	/**
	 * Stops a Player with additional specified attributes.
	 * @param player
	 * @param stopWalk
	 * @param stopInterface
	 */
	public void stopAll(boolean stopWalk, boolean stopInterface) {
		stopAll(stopWalk, stopInterface, true);
	}

	/**
	 * Stops a Player with additional specified attributes.
	 * @param player
	 * @param stopWalk
	 * @param stopInterfaces
	 * @param stopActions
	 */
	public void stopAll(boolean stopWalk, boolean stopInterfaces,
			boolean stopActions) {
		getEntity().toPlayer().setRouteEvent(null);
		if (stopInterfaces)
			getEntity().toPlayer().getInterfaceManager().closeInterfaces();
		if (stopWalk) {
			getEntity().toPlayer().setCoordsEvent(null);
			getEntity().toPlayer().resetWalkSteps();
		}
		if (stopActions)
			getEntity().toPlayer().getActionManager().forceStop();
		getEntity().toPlayer().getCombatDefinitions().resetSpells(false);
	}
}