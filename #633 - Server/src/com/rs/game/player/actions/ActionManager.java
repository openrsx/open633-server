package com.rs.game.player.actions;

import java.util.Optional;

import lombok.Getter;
import lombok.Setter;

/**
 * Handles an Action that a Player creates
 * @author Dennis
 *
 */
public final class ActionManager {
	
	/**
	 * The Action the Player creates
	 */
	@Getter
	private Optional<Action> action;
	
	/**
	 * The Action delay
	 */
	@Getter
	@Setter
	private int actionDelay;

	/**
	 * Handles the processing of an Action
	 */
	public void process() {
		System.out.println("?/");
		if (getAction().isPresent() && !getAction().get().process())
			forceStop();
		if (actionDelay > 0) {
			actionDelay--;
		}
		int delay = getAction().get().processWithDelay();
		if (delay == -1) {
			forceStop();
			return;
		}
		addActionDelay(actionDelay += delay);
	}

	/**
	 * Sets the Action that the Player has requested
	 * @param actionEvent
	 * @return action
	 */
	public void setAction(Action actionEvent) {
		System.out.println("??/");
		getAction().ifPresent(action -> action.stop());
		action = Optional.of(actionEvent);
	}

	/**
	 * Forcivly stops the Players current Action
	 */
	public void forceStop() {
		getAction().ifPresent(presentAction ->  {
			presentAction.stop();
		});
	}

	/**
	 * Adds an additional delay to the action delay
	 * @param delay
	 */
	public void addActionDelay(int delay) {
		this.actionDelay += delay;
	}
}