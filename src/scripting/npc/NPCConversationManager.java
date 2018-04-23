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
package scripting.npc;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.server.Server;
import net.server.guild.MapleAlliance;
import net.server.guild.MapleGuild;
import net.server.world.MapleParty;
import net.server.world.MaplePartyCharacter;
import provider.MapleData;
import provider.MapleDataProviderFactory;
import scripting.AbstractPlayerInteraction;
import server.MapleItemInformationProvider;
import server.MapleStatEffect;
import server.events.gm.MapleEvent;
import server.gachapon.MapleGachapon;
import server.gachapon.MapleGachapon.MapleGachaponItem;
import server.maps.MapleMap;
import server.maps.MapleMapFactory;
import server.partyquest.Pyramid;
import server.partyquest.Pyramid.PyramidMode;
import server.quest.MapleQuest;
import tools.LogHelper;
import tools.DatabaseConnection;
import tools.FilePrinter;
import tools.MaplePacketCreator;
import client.MapleCharacter;
import client.MapleClient;
import client.MapleJob;
import client.MapleSkinColor;
import client.MapleStat;
import client.Skill;
import client.SkillFactory;
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.ItemFactory;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import client.inventory.MaplePet;
import constants.ExpTable;
import tools.Pair;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import scripting.event.Fishing;
import scripting.event.Ranking;
import server.MapleInventoryManipulator;
import server.MapleShopFactory;
import server.life.MapleLifeFactory;
import constants.GameConstants;
import constants.ServerConstants;
import java.awt.Point;
import java.rmi.RemoteException;
import java.util.Arrays;
import server.life.MapleMonster;
import server.life.MapleMonsterStats;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;

/**
 *
 * @author Matze
 */
public class NPCConversationManager extends AbstractPlayerInteraction {

	private int npc;
	private String script;
	private String getText;
        private int getNumber;
        private String roundstarter = "";
        public static Fishing fish = new Fishing();

	public NPCConversationManager(MapleClient c, int npc, String script) {
        super(c);
        this.npc = npc;
        this.script = script;
    }
    
    public String getScript() {
    	return script;
    }
    
    public void setScript(String name) {
    	this.script = name;
    }
    
    public int getNpc() {
        return npc;
    }
        public void openShop(int id) { 
         MapleShopFactory.getInstance().getShop(id).sendShop(getClient()); 
}  

	public void dispose() {
		NPCScriptManager.getInstance().dispose(this);
	}

	public void sendNext(String text) {
		getClient().announce(MaplePacketCreator.getNPCTalk(npc, (byte) 0, text, "00 01", (byte) 0));
	}

	public void sendPrev(String text) {
		getClient().announce(MaplePacketCreator.getNPCTalk(npc, (byte) 0, text, "01 00", (byte) 0));
	}

	public void sendNextPrev(String text) {
		getClient().announce(MaplePacketCreator.getNPCTalk(npc, (byte) 0, text, "01 01", (byte) 0));
	}

	public void sendOk(String text) {
		getClient().announce(MaplePacketCreator.getNPCTalk(npc, (byte) 0, text, "00 00", (byte) 0));
	}

	public void sendYesNo(String text) {
		getClient().announce(MaplePacketCreator.getNPCTalk(npc, (byte) 1, text, "", (byte) 0));
	}

	public void sendAcceptDecline(String text) {
		getClient().announce(MaplePacketCreator.getNPCTalk(npc, (byte) 0x0C, text, "", (byte) 0));
	}

	public void sendSimple(String text) {
		getClient().announce(MaplePacketCreator.getNPCTalk(npc, (byte) 4, text, "", (byte) 0));
	}

	public void sendNext(String text, byte speaker) {
		getClient().announce(MaplePacketCreator.getNPCTalk(npc, (byte) 0, text, "00 01", speaker));
	}

	public void sendPrev(String text, byte speaker) {
		getClient().announce(MaplePacketCreator.getNPCTalk(npc, (byte) 0, text, "01 00", speaker));
	}

	public void sendNextPrev(String text, byte speaker) {
		getClient().announce(MaplePacketCreator.getNPCTalk(npc, (byte) 0, text, "01 01", speaker));
	}

	public void sendOk(String text, byte speaker) {
		getClient().announce(MaplePacketCreator.getNPCTalk(npc, (byte) 0, text, "00 00", speaker));
	}

	public void sendYesNo(String text, byte speaker) {
		getClient().announce(MaplePacketCreator.getNPCTalk(npc, (byte) 1, text, "", speaker));
	}

	public void sendAcceptDecline(String text, byte speaker) {
		getClient().announce(MaplePacketCreator.getNPCTalk(npc, (byte) 0x0C, text, "", speaker));
	}

	public void sendSimple(String text, byte speaker) {
		getClient().announce(MaplePacketCreator.getNPCTalk(npc, (byte) 4, text, "", speaker));
	}

	public void sendStyle(String text, int styles[]) {
		getClient().announce(MaplePacketCreator.getNPCTalkStyle(npc, text, styles));
	}
        
        public void sendSpinel(String text, int spinel[]) {
		getClient().announce(MaplePacketCreator.getNPCTalkSpinel(npc, text, spinel));
	}

	public void sendGetNumber(String text, int def, int min, int max) {
		getClient().announce(MaplePacketCreator.getNPCTalkNum(npc, text, def, min, max));
              //  getPlayer().dropMessage(6,text+"");
                
	}
        
	public int getNumber() {
		return this.getNumber;
	}

	public void sendGetText(String text) {
		getClient().announce(MaplePacketCreator.getNPCTalkText(npc, text, ""));
	}
        

	/*
	 * 0 = ariant colliseum
	 * 1 = Dojo
	 * 2 = Carnival 1
	 * 3 = Carnival 2
	 * 4 = Ghost Ship PQ?
	 * 5 = Pyramid PQ
	 * 6 = Kerning Subway
	 */
	public void sendDimensionalMirror(String text) {
		getClient().announce(MaplePacketCreator.getDimensionalMirror(text));
	}

	public void setGetText(String text) {
		this.getText = text;
	}

	public String getText() {
		return this.getText;
	}

	public int getJobId() {
		return getPlayer().getJob().getId();
	}

	public MapleJob getJob(){
		return getPlayer().getJob();
	}

	public void startQuest(short id) {
		try {
			MapleQuest.getInstance(id).forceStart(getPlayer(), npc);
		} catch (NullPointerException ex) {
		}
	}

	public void completeQuest(short id) {
		try {
			MapleQuest.getInstance(id).forceComplete(getPlayer(), npc);
		} catch (NullPointerException ex) {
		}
	}

