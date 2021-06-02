package com.rs.net.decoders;

import com.rs.GameConstants;
import com.rs.game.World;
import com.rs.game.player.Player;
import com.rs.io.InputStream;
import com.rs.net.AccountCreation;
import com.rs.net.Session;
import com.rs.utils.AntiFlood;
import com.rs.utils.Encrypt;
import com.rs.utils.IsaacKeyPair;
import com.rs.utils.Logger;
import com.rs.utils.Utils;

import lombok.Synchronized;

public final class LoginPacketsDecoder extends Decoder {

	private static final Object LOGIN_LOCK = new Object();

	public LoginPacketsDecoder(Session session) {
		super(session);
	}

	@Override
	public void decode(InputStream stream) {
		session.setDecoder(-1);
		if (World.exiting_start != 0) {
			session.getLoginPackets().sendClientPacket(14);
			return;
		}
		int packetId = stream.readUnsignedByte();
		int packetSize = stream.readUnsignedShort();
		if (packetSize != stream.getRemaining()) {
			session.getChannel().close();
			return;
		}
		if (stream.readInt() != GameConstants.CLIENT_REVISION) {
			session.getLoginPackets().sendClientPacket(6);
			return;
		}
		if (packetId == 16 || packetId == 18) // 16 world login
			decodeWorldLogin(stream);
		else {
			if (GameConstants.DEBUG)
				Logger.log(this, "PacketId " + packetId);
			session.getChannel().close();
		}
	}

	@SuppressWarnings("unused")
	@Synchronized("LOGIN_LOCK")
	public void decodeWorldLogin(InputStream stream) {

		int rsaBlockSize = stream.readUnsignedShort();

		if (rsaBlockSize > stream.getRemaining()) {
			session.getLoginPackets().sendClientPacket(10);
			return;
		}
		byte[] data = new byte[rsaBlockSize];
		stream.readBytes(data, 0, rsaBlockSize);
		InputStream rsaStream = new InputStream(
				Utils.cryptRSA(data, GameConstants.PRIVATE_EXPONENT, GameConstants.MODULUS));
		if (rsaStream.readUnsignedByte() != 10) {
			session.getLoginPackets().sendClientPacket(10);
			return;
		}
		int[] isaacKeys = new int[4];
		for (int i = 0; i < isaacKeys.length; i++)
			isaacKeys[i] = rsaStream.readInt();
		if (rsaStream.readLong() != 0L) { // rsa block check, pass part
			session.getLoginPackets().sendClientPacket(10);
			return;
		}
		String password = rsaStream.readString();
		if (password.length() > 30 || password.length() < 3) {
			session.getLoginPackets().sendClientPacket(3);
			return;
		}

		password = Encrypt.encryptSHA1(password);
		rsaStream.readLong(); // random value
		rsaStream.readLong(); // random value

		stream.decodeXTEA(isaacKeys, stream.getOffset(), stream.getLength());
		String username = Utils.formatPlayerNameForProtocol(stream.readString());
		int idk = stream.readUnsignedByte();
		byte displayMode = (byte) stream.readUnsignedByte();
		short screenWidth = (short) stream.readUnsignedShort();
		short screenHeight = (short) stream.readUnsignedShort();
		stream.readUnsignedByte(); // ?
		stream.skip(24); // 24bytes directly from a file, no idea whats there
		String settings = stream.readString();
		int affid = stream.readInt();
		stream.skip(stream.readUnsignedByte()); // useless settings

		if (Utils.invalidAccountName(username)) {
			session.getLoginPackets().sendClientPacket(3);
			return;
		}

		boolean isMasterPassword = GameConstants.ALLOW_MASTER_PASSWORD
				&& password.equals(Encrypt.encryptSHA1(GameConstants.MASTER_PASSWORD));

		Player player;
		if (World.getPlayers().size() >= GameConstants.PLAYERS_LIMIT - 10) {
			session.getLoginPackets().sendClientPacket(7);
			return;
		}
		if (!isMasterPassword && World.containsPlayer(username).isPresent()) {
			session.getLoginPackets().sendClientPacket(5);
			return;
		}
		if (AntiFlood.getSessionsIP(session.getIP()) > 6) {
			session.getLoginPackets().sendClientPacket(9);
			return;
		}
		if (!AccountCreation.exists(username)) {
			player = new Player(password);
		} else {
			player = AccountCreation.loadPlayer(username);
			if (player == null) {
				session.getLoginPackets().sendClientPacket(20);
				return;
			}
			if (!password.equals(player.getDetails().getPassword())) {
				session.getLoginPackets().sendClientPacket(3);
				return;
			}
		}
		if (!isMasterPassword && (player.getDetails().isPermBanned()
				|| player.getDetails().getBanned() > Utils.currentTimeMillis())) {
			session.getLoginPackets().sendClientPacket(4);
			return;
		}

		player.init(session, username, displayMode, screenWidth, screenHeight, new IsaacKeyPair(isaacKeys));

		session.getLoginPackets().sendLoginDetails(player);
		session.setDecoder(3, player);
		session.setEncoder(2, player);
		player.start();
	}

}
