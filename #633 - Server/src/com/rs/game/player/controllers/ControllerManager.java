package com.rs.game.player.controllers;

import com.rs.game.Entity;
import com.rs.game.WorldObject;
import com.rs.game.WorldTile;
import com.rs.game.item.FloorItem;
import com.rs.game.item.Item;
import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.game.player.content.Foods.Food;
import com.rs.game.player.content.Pots.Pot;
import com.rs.utils.Logger;

import lombok.Data;

@Data
public final class ControllerManager {
	
	private transient Controller controller;
	
	private transient Player player;
	private transient boolean inited;
	private Object[] lastControlerArguments;

	/**
	 * The last known controller from the Player
	 */
	private String lastController;

	public void startControler(Object key, Object... parameters) {
		if (controller != null)
			forceStop();
		controller = (Controller) (key instanceof Controller ? key : ControlerHandler.getControler(key));
		if (controller == null)
			return;
		controller.setPlayer(player);
		lastControlerArguments = parameters;
		lastController = (String) key;
		controller.start();
		inited = true;
		Logger.globalLog(player.getUsername(), player.getSession().getIP(),
				new String(" started controller: " + key.toString() + "."));
	}

	public void login() {
		if (lastController == null)
			return;
		controller = ControlerHandler.getControler(lastController);
		if (controller == null) {
			forceStop();
			return;
		}
		controller.setPlayer(player);
		if (controller.login())
			forceStop();
		else
			inited = true;
	}

	public void logout() {
		if (controller == null)
			return;
		if (controller.logout())
			forceStop();
	}

	public boolean canMove(int dir) {
		if (controller == null || !inited)
			return true;
		return controller.canMove(dir);
	}

	public boolean addWalkStep(int lastX, int lastY, int nextX, int nextY) {
		if (controller == null || !inited)
			return true;
		return controller.checkWalkStep(lastX, lastY, nextX, nextY);
	}

	public boolean canTakeItem(FloorItem item) {
		if (controller == null || !inited)
			return true;
		return controller.canTakeItem(item);
	}

	public boolean keepCombating(Entity target) {
		if (controller == null || !inited)
			return true;
		return controller.keepCombating(target);
	}

	public boolean canEquip(int slotId, int itemId) {
		if (controller == null || !inited)
			return true;
		return controller.canEquip(slotId, itemId);
	}

	public boolean canAddInventoryItem(int itemId, int amount) {
		if (controller == null || !inited)
			return true;
		return controller.canAddInventoryItem(itemId, amount);
	}

	public void trackXP(int skillId, int addedXp) {
		if (controller == null || !inited)
			return;
		controller.trackXP(skillId, addedXp);
	}

	public boolean canDeleteInventoryItem(int itemId, int amount) {
		if (controller == null || !inited)
			return true;
		return controller.canDeleteInventoryItem(itemId, amount);
	}

	public boolean canUseItemOnItem(Item itemUsed, Item usedWith) {
		if (controller == null || !inited)
			return true;
		return controller.canUseItemOnItem(itemUsed, usedWith);
	}

	public boolean canAttack(Entity entity) {
		if (controller == null || !inited)
			return true;
		return controller.canAttack(entity);
	}

	public boolean canPlayerOption1(Player target) {
		if (controller == null || !inited)
			return true;
		return controller.canPlayerOption1(target);
	}

	public boolean canPlayerOption2(Player target) {
		if (controller == null || !inited)
			return true;
		return controller.canPlayerOption2(target);
	}

	public boolean canPlayerOption3(Player target) {
		if (controller == null || !inited)
			return true;
		return controller.canPlayerOption3(target);
	}

	public boolean canPlayerOption4(Player target) {
		if (controller == null || !inited)
			return true;
		return controller.canPlayerOption4(target);
	}

	public boolean canHit(Entity entity) {
		if (controller == null || !inited)
			return true;
		return controller.canHit(entity);
	}

	public void moved() {
		if (controller == null || !inited)
			return;
		controller.moved();
	}

	public void magicTeleported(int type) {
		if (controller == null || !inited)
			return;
		player.getAppearance().setRenderEmote((short) -1);
		controller.magicTeleported(type);
	}

	public void sendInterfaces() {
		if (controller == null || !inited)
			return;
		controller.sendInterfaces();
	}

