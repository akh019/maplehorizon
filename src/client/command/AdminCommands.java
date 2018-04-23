/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client.command;

import bots.Bernard;
import bots.BernardManager;
import client.MapleCharacter;
import client.MapleClient;
import client.MapleDisease;
import client.MapleStat;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import net.server.Server;
import net.server.channel.Channel;
import net.server.world.World;
import provider.MapleDataProviderFactory;
import scripting.npc.NPCScriptManager;
import server.MapleInventoryManipulator;
import server.TimerManager;
import server.life.MapleLifeFactory;
import server.life.MapleMonster;
import server.life.MapleNPC;
import server.life.MobSkill;
import server.maps.MapleMapItem;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.quest.MapleQuest;
import tools.DatabaseConnection;
import tools.MaplePacketCreator;
import tools.Randomizer;
import tools.StringUtil;

/**
 *
 * @author KaiSheng
 */
public class AdminCommands {
    
    public static boolean executeAdminCommandLv8(MapleClient c, String[] sub, char heading) throws SQLException { // Admin
        MapleCharacter player = c.getPlayer();
        switch (sub[0]) {
            case "sp":  //Changed to support giving sp /a
                if (sub.length == 2) {
                    player.setRemainingSp(Integer.parseInt(sub[1]));
                    player.updateSingleStat(MapleStat.AVAILABLESP, player.getRemainingSp());
                } else {
                    MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]);
                    victim.setRemainingSp(Integer.parseInt(sub[2]));
                    victim.updateSingleStat(MapleStat.AVAILABLESP, player.getRemainingSp());
                }
                break;

//                    case "warp":
//        	 MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]);
//             if (victim != null) { 
//                 if (sub.length == 2) { 
//                     MapleMap target = victim.getMap(); 
//                     player.changeMap(target, target.findClosestSpawnpoint(victim.getPosition())); 
//                 } else { 
//                     MapleMap target = c.getChannelServer().getMapFactory().getMap(Integer.parseInt(sub[2])); 
//                     victim.changeMap(target, target.getPortal(0)); 
//                 } 
//             } else { 
//                 try { 
//                     victim = c.getWorldServer().getPlayerStorage().getCharacterByName(sub[1]); 
//                     if (victim != null) { 
//                    	 player.message("You will be cross-channel warped. This may take a few seconds.");
//                         player.getClient().changeChannel(victim.getClient().getChannel());
//                         MapleMap target = victim.getMap(); 
//                         player.changeMap(target, target.findClosestSpawnpoint(victim.getPosition())); 
//                     } else { 
//                         MapleMap target = c.getChannelServer().getMapFactory().getMap(Integer.parseInt(sub[1])); 
//                         player.changeMap(target, target.getPortal(0)); 
//                     } 
//                 } catch (Exception e) { 
//                 } 
//             } 
//        	break;
            case "fakesmega":
                MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]);
                for (MapleCharacter chrs : c.getWorldServer().getPlayerStorage().getAllCharacters()) {
                    if (chrs.getSmegaView()) {
                        chrs.announce(MaplePacketCreator.serverNotice(3, c.getChannel(), victim.getMedalText() + " " + victim.getName() + " : " + StringUtil.joinStringFrom(sub, 2), true));
                    }
                }
                break;

            case "worldtrip":
                MapleCharacter fag = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]);
                int[] maps = {100000000, 101000000, 102000000, 103000000, 104000000, 200000000, 222000000, 221000000}; // edit maps here
                for (int i = 0; i < maps.length; i++) {
                    fag.changeMap(maps[i]);
                }
                fag.changeMap(100000000);
                fag.dropMessage("Around the world in 10 secounds. I think.");//   
                break;
            case "cleanblist":
                BernardManager.instance.getBernardList().clear();
                break;
            case "mybernard":
                if (player.getBernardid() == 0) {
                    player.dropMessage(5, "You don't own a bernard!");
                } else {
                    player.dropMessage(5, player.getBernardid() + "");
                    // BernardManager.instance.getBernard(player.getBernardid()).getAvatar();
                }
                break;
            case "givebernard":
                if (sub.length > 1) {
                    victim = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]);
                    if (victim != null) {
                        if (player.getBernardid() != 0) {
                            victim.setBernardid(player.getBernardid());
                            player.setBernardid(0);
                        } else {
                            player.dropMessage(5, "You don't own any bernards!");
                        }
                    } else {
                        player.dropMessage(5, "Error. Player doesn't exist");
                    }
                } else {
                    player.dropMessage(5, "Error. Type the command as follows !givebernard <player>");
                }

                break;
            case "bernardlist": {
                for (int i = 0; i < BernardManager.instance.getBernardList().size(); i++) {
                    player.dropMessage(i + 1 + ". " + BernardManager.instance.getBernardList().get(i).getId());
                }
                break;
            }
            case "bernard": {

                /*
				 * final int radius = 300;
				 * 
				 * for (int i = 0; i < 100; i++) {
				 * int theta = Randomizer.next(360);
				 * int r = Randomizer.next(radius);
				 * int x = (int) (Math.cos(theta) * r);
				 * int y = (int) (Math.sin(theta) * r);
				 * 
				 * new Bernard(30000, chr.getMap(), chr.getPosition().x + x, chr.getPosition().y + y);
				 * }
                 */
                int id = 0;

                try {
                    id = Integer.parseInt(sub[1]);
                } catch (Exception e) {
                    break;
                }
                //   MapleNPC bernardnpc = MapleLifeFactory.getNPC(id);

                Bernard bernard = BernardManager.instance.createBernard(id, player.getMap(), player.getPosition().x, player.getPosition().y - 25);
                //      player.getMap().addTempNpc(bernardnpc);

                if (bernard.avatar != null) {
                    player.getMap().addPlayer(bernard.avatar);
                }
                player.getMap().addBernard(bernard.avatar);
                player.setBernardid(id);

                break;
            }
            case "bernardmove": {
                if (sub.length > 1) {
                    if (BernardManager.instance.getBernard(Integer.parseInt(sub[1])) != null) {
                        BernardManager.instance.getBernard(Integer.parseInt(sub[1])).interpolateTo(player.getPosition());
                    } else {
                        player.dropMessage(5, "Error. Bernard doesn't exist");
                    }

                } else {
                    if (player.getBernardid() != 0) {
                        BernardManager.instance.getBernard(player.getBernardid()).interpolateTo(player.getPosition());
                    } else {
                        player.dropMessage(5, "Error. Bernard doesn't exist");
                    }
                }
                break;
            }
            case "bernardfollow":
            case "bernardcont": {
                if (sub.length > 1) {
                    if (Integer.parseInt(sub[1]) != player.getBernardid()) {
                        BernardManager.instance.getBernard(player.getBernardid()).setFollowing(false);
                        player.setBernardid(Integer.parseInt(sub[1]));
                    }

                }
                if (BernardManager.instance.getBernard(player.getBernardid()).isFollowing()) {
                    BernardManager.instance.getBernard(player.getBernardid()).setFollowing(false);
                } else {
                    BernardManager.instance.getBernard(player.getBernardid()).setFollowing(true);
                }
                //BernardManager.instance.getLastBernard().interpolateCont(player);
                break;
            }
            case "bfollow":
            case "bernardgoafter":
                if (sub.length > 1) {
                    MapleCharacter target = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]);
                    if (target != null) {
                        target.setBernardid(player.getBernardid());
                        player.setBernardid(0);
                    } else {
                        player.dropMessage(5, "Error. Player's non-existent!");
                    }
                } else {
                    player.dropMessage(5, "Error. Type the command as follows !bernardgoafter <player>");
                }
                break;
            case "bernardkill": { // Doesn't work yet
                if (sub.length > 1) {
                    MapleCharacter target = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]);
                    if (target != null) {
                        //  BernardManager.instance.getLastBernard().interpolateTo(target.getPosition()); 
                        target.setBernardid(BernardManager.instance.getLastBernard().getId());
                        player.setBernardid(0);
                        target.setHp(0);
                        target.updateSingleStat(MapleStat.HP, 0);
                        player.setBernardid(BernardManager.instance.getLastBernard().getId());
                        target.setBernardid(0);
                    } else {
                        player.dropMessage(5, "Error. Player wasn't found!");
                    }

                } else {
                    player.dropMessage(5, "Error. Type the command as follows !bernardkill <target>");
                }

            }

            /*case "setadminlevel":
            case "setgmlevel":
            case "promote":
                victim = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]);
                victim.setGM(Integer.parseInt(sub[2]));
                player.message(String.format("Successfully set %s to level %s admin.", victim.getName(), sub[2]));
                victim.getClient().disconnect(false, false);
                break;*/

            case "strip":
                victim = c.getWorldServer().getPlayerStorage().getCharacterByName(sub[1]);
                victim.unequipEverything();
                break;
            case "strip2":
                victim = c.getWorldServer().getPlayerStorage().getCharacterByName(sub[1]);
                if (!victim.isGM() || victim.isTemp()) {
                    victim.unequipAndDropEverything();
                } else {
                    player.message("nopenope0");
                }
                break;

            case "pmob":
                int npcId = Integer.parseInt(sub[1]);
                int mobTime = Integer.parseInt(sub[2]);
                int xpos = player.getPosition().x;
                int ypos = player.getPosition().y;
                int fh = player.getMap().getFootholds().findBelow(player.getPosition()).getId();
                if (sub[2] == null) {
                    mobTime = 0;
                }
                MapleMonster mob = MapleLifeFactory.getMonster(npcId);
                if (mob != null && !mob.getName().equals("MISSINGNO")) {
                    mob.setPosition(player.getPosition());
                    mob.setCy(ypos);
                    mob.setRx0(xpos + 50);
                    mob.setRx1(xpos - 50);
                    mob.setFh(fh);
                    try {
                        Connection con = DatabaseConnection.getConnection();
                        PreparedStatement ps = con.prepareStatement("INSERT INTO spawns ( idd, f, fh, cy, rx0, rx1, type, x, y, mid, mobtime ) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )");
                        ps.setInt(1, npcId);
                        ps.setInt(2, 0);
                        ps.setInt(3, fh);
                        ps.setInt(4, ypos);
                        ps.setInt(5, xpos + 50);
                        ps.setInt(6, xpos - 50);
                        ps.setString(7, "m");
                        ps.setInt(8, xpos);
                        ps.setInt(9, ypos);
                        ps.setInt(10, player.getMapId());
                        ps.setInt(11, mobTime);
                        ps.executeUpdate();
                    } catch (SQLException e) {
                        player.dropMessage("Failed to save MOB to the database");
                    }
                    player.getMap().addMonsterSpawn(mob, mobTime, 0);
                } else {
                    player.dropMessage("You have entered an invalid Mob-Id");
                }
                break;
            case "pnpc":
                npcId = Integer.parseInt(sub[1]);
                MapleNPC npcx = MapleLifeFactory.getNPC(npcId);
                xpos = player.getPosition().x;
                ypos = player.getPosition().y;
                fh = player.getMap().getFootholds().findBelow(player.getPosition()).getId();
                if (npcx != null && !npcx.getName().equals("MISSINGNO")) {
                    npcx.setPosition(player.getPosition());
                    npcx.setCy(ypos);
                    npcx.setRx0(xpos + 50);
                    npcx.setRx1(xpos - 50);
                    npcx.setFh(fh);
                    try {
                        Connection con = DatabaseConnection.getConnection();
                        PreparedStatement ps = con.prepareStatement("INSERT INTO spawns ( idd, f, fh, cy, rx0, rx1, type, x, y, mid ) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )");
                        ps.setInt(1, npcId);
                        ps.setInt(2, 0);
                        ps.setInt(3, fh);
                        ps.setInt(4, ypos);
                        ps.setInt(5, xpos + 50);
                        ps.setInt(6, xpos - 50);
                        ps.setString(7, "n");
                        ps.setInt(8, xpos);
                        ps.setInt(9, ypos);
                        ps.setInt(10, player.getMapId());
                        ps.executeUpdate();
                    } catch (SQLException e) {
                        player.dropMessage("Failed to save NPC to the database");
                        System.out.print("Error inserting permanent NPC: " + e);
                    }
                    player.getMap().addMapObject(npcx);
                    player.getMap().broadcastMessage(MaplePacketCreator.spawnNPC(npcx));
                } else {
                    player.dropMessage("You have entered an invalid Npc-Id");
                }
                break;
            /*
        case "setpollquestion":
        case "setpollq":
        case "spollq":
        	String newQuestion = StringUtil.joinStringFrom(sub, 1);
		    if (c.getWorldServer().setVoteQuestion(newQuestion))
		        player.dropMessage("Poll question successfully changed to '" + newQuestion +  "'");
		    else
		        player.dropMessage("There is currently a Poll in progress.");
        	break;
        case "setpolloption":
        case "setpollo":
        case "spollo":
        	String newOption = StringUtil.joinStringFrom(sub, 1);
            if (c.getWorldServer().addVoteOption(newOption)) {
                player.dropMessage("Poll option successfully added.");
                for (int i = 0; i < c.getWorldServer().getAllVoteOptions().length; i++) {
                    player.dropMessage("Option " + String.valueOf(i + 1) + ": " + c.getWorldServer().getAllVoteOptions()[i]);
                }
            } else
                player.dropMessage("There is currently a Poll in progress.");
        	break;
        case "removepolloptions":
        case "removepollo":
        case "rpollo":
            if(c.getWorldServer().clearVoteOptions())
                player.dropMessage("Poll options cleared successfully.");
            else
                player.dropMessage("There is currently a Poll in progress.");
            break;
        case "startpoll":
        case "pollstart":
        	 if (c.getWorldServer().isVoteStarted()) {
                 player.dropMessage("There is currently a Poll in progress."); 
             } else {
                 if (sub.length > 1)
                	 c.getWorldServer().startVote(Integer.parseInt(sub[1]));
                 else
                	 c.getWorldServer().startVote();
             }
        	break;
        case "endpoll":
        case "pollend":
        	if (c.getWorldServer().isVoteStarted()) {
        		c.getWorldServer().endVote();
        		player.message("Poll ended.");
        	} else
                player.dropMessage("There is currently no Poll in progress."); 
        	break;
        case "clearpoll":
        case "pollclear":
        	if (c.getWorldServer().isVoteStarted())
                player.dropMessage("There is currently a Poll in progress."); 
             else {
            	 c.getWorldServer().clearVoting();
            	 player.message("Poll cleared.");
             }
        	break;
             */

            case "horntail":
                player.getMap().spawnMonsterOnGroudBelow(MapleLifeFactory.getMonster(8810026), player.getPosition());
                break;
            case "packet":
                player.getMap().broadcastMessage(MaplePacketCreator.customPacket(StringUtil.joinStringFrom(sub, 1)));
                break;
            case "timerdebug":
                TimerManager tMan = TimerManager.getInstance();
                player.dropMessage(6, "Total Task: " + tMan.getTaskCount() + " Current Task: " + tMan.getQueuedTasks() + " Active Task: " + tMan.getActiveCount() + " Completed Task: " + tMan.getCompletedTaskCount());
                break;
            case "warpworld":
                Server server = Server.getInstance();
                byte worldb = Byte.parseByte(sub[1]);
                if (worldb <= (server.getWorlds().size() - 1)) {
                    try {
                        String[] socket = server.getIP(worldb, c.getChannel()).split(":");
                        c.getWorldServer().removePlayer(player);
                        player.getMap().removePlayer(player);//LOL FORGOT THIS ><                    
                        c.updateLoginState(MapleClient.LOGIN_SERVER_TRANSITION);
                        player.setWorld(worldb);
                        player.saveToDB();//To set the new world :O (true because else 2 player instances are created, one in both worlds)
                        c.announce(MaplePacketCreator.getChannelChange(InetAddress.getByName(socket[0]), Integer.parseInt(socket[1])));
                    } catch (UnknownHostException | NumberFormatException ex) {
                        player.message("Error when trying to change worlds, are you sure the world you are trying to warp to has the same amount of channels?");
                    }

                } else {
                    player.message("Invalid world; highest number available: " + (server.getWorlds().size() - 1));
                }

                break;
            /*case "saveall"://fyi this is a stupid command
                for (World world : Server.getInstance().getWorlds()) {
                    for (MapleCharacter chr : world.getPlayerStorage().getAllCharacters()) {
                        chr.saveToDB();
                    }
                }
                String message = player.getName() + " used !saveall.";
                Server.getInstance().broadcastGMMessage(MaplePacketCreator.serverNotice(5, message));
                player.message("All players saved successfully.");
                break;*/
            case "dcall":
                for (World world : Server.getInstance().getWorlds()) {
                    for (MapleCharacter chr : world.getPlayerStorage().getAllCharacters()) {
                        if (!chr.isGM() || chr.isTemp()) {
                            chr.getClient().disconnect(false, false);
                        }
                    }
                }
                player.message("All players successfully disconnected.");
                break;
            case "mapplayers"://fyi this one is even stupider
                //Adding HP to it, making it less useless.
                String names = "";
                int map = player.getMapId();
                for (World world : Server.getInstance().getWorlds()) {
                    for (MapleCharacter chr : world.getPlayerStorage().getAllCharacters()) {
                        int curMap = chr.getMapId();
                        String hp = Integer.toString(chr.getHp());
                        String maxhp = Integer.toString(chr.getMaxHp());
                        String name = chr.getName() + ": " + hp + "/" + maxhp;
                        if (map == curMap) {
                            names = names.equals("") ? name : (names + ", " + name);
                        }
                    }
                }
                player.message("These b lurkin: " + names);
                break;
            case "getacc":
                if (sub.length < 1) {
                    player.message("Please provide an IGN.");
                    break;
                }
                victim = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]);
                player.message(victim.getName() + "'s account name is " + victim.getClient().getAccountName() + ".");
                break;

            case "snpc":
            case "getnpc":
                NPCScriptManager.getInstance().start(c, Integer.parseInt(sub[1]), MapleLifeFactory.getNPC(Integer.parseInt(sub[1])).getScript(), player);
                break;

            case "getscript":
            case "getnpcscript":
                player.message(MapleLifeFactory.getNPC(Integer.parseInt(sub[1])).getName() + "'s script: " + MapleLifeFactory.getNPC(Integer.parseInt(sub[1])).getScript());
                break;

            case "npc":
                if (sub.length < 1) {
                    break;
                }
                Integer npcid = Integer.parseInt(sub[1]);
                MapleNPC npc = MapleLifeFactory.getNPC(npcid);
                if (npc != null && MapleDataProviderFactory.fileInWZPath("Npc.wz/" + npcid + ".img.xml").exists()) {
                    npc.setPosition(player.getPosition());
                    npc.setCy(player.getPosition().y);
                    npc.setRx0(player.getPosition().x + 50);
                    npc.setRx1(player.getPosition().x - 50);
                    npc.setFh(player.getMap().getFootholds().findBelow(c.getPlayer().getPosition()).getId());
                    player.getMap().addMapObject(npc);
                    player.getMap().addTempNpc(npc);
                    player.getMap().broadcastMessage(MaplePacketCreator.spawnNPC(npc));
                } else {
                    player.dropMessage(6, "The npc isn't existent");
                }
                break;

            /*	case "job": { //Honestly, we should merge this with @job and job yourself if array is 1 long only. I'll do it but gotta run at this point lel
			//Alright, doing that. /a
			if (sub.length == 2) {
				player.changeJob(MapleJob.getById(Integer.parseInt(sub[1])));
				player.equipChanged();
			} else if (sub.length == 3) {
				victim = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]);
				victim.changeJob(MapleJob.getById(Integer.parseInt(sub[2])));
				player.equipChanged();
			} else {
				player.message("!job <job id> <opt: IGN of another person>");
			}
			break;
		} */
            case "playernpc":
                player.playerNPC(c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]), Integer.parseInt(sub[2]));
                break;
            case "shutdown":
            case "shutdownnow":
                int time = 60000;
                if (sub[0].equals("shutdownnow")) {
                    for (World world : Server.getInstance().getWorlds()) {
                    for (MapleCharacter chr : world.getPlayerStorage().getAllCharacters()) {
                        chr.saveToDB();
                    time = 1;
                    } 
                    }
                } else if (sub.length > 1) {
                    time *= Integer.parseInt(sub[1]);
                }
                TimerManager.getInstance().schedule(Server.getInstance().shutdown(false), time);
                break;
            case "face":
                if (sub.length == 2) {
                    player.setFace(Integer.parseInt(sub[1]));
                    player.equipChanged();
                } else {
                    victim = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]);
                    player.setFace(Integer.parseInt(sub[2]));
                    player.equipChanged();
                    player.dropMessage("Face change success, relog or cc to see your results");
                }
                break;
            case "hair":
                if (sub.length == 2) {
                    player.setHair(Integer.parseInt(sub[1]));
                    player.equipChanged();
                } else {
                    victim = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[1]);
                    player.setHair(Integer.parseInt(sub[2]));
                    player.equipChanged();
                    player.dropMessage("Hair change success, relog or cc to see your results");
                }
                break;
            case "itemvac":
                List<MapleMapObject> items = player.getMap().getMapObjectsInRange(player.getPosition(), Double.POSITIVE_INFINITY, Arrays.asList(MapleMapObjectType.ITEM));
                for (MapleMapObject item : items) {
                    MapleMapItem mapitem = (MapleMapItem) item;
                    if (!MapleInventoryManipulator.addFromDrop(c, mapitem.getItem(), true)) {
                        continue;
                    }
                    mapitem.setPickedUp(true);
                    player.getMap().broadcastMessage(MaplePacketCreator.removeItemFromMap(mapitem.getObjectId(), 2, player.getId()), mapitem.getPosition());
                    player.getMap().removeMapObject(item);
                }
                break;

            case "unban":
            case "unblock":
                try {
                    try (PreparedStatement p = DatabaseConnection.getConnection().prepareStatement("UPDATE accounts SET banned = 0 WHERE id = " + MapleCharacter.getAccIdByName(sub[1]))) {
                        p.executeUpdate();
                    }
                } catch (Exception e) {
                    player.message(String.format("Failed to un-block [%s].", sub[1]));
                    return true;
                }
                player.message(String.format("You have succesfully un-blocked [%s].", sub[1]));
                break;

            //plague commands
            case "plague":
                switch (sub[1]) {
                    case "start":
                    case "overtime":
                        final MapleCharacter plague;
                        if (sub.length > 2) {
                            plague = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[2]);
                        } else {
                            Object[] knnccb = player.getMap().getAllPlayer().toArray();
                            plague = (MapleCharacter) knnccb[Randomizer.nextInt(knnccb.length)];
                        }
                        if (plague != null) {
                            plague.getMap().broadcastMessage(MaplePacketCreator.earnTitleMessage("The Plague will be starting in 15 Seconds. Remember to spread out evenly..."));
                            plague.getMap().broadcastMessage(MaplePacketCreator.serverNotice(6, "The Plague will be starting in 15 Seconds. Remember to spread out evenly..."));
                            plague.getMap().broadcastMessage(MaplePacketCreator.getClock(15 + 1));
                            final String[] wot = sub;
                            TimerManager.getInstance().schedule(new Runnable() {
                                @Override
                                public void run() {
                                    plague.getMap().broadcastMessage(MaplePacketCreator.earnTitleMessage(String.format("%s has been chosen as the Plague starter! RUN!!", plague.getName())));
                                    plague.getMap().broadcastMessage(MaplePacketCreator.serverNotice(6, String.format("%s has been chosen as the Plague starter! RUN!!", plague.getName())));
                                    plague.getMap().broadcastMessage(MaplePacketCreator.serverNotice(6, "The Plague has started!"));
                                    plague.giveDebuff(MapleDisease.STUN, new MobSkill(123, 1));
                                    plague.gainPlague(wot[1].equals("overtime"));
                                }
                            }, 15 * 1000);
                        } else {
                            player.dropMessage("Player not found!");
                        }
                        break;
                    case "?":
                        for (MapleCharacter chrs : player.getMap().getCharacters()) {
                            if (chrs.hasPlague()) {
                                player.dropMessage(chrs.getName() + ": " + chrs.getPlagueInfects() + " Plague Points.");
                            }
                        }
                        for (MapleCharacter chrs : player.getMap().getCharacters()) {
                            if (!chrs.hasPlague()) {
                                player.dropMessage(chrs.getName() + ": is alive");
                            }
                        }
                        break;
                    case "giveo":
                    case "give":
                        victim = c.getChannelServer().getPlayerStorage().getCharacterByName(sub[2]);
                        victim.gainPlague(sub[1].contains("o"));
                        break;
                    case "reset":
                        for (MapleCharacter chrs : c.getWorldServer().getPlayerStorage().getAllCharacters()) {
                            if (chrs.hasPlague()) {
                                chrs.resetPlague();
                            }
                        }
                        player.message("The Plague Event has been reset.");
                        break;
                    default:
                        player.message("Operation not supported.");
                        break;
                }
                break;

            case "zakum":
                player.getMap().spawnFakeMonsterOnGroundBelow(MapleLifeFactory.getMonster(8800000), player.getPosition());
                for (int x = 8800003; x < 8800011; x++) {
                    player.getMap().spawnMonsterOnGroudBelow(MapleLifeFactory.getMonster(x), player.getPosition());
                }
                break;
            case "clearquestcache":
                MapleQuest.clearCache();
                player.dropMessage(5, "Quest Cache Cleared.");
                break;
            case "clearquest":
                if (sub.length < 1) {
                    player.dropMessage(5, "Plese include a quest ID.");
                } else {
                    MapleQuest.clearCache(Integer.parseInt(sub[1]));
                    player.dropMessage(5, "Quest Cache for quest " + sub[1] + " cleared.");
                }
                break;
            default:
                player.yellowMessage("Command " + heading + sub[0] + " does not exist.");
                return false;
        }
        return true;
    }
}

