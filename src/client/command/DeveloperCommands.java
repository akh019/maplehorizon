/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client.command;

import client.MapleCharacter;
import client.MapleClient;
import java.sql.SQLException;
import net.server.Server;
import net.server.channel.Channel;
import tools.MaplePacketCreator;



/**
 *
 * @author KaiSheng
 */
public class DeveloperCommands {
    
    public static boolean executeDeveloperCommandLv7(MapleClient c, String[] sub, char heading) throws SQLException { //Developer
        MapleCharacter player = c.getPlayer();
        Channel cserv = c.getChannelServer();
        
        if (sub[0].equals("saveall")) {
            for (MapleCharacter chr : c.getWorldServer().getPlayerStorage().getAllCharacters()) {
                chr.saveToDB();
            }
            String message = player.getName() + " used !saveall.";
            Server.getInstance().broadcastGMMessage(MaplePacketCreator.serverNotice(5, message));
            player.message("All players saved successfully.");
            
        } else {
            return false;
        }
        return true;
    }
}
