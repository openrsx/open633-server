package com.rs.net.packets.outgoing;

import java.lang.annotation.Annotation;
import java.lang.annotation.IncompleteAnnotationException;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import com.rs.game.player.Player;
import com.rs.io.InputStream;
import com.rs.net.decoders.WorldPacketsDecoder;
import com.rs.plugin.RSInterfaceDispatcher;
import com.rs.utilities.Utility;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import lombok.SneakyThrows;

/**
 * @author Dennis
 */
public class OutgoingPacketDispatcher {

	/**
	 * The object map which contains all the interface on the world.
	 */
	private static final Object2ObjectArrayMap<OutgoingPacketSignature, OutgoingPacket> PACKET = new Object2ObjectArrayMap<>();

	/**
	 * Executes the specified interface if it's registered.
	 * 
	 * @param player the player executing the interface.
	 * @param parts  the string which represents a interface.
	 */
	@SneakyThrows(Exception.class)
	public static void execute(Player player, InputStream input, int packetId) {
		Optional<OutgoingPacket> incomingPacket = getVerifiedPacket(packetId);
		incomingPacket.ifPresent(packet -> packet.execute(player, input));
		
		if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET
				|| packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET
				|| packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET
				|| packetId == WorldPacketsDecoder.ACTION_BUTTON5_PACKET
				|| packetId == WorldPacketsDecoder.ACTION_BUTTON6_PACKET
				|| packetId == WorldPacketsDecoder.ACTION_BUTTON7_PACKET
				|| packetId == WorldPacketsDecoder.ACTION_BUTTON8_PACKET
				|| packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET
				|| packetId == WorldPacketsDecoder.ACTION_BUTTON9_PACKET
				|| packetId == WorldPacketsDecoder.ACTION_BUTTON10_PACKET) 
			RSInterfaceDispatcher.handleButtons(player, input, packetId);
	}

	/**
	 * Gets a interface which matches the {@code identifier}.
	 * 
	 * @param identifier the identifier to check for matches.
	 * @return an Optional with the found value, {@link Optional#empty} otherwise.
	 */
	private static Optional<OutgoingPacket> getVerifiedPacket(int id) {
		for (Entry<OutgoingPacketSignature, OutgoingPacket> incomingPacket : PACKET.entrySet()) {
			if (isPacket(incomingPacket.getValue(), id)) {
				return Optional.of(incomingPacket.getValue());
			}
		}
		return Optional.empty();
	}

	private static boolean isPacket(OutgoingPacket incomingPacket, int packetId) {
		Annotation annotation = incomingPacket.getClass().getAnnotation(OutgoingPacketSignature.class);
		OutgoingPacketSignature signature = (OutgoingPacketSignature) annotation;
		return Arrays.stream(signature.packetId()).anyMatch(packet -> packet == packetId);
	}

	/**
	 * Loads all the interface into the {@link #PACKET} list.
	 * <p>
	 * </p>
	 * <b>Method should only be called once on start-up.</b>
	 */
	public static void load() {
		List<OutgoingPacket> packets = Utility.getClassesInDirectory("com.rs.net.packets.outgoing.impl").stream()
				.map(clazz -> (OutgoingPacket) clazz).collect(Collectors.toList());

		for (OutgoingPacket outgoingPacket : packets) {
			if (outgoingPacket.getClass().getAnnotation(OutgoingPacketSignature.class) == null) {
				throw new IncompleteAnnotationException(OutgoingPacketSignature.class,
						outgoingPacket.getClass().getName() + " has no annotation.");
			}
			PACKET.put(outgoingPacket.getClass().getAnnotation(OutgoingPacketSignature.class), outgoingPacket);
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