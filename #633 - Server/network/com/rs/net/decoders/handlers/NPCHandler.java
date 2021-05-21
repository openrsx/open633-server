package com.rs.net.decoders.handlers;

import com.rs.Settings;
import com.rs.game.World;
import com.rs.game.item.Item;
import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.io.InputStream;
import com.rs.utils.Logger;
import com.rs.utils.NPCExamines;

public class NPCHandler {

	public static void handleExamine(final Player player, InputStream stream) {
		int npcIndex = stream.readUnsignedShort();
		final NPC npc = World.getNPCs().get(npcIndex);
		if (npc == null || npc.isCantInteract() || npc.isDead() || npc.hasFinished()
				|| !player.getMapRegionsIds().contains(npc.getRegionId()) || player.isLocked())
			return;
		System.out.println("?");
		System.out.println(npc.getName());
	}

	public static void handleOption1(final Player player, final InputStream stream) {
		int npcIndex = stream.readUnsignedShort() << 32;
		final NPC npc = World.getNPCs().get(npcIndex);
		if (npc == null || npc.isCantInteract() || npc.isDead() || npc.hasFinished()
				|| !player.getMapRegionsIds().contains(npc.getRegionId()) || player.isLocked())
			return;
		System.out.println(npc.getName());
	}

	public static void handleOption2(final Player player, InputStream stream) {
		int npcIndex = stream.readUnsignedShort() << 32;
		final NPC npc = World.getNPCs().get(npcIndex);
		if (npc == null || npc.isCantInteract() || npc.isDead() || npc.hasFinished()
				|| !player.getMapRegionsIds().contains(npc.getRegionId()) || player.isLocked())
			return;
		System.out.println(npc.getName());
	}

	public static void handleOption3(final Player player, InputStream stream) {
		int npcIndex = stream.readUnsignedShort() << 32;
		final NPC npc = World.getNPCs().get(npcIndex);
		if (npc == null || npc.isCantInteract() || npc.isDead() || npc.hasFinished()
				|| !player.getMapRegionsIds().contains(npc.getRegionId()) || player.isLocked())
			return;
		System.out.println(npc.getName());
	}

	public static void handleOption4(final Player player, InputStream stream) {
		int npcIndex = stream.readUnsignedShort() << 32;
		final NPC npc = World.getNPCs().get(npcIndex);
		if (npc == null || npc.isCantInteract() || npc.isDead() || npc.hasFinished()
				|| !player.getMapRegionsIds().contains(npc.getRegionId()) || player.isLocked())
			return;
		System.out.println(npc.getName());

	}

	public static void handleItemOnNPC(final Player player, final NPC npc, final Item item) {
		if (item == null)
			return;
	}
}