	public void process() {
		if (controller == null || !inited) {
			return;
		}
		controller.process();
	}

	public boolean sendDeath() {
		if (controller == null || !inited)
			return true;
		return controller.sendDeath();
	}

	public boolean canEat(Food food) {
		if (controller == null || !inited)
			return true;
		return controller.canEat(food);
	}

	public boolean canPot(Pot pot) {
		if (controller == null || !inited)
			return true;
		return controller.canPot(pot);
	}

	public boolean useDialogueScript(Object key) {
		if (controller == null || !inited)
			return true;
		return controller.useDialogueScript(key);
	}

	public boolean processMagicTeleport(WorldTile toTile) {
		if (controller == null || !inited)
			return true;
		return controller.processMagicTeleport(toTile);
	}

	public boolean processItemTeleport(WorldTile toTile) {
		if (controller == null || !inited)
			return true;
		return controller.processItemTeleport(toTile);
	}

	public boolean processObjectTeleport(WorldTile toTile) {
		if (controller == null || !inited)
			return true;
		return controller.processObjectTeleport(toTile);
	}

	public boolean processObjectClick1(WorldObject object) {
		if (controller == null || !inited)
			return true;
		return controller.processObjectClick1(object);
	}

	public boolean processButtonClick(int interfaceId, int componentId, int slotId, int slotId2, int packetId) {
		if (controller == null || !inited)
			return true;
		return controller.processButtonClick(interfaceId, componentId, slotId, slotId2, packetId);
	}

	public boolean processNPCClick1(NPC npc) {
		if (controller == null || !inited)
			return true;
		return controller.processNPCClick1(npc);
	}

	public boolean canSummonFamiliar() {
		if (controller == null || !inited)
			return true;
		return controller.canSummonFamiliar();
	}

	public boolean processNPCClick2(NPC npc) {
		if (controller == null || !inited)
			return true;
		return controller.processNPCClick2(npc);
	}

	public boolean processNPCClick3(NPC npc) {
		if (controller == null || !inited)
			return true;
		return controller.processNPCClick3(npc);
	}

	public boolean processNPCClick4(NPC npc) {
		if (controller == null || !inited)
			return true;
		return controller.processNPCClick4(npc);
	}

	public boolean processObjectClick2(WorldObject object) {
		if (controller == null || !inited)
			return true;
		return controller.processObjectClick2(object);
	}

	public boolean processObjectClick3(WorldObject object) {
		if (controller == null || !inited)
			return true;
		return controller.processObjectClick3(object);
	}

	public boolean processItemOnNPC(NPC npc, Item item) {
		if (controller == null || !inited)
			return true;
		return controller.processItemOnNPC(npc, item);
	}

	public boolean canDropItem(Item item) {
		if (controller == null || !inited)
			return true;
		return controller.canDropItem(item);
	}

	public void forceStop() {
		if (controller != null) {
			controller.forceClose();
			controller = null;
		}
		lastControlerArguments = null;
		lastController = null;
		inited = false;
		Logger.globalLog(player.getUsername(), player.getSession().getIP(),
				new String(" current controller has been stopped."));
	}

	public void removeControlerWithoutCheck() {
		controller = null;
		lastControlerArguments = null;
		lastController = null;
		inited = false;
		Logger.globalLog(player.getUsername(), player.getSession().getIP(),
				new String(" current controller has been stopped."));
	}

	public void setLastController(String controller, Object... args) {
		lastController = controller;
		lastControlerArguments = args;
	}

	public boolean processObjectClick4(WorldObject object) {
		if (controller == null || !inited)
			return true;
		return controller.processObjectClick4(object);
	}

	public boolean processObjectClick5(WorldObject object) {
		if (controller == null || !inited)
			return true;
		return controller.processObjectClick5(object);
	}

	public boolean handleItemOnObject(WorldObject object, Item item) {
		if (controller == null || !inited)
			return true;
		return controller.handleItemOnObject(object, item);
	}

	public boolean processItemOnPlayer(Player p2, Item item) {
		if (controller == null || !inited)
			return true;
		return controller.processItemOnPlayer(p2, item);
	}

	public void processNPCDeath(int id) {
		if (controller == null || !inited)
			return;
		controller.processNPCDeath(id);
	}
}