	public void startQuest(int id) {
		try {
			MapleQuest.getInstance(id).forceStart(getPlayer(), npc);
		} catch (NullPointerException ex) {
		}
	}
	public void completeQuest(int id) {
		try {
			MapleQuest.getInstance(id).forceComplete(getPlayer(), npc);
		} catch (NullPointerException ex) {
		}
	}
        

	public int getMeso() {
		return getPlayer().getMeso();
	}

	public void gainMeso(int gain) {
		if (gain > 0){
			FilePrinter.printError(FilePrinter.EXPLOITS + c.getPlayer().getName() + ".txt", c.getPlayer().getName() + " gained " + gain + " mesos from NPC " + npc + "\r\n");
		}
		getPlayer().gainMeso(gain, true, false, true);
	}

	public void gainExp(int gain) {
		getPlayer().gainExp(gain, true, true);
	}

	public int getLevel() {
		return getPlayer().getLevel();
	}

	public void showEffect(String effect) {
		getPlayer().getMap().broadcastMessage(MaplePacketCreator.environmentChange(effect, 3));
	}

	public void setHair(int hair) {
		getPlayer().setHair(hair);
		getPlayer().updateSingleStat(MapleStat.HAIR, hair);
		getPlayer().equipChanged();
	}
        
        public void cosmeticExists(int type, int idhere)
        {
            getPlayer().cosmeticExists(type, idhere);
        }

	public void setFace(int face) {
		getPlayer().setFace(face);
		getPlayer().updateSingleStat(MapleStat.FACE, face);
		getPlayer().equipChanged();
	}
        public int getMonsterCount() {
		MapleMap map = getPlayer().getMap();
		List<MapleMapObject> monsters = map.getMapObjectsInRange(
				new Point(0, 0), Double.POSITIVE_INFINITY,
				Arrays.asList(MapleMapObjectType.MONSTER));
		return monsters.size();

	}
        public boolean haveSpace(){
            if(getPlayer().getInventory(MapleInventoryType.EQUIP).isFull())
                return false;
            else
                return true;
        }

