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
		int npcIndex = stream.readUnsignedShort128();
		boolean forceRun = stream.read128Byte() == 1;
		if (forceRun)
			player.setRun(forceRun);
		final NPC npc = World.getNPCs().get(npcIndex);
		if (npc == null || npc.hasFinished() || !player.getMapRegionsIds().contains(npc.getRegionId()))
			return;
		player.getPackets().sendNPCMessage(0, 15263739, npc, NPCExamines.getExamine(npc));
		player.getPackets().sendResetMinimapFlag();
		if (Settings.DEBUG)
			Logger.log("NPCHandler", "examined npc: " + npcIndex + ", " + npc.getId());
	}

	public static void handleOption1(final Player player, final InputStream stream) {
		int npcIndex = stream.readUnsignedShort128();
		boolean forceRun = stream.read128Byte() == 1;
		final NPC npc = World.getNPCs().get(npcIndex);
		if (npc == null || npc.isCantInteract() || npc.isDead() || npc.hasFinished()
				|| !player.getMapRegionsIds().contains(npc.getRegionId()) || player.isLocked())
			return;
		player.stopAll();
		if (forceRun)
			player.setRun(forceRun);
	}

	public static void handleOption2(final Player player, InputStream stream) {
		int npcIndex = stream.readUnsignedShort128();
		boolean forceRun = stream.read128Byte() == 1;
		final NPC npc = World.getNPCs().get(npcIndex);
		if (npc == null || npc.isCantInteract() || npc.isDead() || npc.hasFinished()
				|| !player.getMapRegionsIds().contains(npc.getRegionId()) || player.isLocked())
			return;
		player.stopAll();
		if (forceRun)
			player.setRun(forceRun);
	}

	public static void handleOption3(final Player player, InputStream stream) {
		int npcIndex = stream.readUnsignedShort128();
		boolean forceRun = stream.read128Byte() == 1;
		final NPC npc = World.getNPCs().get(npcIndex);
		if (npc == null || npc.isCantInteract() || npc.isDead() || npc.hasFinished()
				|| !player.getMapRegionsIds().contains(npc.getRegionId()) || player.isLocked())
			return;
		player.stopAll(false);
		if (forceRun)
			player.setRun(forceRun);
		if (Settings.DEBUG)
			System.out.println("cliked 3 at npc id : " + npc.getId() + ", " + npc.getX() + ", " + npc.getY() + ", "
					+ npc.getPlane());
	}

	public static void handleOption4(final Player player, InputStream stream) {
		int npcIndex = stream.readUnsignedShort128();
		boolean forceRun = stream.read128Byte() == 1;
		final NPC npc = World.getNPCs().get(npcIndex);
		if (npc == null || npc.isCantInteract() || npc.isDead() || npc.hasFinished()
				|| !player.getMapRegionsIds().contains(npc.getRegionId()) || player.isLocked())
			return;
		player.stopAll(false);
		if (forceRun)
			player.setRun(forceRun);
	
		if (Settings.DEBUG)
			System.out.println("cliked 4 at npc id : " + npc.getId() + ", " + npc.getX() + ", " + npc.getY() + ", "
					+ npc.getPlane());

	}

	public static void handleItemOnNPC(final Player player, final NPC npc, final Item item) {
		if (item == null)
			return;
	}
}
