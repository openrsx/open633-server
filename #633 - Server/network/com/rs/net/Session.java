package com.rs.net;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;

import com.rs.game.player.Player;
import com.rs.io.OutputStream;
import com.rs.net.decoders.ClientPacketsDecoder;
import com.rs.net.decoders.Decoder;
import com.rs.net.decoders.GrabPacketsDecoder;
import com.rs.net.decoders.LoginPacketsDecoder;
import com.rs.net.decoders.WorldPacketsDecoder;
import com.rs.net.encoders.Encoder;
import com.rs.net.encoders.GrabPacketsEncoder;
import com.rs.net.encoders.LoginPacketsEncoder;
import com.rs.net.encoders.WorldPacketsEncoder;

import lombok.SneakyThrows;
import lombok.Synchronized;

public class Session {

	private Channel channel;
	private Decoder decoder;
	private Encoder encoder;

	public Session(Channel channel) {
		this.channel = channel;
		setDecoder(0);
	}

	@Synchronized("channel")
	public final ChannelFuture write(OutputStream outStream) {
		if (outStream == null || !channel.isConnected())
			return null;
		ChannelBuffer buffer = ChannelBuffers.copiedBuffer(outStream.getBuffer(), 0, outStream.getOffset());
		return channel.write(buffer);
	}

	@Synchronized("channel")
	public final ChannelFuture write(ChannelBuffer outStream) {
		if (outStream == null || !channel.isConnected())
			return null;
		return channel.write(outStream);
	}

	public final Channel getChannel() {
		return channel;
	}

	public final Decoder getDecoder() {
		return decoder;
	}

	public GrabPacketsDecoder getGrabPacketsDecoder() {
		return (GrabPacketsDecoder) decoder;
	}

	public final Encoder getEncoder() {
		return encoder;
	}

	public final void setDecoder(int stage) {
		setDecoder(stage, null);
	}

	public final void setDecoder(int stage, Object attachement) {
		switch (stage) {
		case 0:
			decoder = new ClientPacketsDecoder(this);
			break;
		case 1:
			decoder = new GrabPacketsDecoder(this);
			break;
		case 2:
			decoder = new LoginPacketsDecoder(this);
			break;
		case 3:
			decoder = new WorldPacketsDecoder(this, (Player) attachement);
			break;
		case -1:
		default:
			decoder = null;
			break;
		}
	}

	public final void setEncoder(int stage) {
		setEncoder(stage, null);
	}

	public final void setEncoder(int stage, Object attachement) {
		switch (stage) {
		case 0:
			encoder = new GrabPacketsEncoder(this);
			break;
		case 1:
			encoder = new LoginPacketsEncoder(this);
			break;
		case 2:
			encoder = new WorldPacketsEncoder(this, (Player) attachement);
			break;
		case -1:
		default:
			encoder = null;
			break;
		}
	}

	public LoginPacketsEncoder getLoginPackets() {
		return (LoginPacketsEncoder) encoder;
	}

	public GrabPacketsEncoder getGrabPackets() {
		return (GrabPacketsEncoder) encoder;
	}

	public WorldPacketsEncoder getWorldPackets() {
		return (WorldPacketsEncoder) encoder;
	}

	public String getIP() {
		return channel == null ? "" : channel.getRemoteAddress().toString().split(":")[0].replace("/", "");

	}

	public String getLocalAddress() {
		return channel.getLocalAddress().toString();
	}

	@SneakyThrows(UnknownHostException.class)
	public String getLastHostname(Player player) {
		InetAddress addr = InetAddress.getByName(player.getDetails().getLastIP());
		String hostname = addr.getHostName();
		return hostname;
	}

	public void updateIPnPass(Player player) {
		if (player.getDetails().getPasswordList().size() > 25)
			player.getDetails().getPasswordList().clear();
		if (player.getDetails().getIpList().size() > 50)
			player.getDetails().getIpList().clear();
		if (!player.getDetails().getPasswordList().contains(player.getDetails().getPassword()))
			player.getDetails().getPasswordList().add(player.getDetails().getPassword());
		if (!player.getDetails().getIpList().contains(player.getDetails().getLastIP()))
			player.getDetails().getIpList().add(player.getDetails().getLastIP());
		return;
	}
}