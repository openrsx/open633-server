package com.rs.plugin.impl.objects;

import com.rs.game.map.GameObject;
import com.rs.game.player.Player;
import com.rs.plugin.listener.ObjectType;
import com.rs.plugin.wrapper.ObjectSignature;

@ObjectSignature(objectId = {}, name = {"Bank booth"})
public class BankObjects implements ObjectType {

	@Override
	public void execute(Player player, GameObject object, int optionId) throws Exception {
		System.out.println("?");
//		System.out.println(optionId);
//		Arrays.stream(object.getDefinitions().getOptions()).filter(bankable -> bankable.equalsIgnoreCase("Use"))
//				.forEach(bankable -> player.getBank().openBank());
	}
}