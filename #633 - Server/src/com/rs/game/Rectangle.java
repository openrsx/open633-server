package com.rs.game;

import lombok.Data;

@Data
public class Rectangle {

	/**
	 * X of rect.
	 */
	private int x;
	/**
	 * Y of rect.
	 */
	private int y;
	/**
	 * Size X of rect.
	 */
	private int sizeX;
	/**
	 * Size Y of rect.
	 */
	private int sizeY;

	public Rectangle(int x, int y, int sizeX, int sizeY) {
		this.x = x;
		this.y = y;
		this.sizeX = sizeX;
		this.sizeY = sizeY;
	}
}