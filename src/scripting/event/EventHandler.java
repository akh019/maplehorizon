/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scripting.event;

import client.MapleCharacter;
import client.MapleClient;
import com.sun.corba.se.spi.orb.StringPair;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ScheduledFuture;
//import jdk.nashorn.internal.parser.TokenType;
import net.server.Server;
import server.TimerManager;
import tools.DatabaseConnection;
import tools.FilePrinter;
import tools.MaplePacketCreator;

/**
 *
 * @author Memory
 */
public class EventHandler {
    
    private transient ScheduledFuture<?> cancelGates; 
    public int gatescounter = 60;
    public boolean alreadyRunning = false;
    public boolean isOpen = false;
    public String host = "";
    public int eventMap = 0;
    public String eventName = "";
    public int channelOn = 0;
    public ArrayList<String> bannedplayers = new ArrayList<>();
    public HashMap<String, Integer> winners = new HashMap<>();
    public String wintodb = "";
    public String[]eventList = new String[5]; 
    public int[]eventListtimes = new int[5];
    public String eventMvp = "";
    public boolean tk = true; // Default - players who @joinevent will die.
    public boolean jt = false; // Default - players cant @jt.
    
    public void setEventMap(int map)
    {
        eventMap = map;
    }
    
    public void setEvent(boolean yes)
    {
     isOpen = yes;   
    }
    
    public boolean isRunning()
    {
        return alreadyRunning;
    }
    
    public void setRunning(boolean ok)
    {
        alreadyRunning = ok;
    }
    
    
public void eBanPlayer(String name)
   {
       bannedplayers.add(name);
       
   }
   public void eUnbanPlayer(String name)
   {
   bannedplayers.remove(name);
   }
   
   public boolean isEventBanned(String name)
   {
       boolean isBan = false;
       
       if (bannedplayers.contains(name))
       {
           isBan = true;
       }
      
       return isBan;
   }
    
