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
package net.server.channel.handlers;

import bots.BernardManager;
import bots.StringMethods;
import java.rmi.RemoteException;
import java.sql.SQLException;
import tools.FilePrinter;
import tools.MaplePacketCreator;
import tools.data.input.SeekableLittleEndianAccessor;
import client.MapleCharacter;
import client.MapleClient;
import client.command.CommandProcessor;
import client.events.Bongo;
import static client.events.Bongo.initiateBongoInterval;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import constants.ServerConstants;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import server.MapleInventoryManipulator;
import server.TimerManager;
import tools.DatabaseConnection;

public final class GeneralChatHandler extends net.AbstractMaplePacketHandler {

    public static StringMethods sm = new StringMethods();

    public final void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        String s = slea.readMapleAsciiString();

        MapleCharacter chr = c.getPlayer();
        Calendar current = Calendar.getInstance();
        c.getPlayer().updateAfk(current);

        if (System.currentTimeMillis() - chr.getLastGCM() < 300) {
            return;
        }
        chr.setLastGCM(System.currentTimeMillis());

        try {
            if (!CommandProcessor.processCommand(c, s) && !chr.isMuted() && !chr.isPermmute()) {
                /*if (s.length() > Byte.MAX_VALUE && !chr.isGM()) {
                    FilePrinter.printError(FilePrinter.EXPLOITS + c.getPlayer().getName() + ".txt", c.getPlayer().getName() + " tried to send text with length of " + s.length() + "\r\n");
                    c.disconnect(true, false);
                    return;
                }*/
                int show = slea.readByte();
                if (show != 0) {
                    if (chr.macro == null) {
                        return;
                    }
                    chr.macro = null;
                }
                if (chr.isMuted()) {
                    chr.dropMessage(5, "You're currently muted. Please try again later.");
                    return;
                }
                if (chr.isPermmute()) {
                    chr.dropMessage(5, "You're permanently muted. You don't have chatting privileges.");
                    return;
                }
                if (chr.getMap().isMuted() && (!chr.isGM() || chr.isTemp())) {
                    chr.dropMessage(5, "The map you are in is currently muted. Please try again later.");
                    return;
                }
                 if (!chr.isHidden()) {
                    if (c.isJailed(chr.getName()) || chr.isPunished()) {
                        // s += ", btw I'm a fucking retard :(";
                        ResultSet rs;
                        String sentence = "";
                        try {
                            PreparedStatement ps;
                            Connection con = DatabaseConnection.getConnection();
                            ps = con.prepareStatement("SELECT sentence FROM punishsentences ORDER BY RAND() LIMIT 1");
                            rs = ps.executeQuery();
                            while (rs.next()) {
                                sentence += rs.getString("sentence");
                            }
                        } catch (SQLException sqlexc) {
                            System.out.print("Error selecting from punishsentences: " + sqlexc);
                        }
                        s += sentence;
                        chr.getMap().broadcastMessage(MaplePacketCreator.getChatText(chr.getId(), s, chr.getWhiteChat(), show));
                    } else {
                        chr.getMap().broadcastMessage(MaplePacketCreator.getChatText(chr.getId(), s, chr.getWhiteChat(), show));
                        //  chr.dropMessage(6,s);

                    }

                } else {
                    chr.getMap().broadcastGMMessage(MaplePacketCreator.getChatText(chr.getId(), s, false, 1));
                    chr.getMap().broadcastGMMessage(MaplePacketCreator.serverNotice(5, "[Hide] " + chr.getName() + ": " + s));

                }
                if (s.equals(ServerConstants.hitmanAnswer) && chr.getMap().hitmanOn()) {
                    chr.getMap().broadcastMessage(MaplePacketCreator.serverNotice(6, "[Hitman] " + chr.getName() + " has written down the names!"));
                    chr.getMap().setHitman(false);
                    if (chr.getMap().ChalkpointsOn()) {
                        int currentpts;
                        if (chr.getChalkboard() != null) {
                            currentpts = Integer.parseInt(chr.getChalkboard().charAt(0) + "");
                            chr.setChalkboard(currentpts + 1 + " / " + chr.getMap().getPointstowin());
                        } else {
                            chr.setChalkboard("1 / " + chr.getMap().getPointstowin());
                        }

                        chr.getMap().broadcastMessage(MaplePacketCreator.useChalkboard(chr, false));

                        if (Integer.parseInt(chr.getChalkboard().charAt(0) + "") == chr.getMap().getPointstowin()) {
                            chr.getMap().broadcastMessage(MaplePacketCreator.serverNotice(6, chr.getName() + " has reached " + chr.getMap().getPointstowin() + " points, Congratulations!"));
                            chr.getMap().setChalkpoints(false);
                            chr.getMap().setChalk(true);
                            chr.getMap().setClosable(true);
                            for (MapleCharacter a1 : chr.getMap().getCharacters()) {
                                chr.getMap().broadcastMessage(MaplePacketCreator.useChalkboard(a1, true));
                            }

                        }
                    }
                }
                if (s.equals(ServerConstants.blinkAnswer) && chr.getMap().blinkOn()) {
                    chr.getMap().broadcastMessage(MaplePacketCreator.serverNotice(6, "[Blink] " + chr.getName() + " has written down the answer!"));
                    chr.getMap().setBlink(false);
                    if (chr.getMap().ChalkpointsOn()) {
                        int currentpts;
                        if (chr.getChalkboard() != null) {
                            currentpts = Integer.parseInt(chr.getChalkboard().charAt(0) + "");
                            chr.setChalkboard(currentpts + 1 + " / " + chr.getMap().getPointstowin());
                        } else {
                            chr.setChalkboard("1 / " + chr.getMap().getPointstowin());
                        }

                        chr.getMap().broadcastMessage(MaplePacketCreator.useChalkboard(chr, false));

                        if (Integer.parseInt(chr.getChalkboard().charAt(0) + "") == chr.getMap().getPointstowin()) {
                            chr.getMap().broadcastMessage(MaplePacketCreator.serverNotice(6, chr.getName() + " has reached " + chr.getMap().getPointstowin() + " points, Congratulations!"));
                            chr.getMap().setChalkpoints(false);
                            chr.getMap().setChalk(true);
                            chr.getMap().setClosable(true);
                            for (MapleCharacter a1 : chr.getMap().getCharacters()) {
                                chr.getMap().broadcastMessage(MaplePacketCreator.useChalkboard(a1, true));
                            }

                        }
                    }
                }
                if (s.equals(ServerConstants.unscrambleAnswer) && chr.getMap().unscrambleOn()) {
                    chr.getMap().broadcastMessage(MaplePacketCreator.serverNotice(6, "[Unscramble] " + chr.getName() + " has unscrambled correctly!"));
                    chr.getMap().setUnscramble(false);
                    if (chr.getMap().ChalkpointsOn()) {
                        int currentpts;
                        if (chr.getChalkboard() != null) {
                            currentpts = Integer.parseInt(chr.getChalkboard().charAt(0) + "");
                            chr.setChalkboard(currentpts + 1 + " / " + chr.getMap().getPointstowin());
                        } else {
                            chr.setChalkboard("1 / " + chr.getMap().getPointstowin());
                        }

                        chr.getMap().broadcastMessage(MaplePacketCreator.useChalkboard(chr, false));

                        if (Integer.parseInt(chr.getChalkboard().charAt(0) + "") == chr.getMap().getPointstowin()) {
                            chr.getMap().broadcastMessage(MaplePacketCreator.serverNotice(6, chr.getName() + " has reached " + chr.getMap().getPointstowin() + " points, Congratulations!"));
                            chr.getMap().setChalkpoints(false);
                            chr.getMap().setChalk(true);
                            chr.getMap().setClosable(true);
                            for (MapleCharacter a1 : chr.getMap().getCharacters()) {
                                chr.getMap().broadcastMessage(MaplePacketCreator.useChalkboard(a1, true));
                            }

                        }
                    }
                }
                if (s.equals(ServerConstants.speedtypeAnswer) && chr.getMap().speedtypeOn()) {
                    chr.getMap().broadcastMessage(MaplePacketCreator.serverNotice(6, "[Speed Type] " + chr.getName() + " has written down the sentence correctly!"));
                    chr.getMap().setSpeedtype(false);
                    if (chr.getMap().ChalkpointsOn()) {
                        int currentpts;
                        if (chr.getChalkboard() != null) {
                            currentpts = Integer.parseInt(chr.getChalkboard().charAt(0) + "");
                            chr.setChalkboard(currentpts + 1 + " / " + chr.getMap().getPointstowin());
                        } else {
                            chr.setChalkboard("1 / " + chr.getMap().getPointstowin());
                        }

                        chr.getMap().broadcastMessage(MaplePacketCreator.useChalkboard(chr, false));

                        if (Integer.parseInt(chr.getChalkboard().charAt(0) + "") == chr.getMap().getPointstowin()) {
                            chr.getMap().broadcastMessage(MaplePacketCreator.serverNotice(6, chr.getName() + " has reached " + chr.getMap().getPointstowin() + " points, Congratulations!"));
                            chr.getMap().setChalkpoints(false);
                            chr.getMap().setChalk(true);
                            chr.getMap().setClosable(true);
                            for (MapleCharacter a1 : chr.getMap().getCharacters()) {
                                chr.getMap().broadcastMessage(MaplePacketCreator.useChalkboard(a1, true));
                            }

                        }
                    }
                }
                if (s.toLowerCase().equals(ServerConstants.scatAnswer) && chr.getMap().scatOn()) {
                    chr.getMap().broadcastMessage(MaplePacketCreator.serverNotice(6, "[Scattergories] " + chr.getName() + " has written down the correct answer!"));
                    chr.getMap().broadcastMessage(MaplePacketCreator.useChalkboard(chr.getMap().scatGuy(), true));
                    chr.getMap().setScat(false, null);
                    if (chr.getMap().ChalkpointsOn()) {
                        int currentpts;
                        if (chr.getChalkboard() != null) {
                            currentpts = Integer.parseInt(chr.getChalkboard().charAt(0) + "");
                            chr.setChalkboard(currentpts + 1 + " / " + chr.getMap().getPointstowin());
                        } else {
                            chr.setChalkboard("1 / " + chr.getMap().getPointstowin());
                        }

                        chr.getMap().broadcastMessage(MaplePacketCreator.useChalkboard(chr, false));

                        if (Integer.parseInt(chr.getChalkboard().charAt(0) + "") == chr.getMap().getPointstowin()) {
                            chr.getMap().broadcastMessage(MaplePacketCreator.serverNotice(6, chr.getName() + " has reached " + chr.getMap().getPointstowin() + " points, Congratulations!"));
                            chr.getMap().setChalkpoints(false);
                            chr.getMap().setChalk(true);
                            chr.getMap().setClosable(true);
                            for (MapleCharacter a1 : chr.getMap().getCharacters()) {
                                chr.getMap().broadcastMessage(MaplePacketCreator.useChalkboard(a1, true));
                            }

                        }
                    }
                }
                if (s.equals(ServerConstants.ntiAnswer) && chr.getMap().ntiOn()) {
                    chr.getMap().broadcastMessage(MaplePacketCreator.serverNotice(6, "[NTI] " + chr.getName() + " has written down the item's name correctly!"));
                    chr.getMap().clearDrops();
                    chr.getMap().setNti(false);
                    if (chr.getMap().ChalkpointsOn()) {
                        int currentpts;
                        if (chr.getChalkboard() != null) {
                            currentpts = Integer.parseInt(chr.getChalkboard().charAt(0) + "");
                            chr.setChalkboard(currentpts + 1 + " / " + chr.getMap().getPointstowin());
                        } else {
                            chr.setChalkboard("1 / " + chr.getMap().getPointstowin());
                        }

                        chr.getMap().broadcastMessage(MaplePacketCreator.useChalkboard(chr, false));

                        if (Integer.parseInt(chr.getChalkboard().charAt(0) + "") == chr.getMap().getPointstowin()) {
                            chr.getMap().broadcastMessage(MaplePacketCreator.serverNotice(6, chr.getName() + " has reached " + chr.getMap().getPointstowin() + " points, Congratulations!"));
                            chr.getMap().setChalkpoints(false);
                            chr.getMap().setChalk(true);
                            chr.getMap().setClosable(true);
                            for (MapleCharacter a1 : chr.getMap().getCharacters()) {
                                chr.getMap().broadcastMessage(MaplePacketCreator.useChalkboard(a1, true));
                            }

                        }
                    }
                }
                if (s.equals(ServerConstants.cblinkAnswer) && chr.getMap().cblinkOn()) {
                    chr.getMap().broadcastMessage(MaplePacketCreator.serverNotice(6, "[CBlink] " + chr.getName() + " has written down the letters correctly!"));
                    chr.getMap().clearDrops();
                    chr.getMap().broadcastMessage(MaplePacketCreator.getClock(0));
                    chr.getMap().cancelBombs();

                    chr.getMap().setCblink(false);
                    if (chr.getMap().ChalkpointsOn()) {
                        int currentpts;
                        if (chr.getChalkboard() != null) {
                            currentpts = Integer.parseInt(chr.getChalkboard().charAt(0) + "");
                            chr.setChalkboard(currentpts + 1 + " / " + chr.getMap().getPointstowin());
                        } else {
                            chr.setChalkboard("1 / " + chr.getMap().getPointstowin());
                        }

                        chr.getMap().broadcastMessage(MaplePacketCreator.useChalkboard(chr, false));

                        if (Integer.parseInt(chr.getChalkboard().charAt(0) + "") == chr.getMap().getPointstowin()) {
                            chr.getMap().broadcastMessage(MaplePacketCreator.serverNotice(6, chr.getName() + " has reached " + chr.getMap().getPointstowin() + " points, Congratulations!"));
                            chr.getMap().setChalkpoints(false);
                            chr.getMap().setChalk(true);
                            chr.getMap().setClosable(true);
                            for (MapleCharacter a1 : chr.getMap().getCharacters()) {
                                chr.getMap().broadcastMessage(MaplePacketCreator.useChalkboard(a1, true));
                            }

                        }
                    }
                }
                if (s.equals(ServerConstants.miniunscrambleAnswer) && chr.getMap().miniunscrambleOn()) {
                    chr.getMap().broadcastMessage(MaplePacketCreator.serverNotice(6, "[Unscramble] " + chr.getName() + " has unscrambled correctly first!"));
                    chr.getMap().setMiniunscramble(false, chr.getMap().getMiniunscramblehost());
                }
                /*
                     if (s.equals(ServerConstants.compunscrambleAnswer) && chr.getMap().compunscrambleOn())                    
                       chr.getMap().broadcastMessage(MaplePacketCreator.serverNotice(6, "[Unscramble] That is the correct answer!"));
                 */

               

                if (chr.getMap().getBongo() != null && chr.getMap().getBongo().getChosen().getName().equals(chr.getName())) {
                    if (!chr.getMap().getBongo().chosenRight()) {
                        int num;
                        try {
                            num = Integer.valueOf(s);
                        } catch (NumberFormatException nfe) {
                            chr.dropMessage(6, "[Bongo] Please type an integer between 1-3.");
                            return;
                        }
                        if (ServerConstants.bongoAnswer == Integer.valueOf(s)) {
                            chr.getMap().broadcastMessage(MaplePacketCreator.serverNotice(6, "[Bongo] The chosen number was: " + ServerConstants.bongoAnswer + ", please write down a victim's name."));
                            chr.dropMessage(6, "[Bongo] " + Bongo.participantsToString(chr));
                            chr.getMap().getBongo().changeRightState();
                            Bongo.bongoInterval.cancel(true);
                            Bongo.chooseVictimTimer(c.getPlayer().getMap());

                        } else {
                            Bongo.bongoInterval.cancel(true);
                            chr.getMap().broadcastMessage(MaplePacketCreator.serverNotice(6, "[Bongo] The chosen number was: " + ServerConstants.bongoAnswer + ", moving along."));
                            chr.getMap().getBongo().setChosen(Bongo.randomIGNAlive(chr.getMap()));
                        }
                    } else {
                        c.getPlayer().getMap().getBongo().setChosen(null);
                        Bongo.victimTimer.cancel(true);
                        Bongo.bongoInterval.cancel(true);
                        chr.getMap().broadcastMessage(MaplePacketCreator.getClock(10));
                        if (!Bongo.bombChosen(c, s)) {
                            chr.getMap().broadcastMessage(MaplePacketCreator.serverNotice(6, "[Bongo] You failed to provide a correct ign, moving along in 10 seconds!"));
                            Bongo.initiateBongoInterval(c.getPlayer().getMap());
                            Bongo.getRandInt(c.getPlayer().getMap());
                        }
                        final MapleClient client = c;
                        TimerManager.getInstance().schedule(new Runnable() {
                            @Override
                            public void run() {
                                client.getPlayer().getMap().getBongo().setChosen(Bongo.randomIGNAlive(client.getPlayer().getMap()));
                            }
                        }, 10000);
                    }
                }

                if (chr.getBernardid() != 0) {
                    ResultSet rs;
                    String answer = "none", equationans = "";
                    List<String> moreanswers = new ArrayList<>();
                    List<Integer> moreemotes = new ArrayList<>();
                    Item chair = new Item(3010000, (short) 0, (short) 1);;
                    MapleCharacter bernardavatar = BernardManager.instance.getBernard(chr.getBernardid()).getAvatar();
                    Random rand = new Random();
                    int emote = 0;
                    try { // 1st - Trying to find substrings of database bernard sentences like "whos your daddy"
                        PreparedStatement ps;
                        Connection con = DatabaseConnection.getConnection();
                        ps = con.prepareStatement("SELECT * FROM bernardsentences WHERE ? REGEXP sentence");
                        //   ps = con.prepareStatement("SELECT * FROM bernardsentences WHERE sentence = ?");
                        ps.setString(1, s);
                        rs = ps.executeQuery();
                        while (rs.next()) {
                            answer = rs.getString("answer");
                            emote = rs.getInt("emote");
                            if (answer.contains("<player>")) {
                                answer = answer.replace("<player>", chr.getName());
                            }
                            moreanswers.add(answer);
                            moreemotes.add(emote); // In the case of more than 1 answer for the same question

                        }
                    } catch (SQLException exc) {
                        System.out.print("Problem with getting bernard sentences:" + exc);
                    }
                    if (!answer.equals("none")) { // If we did find ones, we'll then publish them here
                        if (moreanswers.size() > 1) {
                            answer = moreanswers.get(rand.nextInt(moreanswers.size()));
                        }
                        chr.getMap().broadcastMessage(MaplePacketCreator.getChatText(bernardavatar.getId(), answer, chr.getWhiteChat(), show));
                        chr.getMap().broadcastMessage(bernardavatar, MaplePacketCreator.facialExpression(bernardavatar, emote), false);
                    }
                    if (s.toLowerCase().contains("bernard sit")) {
                        if (bernardavatar.getInventory(MapleInventoryType.SETUP).findById(3010000) == null) {  // If he doesnt have the chair, Ill just give him one   
                            if (!bernardavatar.getInventory(MapleInventoryType.SETUP).isFull()) // MapleInventoryManipulator.addById(bernardavatar.getClient(), 3010001, (short)1);           
                            {
                                bernardavatar.getInventory(MapleInventoryType.SETUP).addItem(chair);
                            }
                        }
                        bernardavatar.setChair(3010000);
                        chr.getMap().broadcastMessage(bernardavatar, MaplePacketCreator.showChair(bernardavatar.getId(), 3010000), false);
                        bernardavatar.getClient().announce(MaplePacketCreator.enableActions());
                    }
                    if (!formulate(s).equals(639481039.1 + "")) { // 2nd - Trying to find a formula in the text, 639481039.is a dummy number and will return if there isn't any formula 
                        equationans = "The answer is " + formulate(s) + "!";
                        chr.getMap().broadcastMessage(MaplePacketCreator.getChatText(bernardavatar.getId(), equationans, chr.getWhiteChat(), show));
                        chr.getMap().broadcastMessage(bernardavatar, MaplePacketCreator.facialExpression(bernardavatar, rand.nextInt(16) + 1), false);
                    }
                }
                if (chr.getWatcher() != null) {
                    chr.getWatcher().dropMessage(5, "[" + chr.getName() + "] " + s);
                }

            }
        } catch (SQLException | RemoteException ex) {
        }
    }

    public static String formulate(String s) {
        String num1 = "", num2 = "", sign = "none";
        boolean firstdigit = true, end = false, num1nsign = false;
        // s.replaceAll(Character.toString((char)32), "");
        String answer = 639481039.1 + ""; // Randomly picked number to act as our dummy. 
        for (int i = 0; i < s.length() && !end; i++) {
            if (!(s.charAt(i) + "").equals(Character.toString((char) 32))) {
                if (!num1nsign) { // Finding the second number after getting the first number and the operand
                    if (Character.isDigit(s.charAt(i)) || s.charAt(i) == '.' || (s.charAt(i) == '-' && firstdigit)) { // Getting the first number
                        if (firstdigit) {
                            if (s.charAt(i) == '.') {
                                num1 = "0.";
                            } else if (s.charAt(i) == '-') {
                                num1 = "-";
                            } else {
                                num1 = s.charAt(i) + "";
                            }
                            firstdigit = false;
                        } else {
                            num1 += s.charAt(i);
                        }
                    } else {
                        if (!num1.isEmpty()) { // If it doesnt exist, I wont do anything
                            if (s.charAt(i) == '+' || s.charAt(i) == '-' || s.charAt(i) == '*' || s.charAt(i) == '/' || s.charAt(i) == 'x' || s.charAt(i) == ':' || s.charAt(i) == '^') { // If right after the first number theres one of this sign, Ill save them and say that were searching for num2
                                sign = s.charAt(i) + "";
                                num1nsign = true;
                            } else { // If theres no operand, Ill just reset num1 and search for the first digit once again                      
                                num1 = "";
                            }
                            firstdigit = true;
                        }
                    }
                } else {
                    if (Character.isDigit(s.charAt(i)) || s.charAt(i) == '.' || (s.charAt(i) == '-' && firstdigit)) { // Getting the first number
                        if (firstdigit) {
                            if (s.charAt(i) == '.') {
                                num2 = "0.";
                            } else if (s.charAt(i) == '-') {
                                num2 = "-";
                            } else {
                                num2 = s.charAt(i) + "";
                            }
                            firstdigit = false;
                        } else {
                            num2 += s.charAt(i);
                        }
                    } else {
                        if (firstdigit) { // Checking to see if the first things after the operand is a number, if it isnt the formulas broken and thus the process begins once more!
                            num1nsign = false;
                            num1 = "";
                            sign = "none";
                        } else {
                            end = true; // Found the formula  
                        }
                    }
                }
            }
        }
        double powered = 0;
        if (!num1.isEmpty() && !num2.isEmpty() && !sign.equals("none")) // Theres a formula
        {
            switch (sign) {
                case "+":
                    answer = Double.parseDouble(num1) + Double.parseDouble(num2) + "";
                    break;
                case "-":
                    answer = Double.parseDouble(num1) - Double.parseDouble(num2) + "";
                    break;
                case "x":
                case "*":
                    answer = Double.parseDouble(num1) * Double.parseDouble(num2) + "";
                    break;
                case "/":
                    answer = Double.parseDouble(num1) / Double.parseDouble(num2) + "";
                    break;
                case "^":
                    powered = 1;
                    for (int i = 0; i < Math.round(Double.parseDouble(num2)); i++) {
                        powered *= Double.parseDouble(num1);
                    }
                    if (Double.parseDouble(num2) - Math.round(Double.parseDouble(num2)) > 0) {
                        answer = "~" + powered + "";
                    } else {
                        answer = powered + "";
                    }
                    break;
            }

        }

        return answer;
    }

}
