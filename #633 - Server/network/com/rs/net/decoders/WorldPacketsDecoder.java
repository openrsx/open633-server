package com.rs.net.decoders;

import com.rs.GameConstants;
import com.rs.game.World;
import com.rs.game.dialogue.DialogueEventListener;
import com.rs.game.player.Inventory;
import com.rs.game.player.Player;
import com.rs.game.player.content.FriendChatsManager;
import com.rs.game.player.content.Shop;
import com.rs.game.player.content.pet.Pets;
import com.rs.io.InputStream;
import com.rs.net.Encrypt;
import com.rs.net.Huffman;
import com.rs.net.LogicPacket;
import com.rs.net.Session;
import com.rs.net.encoders.other.ChatMessage;
import com.rs.net.encoders.other.PublicChatMessage;
import com.rs.net.encoders.other.QuickChatMessage;
import com.rs.net.packets.outgoing.OutgoingPacketDispatcher;
import com.rs.plugin.CommandDispatcher;
import com.rs.plugin.RSInterfaceDispatcher;
import com.rs.utilities.Logger;
import com.rs.utilities.Utils;

public final class WorldPacketsDecoder extends Decoder {

	/**
	 * The packet sizes.
	 */
	public static final byte[] PACKET_SIZES = new byte[256];

	// Converted
	private final static int WALKING_PACKET = 36;
	private final static int MINI_WALKING_PACKET = 43;
	private final static int COMMANDS_PACKET = 28;
	private final static int CHAT_PACKET = 54;
	private final static int CHAT_TYPE_PACKET = 77;
	private final static int PING_PACKET = 39;
	private final static int IN_OUT_SCREEN_PACKET = 16;
	private final static int MOVE_CAMERA_PACKET = 5;
	private final static int CLICK_PACKET = 55;
	private final static int ATTACK_NPC = 21;
	public final static int ACTION_BUTTON1_PACKET = 11;
	public final static int ACTION_BUTTON2_PACKET = 29;
	public final static int ACTION_BUTTON3_PACKET = 31;
	public final static int ACTION_BUTTON4_PACKET = 9;
	public final static int ACTION_BUTTON5_PACKET = 32;
	public final static int ACTION_BUTTON6_PACKET = 72;
	public final static int ACTION_BUTTON7_PACKET = 19;
	public final static int ACTION_BUTTON8_PACKET = 12;
	public final static int ACTION_BUTTON9_PACKET = 18;
	public final static int ACTION_BUTTON10_PACKET = 74;
	private final static int NPC_CLICK1_PACKET = 22;
	private final static int NPC_CLICK2_PACKET = 24;
	private final static int NPC_CLICK3_PACKET = 27;
	private final static int NPC_CLICK4_PACKET = 80;	
	private final static int DONE_LOADING_REGION_PACKET = 4;
	private final static int PUBLIC_QUICK_CHAT_PACKET = 69;
	private final static int INTERFACE_ON_NPC = 2;
	private final static int INTERFACE_ON_OBJECT = 58;
	private final static int INTERFACE_ON_INTERFACE = 33;
	private final static int INTERFACE_ON_PLAYER = 34;

	private final static int SWITCH_INTERFACE_COMPONENTS_PACKET = 10;
	private final static int SCREEN_PACKET = 62;
	private static final int NPC_EXAMINE_PACKET = 15;
	private final static int ENTER_INTEGER_PACKET = 81;
	private final static int CLOSE_INTERFACE_PACKET = 50;
	private final static int PLAYER_OPTION_1_PACKET = 25;
	private final static int PLAYER_OPTION_2_PACKET = 76;
	private final static int PLAYER_OPTION_3_PACKET = 44;
	private final static int PLAYER_OPTION_4_PACKET = 51;
	private final static int ENTER_NAME_PACKET = 20; 
	private final static int ITEM_TAKE_PACKET = 30;
	public final static int WORLD_MAP_CLICK = 23;
	public final static int RECEIVE_PACKET_COUNT_PACKET = 71;
	private final static int DIALOGUE_CONTINUE_PACKET = 61;
	
	// Convert stream order
	private final static int ADD_FRIEND_PACKET = 79;
	private final static int ADD_IGNORE_PACKET = 7;
	private final static int REMOVE_FRIEND_PACKET = 17;
	private final static int SEND_FRIEND_MESSAGE_PACKET = 40;
	private final static int SEND_FRIEND_QUICK_CHAT_PACKET = 49;
	private final static int JOIN_FRIEND_CHAT_PACKET = 1;
	private final static int KICK_FRIEND_CHAT_PACKET = 64;
	
