package com.rs.game.npc.global;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import com.rs.game.Entity;
import com.rs.game.Hit;
import com.rs.game.npc.NPC;
import com.rs.utilities.Utility;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import lombok.SneakyThrows;

public class GenericNPCDispatcher {

	private static final Object2ObjectArrayMap<GenericNPCSignature, GenericNPC> NPC = new Object2ObjectArrayMap<>();
	
	@SneakyThrows(Exception.class)
	public NPC execute(NPC npc) {
		getVerifiedNPC(npc.getId()).ifPresent(mob -> {
			Annotation annotation = mob.getClass().getAnnotation(GenericNPCSignature.class);
			GenericNPCSignature signature = (GenericNPCSignature) annotation;
			Arrays.stream(signature.npcId()).parallel().filter(id -> npc.getId() == id).forEach(mobId -> new NPC((short) mobId, npc.getNextWorldTile(), (byte) signature.mapAreaNameHash(), signature.canBeAttackFromOutOfArea(), signature.isSpawned()) );
		});
		return npc;
	}
	
	public void process(NPC npc) {
		getVerifiedNPC(npc.getId()).ifPresent(mob -> mob.process(npc));
	}
	
	public void handleIngoingHit(NPC npc, final Hit hit) {
		getVerifiedNPC(npc.getId()).ifPresent(mob -> mob.handleIngoingHit(hit));
	}
	
	public void setRespawnTask(NPC npc) {
		getVerifiedNPC(npc.getId()).ifPresent(mob -> mob.setRespawnTask());
	}
	
	public void possibleTargets(NPC npc) {
		getVerifiedNPC(npc.getId()).ifPresent(mob -> mob.getPossibleTargets(npc));
	}
	
	public void sendDeath(Optional<Entity> source) {
		getVerifiedNPC(Optional.of(source.get().toNPC().getId()).get()).ifPresent(mob -> mob.sendDeath(source));
	}
	
	public void setAttributes(NPC npc) {
		getVerifiedNPC(npc.getId()).ifPresent(mob -> mob.setAttributes(npc));
	}
	
	private Optional<GenericNPC> getVerifiedNPC(int id) {
		for (Entry<GenericNPCSignature, GenericNPC> npc : NPC.entrySet()) {
			return isValidID(npc.getValue(), id) ? Optional.of(npc.getValue()) : Optional.empty();
		}
		return Optional.empty();
	}

	private boolean isValidID(GenericNPC genericNPC, int mobId) {
		Annotation annotation = genericNPC.getClass().getAnnotation(GenericNPCSignature.class);
		GenericNPCSignature signature = (GenericNPCSignature) annotation;
		return Arrays.stream(signature.npcId()).anyMatch(id -> mobId == id);
	}

	public static void load() {
		List<GenericNPC> mobLoader = Utility.getClassesInDirectory("com.rs.game.npc.global.impl").stream().map(clazz -> (GenericNPC) clazz).collect(Collectors.toList());
		mobLoader.forEach(npcs -> NPC.put(npcs.getClass().getAnnotation(GenericNPCSignature.class), npcs));
	}
}