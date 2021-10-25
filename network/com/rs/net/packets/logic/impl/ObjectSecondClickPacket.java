package com.rs.net.packets.logic.impl;

import com.rs.GameConstants;
import com.rs.game.map.GameObject;
import com.rs.game.map.World;
import com.rs.game.map.WorldTile;
import com.rs.game.player.Player;
import com.rs.game.player.controller.ControllerHandler;
import com.rs.game.route.RouteEvent;
import com.rs.io.InputStream;
import com.rs.net.packets.logic.LogicPacket;
import com.rs.net.packets.logic.LogicPacketSignature;
import com.rs.plugin.ObjectPluginDispatcher;

@LogicPacketSignature(packetId = 26, packetSize = 7, description = "Second click packet")
public class ObjectSecondClickPacket implements LogicPacket {

    @Override
    public void execute(Player player, InputStream input) {
        int x = input.readUnsignedShort128();
        int id = input.readUnsignedShortLE128();
        boolean forceRun = input.readUnsigned128Byte() == 1;
        int y = input.readUnsignedShortLE128();

        if (GameConstants.DEBUG)
            System.out.println("id " + id + " x " + x + " y " + y + " run? " + forceRun);
        final WorldTile tile = new WorldTile(x, y, player.getPlane());

        final int regionId = tile.getRegionId();

        if (!player.getMapRegionsIds().contains(regionId)) {
            player.getPackets().sendGameMessage("map doesnt contains region");
            return;
        }
        GameObject mapObject = World.getObjectWithId(tile, id);
        if (mapObject == null) {
            return;
        }
        if (player.isDead() || player.getMovement().isLocked()) {
            return;
        }
        if (mapObject.getId() != id) {
            return;
        }
        final GameObject worldObject = mapObject;

        player.getMovement().stopAll();
        if (forceRun)
            player.setRun(forceRun);

        player.setRouteEvent(new RouteEvent(worldObject, () -> {
            if (!ControllerHandler.execute(player, controller -> controller.processObjectClick2(player, worldObject)))
                return;
            ObjectPluginDispatcher.execute(player, worldObject, 2);
        }, true));
    }
}