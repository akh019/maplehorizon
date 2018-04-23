/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client.command;

import client.BuddylistEntry;
import client.MapleCharacter;
import client.MapleClient;
import client.MapleJob;
import client.MapleSkinColor;
import client.MapleStat;
import client.Skill;
import client.SkillFactory;
import static client.command.EventGMCommands.event;
import client.events.EventCommands;
import client.events.EventHandler2;
import client.inventory.MapleInventoryType;
import java.sql.PreparedStatement;
import constants.GameConstants;
import constants.ServerConstants;
import java.awt.Point;
import java.io.File;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;
import net.server.Server;
import net.server.channel.Channel;
import net.server.world.MaplePartyCharacter;
import provider.MapleData;
import provider.MapleDataProviderFactory;
import scripting.event.EventHandler;
import scripting.event.Fishing;
import scripting.event.Ranking;
import scripting.npc.NPCScriptManager;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.MaplePortal;
import server.gachapon.MapleGachapon.Gachapon;
import server.life.MapleLifeFactory;
import server.life.MapleMonster;
import server.life.MapleMonsterInformationProvider;
import server.life.MonsterDropEntry;
import server.maps.MapleMap;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import tools.DatabaseConnection;
import tools.FilePrinter;
import tools.MaplePacketCreator;
import tools.Pair;
import tools.Randomizer;
import tools.StringUtil;

/**
 *
 * @author KaiSheng
 */
public class SuperDonorCommands {
    
    public static boolean executeSuperDonorCommandLv2(MapleClient c, String[] splitted, char heading) throws SQLException { // Super Donator
        MapleCharacter player = c.getPlayer();
        Channel cserv = c.getChannelServer();
        
    
               if (splitted[0].equalsIgnoreCase("dautorbe")) {
				player.autoRebirth = !player.autoRebirth;
				player.dropMessage(player.autoRebirth ? "Auto rebirth to [Explorer] is on!"
						: "Auto rebirth to [Explorer] is off!");
               
		} else if (splitted[0].equalsIgnoreCase("dautorbc")) {
				player.autoRebirth = !player.autoRebirth;
				player.dropMessage(player.autoRebirth ? "Auto rebirth to [Cygnus] is on!"
						: "Auto rebirth to [Cygnus] is off!");
		} else if (splitted[0].equalsIgnoreCase("dautorba")) {
				player.autoRebirth = !player.autoRebirth;
				player.dropMessage(player.autoRebirth ? "Auto rebirth to [Aran] is on!"
						: "Auto rebirth to [Aran] is off!");
                }
        return false;
    }
}
