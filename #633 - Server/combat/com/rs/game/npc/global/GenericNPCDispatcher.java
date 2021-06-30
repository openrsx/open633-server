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

public final class GenericNPCDispatcher {

	private static final Object2ObjectArrayMap<GenericNPCSignature, GenericNPC> NPC = new Object2ObjectArrayMap<>();
	
	@SneakyThrows(Exception.class)
	public NPC execute(NPC npc) {
		Optional<GenericNPC> globalNPC = getVerifiedNPC(npc.getId());
		globalNPC.ifPresent(mob -> {
			Annotation annotation = mob.getClass().getAnnotation(GenericNPCSignature.class);
			GenericNPCSignature signature = (GenericNPCSignature) annotation;
			new NPC(npc.getId(), npc.getLastWorldTile(), (byte) signature.mapAreaNameHash(), signature.canBeAttackFromOutOfArea(), signature.isSpawned());
		});
		return npc;
	}
	
	@SneakyThrows(Exception.class)
	public void process(NPC npc) {
		Optional<GenericNPC> globalNPC = getVerifiedNPC(npc.getId());
		globalNPC.ifPresent(mob -> mob.process(npc));
	}
	
	@SneakyThrows(Exception.class)
	public void handleIngoingHit(NPC npc, final Hit hit) {
		Optional<GenericNPC> globalNPC = getVerifiedNPC(npc.getId());
		globalNPC.ifPresent(mob -> mob.handleIngoingHit(hit));
	}
	
	@SneakyThrows(Exception.class)
	public void setRespawnTask(NPC npc) {
		Optional<GenericNPC> globalNPC = getVerifiedNPC(npc.getId());
		globalNPC.ifPresent(mob -> mob.setRespawnTask());
	}
	
	@SneakyThrows(Exception.class)
	public void possibleTargets(NPC npc) {
		Optional<GenericNPC> globalNPC = getVerifiedNPC(npc.getId());
		globalNPC.ifPresent(mob -> mob.getPossibleTargets(npc));
	}
	
	@SneakyThrows(Exception.class)
	public void sendDeath(Optional<Entity> source) {
		Optional<GenericNPC> globalNPC = getVerifiedNPC(Optional.of(source.get().toNPC().getId()).get());
		globalNPC.ifPresent(mob -> mob.sendDeath(source));
	}
	
	private Optional<GenericNPC> getVerifiedNPC(int id) {
		for (Entry<GenericNPCSignature, GenericNPC> npc : NPC.entrySet()) {
			if (isValidID(npc.getValue(), id)) {
				return Optional.of(npc.getValue());
				
			}
		}
		return Optional.empty();
	}

	private boolean isValidID(GenericNPC genericNPC, int mobId) {
		Annotation annotation = genericNPC.getClass().getAnnotation(GenericNPCSignature.class);
		GenericNPCSignature signature = (GenericNPCSignature) annotation;
		return Arrays.stream(signature.npcId()).anyMatch(id -> mobId == id);
	}

	public static void load() {
		List<GenericNPC> mobLoader = Utility.getClassesInDirectory("com.rs.game.npc.global.impl").stream()
				.map(clazz -> (GenericNPC) clazz).collect(Collectors.toList());
		mobLoader.forEach(npcs -> NPC.put(npcs.getClass().getAnnotation(GenericNPCSignature.class), npcs));
	}

	public void reload() {
		NPC.clear();
		load();
	}
}