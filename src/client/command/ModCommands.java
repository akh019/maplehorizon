/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client.command;

import client.MapleCharacter;
import client.MapleClient;
import client.MapleJob;
import client.MapleStat;
import client.Skill;
import client.SkillFactory;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import constants.ItemConstants;
import constants.ServerConstants;
import java.io.File;
import java.sql.SQLException;
import net.server.Server;
import net.server.channel.Channel;
import provider.MapleData;
import provider.MapleDataProvider;
import provider.MapleDataProviderFactory;
import provider.MapleDataTool;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.MaplePortal;
import server.MapleShopFactory;
import server.life.MapleLifeFactory;
import server.maps.MapleMap;
import tools.MaplePacketCreator;
import tools.Pair;

/**
 *
 * @author KaiSheng
 */
public class ModCommands {
    
    public static boolean executeModeratorCommandLv3(MapleClient c, String[] sub, char heading) throws SQLException { // Moderator
        MapleCharacter player = c.getPlayer();
        Channel cserv = c.getChannelServer();
        
     if (sub[0].equals("saveall")) {
            for (MapleCharacter chr : c.getWorldServer().getPlayerStorage().getAllCharacters()) {
                chr.saveToDB();
            }
            String message = player.getName() + " used !saveall.";
            Server.getInstance().broadcastGMMessage(MaplePacketCreator.serverNotice(5, message));
            player.message("All players saved successfully.");
            
            } else if (sub[0].equals("watchoff")) {
            MapleCharacter victim = player.getWatched();
            if (victim != null) {
                victim.setWatcher(null);
                player.setWatched(null);
                player.dropMessage(6, "You've stopped watching " + victim.getName());
            }
        } else if (sub[0].equals("watch")) {
            if (sub.length > 1) {
                MapleCharacter victim = cserv.getPlayerStorage().getCharacterByName(sub[1]);
                if (victim != null && !victim.getName().equals(player.getName())) {
                    victim.setWatcher(player);
                    player.setWatched(victim);
                    player.dropMessage(6, "You've started watching " + victim.getName());
                } else {
                    player.dropMessage(5, "Error. Please enter a valid player to watch.");
                }
            } else {
                player.dropMessage(5, "Error. Please type the command as follows !watch <name>");
            }
            
        } else {
            return false;
        }
        return true;
    }
}