	private final static int KEY_TYPED_PACKET = 63;
	private final static int AFK_PACKET = 8;
	private final static int MOUVE_MOUSE_PACKET = 3;
	private final static int OBJECT_CLICK1_PACKET = 75;
	private final static int OBJECT_CLICK2_PACKET = 26;
	private final static int OBJECT_CLICK3_PACKET = 6;
	private final static int OBJECT_CLICK4_PACKET = 13; 
	private final static int OBJECT_CLICK5_PACKET = 37;
	private final static int OBJECT_EXAMINE_PACKET = 67;
	private final static int GRAND_EXCHANGE_ITEM_SELECT_PACKET = 42;

	// Not converted
	private final static int REMOVE_IGNORE_PACKET = -1;
	private final static int CHANGE_FRIEND_CHAT_PACKET = -1;
	private final static int KICK_CLAN_CHAT_PACKET = -1;
	private final static int ENTER_LONG_TEXT_PACKET = -1;
	private final static int REPORT_ABUSE_PACKET = -1;
	
	private final static int PLAYER_OPTION_6_PACKET = -1;
	private final static int PLAYER_OPTION_9_PACKET = -1;
	
	private final static int OPEN_URL_PACKET = -1;

	private Player player;
	private int chatType;

	public static void loadPacketSizes() {
		for (int i : PACKET_SIZES)
			PACKET_SIZES[i] = -3;
		PACKET_SIZES[17] = -1;
		PACKET_SIZES[76] = 3;
		PACKET_SIZES[46] = 3;
		PACKET_SIZES[82] = -1;
		PACKET_SIZES[71] = 2;
		PACKET_SIZES[28] = -1;
		PACKET_SIZES[35] = -1;
		PACKET_SIZES[10] = 16;
		PACKET_SIZES[1] = -1;
		PACKET_SIZES[5] = 4;
		PACKET_SIZES[69] = -1;
		PACKET_SIZES[62] = 6;
		PACKET_SIZES[64] = -1;
		PACKET_SIZES[21] = 3;
		PACKET_SIZES[75] = 7;
		PACKET_SIZES[68] = -1;
		PACKET_SIZES[45] = -1;
		PACKET_SIZES[36] = 5;
		PACKET_SIZES[77] = 1;
		PACKET_SIZES[8] = 2;
		PACKET_SIZES[52] = 3;
		PACKET_SIZES[74] = 8;
		PACKET_SIZES[3] = -1;
		PACKET_SIZES[12] = 8;
		PACKET_SIZES[16] = 1;
		PACKET_SIZES[19] = 8;
		PACKET_SIZES[61] = 6;
		PACKET_SIZES[7] = -1;
		PACKET_SIZES[32] = 8;
		PACKET_SIZES[56] = 4;
		PACKET_SIZES[41] = 2;
		PACKET_SIZES[24] = 3;
		PACKET_SIZES[44] = 3;
		PACKET_SIZES[84] = 7;
		PACKET_SIZES[37] = 7;
		PACKET_SIZES[83] = 4;
		PACKET_SIZES[27] = 3;
		PACKET_SIZES[78] = -1;
		PACKET_SIZES[25] = 3;
		PACKET_SIZES[18] = 8;
		PACKET_SIZES[23] = 4;
		PACKET_SIZES[40] = -1;
		PACKET_SIZES[63] = -1;
		PACKET_SIZES[58] = 15;
		PACKET_SIZES[39] = 0;
		PACKET_SIZES[42] = 2;
		PACKET_SIZES[72] = 8;
		PACKET_SIZES[50] = 0;
		PACKET_SIZES[70] = 4;
		PACKET_SIZES[34] = 11;
		PACKET_SIZES[20] = -1;
		PACKET_SIZES[29] = 8;
		PACKET_SIZES[53] = 7;
		PACKET_SIZES[79] = -1;
		PACKET_SIZES[80] = 3;
		PACKET_SIZES[60] = 4;
		PACKET_SIZES[2] = 11;
		PACKET_SIZES[51] = 3;
		PACKET_SIZES[30] = 7;
		PACKET_SIZES[55] = 6;
		PACKET_SIZES[59] = 7;
		PACKET_SIZES[9] = 8;
		PACKET_SIZES[43] = 18;
		PACKET_SIZES[38] = 3;
		PACKET_SIZES[33] = 16;
		PACKET_SIZES[49] = -1;
		PACKET_SIZES[65] = -1;
		PACKET_SIZES[26] = 7;
		PACKET_SIZES[13] = 7;
		PACKET_SIZES[22] = 3;
		PACKET_SIZES[67] = 2;
		PACKET_SIZES[57] = 12;
		PACKET_SIZES[11] = 8;
		PACKET_SIZES[54] = -1;
		PACKET_SIZES[81] = 4;
		PACKET_SIZES[48] = 2;
		PACKET_SIZES[14] = 3;
		PACKET_SIZES[31] = 8;
		PACKET_SIZES[4] = 0;
		PACKET_SIZES[66] = 15;
		PACKET_SIZES[73] = -1;
		PACKET_SIZES[6] = 7;
		PACKET_SIZES[15] = 2;
		PACKET_SIZES[47] = 3;
		PACKET_SIZES[0] = 7;

	}
	public WorldPacketsDecoder(Session session, Player player) {
		super(session);
		this.player = player;
	}