	public void setSkin(int color) {
		getPlayer().setSkinColor(MapleSkinColor.getById(color));
		getPlayer().updateSingleStat(MapleStat.SKIN, color);
		getPlayer().equipChanged();
	}
        public String JQrank(int id) throws SQLException{
           ResultSet jqrset = Ranking.getJqrank(id);
         int countjqr = 1;
         String month="";
         switch(Calendar.getInstance().get(Calendar.MONTH)+1){
             case 1:
                 month ="January";
                 break;
              case 2:
                 month ="February";
                 break;
                   case 3:
                 month ="March";
                 break;
                        case 4:
                 month ="April";
                 break;
                             case 5:
                 month ="May";
                 break;
                                  case 6:
                 month ="June";
                 break;
                                       case 7:
                 month ="July";
                 break;
                                            case 8:
                 month ="August";
                 break;
                                                 case 9:
                 month ="September";
                 break;
                                                      case 10:
                 month ="October";
                 break;
                                                           case 11:
                 month ="November";
                 break;
                                                                case 12:
                 month ="December";
                 break;
                                                                     default:
                 month ="January";
                 break;
                                                
         }
         String postjqr = "#eTop 10 Players for " + month + " 2017 : #n\r\n";
         while(jqrset.next()){
            postjqr+= countjqr + ". " + jqrset.getString("player") + " - " + jqrset.getDouble("time") + " seconds.";
            postjqr+= "\r\n";
            countjqr++;
         }
         return postjqr; 
        }
        public String JQpersrank(int id) throws SQLException{
           ResultSet jqprset = Ranking.getJqpersrank(id, getPlayer());
         int countjqpr = 1;
         String postjqpr = "#ePersonal Record:#n ";
         while(jqprset.next()){
            postjqpr+= jqprset.getDouble("time") + " seconds.";
            postjqpr+= "\r\n";
            countjqpr++;
         }
         return postjqpr; 
        }
        public void Finishjq(){
            Calendar finishtime = Calendar.getInstance();
            Calendar starttime = getPlayer().JQtime();
            int mapid = getPlayer().getMapId();
            
            int finishhour = finishtime.get(Calendar.HOUR),finishminute =  finishtime.get(Calendar.MINUTE);   
            double finishmili = finishtime.get(Calendar.MILLISECOND);
            double finishsecond = finishtime.get(Calendar.SECOND) + finishmili/1000;
            int starthour = starttime.get(Calendar.HOUR),startminute =  starttime.get(Calendar.MINUTE);
            double startmili = starttime.get(Calendar.MILLISECOND);
            double startsecond = starttime.get(Calendar.SECOND) + startmili/1000;
            String msg= "[JQ System] " + getPlayer().getName() + " has finished " + getPlayer().getMap().getMapName() + " in ";
            
             if(starthour > finishhour)
                     finishhour += 12;
             if(startminute > finishminute){
                    finishminute += 60;
                     finishhour -= 1;
                 }
             if(startsecond > finishsecond){
                 finishsecond+=60;
                 finishminute-=1;
             }
              
              int disthours = finishhour - starthour;
              int distminutes = finishminute - startminute;
              double distseconds = Double.parseDouble(new DecimalFormat("##.###").format(finishsecond -startsecond));
              
              
               if(disthours == 0){  
                    if(distminutes == 1)
                         msg+= distminutes + " minute and " + distseconds + " seconds.";
                    else if(distminutes == 0)
                        msg+= distseconds + " seconds.";
                    else
                        msg+= distminutes + " minutes and " + distseconds + " seconds."; 
               
                }
                else if(disthours == 1){
                    if(distminutes == 0)
                     msg+= disthours + " hour and " + distseconds + " seconds."; 
                    else if(distminutes == 1)
                        msg+= disthours + " hour, " + distminutes + " minute and " + distseconds + " seconds."; 
                    else
                        msg+= disthours + " hour, " + distminutes + " minutes and " + distseconds + " seconds.";
                }
                else{
                    if(distminutes == 0)
                     msg+= disthours + " hours and " + distseconds + " seconds.";
                    else if(distminutes == 1)
                         msg+= disthours + " hours, " + distminutes + " minute and " + distseconds + " seconds.";
                    else
                          msg+= disthours + " hours, " + distminutes + " minutes and " + distseconds + " seconds.";
                }
               
              Server.getInstance().broadcastMessage(MaplePacketCreator.serverNotice(6, msg)); 
              getPlayer().addJqpoints(1);
              double thistime = disthours*3600 + distminutes*60 + distseconds;
              double sqltime=0;
              int month=0,currentmonth=0;
              ResultSet rs;
                if(Calendar.getInstance().get(Calendar.MONTH) + 1 == 13)
                 currentmonth = 1;
             else
                 currentmonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
              
                  try{
        PreparedStatement ps;           
        Connection con =  DatabaseConnection.getConnection();
        ps = con.prepareStatement("SELECT time, month FROM jqtimes WHERE player = ? AND jqid = ?");       
        ps.setString(1,getPlayer().getName());
        ps.setInt(2,mapid);
        rs = ps.executeQuery();
         while(rs.next()){
                  sqltime = rs.getDouble("time");
                  month = rs.getInt("month");
             }
         if(sqltime != 0){           
             if(sqltime > thistime || month != currentmonth){
               try (PreparedStatement ups = con.prepareStatement("UPDATE jqtimes SET time = ?, month = ? WHERE player = ? AND jqid = ?")) {                               
               ups.setDouble(1, thistime);              
                    ups.setInt(2, currentmonth);              ;
                ups.setString(3,getPlayer().getName());
                 ups.setInt(4,mapid);
                ups.executeUpdate();
                ups.close();             
                } 
               getPlayer().dropMessage(6,"You've just beaten your personal record of " + sqltime + " seconds! Congratulations!");
             }
         }
         else{ 
            try (PreparedStatement ips = con.prepareStatement("INSERT INTO jqtimes (player, time, jqid, month, gm) VALUES (?, ?, ?, ?, ?)")) {                               
               ips.setString(1, getPlayer().getName());               
                ips.setDouble(2, thistime);
                ips.setInt(3, mapid);                
                ips.setInt(4, currentmonth);
                if(getPlayer().isGM())
                 ips.setInt(5, 1);
                else
                   ips.setInt(5, 0);  
               
                ips.execute();
                ips.close();             
            } 
         } 
        } catch(SQLException sqlexc){
              System.out.print("Error jqtimes: " + sqlexc);
        }   
        }
        public void startMapUnscramble(String numofletters) { 
            if(!numofletters.equals("none")){                
            int letters = Integer.parseInt(numofletters);
           /* String[]words = {"eye","den","ray","fag","blue","care","more","cock","homo","chair","table","train","block","memory","thanks","trades","bright","tragedy","crowded","brownie","straight","fizzling","tracking","blackjack","mezzaluna","maximizer","embezzling","maplestory","carjacking","homophobic"};
            List<String>specwords = new ArrayList<>();
            
            for(int i = 0 ; i < words.length;i++)
                if(words[i].length() == letters)
                    specwords.add(words[i]);
            
            */
        ResultSet rs;   
        String input="";
             try{
        PreparedStatement ps;           
        Connection con =  DatabaseConnection.getConnection();
        ps = con.prepareStatement("SELECT word FROM wordlist WHERE letters = " + letters + " ORDER BY RAND() LIMIT 1");
        rs = ps.executeQuery();
         while(rs.next()){
                  input += rs.getString("word");             
             }
        } catch(SQLException sqlexc){
              System.out.print("Error selecting wordlist: " + sqlexc);
        }   
               boolean finishedscramble = false;
                     Random rand = new Random();
                  //   int randword = rand.nextInt(specwords.size()); 
                     int randnum; 
                             //input = specwords.get(randword);
                             char[] charinput = input.toCharArray();
                             String output = "";                            
                             while(!finishedscramble){
                                 if(input.length() == output.length())
                                     finishedscramble = true;
                                 else{
                                     randnum = rand.nextInt(input.length());
                                     if(charinput[randnum] !=  0){
                                         output += charinput[randnum];
                                         charinput[randnum] = 0;
                                     }
                                 }                                 
                             }
                   //  specwords.clear();
                     if(getPlayer().getMap().getMiniunscramblehost().equals("") || !getPlayer().getMap().getMiniunscramblehost().equals(getPlayer().getName()))                    
                        getPlayer().getMap().broadcastMessage(MaplePacketCreator.serverNotice(6, "[Unscramble] " + getPlayer().getName() + " has started a round of Unscramble! "));
                                          
                     getPlayer().getMap().broadcastMessage(MaplePacketCreator.serverNotice(6, "[Unscramble] " + output));
                     ServerConstants.miniunscrambleAnswer = input; 
                     getPlayer().getMap().setMiniunscramble(true, getPlayer().getName());
            }
            else
                getPlayer().dropMessage(6,"Error. Please enter a valid input");
        }
        public void deleteChar(int cid) {
            
		FilePrinter.printError(FilePrinter.DELETED_CHARACTERS + c.getAccountName() + ".txt", c.getAccountName() + " deleted CID: " + cid + "\r\n");			
            c.announce(MaplePacketCreator.deleteCharResponse(cid, 0));
            c.deleteCharacter(cid);
	}
    /*     public void finishJQ(int jqid) {
             
        if(jqid == 682000200) {                    
             try {
               Connection con = DatabaseConnection.getConnection();
             //  con.setAutoCommit(false);
            try (PreparedStatement ps = con.prepareStatement("UPDATE characters SET chimneyjq = ? WHERE id = ?")) {
                ps.setString(getNumber, getText)                             
            //    ps.setInt(5, id);
                ps.executeUpdate();
                ps.close();
           
               
            }
        } catch (SQLException e) {
            System.out.print("Error inserting eventlog: " + e);
        }
        }
        else if(jqid == 100000202){
         }
         } */
        public boolean canMSI() {
		if(getPlayer().isGM() || (getPlayer().getStr() > 29999 && getPlayer().getDex() > 29999 && getPlayer().getInt() > 29999 && getPlayer().getLuk() > 29999))
	           return true;
                else
                    return false;
       }
        public int makeMSI() {          
        // Item msi = new Item(id,(short) 0,(short) 1);
     
        // int[]posmsis = {1002600,1002601,1002602,1002603,1002186,1002436,1032001,1032002,1032003,1032004,1032005,1032006,1032007,1032008,1032009,1032010,1032011,1032012,1102053,1102054,1102055,1102056,1102057,1102058,1102059,1102060,1102061,1102062,1102063,1102064,1102065,1102066,1102067,1102068,1102069,1082002,1082145,1082146,1082147,1082148,1082149,1072344,1072038,1072037,1072005,1072012,1072008,1072001,1072006,1040002,1045000,1045001,1045002,1045003,1045004,1045005,1045006,1045007,1045008,1045009,1045010,1045021,1045025,1060002,1060004,1062112,1012070,1012071,1012072,1012073,1012074,1012075,1012080,1022071,1022072,1022073,1022074,1022075,1122001,1122002,1122003,1122004,1122005,1122006,1122007,1112112,1112113,1112114,1112115,1112116,1112117,1112118,1112119,1112120,1042360};   
        
         
         Random rand = new Random();  
        // int id = posmsis[rand.nextInt(posmsis.length)];  
            ResultSet rs;
            int id=0;
         try{
        PreparedStatement ps;           
        Connection con =  DatabaseConnection.getConnection();
        ps = con.prepareStatement("SELECT itemid FROM msilist ORDER BY RAND() LIMIT 1");
        rs = ps.executeQuery();
         while(rs.next()){
                  id += rs.getInt("itemid");            
             }
        } catch(SQLException sqlexc){
              System.out.print("Error selecting msilist: " + sqlexc);
        }
         
         Equip msi = new Equip(id,(short) 0,0);
         
          if(rand.nextInt(100) == 69){ // 1/100 chance
             msi.setOwner("GODLIKE");      
         msi.setStr((short)32767);
         msi.setDex((short)32767);
         msi.setInt((short)32767);
         msi.setLuk((short)32767);
         msi.setAcc((short)32767);
         msi.setAvoid((short)32767);  
         msi.setJump((short)32767);
         msi.setSpeed((short)32767);
         msi.setWatk((short)(rand.nextInt(1000-500) + 500)); 
             
         }
          else{
              
         msi.setStr((short)(rand.nextInt(32767-28000) + 28000));
         msi.setDex((short)(rand.nextInt(32767-28000) + 28000));
         msi.setInt((short)(rand.nextInt(32767-28000) + 28000));
         msi.setLuk((short)(rand.nextInt(32767-28000) + 28000));
         msi.setAcc((short)(rand.nextInt(32767-28000) + 28000));
         msi.setAvoid((short)(rand.nextInt(32767-28000) + 28000));  
         msi.setJump((short)(rand.nextInt(32767-28000) + 28000));
         msi.setSpeed((short)(rand.nextInt(32767-28000) + 28000));
         msi.setWatk((short)(rand.nextInt(100-20) + 20));    
          }
        
       if(!getPlayer().isGM()){
         getPlayer().setStr(getPlayer().getStr() - 29996);
          getPlayer().setDex(getPlayer().getDex() - 29996);
           getPlayer().setInt(getPlayer().getInt() - 29996);
            getPlayer().setLuk(getPlayer().getLuk() - 29996);
            
             getPlayer().updateSingleStat(MapleStat.STR, getPlayer().getStr());
              getPlayer().updateSingleStat(MapleStat.DEX, getPlayer().getDex());
               getPlayer().updateSingleStat(MapleStat.INT, getPlayer().getInt());
                getPlayer().updateSingleStat(MapleStat.LUK, getPlayer().getLuk());
       }
              
                getPlayer().getMap().broadcastMessage(MaplePacketCreator.gachaponMessage(msi, "MSI Npc", getPlayer()));
              MapleInventoryManipulator.addFromDrop(c, (Item)msi, true);  
              return id;
       }        
         public String Rbrank() throws SQLException {
      /*   for (MapleCharacter chr : c.getWorldServer().getPlayerStorage().getAllCharacters()) {
					chr.saveToDB();
				} */
         ResultSet rbset = Ranking.getRbrank();
         int countrb = 1;
         String postrb = "Top 10 RBers: \r\n";
         while(rbset.next()){
            postrb+= countrb + ". " + rbset.getString("name") + " || " + rbset.getInt("rebirths");
            postrb+= "\r\n";
            countrb++;
         }
         return postrb;
       }
          public String Jqptsrank() throws SQLException {
      /*   for (MapleCharacter chr : c.getWorldServer().getPlayerStorage().getAllCharacters()) {
					chr.saveToDB();
				} */
         ResultSet jqptsset = Ranking.getJqptsrank();
         int countjqpts = 1;
         String postjqpts = "Top 10 Most Jumpy Jumpfucks: \r\n";
         while(jqptsset.next()){
            postjqpts+= countjqpts + ". " + jqptsset.getString("name") + " || " + jqptsset.getInt("jqpoints");
            postjqpts+= "\r\n";
            countjqpts++;
         }
         return postjqpts;
       }
         public String Currank() throws SQLException {
         /* for (MapleCharacter chr : c.getWorldServer().getPlayerStorage().getAllCharacters()) {
					chr.saveToDB();
				} */
         ResultSet curset = Ranking.getCurrank();
         int countcur = 1;
         String postcur = "Top 10 Most Rich People: \r\n";
         while(curset.next()){
           postcur+= countcur + ". " + curset.getString("name") + " || " + curset.getInt("currency");
           postcur+= "\r\n";
            countcur++;
         }
         return postcur;
       }
         public String Eprank() throws SQLException {
      /*   for (MapleCharacter chr : c.getWorldServer().getPlayerStorage().getAllCharacters()) {
					chr.saveToDB();
				}  */
         ResultSet epset = Ranking.getEprank();
         int countep = 1;
         String postep = "Top 10 Players with the most event points: \r\n";
         while(epset.next()){
            postep+= countep + ". " + epset.getString("name") + " || " + epset.getInt("eventpoints");
            postep+= "\r\n";
            countep++;
         }
         return postep;
       }
         public String Famerank() throws SQLException {
        
         ResultSet fameset = Ranking.getFamerank();
         int countfame = 1;
         String postfame = "Top 10 Most Popular People: \r\n";
         while(fameset.next()){
            postfame+= countfame + ". " + fameset.getString("name") + " || " + fameset.getInt("fame");
            postfame+= "\r\n";
            countfame++;
         }
         return postfame;
       }
         public String Erprank() throws SQLException {
           
         ResultSet erpset = Ranking.getErprank();
         int counterp = 1;
         String posterp = "Top 10 Event Gods: \r\n";
         while(erpset.next()){
            posterp+= counterp + ". " + erpset.getString("name") + " || " + erpset.getInt("erp");
            posterp+= "\r\n";
            counterp++;
         }
         return posterp;
       }
         public String Omokrank() throws SQLException {
   
         ResultSet omokset = Ranking.getOmokrank();
         int countomok = 1;
         String postomok = "Top 10 Omok Fanatics: \r\n";
         while(omokset.next()){
            postomok+= countomok + ". " + omokset.getString("name") + " || " + omokset.getInt("omokwins");
            postomok+= "\r\n";
            countomok++;
         }
         return postomok;
       }
         public String Omscorerank() throws SQLException {
        
         ResultSet omscoreset = Ranking.getOmscorerank();
         int countomscore = 1;
         String postomscore = "Top 10 Omok Prodigies: \r\n";
         while(omscoreset.next()){
            postomscore+= countomscore + ". " + omscoreset.getString("name") + " || " + omscoreset.getInt("omokpoints");
            postomscore+= "\r\n";
            countomscore++;
         }
         return postomscore;
       }
         public String Fishptsrank() throws SQLException {
         
         ResultSet fishptsset = Ranking.getFishptsrank();
         int countfishpts = 1;
         String postfishpts = "Top 10 Fish Merchants: \r\n";
         while(fishptsset.next()){
            postfishpts+= countfishpts + ". " + fishptsset.getString("name") + " || " + fishptsset.getInt("fishpoints");
            postfishpts+= "\r\n";
            countfishpts++;
         }
         return postfishpts;
       }
         public String Fishexprank() throws SQLException {
        
         ResultSet fishexpset = Ranking.getFishexprank();
         int countfishexp = 1;
         String postfishexp = "Top 10 Fishermen: \r\n";
         while(fishexpset.next()){
            postfishexp+= countfishexp + ". " + fishexpset.getString("name") + " || " + fish.fishLevelDB(fishexpset.getInt("fishexp")) + " (" + fishexpset.getInt("fishexp") + ")";
            postfishexp+= "\r\n";
            countfishexp++;
         }
         return postfishexp;
       }
        
