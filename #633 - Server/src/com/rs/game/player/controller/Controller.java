package com.rs.game.player.controller;

import java.util.Optional;

import com.rs.game.Entity;
import com.rs.game.GameObject;
import com.rs.game.WorldTile;
import com.rs.game.item.FloorItem;
import com.rs.game.item.Item;
import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.game.player.content.Foods.Food;
import com.rs.game.player.content.Pots.Pot;

import lombok.Data;

@Data
public abstract class Controller {

	/**
	 * The string which defines the current controller.
	 */
	private final String controller;

	/**
	 * The current name of this controller.
	 */
	private final ControllerSafety safety;

	/**
	 * The current type of this controller.
	 */
	private final ControllerType type;

	public Optional<Controller> copy() {
		return Optional.empty();
	}

	public abstract void start(Player player);

	public boolean canEat(Food food) {
		return true;
	}

	public boolean canPot(Pot pot) {
		return true;
	}

	/**
	 * return process normaly
	 */
	public boolean canTakeItem(FloorItem item) {
		return true;
	}

	/**
	 * after the normal checks, extra checks, only called when you attacking
	 */
	public boolean keepCombating(Entity target) {
		return true;
	}

	public boolean canEquip(int slotId, int itemId) {
		return true;
	}

	/**
	 * after the normal checks, extra checks, only called when you start trying to
	 * attack
	 */
	public boolean canAttack(Entity target) {
		return true;
	}

	public boolean canDeleteInventoryItem(int itemId, int amount) {
		return true;
	}

	public boolean canUseItemOnItem(Item itemUsed, Item usedWith) {
		return true;
	}

	public boolean canAddInventoryItem(int itemId, int amount) {
		return true;
	}

	public boolean canPlayerOption1(Player target) {
		return true;
	}

	public boolean canPlayerOption2(Player target) {
		return true;
	}

	public boolean canPlayerOption3(Player target) {
		return true;
	}

	public boolean canPlayerOption4(Player target) {
		return true;
	}

	/**
	 * hits as ice barrage and that on multi areas
	 */
	public boolean canHit(Entity entity) {
		return true;
	}

	/**
	 * processes every game ticket, usualy not used
	 */
	public void process() {

	}

	public void moved() {

	}

	/**
	 * called once teleport is performed
	 */
	public void magicTeleported(int type) {

	}

	public void sendInterfaces() {

	}

	/**
	 * return can use script
	 */
	public boolean useDialogueScript(Object key) {
		return true;
	}

	/**
	 * return can teleport
	 */
	public boolean processMagicTeleport(WorldTile toTile) {
		return true;
	}

	/**
	 * return can teleport
	 */
	public boolean processItemTeleport(WorldTile toTile) {
		return true;
	}

	/**
	 * return can teleport
	 */
	public boolean processObjectTeleport(WorldTile toTile) {
		return true;
	}

	/**
	 * return process normaly
	 */
	public boolean processObjectClick1(GameObject object) {
		return true;
	}

	/**
	 * return process normaly
	 * 
	 * @param slotId2 TODO
	 */
	public boolean processButtonClick(int interfaceId, int componentId, int slotId, int slotId2, int packetId) {
		return true;
	}

	/**
	 * return process normaly
	 */
	public boolean processNPCClick1(NPC npc) {
		return true;
	}

	/**
	 * return process normaly
	 */
	public boolean processNPCClick2(NPC npc) {
		return true;
	}

	/**
	 * return process normaly
	 */
	public boolean processNPCClick3(NPC npc) {
		return true;
	}

	/**
	 * return process normaly
	 */
	public boolean processNPCClick4(NPC npc) {
		return true;
	}

	/**
	 * return process normaly
	 */
	public boolean processObjectClick2(GameObject object) {
		return true;
	}

	/**
	 * return process normaly
	 */
	public boolean processObjectClick3(GameObject object) {
		return true;
	}

	public boolean processObjectClick4(GameObject object) {
		return true;
	}

	public boolean processObjectClick5(GameObject object) {
		return true;
	}

	public boolean handleItemOnObject(GameObject object, Item item) {
		return true;
	}

	/**
	 * return let default death
	 */
	public boolean sendDeath() {
		return true;
	}

	/**
	 * return can move that step
	 */
	public boolean canMove(int dir) {
		return true;
	}

	/**
	 * return can set that step
	 */
	public boolean checkWalkStep(int lastX, int lastY, int nextX, int nextY) {
		return true;
	}

	/**
	 * return remove controller
	 */
	public boolean login() {
		return true;
	}

	/**
	 * return remove controller
	 */
	public boolean logout() {
		return true;
	}

	public void forceClose() {
	}

	public boolean processItemOnNPC(NPC npc, Item item) {
		return true;
	}

	public boolean canDropItem(Item item) {
		return true;
	}

	public boolean canSummonFamiliar() {
		return true;
	}

	public boolean processItemOnPlayer(Player target, Item item) {
		return true;
	}

	public void processNPCDeath(int id) {

	}

	/**
	 * Determines if {@code player} is in this minigame.
	 * 
	 * @param player the player to determine this for.
	 * @return <true> if this minigame contains the player, <false> otherwise.
	 */
	public abstract boolean contains(Player player);

	/**
	 * The enumerated type whose elements represent the minigame types.
	 * 
	 * @author lare96 <http://github.com/lare96>
	 */
	public enum ControllerType {
		NORMAL, SEQUENCED
	}

	/**
	 * The enumerated type whose elements represent the item safety of a player who
	 * is playing the minigame.
	 * 
	 * @author <a href="http://www.rune-server.org/members/stand+up/">Stand Up</a>
	 */
	public enum ControllerSafety {
		/**
		 * This safety is similar to when a player dies while he is skulled.
		 */
		DANGEROUS,
		/**
		 * /** Indicates the minigame is fully safe and no items will be lost on death
		 */
		SAFE
	}
}