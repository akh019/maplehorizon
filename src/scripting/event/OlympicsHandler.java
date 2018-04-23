/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scripting.event;

import client.MapleCharacter;
import client.MapleClient;
import client.inventory.Item;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import java.sql.SQLException;
import java.util.List;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.maps.MapleMap;
import server.maps.MapleMapObject;
import tools.MaplePacketCreator;

/**
 *
 * @author Roy
 */
public class OlympicsHandler {
    
    public static final int teams = 5;
    public static final int[] teamNums = {0, 1, 2, 3, 4, 5};
    public static final int[] itemIds =  {}; // todo
    
    public static void startOlympics(MapleClient host) throws SQLException {
        if(!host.getPlayer().getMap().getOlympicsState()) {
            host.getPlayer().getMap().broadcastMessage(MaplePacketCreator.serverNotice(6, "[Olympics] " + host.getPlayer().getName() + " has started the Olympics!"));
            splitTeams(host);
        }
        else {
            host.getPlayer().dropMessage("[Olympics] Olympics event is currently activated on this map.");
        }
    }
    
    public static void splitTeams(MapleClient host) throws SQLException {
        host.getPlayer().dropMessage("Attemtping to shuffle teams...");
        host.getPlayer().getMap().shuffleOlympicTeams();
        host.getPlayer().dropMessage("Shuffled");
    }
    
    public static void endEvent(MapleClient host) {
        host.getPlayer().dropMessage("Attemtping to end olympics...");
        host.getPlayer().getMap().endOlympics();
        host.getPlayer().dropMessage("Ended.");
    }
}
