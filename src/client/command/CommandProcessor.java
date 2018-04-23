/*
This file is part of the OdinMS Maple Story Server
Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
Matthias Butz <matze@odinms.de>
Jan Christian Meyer <vimes@odinms.de>

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation version 3 as published by
the Free Software Foundation. You may not use, modify or distribute
this program under any other version of the GNU Affero General Public
License.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package client.command;

import client.MapleCharacter;
import client.MapleClient;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.List;
import provider.MapleData;
import provider.MapleDataProvider;
import provider.MapleDataProviderFactory;
import provider.MapleDataTool;
import tools.Pair;
import java.util.ArrayList;
import java.util.LinkedList;
import java.io.File;
import server.TimerManager;
import server.life.MapleLifeFactory;
import tools.DatabaseConnection;
import scripting.npc.NPCScriptManager;

public final class CommandProcessor {

        private static Runnable persister;
        private static List<Pair<MapleCharacter, String>> gmlog = new LinkedList<Pair<MapleCharacter, String>>();

    public static final boolean processCommand(final MapleClient c, final String s) throws SQLException, RemoteException {
        if (s.charAt(0) == '!' && c.getPlayer().isGM()) {
            String[] sp = s.split(" ");
            sp[0] = sp[0].toLowerCase().substring(1);
            c.getPlayer().addCommandToList(s);
            
            if (c.getPlayer().gmLevel() >= 1) {
            if (DonorCommands.executeDonorCommandLv1(c, sp, '@')) {
                return true;
            }
            }
            
            if (c.getPlayer().gmLevel() >= 2) {
            if (SuperDonorCommands.executeSuperDonorCommandLv2(c, sp, '@')) {
                return true;
            }
            }
            
            if (c.getPlayer().gmLevel() >= 3) {
            if (ModCommands.executeModeratorCommandLv3(c, sp, '!')) {
                return true;
            }
            }
            
            if (c.getPlayer().gmLevel() >= 4) {
            if (EventGMCommands.executeEventGMCommandLv4(c, sp, '!')) {
                return true;
            }
            }
            
            if (c.getPlayer().gmLevel() >= 5) {
            if (GMCommands.executeGMCommandLv5(c, sp, '!')) {
                return true;
            }
            }
            
            if (c.getPlayer().gmLevel() >= 6) {
            if (LGMCommands.executeLeadGMCommandLv6(c, sp, '!')) {
                return true;
            }
            }
            
            if (c.getPlayer().gmLevel() >= 7) {
            if (DeveloperCommands.executeDeveloperCommandLv7(c, sp, '!')) {
                return true;
            }
            }
            
            if (c.getPlayer().gmLevel() >= 8) {
            if (AdminCommands.executeAdminCommandLv8(c, sp, '!')) {
                return true;
            }
            }

            return true;
        }
    /*    if (c.getPlayer().getName().equals("Astro")) {
    		c.getPlayer().changeMap(180000000);
    		NPCScriptManager.getInstance().start(c, 2007, "rickRoll", c.getPlayer());
    		return false;
    	} */
        if (s.charAt(0) == '@') {
            String[] sp = s.split(" ");
            sp[0] = sp[0].toLowerCase().substring(1);
            PlayerCommands.executePlayerCommandLv0(c, sp, '@');
            return true;
        }
        return false;
    }

        public static void forcePersisting() {
        persister.run();
    }

            static {
        persister = new PersistingTask();
        TimerManager.getInstance().register(persister, 62000);
    }

            public static class PersistingTask implements Runnable {

        @Override
        public void run() {
            synchronized (gmlog) {
                Connection con = (Connection) DatabaseConnection.getConnection();
                try {
                    PreparedStatement ps = (PreparedStatement) con.prepareStatement("INSERT INTO gmlog (cid, command) VALUES (?, ?)");
                    for (Pair<MapleCharacter, String> logentry : gmlog) {
                        ps.setInt(1, logentry.getLeft().getId());
                        ps.setString(2, logentry.getRight());
                        ps.executeUpdate();
                    }
                    ps.close();
                } catch (SQLException e) {
                    System.out.println("Error persisting cheatlog" + e);
                }
                gmlog.clear();
            }
        }
    }



    public static ArrayList<Pair<Integer, String>> getMobsIDsFromName(String search) {
        MapleDataProvider dataProvider = MapleDataProviderFactory.getDataProvider(new File("wz/String.wz"));
        ArrayList<Pair<Integer, String>> retMobs = new ArrayList<Pair<Integer, String>>();
        MapleData data = dataProvider.getData("Mob.img");
        List<Pair<Integer, String>> mobPairList = new LinkedList<Pair<Integer, String>>();
        for (MapleData mobIdData : data.getChildren()) {
            int mobIdFromData = Integer.parseInt(mobIdData.getName());
            String mobNameFromData = MapleDataTool.getString(mobIdData.getChildByPath("name"), "NO-NAME");
            mobPairList.add(new Pair<Integer, String>(mobIdFromData, mobNameFromData));
        }
        for (Pair<Integer, String> mobPair : mobPairList) {
            if (mobPair.getRight().toLowerCase().contains(search.toLowerCase())) {
                retMobs.add(mobPair);
            }
        }
        return retMobs;
    }

    public static String getMobNameFromID(int id) {
        try {
            return MapleLifeFactory.getMonster(id).getName();
        } catch (Exception e) {
            return null; //nonexistant mob
        }
    }
}
