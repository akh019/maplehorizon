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
 * @author Administrator
 */
public class PlayerCommands {

    private static HashMap<String, Integer> gotomaps = new HashMap<>();

    public static Fishing fish = new Fishing();
    public static EventHandler2 event = EventGMCommands.newsystem; // might not work, revisit if it doesnt
    private static String[] tips = {
        "Please only use @gm in emergencies or to report somebody.",
        "To report a bug or make a suggestion, use the forum.",
        "Please do not use @gm to ask if a GM is online.",
        "Do not ask if you can receive help, just state your issue.",
        "Do not say 'I have a bug to report', just state it.",};

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

    static {
        gotomaps.put("gmmap", 180000000);
        gotomaps.put("southperry", 60000);
        gotomaps.put("amherst", 1010000);
        gotomaps.put("henesys", 100000000);
        gotomaps.put("ellinia", 101000000);
        gotomaps.put("perion", 102000000);
        gotomaps.put("kerning", 103000000);
        gotomaps.put("lith", 104000000);
        gotomaps.put("sleepywood", 105040300);
        gotomaps.put("florina", 110000000);
        gotomaps.put("orbis", 200000000);
        gotomaps.put("happy", 209000000);
        gotomaps.put("elnath", 211000000);
        gotomaps.put("ludi", 220000000);
        gotomaps.put("aqua", 230000000);
        gotomaps.put("leafre", 240000000);
        gotomaps.put("mulung", 250000000);
        gotomaps.put("herb", 251000000);
        gotomaps.put("omega", 221000000);
        gotomaps.put("korean", 222000000);
        gotomaps.put("nlc", 600000000);
        gotomaps.put("excavation", 990000000);
        gotomaps.put("pianus", 230040420);
        gotomaps.put("horntail", 240060200);
        gotomaps.put("mushmom", 100000005);
        gotomaps.put("griffey", 240020101);
        gotomaps.put("manon", 240020401);
        gotomaps.put("horseman", 682000001);
        gotomaps.put("balrog", 105090900);
        gotomaps.put("zakum", 211042300);
        gotomaps.put("papu", 220080001);
        gotomaps.put("showa", 801000000);
        gotomaps.put("guild", 200000301);
        gotomaps.put("shrine", 800000000);
        gotomaps.put("skelegon", 240040511);
        gotomaps.put("hpq", 100000200);
        gotomaps.put("ht", 240050400);
        gotomaps.put("fm", 910000000);
        gotomaps.put("hangout", 51);
    }
    static String playertagger = "notagger";
    static boolean tagger = false;
    static boolean tempo = false;

    public static boolean executePlayerCommandLv0(MapleClient c, String[] sub, char heading) throws SQLException {
        MapleCharacter player = c.getPlayer();
        if (heading == '!' && player.gmLevel() == 0) {
            player.yellowMessage("You may not use !" + sub[0] + ", please try /" + sub[0]);
            return false;
        }

        switch (sub[0]) {
            case "commands":
            case "help":
                NPCScriptManager.getInstance().start(c, 1043001, null, player);
                break;
            case "wings":
                NPCScriptManager.getInstance().start(c, 2100000, null, player);
                break;
            
            case "rebirth":
            case "rb":
            case "rebirthe":
            case "rbe":
            case "rebirthc":
            case "rbc":
            case "rebirtha":
            case "rba":
                if (player.getLevel() >= player.getMaxLevel()) {
                    if (sub[0].endsWith("c")) {
                        player.doRebirth((byte) 2);
                        player.getClient().announce(MaplePacketCreator.sendHint("#eYou now have " + c.getPlayer().getRebirths() + " rebirths!", 150, 2));
                    } else if (sub[0].endsWith("a")) {
                        player.doRebirth((byte) 3);
                        player.getClient().announce(MaplePacketCreator.sendHint("#eYou now have " + c.getPlayer().getRebirths() + " rebirths!", 150, 2));
                    } else {
                        player.doRebirth((byte) 1);
                        player.getClient().announce(MaplePacketCreator.sendHint("#eYou now have " + c.getPlayer().getRebirths() + " rebirths!", 150, 2));
                    }
                    if (player.getRebirths() >= 100 && player.getMapId() == 130010110) {
                        player.changeMap(910000000);
                    }
                } else {
                    player.getClient().announce(MaplePacketCreator.sendHint("#eYou must be at least level " + player.getMaxLevel() + " to use this command!", 150, 2));
                }
                break;
            case "home":
                if (!player.inJail()) {
				// check if current map is bossmap
				if (player.getMapId() >= 910000018
						&& player.getMapId() <= 910000022) {
				}
				player.changeMap(1020600);
                                player.getClient().announce(MaplePacketCreator.sendHint("#eWelcome home!", 150, 2));
			} else {
				player.dropMessage("You may not use this command in this map.");
			}
                break;

            case "adv":
                if (player.getRebirths() > 9) {
                    NPCScriptManager.getInstance().start(c, 9000006, null, null);
                } else {
                    String msg = "#e You need 10 rebirths to acces this feature #r";
                }
                break;
            case "job":
                NPCScriptManager.getInstance().start(c, 2003, null, null);
                break;
            /*case "autorebirth":
                if (c.getPlayer().getRebirths() >= 1000) {
                    player.autoRebirth = !player.autoRebirth;
                    player.dropMessage(player.autoRebirth ? "AutoRebirth is now on." : "AutoRebirth is now off.");
                } else {
                    player.dropMessage("You need 1000 rebirths to use this command.");

                }
                break;*/
            case "rank":
                ResultSet rsa = Ranking.getRbrank();
                player.dropMessage(6, "Top 10 most RBed players:");
                int counter = 1;
                while (rsa.next()) {
                    player.dropMessage(6, counter + ". " + rsa.getString("name") + " || " + rsa.getInt("rebirths"));
                    counter++;
                }
                break;
            case "ranking":
                NPCScriptManager.getInstance().start(c, 2040019, null, null);
                break;

            case "time":
                DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
                dateFormat.setTimeZone(TimeZone.getTimeZone("EST"));
                player.yellowMessage("Infinity Server Time (EST): " + dateFormat.format(new Date()));
                break;
            case "vote":
                if (sub.length > 1) {
                    int option = Integer.parseInt(sub[1]);
                    if (c.getWorldServer().isVoteStarted()) {
                        c.getWorldServer().vote(option, player);
                    } else {
                        player.dropMessage(5, "There's no Poll in progress at the moment.");
                    }
                } else {
                    player.dropMessage(5, "Error. Please type the command as follows @vote <option>");
                }
                break;

         /*   case "showwinners":
                String winnertext = "";
                StringBuilder sb2 = new StringBuilder();
                if (event.isRunning()) {
                    for (String key : event.winners.keySet()) {
                        sb2.append(key).append("[").append(event.winners.get(key)).append("]").append(", ");
                    }
                    if (sb2.length() > 0) {
                        sb2.setLength(sb2.length() - 2);
                        player.dropMessage(sb2.toString());

                    } else {
                        player.dropMessage("No current winners.");
                    }
                } else {
                    player.dropMessage("There is currently no events running right now.");
                }

                break; */
            /*case "msg":
                boolean isOn = false;
                String waifu = StringUtil.joinStringFrom(sub, 1);
                if (c.getPlayer().isMarried()) {
                    MapleCharacter wife = c.getChannelServer().getPlayerStorage().getCharacterById(c.getPlayer().getPartnerId());
                    if (wife != null) {
                        wife.getClient().announce(MaplePacketCreator.sendSpouseChat(c.getPlayer(), waifu));
                        c.announce(MaplePacketCreator.sendSpouseChat(c.getPlayer(), waifu));
                    } else {
                        try {
                            for (Channel ch : c.getWorldServer().getChannels()) {
                                if (ch.isConnected(wife.getName())) {
                                    isOn = true;
                                    break;
                                } else {
                                    c.getPlayer().dropMessage("Your spouse is currently not on, or you are not married!");
                                    isOn = false;
                                }
                            }
                            if (isOn) {
                                //c.getChannelServer().getWorldInterface().sendSpouseChat(c.getPlayer().getName(), wife.getName(), msg);
                                c.announce(MaplePacketCreator.sendSpouseChat(c.getPlayer(), waifu
                                ));
                            }

                        } catch (Exception e) {
                            c.getPlayer().message("You are either not married or your spouse is currently offline.");
                        }
                    }
                }
                break;*/
            case "buy":
                player.buyCurrency();
                break;
            case "sell":
                player.sellCurrency();
                break;
            case "sandbox":
            case "sb":
                player.changeMap(300000010);
            case "clearslots":
                if (sub.length == 2) {
                    if (sub[1].equalsIgnoreCase("all")) {
                        player.clearSlots(c, 1);
                        player.clearSlots(c, 2);
                        player.clearSlots(c, 3);
                        player.clearSlots(c, 4);
                        player.clearSlots(c, 5);
                    }
                    if (sub[1].equalsIgnoreCase("equip")) {
                        player.clearSlots(c, 1);
                    }
                    if (sub[1].equalsIgnoreCase("use")) {
                        player.clearSlots(c, 2);
                    }
                    if (sub[1].equalsIgnoreCase("etc")) {
                        player.clearSlots(c, 3);
                    }
                    if (sub[1].equalsIgnoreCase("setup")) {
                        player.clearSlots(c, 4);
                    }
                    if (sub[1].equalsIgnoreCase("cash")) {
                        player.clearSlots(c, 5);
                    }
                    player.dropMessage("Donezo");
                }
                break;
            case "emo":
                player.setHp(0);
                player.updateSingleStat(MapleStat.HP, 0);
                break;
            case "dropinv":
                for (int i = 0; i < player.getInventory(MapleInventoryType.EQUIP).getSlotLimit() + 1; i++) {
                    MapleInventoryManipulator.drop(c, MapleInventoryType.EQUIP, (short) i, (short) 1);
                }
                break;
            case "randlook":
                for (int i = -24; i < 0; i++) {
                    if (i != -11) {
                        if (player.getInventory(MapleInventoryType.EQUIPPED).getItem((short) i) != null) {
                            MapleInventoryManipulator.unequip(c, (short) i, player.getInventory(MapleInventoryType.EQUIP).getNextFreeSlot());
                        }
                    }
                }
                short[] slots = {-9, -7, -2, -1, -49, -13, -5, -6, -8};
                int[] types = {110, 107, 101, 100, 114, 111, 104, 106, 108};
                short chosenrand = 666;

                for (int i = 0; i < slots.length; i++) {
                    chosenrand = player.getRandomslottype(types[i], player);
                    if (chosenrand != 666) {
                        MapleInventoryManipulator.equip(c, chosenrand, slots[i]);
                    }
                }

                //  int[]types =
                // Hats -          
                //   MapleInventoryManipulator.equip(c, , dst)
                break;
            /*case "checkoutput":
                for (int i = -100; i < 100; i++) {
                    if (player.getInventory(MapleInventoryType.EQUIPPED).getItem((short) i) != null) {
                        player.dropMessage("Id : " + player.getInventory(MapleInventoryType.EQUIPPED).getItem((short) i).getItemId() + " ,Slot:" + (short) i);
                    }
                }
                break;*/
            case "rewarp":
            case "rr":
                if (player.JQmap() != 0) {
                    if (player.getMapId() == player.JQmap()) {
                        player.startJQ(player.JQmap());
                    } else {
                        player.dropMessage(5, "You haven't started a JQ yet.");
                    }

                } else {
                    player.dropMessage(5, "You haven't started a JQ yet.");
                }
                break;
            /*case "lastjq":
                if (player.JQmap() != 0) {
                    player.startJQ(player.JQmap());
                } else {
                    player.dropMessage(5, "You haven't played any JQs yet.");
                }
                break;*/
            case "checkafk":
                if (sub.length > 1) {
                    MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]);
                    if (victim != null) {
                        if (victim.isAfk()) {
                            String msg = victim.getName() + " has been AFK for ";
                            Calendar current = Calendar.getInstance();
                            int yourhours = current.get(Calendar.HOUR);
                            int yourminutes = current.get(Calendar.MINUTE);
                            Calendar afk = victim.getAfktime();
                            int afkhours = afk.get(Calendar.HOUR);
                            int afkminutes = afk.get(Calendar.MINUTE);
                            int disthours;
                            int distminutes;

                            if (afkhours > yourhours) {
                                yourhours += 12;
                            }
                            if (afkminutes > yourminutes) {
                                yourminutes += 60;
                                yourhours -= 1;
                            }

                            disthours = yourhours - afkhours;
                            distminutes = yourminutes - afkminutes;

                            if (disthours == 0) {
                                if (distminutes == 1) {
                                    msg += distminutes + " minute.";
                                } else {
                                    msg += distminutes + " minutes.";
                                }

                            } else if (disthours == 1) {
                                if (distminutes == 0) {
                                    msg += disthours + " hour.";
                                } else if (distminutes == 1) {
                                    msg += disthours + " hour and " + distminutes + " minute.";
                                } else {
                                    msg += disthours + " hour and " + distminutes + " minutes.";
                                }
                            } else {
                                if (distminutes == 0) {
                                    msg += disthours + " hours.";
                                } else if (distminutes == 1) {
                                    msg += disthours + " hours and " + distminutes + " minute.";
                                } else {
                                    msg += disthours + " hours and " + distminutes + " minutes.";
                                }
                            }
                            player.dropMessage(6, msg);
                        } else {
                            player.dropMessage(6, victim.getName() + " is not afk!");
                        }
                    } else {
                        player.dropMessage(5, "Error. Player doesn't exist!");
                    }
                } else {
                    player.dropMessage(5, "Error. Please type the command as follows @checkafk <player>");
                }
                break;
            case "whereami":
                player.dropMessage(5, "You're currently in " + player.getMap().getMapName() + " , ID: " + player.getMap().getId());
                break;
            /*case "deathlog":
                player.getMap().deathLog(player);
                break;*/
            /*  case "spref":
                if(sub.length > 1)
                player.setSmegaPrefix(StringUtil.joinStringFrom(sub, 1));
                else
                    player.dropMessage(5,"Error. Type the command as follows !sprefix <text>");
                break; */
 /*  case "savestyle":
                break;
            case "dostyle":
                break; */
            case "s":
            case "smega":
                if (sub.length > 1) {
                    if (!c.isJailed(player.getName()) && !player.isMuted() && !player.isPermmute()) { // Temporary jail map
                        if (player.canSmega()) {
                            String text = StringUtil.joinStringFrom(sub, 1);
                            if (player.getInventory(MapleInventoryType.CASH).findById(5072000) != null || c.getWorldServer().isPlayerTrivia()) {
                                if (!player.getSmegaPrefix().equals("0")) {
                                    Server.getInstance().broadcastMessage(MaplePacketCreator.serverNotice(3, c.getChannel(), player.getMedalText() + player.getName() + " : " + text, true));
                                } // player.announce(MaplePacketCreator.serverNotice(3, c.getChannel(), player.getMedalText() + " " + player.getName() + " : " + StringUtil.joinStringFrom(sub, 1), true));
                                //  player.announce(MaplePacketCreator.serverNotice(3, c.getChannel(), player.getMedalText() + " " + player.getSmegaPrefix() + " " + player.getName() + " : " + StringUtil.joinStringFrom(sub, 1), true));
                                else {
                                    Server.getInstance().broadcastMessage(MaplePacketCreator.serverNotice(3, c.getChannel(), player.getMedalText() + player.getName() + " : " + text, true));
                                }
                                if (!c.getWorldServer().isPlayerTrivia()) // It wont charge smega if theres a trivia going on
                                {
                                    MapleInventoryManipulator.removeById(c, MapleInventoryType.CASH, 5072000, (short) 1, false, true);
                                } else {
                                    if (text.toLowerCase().equals(ServerConstants.playertriviaAnswer) && !player.getName().equals(ServerConstants.playertriviaGuy)) {
                                        Server.getInstance().broadcastMessage(MaplePacketCreator.serverNotice(6, "[Answer] " + player.getName() + " has written down the correct answer which was '" + ServerConstants.playertriviaAnswer + "'. You've won 1 Participation Point."));
                                        player.gainParticipationPoints(1);
                                        ServerConstants.playertriviaAnswer = "";
                                        ServerConstants.playertriviaGuy = "";
                                        c.getWorldServer().setPlayerTrivia(false);
                                    }
                                }

                                player.setSmega(false);
                                player.SmegaCd();
                            } else {
                                player.dropMessage(5, "You don't have any smega.");
                            }
                        } else {
                            player.dropMessage(6, "Please wait 5 seconds between each message.");
                        }
                    } else {
                        player.dropMessage(5, "You're not allowed to smega.");
                    }
                } else {
                    player.dropMessage(5, "Error. Please type out a message.");
                }
                break;
                
