/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.events;

import client.MapleCharacter;
import client.MapleClient;
import java.util.HashMap;
import java.util.Map;
import tools.MaplePacketCreator;

/**
 *
 * @author Roy
 */
public class Scoreboard {
    
    private HashMap<String, Integer> scoreboard;
    
    public Scoreboard() {
        this.scoreboard = new HashMap<>();
    }
    
    public void addPoint(MapleClient host, Event e, String name) {
        MapleCharacter winner = host.getChannelServer().getPlayerStorage().getCharacterByName(name);
        String crName = (winner == null ? name : winner.getName());
        if(scoreboard.get(crName) == null) {
            scoreboard.put(crName, 1);
            host.getPlayer().getMap().broadcastMessage(MaplePacketCreator.serverNotice(6, "[" + e.getEventName() + "] " + crName + " won this round and now has 1 point."));
        } else {
            scoreboard.put(crName, scoreboard.get(crName) + 1);
            host.getPlayer().getMap().broadcastMessage(MaplePacketCreator.serverNotice(6, "[" + e.getEventName() + "] " + crName + " won this round and now has " + scoreboard.get(crName) + " points."));
        }
        if (winner == null) givePoint(crName);
        else givePoint(winner);
    }
    
    private void givePoint(String crName) {
        
    }
    
    private void givePoint(MapleCharacter winner) {
        
    }
}
