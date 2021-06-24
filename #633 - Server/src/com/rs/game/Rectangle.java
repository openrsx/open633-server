package com.rs.game;

import lombok.Data;

@Data
public class Rectangle {

	/**
	 * X of rect.
	 */
	private final int x;
	/**
	 * Y of rect.
	 */
	private final int y;
	/**
	 * Size X of rect.
	 */
	private final int sizeX;
	/**
	 * Size Y of rect.
	 */
	private final int sizeY;
}