            public void summonMob(int mobid, int customHP, int customEXP, byte amount) {
		MapleMonsterStats newStats = new MapleMonsterStats();

		if (customHP > 0) {
			newStats.setHp(customHP);
		}
		if (customEXP >= 0) {
			newStats.setExp(customEXP);
		}
		if (amount <= 1) {
			MapleMonster npcmob = MapleLifeFactory.getMonster(mobid);
			npcmob.setOverrideStats(newStats);
			npcmob.setHp(npcmob.getMaxHp());
			getPlayer().getMap().spawnMonsterOnGroudBelow(npcmob,
					getPlayer().getPosition());
		} else if (getMonsterCount() > 15) {

			MapleMap map = getPlayer().getMap();
			map.killAllMonsters(false); // No drop.s
		} else {
			for (int i = 0; i < amount; i++) {
				MapleMonster npcmob = MapleLifeFactory.getMonster(mobid);
				npcmob.setOverrideStats(newStats);
				npcmob.setHp(npcmob.getMaxHp());
				getPlayer().getMap().spawnMonsterOnGroudBelow(npcmob,
						getPlayer().getPosition());
			}
		}
	}


	public int itemQuantity(int itemid) {
		return getPlayer().getInventory(MapleItemInformationProvider.getInstance().getInventoryType(itemid)).countById(itemid);
	}

