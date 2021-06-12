package com.rs.plugin.impl.objects;

import java.util.stream.IntStream;

import com.rs.game.WorldObject;
import com.rs.game.player.Player;
import com.rs.plugin.listener.ObjectType;
import com.rs.plugin.wrapper.ObjectSignature;

import skills.thieving.impl.Stalls;
import skills.thieving.impl.Stalls.StallData;

@ObjectSignature(objectId = {}, name = {})
public class ThievableObjects implements ObjectType {

	/**
	 * Thieving stalls has been issue reading X / Y coordinate for some reason.
	 * Not sure why, cause rest objects are handled just fine.
	 */
	@Override
	public void execute(Player player, WorldObject object, int optionId) throws Exception {
		for(StallData data : StallData.values()) {
			Stalls stall = new Stalls(player, data, object);
			IntStream.of(data.objectId).filter(stallObject -> object.getId() == stallObject).forEach(stallObject -> stall.start());
		}
	}
}