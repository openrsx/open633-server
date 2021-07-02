package com.rs.plugin.impl.objects;

import com.rs.game.GameObject;
import com.rs.game.player.Player;
import com.rs.game.player.content.WildernessObelisk;
import com.rs.plugin.listener.ObjectType;
import com.rs.plugin.wrapper.ObjectSignature;

@ObjectSignature(objectId = {}, name = {"Obelisk"})
public class WildernessObilisk implements ObjectType {

	@Override
	public void execute(Player player, GameObject object, int optionId) throws Exception {
		System.out.println("?");
		WildernessObelisk.activateObelisk(object.getId(), player);
	}
}