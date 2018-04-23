/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client.command;

import client.MapleBuffStat;
import client.MapleCharacter;
import client.MapleClient;
import client.MapleDisease;
import client.MapleJob;
import client.MapleSkinColor;
import client.MapleStat;
import client.SkillFactory;
import client.events.Bongo;
import client.events.Event;
import client.events.EventCommands;
import client.events.EventHandler2;
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import client.inventory.MaplePet;
import constants.ItemConstants;
import constants.ServerConstants;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import net.MaplePacketHandler;
import net.PacketProcessor;
import net.server.Server;
import net.server.channel.Channel;
import net.server.world.MaplePartyCharacter;
import provider.MapleData;
import provider.MapleDataProvider;
import provider.MapleDataProviderFactory;
import provider.MapleDataTool;
import scripting.event.EventHandler;
import scripting.event.OlympicsHandler;
import scripting.npc.NPCScriptManager;
import scripting.portal.PortalScriptManager;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.MapleShopFactory;
import server.TimerManager;
import server.expeditions.MapleExpedition;
import server.life.MapleLifeFactory;
import server.life.MapleMonster;
import server.life.MapleMonsterInformationProvider;
import server.life.MapleMonsterStats;
import server.life.MobSkillFactory;
import server.maps.MapleMap;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.maps.PlayerNPCs;
import tools.DatabaseConnection;
import tools.FilePrinter;
import tools.HexTool;
import tools.MapleLogger;
import tools.MaplePacketCreator;
import tools.Pair;
import tools.StringUtil;
import tools.data.input.ByteArrayByteStream;
import tools.data.input.GenericSeekableLittleEndianAccessor;
import tools.data.input.SeekableLittleEndianAccessor;
import tools.data.output.MaplePacketLittleEndianWriter;

/**
 *
 * @author Administrator
 */
public class GMCommands {

    
    private static MapleClient tmp = null;
    
    private static String[] songs = {
        "Jukebox/Congratulation",
        "Bgm00/SleepyWood",
        "Bgm00/FloralLife",
        "Bgm00/GoPicnic",
        "Bgm00/Nightmare",
        "Bgm00/RestNPeace",
        "Bgm01/AncientMove",
        "Bgm01/MoonlightShadow",
        "Bgm01/WhereTheBarlogFrom",
        "Bgm01/CavaBien",
        "Bgm01/HighlandStar",
        "Bgm01/BadGuys",
        "Bgm02/MissingYou",
        "Bgm02/WhenTheMorningComes",
        "Bgm02/EvilEyes",
        "Bgm02/JungleBook",
        "Bgm02/AboveTheTreetops",
        "Bgm03/Subway",
        "Bgm03/Elfwood",
        "Bgm03/BlueSky",
        "Bgm03/Beachway",
        "Bgm03/SnowyVillage",
        "Bgm04/PlayWithMe",
        "Bgm04/WhiteChristmas",
        "Bgm04/UponTheSky",
        "Bgm04/ArabPirate",
        "Bgm04/Shinin'Harbor",
        "Bgm04/WarmRegard",
        "Bgm05/WolfWood",
        "Bgm05/DownToTheCave",
        "Bgm05/AbandonedMine",
        "Bgm05/MineQuest",
        "Bgm05/HellGate",
        "Bgm06/FinalFight",
        "Bgm06/WelcomeToTheHell",
        "Bgm06/ComeWithMe",
        "Bgm06/FlyingInABlueDream",
        "Bgm06/FantasticThinking",
        "Bgm07/WaltzForWork",
        "Bgm07/WhereverYouAre",
        "Bgm07/FunnyTimeMaker",
        "Bgm07/HighEnough",
        "Bgm07/Fantasia",
        "Bgm08/LetsMarch",
        "Bgm08/ForTheGlory",
        "Bgm08/FindingForest",
        "Bgm08/LetsHuntAliens",
        "Bgm08/PlotOfPixie",
        "Bgm09/DarkShadow",
        "Bgm09/TheyMenacingYou",
        "Bgm09/FairyTale",
        "Bgm09/FairyTalediffvers",
        "Bgm09/TimeAttack",
        "Bgm10/Timeless",
        "Bgm10/TimelessB",
        "Bgm10/BizarreTales",
        "Bgm10/TheWayGrotesque",
        "Bgm10/Eregos",
        "Bgm11/BlueWorld",
        "Bgm11/Aquarium",
        "Bgm11/ShiningSea",
        "Bgm11/DownTown",
        "Bgm11/DarkMountain",
        "Bgm12/AquaCave",
        "Bgm12/DeepSee",
        "Bgm12/WaterWay",
        "Bgm12/AcientRemain",
        "Bgm12/RuinCastle",
        "Bgm12/Dispute",
        "Bgm13/CokeTown",
        "Bgm13/Leafre",
        "Bgm13/Minar'sDream",
        "Bgm13/AcientForest",
        "Bgm13/TowerOfGoddess",
        "Bgm14/DragonLoad",
        "Bgm14/HonTale",
        "Bgm14/CaveOfHontale",
        "Bgm14/DragonNest",
        "Bgm14/Ariant",
        "Bgm14/HotDesert",
        "Bgm15/MureungHill",
        "Bgm15/MureungForest",
        "Bgm15/WhiteHerb",
        "Bgm15/Pirate",
        "Bgm15/SunsetDesert",
        "Bgm16/Duskofgod",
        "Bgm16/FightingPinkBeen",
        "Bgm16/Forgetfulness",
        "Bgm16/Remembrance",
        "Bgm16/Repentance",
        "Bgm16/TimeTemple",
        "Bgm17/MureungSchool1",
        "Bgm17/MureungSchool2",
        "Bgm17/MureungSchool3",
        "Bgm17/MureungSchool4",
        "Bgm18/BlackWing",
        "Bgm18/DrillHall",
        "Bgm18/QueensGarden",
        "Bgm18/RaindropFlower",
        "Bgm18/WolfAndSheep",
        "Bgm19/BambooGym",
        "Bgm19/CrystalCave",
        "Bgm19/MushCatle",
        "Bgm19/RienVillage",
        "Bgm19/SnowDrop",
        "Bgm20/GhostShip",
        "Bgm20/NetsPiramid",
        "Bgm20/UnderSubway",
        "Bgm21/2021year",
        "Bgm21/2099year",
        "Bgm21/2215year",
        "Bgm21/2230year",
        "Bgm21/2503year",
        "Bgm21/KerningSquare",
        "Bgm21/KerningSquareField",
        "Bgm21/KerningSquareSubway",
        "Bgm21/TeraForest",
        "BgmEvent/FunnyRabbit",
        "BgmEvent/FunnyRabbitFaster",
        "BgmEvent/wedding",
        "BgmEvent/weddingDance",
        "BgmEvent/wichTower",
        "BgmGL/amoria",
        "BgmGL/Amorianchallenge",
        "BgmGL/chapel",
        "BgmGL/cathedral",
        "BgmGL/Courtyard",
        "BgmGL/CrimsonwoodKeep",
        "BgmGL/CrimsonwoodKeepInterior",
        "BgmGL/GrandmastersGauntlet",
        "BgmGL/HauntedHouse",
        "BgmGL/NLChunt",
        "BgmGL/NLCtown",
        "BgmGL/NLCupbeat",
        "BgmGL/PartyQuestGL",
        "BgmGL/PhantomForest",
        "BgmJp/Feeling",
        "BgmJp/BizarreForest",
        "BgmJp/Hana",
        "BgmJp/Yume",
        "BgmJp/Bathroom",
        "BgmJp/BattleField",
        "BgmJp/FirstStepMaster",
        "BgmMY/Highland",
        "BgmMY/KualaLumpur",
        "BgmSG/BoatQuay_field",
        "BgmSG/BoatQuay_town",
        "BgmSG/CBD_field",
        "BgmSG/CBD_town",
        "BgmSG/Ghostship",
        "BgmUI/ShopBgm",
        "BgmUI/Title"
    };

     
    static String eventstarter = "notagger";
    private static HashMap<String, String> eventcommands = new HashMap<>();
    public static EventHandler2 newsystem = new EventHandler2();
    public static EventHandler event = new EventHandler();

