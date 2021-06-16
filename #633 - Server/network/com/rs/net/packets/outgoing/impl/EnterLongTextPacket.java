package com.rs.net.packets.outgoing.impl;

import com.rs.game.player.Player;
import com.rs.game.player.content.pet.Pets;
import com.rs.io.InputStream;
import com.rs.net.Encrypt;
import com.rs.net.packets.outgoing.OutgoingPacket;
import com.rs.net.packets.outgoing.OutgoingPacketSignature;
import com.rs.utilities.Utils;

//TODO: Convert this packet
@OutgoingPacketSignature(packetId = -1, description = "Represents a Longer string of text used for input handling")
public class EnterLongTextPacket implements OutgoingPacket {

	@Override
	public void execute(Player player, InputStream stream) {
		if (!player.isRunning() || player.isDead())
			return;
		String value = stream.readString();
		if (value.equals(""))
			return;
		if (player.getTemporaryAttributes().remove("entering_note") == Boolean.TRUE)
			player.getNotes().add(value);
		else if (player.getTemporaryAttributes().remove("editing_note") == Boolean.TRUE)
			player.getNotes().edit(value);
		else if (player.getTemporaryAttributes().remove("change_pass") == Boolean.TRUE) {
			if (value.length() < 5 || value.length() > 15) {
				player.getPackets().sendGameMessage("Password length is limited to 5-15 characters.");
				return;
			}
			player.getDetails().setPassword(Encrypt.encryptSHA1(value));
			player.getPackets()
					.sendGameMessage("You have changed your password! Your new password is \"" + value + "\".");
		} else if (player.getTemporaryAttributes().remove("change_troll_name") == Boolean.TRUE) {
			value = Utils.formatPlayerNameForDisplay(value);
			if (value.length() < 3 || value.length() > 14) {
				player.getPackets()
						.sendGameMessage("You can't use a name shorter than 3 or longer than 14 characters.");
				return;
			}
			if (value.equalsIgnoreCase("none")) {
				player.getPetManager().setTrollBabyName(null);
			} else {
				player.getPetManager().setTrollBabyName(value);
				if (player.getPet() != null && player.getPet().getId() == Pets.TROLL_BABY.getBabyNpcId()) {
					player.getPet().setName(value);
				}
			}
		}
	}
}