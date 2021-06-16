package com.rs.net.packets.outgoing.impl;

import com.rs.GameConstants;
import com.rs.game.player.Player;
import com.rs.io.InputStream;
import com.rs.net.packets.outgoing.OutgoingPacket;
import com.rs.net.packets.outgoing.OutgoingPacketSignature;
import com.rs.plugin.CommandDispatcher;
import com.rs.utilities.Logger;

@OutgoingPacketSignature(packetId = 28, description = "A command that the Player is sending to the client")
public class CommandPacket implements OutgoingPacket {

	@Override
	public void execute(Player player, InputStream stream) {
		if (!player.isRunning())
			return;
		boolean clientCommand = stream.readUnsignedByte() == 1;
		boolean console = stream.readUnsignedByte() == 1;
		String command = stream.readString();
		if (!CommandDispatcher.processCommand(player, command, console, clientCommand)
				&& GameConstants.DEBUG)
			Logger.log(this, "Command: " + command);
	}
}