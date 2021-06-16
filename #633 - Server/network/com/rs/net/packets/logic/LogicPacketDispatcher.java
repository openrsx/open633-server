package com.rs.net.packets.logic;

import java.lang.annotation.Annotation;
import java.lang.annotation.IncompleteAnnotationException;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import com.rs.game.player.Player;
import com.rs.io.InputStream;
import com.rs.utilities.Utils;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import lombok.SneakyThrows;

/**
 * @author Dennis
 */
public class LogicPacketDispatcher {

	/**
	 * The object map which contains all the interface on the world.
	 */
	private static final Object2ObjectArrayMap<LogicPacketSignature, LogicPacket> PACKET = new Object2ObjectArrayMap<>();

	/**
	 * Executes the specified interface if it's registered.
	 * 
	 * @param player the player executing the interface.
	 * @param parts  the string which represents a interface.
	 */
	@SneakyThrows(Exception.class)
	public static void execute(Player player, InputStream input, int packetId) {
		Optional<LogicPacket> outgoingPacket = getVerifiedPacket(packetId);
		outgoingPacket.filter(packet -> matchesSize(packet, input.getLength())).ifPresent(packet -> packet.execute(player, input));
	}

	/**
	 * Gets a interface which matches the {@code identifier}.
	 * 
	 * @param identifier the identifier to check for matches.
	 * @return an Optional with the found value, {@link Optional#empty} otherwise.
	 */
	private static Optional<LogicPacket> getVerifiedPacket(int id) {
		for (Entry<LogicPacketSignature, LogicPacket> outgoingPacket : PACKET.entrySet()) {
			if (isPacket(outgoingPacket.getValue(), id)) {
				return Optional.of(outgoingPacket.getValue());
			}
		}
		return Optional.empty();
	}

	private static boolean isPacket(LogicPacket outgoingPacket, int packetId) {
		Annotation annotation = outgoingPacket.getClass().getAnnotation(LogicPacketSignature.class);
		LogicPacketSignature signature = (LogicPacketSignature) annotation;
		return signature.packetId() == packetId;
	}
	
	private static boolean matchesSize(LogicPacket outgoingPacket, int size) {
		Annotation annotation = outgoingPacket.getClass().getAnnotation(LogicPacketSignature.class);
		LogicPacketSignature signature = (LogicPacketSignature) annotation;
		if (signature.packetSize() != size)
			System.out.println("Invalid Packet size!");
		return signature.packetSize() == size;
	}

	/**
	 * Loads all the interface into the {@link #PACKET} list.
	 * <p>
	 * </p>
	 * <b>Method should only be called once on start-up.</b>
	 */
	public static void load() {
		List<LogicPacket> packets = Utils.getClassesInDirectory("com.rs.net.packets.logic.impl").stream()
				.map(clazz -> (LogicPacket) clazz).collect(Collectors.toList());

		for (LogicPacket outgoingPacket : packets) {
			if (outgoingPacket.getClass().getAnnotation(LogicPacketSignature.class) == null) {
				throw new IncompleteAnnotationException(LogicPacketSignature.class,
						outgoingPacket.getClass().getName() + " has no annotation.");
			}
			PACKET.put(outgoingPacket.getClass().getAnnotation(LogicPacketSignature.class), outgoingPacket);
		}
	}

	/**
	 * Reloads all the interface into the {@link #PACKET} list.
	 * <p>
	 * </p>
	 * <b>This method can be invoked on run-time to clear all the commands in the
	 * list and add them back in a dynamic fashion.</b>
	 */
	public static void reload() {
		PACKET.clear();
		load();
	}
}