	public void displayGuildRanks() {
		MapleGuild.displayGuildRanks(getClient(), npc);
	}
        public void killAllMonsters() {
		MapleMap map = getPlayer().getMap();
		map.killAllMonsters(false); // No drop.
	}
        public void gainReborns(int reborns) {
		getPlayer().setRebirths(reborns + getPlayer().getRebirths());
	}

	public int getReborns() {
		return getPlayer().getRebirths();
	}


	@Override
	public MapleParty getParty() {
		return getPlayer().getParty();
	}

	@Override
	public void resetMap(int mapid) {
        try {
            c.getChannelServer().getMapFactory().disposeMap(mapid);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        try {
            getClient().getChannelServer().getMapFactory().getMap(mapid).resetReactors();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
	}

	public void gainCloseness(int closeness) {
		for (MaplePet pet : getPlayer().getPets()) {
			if (pet.getCloseness() > 30000) {
				pet.setCloseness(30000);
				return;
			}
			pet.gainCloseness(closeness);
			while (pet.getCloseness() > ExpTable.getClosenessNeededForLevel(pet.getLevel())) {
				pet.setLevel((byte) (pet.getLevel() + 1));
				byte index = getPlayer().getPetIndex(pet);
				getClient().announce(MaplePacketCreator.showOwnPetLevelUp(index));
				getPlayer().getMap().broadcastMessage(getPlayer(), MaplePacketCreator.showPetLevelUp(getPlayer(), index));
			}
			Item petz = getPlayer().getInventory(MapleInventoryType.CASH).getItem(pet.getPosition());
			getPlayer().forceUpdateItem(petz);
		}
	}
        
        public Equip getEquip(byte slot, MapleCharacter player) {
        MapleInventory equip = player.getInventory(MapleInventoryType.EQUIP);
        Equip eu = (Equip) equip.getItem(slot);
        return eu;
    }
    
    public Equip getEquipped(byte slot, MapleCharacter player) {
        MapleInventory equip = player.getInventory(MapleInventoryType.EQUIPPED);
        Equip eu = (Equip) equip.getItem(slot);
        return eu;
    }
    
	public String getName() {
		return getPlayer().getName();
	}

	public int getGender() {
		return getPlayer().getGender();
	}

	public void changeJobById(int a) {
		getPlayer().changeJob(MapleJob.getById(a));
	}

	public void changeJob(MapleJob job){
		getPlayer().changeJob(job);
	}

	public MapleJob getJobName(int id) {
		return MapleJob.getById(id);
	}

	public MapleStatEffect getItemEffect(int itemId) {
		return MapleItemInformationProvider.getInstance().getItemEffect(itemId);
	}

	public void resetStats() {
		getPlayer().resetStats();
	}

	public void maxMastery() {
		for (MapleData skill_ : MapleDataProviderFactory.getDataProvider(new File(System.getProperty("wzpath") + "/" + "String.wz")).getData("Skill.img").getChildren()) {
			try {
				Skill skill = SkillFactory.getSkill(Integer.parseInt(skill_.getName()));
				getPlayer().changeSkillLevel(skill, (byte) 0, skill.getMaxLevel(), -1);
			} catch (NumberFormatException nfe) {
				break;
			} catch (NullPointerException npe) {
				continue;
			}
		}
	}
        public void showItemsgained(int id, short quantity){
            c.announce(MaplePacketCreator.getShowItemGain(id, quantity, true));
        

        }
        public void gainSandboxitem(int id){
         //   MapleInventory equips = getPlayer().getInventory(MapleInventoryType.EQUIP);            
            Equip eq = new Equip(id,(short)0,0);
            eq.setOwner("SANDBOX");
             MapleInventoryManipulator.addFromDrop(c, (Item)eq, true); 
            
           
            
        }
        
        public String getItemNameById(int itemid) {
        String name = null;
        for (Pair<Integer, String> itemPair : MapleItemInformationProvider.getInstance().getAllItems()) {
            if (itemPair.getLeft() == itemid) {
                name = itemPair.getRight();
            }
        }
        return name;
    }
    
    public int getItemIdByName(String name) {
        int id = 0;
        for (Pair<Integer, String> itemPair : MapleItemInformationProvider.getInstance().getAllItems()) {
            if (itemPair.getRight().equals(name)) {
                id = itemPair.getLeft();
            }
        }
        return id;
    }
       
	public void doGachapon() throws SQLException {
		int[] maps = {100000000, 101000000, 102000000, 103000000, 105040300, 800000000, 809000101, 809000201, 600000000, 120000000};

		MapleGachaponItem item = MapleGachapon.getInstance().process(npc);

		Item itemGained = gainItem(item.getId(), (short) (item.getId() / 10000 == 200 ? 100 : 1), true, true); // For normal potions, make it give 100.

		sendNext("You have obtained a #b#t" + item.getId() + "##k.");
		
		String map = c.getChannelServer().getMapFactory().getMap(maps[(getNpc() != 9100117 && getNpc() != 9100109) ? (getNpc() - 9100100) : getNpc() == 9100109 ? 8 : 9]).getMapName();
		
		LogHelper.logGacha(getPlayer(), item.getId(), map);
		
		if (item.getTier() > 0){ //Uncommon and Rare
			Server.getInstance().broadcastMessage(MaplePacketCreator.gachaponMessage(itemGained, map, getPlayer()));
		}
	}

	public void disbandAlliance(MapleClient c, int allianceId) {
		PreparedStatement ps = null;
		try {
			ps = DatabaseConnection.getConnection().prepareStatement("DELETE FROM `alliance` WHERE id = ?");
			ps.setInt(1, allianceId);
			ps.executeUpdate();
			ps.close();
			Server.getInstance().allianceMessage(c.getPlayer().getGuild().getAllianceId(), MaplePacketCreator.disbandAlliance(allianceId), -1, -1);
			Server.getInstance().disbandAlliance(allianceId);
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			try {
				if (ps != null && !ps.isClosed()) {
					ps.close();
				}
			} catch (SQLException ex) {
			}
		}
	}

	public boolean canBeUsedAllianceName(String name) {
		if (name.contains(" ") || name.length() > 12) {
			return false;
		}
		try {
			ResultSet rs;
			try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("SELECT name FROM alliance WHERE name = ?")) {
				ps.setString(1, name);
				rs = ps.executeQuery();
				if (rs.next()) {
					ps.close();
					rs.close();
					return false;
				}
			}
			rs.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
        }
                public void MakeGMItem(byte slot, MapleCharacter player) {
		MapleInventory equip = player.getInventory(MapleInventoryType.EQUIP);
		Equip eu = (Equip) equip.getItem(slot);
		int item = equip.getItem(slot).getItemId();
		MapleJob job = eu.getJob();
		short hand = eu.getHands();
		byte level = eu.getLevel();
		Equip oItem = new Equip(item, equip.getNextFreeSlot());
		Equip nItem = new Equip(item, equip.getNextFreeSlot());
		nItem.setStr((short) 32767); // STR
		nItem.setDex((short) 32767); // DEX
		nItem.setInt((short) 32767); // INT
		nItem.setLuk((short) 32767); // LUK
		nItem.setWdef((short) 69);
		nItem.setMdef((short) 69);
		nItem.setUpgradeSlots((byte) 0);
		nItem.setJob(job);
		nItem.setHands(hand);
		nItem.setLevel(level);
		player.getInventory(MapleInventoryType.EQUIP).addFromDB(nItem);
		player.getInventory(MapleInventoryType.EQUIP).removeItem(slot);
	}

	public void MakeGMItem1(byte slot, MapleCharacter player) {
		int randwa = 15 + (int) (Math.random() * ((25 - 15) + 1));
		int randstr = 25000 + (int) (Math.random() * ((32767 - 25000) + 1));
		int randdex = 25000 + (int) (Math.random() * ((32767 - 25000) + 1));
		int randluk = 25000 + (int) (Math.random() * ((32767 - 25000) + 1));
		int randint = 25000 + (int) (Math.random() * ((32767 - 25000) + 1));
		MapleInventory equip = player.getInventory(MapleInventoryType.EQUIP);
		Equip eu = (Equip) equip.getItem(slot);
		int item = equip.getItem(slot).getItemId();
		MapleJob job = eu.getJob();
		short hand = eu.getHands();
		byte level = eu.getLevel();
		Equip nItem = new Equip(item, equip.getNextFreeSlot());
		// TODO: randomise msi stats
		int randstat = (int) (25000 + (Math.random() * ((32767 - 25000) + 1)));
		nItem.setStr((short) randstr); // STR
		nItem.setDex((short) randdex); // DEX
		nItem.setInt((short) randint); // INT
		nItem.setLuk((short) randluk); // LUK
		nItem.setWdef((short) 69);
		nItem.setMdef((short) 69);
		nItem.setWatk((short) randwa); // WA
		nItem.setUpgradeSlots((byte) 0); // SLOT
		nItem.setJob(job);
		nItem.setHands(hand);
		nItem.setLevel(level);
		player.getInventory(MapleInventoryType.EQUIP).addFromDB(nItem);
		player.getInventory(MapleInventoryType.EQUIP).removeItem(slot);
	}

	public void MapleHat(byte slot, MapleCharacter player) {
		MapleInventory equip = player.getInventory(MapleInventoryType.EQUIP);
		Equip eu = (Equip) equip.getItem(slot);
		int item = equip.getItem(slot).getItemId();
		MapleJob job = eu.getJob();
		short hand = eu.getHands();
		byte level = eu.getLevel();
		Equip nItem = new Equip(1002508, equip.getNextFreeSlot());
		nItem.setStr((short) 10000); // STR
		nItem.setDex((short) 10000); // DEX
		nItem.setInt((short) 10000); // INT
		nItem.setLuk((short) 10000);
		nItem.setWatk((short) 20); // WA
		nItem.setUpgradeSlots((byte) 0); // SLOT
		nItem.setJob(job);
		nItem.setHands(hand);
		nItem.setLevel(level);
		player.getInventory(MapleInventoryType.EQUIP).addFromDB(nItem);
	}

	public void MapleCape(byte slot, MapleCharacter player) {
		MapleInventory equip = player.getInventory(MapleInventoryType.EQUIP);
		Equip eu = (Equip) equip.getItem(slot);
		int item = equip.getItem(slot).getItemId();
		MapleJob job = eu.getJob();
		short hand = eu.getHands();
		byte level = eu.getLevel();
		Equip nItem = new Equip(1102166, equip.getNextFreeSlot());
		nItem.setStr((short) 6000); // STR
		nItem.setDex((short) 6000); // DEX
		nItem.setInt((short) 6000); // INT
		nItem.setLuk((short) 6000);
		nItem.setWatk((short) 12); // WA
		nItem.setUpgradeSlots((byte) 0); // SLOT
		nItem.setJob(job);
		nItem.setHands(hand);
		nItem.setLevel(level);
		player.getInventory(MapleInventoryType.EQUIP).addFromDB(nItem);
	}

	public void MapleEarrings(byte slot, MapleCharacter player) {
		MapleInventory equip = player.getInventory(MapleInventoryType.EQUIP);
		Equip eu = (Equip) equip.getItem(slot);
		int item = equip.getItem(slot).getItemId();
		MapleJob job = eu.getJob();
		short hand = eu.getHands();
		byte level = eu.getLevel();
		Equip nItem = new Equip(1032040, equip.getNextFreeSlot());
		nItem.setStr((short) 8000); // STR
		nItem.setDex((short) 8000); // DEX
		nItem.setInt((short) 8000); // INT
		nItem.setLuk((short) 8000);
		nItem.setWatk((short) 16); // WA
		nItem.setUpgradeSlots((byte) 0); // SLOT
		nItem.setJob(job);
		nItem.setHands(hand);
		nItem.setLevel(level);
		player.getInventory(MapleInventoryType.EQUIP).addFromDB(nItem);
	}

	public void MapleFace(byte slot, MapleCharacter player) {
		MapleInventory equip = player.getInventory(MapleInventoryType.EQUIP);
		Equip eu = (Equip) equip.getItem(slot);
		int item = equip.getItem(slot).getItemId();
		MapleJob job = eu.getJob();
		short hand = eu.getHands();
		byte level = eu.getLevel();
		Equip nItem = new Equip(1012286, equip.getNextFreeSlot());
		nItem.setStr((short) 5000); // STR
		nItem.setDex((short) 5000); // DEX
		nItem.setInt((short) 5000); // INT
		nItem.setLuk((short) 5000);
		nItem.setWatk((short) 10); // WA
		nItem.setUpgradeSlots((byte) 0); // SLOT
		nItem.setJob(job);
		nItem.setHands(hand);
		nItem.setLevel(level);
		player.getInventory(MapleInventoryType.EQUIP).addFromDB(nItem);
	}

	public void MapleMedal(byte slot, MapleCharacter player) {
		MapleInventory equip = player.getInventory(MapleInventoryType.EQUIP);
		Equip eu = (Equip) equip.getItem(slot);
		int item = equip.getItem(slot).getItemId();
		MapleJob job = eu.getJob();
		short hand = eu.getHands();
		byte level = eu.getLevel();
		Equip nItem = new Equip(1142355, equip.getNextFreeSlot());
		nItem.setStr((short) 4000); // STR
		nItem.setDex((short) 4000); // DEX
		nItem.setInt((short) 4000); // INT
		nItem.setLuk((short) 4000);
		nItem.setWatk((short) 8); // WA
		nItem.setUpgradeSlots((byte) 0); // SLOT
		nItem.setJob(job);
		nItem.setHands(hand);
		nItem.setLevel(level);
		player.getInventory(MapleInventoryType.EQUIP).addFromDB(nItem);
	}

	public void MaplePendant(byte slot, MapleCharacter player) {
		MapleInventory equip = player.getInventory(MapleInventoryType.EQUIP);
		Equip eu = (Equip) equip.getItem(slot);
		int item = equip.getItem(slot).getItemId();
		MapleJob job = eu.getJob();
		short hand = eu.getHands();
		byte level = eu.getLevel();
		Equip nItem = new Equip(1122154, equip.getNextFreeSlot());
		nItem.setStr((short) 2000); // STR
		nItem.setDex((short) 2000); // DEX
		nItem.setInt((short) 2000); // INT
		nItem.setLuk((short) 2000);
		nItem.setWatk((short) 6); // WA
		nItem.setUpgradeSlots((byte) 0); // SLOT
		nItem.setJob(job);
		nItem.setHands(hand);
		nItem.setLevel(level);
		player.getInventory(MapleInventoryType.EQUIP).addFromDB(nItem);
	}

	public void MapleGloves(byte slot, MapleCharacter player) {
		MapleInventory equip = player.getInventory(MapleInventoryType.EQUIP);
		Equip eu = (Equip) equip.getItem(slot);
		int item = equip.getItem(slot).getItemId();
		MapleJob job = eu.getJob();
		short hand = eu.getHands();
		byte level = eu.getLevel();
		Equip nItem = new Equip(1082315, equip.getNextFreeSlot());
		nItem.setStr((short) 1000); // STR
		nItem.setDex((short) 1000); // DEX
		nItem.setInt((short) 1000); // INT
		nItem.setLuk((short) 1000);
		nItem.setWatk((short) 4); // WA
		nItem.setUpgradeSlots((byte) 0); // SLOT
		nItem.setJob(job);
		nItem.setHands(hand);
		nItem.setLevel(level);
		player.getInventory(MapleInventoryType.EQUIP).addFromDB(nItem);
	}

	public void MakeGMItem2(byte slot, MapleCharacter player) {
		MapleInventory equip = player.getInventory(MapleInventoryType.EQUIP);
		Equip eu = (Equip) equip.getItem(slot);
		int item = equip.getItem(slot).getItemId();
		MapleJob job = eu.getJob();
		short hand = eu.getHands();
		byte level = eu.getLevel();
		Equip nItem = new Equip(item, equip.getNextFreeSlot());
		nItem.setStr((short) 32767); // STR
		nItem.setDex((short) 32767); // DEX
		nItem.setInt((short) 32767); // INT
		nItem.setLuk((short) 32767); // LUK
		nItem.setHp((short) 32767); // HP
		nItem.setMp((short) 32767); // MP
		nItem.setWatk((short) 32767); // WA
		nItem.setMatk((short) 32767); // MA
		nItem.setWdef((short) 32767); // Wdef
		nItem.setMdef((short) 32767); // Mdef
		nItem.setAcc((short) 32767); // Acc
		nItem.setAvoid((short) 32767); // Avoid
		nItem.setSpeed((short) 32767); // speed
		nItem.setJump((short) 32767); // Jump
		nItem.setUpgradeSlots((byte) 0);
		nItem.setJob(job);
		nItem.setHands(hand);
		nItem.setLevel(level);
		player.getInventory(MapleInventoryType.EQUIP).addFromDB(nItem);
	}

	public void MakeGMItem3(byte slot, MapleCharacter player) {
		int randwa = 10 + (int) (Math.random() * ((20 - 10) + 1));
		int randstr = 10000 + (int) (Math.random() * ((20000 - 10000) + 1));
		int randdex = 10000 + (int) (Math.random() * ((20000 - 10000) + 1));
		int randluk = 10000 + (int) (Math.random() * ((20000 - 10000) + 1));
		int randint = 10000 + (int) (Math.random() * ((20000 - 10000) + 1));
		MapleInventory equip = player.getInventory(MapleInventoryType.EQUIP);
		Equip eu = (Equip) equip.getItem(slot);
		int item = equip.getItem(slot).getItemId();
		MapleJob job = eu.getJob();
		short hand = eu.getHands();
		byte level = eu.getLevel();
		Equip nItem = new Equip(item, equip.getNextFreeSlot());
		nItem.setStr((short) randstr); // STR
		nItem.setDex((short) randdex); // DEX
		nItem.setInt((short) randluk); // INT
		nItem.setLuk((short) randint); // LUK
		nItem.setWdef((short) 69);
		nItem.setMdef((short) 69);
		nItem.setWatk((short) randwa); // WA
		nItem.setUpgradeSlots((byte) 0); // SLOT
		nItem.setJob(job);
		nItem.setHands(hand);
		nItem.setLevel(level);
		player.getInventory(MapleInventoryType.EQUIP).addFromDB(nItem);
		player.getInventory(MapleInventoryType.EQUIP).removeItem(slot);
	}

	public void MakeGMItem4(byte slot, MapleCharacter player) {
		int randwa = 5 + (int) (Math.random() * ((10 - 5) + 1));
		int randstr = 5000 + (int) (Math.random() * ((10000 - 5000) + 1));
		int randdex = 5000 + (int) (Math.random() * ((10000 - 5000) + 1));
		int randluk = 5000 + (int) (Math.random() * ((10000 - 5000) + 1));
		int randint = 5000 + (int) (Math.random() * ((10000 - 5000) + 1));
		MapleInventory equip = player.getInventory(MapleInventoryType.EQUIP);
		Equip eu = (Equip) equip.getItem(slot);
		int item = equip.getItem(slot).getItemId();
		MapleJob job = eu.getJob();
		short hand = eu.getHands();
		byte level = eu.getLevel();
		Equip nItem = new Equip(item, equip.getNextFreeSlot());
		nItem.setStr((short) randstr); // STR
		nItem.setDex((short) randdex); // DEX
		nItem.setInt((short) randluk); // INT
		nItem.setLuk((short) randint); // LUK
		nItem.setWdef((short) 69);
		nItem.setMdef((short) 69);
		nItem.setWatk((short) randwa); // WA
		nItem.setUpgradeSlots((byte) 0); // SLOT
		nItem.setJob(job);
		nItem.setHands(hand);
		nItem.setLevel(level);
		player.getInventory(MapleInventoryType.EQUIP).addFromDB(nItem);
		player.getInventory(MapleInventoryType.EQUIP).removeItem(slot);
	}


	public static MapleAlliance createAlliance(MapleCharacter chr1, MapleCharacter chr2, String name) {
		int id;
		int guild1 = chr1.getGuildId();
		int guild2 = chr2.getGuildId();
		try {
			try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("INSERT INTO `alliance` (`name`, `guild1`, `guild2`) VALUES (?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS)) {
				ps.setString(1, name);
				ps.setInt(2, guild1);
				ps.setInt(3, guild2);
				ps.executeUpdate();
				try (ResultSet rs = ps.getGeneratedKeys()) {
					rs.next();
					id = rs.getInt(1);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		MapleAlliance alliance = new MapleAlliance(name, id, guild1, guild2);
		try {
			Server.getInstance().setGuildAllianceId(guild1, id);
			Server.getInstance().setGuildAllianceId(guild2, id);
			chr1.setAllianceRank(1);
			chr1.saveGuildStatus();
			chr2.setAllianceRank(2);
			chr2.saveGuildStatus();
			Server.getInstance().addAlliance(id, alliance);
			Server.getInstance().allianceMessage(id, MaplePacketCreator.makeNewAlliance(alliance, chr1.getClient()), -1, -1);
		} catch (Exception e) {
			return null;
		}
		return alliance;
	}

	public boolean hasMerchant() {
		return getPlayer().hasMerchant();
	}

	public boolean hasMerchantItems() {
		try {
			if (!ItemFactory.MERCHANT.loadItems(getPlayer().getId(), false).isEmpty()) {
				return true;
			}
		} catch (SQLException e) {
			return false;
		}
		if (getPlayer().getMerchantMeso() == 0) {
			return false;
		} else {
			return true;
		}
	}

	public void showFredrick() {
		c.announce(MaplePacketCreator.getFredrick(getPlayer()));
	}

	public int partyMembersInMap() {
		int inMap = 0;
		for (MapleCharacter char2 : getPlayer().getMap().getCharacters()) {
			if (char2.getParty() == getPlayer().getParty()) {
				inMap++;
			}
		} 
		return inMap;
	}

	public MapleCharacter getMapleCharacter(String player) {
		MapleCharacter target =  Server.getInstance().getWorld(c.getWorld()).getChannel(c.getChannel()).getPlayerStorage().getCharacterByName(player);
		return target;
	}

	public void logLeaf(String prize) {
		LogHelper.logLeaf(getPlayer(), true, prize);
	}
        
        public int[] getItemSearchResult(String search) {
    	List<Integer> results = new ArrayList<>();
    	boolean idSearch = search.startsWith("*");
    	boolean exactSearch = search.endsWith("*");
    	int wildcard = search.startsWith("%") ? 1 : search.endsWith("%") ? 2 : 0;
    	if (idSearch || wildcard == 1)
    		search = search.substring(1, search.length());
    	if (exactSearch || wildcard == 2)
    		search = search.substring(0, search.length() - 1);
    	for (Pair<Integer, String> itemPair : MapleItemInformationProvider.getInstance().getAllItems()) {
    		if (idSearch) {
    			if (exactSearch ? itemPair.getLeft().toString().equals(search) : itemPair.getLeft().toString().contains(search))
    				results.add(itemPair.getLeft());
    		} else if (wildcard > 0) {
				if (wildcard == 1 ? itemPair.getRight().toLowerCase().endsWith(search.toLowerCase()) : itemPair.getRight().toLowerCase().startsWith(search.toLowerCase())) {
	                results.add(itemPair.getLeft());
	            }
    		} else {
    			if (exactSearch ? itemPair.getRight().toLowerCase().equals(search.toLowerCase()) : itemPair.getRight().toLowerCase().contains(search.toLowerCase())) {
	                results.add(itemPair.getLeft());
	            }
    		}
        }
    	List<Integer> filtered = new ArrayList<>();
    	for (Integer id : results) {
    		if (!GameConstants.isBlockedIOC(id) && id >= 1000000)
    			filtered.add(id);
    	}
    	int[] ret = new int[filtered.size()];
    	for(int i = 0;i < ret.length;i++)
    	   ret[i] = filtered.get(i);
    	return ret;
    }

	public boolean createPyramid(String mode, boolean party) throws SQLException {//lol
		PyramidMode mod = PyramidMode.valueOf(mode);

		MapleParty partyz = getPlayer().getParty();
		MapleMapFactory mf = c.getChannelServer().getMapFactory();

		MapleMap map = null;
		int mapid = 926010100;
		if (party) {
			mapid += 10000;
		}
		mapid += (mod.getMode() * 1000);

		for (byte b = 0; b < 5; b++) {//They cannot warp to the next map before the timer ends (:
			map = mf.getMap(mapid + b);
			if (map.getCharacters().size() > 0) {
				continue;
			} else {
				break;
			}
		}

		if (map == null) {
			return false;
		}

		if (!party) {
			partyz = new MapleParty(-1, new MaplePartyCharacter(getPlayer()));
		}
		Pyramid py = new Pyramid(partyz, mod, map.getId());
		getPlayer().setPartyQuest(py);
		py.warp(mapid);
		dispose();
		return true;
	}
}
