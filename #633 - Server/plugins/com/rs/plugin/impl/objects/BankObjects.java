package com.rs.plugin.impl.objects;

import com.rs.game.map.GameObject;
import com.rs.game.player.Player;
import com.rs.plugin.listener.ObjectType;
import com.rs.plugin.wrapper.ObjectSignature;

@ObjectSignature(objectId = {}, name = {"Bank booth", "Bank chest"})
public class BankObjects implements ObjectType {

	@Override
	public void execute(Player player, GameObject object, int optionId) throws Exception {
		if (object.getDefinitions().containsOption("Use") || object.getDefinitions().containsOption("Use-quickly"))
			player.getBank().openBank();
		if (object.getDefinitions().containsOption("Collect")) { 
			//ge collect
		}
	}
}