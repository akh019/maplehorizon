/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client.command;

import client.MapleCharacter;
import client.MapleClient;
import client.MapleDisease;
import client.events.EventCommands;
import client.events.EventHandler2;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import net.server.Server;
import net.server.channel.Channel;
import net.server.world.MaplePartyCharacter;
import scripting.event.EventHandler;
import server.life.MapleLifeFactory;
import server.life.MapleMonster;
import server.life.MobSkillFactory;
import server.maps.MapleMap;
import tools.MaplePacketCreator;
import tools.StringUtil;

/**
 *
 * @author KaiSheng
 */
public class EventGMCommands {
    private static HashMap<String, String> eventcommands = new HashMap<>();
    public static EventHandler2 newsystem = new EventHandler2();
    public static EventHandler event = new EventHandler();
    
    public static boolean executeEventGMCommandLv4(MapleClient c, String[] sub, char heading) throws SQLException { // EventCommands
        MapleCharacter player = c.getPlayer();
        Channel cserv = c.getChannelServer();
    
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
        } else if (sub[0].equalsIgnoreCase("seducemap") || sub[0].equalsIgnoreCase("seducem")) {
            int level = Integer.parseInt(sub[1]);
            for (MapleCharacter victim : player.getMap().getCharacters()) {
                if (victim != null) {
                    victim.setChair(0);
                    victim.getClient().getSession().write(MaplePacketCreator.cancelChair(-1));
                    victim.getMap().broadcastMessage(victim, MaplePacketCreator.showChair(victim.getId(), 0), false);
                    victim.giveDebuff(MapleDisease.SEDUCE, MobSkillFactory.getMobSkill(128, level));
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
            
        } else {
            return false;
        }
        return true;
    }
}