package com.rs.game;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

import com.rs.cores.WorldThread;
import com.rs.game.player.Player;
import com.rs.game.player.content.TeleportType;
import com.rs.game.task.LinkedTaskSequence;

import lombok.Data;

/**
 * Represents a state of movement for an Entity
 * @author Dennis
 */
@Data
public class EntityMovement {
	
	/**
	 * Constructs a new movement for an Entity
	 */
	protected EntityMovement() {
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
	 * Is the Entity locked?
	 * @return state
	 */
	public boolean isLocked() {
		return getLockDelay() > WorldThread.WORLD_CYCLE;// Utils.currentTimeMillis();
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
		setLockDelay(time == -1 ? Long.MAX_VALUE : WorldThread.WORLD_CYCLE + time);
	}

	/**
	 * Unlocks the Entity from their lock state
	 */
	public void unlock() {
		setLockDelay(0);
	}

	/**
	 * Queue Teleport type handling with Consumer support
	 * @param destination
	 * @param type
	 * @param player
	 */
	public void move(Entity entity, boolean instant, WorldTile destination, TeleportType type, Consumer<Entity> consumer) {
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
	public void move(Entity entity, boolean instant, WorldTile destination, TeleportType type) {
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
	public final byte TELE_MOVE_TYPE = 127, WALK_MOVE_TYPE = 1, RUN_MOVE_TYPE = 2;
	
	/**
	 * Gets the Type state
	 * @param player
	 * @return type
	 */
	public int getMovementType(Player player) {
		if (player.getTemporaryMovementType() != -1)
			return player.getTemporaryMovementType();
		return player.isRun() ? RUN_MOVE_TYPE : WALK_MOVE_TYPE;
	}
}