    public void openEvent(MapleClient c, String eventname)
    {
       eventName = eventname;
        
        isOpen = true;
                       alreadyRunning = true;
                       channelOn = c.getPlayer().getClient().getChannel();
                        eventMap = c.getPlayer().getMapId();
                     host = c.getPlayer().getName(); // uh 1 sec i forgot how to server ima get food kk
                     
                      int chars = (gatescounter+"").length();
                   
                     
                        Server.getInstance().broadcastMessage(MaplePacketCreator.serverNotice(6, "[Event] " + c.getPlayer().getName() + " is hosting the event " + eventname + " on channel " + channelOn + ". You have " + gatescounter + " seconds to join the event."));
                      cancelGates =  TimerManager.getInstance().register(new Runnable(){
                          public void run()
                                {
                                    gatescounter-= 1;
                                 //   p.dropMessage(gatescounter + "");
                                    if(gatescounter == 45)
                                           Server.getInstance().broadcastMessage(MaplePacketCreator.serverNotice(6, "[Event] There is only " + gatescounter + " seconds remaining till the gates close! Type @joinevent to join " + eventName + " hosted by " + host));
                                    if(gatescounter == 30)
                                           Server.getInstance().broadcastMessage(MaplePacketCreator.serverNotice(6, "[Event] There is only " + gatescounter + " seconds remaining till the gates close! Type @joinevent to join " + eventName + " hosted by " + host));
                                    if(gatescounter == 15)
                                           Server.getInstance().broadcastMessage(MaplePacketCreator.serverNotice(6, "[Event] There is only " + gatescounter + " seconds remaining till the gates close! Type @joinevent to join " + eventName + " hosted by " + host));
                                    if(gatescounter == 0)  {
                                          Server.getInstance().broadcastMessage(MaplePacketCreator.serverNotice(6, "The event gates have been closed!"));
                                       isOpen = false;
                                       gatescounter = 60;
                                       cancelGate();
                                    }
                                    if(!isOpen){
                                    //     Server.getInstance().broadcastMessage(MaplePacketCreator.serverNotice(6, "The event gates have been closed!"));
                                       gatescounter = 60;
                                       cancelGate(); 
                                    }
                                }
                      }, 1000);
                      
       try {
               Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("INSERT INTO eventlog (eventname, hostname, winners) VALUES (?, ?, ?)")) {                               
               ps.setString(1, eventName);
                ps.setString(2, host);
                ps.setString(3, "notdone");               
                ps.execute();
                ps.close();
            }
        } catch (SQLException e) {
            System.out.print("Error inserting eventlog: " + e);
        }         
                      
    }
    public void cancelGate(){
        if(cancelGates != null){
             cancelGates.cancel(false);
            cancelGates = null;
        }
    }
    public void closeEventMessage(String msg)
    {
          Server.getInstance().broadcastMessage(MaplePacketCreator.serverNotice(6, msg));
               winners.clear();
               wintodb = "";
    }
    
    public void closeEvent()
    {
        Calendar calendar = Calendar.getInstance();
                  int hours = calendar.get(Calendar.HOUR);  
                  int minutes = calendar.get(Calendar.MINUTE); 
                   
                  
          for(int i=4; i > 0 ; i--)
                 if(eventList[i] != ""){
                    eventList[i] = eventList[i-1];
                    eventListtimes[i] = eventListtimes[i-1];
                 }
             if(eventMvp.isEmpty())
             eventList[0] = eventName + " hosted by " + host;
            else
               eventList[0] = eventName + " hosted by " + host + ", MVP: " + eventMvp;  
             eventListtimes[0] = hours*60 + minutes;
          
     
         isOpen = false;
                channelOn = 0;
                eventMap = 0;              
                alreadyRunning = false;
                
                
                try {
               Connection con = DatabaseConnection.getConnection();
             //  con.setAutoCommit(false);
            try (PreparedStatement ps = con.prepareStatement("UPDATE eventlog SET winners = ? WHERE winners = 'notdone'")) {
                               if(winners.isEmpty())
                    ps.setString(1, "None");
                else{                   
                              
                  
                 //     sb.substring(0, sb.length()-2);
                 wintodb = wintodb.substring(0,wintodb.length()-2);
                 wintodb+= ".";
                 ps.setString(1,wintodb);
                }              
            //    ps.setInt(5, id);
                ps.executeUpdate();
                ps.close();
           
               
            }
        } catch (SQLException e) {
            System.out.print("Error inserting eventlog: " + e);
        }
                
           
             
    } /*
    public void lastEvent(MapleCharacter player){
      Calendar calendar = Calendar.getInstance(); 
        String msg = ""; 
        int eventhours = 0;
        int eventminutes = 0;
        int disthours = 0;
        int distminutes = 0;
        int yourhours = calendar.get(Calendar.HOUR);  
        int yourminutes = calendar.get(Calendar.MINUTE);        
       
               
            if(!(eventList[0] == null)){
                msg = "The last event was " + eventList[0];
                 eventhours = eventListtimes[0] / 60;           
                 eventminutes = eventListtimes[0] % 60;  
                 if(eventhours > yourhours)
                     yourhours += 12;
                 if(eventminutes > yourminutes){
                     yourminutes += 60;
                     yourhours -= 1;
                 }
                 
                 disthours = yourhours - eventhours;
                 distminutes = yourminutes - eventminutes; 
            
                if(disthours == 0){  
                    if(distminutes == 1)
                         msg+= ", "  + distminutes + " minute ago.";
                    else
                        msg+= ", "  + distminutes + " minutes ago.";
               
                }
                else if(disthours == 1){
                    if(distminutes == 0)
                     msg+= ", " + disthours + " hour ago.";   
                    else if(distminutes == 1)
                        msg+= ", " + disthours + " hour and " + distminutes + " minute ago."; 
                    else
                        msg+= ", " + disthours + " hour and " + distminutes + " minutes ago.";  
                }
                else{
                    if(distminutes == 0)
                     msg+= ", " + disthours + " hours ago."; 
                    else if(distminutes == 1)
                         msg+= ", " + disthours + " hours and " + distminutes + " minute ago."; 
                    else
                          msg+= ", " + disthours + " hours and " + distminutes + " minutes ago.";  
                }
            //    player.dropMessage(6,msg);
            }
            else
                msg = "There hasnt been any events recently.";
           player.dropMessage(6,msg); 
    
    }
    public void eventLog(MapleCharacter player){
        
      Calendar calendar = Calendar.getInstance(); 
        String msg = ""; 
        int eventhours = 0;
        int eventminutes = 0;
        int disthours = 0;
        int distminutes = 0;
        int yourhours = calendar.get(Calendar.HOUR);  
        int yourminutes = calendar.get(Calendar.MINUTE); 
        
        boolean nomorelog = false;
        player.dropMessage(6,"[Event Log]");
        for(int i = 0; i< 5 & !nomorelog; i++){
            if(!eventList[i].equals("")){
                msg = i+1 + ". " + eventList[i];
                 eventhours = eventListtimes[i] / 60;
             //      player.dropMessage(6,i + "");
             //    player.dropMessage(6,eventhours + "event hours");
             //     player.dropMessage(6,eventminutes + "event minutes");
                 eventminutes = eventListtimes[i] % 60;  
                 if(eventhours > yourhours)
                     yourhours += 12;
                 if(eventminutes > yourminutes){
                     yourminutes += 60;
                     yourhours -= 1;
                 }
                 
                 disthours = yourhours - eventhours;
                 distminutes = yourminutes - eventminutes; 
             //      player.dropMessage(6,disthours + "dist hours");
             //     player.dropMessage(6,distminutes + "dist minutes");
                if(disthours == 0){  
                    if(distminutes == 1)
                         msg+= ", "  + distminutes + " minute ago.";
                    else
                        msg+= ", "  + distminutes + " minutes ago.";
               
                }
                else if(disthours == 1){
                    if(distminutes == 0)
                     msg+= ", " + disthours + " hour ago.";   
                    else if(distminutes == 1)
                        msg+= ", " + disthours + " hour and " + distminutes + " minute ago."; 
                    else
                        msg+= ", " + disthours + " hour and " + distminutes + " minutes ago.";  
                }
                else{
                    if(distminutes == 0)
                     msg+= ", " + disthours + " hours ago."; 
                    else if(distminutes == 1)
                         msg+= ", " + disthours + " hours and " + distminutes + " minute ago."; 
                    else
                          msg+= ", " + disthours + " hours and " + distminutes + " minutes ago.";  
                }
                player.dropMessage(6,msg);
            }
            else
                nomorelog = true;
            msg = "";
        }
        
        
    } */

}