            case "t":
                String text = StringUtil.joinStringFrom(sub, 1);
                if (c.getWorldServer().isPlayerTrivia() && text.toLowerCase().equals(ServerConstants.playertriviaAnswer) && !player.getName().equals(ServerConstants.playertriviaGuy)) {
                    Server.getInstance().broadcastMessage(MaplePacketCreator.serverNotice(6, "[Correct!] " + "The answer was: " + ServerConstants.playertriviaAnswer + "! Well done, " + player.getName()));
                    ServerConstants.playertriviaAnswer = "";
                    ServerConstants.playertriviaGuy = "";
                    c.getWorldServer().setPlayerTrivia(false);
                } else {
            player.getClient().announce(MaplePacketCreator.sendHint("#eThere is no trivia going on right now!", 150, 2));
                }
                break;
            case "lastevent":
                event.lastEvent(player);
                break;
            case "spy":
                MapleCharacter p = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]);
                if (p != null) { // player = you, victim or p or w/e = others it wasvictim here too
                    player.dropMessage("Players stats are:");
                    player.dropMessage("Level: " + p.getLevel() + "");
                    player.dropMessage("Fame: " + p.getFame());
                    player.dropMessage("STR: " + p.getStr() + "  ||  DEX: " + p.getDex() + "  ||  INT: " + p.getInt() + "  ||  LUK: " + p.getLuk());
                    player.dropMessage("Player has " + p.getMeso() + " mesos.");
                    player.dropMessage("Event points: " + p.getEventpoints() + " || Participation points: " + p.getParticipationPoints());
                    player.dropMessage("HP: " + p.getHp() + "/" + p.getCurrentMaxHp() + "  ||  MP: " + p.getMp() + "/" + p.getCurrentMaxMp());
                    player.dropMessage("NX Cash: " + +p.getCashShop().getCash(1) + " || JQ Points: " + p.getJqpoints());
                    player.dropMessage("Currency: " + +p.getCurrency() + " || Event Wins: " + p.getErp());
                    player.dropMessage("Rebirths: " + p.getRebirths() + " || Fishing Level: " + fish.fishLevel(p) + " || Fishing Exp: " + p.getFishexp() + "/" + fish.fishLevelCap(p));
                    player.dropMessage("VotePoints: " + p.getClient().getVotePoints() + " || Fishing Points: " + p.getFishpoints());
                } else {
                    player.dropMessage("Player not found."); // ok relaunch
                }
                break;
            case "checkme":
                player.dropMessage("Players stats are:");
                player.dropMessage("Level: " + player.getLevel() + "");
                player.dropMessage("Fame: " + player.getFame());
                player.dropMessage("STR: " + player.getStr() + "  ||  DEX: " + player.getDex() + "  ||  INT: " + player.getInt() + "  ||  LUK: " + player.getLuk());
                player.dropMessage("Player has " + player.getMeso() + " mesos.");
                player.dropMessage("Event points: " + player.getEventpoints() + " || Participation points: " + player.getParticipationPoints());
                player.dropMessage("HP: " + player.getHp() + "/" + player.getCurrentMaxHp() + "  ||  MP: " + player.getMp() + "/" + player.getCurrentMaxMp());
                player.dropMessage("NX Cash: " + player.getCashShop().getCash(1) + " || JQ Points: " + player.getJqpoints());
                player.dropMessage("Currency: " + +player.getCurrency() + " || Event Wins: " + player.getErp());
                player.dropMessage("Rebirths: " + player.getRebirths() + " || Fishing Level: " + fish.fishLevel(player) + " || Fishing Exp: " + player.getFishexp() + "/" + fish.fishLevelCap(player));
                player.dropMessage("VotePoints: " + player.getClient().getVotePoints() + " || Fishing Points: " + player.getFishpoints());
                break;
            case "TRADEep":
                if (sub.length > 2) {
                    MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]);
                    if (victim != null) {
                        if (Integer.parseInt(sub[2]) <= player.getEventpoints()) {
                            victim.addEventpoints(Integer.parseInt(sub[2]));
                            player.addEventpoints(-Integer.parseInt(sub[2]));
                            victim.dropMessage(6, player.getName() + " has given you " + Integer.parseInt(sub[2]) + " ep!");
                            player.dropMessage(6, "You've given " + victim.getName() + " " + Integer.parseInt(sub[2]) + " ep!");
                        } else {
                            player.dropMessage(5, "Error. You don't have enough ep");
                        }
                    } else {
                        player.dropMessage(5, "Who's that? I think you got the wrong person.");
                    }
                } else {
                    player.dropMessage(5, "Error. Please type the command as follows @giveep <name> <amount>");
                }
                break;
            case "save":
                player.saveToDB();
                player.dropMessage(6, "Saved.");
                break;
            /*case "checksolo":
                String soloplayers="";
                for(MapleCharacter a1 : player.getMap().getCharacters())
                    if(a1.getParty() == null)
                        soloplayers+= a1.getName() + ", ";
                if(!soloplayers.equals("")){
                    soloplayers = soloplayers.substring(0,soloplayers.length()-2);
                    soloplayers+= ".";
                            }
                player.dropMessage(5,"Players without a party: ");
                player.dropMessage(5,soloplayers);
                    
                break;*/
            /*case "whosalive":
                String names = "";
                int number = 0;
                for (MapleCharacter a1 : player.getMap().getCharacters()) {
                    if (a1.isAlive()) {
                        names += a1.getName() + ", ";
                        number++;
                    }
                }
                if(!names.equals("")){
                    names = names.substring(0, names.length()-2);
                names+= ".";
                }
                player.dropMessage(6, "There are " + number + " players alive : " + names);
                break;
            case "whosdead":
                String namesdead = "";
                int numberdead = 0;
                for (MapleCharacter a1 : player.getMap().getCharacters()) {
                    if (!a1.isAlive()) {
                        namesdead += a1.getName() + " ,";
                        numberdead++;
                    }
                }
                  if(!namesdead.equals("")){
                    namesdead = namesdead.substring(0, namesdead.length()-2);
                namesdead+= ".";
                  }
                player.dropMessage(6, "There are " + numberdead + " players dead : " + namesdead);
                break;*/
            
            /*case "mobsalive":
                if (player.getMap().mobsAlive(player)) {
                    player.dropMessage("mobs alive");
                } else {
                    player.dropMessage("no mobs");
                }

                break;*/
            case "goafk":
                if (player.getMap().isChalkAllowed()) {                   
                      
                        player.setChalkboard("I'm afk");
                        
                        player.getMap().broadcastMessage(MaplePacketCreator.useChalkboard(player, false));

                    

                } else {
                    player.dropMessage(5, "@chalk has been disabled in this map.");
                }
                break;
            case "eventlog":
                if(sub.length > 1)
                event.eventLog(player, Integer.parseInt(sub[1]));
                else
                event.eventLog(player, 5);
                break;
            case "apreset":
                int ap = 0;
                ap += player.getStr() - 4;
                ap += player.getDex() - 4;
                ap += player.getInt() - 4;
                ap += player.getLuk() - 4;
                player.setStr(4);
                player.setDex(4);
                player.setInt(4);
                player.setLuk(4);
                player.setRemainingAp(player.getRemainingAp() + ap);

                player.updateSingleStat(MapleStat.STR, 4);
                player.updateSingleStat(MapleStat.DEX, 4);
                player.updateSingleStat(MapleStat.INT, 4);
                player.updateSingleStat(MapleStat.LUK, 4);

                player.updateSingleStat(MapleStat.AVAILABLEAP, player.getRemainingAp());
                break;
            case "daily":    
            case "dailyreward":
                NPCScriptManager.getInstance().start(c, 2012024, null, null);
                break;
            case "maxskills":
                for (MapleData skill_ : MapleDataProviderFactory.getDataProvider(new File(System.getProperty("wzpath") + "/" + "String.wz")).getData("Skill.img").getChildren()) {
                    try {
                        Skill skill = SkillFactory.getSkill(Integer.parseInt(skill_.getName()));
                        if (GameConstants.isInJobTree(skill.getId(), player.getJob().getId())) {
                            if (skill.getId() == 21001001) {
                                player.changeSkillLevel(skill, (byte) 0, 0, -1);
                            } else if (skill.getId() == 2311002) {
                                player.changeSkillLevel(skill, (byte) 0, 0, -1);
                            } else if (skill.getId() == 5001005) {
                                player.changeSkillLevel(skill, (byte) 0, 0, -1);
                            } else if (skill.getId() == 2111003) {
                                player.changeSkillLevel(skill, (byte) 0, 0, -1);
                            } else if (skill.getId() == 4121006) {
                                player.changeSkillLevel(skill, (byte) 0, 0, -1);
                            } else {
                                player.changeSkillLevel(skill, (byte) skill.getMaxLevel(), skill.getMaxLevel(), -1);
                            }
                        }
                    } catch (NumberFormatException nfe) {
                        break;
                    } catch (NullPointerException npe) {
                        continue;
                    }
                }
                break;
            case "revamp":
                for (MapleData skill_ : MapleDataProviderFactory.getDataProvider(new File(System.getProperty("wzpath") + "/" + "String.wz")).getData("Skill.img").getChildren()) {
                    try {
                        Skill skill = SkillFactory.getSkill(Integer.parseInt(skill_.getName()));
                        if (GameConstants.isInJobTree(skill.getId(), player.getJob().getId())) {
                            player.changeSkillLevel(skill, (byte) 0, 0, -1);
                        }
                    } catch (NumberFormatException nfe) {
                        break;
                    } catch (NullPointerException npe) {
                        continue;
                    }
                }
                break;
            case "chalk":
            case "c":
                if (player.getMap().isChalkAllowed()) {
                    if (sub.length > 1) {
                        player.setChalkboard("" + StringUtil.joinStringFrom(sub, 1));

                    } else {
                        player.dropMessage(5, "No can do, you have to at least type something");
                    }

                } else {
                    player.dropMessage(5, "@chalk/@c has been disabled in this map.");
                }
                break;

            case "str": // had to combine this bad boy too haha - troxied
            case "dex":
            case "int":
            case "luk":
                int amount = Integer.parseInt(sub[1]);
                boolean str = sub[0].equalsIgnoreCase("str");
                boolean Int = sub[0].equalsIgnoreCase("int");
                boolean luk = sub[0].equalsIgnoreCase("luk");
                boolean dex = sub[0].equalsIgnoreCase("dex");

                int stat = (str ? player.getStr() : (Int ? player.getInt() : (luk ? player.getLuk() : player.getDex())));
                MapleStat maplestat = (str ? MapleStat.STR : (Int ? MapleStat.INT : (luk ? MapleStat.LUK : MapleStat.DEX)));

                if (sub.length == 0 || amount == 0) {
                    if (stat < 32767) {
                        int reduction = Math.min(32767 - stat, player.getRemainingAp());
                        if (str) {
                            player.setStr(stat + reduction);
                        } else if (Int) {
                            player.setInt(stat + reduction);
                        } else if (luk) {
                            player.setLuk(stat + reduction);
                        } else if (dex) {
                            player.setDex(stat + reduction);
                        }
                        player.updateSingleStat(MapleStat.STR, player.getStr());
                        player.updateSingleStat(MapleStat.DEX, player.getDex());
                        player.updateSingleStat(MapleStat.INT, player.getInt());
                        player.updateSingleStat(MapleStat.LUK, player.getLuk());
                        player.setRemainingAp(player.getRemainingAp() - reduction);
                        player.updateSingleStat(MapleStat.AVAILABLEAP, player.getRemainingAp());
                    }

                } else if ((stat + amount <= 32767) || (stat + amount < 4)) {
                    if (amount <= player.getRemainingAp()) {
                        if (str) {
                            player.setStr(stat + amount);
                        } else if (Int) {
                            player.setInt(stat + amount);
                        } else if (luk) {
                            player.setLuk(stat + amount);
                        } else if (dex) {
                            player.setDex(stat + amount);
                        }
                        player.updateSingleStat(MapleStat.STR, player.getStr());
                        player.updateSingleStat(MapleStat.DEX, player.getDex());
                        player.updateSingleStat(MapleStat.INT, player.getInt());
                        player.updateSingleStat(MapleStat.LUK, player.getLuk());
                        player.setRemainingAp(player.getRemainingAp() - amount);
                        player.updateSingleStat(MapleStat.AVAILABLEAP, player.getRemainingAp());
                    }
                }

                break;

            /*case "president":
                if (!player.getName().equals(c.getChannelServer().getPresident())) {
                    if (player.haveItem(4031466, (10 + c.getChannelServer().getPresidentialTakeOver()))) {
                        Server.getInstance().broadcastMessage(c.getWorld(), MaplePacketCreator.serverNotice(6, c.getChannelServer().getPresident().equals("") ? ("[President] " + player.getName() + " has became the President of Channel " + c.getChannel()) : ("[President] " + player.getName() + " has taken over " + c.getChannelServer().getPresident() + " and became the President of Channel " + c.getChannel() + ".")));
                        MapleInventoryManipulator.removeById(c, MapleInventoryType.ETC, 4031466, 10 + c.getChannelServer().getPresidentialTakeOver(), false, false);
                        c.getChannelServer().setPresident(player.getName());
                        c.getChannelServer().setPresidentialTakeOver(c.getChannelServer().getPresidentialTakeOver() + 10);
                        if (player.getGuild() != null && c.getChannelServer().getPresidentialTakeOver() > 0) {
                            player.getGuild().gainGP(Math.round((c.getChannelServer().getPresidentialTakeOver() / (10 / 3))) / 2);
                        }
                    } else {
                        player.dropMessage("You do not have enough Fireflies to do this.");
                    }
                } else {
                    player.dropMessage("You are already president of this channel.");
                }
                break;

            case "presnotice":
                if (player.getName().equals(c.getChannelServer().getPresident())) {
                    Server.getInstance().broadcastMessage(c.getWorld(), MaplePacketCreator.serverNotice(6, "[President Chnl." + c.getChannel() + "] " + player.getName() + ": " + StringUtil.joinStringFrom(sub, 1)));
                } else {
                    player.dropMessage("You are not the President of this Channel.");
                }
                break;
            case "presstepdown":
                if (player.getName().equals(c.getChannelServer().getPresident())) {
                    Server.getInstance().broadcastMessage(c.getWorld(), MaplePacketCreator.serverNotice(6, player.getName() + " has stepped down as President of Channel " + c.getChannel() + "."));
                    c.getChannelServer().setPresident("");
                    c.getChannelServer().setPresidentialTakeOver(0);
                } else {
                    player.dropMessage("You are not the President of this Channel.");
                }

                break;*/
            /*  case "whosalivess":
                     int amountofplayers = 0;
                     StringBuilder sb = new StringBuilder();
                 for (MapleCharacter person : player.getMap().getCharacters())
                 {
                   
                       if (!player.isGM() && player.getHp() > 0)
                       {
                           sb.append(person.getName()).append((", "));
                            amountofplayers++;
                       }
                 }
                     
                     if (sb.toString().length() > 1)
                     {
                         sb.setLength(sb.length() - 2);
                         player.dropMessage("Players alive: " + amountofplayers);
                         player.dropMessage(sb.toString());
                     }
                     else
                     {
                         player.dropMessage("There are no current players alive on the map.");
                     }
                 
                    break; */
            
            case "shop":
                NPCScriptManager.getInstance().start(c, 1092019, null, null);
                break;
            /*case "sprefix":
                NPCScriptManager.getInstance().start(c, 9201052, null, null);
                break;*/
            case "aio":
                NPCScriptManager.getInstance().start(c, 2141013, null, null);
                break;
            /*case "ioc":
            case "bigbad":
                NPCScriptManager.getInstance().start(c, 9000053, null, null);
                break;*/
            case "spinel":
            case "worldtour":
            case "portal":
                NPCScriptManager.getInstance().start(c, 9000020, null, null);
                break;
            case "minigames":
                NPCScriptManager.getInstance().start(c, 1012008, null, null);
                break;
            case "styler":
            case "style":
                /* 
                      if(player.isMale()) {
            NPCScriptManager.getInstance().start(c, 9900000, null, null);
                      }
                    else  {
            NPCScriptManager.getInstance().start(c, 9900001, null, null);
                      } */
                NPCScriptManager.getInstance().start(c, 1012117, null, null);
                break;
            case "jq":
                NPCScriptManager.getInstance().start(c, 1095000, null, null);
                break;

            case "fm": // i just had to remake it LOL - Troxied
                if (sub.length > 1) {
                    if (Integer.parseInt(sub[1]) >= 1 && Integer.parseInt(sub[1]) <= 22) {
                        player.changeMap(910000000 + Integer.parseInt(sub[1]));
                    } else {
                        player.dropMessage("Invalid FM Room");
                    }
                } else {
                    player.changeMap(910000000);
                }
                break;

            case "expfix":
                player.setExp(0);
                player.updateSingleStat(MapleStat.EXP, 0);
                ;
                break;
            case "highfive":
                if (sub.length > 1) {
                    MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]);
                    if (victim != null) {
                        if (player.getMeso() >= 50000000) {
                            player.gainMeso(-50000000, true);
                            victim.gainMeso(50000000, true);
                            player.dropMessage(6, "You've highfived " + victim.getName() + "!");
                            victim.dropMessage(6, player.getName() + " highfived you!");
                        } else {
                            player.dropMessage(5, "Error. You don't have enough money!");
                        }

                    } else {
                        player.dropMessage(5, "Error. The player doesn't exist!");
                    }
                } else {
                    player.dropMessage(5, "Error. Choose a player.");
                }
                break;
            case "rape":
                if (sub.length > 1) {
                    MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]);
                    if (victim != null) {
                        if (player.getMeso() >= 50000000) {
                            player.gainMeso(-50000000, true);
                            victim.gainMeso(50000000, true);
                            player.dropMessage(6, "You've raped " + victim.getName() + "!");
                            victim.dropMessage(6, player.getName() + " raped you!");
                        } else {
                            player.dropMessage(5, "Error. You don't have enough money!");
                        }

                    } else {
                        player.dropMessage(5, "Error. The player doesn't exist!");
                    }
                } else {
                    player.dropMessage(5, "Error. Choose a player.");
                }
                break;
            case "kiss":
                if (sub.length > 1) {
                    MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]);
                    if (victim != null) {
                        if (player.getMeso() >= 50000000) {
                            player.gainMeso(-50000000, true);
                            victim.gainMeso(50000000, true);
                            player.dropMessage(6, "You've kissed " + victim.getName() + "!");
                            victim.dropMessage(6, player.getName() + " kissed you!");
                        } else {
                            player.dropMessage(5, "Error. You don't have enough money!");
                        }

                    } else {
                        player.dropMessage(5, "Error. The player doesn't exist!");
                    }
                } else {
                    player.dropMessage(5, "Error. Choose a player.");
                }
                break;
            case "fuck":
                if (sub.length > 1) {
                    MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]);
                    if (victim != null) {
                        if (player.getMeso() >= 50000000) {
                            player.gainMeso(-50000000, true);
                            victim.gainMeso(50000000, true);
                            player.dropMessage(6, "You've fucked " + victim.getName() + "!");
                            victim.dropMessage(6, player.getName() + " fucked you!");
                        } else {
                            player.dropMessage(6, "Error. You don't have enough money!");
                        }

                    } else {
                        player.dropMessage(6, "Error. The player doesn't exist!");
                    }
                } else {
                    player.dropMessage(6, "Error. Choose a player you'd like to fuck!");
                }
                break;
            case "slap":
                if (sub.length > 1) {
                    MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]);
                    if (victim != null) {
                        if (player.getMeso() >= 50000000) {
                            player.gainMeso(-50000000, true);
                            victim.gainMeso(50000000, true);
                            player.dropMessage(6, "You've slapped " + victim.getName() + "!");
                            victim.dropMessage(6, player.getName() + " slapped you!");
                        } else {
                            player.dropMessage(5, "Error. You don't have enough money!");
                        }

                    } else {
                        player.dropMessage(5, "Error. The player doesn't exist!");
                    }
                } else {
                    player.dropMessage(5, "Error. Choose a player.");
                }
                break;
            case "dickslap":
                if (sub.length > 1) {
                    MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]);
                    if (victim != null) {
                        if (player.getMeso() >= 50000000) {
                            player.gainMeso(-50000000, true);
                            victim.gainMeso(50000000, true);
                            player.dropMessage(6, "You've dickslapped " + victim.getName() + "!");
                            victim.dropMessage(6, player.getName() + " has slapped you with his dick!");
                        } else {
                            player.dropMessage(5, "Error. You don't have enough money!");
                        }

                    } else {
                        player.dropMessage(5, "Error. The player doesn't exist!");
                    }
                } else {
                    player.dropMessage(5, "Error. Choose a player.");
                }
                break;
            case "kick":
                if (sub.length > 1) {
                    MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]);
                    if (victim != null) {
                        if (player.getMeso() >= 50000000) {
                            player.gainMeso(-50000000, true);
                            victim.gainMeso(50000000, true);
                            player.dropMessage(6, "You've kicked " + victim.getName() + "!");
                            victim.dropMessage(6, player.getName() + " kicked you!");
                        } else {
                            player.dropMessage(5, "Error. You don't have enough money!");
                        }

                    } else {
                        player.dropMessage(5, "Error. The player doesn't exist!");
                    }
                } else {
                    player.dropMessage(5, "Error. Choose a player.");
                }
                break;
            case "bite":
                if (sub.length > 1) {
                    MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]);
                    if (victim != null) {
                        if (player.getMeso() >= 50000000) {
                            player.gainMeso(-50000000, true);
                            victim.gainMeso(50000000, true);
                            player.dropMessage(6, "You've bitten " + victim.getName() + "!");
                            victim.dropMessage(6, player.getName() + " has bitten you!");
                        } else {
                            player.dropMessage(5, "Error. You don't have enough money!");
                        }

                    } else {
                        player.dropMessage(5, "Error. The player doesn't exist!");
                    }
                } else {
                    player.dropMessage(5, "Error. Choose a player.");
                }
                break;
            case "punch":
                if (sub.length > 1) {
                    MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]);
                    if (victim != null) {
                        if (player.getMeso() >= 50000000) {
                            player.gainMeso(-50000000, true);
                            victim.gainMeso(50000000, true);
                            player.dropMessage(6, "You've punched " + victim.getName() + "!");
                            victim.dropMessage(6, player.getName() + " punched you!");
                        } else {
                            player.dropMessage(5, "Error. You don't have enough money!");
                        }

                    } else {
                        player.dropMessage(5, "Error. The player doesn't exist!");
                    }
                } else {
                    player.dropMessage(5, "Error. Choose a player.");
                }
                break;
            case "yell":
                if (sub.length > 1) {
                    MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]);
                    if (victim != null) {
                        if (player.getMeso() >= 50000000) {
                            player.gainMeso(-50000000, true);
                            victim.gainMeso(50000000, true);
                            player.dropMessage(6, "You've yelled at " + victim.getName() + "!");
                            victim.dropMessage(6, player.getName() + " yelled at you!");
                        } else {
                            player.dropMessage(5, "Error. You don't have enough money!");
                        }

                    } else {
                        player.dropMessage(5, "Error. The player doesn't exist!");
                    }
                } else {
                    player.dropMessage(5, "Error. Choose a player.");
                }
                break;
            case "hug":
                if (sub.length > 1) {
                    MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]);
                    if (victim != null) {
                        if (player.getMeso() >= 50000000) {
                            player.gainMeso(-50000000, true);
                            victim.gainMeso(50000000, true);
                            player.dropMessage(6, "You gave " + victim.getName() + " a hug!");
                            victim.dropMessage(6, player.getName() + " has given you a hug!");
                        } else {
                            player.dropMessage(5, "Error. You don't have enough money!");
                        }

                    } else {
                        player.dropMessage(5, "Error. The player doesn't exist!");
                    }
                } else {
                    player.dropMessage(5, "Error. Choose a player.");
                }
                break;
            case "poke":
                if (sub.length > 1) {
                    MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]);
                    if (victim != null) {
                        if (player.getMeso() >= 50000000) {
                            player.gainMeso(-50000000, true);
                            victim.gainMeso(50000000, true);
                            player.dropMessage(6, "You've poked " + victim.getName() + "!");
                            victim.dropMessage(6, player.getName() + " poked you!");
                        } else {
                            player.dropMessage(5, "Error. You don't have enough money!");
                        }

                    } else {
                        player.dropMessage(5, "Error. The player doesn't exist!");
                    }
                } else {
                    player.dropMessage(5, "Error. Choose a player you'd like to highfive!");
                }
                break;
            case "choke":
                if (sub.length > 1) {
                    MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]);
                    if (victim != null) {
                        if (player.getMeso() >= 50000000) {
                            player.gainMeso(-50000000, true);
                            victim.gainMeso(50000000, true);
                            player.dropMessage(6, "You've choked " + victim.getName() + "!");
                            victim.dropMessage(6, player.getName() + " choked you!");
                        } else {
                            player.dropMessage(5, "Error. You don't have enough money!");
                        }

                    } else {
                        player.dropMessage(5, "Error. The player doesn't exist!");
                    }
                } else {
                    player.dropMessage(5, "Error. Choose a player.");
                }
                break;
            case "stare":
                if (sub.length > 1) {
                    MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]);
                    if (victim != null) {
                        if (player.getMeso() >= 50000000) {
                            player.gainMeso(-50000000, true);
                            victim.gainMeso(50000000, true);
                            player.dropMessage(6, "You're staring at " + victim.getName() + "!");
                            victim.dropMessage(6, player.getName() + " is staring at you!");
                        } else {
                            player.dropMessage(5, "Error. You don't have enough money!");
                        }

                    } else {
                        player.dropMessage(5, "Error. The player doesn't exist!");
                    }
                } else {
                    player.dropMessage(5, "Error. Choose a player.");
                }
                break;
            case "spank":
                if (sub.length > 1) {
                    MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]);
                    if (victim != null) {
                        if (player.getMeso() >= 50000000) {
                            player.gainMeso(-50000000, true);
                            victim.gainMeso(50000000, true);
                            player.dropMessage(6, "You've spanked " + victim.getName() + "!");
                            victim.dropMessage(6, player.getName() + " spanked you!");
                        } else {
                            player.dropMessage(5, "Error. You don't have enough money!");
                        }

                    } else {
                        player.dropMessage(5, "Error. The player doesn't exist!");
                    }
                } else {
                    player.dropMessage(5, "Error. Choose a player.");
                }
                break;
            case "touch":
                if (sub.length > 1) {
                    MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]);
                    if (victim != null) {
                        if (player.getMeso() >= 50000000) {
                            player.gainMeso(-50000000, true);
                            victim.gainMeso(50000000, true);
                            player.dropMessage(6, "You've touched " + victim.getName() + "!");
                            victim.dropMessage(6, player.getName() + " touched you!");
                        } else {
                            player.dropMessage(5, "Error. You don't have enough money!");
                        }

                    } else {
                        player.dropMessage(5, "Error. The player doesn't exist!");
                    }
                } else {
                    player.dropMessage(5, "Error. Choose a player.");
                }
                break;
            case "harass":
                if (sub.length > 1) {
                    MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]);
                    if (victim != null) {
                        if (player.getMeso() >= 50000000) {
                            player.gainMeso(-50000000, true);
                            victim.gainMeso(50000000, true);
                            player.dropMessage(6, "You've sexually harassed " + victim.getName() + "!");
                            victim.dropMessage(6, player.getName() + " sexually harassed you!");
                        } else {
                            player.dropMessage(5, "Error. You don't have enough money!");
                        }

                    } else {
                        player.dropMessage(5, "Error. The player doesn't exist!");
                    }
                } else {
                    player.dropMessage(5, "Error. Choose a player.");
                }
                break;
            case "cheer":
                if (sub.length > 1) {
                    MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]);
                    if (victim != null) {
                        if (player.getMeso() >= 50000000) {
                            player.gainMeso(-50000000, true);
                            victim.gainMeso(50000000, true);
                            player.dropMessage(6, "You've tried cheering " + victim.getName() + " up!");
                            victim.dropMessage(6, player.getName() + " is trying to cheer you up!");
                        } else {
                            player.dropMessage(5, "Error. You don't have enough money!");
                        }

                    } else {
                        player.dropMessage(5, "Error. The player doesn't exist!");
                    }
                } else {
                    player.dropMessage(5, "Error. Choose a player.");
                }
                break;
            case "spit":
                if (sub.length > 1) {
                    MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]);
                    if (victim != null) {
                        if (player.getMeso() >= 50000000) {
                            player.gainMeso(-50000000, true);
                            victim.gainMeso(50000000, true);
                            player.dropMessage(6, "You've spat at " + victim.getName() + "!");
                            victim.dropMessage(6, player.getName() + " spat at you!");
                        } else {
                            player.dropMessage(5, "Error. You don't have enough money!");
                        }

                    } else {
                        player.dropMessage(5, "Error. The player doesn't exist!");
                    }
                } else {
                    player.dropMessage(5, "Error. Choose a player.");
                }
                break;
            case "social":
                player.dropMessage(6, "============================================");
                player.dropMessage(6, "                                  Social Commands                      ");
                player.dropMessage(6, "============================================");
                player.dropMessage(6, "@highfive | @rape | @kiss | @fuck | @slap");
                player.dropMessage(6, "@kick | @punch | @yell | @hug | @poke");
                player.dropMessage(6, "@choke | @stare | @spank | @touch | @harass");
                player.dropMessage(6, "@cheer | @spit | @dickslap | @bite");
                player.dropMessage(6, "Choose a victim and do with it as you please!");
                break;
            case "tagger":
                if (sub.length > 1) {
                    MapleCharacter tagdude = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]);
                    if (tagdude.getName() != player.getName()) {
                        player.Tagger(player, tagdude);
                    } else {
                        player.dropMessage(5, "Error.. Please enter a name thats different than yours");
                    }
                } else {
                    if (player.didChooseTagger()) { // This isnt even getting true, why? no idea, hmm let me try this then
                        player.setChosenTagger(false);
                        BuddylistEntry[] getsaved = player.getSavedBL();
                        player.getBuddylist().remove(player.getTaggerId());
                        if (getsaved.length > 0) {
                            for (int j = 0; j < getsaved.length; j++) // note to self: u still gotta remove tagger
                            {
                                player.getBuddylist().put(getsaved[j]);
                            }

                        }
                        c.announce(MaplePacketCreator.updateBuddylist(player.getBuddylist().getBuddies()));
                        player.setChosenTagger(false);
                        player.dropMessage(5, "You've turned off tagger-tracking.");
                    } else {
                        player.dropMessage(5, "Error. Please type the command as follows @tagger <name>");
                    }
                }
                break;
            case "taggeroff":
                if (player.didChooseTagger()) { // This isnt even getting true, why? no idea, hmm let me try this then
                    player.setChosenTagger(false);
                    BuddylistEntry[] getsaved = player.getSavedBL();
                    player.getBuddylist().remove(player.getTaggerId());
                    if (getsaved.length > 0) {
                        for (int j = 0; j < getsaved.length; j++) // note to self: u still gotta remove tagger
                        {
                            player.getBuddylist().put(getsaved[j]);
                        }

                    }
                    c.announce(MaplePacketCreator.updateBuddylist(player.getBuddylist().getBuddies()));
                    player.setChosenTagger(false);
                    player.dropMessage(5, "You've turned off tagger-tracking");
                } else {
                    player.dropMessage(5, "You've not chosen a tagger to begin with..");
                }
                break;
            case "bomb":
                if (player.getMap().bombermapOn()) {
                    player.getMap().spawnBombOnGroundBelow(MapleLifeFactory.getMonster(9300166), player.getPosition());
                } else {
                    player.dropMessage(5, "Error. This isn't a bombermap! you're not allowed to spawn any bombs!");
                }
                break;
            case "tag":
                
                /*int istagger = 0;               
               try {
                    Connection con = DatabaseConnection.getConnection();
                    ResultSet rs;
                    //  con.setAutoCommit(false);
                    try (PreparedStatement ps = con.prepareStatement("SELECT tagger FROM characters WHERE name = ?")) {
                        ps.setString(1, player.getName());
                        rs = ps.executeQuery();
                        while (rs.next()) {
                            istagger += rs.getInt("tagger");
                        }
                    }
                } catch (SQLException sqlexc) {
                    System.out.print("Problem with player tag in player commands:" + sqlexc);
                } */
                 List<String>tagsqls = new ArrayList<>();
                 try {
                    Connection con = DatabaseConnection.getConnection();
                    
                    ResultSet rs;
                    //  con.setAutoCommit(false);
                    try (PreparedStatement ps = con.prepareStatement("SELECT name FROM characters WHERE tagger = ?")) {                        
                        ps.setInt(1,1);
                        rs = ps.executeQuery();
                        while (rs.next()) {
                           tagsqls.add(rs.getString("name"));
                        }
                    }
                } catch (SQLException sqlexc) {
                    System.out.print("Problem with player tag in player commands:" + sqlexc);
                }
               
               
             //   if (istagger == 1 || player.getMap().taggermapOn() || player.getName().equals("Iced")) {
                 if (tagsqls.contains(player.getName()) || player.getMap().taggermapOn() || player.getName().equals("Iced")) {
                    MapleMap map = player.getMap();
                    List<MapleMapObject> players = map.getMapObjectsInRange(player.getPosition(), (double) 10000, Arrays.asList(MapleMapObjectType.PLAYER));
                    for (MapleMapObject closeplayers : players) {
				MapleCharacter playernear = (MapleCharacter) closeplayers;
                        if (playernear.isAlive() && playernear != player && !tagsqls.contains(playernear.getName()) && playernear.getGMLevel()) {  // Klaus u can play while i research nblue dot
                            playernear.setHp(0);
                            playernear.updateSingleStat(MapleStat.HP, 0);
                            playernear.dropMessage(6, "You were tagged!");
                            map.broadcastMessage(MaplePacketCreator.serverNotice(6, playernear.getName() + " has been tagged. "));

                        }
                    }
                } else {
                    player.dropMessage(5, "You weren't given privileges to tag.");
                }

                break;
            /*case "triviaq":
            case "tq":
                String q = "";
                if (sub.length > 1) {
                    q = StringUtil.joinStringFrom(sub, 1);
                    player.setTriviaQ(q);
                    player.dropMessage(6, "You've set the trivia question to be: '" + q + "'.");
                } else {
                    player.dropMessage(5, "Error. Please type the command as follows @triviaq/@tq <question>");
                }
                break;
            case "triviaa":
            case "ta":
                String a = "";
                if (sub.length > 1) {
                    a = StringUtil.joinStringFrom(sub, 1).toLowerCase();
                    player.setTriviaA(a);
                    player.dropMessage(6, "You've set the trivia answer to be: '" + a + "'.");
                } else {
                    player.dropMessage(5, "Error. Please type the command as follows @triviaa/@ta <answer>");
                }
                break;
            case "trivia":
                //if(player.isPatron() || player.isGM()) // Only Patrons & GMs can trivia isPatron()
                //  {
                if (player.getParticipationPoints() > 0) {
                    if (!player.getTriviaQ().equals("") && !player.getTriviaA().equals("")) {
                        Server.getInstance().broadcastMessage(MaplePacketCreator.serverNotice(6, "[Trivia] " + player.getName() + " has started a round of trivia!! The player who answers correctly will win 1 Participation Point!"));
                        Server.getInstance().broadcastMessage(MaplePacketCreator.serverNotice(6, "[Question] " + player.getTriviaQ()));
                        ServerConstants.playertriviaAnswer = player.getTriviaA();
                        ServerConstants.playertriviaGuy = player.getName();
                        c.getWorldServer().setPlayerTrivia(true);
                        player.dropMessage(6, "You've just lost 1 participation point.");
                        player.gainParticipationPoints(-1);
                    } else {
                        player.dropMessage(5, "Please use @triviaq/tq to set your question and @triviaa/ta to set the answer before proceeding!");
                    }
                } else {
                    player.dropMessage(5, "Trivia cost is 1PP, you don't meet the requirements.");
                }
                //  }
                //  else
                //   player.dropMessage(5,"You weren't given the privileges to use this command.");

                break;*/
            case "styleinfo":
                player.dropMessage(5, "Face: " + player.getFace());
                player.dropMessage(5, "Hair: " + player.getHair());
                if (!(player.getSkinColor() == null)) {
                    player.dropMessage(5, "Skin: " + player.getSkinColor().getId());
                } else {
                    player.dropMessage(5, "Skin: " + 1);
                }
                break;
            case "savestyle":
                String style = player.getFace() + "" + player.getHair() + "";
                if (player.getSkinColor() == null) {
                    style += "0";
                } else {
                    style += "" + player.getSkinColor().getId();
                }
                player.setStyle(style);
                player.dropMessage(5, "Saved Style.");
                break;
            case "dostyle":
                String savedstyle = "";
                if (!player.getStyle().equals("")) {
                    savedstyle = player.getStyle();
                    player.setFace(Integer.parseInt(savedstyle.substring(0, 5)));
                    player.setHair(Integer.parseInt(savedstyle.substring(5, 10)));
                    MapleSkinColor Skin = MapleSkinColor.getById(Integer.parseInt(savedstyle.charAt(10) + ""));
                    player.setSkinColor(Skin);

                    player.getMap().removePlayer(player);
                    c.getChannelServer().removePlayer(player);
                    c.updateLoginState(MapleClient.LOGIN_SERVER_TRANSITION);

                    int channel = player.getClient().getChannel();
                    c.changeChannel(channel);

                    player.saveToDB();
                } else {
                    player.dropMessage(5, "Error. You don't have any saved styles");
                }
                break;
            case "staff":
            case "team":
                String staffInfo[][] = {
                    {"papi", "Owner"}};
                for (String[] staffMember : staffInfo) {
                    String staffMemberString = staffMember[0]; // so we don't have a ' |' at the end of every line
                    for (int i = 1; i < staffMember.length; i++) {
                        staffMemberString += " | " + staffMember[i];
                    }
                    boolean foundOnline = false;
                    MapleCharacter victim = c.getWorldServer().getPlayerStorage().getCharacterByName(staffMember[0]); // staffMember[0] = IGN
                    if (victim != null) {
                        foundOnline = true;
                    }
                    String status = foundOnline ? "Online" : "Offline";
                    player.message(staffMemberString + " | " + status);
                }

                break;
                case "go":
                if (!player.inJail()) {
				HashMap<String, Integer> maps = new HashMap<String, Integer>();
				maps.put("henesys", 100000000);
				maps.put("ellinia", 101000000);
				maps.put("perion", 102000000);
				maps.put("kerning", 103000000);
				maps.put("lith", 104000000);
				maps.put("sleepywood", 105040300);
				maps.put("florina", 110000000);
				maps.put("orbis", 200000000);
				maps.put("happy", 209000000);
				maps.put("elnath", 211000000);
				// maps.put("ereve", 130000000);
				maps.put("ludi", 220000000);
				maps.put("omega", 221000000);
				maps.put("korean", 222000000);
				maps.put("aqua", 230000000);
				maps.put("leafre", 240000000);
				maps.put("mulung", 250000000);
				maps.put("herb", 251000000);
				maps.put("nlc", 600000000);
				maps.put("shrine", 800000000);
				maps.put("showa", 801000000);
				maps.put("fm", 910000000);
				maps.put("guild", 200000301);
				maps.put("fog", 105040306);
				maps.put("ToT", 270000100);
				if (sub.length != 2) {
					StringBuilder builder = new StringBuilder(
							"Syntax: @goto <mapname>");
					int i = 0;
					for (String mapss : maps.keySet()) {
						if (1 % 10 == 0) {// 10 maps per line
							player.dropMessage(builder.toString());
						} else {
							builder.append(mapss + ", ");
						}
					}
					player.dropMessage(builder.toString());
				} else if (maps.containsKey(sub[1])) {
					int map = maps.get(sub[1]);
					if (map == 910000000) {
						player.saveLocation("FREE_MARKET");
					}
					player.changeMap(map);
				} else {
					player.dropMessage("========================================================================");
					player.dropMessage("                ..::| MapleDivinity's Goto Map Selections |::..                 ");
					player.dropMessage("========================================================================");
					player.dropMessage("| henesys | ellinia | perion | kerning | lith   | sleepywood | florina |");
					player.dropMessage("| fog     | orbis   | happy  | elnath  | ereve  | ludi       | omega   |");
					player.dropMessage("| korean  | aqua    | leafre | mulung  | herb   | nlc        | shrine  |");
					player.dropMessage("| showa  | fm      | guild  | ToT");
				}
				maps.clear();
			} else {
				player.dropMessage("You may not use this command while you are in this map.");
			}

            case "lastrestart":
            case "uptime":
                long milliseconds = System.currentTimeMillis() - Server.uptime;
                int seconds = (int) (milliseconds / 1000) % 60;
                int minutes = (int) ((milliseconds / (1000 * 60)) % 60);
                int hours = (int) ((milliseconds / (1000 * 60 * 60)) % 24);
                int days = (int) ((milliseconds / (1000 * 60 * 60 * 24)));
                player.yellowMessage("MapleHorizon has been online for " + days + " days " + hours + " hours " + minutes + " minutes and " + seconds + " seconds.");
                break;

            case "gacha":
                if (player.gmLevel() == 0) { // Sigh, need it for now...
                    player.yellowMessage("Player Command " + heading + sub[0] + " does not exist, see @help for a list of commands.");
                    return false;
                }
                Gachapon gacha = null;
                String search = StringUtil.joinStringFrom(sub, 1);
                String gachaName = "";
                String[] namess = {"Henesys", "Ellinia", "Perion", "Kerning City", "Sleepywood", "Mushroom Shrine", "Showa Spa Male", "Showa Spa Female", "New Leaf City", "Nautilus Harbor"};
                int[] ids = {9100100, 9100101, 9100102, 9100103, 9100104, 9100105, 9100106, 9100107, 9100109, 9100117};
                for (int j = 0; j < namess.length; j++) {
                    if (search.equalsIgnoreCase(namess[j])) {
                        gachaName = namess[j];
                        gacha = Gachapon.getByNpcId(ids[j]);
                    }
                }
                if (gacha == null) {
                    player.yellowMessage("Please use @gacha <name> where name corresponds to one of the below:");
                    for (String namesss : namess) {
                        player.yellowMessage(namesss);
                    }
                    break;
                }
                String output = "The #b" + gachaName + "#k Gachapon contains the following items.\r\n\r\n";
                for (int j = 0; j < 2; j++) {
                    for (int id : gacha.getItems(j)) {
                        output += "-" + MapleItemInformationProvider.getInstance().getName(id) + "\r\n";
                    }
                }
                output += "\r\nPlease keep in mind that there are items that are in all gachapons and are not listed here.";
                c.announce(MaplePacketCreator.getNPCTalk(9010000, (byte) 0, output, "00 00", (byte) 0));
                break;

            case "whatdropsfrom":
                if (sub.length < 2) {
                    player.dropMessage(5, "Please do @whatdropsfrom <monster name>");
                    break;
                }
                String monsterName = StringUtil.joinStringFrom(sub, 1);
                output = "";
                int limit = 3;
                Iterator<Pair<Integer, String>> listIterator = MapleMonsterInformationProvider.getMobsIDsFromName(monsterName).iterator();
                for (int j = 0; j < limit; j++) {
                    if (listIterator.hasNext()) {
                        Pair<Integer, String> data = listIterator.next();
                        int mobId = data.getLeft();
                        String mobName = data.getRight();
                        output += mobName + " drops the following items:\r\n\r\n";
                        for (MonsterDropEntry drop : MapleMonsterInformationProvider.getInstance().retrieveDrop(mobId)) {
                            try {
                                String name = MapleItemInformationProvider.getInstance().getName(drop.itemId);
                                if (name.equals("null") || drop.chance == 0) {
                                    continue;
                                }
                                float chance = 1000000 / drop.chance / player.getDropRate();
                                output += "- " + name + " (1/" + (int) chance + ")\r\n";
                            } catch (Exception ex) {
                                continue;
                            }
                        }
                        output += "\r\n";
                    }
                }
                c.announce(MaplePacketCreator.getNPCTalk(9010000, (byte) 0, output, "00 00", (byte) 0));
                break;

            case "whodrops":
                if (sub.length < 2) {
                    player.dropMessage(5, "Please do @whodrops <item name>");
                    break;
                }
                String searchString = StringUtil.joinStringFrom(sub, 1);
                output = "";
                listIterator = MapleItemInformationProvider.getInstance().getItemDataByName(searchString).iterator();
                if (listIterator.hasNext()) {
                    int count = 1;
                    while (listIterator.hasNext() && count <= 3) {
                        Pair<Integer, String> data = listIterator.next();
                        output += "#b" + data.getRight() + "#k is dropped by:\r\n";
                        try {
                            PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM drop_data WHERE itemid = ? LIMIT 50");
                            ps.setInt(1, data.getLeft());
                            ResultSet rs = ps.executeQuery();
                            while (rs.next()) {
                                String resultName = MapleMonsterInformationProvider.getMobNameFromID(rs.getInt("dropperid"));
                                if (resultName != null) {
                                    output += resultName + ", ";
                                }
                            }
                            rs.close();
                            ps.close();
                        } catch (Exception e) {
                            player.dropMessage("There was a problem retreiving the required data. Please try again.");
                            e.printStackTrace();
                            return true;
                        }
                        output += "\r\n\r\n";
                        count++;
                    }
                } else {
                    player.dropMessage(5, "The item you searched for doesn't exist.");
                    break;
                }
                c.announce(MaplePacketCreator.getNPCTalk(9010000, (byte) 0, output, "00 00", (byte) 0));
                break;

            case "dispose":
                NPCScriptManager.getInstance().dispose(c);
                c.announce(MaplePacketCreator.enableActions());
                c.removeClickedNPC();
                player.message("You've been disposed.");
                break;

            /*case "rates":
                c.resetVoteTime();
                player.setRates();
                player.yellowMessage("DROP RATE");
                player.message(">>Total DROP Rate: " + player.getDropRate() + "x");

                player.yellowMessage("MESO RATE");
                player.message(">>Base MESO Rate: " + c.getWorldServer().getMesoRate() + "x");
                player.message(">>Guild MESO Rate bonus: " + (player.getGuild() != null ? "1" : "0") + "x");
                player.message(">>Total MESO Rate: " + player.getMesoRate() + "x");

                player.yellowMessage("EXP RATE");
                player.message(">>Base Server EXP Rate: " + ServerConstants.EXP_RATE + "x");
                if (c.getWorldServer().getExpRate() > ServerConstants.EXP_RATE) {
                    player.message(">>Event EXP bonus: " + (c.getWorldServer().getExpRate() - ServerConstants.EXP_RATE) + "x");
                }
                player.message(">>Voted EXP bonus: " + (c.hasVotedAlready() ? "1x" : "0x (If you vote now, you will earn an additional 1x EXP!)"));
                player.message(">>Total EXP Rate: " + player.getExpRate() + "x");

                if (player.getLevel() < 10) {
                    player.message("Players under level 10 always have 1x exp.");
                }
                break;*/

            /*case "online2":
              String text = "Online Players : ";           
            String cc1 = "Players in Channel 1: ", cc2 = "Players in Channel 2: ", cc3 = "Players in Channel 3: ";
            for (MapleCharacter chrs : c.getWorldServer().getPlayerStorage().getAllCharacters()) {
                if(!chrs.isGM() || chrs.isTemp()){
                switch (chrs.getClient().getChannel()) {
                    case 1:
                        cc1 += chrs.getName() + ", ";
                       
                        break;
                    case 2:
                        cc2 += chrs.getName() + ", ";
                       
                        break;
                    case 3:
                        cc3 += chrs.getName() + ", ";
                       
                        break;
                }
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

            player.dropMessage(6, text);
            player.dropMessage(6, cc1);
            player.dropMessage(6, cc2);
            player.dropMessage(6, cc3);
                break; */
            case "online":
                int allplayers = 0;
                for (MapleCharacter chrs : c.getWorldServer().getPlayerStorage().getAllCharacters()) {
                   
                    switch (chrs.getClient().getChannel()) {
                        case 1:
                            allplayers++;
                            break;
                        case 2:
                            allplayers++;
                            break;
                        case 3:
                            allplayers++;
                            break;
                    }
                }
               if(allplayers == 1)
                   player.getClient().announce(MaplePacketCreator.sendHint("#e[MapleHorizon] There is " + allplayers + " player online.#k", 150, 2));
               else    
                player.getClient().announce(MaplePacketCreator.sendHint("#e[MapleHorizon] There is " + allplayers + " player online.#k", 150, 2));
                break;
            case "callgm":
            case "gm":
                if (!player.isCommandcooldown(300)) {
                    if (sub.length < 3) { // #goodbye 'hi'
                        player.dropMessage(5, "Your message was too short. Please provide as much detail as possible.");

                    }
                    String message = StringUtil.joinStringFrom(sub, 1);
                    Server.getInstance().broadcastGMMessage(MaplePacketCreator.sendYellowTip("[GM MESSAGE]:" + MapleCharacter.makeMapleReadable(player.getName()) + ": " + message));
                    Server.getInstance().broadcastGMMessage(MaplePacketCreator.serverNotice(1, message));
                    FilePrinter.printError("gm.txt", MapleCharacter.makeMapleReadable(player.getName()) + ": " + message + "\r\n");
                    player.dropMessage(5, "Your message '" + message + "' was sent to the Staff.");
                    player.dropMessage(5, tips[Randomizer.nextInt(tips.length)]);
                    player.setCommandcooldown(true);
                    player.cooldownCommand(300); // 5 minutes

                } else {
                    player.dropMessage(6, "Please wait 5 minutes before using @gm again!");
                }
                break;
            /* case "bug":
			if (sub.length < 2) {
				player.dropMessage(5, "Message too short and not sent. Please do @bug <bug>");
				break;
			}
			String message = joinStringFrom(sub, 1);
			Server.getInstance().broadcastGMMessage(MaplePacketCreator.sendYellowTip("[BUG]:" + MapleCharacter.makeMapleReadable(player.getName()) + ": " + message));
			Server.getInstance().broadcastGMMessage(MaplePacketCreator.serverNotice(1, message));
			FilePrinter.printError("bug.txt", MapleCharacter.makeMapleReadable(player.getName()) + ": " + message + "\r\n");
			player.dropMessage(5, "Your bug '" + message + "' was submitted successfully to our developers. Thank you!");
			break; */
            case "lastvote":
                if (c.hasVotedAlready()) {
                    Date currentDate = new Date();
                    int time = (int) ((int) 86400 - ((currentDate.getTime() / 1000) - c.getVoteTime())); //ugly as fuck
                    hours = time / 3600;
                    minutes = time % 3600 / 60;
                    seconds = time % 3600 % 60;
                    player.yellowMessage("You have already voted. You can vote again in " + hours + " hours, " + minutes + " minutes, " + seconds + " seconds.");
                } else {
                    player.yellowMessage("You are free to vote! Make sure to vote to gain a vote point!");
                }
                break;
            case "resscd":
                int timepassed = player.getResstime();
                int mpass = 0,
                 spass = 0;
                if (timepassed == 300) {
                    player.dropMessage(6, "You can use your ress!");
                } else {
                    mpass = (300 - timepassed) / 60;
                    spass = 300 - timepassed - (mpass * 60);
                    switch (mpass) {
                        case 0:
                            player.dropMessage(5, "You can use your ress in " + spass + " seconds.");
                            break;
                        case 1:
                            player.dropMessage(5, "You can use your ress in 1 minute and " + spass + " seconds.");
                            break;
                        case 2:
                            player.dropMessage(5, "You can use your ress in 2 minutes and " + spass + " seconds.");
                            break;
                        case 3:
                            player.dropMessage(5, "You can use your ress in 3 minutes and " + spass + " seconds.");
                            break;
                        case 4:
                            player.dropMessage(5, "You can use your ress in 4 minutes and " + spass + " seconds.");
                            break;
                    }
                    //  player.dropMessage(5,"You can use your ress in " + (300 - timepassed) + " seconds.");
                }
                break;
            case "press":
                int timepassedp = 0;
                int mpassp = 0,
                 spassp = 0;
                if (player.getParty() != null) {
                    for (MaplePartyCharacter pm : player.getParty().getMembers()) {
                        if (pm.getPlayer() != player) {
                            timepassedp = pm.getPlayer().getResstime();
                            if (timepassedp == 300) {
                                player.dropMessage(6, pm.getPlayer() + " can use their ress!");
                            } else {
                                mpassp = (300 - timepassedp) / 60;
                                spassp = 300 - timepassedp - (mpassp * 60);
                                switch (mpassp) {
                                    case 0:
                                        player.dropMessage(5, pm.getPlayer() + " can use their ress in " + spassp + " seconds.");
                                        break;
                                    case 1:
                                        player.dropMessage(5, pm.getPlayer() + " can use their ress in 1 minute and " + spassp + " seconds.");
                                        break;
                                    case 2:
                                        player.dropMessage(5, pm.getPlayer() + " can use their ress in 2 minutes and " + spassp + " seconds.");
                                        break;
                                    case 3:
                                        player.dropMessage(5, pm.getPlayer() + " can use their ress in 3 minutes and " + spassp + " seconds.");
                                        break;
                                    case 4:
                                        player.dropMessage(5, pm.getPlayer() + " can use their ress in 4 minutes and " + spassp + " seconds.");
                                        break;
                                }
                                // player.dropMessage(5,pm.getPlayer() +" can use their ress in " + (300 - timepassedp) + " seconds.");
                            }
                        }
                    }

                } else {
                    player.dropMessage(5, "You're not in a party!");
                }
                break; 
            case "jt":
            case "jointag": /*
                // Point pos = new Point(361,-206);
                if (!event.isEventBanned(c.getPlayer().getName())) {
                    if (event.isRunning()) {
                        if (player.getClient().getChannel() == event.channelOn) {
                            if (event.jt) {
                                player.changeMap(event.eventMap);
                                player.setHpMp(0);

                            } else {
                                player.dropMessage("@jointag is not allowed at this moment.");
                            }
                        } else {
                            player.dropMessage("You are not on the channel that the event is being hosted on!");
                        }
                    } else {
                        player.dropMessage("There is no event currently running right now.");
                    }
                } else {
                    player.dropMessage("You are banned from using @joinevent. Please message a GM.");
                } */
                if(event.e.canJt())
                EventCommands.playerJoin(c);
                else
                    player.dropMessage(5,"Event gates aren't open at the current moment. Try again later.");
                break; 
            case "j":
            case "join":
            case "joinevent":
                /*if (!event.isEventBanned(c.getPlayer().getName())) {
                    if (event.isRunning()) {
                        if (player.getClient().getChannel() == event.channelOn) {
                            if (event.isOpen) {
                                player.changeMap(event.eventMap);
                                if (event.tk && !player.isGM()) 
                                    player.setHpMp(0);
                                else
                                    player.setHpMp(30000);
                                
                            } else {
                                player.dropMessage("The event gates are currently closed.");
                            }
                        } else {
                            player.dropMessage("You are not on the channel that the event is being hosted on!");
                        }
                    } else {
                        player.dropMessage("There is no event currently running right now.");
                    }
                } else {
                    player.dropMessage("You are banned from using @joinevent. Please message a GM.");
                }
                break;
            case "viewwinners":
            case "whowon":
            case "vw":
                int place = 1;
                if(event.isRunning()){
                  if(!event.winners.isEmpty()){
                      player.dropMessage(5,"Current Event Winners: ");
                     for(String key : event.winners.keySet()){
                         player.dropMessage(5,place + ". " + key + " - " + event.winners.get(key));
                         place++;
                     }
                     
                                  
                }
                          
                         
                  }  
                
                else
                     player.dropMessage("There is no event currently running right now.");
                 */
                EventCommands.playerJoin(c);
                break;
            case "grank":
                NPCScriptManager.getInstance().start(c, 9040004, null, null);
                break;
            case "clearinv":
                if (sub.length > 1) { // (short)player.getInventory(MapleInventoryType.USE).getItem((short) i).getQuantity()
                    switch (sub[1]) {
                        case "equip":
                            for (int i = 0; i < player.getInventory(MapleInventoryType.EQUIP).getSlotLimit(); i++) {
                                if (player.getInventory(MapleInventoryType.EQUIP).getItem((short) i) != null) {
                                    MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.EQUIP, (short) i, (short) 1, false);
                                }
                            }
                            break;
                        case "use":
                            for (int i = 0; i < player.getInventory(MapleInventoryType.USE).getSlotLimit(); i++) {
                                if (player.getInventory(MapleInventoryType.USE).getItem((short) i) != null) {
                                    MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, (short) i, (short) player.getInventory(MapleInventoryType.USE).getItem((short) i).getQuantity(), false);
                                }
                            }
                            break;
                        case "setup":
                            for (int i = 0; i < player.getInventory(MapleInventoryType.SETUP).getSlotLimit(); i++) {
                                if (player.getInventory(MapleInventoryType.SETUP).getItem((short) i) != null) {
                                    MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.SETUP, (short) i, (short) player.getInventory(MapleInventoryType.SETUP).getItem((short) i).getQuantity(), false);
                                }
                            }
                            break;
                        case "etc":
                            for (int i = 0; i < player.getInventory(MapleInventoryType.ETC).getSlotLimit(); i++) {
                                if (player.getInventory(MapleInventoryType.ETC).getItem((short) i) != null) {
                                    MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.ETC, (short) i, (short) player.getInventory(MapleInventoryType.ETC).getItem((short) i).getQuantity(), false);
                                }
                            }
                            break;
                        case "cash":
                            for (int i = 0; i < player.getInventory(MapleInventoryType.CASH).getSlotLimit(); i++) {
                                if (player.getInventory(MapleInventoryType.CASH).getItem((short) i) != null) {
                                    MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.CASH, (short) i, (short) player.getInventory(MapleInventoryType.CASH).getItem((short) i).getQuantity(), false);
                                }
                            }
                            break;
                        default:
                            player.dropMessage("Error. Please enter a valid type : equip, use, setup, etc or cash.");
                            break;
                    }
                } else {
                    player.dropMessage(5, "Error. Please type the command as follows @clearinv <type> There are 5 types: equip, use, setup, etc and cash.");
                }
                break;
            case "bosshp":
                for (MapleMonster monster : player.getMap().getMonsters()) {
                    if (monster != null && monster.isBoss() && monster.getHp() > 0) {
                        long percent = monster.getHp() * 100L / monster.getMaxHp();
                        String bar = "[";
                        for (int j = 0; j < 100; j++) {
                            bar += j < percent ? "|" : ".";
                        }
                        bar += "]";
                        player.yellowMessage(monster.getName() + " has " + percent + "% HP left.");
                        player.yellowMessage("HP: " + bar);
                    }
                }
                break;
            case "ranks":
                PreparedStatement ps = null;
                ResultSet rs = null;
                try {
                    ps = DatabaseConnection.getConnection().prepareStatement("SELECT `characters`.`name`, `characters`.`level` FROM `characters` LEFT JOIN accounts ON accounts.id = characters.accountid WHERE `characters`.`gm` = '0' AND `accounts`.`banned` = '0' ORDER BY level DESC, exp DESC LIMIT 50");
                    rs = ps.executeQuery();

                    player.announce(MaplePacketCreator.showPlayerRanks(9010000, rs));
                    ps.close();
                    rs.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                } finally {
                    try {
                        if (ps != null && !ps.isClosed()) {
                            ps.close();
                        }
                        if (rs != null && !rs.isClosed()) {
                            rs.close();
                        }
                    } catch (SQLException e) {
                    }
                }
                break;
            default:

                player.dropMessage(5, "Player Command " + heading + sub[0] + " does not exist, see @help for a list of commands.");

                return false;
        }
        return true;
    }
    static boolean tempplayer = false; // Makes gms vulnerable to tag      
    static String eventstarter = "notagger";
}
