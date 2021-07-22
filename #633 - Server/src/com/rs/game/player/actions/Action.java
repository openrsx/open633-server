package com.rs.game.player.actions;

import java.util.Optional;

import com.rs.game.Entity;
import com.rs.game.player.Player;

import lombok.Getter;

/**
 * Represents an Action a Player creates
 * @author Dennis
 *
 */
public abstract class Action {

	/**
	 * The Player
	 */
	@Getter
	private Player player;
	
	/**
	 * The Target the {@link #player} will be interacting with
	 */
	@Getter
	private Optional<Entity> target;
	
	public Action(Player player) {
		this.player = player;
	}
	
	public Action(Player player, Optional<Entity> target) {
		this.player = player;
		this.target = target;
	}
	
	/**
	 * Starts the Action
	 * @return action
	 */
    public abstract boolean start();

    /**
     * Processes the Action in real time
     * @return process
     */
    public abstract boolean process();

    /**
     * Process the Action with a fixed delay
     * @return fixed delayed process
     */
    public abstract int processWithDelay();

    /**
     * Stops the Action that's currently being performed
     */
    public abstract void stop();
}