	@Override
	public void decode(InputStream stream) {
		while (stream.getRemaining() > 0 && session.getChannel().isConnected()
				&& !player.isFinished()) {
			int packetId = stream.readPacket(player);
			if (packetId >= PACKET_SIZES.length || packetId < 0) {
				if (GameConstants.DEBUG)
					System.out.println("PacketId " + packetId
							+ " has fake packet id.");
				break;
			}
			int length = PACKET_SIZES[packetId];
			if (length == -1)
				length = stream.readUnsignedByte();
			else if (length == -2)
				length = stream.readUnsignedShort();
			else if (length == -3)
				length = stream.readInt();
			else if (length == -4) {
				length = stream.getRemaining();
				if (GameConstants.DEBUG)
					System.out.println("Invalid size for PacketId " + packetId
							+ ". Size guessed to be " + length);
			}
			if (length > stream.getRemaining()) {
				length = stream.getRemaining();
				if (GameConstants.DEBUG)
					System.out.println("PacketId " + packetId
							+ " has fake size. - expected size " + length);
				// break;

			}
			/*
			 * System.out.println("PacketId " +packetId+
			 * " has . - expected size " +length);
			 */
			int startOffset = stream.getOffset();
			processPackets(packetId, stream, length);
			
			stream.setOffset(startOffset + length);
		}
	}

	public static void decodeLogicPacket(final Player player, LogicPacket packet) {
		InputStream stream = new InputStream(packet.getData());
		if (GameConstants.DEBUG)
			System.out.println("packet: " + packet.getId());
		OutgoingPacketDispatcher.execute(player, stream, packet.getId());
	}

