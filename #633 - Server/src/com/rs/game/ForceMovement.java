package com.rs.game;

import com.rs.utilities.Utils;

import lombok.Data;

@Data
public class ForceMovement {

	public static final int NORTH = 0, EAST = 1, SOUTH = 2, WEST = 3;

	private final WorldTile toFirstTile;
	private final WorldTile toSecondTile;
	private final int firstTileTicketDelay;
	private final int secondTileTicketDelay;
	protected final int direction;

	public int getDirection() {
		switch (direction) {
		case NORTH:
			return Utils.getFaceDirection(0, 1);
		case EAST:
			return Utils.getFaceDirection(1, 0);
		case SOUTH:
			return Utils.getFaceDirection(0, -1);
		case WEST:
		default:
			return Utils.getFaceDirection(-1, 0);
		}
	}
}