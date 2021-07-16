package com.rs.game.player.content.pet;

import java.util.HashMap;
import java.util.Map;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.item.Item;
import com.rs.game.item.ItemConstants;
<<<<<<< HEAD
<<<<<<< HEAD
import com.rs.game.npc.other.Pet;
=======
import com.rs.game.npc.others.Pet;
>>>>>>> parent of 03d7d9c7 (Removed Old NPC classes)
=======
import com.rs.game.npc.others.Pet;
>>>>>>> parent of 03d7d9c7 (Removed Old NPC classes)
import com.rs.game.player.Player;
import com.rs.net.encoders.other.Animation;
import com.rs.net.encoders.other.ForceTalk;

import lombok.Data;

/**
 * The pet manager.
 * 
 * @author Emperor
 * 
 */
@Data
public final class PetManager {

	/**
	 * The pet details mapping, sorted by item id.
	 */
	private final Map<Integer, PetDetails> petDetails = new HashMap<Integer, PetDetails>();

	/**
	 * The player.
	 */
	private transient Player player;

	/**
	 * The current NPC id.
	 */
	private int npcId;

	/**
	 * The current item id.
	 */
	private int itemId;

	/**
	 * The troll baby's name (if any).
	 */
	private String trollBabyName;

	/**
	 * Constructs a new {@code PetManager} {@code Object}.
	 */
	public PetManager() {
		/*
		 * empty.
		 */
	}

	/**
	 * Spawns a pet.
	 * 
	 * @param itemId     The item id.
	 * @param deleteItem If the item should be removed.
	 * @return {@code True} if we were dealing with a pet item id.
	 */
	public boolean spawnPet(int itemId, boolean deleteItem) {
		Pets pets = Pets.forId(itemId);
		if (pets == null) {
			return false;
		}
		if (player.getPet() != null || player.getFamiliar() != null) {
			player.getPackets().sendGameMessage("You already have a follower.");
			return true;
		}
		int baseItemId = pets.getBabyItemId();
		PetDetails details = petDetails.get(baseItemId);
		if (details == null) {
			details = new PetDetails(pets.getGrowthRate() == 0.0 ? 100.0 : 0.0);
			petDetails.put(baseItemId, details);
		}
		int id = pets.getItemId(details.getStage());
		if (itemId != id) {
			player.getPackets().sendGameMessage("This is not the right pet, grow the pet correctly.");
			return true;
		}
		short npcId = (short) pets.getNpcId(details.getStage());
		if (npcId > 0) {
			Pet pet = new Pet(npcId, itemId, player, player, details);
			this.npcId = npcId;
			this.itemId = itemId;
			pet.setGrowthRate(pets.getGrowthRate());
			player.setPet(pet);
			if (deleteItem) {
				player.setNextAnimation(new Animation(827));
				player.getInventory().deleteItem(itemId, 1);
			}
			return true;
		}
		return true;
	}

	/**
	 * Initializes the pet manager.
	 */
	public void init() {
		if (npcId > 0 && itemId > 0) {
			spawnPet(itemId, false);
		}
	}

	/**
	 * Makes the pet eat.
	 * 
	 * @param foodId The food item id.
	 * @param npc    The pet NPC.
	 */
	public void eat(int foodId, Pet npc) {
		Pets pets = Pets.forId(itemId);
		if (pets == null) {
			return;
		}
		if (pets == Pets.TROLL_BABY) {
			if (!ItemConstants.isTradeable(new Item(foodId))) {
				npc.setNextForceTalk(new ForceTalk("I no like that!"));
				return;
			}
			if (trollBabyName == null) {
				trollBabyName = ItemDefinitions.getItemDefinitions(foodId).getName();
				npc.getDefinitions().setName(trollBabyName);
				npc.setNextForceTalk(new ForceTalk("YUM! Me likes " + trollBabyName + "!"));
			}
			player.getInventory().deleteItem(foodId, 1);
			npc.getDetails().updateHunger(-15.0);
			player.getPackets().sendGameMessage(
					"Your pet happily eats the " + ItemDefinitions.getItemDefinitions(foodId).getName() + ".");
			return;
		}
		for (int food : pets.getFood()) {
			if (food == foodId) {
				player.getInventory().deleteItem(food, 1);
				player.getPackets().sendGameMessage(
						"Your pet happily eats the " + ItemDefinitions.getItemDefinitions(food).getName() + ".");
				player.setNextAnimation(new Animation(827));
				npc.getDetails().updateHunger(-15.0);
				return;
			}
		}
		player.getPackets().sendGameMessage("Nothing interesting happens.");
	}

	/**
	 * Removes the details for this pet.
	 * 
	 * @param npcId The item id of the pet.
	 */
	public void removeDetails(int itemId) {
		Pets pets = Pets.forId(itemId);
		if (pets == null) {
			return;
		}
		petDetails.remove(pets.getBabyItemId());
	}
}