	@SuppressWarnings("unused")
	public void processPackets(final int packetId, InputStream stream, int length) {
		if (packetId == PING_PACKET) {
			// kk we ping :)
		} else if (packetId == MOUVE_MOUSE_PACKET) {
			// USELESS PACKET
		} else if (packetId == KEY_TYPED_PACKET) {
			// USELESS PACKET
		} else if (packetId == RECEIVE_PACKET_COUNT_PACKET) {
			// interface packets
			stream.readShort();

		} else if (packetId == INTERFACE_ON_INTERFACE) {
			RSInterfaceDispatcher.handleInterfaceOnInterface(player, stream);
		} else if (packetId == AFK_PACKET) {
			//player.getSession().getChannel().close();
		} else if (packetId == CLOSE_INTERFACE_PACKET) {
			if (player.isStarted() && !player.isFinished()
					&& !player.isRunning()) { // used
				// for
				// old
				// welcome
				// screen
				player.run();
				return;
			}
			player.stopAll();
		} else if (packetId == MOVE_CAMERA_PACKET) {
			// not using it atm
			stream.readUnsignedShort();
			stream.readUnsignedShort();
		} else if (packetId == IN_OUT_SCREEN_PACKET) {
			// not using this check because not 100% efficient
			boolean inScreen = stream.readByte() == 1;
		} else if (packetId == SCREEN_PACKET) {
			byte displayMode = (byte) stream.readUnsignedByte();
			player.setScreenWidth((short) stream.readUnsignedShort());
			player.setScreenHeight((short) stream.readUnsignedShort());
			boolean switchScreenMode = stream.readUnsignedByte() == 1;
			if (!player.isStarted() || player.isFinished()
					|| displayMode == player.getDisplayMode()
					|| !player.getInterfaceManager().containsInterface(742))
				return;
			player.setDisplayMode(displayMode);
			player.getInterfaceManager().getOpenedinterfaces().clear();
			player.getInterfaceManager().sendInterfaces();
			player.getInterfaceManager().sendInterface(742);
		} else if (packetId == CLICK_PACKET) {
			int mouseHash = stream.readShortLE128();
			int mouseButton = mouseHash >> 15;
			int time = mouseHash - (mouseButton << 15); // time
			int positionHash = stream.readIntV1();
			int y = positionHash >> 16; // y;
			int x = positionHash - (y << 16); // x
			boolean clicked;
			// mass click or stupid autoclicker, lets stop lagg
			if (time <= 1 || x < 0 || x > player.getScreenWidth() || y < 0
					|| y > player.getScreenHeight()) {
				// player.getSession().getChannel().close();
				clicked = false;
				return;
			}
			clicked = true;
		} else if (packetId == DIALOGUE_CONTINUE_PACKET) {
			int junk = stream.readShortLE128();
			int interfaceHash = stream.readIntV2();
			int interfaceId = interfaceHash >> 16;
			int buttonId = (interfaceHash & 0xFF);
			if (Utils.getInterfaceDefinitionsSize() <= interfaceId) {
				// hack, or server error or client error
				// player.getSession().getChannel().close();
				return;
			}
			if (!player.isRunning()
					|| !player.getInterfaceManager().containsInterface(
							interfaceId))
				return;
			if (GameConstants.DEBUG)
				Logger.log(this, "Dialogue: " + interfaceId + ", " + buttonId
						+ ", " + junk);
			int componentId = interfaceHash - (interfaceId << 16);
			if (DialogueEventListener.continueDialogue(player, componentId))
				return;
		} else if (packetId == WORLD_MAP_CLICK) {
			int coordinateHash = stream.readIntV2();
			int x = coordinateHash >> 14;
			int y = coordinateHash & 0x3fff;
			int plane = coordinateHash >> 28;
			Integer hash = (Integer) player.getTemporaryAttributes().get(
					"worldHash");
			if (hash == null || coordinateHash != hash)
				player.getTemporaryAttributes().put("worldHash",
						coordinateHash);
			else {
				player.getTemporaryAttributes().remove("worldHash");
				player.getHintIconsManager().addHintIcon(x, y, plane, 20, 0, 2,
						-1, true);
				player.getVarsManager().sendVar(1159, coordinateHash);
			}
		} else if (packetId == ACTION_BUTTON1_PACKET
				|| packetId == ACTION_BUTTON2_PACKET
				|| packetId == ACTION_BUTTON4_PACKET
				|| packetId == ACTION_BUTTON5_PACKET
				|| packetId == ACTION_BUTTON6_PACKET
				|| packetId == ACTION_BUTTON7_PACKET
				|| packetId == ACTION_BUTTON8_PACKET
				|| packetId == ACTION_BUTTON3_PACKET
				|| packetId == ACTION_BUTTON9_PACKET
				|| packetId == ACTION_BUTTON10_PACKET) {
			RSInterfaceDispatcher.handleButtons(player, stream, packetId);
		} else if (packetId == ENTER_NAME_PACKET) {
			if (!player.isRunning() || player.isDead())
				return;
			String value = stream.readString();
			if (value.equals(""))
				return;
			if (player.getInterfaceManager().containsInterface(1108))
				player.getFriendsIgnores().setChatPrefix(value);
			
			else if (player.getTemporaryAttributes().remove(
					"forum_authuserinput") == Boolean.TRUE) {
				player.getTemporaryAttributes().put("forum_authuser", value);
				player.getTemporaryAttributes().put("forum_authpasswordinput",
						true);
				player.getPackets().sendInputNameScript(
						"Enter your forum password:");
			}
		} else if (packetId == ENTER_LONG_TEXT_PACKET) {
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
					player.getPackets().sendGameMessage(
							"Password length is limited to 5-15 characters.");
					return;
				}
				player.getDetails().setPassword(Encrypt.encryptSHA1(value));
				player.getPackets().sendGameMessage(
						"You have changed your password! Your new password is \""
								+ value + "\".");
			} else if (player.getTemporaryAttributes().remove(
					"change_troll_name") == Boolean.TRUE) {
				value = Utils.formatPlayerNameForDisplay(value);
				if (value.length() < 3 || value.length() > 14) {
					player.getPackets()
							.sendGameMessage(
									"You can't use a name shorter than 3 or longer than 14 characters.");
					return;
				}
				if (value.equalsIgnoreCase("none")) {
					player.getPetManager().setTrollBabyName(null);
				} else {
					player.getPetManager().setTrollBabyName(value);
					if (player.getPet() != null
							&& player.getPet().getId() == Pets.TROLL_BABY
									.getBabyNpcId()) {
						player.getPet().setName(value);
					}
				}
			} else if (player.getTemporaryAttributes().remove("yellcolor") == Boolean.TRUE) {
				if (value.length() != 6) {
					player.getPackets()
							.sendGameMessage(
									"The HEX yell color you wanted to pick cannot be longer and shorter then 6.");
				} else if (Utils.containsInvalidCharacter(value)
						|| value.contains("_")) {
					player.getPackets()
							.sendGameMessage(
									"The requested yell color can only contain numeric and regular characters.");
				} else {
					player.getDetails().setYellColor(value);
					player.getPackets().sendGameMessage(
							"Your yell color has been changed to <col="
									+ player.getDetails().getYellColor() + ">"
									+ player.getDetails().getYellColor() + "</col>.");
				}
			}
		} else if (packetId == ENTER_INTEGER_PACKET) {
			if (!player.isRunning() || player.isDead())
				return;
			int value = stream.readInt();
			if (value < 0)
				return;
			if ((player.getInterfaceManager().containsInterface(762) && player
					.getInterfaceManager().containsInterface(763))
					|| player.getInterfaceManager().containsInterface(11)) {
				Integer bank_item_X_Slot = (Integer) player
						.getTemporaryAttributes().remove("bank_item_X_Slot");
				if (bank_item_X_Slot == null)
					return;
				player.getBank().setLastX(value);
				player.getBank().refreshLastX();
				if (player.getTemporaryAttributes().remove("bank_isWithdraw") != null)
					player.getBank().withdrawItem(bank_item_X_Slot, value);
				else
					player.getBank()
							.depositItem(
									bank_item_X_Slot,
									value,
									player.getInterfaceManager()
											.containsInterface(11) ? false
											: true);
			} else if (player.getInterfaceManager().containsInterface(206)
					&& player.getInterfaceManager().containsInterface(207)) {
				Integer pc_item_X_Slot = (Integer) player
						.getTemporaryAttributes().remove("pc_item_X_Slot");
				if (pc_item_X_Slot == null)
					return;
				if (player.getTemporaryAttributes().remove("pc_isRemove") != null)
					player.getPriceCheckManager().removeItem(pc_item_X_Slot,
							value);
				else
					player.getPriceCheckManager()
							.addItem(pc_item_X_Slot, value);
			} else if (player.getInterfaceManager().containsInterface(671)
					&& player.getInterfaceManager().containsInterface(665)) {
				if (player.getFamiliar() == null
						|| player.getFamiliar().getBob() == null)
					return;
				Integer bob_item_X_Slot = (Integer) player
						.getTemporaryAttributes().remove("bob_item_X_Slot");
				if (bob_item_X_Slot == null)
					return;
				if (player.getTemporaryAttributes().remove("bob_isRemove") != null)
					player.getFamiliar().getBob()
							.removeItem(bob_item_X_Slot, value);
				else
					player.getFamiliar().getBob()
							.addItem(bob_item_X_Slot, value);
			} else if (player.getInterfaceManager().containsInterface(335)
					&& player.getInterfaceManager().containsInterface(336)) {
				Integer trade_item_X_Slot = (Integer) player
						.getTemporaryAttributes().remove("trade_item_X_Slot");
				if (trade_item_X_Slot == null)
					return;
				if (player.getTemporaryAttributes().remove("trade_isRemove") != null)
					player.getTrade().removeItem(trade_item_X_Slot, value);
				else
					player.getTrade().addItem(trade_item_X_Slot, value);
			} else if (player.getTemporaryAttributes().remove("xformring") == Boolean.TRUE)
				player.getAppearance().transformIntoNPC(value);
		} else if (packetId == SWITCH_INTERFACE_COMPONENTS_PACKET) {

			int fromInterfaceHash = stream.readInt();
			int idk2 = stream.readUnsignedShortLE128();
			int toSlot = stream.readUnsignedShortLE128();
			int toInterfaceHash = stream.readIntV1();
			int fromSlot = stream.readUnsignedShortLE128();
			int idk = stream.readUnsignedShort();

			int toInterfaceId = toInterfaceHash >> 16;
			int toComponentId = toInterfaceHash - (toInterfaceId << 16);
			int fromInterfaceId = fromInterfaceHash >> 16;
			int fromComponentId = fromInterfaceHash - (fromInterfaceId << 16);
			
			// System.out.println(fromInterfaceHash + " IDK:" + idk + " "
			// + toInterfaceHash + " " + idk1 + " " + fromSlot + " "
			// + toSlot);
			
			// System.out.println(toInterfaceId + " " + fromInterfaceId + " "
			// + fromComponentId + " " + toComponentId);

			if (Utils.getInterfaceDefinitionsSize() <= fromInterfaceId
					|| Utils.getInterfaceDefinitionsSize() <= toInterfaceId)
				return;
			if (!player.getInterfaceManager()
					.containsInterface(fromInterfaceId)
					|| !player.getInterfaceManager().containsInterface(
							toInterfaceId))
				return;
			if (fromComponentId != -1
					&& Utils.getInterfaceDefinitionsComponentsSize(fromInterfaceId) <= fromComponentId)
				return;
			if (toComponentId != -1
					&& Utils.getInterfaceDefinitionsComponentsSize(toInterfaceId) <= toComponentId)
				return;
			if (fromInterfaceId == Inventory.INVENTORY_INTERFACE
					&& fromComponentId == 0
					&& toInterfaceId == Inventory.INVENTORY_INTERFACE
					&& toComponentId == 0) {
				toSlot -= 28;
				if (toSlot < 0
						|| toSlot >= player.getInventory()
								.getItemsContainerSize()
						|| fromSlot >= player.getInventory()
								.getItemsContainerSize())
					return;
				player.getInventory().switchItem(fromSlot, toSlot);
			} else if (fromInterfaceId == 763 && fromComponentId == 0
					&& toInterfaceId == 763 && toComponentId == 0) {
				if (toSlot >= player.getInventory().getItemsContainerSize()
						|| fromSlot >= player.getInventory()
								.getItemsContainerSize())
					return;
				player.getInventory().switchItem(fromSlot, toSlot);
			} else if (fromInterfaceId == 762 && toInterfaceId == 762) {
				player.getBank().switchItem(fromSlot, toSlot, fromComponentId,
						toComponentId);
			} else if (fromInterfaceId == 1265
					&& toInterfaceId == 1266
					&& player.getTemporaryAttributes().get("is_buying") != null) {
				if ((boolean) player.getTemporaryAttributes().get("is_buying") == true) {
					Shop shop = (Shop) player.getTemporaryAttributes().get(
							"shop_instance");
					if (shop == null)
						return;
					// shop.buyItem(player, fromSlot, 1);
				}
			} else if (fromInterfaceId == 34 && toInterfaceId == 34)
				player.getNotes().switchNotes(fromSlot, toSlot);
			if (GameConstants.DEBUG)
				System.out.println("Switch item " + fromInterfaceId + ", "
						+ fromSlot + ", " + toSlot);
		} else if (packetId == DONE_LOADING_REGION_PACKET) {
			/*
			 * if(!player.isClientLoadedMapRegion()) { //load objects and items
			 * here player.setClientHasLoadedMapRegion(); }
			 * //player.refreshSpawnedObjects(); //player.refreshSpawnedItems();
			 */
		} else if (packetId == WALKING_PACKET
				|| packetId == MINI_WALKING_PACKET
				|| packetId == ITEM_TAKE_PACKET
				|| packetId == PLAYER_OPTION_2_PACKET
				|| packetId == PLAYER_OPTION_3_PACKET
				|| packetId == PLAYER_OPTION_4_PACKET
				|| packetId == PLAYER_OPTION_6_PACKET
				|| packetId == PLAYER_OPTION_9_PACKET
				|| packetId == PLAYER_OPTION_1_PACKET || packetId == ATTACK_NPC
				|| packetId == INTERFACE_ON_PLAYER
				|| packetId == INTERFACE_ON_NPC
				|| packetId == NPC_CLICK1_PACKET
				|| packetId == NPC_CLICK2_PACKET
				|| packetId == NPC_CLICK3_PACKET
				|| packetId == NPC_CLICK4_PACKET
				|| packetId == OBJECT_CLICK1_PACKET
				|| packetId == SWITCH_INTERFACE_COMPONENTS_PACKET
				|| packetId == OBJECT_CLICK2_PACKET
				|| packetId == OBJECT_CLICK3_PACKET
				|| packetId == OBJECT_CLICK4_PACKET
				|| packetId == OBJECT_CLICK5_PACKET
				|| packetId == INTERFACE_ON_OBJECT)
			player.addLogicPacketToQueue(new LogicPacket(packetId, length,
					stream));
		else if (packetId == OBJECT_EXAMINE_PACKET) {
			System.out.println("examine packet");
//			ObjectDispatcher.handleOption(player, stream, -1);
		} else if (packetId == NPC_EXAMINE_PACKET) {
//			NPCDispatcher.handleExamine(player, stream);
		} else if (packetId == JOIN_FRIEND_CHAT_PACKET) {
			if (!player.isStarted())
				return;
			FriendChatsManager.joinChat(stream.readString(), player);
		} else if (packetId == KICK_FRIEND_CHAT_PACKET) {
			if (!player.isStarted())
				return;
			player.setLastPublicMessage(Utils.currentTimeMillis() + 1000);
			FriendChatsManager fcManager = new FriendChatsManager(player);
			fcManager.kickPlayerFromFriendsChannel(player, stream.readString());
		} else if (packetId == KICK_CLAN_CHAT_PACKET) {
			if (!player.isStarted())
				return;
			player.setLastPublicMessage(Utils.currentTimeMillis() + 1000); // avoids
			// message
			// appearing
			boolean guest = stream.readByte() == 1;
			if (!guest)
				return;
			stream.readUnsignedShort();
//			player.kickPlayerFromClanChannel(stream.readString());
		} else if (packetId == CHANGE_FRIEND_CHAT_PACKET) {
			if (!player.isStarted()
					|| !player.getInterfaceManager().containsInterface(1108))
				return;
			player.getFriendsIgnores().changeRank(stream.readString(),
					stream.readUnsignedByte128());
		} else if (packetId == ADD_FRIEND_PACKET) {
			if (!player.isStarted())
				return;
			player.getFriendsIgnores().addFriend(stream.readString());
		} else if (packetId == REMOVE_FRIEND_PACKET) {
			if (!player.isStarted())
				return;
			player.getFriendsIgnores().removeFriend(stream.readString());
		} else if (packetId == ADD_IGNORE_PACKET) {
			if (!player.isStarted())
				return;
			player.getFriendsIgnores().addIgnore(stream.readString(),
					stream.readUnsignedByte() == 1);
		} else if (packetId == REMOVE_IGNORE_PACKET) {
			if (!player.isStarted())
				return;
			player.getFriendsIgnores().removeIgnore(stream.readString());
		} else if (packetId == SEND_FRIEND_MESSAGE_PACKET) {
			if (!player.isStarted())
				return;
			if (player.getDetails().getMuted() > Utils.currentTimeMillis()) {
				player.getPackets().sendGameMessage(
						"You temporary muted. Recheck in 48 hours.");
				return;
			}
			String username = stream.readString();
			Player p2 = World.getPlayerByDisplayName(username);
			if (p2 == null)
				return;

			player.getFriendsIgnores().sendMessage(p2,
					new ChatMessage(Huffman.readEncryptedMessage(150, stream)));
		} else if (packetId == SEND_FRIEND_QUICK_CHAT_PACKET) {
			if (!player.isStarted())
				return;
			String username = stream.readString();
			int fileId = stream.readUnsignedShort();
			if (!Utils.isQCValid(fileId))
				return;
			byte[] data = null;
			if (length > 3 + username.length()) {
				data = new byte[length - (3 + username.length())];
				stream.readBytes(data);
			}
			data = Utils.completeQuickMessage(player, fileId, data);
			Player p2 = World.getPlayerByDisplayName(username);
			if (p2 == null)
				return;
			player.getFriendsIgnores().sendQuickChatMessage(p2,
					new QuickChatMessage(fileId, data));
		} else if (packetId == PUBLIC_QUICK_CHAT_PACKET) {
			if (!player.isStarted())
				return;
			if (player.getLastPublicMessage() > Utils.currentTimeMillis())
				return;
			player.setLastPublicMessage(Utils.currentTimeMillis() + 300);
			// just tells you which client script created packet
			boolean secondClientScript = stream.readByte() == 1;// script 5059
			// or 5061
			int fileId = stream.readUnsignedShort();
			if (!Utils.isQCValid(fileId))
				return;
			byte[] data = null;
			if (length > 3) {
				data = new byte[length - 3];
				stream.readBytes(data);
			}
			data = Utils.completeQuickMessage(player, fileId, data);
//			if (chatType == 0)
//				player.sendPublicChatMessage(new QuickChatMessage(fileId, data));
//			else if (chatType == 1)
//				player.sendFriendsChannelQuickMessage(new QuickChatMessage(
//						fileId, data));
			 if (GameConstants.DEBUG)
				Logger.log(this, "Unknown chat type: " + chatType);
		} else if (packetId == CHAT_TYPE_PACKET) {
//			chatType = stream.readUnsignedByte();
		} else if (packetId == CHAT_PACKET) {
			if (!player.isStarted())
				return;
			if (player.getLastPublicMessage() > Utils.currentTimeMillis())
				return;
			player.setLastPublicMessage(Utils.currentTimeMillis() + 300);
			int colorEffect = stream.readUnsignedByte();
			int moveEffect = stream.readUnsignedByte();
			String message = Huffman.readEncryptedMessage(200, stream);
			if (message == null || message.replaceAll(" ", "").equals(""))
				return;
			if (message.startsWith("::") || message.startsWith(";;")) {
				// if command exists and processed wont send message as public
				// message
				CommandDispatcher.processCommand(player, message.replace("::", "")
						.replace(";;", ""), false, false);
				return;
			}
			if (player.getDetails().getMuted() > Utils.currentTimeMillis()) {
				player.getPackets().sendGameMessage(
						"You temporary muted. Recheck in 48 hours.");
				return;
			}
			int effects = (colorEffect << 8) | (moveEffect & 0xff);
//			if (chatType == 1)
//				player.sendFriendsChannelMessage(new ChatMessage(message));
//			else if (chatType == 2)
//				player.sendClanChannelMessage(new ChatMessage(message));
//			else if (chatType == 3)
//				player.sendGuestClanChannelMessage(new ChatMessage(message));
//			else
			PublicChatMessage chatMessage = new PublicChatMessage(message, effects);
			chatMessage.sendPublicChatMessage(player, chatMessage);
			if (GameConstants.DEBUG)
				Logger.log(this, "Chat type: " + chatType);
		} else if (packetId == COMMANDS_PACKET) {
			if (!player.isRunning())
				return;
			boolean clientCommand = stream.readUnsignedByte() == 1;
			boolean unknown = stream.readUnsignedByte() == 1;
			String command = stream.readString();
			if (!CommandDispatcher.processCommand(player, command, true, clientCommand)
					&& GameConstants.DEBUG)
				Logger.log(this, "Command: " + command);
		} else if (packetId == REPORT_ABUSE_PACKET) {
			if (!player.isStarted())
				return;
			String displayName = stream.readString();
			int type = stream.readUnsignedByte();
			boolean mute = stream.readUnsignedByte() == 1;
			String unknown2 = stream.readString();
//			ReportAbuse.report(player, displayName, type, mute);
		
//		} else if (packetId == FORUM_THREAD_ID_PACKET) {
//			String threadId = stream.readString();
//			if (player.getInterfaceManager().containsInterface(1100))
//				ClansManager.setThreadIdInterface(player, threadId);
//			else if (Settings.DEBUG)
//				Logger.log(this, "Called FORUM_THREAD_ID_PACKET: " + threadId);
		} else if (packetId == OPEN_URL_PACKET) {
			String type = stream.readString();
			String path = stream.readString();
			String unknown = stream.readString();
			int flag = stream.readUnsignedByte();
			
		} else if (packetId == GRAND_EXCHANGE_ITEM_SELECT_PACKET) {
			int itemId = stream.readUnsignedShort();
//			player.getGeManager().chooseItem(itemId);
		} else {
			if (GameConstants.DEBUG)
				Logger.log(this, "Missing packet " + packetId
						+ ", expected size: " + length + ", actual size: "
						+ PACKET_SIZES[packetId]);
		}
	}
}