    public static boolean executeGMCommandLv5(MapleClient c, String[] sub, char heading) throws SQLException {
        MapleCharacter player = c.getPlayer();

        Channel cserv = c.getChannelServer();
        Server srv = Server.getInstance();
        if (sub[0].equals("ppmap")) {
          if(sub.length > 1){
            int amount = Integer.parseInt(sub[1]);
            for (MapleCharacter victim : c.getPlayer().getMap().getCharacters()) {
                victim.gainParticipationPoints(amount);
                victim.dropMessage("You have gained " + amount + " participation points! You now have " + victim.getParticipationPoints() + " participation points.");
            }
            player.dropMessage("Done! Gave the map " + amount + " particiaption points.");
          } else {
              for (MapleCharacter victim : c.getPlayer().getMap().getCharacters()) {
                victim.gainParticipationPoints(1);
                victim.dropMessage("You have gained " + 1 + " participation point! You now have " + victim.getParticipationPoints() + " participation points.");
            }
            player.dropMessage("Done! Gave the map " + 1 + " participaption point."); 
          }
     } else if (sub[0].equals("ebanplayer")) {
            String name = sub[1];

            if (event.isEventBanned(name)) {
                player.dropMessage("This person is already banned from using @joinevent.");

            } else {
                event.eBanPlayer(name);
                player.dropMessage("The player is now banned from using @joinevent.");

            }

        } else if (sub[0].equals("eunbanall")) {
            event.bannedplayers.clear();
            player.dropMessage("Everyone is now unbanned!"); // for now, GM use only to see if it works
        
        } else if (sub[0].equals("eunbanplayer")) {
            String name = sub[1];
            if (event.isEventBanned(name)) {
                event.eUnbanPlayer(name);
                player.dropMessage("The player is now unbanned from using @joienvent.");
            } else {
                player.dropMessage("The person is not even event banned!!");
            }
            
            } else if (sub[0].equals("oxmap") || sub[0].equals("ox")) {
            player.changeMap(109020001);
        } else if (sub[0].equals("minigame")) { 
            player.changeMap(109070000);
        } else if (sub[0].equals("wr")) { 
            player.changeMap(109010203);
        } else if (sub[0].equals("waterm")) { 
            switch(sub[1]){
                case "1":                    
             player.changeMap(922210000);
                    break;
                      case "2":                    
             player.changeMap(922210100);
                    break;
                      case "3":                    
             player.changeMap(922210200);
                    break;
                      case "end":
                      case "4":                    
             player.changeMap(922210300);
                    break;
                      default:
                          player.dropMessage(5,"Error. Please type the command as follows !waterm 1/2/3/4 or end");
                          break;
                    
            }
            // player.changeMap(922210000);
        } else if (sub[0].equals("alq")) { 
            player.changeMap(610030400);   
        } else if (sub[0].equals("slender")) { 
            player.changeMap(261030001);    
        } else if (sub[0].equals("impbobmap")) {
            player.changeMap(109010104);
        } else if (sub[0].equals("sotf")) {
            player.changeMap(109010100);
        } else if (sub[0].equals("event")) {
            /*String eventname = StringUtil.joinStringFrom(sub, 2);
            if (!event.isRunning()) {
                if (eventname.length() < 1) {
                    player.dropMessage("Please make sure you put in an event name!");
                } else {
                    event.openEvent(player.getClient(), eventname);

                }
            } else {
                player.dropMessage("There is already an event running right now!");
            }*/
            if(sub[1].equals("new")) EventCommands.createEvent(c);
            else if(sub[1].equals("name")) EventCommands.eventName(c, StringUtil.joinStringFrom(sub, 2));
            else if(sub[1].equals("help")) EventCommands.eventHelp(c);
            else if(sub[1].equals("start")) EventCommands.eventStart(c);
            else if(sub[1].equals("nextround")) EventCommands.nextRound(c);
            else if(sub[1].equals("portal")) EventCommands.setSpawnPoint(c);
            else if(sub[1].equals("timer")) EventCommands.setTimer(c, Integer.valueOf(sub[2]));
            else if(sub[1].equals("end")) EventCommands.eventEnd(c);
            
             } else if (sub[0].equals("eventcommands")) {
            String[] eventcommands = new String[15];
             player.dropMessage(6,"============================================");
                player.dropMessage(6,"                                  Event Commands                      ");
                player.dropMessage(6,"============================================");
            eventcommands[0] = "!event <eventname> - Opens up an event in the current map.";
            eventcommands[1] = "!closeevent - Closes the event and announces the winners.";
            eventcommands[2] = "!opengates - Opens the event gates.";
            eventcommands[3] = "!addwinner <name> - Adds the winner to the event winner list.";
            eventcommands[4] = "!removewinner <name> - Removes a point from the winner.";
            eventcommands[5] = "!addpoint <name> - Adds a point to the player.";
            eventcommands[6] = "!eventmvp <player> - Adds an event MVP if your choice.";
            eventcommands[7] = "!ebanplayer <name> - Bans the player from joining event.";
            eventcommands[8] = "!eunbanplayer <name> - Unbans the player from joining the event.";
            eventcommands[9] = "!eunbanall - Unbans everyone from @joinevent.";
            eventcommands[10] = "!tk e/d - Makes players who @joinevent die when enabled";
            eventcommands[11] = "!jt e/d - Enables/Disables @jointag for players";
            eventcommands[12] = "!resetr/resetres - Resets Ressurection cooldown";
            

            for (int i = 0; i < eventcommands.length; i++) {
                player.dropMessage(6,eventcommands[i]);
            }
            
            } else if (sub[0].equals("opengates")) {
            if (event.isRunning()) {
                if (!event.isOpen) {
                    event.isOpen = true;
                    Server.getInstance().broadcastMessage(MaplePacketCreator.serverNotice(6, "[Event] the gates are now open again!"));
                } else {
                    player.dropMessage("The gates are already open.");
                }
            } else {
                player.dropMessage("First open up an event!");
            }
        } else if (sub[0].equals("closegates")) {
            if (event.isRunning()) {
                if (event.isOpen) {
                    event.isOpen = false;
                    Server.getInstance().broadcastMessage(MaplePacketCreator.serverNotice(6, "[Event] the gates are now closed."));
                } else {
                    player.dropMessage("The gates are already closed.");
                }
            } else {
                player.dropMessage("First open up an event!");
            }
            } else if (sub[0].equals("tk")) {
              if(sub.length > 1){
                if(sub[1].equals("d")){
                    newsystem.e.setDeathonJoin(false);
                    player.dropMessage("Players will now not die when entering the event!");
                }
                else {
                    newsystem.e.setDeathonJoin(true);
                    player.dropMessage("Default Mode - Players will now die.");
                }
                    
              }
              else
                  player.dropMessage("Error. Please type the command as follows !tk e/d (enable/disable)");
         } else if (sub[0].equals("jt")) {
              if(sub.length > 1){
                if(sub[1].equals("e")){
                    newsystem.e.setJt(true);
                    player.dropMessage("Players will now be able to @jt/@jointag");
                } else {
                   newsystem.e.setJt(false);
                    player.dropMessage("Default Mode - Players will now die.");
                }      
              } else {
                 player.dropMessage("Error. Please type the command as follows !jt e/d (enable/disable)");
             }   
              
          } else if (sub[0].equals("resetr") || sub[0].equals("resetres")) {
              if(sub.length > 1){
                  MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]);
                  if(victim != null){
                     victim.getClient().announce(MaplePacketCreator.skillCooldown(2321006,0));
                  victim.cancelResstime();                
                  victim.setRess(true); 
                  victim.dropMessage(5,"Your ress cooldown has been reset.");
                  player.dropMessage(5,"You've reset " + victim.getName() + "'s ress cooldown.");
                  } else {
                      player.dropMessage(5,"Error. Type the command as follows !resetr/resetres for the whole map or !resetr/resetres for a specific player.");
                  }
              } else {
              for(MapleCharacter a1 : player.getMap().getCharacters())
              {
                  a1.getClient().announce(MaplePacketCreator.skillCooldown(2321006,0));
                  a1.cancelResstime();                
                  a1.setRess(true);
                  //a1.cancelRessCd();
                  a1.dropMessage(5,"Your ress cooldown has been reset.");
              }
              }
              
          } else if (sub[0].equals("setptlimit") || sub[0].equals("setptl") ||  sub[0].equals("setptlim") ||  sub[0].equals("sptl") ) {
              if(sub[1].equals("o") || sub[1].equals("off")){
                  player.getMap().setPartyLimitBol(false);
                  player.dropMessage(5,"You've turned party limit off.");
              }
              else{
                  int limit = 0;
                  if(Integer.parseInt(sub[1]) > 0)
                    limit = Integer.parseInt(sub[1]);  
              player.getMap().setPartyLimit(limit);
              player.getMap().setPartyLimitBol(true);
              for(MapleCharacter a1 : player.getMap().getCharacters()){     
                      if(!(a1.getParty() == null))
                          a1.disbandParty();
                     
                  a1.dropMessage(5,"Amount of players per party has been set to " + sub[1] + ".");
              }
              }
           } else if (sub[0].equals("checkpt")) {
               if(sub.length > 1){
                 MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]);
                 if(victim != null){
                   if(victim.getParty() != null){
                       String text = victim.getName() + " is partied with: ";
                       String onoff = "", name = "";
                       for(MaplePartyCharacter mpc : victim.getParty().getMembers())
                           if(!mpc.getPlayer().getName().equals(victim.getName())){
                               name = mpc.getPlayer().getName();
                               if(c.getWorldServer().getPlayerStorage().getCharacterByName(name) != null)
                                   onoff = "Online";
                               else
                                   onoff = "Offline";
                               text+= name + " (" + onoff + "), ";
                           }
                       if(!onoff.equals("")){
                           text = text.substring(0,text.length() -2);
                           text+= ".";
                       }
                       player.dropMessage(5,text);
                           
                   } else {
                         player.dropMessage(5,victim.getName() + " isn't in a party.");
                     }
                 } else {
                       player.dropMessage(5,"Error. Player doesnt exist.");
                   }
               } else {
                   player.dropMessage(5,"Error. Please type the command as follows !checkpt <player>");
               }
               
         } else if (sub[0].equals("closeevent")) {
            if (event.isRunning()) {

                StringBuilder sb = new StringBuilder();

                event.closeEvent();
                /*  for (MapleCharacter person : c.getPlayer().getMap().getCharacters())
                {
                    if (person.gmLevel() < 1)
                    {
                    person.changeMap(910000000);
                }
                } */
                for (String key : event.winners.keySet()) {
                    sb.append(key).append("[").append(event.winners.get(key)).append("]").append(", ");
                }
                if (sb.length() > 0) {
                    sb.setLength(sb.length() - 2);
                    if (event.eventMvp.isEmpty()) {
                        event.closeEventMessage("The event has ended! Congratulations to the following players for winning: " + sb);
                    } else {
                        event.closeEventMessage("The event has ended! Congratulations to the following players for winning: " + sb + ", and Congratulations to our MVP.. " + event.eventMvp + "! You're most welcome to whisper him or her your congratulations!");
                    }

                    event.bannedplayers.clear();
                } else {
                    if (event.eventMvp.isEmpty()) {
                        event.closeEventMessage("The event has ended! There were no winners!");
                    } else {
                        event.closeEventMessage("The event has ended! There were no winners! Even so, Congratulations to our MVP.. " + event.eventMvp + "! You're most welcome to whisper him or her your congratulations!");
                    }
                    event.bannedplayers.clear();
                }
                event.eventMvp = "";
            } else {
                player.dropMessage("There is no event running to close!");
            }
        } else if (sub[0].equals("adderp")) { // Manual addition
            if (sub.length > 2) {
                MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]);
                if (victim != null) {
                    victim.addErp(Integer.parseInt(sub[2]));
                    victim.dropMessage(6, "You've just gained " + sub[2] + "erp!");
                    player.dropMessage(6, "You've given " + victim.getName() + " " + sub[2] + " erp!");
                }
            } else {
                player.dropMessage(5, "Error. Please type the command as follows !adderp <player> <amount>");
            }
        } else if (sub[0].equals("removewinner")) {
            if (event.isRunning()) {
                String name = StringUtil.joinStringFrom(sub, 1);

                if (event.winners.containsKey(name)) {
                    event.winners.remove(name);
                    MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(name);
                    if (victim != null) {
                        victim.addErp(-1);
                    }
                } else {
                    player.dropMessage("This player is not added to the winners list yet!");
                }
            } else {
                player.dropMessage(5, "Error. No event running atm.");
            }
        } else if (sub[0].equals("eventmvp")) {
            if (event.isRunning()) {
                if (sub.length > 1) {
                    MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]);
                    if (victim != null) {
                        event.eventMvp = victim.getName();
                        player.dropMessage(5, "You've chosen " + victim.getName() + " to be this event's MVP!");
                    } else {
                        player.dropMessage(5, "Error. You have to choose a participant to be this events MVP.");
                    }
                } else {
                    player.dropMessage(5, "Error. Write the command as follows !eventmvp <player>");
                }
            } else {
                player.dropMessage(5, "Error. No event running atm.");
            }
        } else if (sub[0].equals("addwinner")) {
            if (event.isRunning()) {
                String name = StringUtil.joinStringFrom(sub, 1);
                if (!event.winners.containsKey(name)) {
                    event.winners.put(name, 1);
                    
                    event.wintodb += name + ", ";
                    MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(name);
                    if (victim != null) {
                        victim.addErp(1);
                        if(victim.getGuild() != null) {
                            victim.getGuild().gainGP(10);
                        }
                    }
                    player.dropMessage("The player has been added to the winners list with 1 point.");
                } else {
                    player.dropMessage("This winner is already added. Use !addpoint <name> to add a point.");
                }
            } else {
                player.dropMessage(5, "Error. No event running atm.");
            }
        } else if (sub[0].equals("addpoint")) {
            if (event.isRunning()) {
                String name = StringUtil.joinStringFrom(sub, 1);
                if (event.winners.containsKey(name)) {
                    int points = event.winners.get(name);
                    event.winners.put(name, points + 1);
                    player.dropMessage("1 point has been added to " + name + ". They now have a total of " + event.winners.get(name) + " points.");
                } else {
                    player.dropMessage("Please ensure you have added the player first by doing !addwinner <name>");
                }
            } else {
                player.dropMessage(5, "Error. No event running atm.");
            }
            
            } else if (sub[0].equals("diseasemap")) {
            for (MapleCharacter victim : player.getMap().getCharacters()) {
                int type = 0;
                if (sub[2].equalsIgnoreCase("SEAL")) {
                    type = 120;
                } else if (sub[2].equalsIgnoreCase("DARKNESS")) {
                    type = 121;
                } else if (sub[2].equalsIgnoreCase("WEAKEN")) {
                    type = 122;
                } else if (sub[2].equalsIgnoreCase("STUN")) {
                    type = 123;
                } else if (sub[2].equalsIgnoreCase("POISON")) {
                    type = 125;
                } else if (sub[2].equalsIgnoreCase("SEDUCE")) {
                    type = 128;
                } else {
                    player.dropMessage("ERROR.");
                }
                MobSkillFactory.setLong(true);
                victim.giveDebuff(MapleDisease.getType(type), MobSkillFactory.getMobSkill(type, Integer.parseInt(sub[1]))); // wat
            }
        } else if (sub[0].equalsIgnoreCase("dispelmap") || sub[0].equalsIgnoreCase("dispelm")) {
            for (MapleCharacter a1 : player.getMap().getCharacters()) {
                a1.dispelDebuffs(true);
                a1.dropMessage(5, "Dispelled.");
            }
        } else if (sub[0].equalsIgnoreCase("dispel")) {
            if (sub.length > 1) {
                MapleCharacter victim = cserv.getPlayerStorage().getCharacterByName(sub[1]);
                if (victim != null) {
                    victim.dispelDebuffs(true);
                }
                victim.dropMessage(5, "Dispelled.");
            } else {
                player.dropMessage(5, "Error. Please type the command as follows !dispel <name>");
            }
        } else if (sub[0].equalsIgnoreCase("seduce")) {
            MapleCharacter victim = cserv.getPlayerStorage().getCharacterByName(sub[1]);
            int level = Integer.parseInt(sub[2]);
            if (victim != null) {
                victim.setChair(0);
                victim.getClient().getSession().write(MaplePacketCreator.cancelChair(-1));
                victim.getMap().broadcastMessage(victim, MaplePacketCreator.showChair(victim.getId(), 0), false);
                victim.giveDebuff(MapleDisease.SEDUCE, MobSkillFactory.getMobSkill(128, level));
            } else {
                player.dropMessage("Player is not on.");
            }
            } else if (sub[0].equalsIgnoreCase("charview")) {
			if (player.getCharToggle() == true) {
				player.setCharToggle(false);
				player.dropMessage(6,
						"You have set your Char Toggle OFF. Players can click you now.");
			} else if (!player.getCharToggle()) {
				player.setCharToggle(true);
				player.dropMessage(6,
						"You have set your Char Toggle ON. Players can't click you now.");
			}
        } else if (sub[0].equalsIgnoreCase("seducemap")) {
			for (MapleCharacter victim : player.getMap().getCharacters()) {
				int level = Integer.parseInt(sub[1]);
				if (victim != null && !victim.isGM1()) {
					victim.setChair(-1);
					victim.getMap().broadcastMessage(victim, MaplePacketCreator.showChair(victim.getId(), 0), false);
                    victim.giveDebuff(MapleDisease.SEDUCE, MobSkillFactory.getMobSkill(128, level));
                } else {
					player.dropMessage("Player is not on.");
				}
			}
        } else if (sub[0].equalsIgnoreCase("stun")) {
            MapleCharacter victim = cserv.getPlayerStorage().getCharacterByName(sub[1]); // leggo!uh did ubuild yaesxxxxxxxxxxxx
            if (victim != null) {
                victim.setChair(0);
                victim.getClient().getSession().write(MaplePacketCreator.cancelChair(-1));
                victim.getMap().broadcastMessage(victim, MaplePacketCreator.showChair(victim.getId(), 0), false);
                victim.givePermDebuff(MapleDisease.STUN, MobSkillFactory.getMobSkill(123, 1));
            } else {
                player.dropMessage("Player is not on.");
            }
        } else if (sub[0].equalsIgnoreCase("stunmap") ||sub[0].equalsIgnoreCase("stunm")) {
            for (MapleCharacter victim : player.getMap().getCharacters()) {
                if (victim != null && (!victim.isGM() || victim.isTemp())) {
                    victim.setChair(0);
                    victim.getClient().getSession().write(MaplePacketCreator.cancelChair(-1));
                    victim.getMap().broadcastMessage(victim, MaplePacketCreator.showChair(victim.getId(), 0), false);
                    victim.givePermDebuff(MapleDisease.STUN, MobSkillFactory.getMobSkill(123, 1));
                }
            }
         } else if (sub[0].equalsIgnoreCase("confuse")) {    
          MapleCharacter victim = cserv.getPlayerStorage().getCharacterByName(sub[1]); // leggo!uh did ubuild yaesxxxxxxxxxxxx
            if (victim != null) {
                victim.setChair(0);
                victim.getClient().getSession().write(MaplePacketCreator.cancelChair(-1));
                victim.getMap().broadcastMessage(victim, MaplePacketCreator.showChair(victim.getId(), 0), false);
                victim.givePermDebuff(MapleDisease.CONFUSE, MobSkillFactory.getMobSkill(132, 1));
            } else {
                player.dropMessage("Player is not on.");
            }  
         } else if (sub[0].equalsIgnoreCase("confusemap") || sub[0].equalsIgnoreCase("confusem") ) {    
          for (MapleCharacter victim : player.getMap().getCharacters()) {
                if (victim != null && (!victim.isGM() || victim.isTemp())) {
                    victim.getClient().getSession().write(MaplePacketCreator.cancelChair(-1));
                    victim.getMap().broadcastMessage(victim, MaplePacketCreator.showChair(victim.getId(), 0), false);
                    victim.givePermDebuff(MapleDisease.CONFUSE, MobSkillFactory.getMobSkill(132, 1));
                }
            } 
         } else if (sub[0].equalsIgnoreCase("seal")) {
            MapleCharacter victim = cserv.getPlayerStorage().getCharacterByName(sub[1]); // leggo!uh did ubuild yaesxxxxxxxxxxxx
            if (victim != null) {
                victim.setChair(0);
                victim.getClient().getSession().write(MaplePacketCreator.cancelChair(-1));
                victim.getMap().broadcastMessage(victim, MaplePacketCreator.showChair(victim.getId(), 0), false);
                victim.givePermDebuff(MapleDisease.SEAL, MobSkillFactory.getMobSkill(120, 1));
            } else {
                player.dropMessage("Player is not on.");
            } 
        } else if (sub[0].equalsIgnoreCase("sealmap") || sub[0].equalsIgnoreCase("sealm")) {
            for (MapleCharacter victim : player.getMap().getCharacters()) {
                if (victim != null && (!victim.isGM() || victim.isTemp())) {
                    victim.getClient().getSession().write(MaplePacketCreator.cancelChair(-1));
                    victim.getMap().broadcastMessage(victim, MaplePacketCreator.showChair(victim.getId(), 0), false);
                    victim.givePermDebuff(MapleDisease.STUN, MobSkillFactory.getMobSkill(120, 1));
                }
            }
            
            } else if (sub[0].equals("autoaggro")) {
            if (MapleLifeFactory.isAggro()) {
                MapleLifeFactory.setAggro(false);
                player.dropMessage(5, "You've deactivated auto-aggro.");

            } else {
                MapleLifeFactory.setAggro(true);
                player.dropMessage(5, "You've set mobs to be aggressive.");
            }

            /* } else if (sub[0].equals("aggrooff")) {  
                      MapleLifeFactory.setAggro(false);
                       player.dropMessage(6,"You've deactivated auto-aggro"); */
        } else if (sub[0].equals("bomb")) {
            if (sub.length > 1) {
                MapleCharacter victim = c.getWorldServer().getPlayerStorage().getCharacterByName(sub[1]);
                victim.getMap().spawnBombOnGroundBelow(MapleLifeFactory.getMonster(9300166), victim.getPosition());
                Server.getInstance().broadcastGMMessage(MaplePacketCreator.serverNotice(5, player.getName() + " used !bomb on " + victim.getName()));
            } else {
                player.getMap().spawnBombOnGroundBelow(MapleLifeFactory.getMonster(9300166), player.getPosition());
            }

        } else if (sub[0].equals("randign") || sub[0].equals("r") || sub[0].equals("ign")) {        
            Random rand = new Random();            
            Collection<MapleCharacter> chars = player.getMap().getCharacters();
            List<MapleCharacter> charlist = new ArrayList<>();  
            
         
           if(sub.length > 1){
               if(sub[1].equals("d") || sub[1].equals("dead")){
                    for(MapleCharacter a1 : chars)
                     if((!a1.isGM() || a1.isTemp()) && !a1.isAlive())
                    charlist.add(a1);
               }
               else if(sub[1].equals("a") || sub[1].equals("alive")){
                 for(MapleCharacter a1 : chars)
                     if((!a1.isGM() || a1.isTemp()) && a1.isAlive())
                    charlist.add(a1);  
               }
               else{
                   for(MapleCharacter a1 : chars)
                     if(!a1.isGM() || a1.isTemp())
                    charlist.add(a1);
               }
           }
           else{
               for(MapleCharacter a1 : chars)
                     if(!a1.isGM() || a1.isTemp())
                    charlist.add(a1);
           }
           
           
                     
            int amountofigns = 1, randnum = 0;
            String igns = "";
            
            if(sub.length > 1){
              if(sub.length > 2){
                 if(Integer.parseInt(sub[2]) >= charlist.size()){
                    for(int i = 0; i < charlist.size();i++)
                        igns+= charlist.get(i).getName() + ", ";
                }
                else{
                    amountofigns = Integer.parseInt(sub[2]);
                     for(int i = 0; i < amountofigns; i ++){
                         randnum = rand.nextInt(charlist.size());
                    igns+= charlist.get(randnum).getName() + ", ";
                    charlist.remove(randnum);                   
                }
             }
               
                igns = igns.substring(0,igns.length() -2);
                 if(sub[1].equals("d") || sub[1].equals("dead") )
                     player.getMap().broadcastMessage(MaplePacketCreator.sendYellowTip("The chosen dead players were: " + igns));
                else if(sub[1].equals("a") || sub[1].equals("alive") )
                     player.getMap().broadcastMessage(MaplePacketCreator.sendYellowTip("The chosen alive players were: " + igns));
                 else
                     player.getMap().broadcastMessage(MaplePacketCreator.sendYellowTip("The chosen players were: " + igns));
              }
              else if(Character.isDigit(sub[1].charAt(0))){
                if(Integer.parseInt(sub[1]) >= charlist.size()){
                    for(int i = 0; i < charlist.size();i++)
                        igns+= charlist.get(i).getName() + ", ";
                }
                else{
                    amountofigns = Integer.parseInt(sub[1]);
                     for(int i = 0; i < amountofigns; i ++){
                         randnum = rand.nextInt(charlist.size());
                    igns+= charlist.get(randnum).getName() + ", ";
                    charlist.remove(randnum);                   
                }
             }
               
                igns = igns.substring(0,igns.length() -2);
                  player.getMap().broadcastMessage(MaplePacketCreator.sendYellowTip("The chosen players were: " + igns));
            }
              else{
                 if(sub[1].equals("d") || sub[1].equals("dead") ){
                     igns = charlist.get(rand.nextInt(charlist.size())).getName();               
                  player.getMap().broadcastMessage(MaplePacketCreator.sendYellowTip("Dead player selected was: " + igns));
                 } 
                 else if(sub[1].equals("a") || sub[1].equals("alive") ){
                  igns = charlist.get(rand.nextInt(charlist.size())).getName();
               
                  player.getMap().broadcastMessage(MaplePacketCreator.sendYellowTip("Alive player selected was: " + igns));
              }
                 else{
                   igns = charlist.get(rand.nextInt(charlist.size())).getName();
                  player.getMap().broadcastMessage(MaplePacketCreator.sendYellowTip("Random player selected was: " + igns));
                         }
              }
                            
        } else{
                 igns = charlist.get(rand.nextInt(charlist.size())).getName();
               
                  player.getMap().broadcastMessage(MaplePacketCreator.sendYellowTip("Random player selected was: " + igns));
            }
            
       /*     
        } else if (sub[0].equals("randignl") || sub[0].equals("randliveign")) {        
            Random rand = new Random();            
            Collection<MapleCharacter> chars = player.getMap().getCharacters();
             List<MapleCharacter> charlist = new ArrayList<>();          
            for(MapleCharacter a1 : chars)
                if(a1.isAlive() && !a1.isGM())
                    charlist.add(a1);           
            int amountofigns = 1, randnum = 0;
            String igns = "";
            if(sub.length > 1){                
                if(Integer.parseInt(sub[1]) >= charlist.size()){
                    for(int i = 0; i < charlist.size();i++)
                        igns+= charlist.get(i).getName() + ", ";
                }
                else{
                    amountofigns = Integer.parseInt(sub[1]);
                     for(int i = 0; i < amountofigns; i ++){
                         randnum = rand.nextInt(charlist.size());
                    igns+= charlist.get(randnum).getName() + ", ";
                    charlist.remove(randnum);                   
                }
             }
               
                igns = igns.substring(0,igns.length() -2);
                  player.getMap().broadcastMessage(MaplePacketCreator.sendYellowTip("[Random IGN] The chosen living players were: " + igns));
            }
            else{
                 igns = charlist.get(rand.nextInt(charlist.size())).getName();
                  player.getMap().broadcastMessage(MaplePacketCreator.sendYellowTip("[Random IGN] Living player selected was: " + igns));
            }
        } else if (sub[0].equals("randignd") || sub[0].equals("randdeadign") ) {        
            Random rand = new Random();            
            Collection<MapleCharacter> chars = player.getMap().getCharacters();
            List<MapleCharacter> charlist = new ArrayList<>();          
            for(MapleCharacter a1 : chars)
                if(!a1.isAlive() && !a1.isGM())
                    charlist.add(a1);
            int amountofigns = 1, randnum = 0;
            String igns = "";
            if(sub.length > 1){                
                if(Integer.parseInt(sub[1]) >= charlist.size()){
                    for(int i = 0; i < charlist.size();i++)
                        igns+= charlist.get(i).getName() + ", ";
                }
                else{
                    amountofigns = Integer.parseInt(sub[1]);
                     for(int i = 0; i < amountofigns; i ++){
                         randnum = rand.nextInt(charlist.size());
                    igns+= charlist.get(randnum).getName() + ", ";
                    charlist.remove(randnum);                   
                }
             }
               
                igns = igns.substring(0,igns.length() -2);
                  player.getMap().broadcastMessage(MaplePacketCreator.sendYellowTip("[Random IGN] The chosen dead players were: " + igns));
            }
            else{
                 igns = charlist.get(rand.nextInt(charlist.size())).getName();
                  player.getMap().broadcastMessage(MaplePacketCreator.sendYellowTip("[Random IGN] The dead player selected was: " + igns));
            }  */   
        } else if (sub[0].equals("flyingbobs")) { // Note to self: aggro'd mobs spawn evenly between players in the map.
            if (sub.length > 1) // Thats why you should spawn player amount * 3, so ea player will have 3 bobs after him
            {
                player.getMap().startFlyingbobs(player, Integer.parseInt(sub[1]));
            } else {
                player.dropMessage(5, "Error. Please type the command as follows !flyingbobs <num of bobs>");
            }
        } else if (sub[0].equals("ftj")) {
            if (sub.length > 1) {
                if (!player.getMap().isFTJ()) {
                    if (!player.getMap().iscanstartFTJon()) {
                        player.getMap().canstartFTJ(player);
                    } else {
                        player.getMap().startFindtheJewel(player, Integer.parseInt(sub[1]));
                    }
                } else {
                    player.dropMessage(5, "Error. There's already a round of FTG going on!");
                }
            } else {
                player.dropMessage(5, "Error. Please type the command as following !ftg <timelimit>");
            }

        } else if (sub[0].equals("ebod")) { // look into this 
            player.getMap().elimDoomFin(player);
            player.getMap().startElimDoom(player);

        } else if (sub[0].equals("impbob")) {
            player.getMap().startImpossibleBob(player);
            player.dropMessage(5, "Dont forget to turn !mobkillon");
        } else if (sub[0].equals("randnum") || sub[0].equals("num") || sub[0].equals("rnum") ) {
            MapleMap m = player.getMap();
            boolean outofbounds = true;
            int num1= 3,num2=1;
            if(sub.length > 1)
            num1 = Integer.parseInt(sub[1]);  
            if(sub.length > 2)
             num2 = Integer.parseInt(sub[2]);           
            Random rand = new Random();
            int randnum = 0;         

            if (num1 > num2) {
                randnum = rand.nextInt(num1 + 1 - num2) + num2;
               m.broadcastMessage(MaplePacketCreator.sendYellowTip("Random number selected was: " + randnum));
                        
            }
            else{
               randnum = rand.nextInt(num2 + 1 - num1) + num1;
               m.broadcastMessage(MaplePacketCreator.sendYellowTip("Random number selected was: " + randnum));  
            }

           

           
        } else if (sub[0].equals("box")) {
            MapleMonster monster;
            if (sub.length > 1) {
                monster = MapleLifeFactory.getMonster(9500365);
                int hp = Integer.parseInt(sub[1]);
                monster.setHp(hp);
                player.getMap().spawnMonsterOnGroudBelow(monster, player.getPosition());
                player.dropMessage(6, "Box has " + sub[1] + " hp");
            } else {
                monster = MapleLifeFactory.getMonster(9500365);
                Random rand = new Random();
                int num = rand.nextInt(100) + 1;
                monster.setHp(num);
                player.getMap().spawnMonsterOnGroudBelow(monster, player.getPosition());

                player.dropMessage(6, "Box has " + num + " hp");
            }

        } else if (sub[0].equals("cancelrainingbombs")) {
            player.getMap().cancelBombs();
        } else if (sub[0].equals("cancelebod")) {
            player.getMap().elimDoomFin(player);
            /*  } else if (sub[0].equals("killallf")) { // kills all mobs (including friendly mobs) NOT WORKING
                           List<MapleMapObject> monsters = player.getMap().getMapObjectsInRange(player.getPosition(), Double.POSITIVE_INFINITY, Arrays.asList(MapleMapObjectType.MONSTER));
			MapleMap map = player.getMap(); 
			for (MapleMapObject monstermo : monsters) {
				MapleMonster monster = (MapleMonster) monstermo;				
				map.killMonster(monster, player, true);				
			} */
        } else if (sub[0].equals("mobkill")) {
            if (player.getMap().mobkillOn()) {
                player.getMap().setMobkill(false);
                player.dropMessage(5, "Mobkill is now off.");
            } else {
                player.getMap().setMobkill(true);
                player.dropMessage(5, "Mobkill is now on.");
            }
            //  player.getMap().broadcastMessage(MaplePacketCreator.serverNotice(6,"Mobkill is on"));
            /*  } else if (sub[0].equals("mobkilloff")) { 
                      player.getMap().setMobkill(false);
                       player.getMap().broadcastMessage(MaplePacketCreator.serverNotice(6,"Mobkill is off")); */
        } else if (sub[0].equals("bombkill") || sub[0].equals("bd")) {
            if (sub[1].equals("d")) {
                player.getMap().setBombkill(false);
                player.dropMessage(5, "Bombkill is now off.");
            } else if(sub[1].equals("e")) {
                player.getMap().setBombkill(true);
                player.dropMessage(5, "Bombkill is now on.");
            } else {
                player.dropMessage(5,"Error.  Please type the command as follows !bd e/d (enable/disable)");
            }     
        }  else if (sub[0].equals("ap")) {
            if (sub.length < 3) {
                player.setRemainingAp(Integer.parseInt(sub[1]));
                player.updateSingleStat(MapleStat.AVAILABLEAP, player.getRemainingAp());
            } else {
                MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]);
                victim.setRemainingAp(Integer.parseInt(sub[2]));
                victim.updateSingleStat(MapleStat.AVAILABLEAP, victim.getRemainingAp());
            }
        } else if (sub[0].equals("!")) {
            for (MapleCharacter geam : c.getWorldServer().getPlayerStorage().getAllCharacters()) {
                if (geam.gmLevel() > 0) {
                    geam.message("[GM Chat] " + player.getName() + ": " + StringUtil.joinStringFrom(sub, 1));
                }
            }
            Server.getInstance().gmChat(StringUtil.joinStringFrom(sub, 1), "");
        } else if (sub[0].equals("takeover")) {
            LinkedList<Integer> itemMap = new LinkedList<>();
            MapleCharacter victim = c.getWorldServer().getPlayerStorage().getCharacterByName(sub[1]);
            if (victim.isLoggedin()) {
                int vHair = victim.getHair();
                int vEye = victim.getFace();
                MapleSkinColor vSkin = victim.getSkinColor();
                for (Item item : victim.getInventory(MapleInventoryType.EQUIPPED)) {
                    MapleInventoryManipulator.addById(c, item.getItemId(), (short) 1);
                }
                c.getPlayer().setHair(vHair);
                c.getPlayer().setFace(vEye);
                c.getPlayer().setSkinColor(vSkin);
                c.getPlayer().getMap().removePlayer(c.getPlayer());
                c.getChannelServer().removePlayer(c.getPlayer());
                c.updateLoginState(MapleClient.LOGIN_SERVER_TRANSITION);

                int channel = c.getPlayer().getClient().getChannel();
                c.getPlayer().getClient().changeChannel(channel);

                c.getPlayer().saveToDB();

            }
        } else if (sub[0].equals("mesos")) {
            MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]);
            int amount = Integer.parseInt(sub[2]);
            victim.gainMeso(amount, true);
            victim.dropMessage("You have gained " + amount + " meso.");
            c.getPlayer().dropMessage("You have gave " + victim.getName() + " " + amount + " meso.");

        } /* else if (sub[0].equals("disablemm"))    {
                  //    c.announce(MaplePacketCreator.disableMinimap());
                      c.getPlayer().announce(MaplePacketCreator.disableMinimap());
        
    
                } */ 

        
        else if (sub[0].equals("marry")) {
            MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]);

            victim.getClient().announce(MaplePacketCreator.sendEngagementRequest(c.getPlayer().getName()));

        

        } else if (sub[0].equals("buffme")) {
            final int[] array = {9001000, 9101002, 9101003, 9101008, 2001002, 1101007, 1005, 2301003, 5121009, 1111002, 4111001, 4111002, 4211003, 4211005, 1321000, 2321004, 3121002};
            for (int i : array) {
                SkillFactory.getSkill(i).getEffect(SkillFactory.getSkill(i).getMaxLevel()).applyTo(player);
            }
        } else if (sub[0].equals("flashbang")) {
            MapleCharacter victim = cserv.getPlayerStorage().getCharacterByName(sub[1]);
            if (victim != null) {
                for (int i = 0; i < 29; i++) {
                    victim.changeMap(913040106);
                }
                victim.changeMap(910000000);
                victim.dropMessage(6, "You have been flash banged mother fucker!");
                player.dropMessage(6, "You have flash banged the bitch!");
            } else {
                player.dropMessage("Player not found");
            }

        

        } else if (sub[0].equals("gmmap")) {
            player.changeMap(180000000);
            //tut stages 
        } else if (sub[0].equals("stage1")) {
            player.changeMap(450005010);
        } else if (sub[0].equals("stage2")) {
            player.changeMap(63);
        } else if (sub[0].equals("stage3")) {
            player.changeMap(450005100);
        } else if (sub[0].equals("stage4")) {
            player.changeMap(450000010);

        } else if (sub[0].equals("addword")) {
            if (sub.length > 1) {
                String word = sub[1].toLowerCase();
                if (word.length() > 2) {

                    try {
                        Connection con = DatabaseConnection.getConnection();
                        try (PreparedStatement ps = con.prepareStatement("INSERT INTO wordlist (word, letters) VALUES (?, ?)")) {
                            ps.setString(1, word);
                            int length = word.length();
                            ps.setInt(2, length);
                            ps.execute();
                            ps.close();
                            player.dropMessage(6, "Inserted word: " + word);
                        }
                    } catch (SQLException e) {
                        System.out.print("Error inserting wordlist: " + e);
                    }
                } else {
                    player.dropMessage(5, "Error. Word too short!");
                }
            } else {
                player.dropMessage(5, "Error. Type the command as follows !addword <word>");
            }
        } else if (sub[0].equals("punish")) {
            if (sub.length > 1) {
                String name = String.valueOf(sub[1]);
                if(name.equals("dev")) name = player.getName();
                MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(name);
                if (victim != null) {
                    if (!victim.isPunished()) {
                        victim.setPunished(true);
                        player.dropMessage(5, "You've punished " + victim.getName() + ".");
                        victim.dropMessage(5, player.getName() + " has punished you! You better behave now!");
                    } else {
                        victim.setPunished(false);
                        player.dropMessage(5, "You've unpunished " + victim.getName() + ".");

                    }
                } else {
                    player.dropMessage(5, "Error. Chosen player isn't in the map");
                }
            } else {
                player.dropMessage(5, "Error. Type the command as follows !punish <player>");
            }
        } else if (sub[0].equals("addpsent")) {
            if (sub.length > 1) {
                String sentence = StringUtil.joinStringFrom(sub, 1);
                sentence = " " + sentence;
                try {
                    Connection con = DatabaseConnection.getConnection();
                    try (PreparedStatement ps = con.prepareStatement("INSERT INTO punishsentences (sentence, gm) VALUES (?, ?)")) {
                        ps.setString(1, sentence);
                        ps.setString(2, player.getName());
                        ps.execute();
                        ps.close();
                        player.dropMessage(6, "Inserted sentence: " + sentence);
                    }
                } catch (SQLException e) {
                    System.out.print("Error inserting sentencelist: " + e);
                }

            } else {
                player.dropMessage(5, "Error. Type the command as follows !addpsent <sentence>");
            }
        } else if (sub[0].equals("addsworduser")) {
            if (sub.length > 1) {
                MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]);
                if (victim != null) {
                    try {
                        Connection con = DatabaseConnection.getConnection();
                        try (PreparedStatement ps = con.prepareStatement("INSERT INTO swordusers (player) VALUES (?)")) {
                            ps.setString(1, victim.getName());
                            ps.execute();
                            ps.close();
                            Server.getInstance().broadcastMessage(MaplePacketCreator.serverNotice(6, "Ominous beings have chosen " + victim.getName() + "."));
                        }
                    } catch (SQLException e) {
                        System.out.print("Error inserting swordusers: " + e);
                    }
                } else {
                    player.dropMessage(5, "Error. Player cannot be found.");
                }
            } else {
                player.dropMessage(5, "Error. Type the command as follows !addsword <player>");
            }
        } else if (sub[0].equals("removesworduser")) {
            if (sub.length > 1) {
                MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]);
                if (victim != null) {
                    try {
                        Connection con = DatabaseConnection.getConnection();
                        try (PreparedStatement ps = con.prepareStatement("DELETE FROM swordusers WHERE player = ?")) {
                            ps.setString(1, victim.getName());
                            ps.execute();
                            ps.close();
                            //   Server.getInstance().broadcastMessage(MaplePacketCreator.serverNotice(6, victim.getName() + " has just lost his special ability."));             
                        }
                    } catch (SQLException e) {
                        System.out.print("Error inserting swordusers: " + e);
                    }
                } else {
                    player.dropMessage(5, "Error. Player cannot be found.");
                }
            } else {
                player.dropMessage(5, "Error. Type the command as follows !removesworduser <player>");
            }
        } else if (sub[0].equals("addmsi")) {
            if (sub.length > 1) {
                int msiid = Integer.parseInt(sub[1]);
                if (player.itemExists(msiid)) {

                    try {
                        Connection con = DatabaseConnection.getConnection();
                        try (PreparedStatement ps = con.prepareStatement("INSERT INTO msilist (itemid) VALUES (?)")) {
                            ps.setInt(1, msiid);
                            ps.execute();
                            ps.close();
                            player.dropMessage(6, "Inserted id: " + msiid);
                        }
                    } catch (SQLException e) {
                        System.out.print("Error inserting msilist: " + e);
                    }
                } else {
                    player.dropMessage(5, "Error. Item doesn't exist!");
                }
            } else {
                player.dropMessage(5, "Error. Type the command as follows !addmsi <itemid>");
            }

        
        
        } else if (sub[0].equals("unhide")) {
            player.Hide(false);
       
           } else if (sub[0].equals("shpm")) {
               for(MapleCharacter a1 : player.getMap().getCharacters()){
                   if(!a1.isGM() || a1.isTemp()){
                   a1.setSecondaryHp(Integer.parseInt(sub[1]));
                    a1.setInitialSecondaryHp(Integer.parseInt(sub[1]));
                    a1.setShpBol(true);
                    a1.announce(MaplePacketCreator.earnTitleMessage("Secondary Hp: " + Integer.parseInt(sub[1]) + " / " + Integer.parseInt(sub[1])));
                   }
               }
           } else if (sub[0].equals("shp")) {
              MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]); 
              if(victim != null){
              victim.setSecondaryHp(Integer.parseInt(sub[2]));
                    victim.setInitialSecondaryHp(Integer.parseInt(sub[2]));
                    victim.setShpBol(true);
                    victim.announce(MaplePacketCreator.earnTitleMessage("Secondary Hp: " + Integer.parseInt(sub[2]) + " / " + Integer.parseInt(sub[2])));
              }
              else
                  player.dropMessage(5,"Error. Player doesnt exist.");
              
         

        } else if (sub[0].equals("sendhint")) { // shit nvm im stupid af
            if (sub.length > 2) {
                String hint = "";
                for (int i = 2; i < sub.length; i++) {
                    hint = hint + sub[i];
                }
                MapleCharacter victim = c.getWorldServer().getPlayerStorage().getCharacterByName(sub[1]);
                victim.getMap().broadcastMessage(MaplePacketCreator.sendHint(hint, 200, 40));
                //   victim.getClient().getSession().write(MaplePacketCreator.sendHint(hint,200,40));
            } // hmm lets try it out! how would u use it !hint <player> <message>uh ok, hits i, ill sne


        } else if (sub[0].equals("deleteitem")) {
            int itemid = Integer.parseInt(sub[1]);
            for (Item item : c.getPlayer().getInventory(MapleInventoryType.EQUIP)) {
                if (item.getItemId() == itemid) {
                    MapleInventoryManipulator.removeById(c, MapleInventoryType.EQUIP, itemid, itemid, false, false);
                }
            }
        } else if (sub[0].equals("dropinv")) {
            if (sub.length > 2) {
                MapleCharacter victim = c.getWorldServer().getPlayerStorage().getCharacterByName(sub[1]);
                if (victim != null) {
                    if (sub[2].equals("equip")) {
                        for (int i = 0; i < victim.getInventory(MapleInventoryType.EQUIP).getSlotLimit() + 1; i++) {
                            MapleInventoryManipulator.drop(victim.getClient(), MapleInventoryType.EQUIP, (short) i, (short) 1);
                        }
                    } else if (sub[2].equals("use")) {
                        for (int i = 0; i < victim.getInventory(MapleInventoryType.USE).getSlotLimit() + 1; i++) {
                            if (victim.getInventory(MapleInventoryType.USE).getItem((short) i) != null) {
                                MapleInventoryManipulator.drop(victim.getClient(), MapleInventoryType.USE, (short) i, (short) victim.getInventory(MapleInventoryType.USE).getItem((short) i).getQuantity());
                            }
                        }

                    } else if (sub[2].equals("setup")) {
                        for (int i = 0; i < victim.getInventory(MapleInventoryType.SETUP).getSlotLimit() + 1; i++) {
                            if (victim.getInventory(MapleInventoryType.SETUP).getItem((short) i) != null) {
                                MapleInventoryManipulator.drop(victim.getClient(), MapleInventoryType.SETUP, (short) i, (short) victim.getInventory(MapleInventoryType.SETUP).getItem((short) i).getQuantity());
                            }
                        }
                    } else if (sub[2].equals("etc")) {
                        for (int i = 0; i < victim.getInventory(MapleInventoryType.ETC).getSlotLimit() + 1; i++) {
                            if (victim.getInventory(MapleInventoryType.ETC).getItem((short) i) != null) {
                                MapleInventoryManipulator.drop(victim.getClient(), MapleInventoryType.ETC, (short) i, (short) victim.getInventory(MapleInventoryType.ETC).getItem((short) i).getQuantity());
                            }
                        }
                    } else if (sub[2].equals("cash")) {
                        for (int i = 0; i < victim.getInventory(MapleInventoryType.CASH).getSlotLimit() + 1; i++) {
                            if (victim.getInventory(MapleInventoryType.CASH).getItem((short) i) != null) {
                                MapleInventoryManipulator.drop(victim.getClient(), MapleInventoryType.CASH, (short) i, (short) victim.getInventory(MapleInventoryType.CASH).getItem((short) i).getQuantity());
                            }
                        }
                    } else {
                        player.dropMessage(5, "There are 5 types: equip,use,setup,etc and cash.");
                    }

                } else {
                    player.dropMessage(5, "Error. No such player exists in this map!");
                }
            } else {
                player.dropMessage(5, "Error. Type the command as follows !dropinv <player> <type>.");
            }

        } else if (sub[0].equals("spawn")) {
            MapleMonster monster = MapleLifeFactory.getMonster(Integer.parseInt(sub[1]));
            if (monster == null) {
                return true;
            }
            if (sub.length > 2) {
                for (int i = 0; i < Integer.parseInt(sub[2]); i++) {
                    player.getMap().spawnMonsterOnGroudBelow(MapleLifeFactory.getMonster(Integer.parseInt(sub[1])), player.getPosition());
                }
            } else {
                player.getMap().spawnMonsterOnGroudBelow(MapleLifeFactory.getMonster(Integer.parseInt(sub[1])), player.getPosition());
            }

        
        } else if (sub[0].equals("say")) {
            String text = StringUtil.joinStringFrom(sub, 1);
            Server.getInstance().broadcastMessage(MaplePacketCreator.serverNotice(6, "[Staff] " + player.getName() + ": " + text));
        } else if (sub[0].equals("srccheck")) {
           String msg = sub[1];
           if(msg.contains(","))
           player.dropMessage(5,msg.substring(0,msg.indexOf(",")));
           else
               player.dropMessage(5,msg);
          
        } else if (sub[0].equals("speedtype")) {
            String text = StringUtil.joinStringFrom(sub, 1);
            if (sub.length > 1) {
                player.setChalkboard(text);
                ServerConstants.speedtypeAnswer = text;
                player.getMap().setSpeedtype(true);
            } else {
                player.dropMessage(5, "Error. Please insert a sentence");
            }
        } else if (sub[0].equals("scat")) {  // Scattergories
            if (sub.length > 2) {
                String category = sub[1];
                String answer = StringUtil.joinStringFrom(sub, 2).toLowerCase();
                // String answer = sub[2].toLowerCase();
                player.setChalkboard((category + " - " + answer.charAt(0)));
                player.getMap().broadcastMessage(MaplePacketCreator.useChalkboard(player, false));
                ServerConstants.scatAnswer = answer;
                player.getMap().setScat(true, player);

            } else {
                player.dropMessage(5, "Error. type the command as follows : !scat <category> <answer>");
            }
        } else if (sub[0].equals("getobjs")) {
            for (MapleMapObject o1 : player.getMap().getMapObjects()) {
                player.dropMessage(o1.getObjectId() + "");
            }
        } else if (sub[0].equals("closestto")) {
            if (sub.length > 1) {
                player.getMap().closestToX(c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]));
            } else {
                player.getMap().closestToX(player);
            }
        } else if (sub[0].equals("setchalkpoints")) { // to manually set up points for players
            if (sub.length > 2) {
                MapleCharacter chr = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]);
                chr.setChalkboard(sub[2] + " / " + chr.getMap().getPointstowin());
                chr.getMap().broadcastMessage(MaplePacketCreator.useChalkboard(chr, false));

            } else {
                player.dropMessage(5, "Error. Please enter the command as follows !setchalkpoints <name> <pts>");
            }
        } else if (sub[0].equals("dchalk")) {
            for(MapleCharacter a1 : player.getMap().getCharacters()){
                if(!a1.isGM() || a1.isTemp()){
           a1.getMap().setChalk(false);
            a1.setChalkboard(null);
            a1.getMap().broadcastMessage(MaplePacketCreator.useChalkboard(a1, true));
                }
               
            }
             player.dropMessage(5,"You've disabled @chalk on this map.");
         } else if (sub[0].equals("echalk")) {
            player.getMap().setChalk(true);  
              player.dropMessage(5,"You've enabled @chalk on this map.");
        } else if (sub[0].equals("chalkpoints")) {
            if (sub.length > 1) {
                player.getMap().setChalk(false);
                player.getMap().setClosable(false);
                player.dropMessage(5, "[Tip]To cancel !chalkpoints use !cancelcp");
                for (MapleCharacter a1 : player.getMap().getCharacters()) {
                    if(!a1.isGM() || a1.isTemp()){
                    a1.setChalkboard(null);
                    player.getMap().broadcastMessage(MaplePacketCreator.useChalkboard(a1, true));
                    }
                }

                player.getMap().broadcastMessage(MaplePacketCreator.sendYellowTip("[Attention] The first participant to get to " + sub[1] + " points wins this round. Good luck!"));
                player.getMap().setChalkpoints(true);
                player.getMap().insertChalkpoints(Integer.parseInt(sub[1]));
            } else {
                player.dropMessage(5, "Error. type the command as follows : !chalkpoints <points to win>");
            }
        } else if (sub[0].equals("cancelcp")) {    // cancel chalkpoints
            player.getMap().setChalkpoints(false);
            player.getMap().setChalk(true);
            player.getMap().setClosable(true);
            player.dropMessage(5, "You've cancelled points");
            for (MapleCharacter a1 : player.getMap().getCharacters()) {
                if(!a1.isGM() || a1.isTemp())
                player.getMap().broadcastMessage(MaplePacketCreator.useChalkboard(a1, true));
            }
        } else if (sub[0].equals("nti")) {
            if (sub.length > 2) {
                Item toDrop;
                if (MapleItemInformationProvider.getInstance().getInventoryType(Integer.parseInt(sub[1])) == MapleInventoryType.EQUIP) {
                    toDrop = MapleItemInformationProvider.getInstance().getEquipById(Integer.parseInt(sub[1]));
                } else {
                    toDrop = new Item(Integer.parseInt(sub[1]), (short) 0, (short) 0, 1);
                }
                c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), toDrop, c.getPlayer().getPosition(), false, true);
                player.getMap().broadcastMessage(MaplePacketCreator.sendYellowTip("[NTI] Name the item you see before you, don't forget too capitalize correctly!"));
                ServerConstants.ntiAnswer = StringUtil.joinStringFrom(sub, 2);;
                player.getMap().setNti(true);
            } else {
                player.dropMessage(5, "Error. type the command as follows : !nti <id> <answer>");
            }
        } else if (sub[0].equals("unscramble")) {

            boolean finishedscramble = false;
            Random rand = new Random();
            int randnum;
            if (sub.length > 1) {
                if (sub[1].length() > 2) {
                    String input = sub[1];
                    char[] charinput = input.toCharArray();
                    String output = "";
                    while (!finishedscramble) {
                        if (input.length() == output.length()) {
                            finishedscramble = true;
                        } else {
                            randnum = rand.nextInt(input.length());
                            if (charinput[randnum] != 0) {
                                output += charinput[randnum];
                                charinput[randnum] = 0;
                            }
                        }

                    }
                    player.setChalkboard(output);
                    ServerConstants.unscrambleAnswer = input;
                    player.getMap().setUnscramble(true);

                } else {
                    player.dropMessage(5, "Error. Please insert a word with 3 or more letters");
                }
            }
        } else if (sub[0].equals("xmas")) {
            // hmm, itll take a bit more, give me 10
            if (sub.length > 1) {
                String text = (StringUtil.joinStringFrom(sub, 1)).toLowerCase(), revtext = "";

                int space = text.length() * 30, charrand = 0, dist = 0;
                String code = "";
                Point pos;
                Item droppedletter;

                for (int i = text.length() - 1; i > -1; i--) {
                    revtext += text.charAt(i);
                }

                for (int i = 0; i < revtext.length(); i++) {
                    if ((int) revtext.charAt(i) != 32) {
                        charrand = (int) revtext.charAt(i) - 97;
                        if (charrand < 10) {
                            code = "399100" + charrand;
                        } else {
                            code = "39910" + charrand;
                        }

                        droppedletter = new Item(Integer.parseInt(code), (short) 0, (short) 0, 1);
                        pos = new Point(player.getPosition().x + space / 2 - dist, player.getPosition().y);
                        player.getMap().spawnItemDrop(player, player, droppedletter, pos, false, true);
                        dist += 30; // Space between letters, while "space" is the space cut out from the map given to spawn letters      
                    } else {
                        dist += 30;
                    }

                }

            } else {
                player.dropMessage(5, "Error. Type out a message");
            }

        } else if (sub[0].equals("cblink")) {
            Random rand = new Random();
            int amount;
            player.dropMessage(5, "Insert 0 for random amount of letters");
            if (sub.length > 2) {

                if (Integer.parseInt(sub[1]) == 0) {
                    amount = rand.nextInt(20) + 1;
                    player.getMap().startCBlink(player, amount, Integer.parseInt(sub[2])); //nothin okie
                } else {
                    amount = Integer.parseInt(sub[1]);
                    player.getMap().startCBlink(player, amount, Integer.parseInt(sub[2])); //nothin okie
                }

            } else {
                player.dropMessage(5, "Error. Please enter the command as following !cblink <num of letters> <timeforshow>");
            }
            /* if(sub.length > 1)
                      {
                        amount = Integer.parseInt(sub[1]);  
                      }
                      else
                      {
                        amount= rand.nextInt(20) + 1; // Between 1 and 20 letters when no specifics entered
                      }
                        int space = amount*30, charrand = 0,dist = 0;                        
                        String code = "", answer = "", revanswer = "";
                        Point pos;
                        Item droppedletter;
                        
                        for(int i=0;i<amount;i++){
                          charrand = rand.nextInt(25); // Letters in the alphabet 0-25
                          answer += (char)(charrand+97);                          
                         
                          if(charrand < 10)
                              code = "399100"+charrand;
                          else
                              code = "39910"+charrand;
                          droppedletter = new Item(Integer.parseInt(code),(short)0, (short) 0, 1);
                          pos = new Point(player.getPosition().x + space/2 - dist,player.getPosition().y);
                          c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), droppedletter, pos, false, true);
                          dist += 30; // Space between letters, while "space" is the space cut out from the map given to spawn letters                          
                        
                      } // are u there? ya
                        
                        for(int i =amount-1;i > -1; i--)
                            revanswer += answer.charAt(i);  
                        player.dropMessage(5,revanswer);
                     player.getMap().broadcastMessage(MaplePacketCreator.sendYellowTip("WARNING: Don't blink"));
                     ServerConstants.cblinkAnswer = revanswer; 
                     player.getMap().setCblink(true); // Okay, lets try it out! */
        } else if (sub[0].equals("blink")) {
            String blink = "";
            String numericalvalue = ""; // for check
            Random rand = new Random();
            int numchar, randchars;
            String asciitochar;
            if (sub.length > 1) {
                for (int i = 0; i < Integer.parseInt(sub[1]); i++) {
                    numchar = rand.nextInt(126) + 33;
                    if (numchar < 127) {
                        asciitochar = Character.toString((char) numchar);
                        blink += asciitochar;
                        numericalvalue += ", " + numchar;
                    } else {
                        numchar = rand.nextInt(60) + 33;
                        asciitochar = Character.toString((char) numchar);
                        blink += asciitochar;
                        numericalvalue += ", " + numchar;
                    }
                }

            } else {
                randchars = rand.nextInt(15) + 1;
                for (int i = 0; i < randchars; i++) {
                    numchar = rand.nextInt(126) + 33;
                    if (numchar < 127) {
                        asciitochar = Character.toString((char) numchar);
                        blink += asciitochar;
                        numericalvalue += ", " + numchar;
                    } else {
                        numchar = rand.nextInt(60) + 33;
                        asciitochar = Character.toString((char) numchar);
                        blink += asciitochar;
                        numericalvalue += ", " + numchar;
                    }

                }

            }
            if (blink.charAt(0) == '@') {
                blink = '#' + blink;
            }
            player.dropMessage(5, "Blink numerical value : " + numericalvalue);
            player.getMap().broadcastMessage(MaplePacketCreator.sendYellowTip("[Blink] " + blink));
            ServerConstants.blinkAnswer = blink;
            player.getMap().setBlink(true);
            /*   String word = StringUtil.joinStringFrom(sub, 1);
            player.setChalkboard(word);
            player.getMap().broadcastMessage(MaplePacketCreator.useChalkboard(player, false));
            ServerConstants.blinkAnswer = word;
            player.getMap().setBlink(true); */

        } else if (sub[0].equals("hitman")) {
            if (sub.length > 1) {
                if (Integer.parseInt(sub[1]) < 1000 && Integer.parseInt(sub[1]) > 0) {
                    ResultSet rs;
                    // String answer="";
                    String forshow = "";
                    Random rand = new Random();
                    List<MapleCharacter> players = new ArrayList<>();
                    for (MapleCharacter a1 : player.getMap().getCharacters()) {
                        players.add(a1);
                    }
                    try {
                        PreparedStatement ps;
                        Connection con = DatabaseConnection.getConnection();
                        ps = con.prepareStatement("SELECT name FROM characters ORDER BY RAND() LIMIT " + Integer.parseInt(sub[1]));
                        rs = ps.executeQuery();
                        while (rs.next()) {
                            //  answer+= rs.getString("name") + " ";                
                            if (!StringUtil.isSpam(rs.getString("name"))) // Checking for spam igns
                            {
                                forshow += rs.getString("name") + ", ";
                            } else {
                                forshow += players.get(rand.nextInt(players.size())).getName() + ", ";
                            }
                            //  forshow+= rs.getString("name") + ", "; 

                        }
                    } catch (SQLException sqlexc) {
                        System.out.print("Error selecting wordlist: " + sqlexc);
                    }
                    forshow = forshow.substring(0, forshow.length() - 2);
                    player.getMap().broadcastMessage(MaplePacketCreator.sendYellowTip("Players chosen were: " + forshow));
                    ServerConstants.hitmanAnswer = forshow.replaceAll(",", "");
                    player.getMap().setHitman(true);

                } else {
                    player.dropMessage(5, "Error. Please insert a number between 0 and 1000");
                }
            } else {
                player.dropMessage(5, "Error. Please type the command as follows !hitman <number of igns>");
            }
        } else if (sub[0].equals("dodgebob")) {
            player.getMap().startDodgeBob(player);
        } else if (sub[0].equals("dodgebomb")) {
            if (sub.length > 1) {
                player.getMap().startDodgeBomb(player, Integer.parseInt(sub[1]));
            } else {
                player.getMap().startDodgeBomb(player, 1);
            }

        } else if (sub[0].equals("rainingbombs")) {

            player.getMap().startTestBombs(player);

        } else if (sub[0].equals("autokill") || sub[0].equals("ak") ) {
            if (sub.length != 2) {
                player.dropMessage(5, "[Event] Use !ak e/d to enable/disable autokill.");
                player.dropMessage(5, "[Event] Use !ak b/a to set the autokill for the platforms below/above.");
                player.dropMessage(5, "[Event] Use !ak r to reset the coordinates for both below/above platforms.");
                player.dropMessage(5, "[Event] !ak r must be used if you want it kill for below OR above, not both.");
            }
            if (sub[1].equals("e") || sub[1].equals("enabled")) {
                player.getMap().setAutoKill(true);
                player.getMap().broadcastMessage(MaplePacketCreator.serverNotice(6, "[Event] Autokill has been enabled."));
            } else if (sub[1].equals("d") || sub[1].equals("disable")) {
                player.getMap().setAutoKill(false);
                player.getMap().broadcastMessage(MaplePacketCreator.serverNotice(6, "[Event] Autokill has been disabled."));
            } else if (sub[1].equals("b") || sub[1].equals("below")) {
                player.getMap().setAKPositionB(player.getPosition().y);
                player.dropMessage(6, "[Event] Autokill has been set to your platform and below.");
            } else if (sub[1].equals("a") || sub[1].equals("above")) {
                player.getMap().setAKPositionA(player.getPosition().y);
                player.dropMessage(6, "[Event] Autokill has been set to your platform and above.");
            } else if (sub[1].equals("r") || sub[1].equals("reset")) {
                player.getMap().setAKPositionB(-1);
                player.getMap().setAKPositionA(-1);
                player.dropMessage(6, "[Event] Autokill coordinates reset. Use !ak b or !ak a to set them again.");
            } else {
                player.dropMessage("Invalid command. !ak e/d/b/a/r");
            }
            
            } else if (sub[0].equals("triviaq") || sub[0].equals("tq") ) {
                String q = "";
                if (sub.length > 1) {
                    q = StringUtil.joinStringFrom(sub, 1);
                    player.setTriviaQ(q);
                    player.dropMessage(6, "You've set the trivia question to be: '" + q + "'.");
                } else {
                    player.dropMessage(5, "Error. Please type the command as follows @triviaq/@tq <question>");
                }
                } else if (sub[0].equals("triviaa") || sub[0].equals("ta") ) {
                String a = "";
                if (sub.length > 1) {
                    a = StringUtil.joinStringFrom(sub, 1).toLowerCase();
                    player.setTriviaA(a);
                    player.dropMessage(6, "You've set the trivia answer to be: '" + a + "'.");
                } else {
                    player.dropMessage(5, "Error. Please type the command as follows @triviaa/@ta <answer>");
                }
                } else if (sub[0].equals("trivia")) {
                //if(player.isPatron() || player.isGM()) // Only Patrons & GMs can trivia isPatron()
                //  {
                 if (!player.getTriviaQ().equals("") && !player.getTriviaA().equals("")) {
                        Server.getInstance().broadcastMessage(MaplePacketCreator.serverNotice(6, "[Trivia] " + player.getName() + " has started a round of trivia! Use @t <answer> to answer the question!"));
                        Server.getInstance().broadcastMessage(MaplePacketCreator.serverNotice(6, "Q: " + player.getTriviaQ()));
                        ServerConstants.playertriviaAnswer = player.getTriviaA();
                        ServerConstants.playertriviaGuy = player.getName();
                        c.getWorldServer().setPlayerTrivia(true);
                    } else {
                        player.dropMessage(5, "Please use @triviaq/tq to set your question and @triviaa/ta to set the answer before proceeding!");
                    }
                
            
        } else if (sub[0].equals("!")) //?
        {
            String text = StringUtil.joinStringFrom(sub, 1);
            Server.getInstance().broadcastGMMessage(MaplePacketCreator.serverNotice(6, "[Staff Chat] " + player.getName() + ": " + text));

        } else if (sub[0].equals("randombomb")) { //i think its cus i didnt restart my laptop in like 3 days ya it was being laggy but now its fineclick on the line number to the side << wha now? udidnt click on the line number each
            if (sub.length > 1) {
                MapleMap map = player.getMap();
                Random rand = new Random();
                int x, y;
                int[] oxvalues = {-536, -446, -626, -716, -807};
                int[] oyvalues = {214, 154, 94, 34, -26};
                int[] xxvalues = {4, 94, 184, 274, 364};

                for (int i = 0; i < Integer.parseInt(sub[1]); i++) // F(N)
                {
                    if (player.getPosition().x > -145) {
                        x = xxvalues[rand.nextInt(xxvalues.length)];
                        y = oyvalues[rand.nextInt(oyvalues.length)];
                        while (((x == 94 || x == 184 || x == 274) && y == 214) || (x == 184 && y == 154)) {
                            x = xxvalues[rand.nextInt(xxvalues.length)];
                            y = oyvalues[rand.nextInt(oyvalues.length)];
                        }
                    } else {
                        x = oxvalues[rand.nextInt(oxvalues.length)];
                        y = oyvalues[rand.nextInt(oyvalues.length)];
                        while ((x == -807 || x == -446) && y == 214) {
                            x = oxvalues[rand.nextInt(oxvalues.length)];
                            y = oyvalues[rand.nextInt(oyvalues.length)];
                        }
                    }
                    map.spawnBombOnGroudBelow(9300166, x, y);

                }
            } else {
                player.dropMessage(5, "Error. Type the command as follows !boombayah <number of bombs>");

            }

        } else if (sub[0].equals("clock")) {
            int time = Integer.parseInt(sub[1]);
            player.getMap().broadcastMessage(MaplePacketCreator.getClock(time));
        } else if (sub[0].equals("morphvalues")) {
            String[] messagesToDrop = {
                "00 - Orange Mushroom Piece",
                "01 - Ribbon Pig Piece",
                "02 - Grey Piece",
                "03 - Dragon Elixir",
                "05 - Tigun Transformation Bundle",
                "06 - Rainbow-colored Snail Shell",
                "07 - Change to Ghost",
                "08 - Ghost Candy",
                "09 - Sophillia's Abandoned Doll",
                "10 - Potion of Transformation",
                "11 - Potion of Transformation ",
                "12 - Change to Mouse",
                "16 - Mini Draco Transformation",
                "17 - moon",
                "18 - moon bunny",
                "21 - gaga (a guy lol)",
                "22 - old guy",
                "30 - REALLY old guy",
                "32 - Cody's Picture",
                "33 - Cake Picture",
                "34 - alien gray",
                "35 - pissed off penguin",
                "36 - smart ass penguin",
                "37 - big ass blade penguin",
                "38 - big ass blade penguin on pot",
                "39 - gay penguin",
                "43 - freaky ass worm"};
            for (int i = 0; i < messagesToDrop.length; i++) {
                player.dropMessage(messagesToDrop[i]);
            }
        } else if (sub[0].equals("characternpc")) {
            int scriptId = Integer.parseInt(sub[2]);
            MapleCharacter victim = cserv.getPlayerStorage().getCharacterByName(sub[1]);
            int npcId;
            if (sub.length != 3) {
                player.dropMessage("Pleaase use the correct syntax. !characternpc <character> <npc id>");
                /*    } else if (scriptId < 9901000 || scriptId > 9901319) {
                player.dropMessage("Please enter a script id that is between 9901000 and 9901319."); */
            } else if (victim == null) {
                player.dropMessage("The character is not in this channel.");
            } else {
                try {
                    Connection con = (Connection) DatabaseConnection.getConnection();
                    PreparedStatement ps = (PreparedStatement) con.prepareStatement("SELECT * FROM playernpcs WHERE ScriptId = ?");
                    ps.setInt(1, scriptId);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        player.dropMessage("The script id is already in use");
                        rs.close();
                    } else {
                        rs.close();
                        ps = (PreparedStatement) con.prepareStatement("INSERT INTO playernpcs (name, hair, face, skin, x, cy, map, ScriptId, Foothold, rx0, rx1, gender, dir) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                        ps.setString(1, victim.getName());
                        ps.setInt(2, victim.getHair());
                        ps.setInt(3, victim.getFace());
                        ps.setInt(4, victim.getSkinColor().getId());
                        ps.setInt(5, player.getPosition().x);
                        ps.setInt(6, player.getPosition().y);
                        ps.setInt(7, player.getMapId());
                        ps.setInt(8, scriptId);
                        ps.setInt(9, player.getMap().getFootholds().findBelow(player.getPosition()).getId());
                        ps.setInt(10, player.getPosition().x + 50); // I should really remove rx1 rx0. Useless piece of douche
                        ps.setInt(11, player.getPosition().x - 50);
                        ps.setInt(12, victim.getGender());
                        ps.setInt(13, player.isFacingLeft() ? 0 : 1);
                        ps.executeUpdate();
                        rs = ps.getGeneratedKeys();
                        rs.next();
                        npcId = rs.getInt(1);
                        ps.close();
                        ps = (PreparedStatement) con.prepareStatement("INSERT INTO playernpcs_equip (NpcId, equipid, equippos) VALUES (?, ?, ?)");
                        ps.setInt(1, npcId);
                        for (Item equip : victim.getInventory(MapleInventoryType.EQUIPPED)) {
                            ps.setInt(2, equip.getItemId());
                            ps.setInt(3, equip.getPosition());
                            ps.executeUpdate();
                        }
                        ps.close();
                        rs.close();

                        ps = (PreparedStatement) con.prepareStatement("SELECT * FROM playernpcs WHERE ScriptId = ?");
                        ps.setInt(1, scriptId);
                        rs = ps.executeQuery();
                        rs.next();
                        PlayerNPCs pn = new PlayerNPCs(rs);
                        for (Channel channel : Server.getInstance().getAllChannels()) {
                            MapleMap map = channel.getMapFactory().getMap(player.getMapId());
                            map.broadcastMessage(MaplePacketCreator.spawnPlayerNPC(pn));
                            map.broadcastMessage(MaplePacketCreator.getPlayerNPC(pn));
                            map.addMapObject(pn);
                        }
                    }
                    ps.close();
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } else if (sub[0].equals("checktimes")) {
            Calendar calen = Calendar.getInstance();
            double milis = calen.get(Calendar.MILLISECOND);
            int seconds = calen.get(Calendar.SECOND);
            player.dropMessage(6, milis + "");
            player.dropMessage(6, seconds + "");
            double time = seconds + milis / 1000;
            player.getMap().broadcastMessage(MaplePacketCreator.serverNotice(6, "Current time: " + time));
        } else if (sub[0].equals("morph")) {
            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            ii.getItemEffect(Integer.parseInt("22100" + sub[1])).applyTo(player);
        } else if (sub[0].equals("getpos")) {

            player.dropMessage(6, player.getPosition() + ""); // kk relaunch, oh
        } else if (sub[0].equals("temp") || sub[0].equals("tempplayer") || sub[0].equals("play")) {
            if(sub[1].equals("e")){
                player.dropMessage("You've activated temp player! You'll now be treated as a player by the system.");
                player.setTemp(true);
            }
            else if(sub[1].equals("d")){
                player.dropMessage("You've turned temp player off! You'll now be treated as a GM by the system.");
                player.setTemp(false);
            }
        

        } else if (sub[0].equals("settagger") || sub[0].equals("et")) { // cool relaunch whenever u want

            if (sub.length > 1) {
                MapleCharacter victim = cserv.getPlayerStorage().getCharacterByName(sub[1]);
                if (victim != null) {
                    try {
                        Connection con = DatabaseConnection.getConnection();
                        //  con.setAutoCommit(false);
                        try (PreparedStatement ps = con.prepareStatement("UPDATE characters SET tagger = 1 WHERE name = ?")) {
                            // ps.setInt(1, 1);
                            ps.setString(1, victim.getName());
                            player.dropMessage(6, "You've given " + victim.getName() + " the power to tag!");
                            ps.executeUpdate();
                            ps.close();
                            victim.setTaggerLevel(1);
                        }
                    } catch (SQLException exc) {
                        System.out.print("Problem with setting tagger:" + exc);
                    }
                } else {
                    player.dropMessage(5, "Error. Player doesn't exist.");
                }
            } else {
                player.dropMessage(5, "Error. Type the command as follows !settagger <player>");
            }

        } else if (sub[0].equals("settaggeroff") || sub[0].equals("dt")) {
            try {
                Connection con = DatabaseConnection.getConnection();
                //  con.setAutoCommit(false);
                if (sub.length > 1) {
                    MapleCharacter victim = cserv.getPlayerStorage().getCharacterByName(sub[1]);
                    if (victim != null) {
                        try (PreparedStatement ps = con.prepareStatement("UPDATE characters SET tagger = 0 WHERE name = ?")) {
                            ps.setString(1, victim.getName());
                            ps.executeUpdate();
                            ps.close();
                            victim.setTaggerLevel(0);
                            player.dropMessage(5, "You've turned " + victim.getName() + " tagger powers off.");

                        }
                    } else {
                        player.dropMessage(5, "Error. Player doesn't exist.");
                    }
                } else {
                    try (PreparedStatement ps = con.prepareStatement("UPDATE characters SET tagger = 0")) {
                        ps.executeUpdate();
                        ps.close();
                        player.dropMessage(5, "You've turned all of the chosen taggers off.");

                    }
                }
            } catch (SQLException exc) {
                System.out.print("Problem with setting tagger:" + exc);
            }
        } else if (sub[0].equals("tag")) {
			MapleMap map = player.getMap();
			List<MapleMapObject> players = map.getMapObjectsInRange(
					player.getPosition(), (double) 10000,
					Arrays.asList(MapleMapObjectType.PLAYER));
			for (MapleMapObject closeplayers : players) {
				MapleCharacter playernear = (MapleCharacter) closeplayers;
				if (playernear.isAlive() && playernear != player && playernear.getGMLevel()) {
					playernear.setHp(0);
					playernear.updateSingleStat(MapleStat.HP, 0);
					playernear.dropMessage(6, "You were tagged!");
				}
			}
        } else if (sub[0].equals("bombermap")) {
            if (player.getMap().bombermapOn()) {
                player.getMap().setBombermap(false);
                player.dropMessage(6, "You've deactivated bombermap.");

            } else {
                player.getMap().setBombermap(true);
                player.dropMessage(5, "You've declared the map as a bombermap.");
                player.getMap().broadcastMessage(MaplePacketCreator.serverNotice(6, "Bombermap has been activated! use @bomb inorder to spawn bombs."));
            }

            /* } else if (sub[0].equals("bombermapoff")) {
               player.getMap().setBombermap(false);    
                 player.dropMessage(6,"You've deactivated bombermap"); */
         } else if (sub[0].equals("ebm")) {
              player.getMap().setBombermap(true);
                player.dropMessage(5, "You've declared the map as a bombermap.");
                player.getMap().broadcastMessage(MaplePacketCreator.serverNotice(6, "Bombermap has been activated! use @bomb inorder to spawn bombs."));
        
         } else if (sub[0].equalsIgnoreCase("sethair")) {
			MapleCharacter victim = c.getChannelServer().getPlayerStorage()
					.getCharacterByName(sub[1]);
			victim.setHair(Integer.parseInt(sub[2]));
			victim.dropMessage("Your hair has been set to  " + sub[2] + "");
			victim.updateSingleStat(MapleStat.HAIR,
					Integer.parseInt(sub[2]));
			victim.changeMap(c.getPlayer().getMap(), c.getPlayer().getMap()
					.findClosestSpawnpoint(c.getPlayer().getPosition()));
        } else if (sub[0].equalsIgnoreCase("setskin")) {
			MapleCharacter victim = c.getChannelServer().getPlayerStorage()
					.getCharacterByName(sub[1]);
                        MapleSkinColor Skin = MapleSkinColor.getById(Integer.parseInt(sub[2]));
			victim.setSkinColor(Skin);
			victim.dropMessage("Your skin has been set to  " + sub[2]
					+ "");
			victim.updateSingleStat(MapleStat.SKIN,
					Integer.parseInt(sub[2]));
			victim.changeMap(c.getPlayer().getMap(), c.getPlayer().getMap()
					.findClosestSpawnpoint(c.getPlayer().getPosition()));
        } else if (sub[0].equalsIgnoreCase("setface")) {
			MapleCharacter victim = c.getChannelServer().getPlayerStorage()
					.getCharacterByName(sub[1]);
			victim.setFace(Integer.parseInt(sub[2]));
			victim.dropMessage("Your eyes have been set to  " + sub[2]
					+ "");
			victim.updateSingleStat(MapleStat.FACE,
					Integer.parseInt(sub[2]));
			victim.changeMap(c.getPlayer().getMap(), c.getPlayer().getMap()
					.findClosestSpawnpoint(c.getPlayer().getPosition()));
            } else if (sub[0].equals("dbm")) {   
               player.getMap().setBombermap(false);
                player.dropMessage(6, "You've deactivated bombermap.");
            } else if (sub[0].equalsIgnoreCase("jobmapbeg")) {
			for (MapleCharacter victim : player.getMap().getCharacters()) {
				if (victim != null) {
					victim.changeJob(MapleJob.getById(0));
				} else {
					player.dropMessage("Player is not on.");
				}
			}
            } else if (sub[0].equals("unbuffmap")) {
			for (MapleCharacter map : player.getMap().getCharacters()) {
				if (map != null && map != player) {
					map.cancelAllBuffs(true);
				}
			}
            } else if (sub[0].equalsIgnoreCase("dispelmap")) {
			for (MapleCharacter victim : player.getMap().getCharacters()) {
				if (victim != null) {
					victim.dispelDebuffs(true);
				} else {
					player.dropMessage("Player was not found in this channel.");
				}
			}
              } else if (sub[0].equals("jobperson")) {
			cserv.getPlayerStorage().getCharacterByName(sub[1])
					.changeJob(MapleJob.getById(Integer.parseInt(sub[2])));    
            } else if (sub[0].equalsIgnoreCase("playerequip")) {
			MapleCharacter victim = c.getChannelServer().getPlayerStorage()
					.getCharacterByName(sub[1]);
			victim.playerEquip(Integer.parseInt(sub[2]),
					Integer.parseInt(sub[3]));
			victim.dropMessage("" + player.getName() + " has taken "
					+ sub[3] + " of " + sub[2] + " from you.");
		} else if (sub[0].equalsIgnoreCase("playeruse")) {
			MapleCharacter victim = c.getChannelServer().getPlayerStorage()
					.getCharacterByName(sub[1]);
			victim.playerUse(Integer.parseInt(sub[2]),
					Integer.parseInt(sub[3]));
			victim.dropMessage("" + player.getName() + " has taken "
					+ sub[3] + " of " + sub[2] + " from you.");
		} else if (sub[0].equalsIgnoreCase("playercash")) {
			MapleCharacter victim = c.getChannelServer().getPlayerStorage()
					.getCharacterByName(sub[1]);
			victim.playerCash(Integer.parseInt(sub[2]),
					Integer.parseInt(sub[3]));
			victim.dropMessage("" + player.getName() + " has taken "
					+ sub[3] + " of " + sub[2] + " from you.");
		} else if (sub[0].equalsIgnoreCase("playeretc")) {
			MapleCharacter victim = c.getChannelServer().getPlayerStorage()
					.getCharacterByName(sub[1]);
			victim.playerEtc(Integer.parseInt(sub[2]),
					Integer.parseInt(sub[3]));
			victim.dropMessage("" + player.getName() + " has taken "
					+ sub[3] + " of " + sub[2] + " from you.");
		} else if (sub[0].equalsIgnoreCase("playersetup")) {
			MapleCharacter victim = c.getChannelServer().getPlayerStorage()
					.getCharacterByName(sub[1]);
			victim.playerSetup(Integer.parseInt(sub[2]),
					Integer.parseInt(sub[3]));
			victim.dropMessage("" + player.getName() + " has taken "
					+ sub[3] + " of " + sub[2] + " from you.");
             } else if (sub[0].equals("kill5050")) {
			if (player.getMap().getId() == 109020001) {
				int min = 0, max = 1;
				// choose 0 or 1; if 0, kill players on left
				if (ThreadLocalRandom.current().nextInt(min, max + 1) == 0) {
					for (MapleMapObject losers : player.getMap().getCharactersAsMapObjects()) {
						MapleCharacter person = (MapleCharacter) losers;
						if (person.getPosition().y > -206
								&& person.getPosition().y <= 334
								&& person.getPosition().x >= -952
								&& person.getPosition().x <= -308
								&& !person.isGM()) {
							person.setHp(0);
							person.updateSingleStat(MapleStat.HP, 0);
						}
					}
				} else {
					// if 1, kill players on right
					for (MapleMapObject losers : player.getMap().getCharactersAsMapObjects()) {
						MapleCharacter person = (MapleCharacter) losers;
						if (person.getPosition().y > -206
								&& person.getPosition().y <= 334
								&& person.getPosition().x >= -142
								&& person.getPosition().x <= 502
								&& !person.isGM()) {
							person.setHp(0);
							person.updateSingleStat(MapleStat.HP, 0);
						}
					}
				}
				// always kill players in middle as default disqualification
				for (MapleMapObject losers : player.getMap()
						.getCharactersAsMapObjects()) {
					MapleCharacter person = (MapleCharacter) losers;
					if (person.getPosition().y > -206
							&& person.getPosition().y <= 274
							&& person.getPosition().x >= -308
							&& person.getPosition().x <= -142 && !person.isGM()) {
						person.setHp(0);
						person.updateSingleStat(MapleStat.HP, 0);
					}
				}
			} else {
				player.dropMessage("This command can only be used in the OX map.");
			}

            } else if (sub[0].equals("gmmap")) {
			player.changeMap(180000000);
            } else if (sub[0].equals("oxmap")) {
			player.changeMap(109020001);
            }else if (sub[0].equals("snowmap")) {
			player.changeMap(109060000);
            } else if (sub[0].equals("mgmap")) {
			player.changeMap(109070000);
            } else if (sub[0].equals("fame")) {
			MapleCharacter victim = cserv.getPlayerStorage()
					.getCharacterByName(sub[1]);
			victim.setFame(Integer.parseInt(sub[2]));
			victim.updateSingleStat(MapleStat.FAME, victim.getFame());
		} else if (sub[0].equals("fameme")) {
			player.setFame(Integer.parseInt(sub[1]));
			player.updateSingleStat(MapleStat.FAME, player.getFame());
		} else if (sub[0].equals("giftnx")) {
			MapleCharacter victim = cserv.getPlayerStorage()
					.getCharacterByName(sub[1]);
			victim.getCashShop().gainCash(1, Integer.parseInt(sub[2]));
			player.dropMessage("Done!");
			victim.dropMessage(6, player.getName() + " has given you "
					+ sub[2] + " NX.");
		} else if (sub[0].equals("nxme")) {
			player.getCashShop().gainCash(1, Integer.parseInt(sub[1]));
			player.dropMessage(6, "You know have " + sub[1] + " NX.");
		
		} else if (sub[0].equalsIgnoreCase("ep")) {
			MapleCharacter victim = c.getChannelServer().getPlayerStorage()
					.getCharacterByName(sub[1]);
			victim.addEventpoints(Integer.parseInt(sub[2]));
			player.dropMessage("You have given " + sub[1] + " "
					+ sub[2] + " Event Points.");
			victim.dropMessage(6, player.getName() + " has given you "
					+ sub[2] + " Event Points.");
		} else if (sub[0].equalsIgnoreCase("epme")) {
			player.addEventpoints(Integer.parseInt(sub[1]));
			player.dropMessage("You now have " + sub[1] + " Event Points.");
                 } else if (sub[0].equalsIgnoreCase("rr")) {
			int range1 = Integer.parseInt(sub[1]);
			int range2 = Integer.parseInt(sub[2]);
			int randRR = (int) 1
					+ (int) (Math.random() * ((range1 - range2) + 1));
			String rand = Integer.toString(randRR);
			for (MapleCharacter victim : player.getMap().getCharacters()) {
				if (victim != null && victim.getGMLevel()) {

					victim.setHp(0);
					victim.setMp(0);
					victim.updateSingleStat(MapleStat.HP, 0);
					victim.updateSingleStat(MapleStat.MP, 0);
					victim.dropMessage(
							6,
							String.format(
									"[Russian Roulette] The random number is: %s",
									rand));
				} else {
					victim.dropMessage(
							6,
							String.format(
									"[Russian Roulette] The random number is: %s",
									rand));
				}
			}
            
        } else if (sub[0].equals("taggermap")) {
            if (player.getMap().taggermapOn()) {
                player.getMap().setTaggermap(false);
                player.dropMessage(6, "You've deactivated taggermap.");
            } else {
                player.getMap().setTaggermap(true);
                player.dropMessage(5, "You've declared the map as a taggermap.");
                player.getMap().broadcastMessage(MaplePacketCreator.serverNotice(6, "Taggermap has been activated! use @tag inorder to tag other players."));
            }
        } else if (sub[0].equals("dtm")) {
                  player.getMap().setTaggermap(false);
                player.dropMessage(6, "You've deactivated taggermap."); 
        } else if (sub[0].equals("etm")) { 
            player.getMap().setTaggermap(true);
                player.dropMessage(5, "You've declared the map as a taggermap.");
                player.getMap().broadcastMessage(MaplePacketCreator.serverNotice(6, "Taggermap has been activated! use @tag inorder to tag other players."));            
        } else if (sub[0].equals("tag")) {
            MapleMap map = player.getMap();
            List<MapleMapObject> players = map.getMapObjectsInRange(player.getPosition(), player.getMap().getTagRange(), Arrays.asList(MapleMapObjectType.PLAYER));
            for (MapleMapObject closeplayers : players) {
                MapleCharacter playernear = (MapleCharacter) closeplayers;
                if (playernear.isAlive() && playernear != player) {                     
                        if (!playernear.isGM() || playernear.isTemp()) {
                            playernear.setHp(0);
                            playernear.updateSingleStat(MapleStat.HP, 0);
                            playernear.dropMessage(6, "You were too close to a GM.");
                            map.broadcastMessage(MaplePacketCreator.serverNotice(6, playernear.getName() + " has been tagged. "));

                        } else {

                            playernear.dropMessage(6, "Gms cant play tag fuckoff.");
                        }
                    
                }
            }
        } else if (sub[0].equalsIgnoreCase("warpmap") || sub[0].equalsIgnoreCase("wm") ) {
            try {
                List<MapleCharacter> players = new ArrayList<>();
                if (sub.length > 1) {
                    MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]);
                    MapleMap newmap;
                    if (victim != null) {
                        newmap = victim.getMap(); // Players name
                    } else {
                        newmap = c.getChannelServer().getMapFactory().getMap(Integer.valueOf(sub[1])); // Map id
                    }
                    for (MapleCharacter tobewarped : player.getMap().getCharacters()) {
                        if (victim != null) {
                            if (tobewarped != victim) {
                                players.add(tobewarped);
                            }
                        } else {
                            players.add(tobewarped);
                        }
                    }
                    for (int i = 0; i < players.size(); i++) {
                        if (victim != null) {
                            players.get(i).changeMap(newmap, victim.getPosition());
                        } else {
                            players.get(i).changeMap(newmap);
                        }
                    }
                } else {
                    for (MapleCharacter tobewarped : player.getMap().getCharacters()) {
                        if (tobewarped != player) {
                            players.add(tobewarped);
                        }
                    }
                    for (int i = 0; i < players.size(); i++) {
                        players.get(i).changeMap(player.getMap(), player.getPosition());
                    }

                }
            } catch (Exception e) {
                System.out.println("Failed to warp map [" + player.getName() + "]");
            }
        } else if (sub[0].equalsIgnoreCase("wdm")) {
                  List<MapleCharacter>players = new ArrayList<>();
                  for(MapleCharacter a1 : player.getMap().getCharacters())
                      if(!a1.isGM() || a1.isTemp())
                          players.add(a1);                          
                      
                  
                  for(int i = 0 ; i < players.size(); i++){
                      players.get(i).setHpMp(0);
                      players.get(i).changeMap(910000000);
                  }
          } else if (sub[0].equalsIgnoreCase("wam")) {
                  List<MapleCharacter>players = new ArrayList<>();
                  for(MapleCharacter a1 : player.getMap().getCharacters())
                      if(!a1.isGM() || a1.isTemp())
                          players.add(a1);                          
                      
                  
                  for(int i = 0 ; i < players.size(); i++){
                      players.get(i).setHpMp(30000);
                      players.get(i).changeMap(910000000);
                  } 
          } else if (sub[0].equalsIgnoreCase("warpdead") || sub[0].equalsIgnoreCase("wd")) {
                  MapleCharacter victim = c.getWorldServer().getPlayerStorage().getCharacterByName(sub[1]);
                     victim.setHpMp(0);
                      victim.changeMap(1020600);         
          } else if (sub[0].equalsIgnoreCase("warpalive") || sub[0].equalsIgnoreCase("wa")) {
                  MapleCharacter victim = c.getWorldServer().getPlayerStorage().getCharacterByName(sub[1]);
                     victim.setHpMp(30000);
                      victim.changeMap(1020600);
                         
         } else if (sub[0].equalsIgnoreCase("warpmapx") || sub[0].equalsIgnoreCase("wmx") ) {
        
             
                List<MapleCharacter> players = new ArrayList<>();
                for(MapleCharacter a1 : player.getMap().getCharacters())
                    if(a1.getName() != player.getName())
                        players.add(a1);
                
                for(int i=0;i < players.size();i++)
                    players.get(i).changeMap(player.getMap(),player.getPosition());
        } else if (sub[0].equalsIgnoreCase("warpmapdx") || sub[0].equalsIgnoreCase("wdx") ) {   
             List<MapleCharacter> players = new ArrayList<>();
                for(MapleCharacter a1 : player.getMap().getCharacters())
                    if(a1.getName() != player.getName() && !a1.isAlive())
                        players.add(a1);
                for(int i = 0 ; i < players.size() ; i ++)
                    players.get(i).changeMap(player.getMap(),player.getPosition());
                        
                    
         } else if (sub[0].equalsIgnoreCase("warpmapax") || sub[0].equalsIgnoreCase("wax") ) {   
             List<MapleCharacter> players = new ArrayList<>();
                for(MapleCharacter a1 : player.getMap().getCharacters())
                    if(a1.getName() != player.getName() && a1.isAlive())
                       players.add(a1);
                for(int i = 0 ;i < players.size() ; i ++)
                    players.get(i).changeMap(player.getMap(),player.getPosition());
                           
        } else if (sub[0].equals("addcurr")) {
            if (sub.length > 2) {
                MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]);
                if (victim != null) {
                    MapleInventoryManipulator.addById(victim.getClient(), 4000038, (short) Integer.parseInt(sub[2]));
                } else {
                    player.dropMessage(5, "Error. Player isn't on");
                }
            } else {
                player.dropMessage(5, "Error. Please type the command as follows !addcurr <player> <amount>");
            }
        } else if (sub[0].equals("addep")) {
            if (sub.length > 2) {
                MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]);
                if (victim != null) {
                    if (victim.isLoggedin()) {
                        victim.addEventpoints(Integer.parseInt(sub[2]));
                        victim.dropMessage(6, player.getName() + " has given you " + Integer.parseInt(sub[2]) + " ep!");
                        player.dropMessage(6, "You've given " + victim.getName() + " " + Integer.parseInt(sub[2]) + " ep!");
                    } else {
                        player.dropMessage(5, "Error. The following player isnt online!");
                    }
                } else {
                    player.dropMessage(5, "Error. The following player doesn't exist");
                }
            } else {
                player.dropMessage(5, "Error. Please type the command as follows, !addep <player> <amount>");
            }

        } else if (sub[0].equals("tool")) {
            NPCScriptManager.getInstance().start(c, 9010000, "c_gmTool", player);

            // Maplediseases
        } else if (sub[0].equals("mute")) {
            if (sub.length > 1) {
                MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]);
                victim.setMuted(true);
                victim.dropMessage(5,"You've been muted. @callgm is available if in need.");
            } else {
                player.dropMessage(5, "Error. Please type in a player you wish to mute.");
            }
        } else if (sub[0].equals("permmute") || sub[0].equals("pmute")) {
             if (sub.length > 1) {
                MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]);
                if(victim != null){
                  victim.setPermmute(1);
                   victim.dropMessage(5,"You've been permanently muted. @callgm is available if in need.");
                }
                
            } else {
                player.dropMessage(5, "Error. Please type in a player you wish to permanently mute.");
            }
        } else if (sub[0].equals("permunmute") || sub[0].equals("punmute")) {
             if (sub.length > 1) {
                MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]);
                if(victim != null)
                 victim.setPermmute(0);
            } else {
                player.dropMessage(5, "Error. Please type in a player you wish to unmute.");
            }
        } else if (sub[0].equals("unmute")) {
            if (sub.length > 1) {
                MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]);
                victim.setMuted(false);
            } else {
                player.dropMessage(5, "Error. Please type in a player to unmute");
            }
        } else if (sub[0].equals("unmutemap")) {
            player.getMap().setMuted(false);
            player.dropMessage(5, "The map you are in has been un-muted.");
        } else if (sub[0].equals("mutemap")) {
            player.getMap().setMuted(true);
            player.dropMessage(5, "The map you are in has been muted.");
        } else if (sub[0].equals("checkdmg")) {
            MapleCharacter victim = c.getWorldServer().getPlayerStorage().getCharacterByName(sub[1]);
            int maxBase = victim.calculateMaxBaseDamage(victim.getTotalWatk());
            Integer watkBuff = victim.getBuffedValue(MapleBuffStat.WATK);
            Integer matkBuff = victim.getBuffedValue(MapleBuffStat.MATK);
            Integer blessing = victim.getSkillLevel(10000000 * player.getJobType() + 12);
            if (watkBuff == null) {
                watkBuff = 0;
            }
            if (matkBuff == null) {
                matkBuff = 0;
            }

            player.dropMessage(5, "Cur Str: " + victim.getTotalStr() + " Cur Dex: " + victim.getTotalDex() + " Cur Int: " + victim.getTotalInt() + " Cur Luk: " + victim.getTotalLuk());
            player.dropMessage(5, "Cur WATK: " + victim.getTotalWatk() + " Cur MATK: " + victim.getTotalMagic());
            player.dropMessage(5, "Cur WATK Buff: " + watkBuff + " Cur MATK Buff: " + matkBuff + " Cur Blessing Level: " + blessing);
            player.dropMessage(5, victim.getName() + "'s maximum base damage (before skills) is " + maxBase);
        } else if (sub[0].equals("inmap")) {
            String s = "";
            for (MapleCharacter chr : player.getMap().getCharacters()) {
                s += chr.getName() + " ";
            }
            player.message(s);
        } else if (sub[0].equals("cleardrops")) {
            player.getMap().clearDrops(player);
        } else if (sub[0].equals("reloadevents")) {
            for (Channel ch : Server.getInstance().getAllChannels()) {
                ch.reloadEventScriptManager();
            }
            player.dropMessage(5, "Reloaded Events");
        } else if (sub[0].equals("reloaddrops")) {
            MapleMonsterInformationProvider.getInstance().clearDrops();
            player.dropMessage(5, "Reloaded Drops");
        } else if (sub[0].equals("reloadportals")) {
            PortalScriptManager.getInstance().reloadPortalScripts();
            player.dropMessage(5, "Reloaded Portals");
        } else if (sub[0].equals("whereami")) { //This is so not going to work on the first commit
            player.yellowMessage("Map ID: " + player.getMap().getId());
        } else if (sub[0].equals("rewarp")) {
         List<MapleCharacter>players = new ArrayList<>();
                  for(MapleCharacter a1 : player.getMap().getCharacters())
                      if(!a1.isGM() || a1.isTemp())
                          players.add(a1);                          
                      
                  
                  for(int i = 0 ; i < players.size(); i++)                     
                      players.get(i).changeMap(player.getMapId());
                    
        } else if (sub[0].equals("reloadmap")) {
            if (sub.length > 1) {
                MapleMap oldMap = c.getChannelServer().getMapFactory().getMap(Integer.parseInt(sub[1]));
                MapleMap newMap = c.getChannelServer().getMapFactory().getMap(Integer.parseInt(sub[1]));              
                List<MapleCharacter> players = new ArrayList<>();
                for (MapleCharacter ch : oldMap.getCharacters()) {
                    players.add(ch);
                }              
                oldMap.deleteTempNpcs();
                oldMap.deleteBernards();
                for (int i = 0; i < players.size(); i++) {
                    players.get(i).changeMap(newMap);
                }

                oldMap = null;
                newMap.respawn();
            } else {
                MapleMap oldMap = c.getPlayer().getMap();
                MapleMap newMap = c.getChannelServer().getMapFactory().getMap(player.getMapId());
                //  int playersinmap = oldMap.getCharacters().size();
                List<MapleCharacter> players = new ArrayList<>();
                for (MapleCharacter ch : oldMap.getCharacters()) {
                    players.add(ch);
                }
                /* for(MapleMapObject mmo : player.getMap().getMapObjects())
                            if(mmo.getObjectId() < 1000)
                            player.getMap().removeMapObject(mmo); */
                oldMap.deleteTempNpcs();
                for (int i = 0; i < players.size(); i++) {
                    players.get(i).changeMap(newMap);
                }

                oldMap = null;
                newMap.respawn();
            }

     /*  } else if (sub[0].equals("checkcd")) {
           c.announce(MaplePacketCreator.skillCooldown(2321006,1));
         } else if (sub[0].equals("checkcdoff")) {
              c.announce(MaplePacketCreator.skillCooldown(2321006,0)); */
       } else if (sub[0].equals("music")) {
            if (sub.length < 2) {
                player.yellowMessage("Syntax: !music <song>");
                for (String s : songs) {
                    player.yellowMessage(s);
                }
                return false;
            }
            String song = StringUtil.joinStringFrom(sub, 1);
            for (String s : songs) {
                if (s.equals(song)) {
                    player.getMap().broadcastMessage(MaplePacketCreator.musicChange(s));
                    player.yellowMessage("Now playing song " + song + ".");
                    return true;
                }
            }
            player.yellowMessage("Song not found, please enter a song below.");
            for (String s : songs) {
                player.yellowMessage(s);
            }
        } else if (sub[0].equals("monitor")) {
            if (sub.length < 1) {
                player.yellowMessage("Syntax: !monitor <ign>");
                return false;
            }
            MapleCharacter victim = c.getWorldServer().getPlayerStorage().getCharacterByName(sub[1]);
            if (victim == null) {
                player.yellowMessage("Player not found!");
                return false;
            }
            boolean monitored = MapleLogger.monitored.contains(victim.getName());
            if (monitored) {
                MapleLogger.monitored.remove(victim.getName());
            } else {
                MapleLogger.monitored.add(victim.getName());
            }
            player.yellowMessage(victim.getName() + " is " + (!monitored ? "now being monitored." : "no longer being monitored."));
            String message = player.getName() + (!monitored ? " has started monitoring " : " has stopped monitoring ") + victim.getName() + ".";
            Server.getInstance().broadcastGMMessage(MaplePacketCreator.serverNotice(5, message));
        } else if (sub[0].equals("monitors")) {
            for (String ign : MapleLogger.monitored) {
                player.yellowMessage(ign + " is being monitored.");
            }
        } else if (sub[0].equals("ignore")) {
            if (sub.length < 1) {
                player.yellowMessage("Syntax: !ignore <ign>");
                return false;
            }
            MapleCharacter victim = c.getWorldServer().getPlayerStorage().getCharacterByName(sub[1]);
            if (victim == null) {
                player.yellowMessage("Player not found!");
                return false;
            }
            boolean monitored = MapleLogger.ignored.contains(victim.getName());
            if (monitored) {
                MapleLogger.ignored.remove(victim.getName());
            } else {
                MapleLogger.ignored.add(victim.getName());
            }
            player.yellowMessage(victim.getName() + " is " + (!monitored ? "now being ignored." : "no longer being ignored."));
            String message = player.getName() + (!monitored ? " has started ignoring " : " has stopped ignoring ") + victim.getName() + ".";
            Server.getInstance().broadcastGMMessage(MaplePacketCreator.serverNotice(5, message));
        } else if (sub[0].equals("ignored")) {
            for (String ign : MapleLogger.ignored) {
                player.yellowMessage(ign + " is being ignored.");
            }
        } else if (sub[0].equals("pos")) {
            float xpos = player.getPosition().x;
            float ypos = player.getPosition().y;
            float fh = player.getMap().getFootholds().findBelow(player.getPosition()).getId();
            player.dropMessage("Position: (" + xpos + ", " + ypos + ")");
            player.dropMessage("Foothold ID: " + fh);
        } else if (sub[0].equals("dc")) {
            MapleCharacter victim = c.getWorldServer().getPlayerStorage().getCharacterByName(sub[1]);
            if (victim == null) {
                victim = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]);
                if (victim == null) {
                    victim = player.getMap().getCharacterByName(sub[1]);
                    if (victim != null) {
                        try {//sometimes bugged because the map = null
                            victim.getClient().disconnect(true, false);
                            player.getMap().removePlayer(victim);
                        } catch (Exception e) {
                        }
                    } else {
                        return true;
                    }
                }
            }
            if (player.gmLevel() < victim.gmLevel()) {
                victim = player;
            }
            victim.getClient().disconnect(false, false);
        } else if (sub[0].equals("exprate")) {
            c.getWorldServer().setExpRate(Integer.parseInt(sub[1]));
        } else if (sub[0].equals("chat") || sub[0].equals("chattype")) {
            player.toggleWhiteChat();
            player.message("Your chat is now " + (player.getWhiteChat() ? "white" : "normal") + ".");

        } else if (sub[0].equals("warp")) {

            MapleCharacter victim = cserv.getPlayerStorage().getCharacterByName(sub[1]);
             MapleMap target;
            if (victim != null) { // Warping to player                                  
                 target = victim.getMap();
                player.changeMap(target);

            } else { // Warping to map
                  target = c.getChannelServer().getMapFactory().getMap(Integer.valueOf(sub[1]));
                   player.changeMap(target);
                  
//  player.message("Error. Player not found on the channel.");
            }
        } else if (sub[0].equals("warpx") || sub[0].equals("wx")) {
            MapleCharacter victim = cserv.getPlayerStorage().getCharacterByName(sub[1]);
            if (victim != null) { // Warping to player                                  
                MapleMap target = victim.getMap();
                player.changeMap(target, victim.getPosition());
            } else {
                player.message("Error. Player not found on the channel.");
            }
         } else if (sub[0].equals("warpheredx") || sub[0].equals("whdx")) {
            MapleCharacter victim = cserv.getPlayerStorage().getCharacterByName(sub[1]);
            if (victim != null) { // Warping player here                                 
                victim.changeMap(player.getMap(),player.getPosition());
                victim.setHpMp(0);
            } else {
                player.message("Error. Player not found on the channel.");
            } 
        } else if (sub[0].equals("warphereax") || sub[0].equals("whax")) {
            MapleCharacter victim = cserv.getPlayerStorage().getCharacterByName(sub[1]);
            if (victim != null) { // Warping to player                                  
                victim.changeMap(player.getMap(),player.getPosition());
                victim.setHpMp(30000);
            } else {
                player.message("Error. Player not found on the channel.");
            }     
        } else if (sub[0].equals("warphere") || sub[0].equals("wh")) {
            List<MapleCharacter> victims = new ArrayList<MapleCharacter>();
            for (int v = 1; v < sub.length; v++) {
                victims.add(c.getChannelServer().getPlayerStorage().getCharacterByName(sub[v]));
            }
            for (MapleCharacter chr : victims) {
                if (player.getClient().getChannel() != chr.getClient().getChannel()) {
                    chr.message("You will be cross-channel warped. This may take a few seconds.");
                    chr.getClient().changeChannel(player.getClient().getChannel());
                }
                chr.changeMap(player.getMap(), player.getMap().findClosestSpawnpoint(player.getPosition()));
            }
        } else if (sub[0].equals("whx")) {
            List<MapleCharacter> victims = new ArrayList<MapleCharacter>();
            for (int v = 1; v < sub.length; v++) {
                victims.add(c.getChannelServer().getPlayerStorage().getCharacterByName(sub[v]));
            }
            for (MapleCharacter chr : victims) {
                chr.changeMap(c.getPlayer().getMap(), c.getPlayer().getPosition().x, c.getPlayer().getPosition().y);
            }
            victims.clear();
        } else if (sub[0].equals("warpout") || sub[0].equals("wo")) {
            List<MapleCharacter> victims = new ArrayList<MapleCharacter>();
            for (int v = 1; v < sub.length; v++) {
                victims.add(c.getChannelServer().getPlayerStorage().getCharacterByName(sub[v]));
            }
            for (MapleCharacter chr : victims) {
                chr.changeMap(910000000);
                chr.dispelDebuffs(true);
                chr.setHp(chr.getCurrentMaxHp());
                chr.updateSingleStat(MapleStat.HP, chr.getHp());
                chr.setMp(chr.getCurrentMaxMp());
                chr.updateSingleStat(MapleStat.MP, chr.getMp());
                chr.setMuted(false);
            }
            victims.clear();
        } else if (sub[0].equals("fame")) {
            MapleCharacter victim = cserv.getPlayerStorage().getCharacterByName(sub[1]);
            if(!victim.isGM()) return false;
            victim.setFame(Integer.parseInt(sub[2]));
            victim.updateSingleStat(MapleStat.FAME, victim.getFame());      
        } else if (sub[0].equals("online")) {
            String text = "Online Players "; 
            int allplayers = 0;
            String cc1 = "Players in Channel 1: ", cc2 = "Players in Channel 2: ", cc3 = "Players in Channel 3: ";
            for (MapleCharacter chrs : c.getWorldServer().getPlayerStorage().getAllCharacters()) {
                // text += chrs.getName() + ", ";
                switch (chrs.getClient().getChannel()) {
                    case 1:
                        cc1 += chrs.getName() + ", ";
                        allplayers++;
                        break;
                    case 2:
                        cc2 += chrs.getName() + ", ";
                        allplayers++;
                        break;
                    case 3:
                        cc3 += chrs.getName() + ", ";
                        allplayers++;
                        break;
                }

            }
            if (cc1.length() > 22) {
                cc1 = cc1.substring(0, cc1.length() - 2);
            }
            if (cc2.length() > 22) {
                cc2 = cc2.substring(0, cc2.length() - 2);
            }
            if (cc3.length() > 22) {
                cc3 = cc3.substring(0, cc3.length() - 2);
            }

            player.dropMessage(6, text + "(" + allplayers + ") :");
            player.dropMessage(6, cc1);
            player.dropMessage(6, cc2);
            player.dropMessage(6, cc3);

        
        } else if (sub[0].equals("gmshop")) {
            MapleShopFactory.getInstance().getShop(1337).sendShop(c);
        } else if (sub[0].equals("heal")) {
            if (sub.length > 1) {
                MapleCharacter victim = c.getWorldServer().getPlayerStorage().getCharacterByName(sub[1]);
                victim.setHpMp(30000);
            } else {
                player.setHpMp(30000);
            }
        } else if (sub[0].equals("healmap")) {
            for (MapleCharacter chrs : player.getMap().getCharacters()) {
                chrs.setHpMp(30000);
            }

        } else if (sub[0].equals("clock")) {
            player.getMap().broadcastMessage(MaplePacketCreator.getClock(Integer.parseInt(sub[1])));
            final String[] s = StringUtil.joinStringFrom(sub, 2).split(" ");
            tmp = c;
            TimerManager.getInstance().schedule(new Runnable() {
                @Override
                public void run() {
                    try {
                        executeGMCommandLv5(tmp, s, '/');
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                    tmp = null;
                }
            }, Integer.parseInt(sub[1]) * 1000);
            } else if (sub[0].equals("htb")) {
			int mid = 9500365;
			int num = 1;
			int newhp = Integer.parseInt(sub[1]);

			MapleMonsterStats overrideStats = new MapleMonsterStats();
			overrideStats.setHp(newhp);

			for (int i = 0; i < num; i++) {
				MapleMonster mob = MapleLifeFactory.getMonster(mid);
				mob.setHp(newhp);
				mob.setOverrideStats(overrideStats);
				c.getPlayer()
						.getMap()
						.spawnMonsterOnGroudBelow(mob,
								c.getPlayer().getPosition());

			}
			return true;

        } else if (sub[0].equals("vp")) {
            c.addVotePoints(Integer.parseInt(sub[1]));
        } else if (sub[0].equalsIgnoreCase("gmtext")) {
			int text;
			if (sub[1].equalsIgnoreCase("normal")) {
				text = 0;
			} else if (sub[1].equalsIgnoreCase("orange")) {
				text = 1;
			} else if (sub[1].equalsIgnoreCase("pink")) {
				text = 2;
			} else if (sub[1].equalsIgnoreCase("purple")) {
				text = 3;
			} else if (sub[1].equalsIgnoreCase("green")) {
				text = 4;
			} else if (sub[1].equalsIgnoreCase("red")) {
				text = 5;
			} else if (sub[1].equalsIgnoreCase("blue")) {
				text = 6;
			} else if (sub[1].equalsIgnoreCase("whitebg")) {
				text = 7;
			} else if (sub[1].equalsIgnoreCase("lightinggreen")) {
				text = 8;
			} else if (sub[1].equalsIgnoreCase("yellow")) {
				text = 9;
			} else {
				player.dropMessage("Wrong syntax: use !gmtext normal/orange/pink/purple/green/blue/red/whitebg/lightinggreen");
				return true;
			}
        } else if (sub[0].equals("id")) {
            try {
                try (BufferedReader dis = new BufferedReader(new InputStreamReader(new URL("http://www.mapletip.com/search_java.php?search_value=" + sub[1] + "&check=true").openConnection().getInputStream()))) {
                    String s;
                    while ((s = dis.readLine()) != null) {
                        player.dropMessage(s);
                    }
                }
            } catch (Exception e) {
            }

        } else if (sub[0].equals("item") || sub[0].equals("drop")) {
            int itemId = Integer.parseInt(sub[1]);
            short quantity = 1;
            try {
                quantity = Short.parseShort(sub[2]);
            } catch (Exception e) {
            }
            if (sub[0].equals("item")) {
                int petid = -1;
                if (ItemConstants.isPet(itemId)) {
                    petid = MaplePet.createPet(itemId);
                }
                if (player.itemExists(itemId)) {
                    MapleInventoryManipulator.addById(c, itemId, quantity, player.getName(), petid, -1);
                } else {
                    player.dropMessage("This item does not exist.");
                }
            } else {
                Item toDrop;
                if (MapleItemInformationProvider.getInstance().getInventoryType(itemId) == MapleInventoryType.EQUIP) {
                    toDrop = MapleItemInformationProvider.getInstance().getEquipById(itemId);
                } else {
                    toDrop = new Item(itemId, (short) 0, quantity);
                }
                c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), toDrop, c.getPlayer().getPosition(), true, true);
            }
        } else if (sub[0].equals("randitem")) {
            Equip sword = new Equip(1302000, (short) 0, 1);
            sword.setStr((short) 30000);
            MapleInventoryManipulator.addFromDrop(c, (Item) sword, true);
        } else if (sub[0].equals("expeds")) {
            for (Channel ch : Server.getInstance().getChannelsFromWorld(0)) {
                if (ch.getExpeditions().size() == 0) {
                    player.yellowMessage("No Expeditions in Channel " + ch.getId());
                    continue;
                }
                player.yellowMessage("Expeditions in Channel " + ch.getId());
                int id = 0;
                for (MapleExpedition exped : ch.getExpeditions()) {
                    id++;
                    player.yellowMessage("> Expedition " + id);
                    player.yellowMessage(">> Type: " + exped.getType().toString());
                    player.yellowMessage(">> Status: " + (exped.isRegistering() ? "REGISTERING" : "UNDERWAY"));
                    player.yellowMessage(">> Size: " + exped.getMembers().size());
                    player.yellowMessage(">> Leader: " + exped.getLeader().getName());
                    int memId = 2;
                    for (MapleCharacter member : exped.getMembers()) {
                        if (exped.isLeader(member)) {
                            continue;
                        }
                        player.yellowMessage(">>> Member " + memId + ": " + member.getName());
                        memId++;
                    }
                }
            }
        } else if (sub[0].equals("kill")) {
            if (sub.length >= 2) {
                MapleCharacter victim = cserv.getPlayerStorage().getCharacterByName(sub[1]);
                if (victim != null) {
                    victim.setHpMp(0);
                    Server.getInstance().broadcastGMMessage(MaplePacketCreator.serverNotice(5, player.getName() + " used !kill on " + victim.getName()));
                }
            }
        } else if (sub[0].equals("killmap")) {

            for (MapleCharacter a1 : player.getMap().getCharacters()) {
                if(!a1.isGM() || a1.isTemp())
                a1.setHpMp(0);
            }
        } else if (sub[0].equals("seed")) {
            if (player.getMapId() != 910010000) {
                player.yellowMessage("This command can only be used in HPQ.");
                return false;
            }
            Point pos[] = {new Point(7, -207), new Point(179, -447), new Point(-3, -687), new Point(-357, -687), new Point(-538, -447), new Point(-359, -207)};
            int seed[] = {4001097, 4001096, 4001095, 4001100, 4001099, 4001098};
            for (int i = 0; i < pos.length; i++) {
                Item item = new Item(seed[i], (byte) 0, (short) 1);
                player.getMap().spawnItemDrop(player, player, item, pos[i], false, true);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } else if (sub[0].equals("killall")) {
            List<MapleMapObject> monsters = player.getMap().getMapObjectsInRange(player.getPosition(), Double.POSITIVE_INFINITY, Arrays.asList(MapleMapObjectType.MONSTER));
            MapleMap map = player.getMap();
            for (MapleMapObject monstermo : monsters) {
                MapleMonster monster = (MapleMonster) monstermo;
                if (!monster.getStats().isFriendly()) {
                    map.killMonster(monster, player, true);
                    monster.giveExpToCharacter(player, monster.getExp() * c.getPlayer().getExpRate(), true, 1);
                }
            }
            player.dropMessage("Killed " + monsters.size() + " monsters.");
        } else if (sub[0].equals("monsterdebug")) {
            List<MapleMapObject> monsters = player.getMap().getMapObjectsInRange(player.getPosition(), Double.POSITIVE_INFINITY, Arrays.asList(MapleMapObjectType.MONSTER));
            for (MapleMapObject monstermo : monsters) {
                MapleMonster monster = (MapleMonster) monstermo;
                player.message("Monster ID: " + monster.getId());
            }
        } else if (sub[0].equals("unbug")) { // uh well i thin
            c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.enableActions());
        } else if (sub[0].equals("level")) {
            if (sub.length < 3) {
                player.setLevel(Integer.parseInt(sub[1]) - 1);
                player.gainExp(-player.getExp(), false, false);
                player.levelUp(false);
            } else {
                MapleCharacter victim = cserv.getPlayerStorage().getCharacterByName(sub[1]);
                if (victim != null) {
                    victim.setLevel(Integer.parseInt(sub[2]) - 1);
                    victim.gainExp(-victim.getExp(), false, false);
                    victim.levelUp(false);
                } else {
                    player.dropMessage(5, "Error. player isn't on.");
                }
            }

        } else if (sub[0].equals("levelpro")) {
            while (player.getLevel() < Math.min(255, Integer.parseInt(sub[1]))) {
                player.levelUp(false);

            }
        } else if (sub[0].equals("speak")) {
            String text = StringUtil.joinStringFrom(sub, 2);
            String name = String.valueOf(sub[1]);
            if(name.equals("dev")) name = player.getName();
            MapleCharacter victim = player.getMap().getCharacterByName(name);
            if (victim != null) {
                player.getMap().broadcastMessage(MaplePacketCreator.getChatText(victim.getId(), text, false, 0));
            } else {
                victim = player.getMap().getCharacterById(Integer.parseInt(sub[1]));
                if (victim != null) {
                    player.getMap().broadcastMessage(MaplePacketCreator.getChatText(victim.getId(), text, false, 0));
                } else {
                    player.dropMessage(5, "Error. Chosen player doesn't exist");
                }

            }

        } else if (sub[0].equals("speakall")) {
            String text = StringUtil.joinStringFrom(sub, 1);
            for (MapleCharacter mch : player.getMap().getCharacters()) {
                mch.getMap().broadcastMessage(MaplePacketCreator.getChatText(mch.getId(), text, false, 0));
            }
        } else if (sub[0].equals("maxstat")) {
            final String[] s = {"setall", String.valueOf(Short.MAX_VALUE)};
            executeGMCommandLv5(c, s, heading);
            player.setLevel(255);
            player.setFame(13337);
            player.setMaxHp(30000);
            player.setMaxMp(30000);
            player.updateSingleStat(MapleStat.LEVEL, 255);
            player.updateSingleStat(MapleStat.FAME, 13337);
            player.updateSingleStat(MapleStat.MAXHP, 30000);
            player.updateSingleStat(MapleStat.MAXMP, 30000);
        } else if (sub[0].equals("job")) {
                player.changeJob(MapleJob.getById(Integer.parseInt(sub[1])));
                player.equipChanged();
            
        } else if (sub[0].equals("jobmap") || sub[0].equals("jobm")) {
            if (sub.length > 1) {
                for (MapleCharacter victim : player.getMap().getCharacters()) {
                    victim.changeJob(MapleJob.getById(Integer.parseInt(sub[1])));
                    victim.equipChanged();
                }
            } else {
                player.dropMessage(5, "Error. Please type the command as follows !jobmap <id>");
            }
        } else if (sub[0].equals("mesos")) {
            player.gainMeso(Integer.parseInt(sub[1]), true);
        } else if (sub[0].equals("fieldlimits")) {

            player.dropMessage(6, "Check : " + player.getMap().getFootholds().getX1() + ", " + player.getMap().getFootholds().getY1() + ", " + player.getMap().getFootholds().getX2() + ", " + player.getMap().getFootholds().getY2());
        } else if (sub[0].equals("notice")) {
            if (sub[1].equals("b")) {
                Server.getInstance().broadcastMessage(MaplePacketCreator.serverNotice(1, "[Notice] " + StringUtil.joinStringFrom(sub, 2)));
                return true;
            }
            Server.getInstance().broadcastMessage(MaplePacketCreator.serverNotice(6, "[Notice] " + StringUtil.joinStringFrom(sub, 1)));
        } else if (sub[0].equals("rip")) {
            Server.getInstance().broadcastMessage(MaplePacketCreator.serverNotice(6, "[RIP]: " + StringUtil.joinStringFrom(sub, 1)));
        } else if (sub[0].equals("openportal")) {
            player.getMap().getPortal(sub[1]).setPortalState(true);
        } else if (sub[0].equals("pe")) {
            String packet = "";
            try {
                InputStreamReader is = new FileReader("pe.txt");
                Properties packetProps = new Properties();
                packetProps.load(is);
                is.close();
                packet = packetProps.getProperty("pe");
            } catch (IOException ex) {
                player.yellowMessage("Failed to load pe.txt");
                return false;
            }
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
            mplew.write(HexTool.getByteArrayFromHexString(packet));
            SeekableLittleEndianAccessor slea = new GenericSeekableLittleEndianAccessor(new ByteArrayByteStream(mplew.getPacket()));
            short packetId = slea.readShort();
            final MaplePacketHandler packetHandler = PacketProcessor.getProcessor(0, c.getChannel()).getHandler(packetId);
            if (packetHandler != null && packetHandler.validateState(c)) {
                try {
                    player.yellowMessage("Recieving: " + packet);
                    packetHandler.handlePacket(slea, c);
                } catch (final Throwable t) {
                    FilePrinter.printError(FilePrinter.PACKET_HANDLER + packetHandler.getClass().getName() + ".txt", t, "Error for " + (c.getPlayer() == null ? "" : "player ; " + c.getPlayer() + " on map ; " + c.getPlayer().getMapId() + " - ") + "account ; " + c.getAccountName() + "\r\n" + slea.toString());
                    return false;
                }
            }
        } else if (sub[0].equals("closeportal")) {
            player.getMap().getPortal(sub[1]).setPortalState(false);
        } else if (sub[0].equals("online2")) {
            int total = 0;
            for (Channel ch : srv.getChannelsFromWorld(player.getWorld())) {
                int size = ch.getPlayerStorage().getAllCharacters().size();
                total += size;
                String s = "(Channel " + ch.getId() + " Online: " + size + ") : ";
                if (ch.getPlayerStorage().getAllCharacters().size() < 50) {
                    for (MapleCharacter chr : ch.getPlayerStorage().getAllCharacters()) {
                        s += MapleCharacter.makeMapleReadable(chr.getName()) + ", ";
                    }
                    player.dropMessage(s.substring(0, s.length() - 2));
                }
            }
            player.dropMessage("There are a total of " + total + " players online.");
        } else if (sub[0].equals("pap")) {
            player.getMap().spawnMonsterOnGroudBelow(MapleLifeFactory.getMonster(8500001), player.getPosition());
        } else if (sub[0].equals("pianus")) {
            player.getMap().spawnMonsterOnGroudBelow(MapleLifeFactory.getMonster(8510000), player.getPosition());

        } else if (sub[0].equals("search")) {
            if (sub.length > 2) {
                String search = StringUtil.joinStringFrom(sub, 2);
                MapleData data = null;
                MapleDataProvider dataProvider = MapleDataProviderFactory.getDataProvider(new File("wz/" + "String.wz"));
                if (!sub[1].equalsIgnoreCase("ITEM")) {
                    if (sub[1].equalsIgnoreCase("NPC")) {
                        data = dataProvider.getData("Npc.img");
                    } else if (sub[1].equalsIgnoreCase("MOB") || sub[1].equalsIgnoreCase("MONSTER")) {
                        data = dataProvider.getData("Mob.img");
                    } else if (sub[1].equalsIgnoreCase("SKILL")) {
                        data = dataProvider.getData("Skill.img");
                    } else {
                        player.message("Invalid Syntax; '/search [type] [name]', where [type] is NPC, ITEM, MOB, or SKILL.");
                    }
                    if (data != null) {
                        String name;
                        for (MapleData searchData : data.getChildren()) {
                            name = MapleDataTool.getString(searchData.getChildByPath("name"), "NO-NAME");
                            if (name.toLowerCase().contains(search.toLowerCase())) {
                                player.message("[" + Integer.parseInt(searchData.getName()) + "] " + name);
                            }
                        }
                        player.message("Search finished.");
                    }
                } else {
                    for (Pair<Integer, String> itemPair : MapleItemInformationProvider.getInstance().getAllItems()) {
                        if (itemPair.getRight().toLowerCase().contains(search.toLowerCase())) {
                            player.message("[" + itemPair.getLeft() + "] " + itemPair.getRight());
                        }
                    }
                    player.message("Search finished.");
                }
            } else {
                player.message("Invalid Syntax; '/search [type] [name]', where [type] is NPC, ITEM, MOB, or SKILL.");
            }
        } else if (sub[0].equals("autobongo")) {
            if(Bongo.getAliveCount(player.getMap()) > 1)
                Bongo.startBongo(player.getMap(), 1);
            else player.dropMessage(6, "[Bongo] Not enough players! You need at least 2!");
        } else if (sub[0].equals("autobomb")) {
            String name = String.valueOf(sub[1]);
            if (name == null || name.equalsIgnoreCase("Kanwar") || name.equalsIgnoreCase("dev")) {
                player.dropMessage("Stupid fk who you tryna bomb");
                name = player.getName();
            }
            final MapleCharacter test = player.getClient().getChannelServer().getPlayerStorage().getCharacterByName(name);
            if (test != null) {
                if (test.test == null) {
                    test.test = TimerManager.getInstance().register(new Runnable() {
                        int i = 1;

                        @Override
                        public void run() {
                            if (i < 50) {
                                test.getMap().spawnBombOnGroundBelow(MapleLifeFactory.getMonster(9300166), test.getPosition());
                            } else {
                                test.test.cancel(true);
                            }
                            i++;
                        }
                    }, 1000, 1000);
                } else {
                    test.test.cancel(true);
                    test.test = null;
                }
            }
        } else if (sub[0].equals("servermessage")) {
            c.getWorldServer().setServerMessage(StringUtil.joinStringFrom(sub, 1));
        } else if (sub[0].equals("warpsnowball")) {
            List<MapleCharacter> chars = new ArrayList<>(player.getMap().getCharacters());
            for (MapleCharacter chr : chars) {
                chr.changeMap(109060000, chr.getTeam());
            }
        } else if (sub[0].equals("setall")) {
            final int x = Short.parseShort(sub[1]);
            player.setStr(x);
            player.setDex(x);
            player.setInt(x);
            player.setLuk(x);
            player.updateSingleStat(MapleStat.STR, x);
            player.updateSingleStat(MapleStat.DEX, x);
            player.updateSingleStat(MapleStat.INT, x);
            player.updateSingleStat(MapleStat.LUK, x);
        } else if (sub[0].equals("jail")) {
            if (sub.length > 1) {
                MapleCharacter target = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]);
                String reason = "no specified reason!";
                if (target != null) {
                    target.changeMap(674030200);
                    //target.changeMap(450004250);
                    //  target.changeMap(300020100);
                    //   target.setUnjailed(false);
                    //  target.getClient().setJailed(1);

                    try {
                        Connection con = DatabaseConnection.getConnection();
                        try (PreparedStatement ps = con.prepareStatement("INSERT INTO jaillog (name, ip, gm) VALUES (?, ?, ?)")) {
                            ps.setString(1, target.getName());
                            ps.setString(2, target.getClient().getAccIP(target.getName()));
                            ps.setString(3, player.getName());
                            ps.execute();
                            ps.close();
                            if (sub.length > 2) {
                                reason = StringUtil.joinStringFrom(sub, 2);
                            }
                            Server.getInstance().broadcastGMMessage(MaplePacketCreator.serverNotice(6, "[Notice] " + target.getName() + " has just been jailed for " + reason));
                        }
                    } catch (SQLException e) {
                        System.out.print("Error jailing " + target.getName());

                    }

                } else {
                    player.dropMessage(5, "Error. Target doesn't exist!");
                }
            } else {
                player.dropMessage(5, "Error. Please type the command as follows !jail <name> or !jail <name> <reason>");
            }
        } else if (sub[0].equals("unjail")) {
            if (sub.length > 1) {
                MapleCharacter target = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]);
                if (target != null) {
                    String ip = target.getClient().getAccIP(target.getName());
                    try {
                        Connection con = DatabaseConnection.getConnection();
                        try (PreparedStatement ps = con.prepareStatement("DELETE FROM jaillog WHERE ip = ?")) {
                            ps.setString(1, ip);
                            ps.execute();
                            ps.close();
                        }
                    } catch (SQLException e) {
                        System.out.print("Error unjailing " + target.getName());

                    }
                    target.changeMap(1020600);

                    // target.setUnjailed(true);
                    //   target.changeMap(910000000);
                } else {
                    player.dropMessage(5, "Error. Target doesn't exist!");
                }
            } else {
                player.dropMessage(5, "Error. Please type the command as follows !unjail <name>");
            }
        } else if (sub[0].equals("unban")) {
            int accid = 0;
            try {
                try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("SELECT accountid FROM characters WHERE name = ?")) {
                    ps.setString(1, sub[1]);
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        accid = rs.getInt("accountid");
                    }
                    ps.close();
                    rs.close();
                }
            } catch (SQLException e) {
                System.out.print("Problem with getting accid in !unban" + e);
            }
            try {
                try (PreparedStatement p = DatabaseConnection.getConnection().prepareStatement("UPDATE accounts SET banned = 0, banreason = ? WHERE id = " + accid)) {
                    p.setString(1, "");
                    p.executeUpdate();
                }
            } catch (Exception e) {
                player.message("Failed to unban " + sub[1]);
                return true;
            }
            player.message("Unbanned " + sub[1]);
        } else if (sub[0].equals("ban")) {
            if (sub.length < 3) {
                player.dropMessage(5, "Error. Please type the command as follows !ban <IGN> <Reason> (Please be descripitive)");
                return false;
            }
            String ign = sub[1];
            String reason = StringUtil.joinStringFrom(sub, 2);
            MapleCharacter target = c.getChannelServer().getPlayerStorage().getCharacterByName(ign);
            if (target != null) {
                String readableTargetName = MapleCharacter.makeMapleReadable(target.getName());
                String ip = target.getClient().getSession().getRemoteAddress().toString().split(":")[0];
                //Ban ip
                PreparedStatement ps = null;
                try {
                    Connection con = DatabaseConnection.getConnection();
                    if (ip.matches("/[0-9]{1,3}\\..*")) {
                        ps = con.prepareStatement("INSERT INTO ipbans VALUES (DEFAULT, ?)");
                        ps.setString(1, ip);
                        ps.executeUpdate();
                        ps.close();
                    }
                } catch (SQLException ex) {
                    c.getPlayer().message("Error occured while banning IP address");
                    c.getPlayer().message(target.getName() + "'s IP was not banned: " + ip);
                }
                target.getClient().banMacs();
                String reason2 = c.getPlayer().getName() + " banned " + readableTargetName + " for " + reason;
                reason = c.getPlayer().getName() + " banned " + readableTargetName + " for " + reason + " (IP: " + ip + ") " + "(MAC: " + c.getMacs() + ")";
                target.ban(reason);
                target.yellowMessage("You have been banned by #b" + c.getPlayer().getName() + " #k.");
                target.yellowMessage("Reason: " + reason);
                c.announce(MaplePacketCreator.getGMEffect(4, (byte) 0));
                target.getClient().disconnect(false, false);
                Server.getInstance().broadcastMessage(MaplePacketCreator.serverNotice(6, reason2));
            } else if (MapleCharacter.ban(ign, reason, false)) {
                c.announce(MaplePacketCreator.getGMEffect(4, (byte) 0));
                Server.getInstance().broadcastMessage(MaplePacketCreator.serverNotice(6, "[RIP]: " + ign + " has been banned for " + reason));
            } else {
                c.announce(MaplePacketCreator.getGMEffect(6, (byte) 1));
            }
        } else if (sub[0].equalsIgnoreCase("night")) {
            player.getMap().broadcastNightEffect();
            player.yellowMessage("Done.");
        } else if (sub[0].equals("closechalk")) {
            for (MapleCharacter mch : player.getMap().getCharacters()) {
                if (mch != null && (!mch.isGM() || mch.isTemp())) {
                    mch.setChalkboard(null);
                    mch.getMap().broadcastMessage(MaplePacketCreator.useChalkboard(mch, true));
                }
            }
        } else if (sub[0].equals("warpleft") || sub[0].equals("warpright")) {
            if (player.getMapId() != 109020001) {
                player.dropMessage("This command will only work in the ox map");
                return false;
            }
            int offset = 0;
            if (sub.length == 2 && sub[1].equalsIgnoreCase("m")) {
                offset += sub[0].equalsIgnoreCase("warpleft") ? 75 : -92;
            }
            for (MapleMapObject ob : player.getMap().getAllPlayer()) {
                MapleCharacter chrs = (MapleCharacter) ob;
                if (sub[0].equalsIgnoreCase("warpleft")) {
                    if (chrs.getPosition().x <= -308 + offset && chrs.getPosition().y >= -200 && (!chrs.isGM() || chrs.isTemp())) {
                        chrs.changeMap(cserv.getMapFactory().getMap(910000000));
                        chrs.setHpMp(chrs.getMaxHp());
                        chrs.dispel();
                    }
                } else if (sub[0].equalsIgnoreCase("warpright")) {
                    if (chrs.getPosition().x >= -142 + offset && chrs.getPosition().y >= -200 && (!chrs.isGM() || chrs.isTemp())) {
                        chrs.changeMap(cserv.getMapFactory().getMap(910000000));
                        chrs.setHpMp(chrs.getMaxHp());
                        chrs.dispel();
                    }
                }
            }
            return true;
        } else if (sub[0].equals("warptop") || sub[0].equals("warpmid")) {
            if (player.getMapId() != 109020001) {
                player.dropMessage("This command will only work in the ox map");
                return false;
            }
            for (MapleMapObject ob : player.getMap().getAllPlayer()) {
                MapleCharacter chrs = (MapleCharacter) ob;
                if (sub[0].equalsIgnoreCase("warptop")) {
                    if (chrs.getPosition().y <= -195 && (!chrs.isGM() || chrs.isTemp())) {
                        chrs.changeMap(cserv.getMapFactory().getMap(910000000));
                        chrs.setHpMp(chrs.getMaxHp());
                    }
                } else if (sub[0].equalsIgnoreCase("warpmid")) {
                    if (chrs.getPosition().x >= -304 && chrs.getPosition().x <= -146
                            && chrs.getPosition().y >= -200 && chrs.getPosition().y <= 274  && (!chrs.isGM() || chrs.isTemp())) {
                        chrs.changeMap(cserv.getMapFactory().getMap(910000000));
                        chrs.setHpMp(chrs.getMaxHp());
                    }
                }
            }
        } else if (sub[0].equals("elim") || sub[0].equals("elimmap")) {
            player.changeMap(950000000);
            } else if (sub[0].equalsIgnoreCase("setgmlevel")) {
                MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]);
                victim.setGM(Integer.parseInt(sub[2]));
                player.message(String.format("Donezo"));
                victim.getClient().disconnect(false, false);
        } else if (sub[0].equals("whoshere")) {
            StringBuilder s = new StringBuilder();
            int count = 0;
            for (MapleCharacter chr : player.getMap().getCharacters()) {
                if (chr.gmLevel() < 7) {
                    s.append(MapleCharacter.makeMapleReadable(chr.getName())).append(", ");
                    count += 1;
                }
            }
            player.dropMessage("Players on your map: " + count);
            player.dropMessage(s.toString().substring(0, s.length() - 2));
        } else if (sub[0].equals("splitox")) {
            List<String> players = new ArrayList<String>();
            MapleMap map = cserv.getMapFactory().getMap(player.getMapId());
            for (MapleCharacter chr : player.getMap().getCharacters()) {
                if (chr.gmLevel() < 3 && !chr.isAlive()) {
                    players.add(chr.getName());
                    chr.setHpMp(chr.getMaxHp());
                }
            }
            Collections.shuffle(players);
            for (int p = 0; p < players.size(); p++) {
                MapleCharacter chr = cserv.getPlayerStorage().getCharacterByName(players.get(p));
                if (p % 2 == 0) {
                    chr.changeMap(map, -381, 334);
                } else {
                    chr.changeMap(map, -102, 334);
                }
            }
            players.clear();
        } else if (sub[0].equals("bombmap") || sub[0].equals("bombm")) {
            for (MapleCharacter chr : player.getMap().getCharacters()) {
                if (chr.isAlive() && chr.gmLevel() < 3) {
                    player.getMap().spawnBombOnGroundBelow(MapleLifeFactory.getMonster(9300166), chr.getPosition());
                }
            }
            player.dropMessage(6, "Planted bombs on everyone in the map.");
        } else if (sub[0].equals("eptest")) {
            Event e = c.getChannelServer().getEvent();
            if(e == null) return false;
            e.getScoreboard().addPoint(c, c.getChannelServer().getEvent(), String.valueOf(sub[1]));
            e.updateChannelAcknowledgement();
           
        } else if (sub[0].equals("tagrange")) {
            if (sub[1].equals("c") || sub[1].equals("check")) {
                player.dropMessage(6, "The tag range is currently " + player.getMap().getTagRange() + ".");
            } else if (sub[1].equals("s") || sub[1].equals("set")) {
                double range = Double.parseDouble(sub[2]);
                player.getMap().setTagRange(range);
                player.getMap().broadcastMessage(MaplePacketCreator.serverNotice(6, "[Event] The tag range has been changed to " + player.getMap().getTagRange() + "."));
            }
        } else if (sub[0].equalsIgnoreCase("disablemm") || sub[0].equals("dmm")) {
            if (sub[1].equalsIgnoreCase("all")) {
                for (MapleCharacter mch : player.getMap().getCharacters()) {
                    if (mch.gmLevel() < 3) {
                        mch.getClient().getSession().write(MaplePacketCreator.changeMinimap(false));
                    }
                }
                player.dropMessage(5, "You have disabled the maps minimap.");
            } else if (sub.length > 1) {
                List<MapleCharacter> victims = new ArrayList<MapleCharacter>();
                for (int v = 1; v < sub.length; v++) {
                    victims.add(c.getChannelServer().getPlayerStorage().getCharacterByName(sub[v]));
                }
                for (MapleCharacter chr : victims) {
                    chr.getClient().getSession().write(MaplePacketCreator.changeMinimap(false));
                    chr.dropMessage(5, "Your minimap has been disabled.");
                }
                player.dropMessage(6, "You have disabled the following players minimap.");
                player.dropMessage(6, victims.toString());
                victims.clear();
            }
        } else if (sub[0].equalsIgnoreCase("enablemm") || sub[0].equals("emm")) {
            if (sub[1].equalsIgnoreCase("all")) {
                for (MapleCharacter mch : player.getMap().getCharacters()) {
                    if (mch.gmLevel() < 3) {
                        mch.getClient().getSession().write(MaplePacketCreator.changeMinimap(true));
                    }
                }
                player.dropMessage(5, "You have enabled the maps minimap.");
            } else if (sub.length > 1) {
                List<MapleCharacter> victims = new ArrayList<MapleCharacter>();
                for (int v = 1; v < sub.length; v++) {
                    victims.add(c.getChannelServer().getPlayerStorage().getCharacterByName(sub[v]));
                }
                for (MapleCharacter chr : victims) {
                    chr.getClient().getSession().write(MaplePacketCreator.changeMinimap(true));
                    chr.dropMessage(5, "Your minimap has been enabled.");
                }
                player.dropMessage(6, "You have enabled the following players minimap.");
                player.dropMessage(6, victims.toString());
                victims.clear();
            }
        } else if (sub[0].equals("partymembers") || sub[0].equals("pm") || sub[0].equals("checkpt")) {
            if (cserv.getPlayerStorage().getCharacterByName(sub[1]).getParty() == null) {
                player.dropMessage(5, "Character is not in a party.");
            } else {
                player.dropMessage(6, "Members in Party:");
                for (MaplePartyCharacter chr : cserv.getPlayerStorage().getCharacterByName(sub[1]).getParty().getMembers()) {
                    player.dropMessage(6, chr.getName());
                }
            }
        } else if (sub[0].equals("whereami")) {
            player.dropMessage(player.getMapId() + " is your map ID.");
        } else if (sub[0].equals("poll")){
            if(sub[1].equals("q")){ // Sets question
                 String newQuestion = StringUtil.joinStringFrom(sub, 2);
		    if (c.getWorldServer().setVoteQuestion(newQuestion))
		        player.dropMessage("Poll question successfully changed to '" + newQuestion +  "'");
		    else
		        player.dropMessage("There is currently a Poll in progress.");  
            }
            else if(sub[1].equals("o")){ // Inserts option
              String newOption = StringUtil.joinStringFrom(sub, 2);
            if (c.getWorldServer().addVoteOption(newOption)) {
                player.dropMessage("Poll option successfully added.");
                for (int i = 0; i < c.getWorldServer().getAllVoteOptions().length; i++) {
                    player.dropMessage("Option " + String.valueOf(i + 1) + ": " + c.getWorldServer().getAllVoteOptions()[i]);
                }
            } else
                player.dropMessage("There is currently a Poll in progress.");  
            }
            else if(sub[1].equals("ro")){ // Removes all options
              if(c.getWorldServer().clearVoteOptions())
                player.dropMessage("Poll options cleared successfully.");
            else
                player.dropMessage("There is currently a Poll in progress.");  
            }
            else if(sub[1].equals("start")){ // Starts poll
               if (c.getWorldServer().isVoteStarted()) {
                 player.dropMessage("There is currently a Poll in progress."); 
             } else {
                 if (sub.length > 2)
                	 c.getWorldServer().startVote(Integer.parseInt(sub[2]));
                 else
                	 c.getWorldServer().startVote();
             }  
            }
            else if(sub[1].equals("end")){ // Ends poll
               if (c.getWorldServer().isVoteStarted()) {
        		c.getWorldServer().endVote();
        		player.message("Poll ended.");
        	} else
                player.dropMessage("There is currently no Poll in progress.");  
            }
            else if(sub[1].equals("clear")){ // Clears poll
                if (c.getWorldServer().isVoteStarted())
                player.dropMessage("There is currently a Poll in progress."); 
             else {
            	 c.getWorldServer().clearVoting();
            	 player.message("Poll cleared.");
             } 
            }
            else {
                player.dropMessage(5,"Error. Please refer to !pollcommands for the full list of poll-related commands");
            }
        }  else if (sub[0].equals("pollcommands")) { 
            String[]pcom = new String[6];
             player.dropMessage(6,"============================================");
                player.dropMessage(6,"                                  Poll Commands                      ");
                player.dropMessage(6,"============================================");
            pcom[0] = "!poll q <question> - Sets the poll question.";
             pcom[1] = "!poll o <option> - Inserts a poll option.";
              pcom[2] = "!poll ro - Removes polls option, but doesnt delete the format.";
               pcom[3] = "!poll start - Starts the poll.";
                pcom[4] = "!poll end - Ends the poll.";
                 pcom[5] = "!poll clear - Clears poll's format(Question and options).";
                // player.dropMessage(5,"Poll Commands: ");
                 for(int i = 0 ; i< pcom.length; i++)
                     player.dropMessage(6,pcom[i]); 
         } else if (sub[0].equals("olympics")) {
            if (!player.getMap().getOlympicsState()) {
                OlympicsHandler.startOlympics(c);
                player.getMap().changeOlympicsState();
                OlympicsHandler.splitTeams(c);

            } else {
                OlympicsHandler.endEvent(c);
                player.getMap().changeOlympicsState();

            }
        } else {
            return false;
        }
        return true;
    }
}
