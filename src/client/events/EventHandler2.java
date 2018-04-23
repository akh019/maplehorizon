/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client.events;

import client.MapleCharacter;
import client.MapleClient;
import client.events.Event;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import net.server.Server;
import server.TimerManager;
import tools.DatabaseConnection;
import tools.FilePrinter;
import tools.MaplePacketCreator;

/**
 *
 * @author Roy
 */
public class EventHandler2 {

    public static Event e;
    public static ScheduledFuture<?> cancelGates;
    
    public static void startEvent(final int timeLeft, final MapleClient host) {
        if (cancelGates == null) {
            e.setRound(e.getRound() + 1);
            e.changeGateState();
            host.getWorldServer().broadcastPacket(MaplePacketCreator.serverNotice(6, eventMessage(timeLeft, e.getRound())));
            cancelGates = TimerManager.getInstance().register(new Runnable() {
                int time = timeLeft - 15;

                @Override
                public void run() {
                    if (time <= 0) {
                        host.getWorldServer().broadcastPacket(MaplePacketCreator.serverNotice(6, "The event gate is now closed. Please wait for the next round."));
                        e.changeGateState();
                        cancelGates.cancel(true);
                        cancelGates = null;
                    } else {
                        host.getWorldServer().broadcastPacket(MaplePacketCreator.serverNotice(6, eventMessage(time, e.getRound())));
                        time -= 15;
                    }
                }
            }, 1000 * 15, 1000 * 15);
        } else {
            host.getPlayer().dropMessage(6, "[Event] Wait for the current timer instance to finish!");
        }
      //  startLogUpdate();
        
    }

    public static void stopTimer() {
        if (cancelGates != null) {
            cancelGates.cancel(true);
        }
        cancelGates = null;
    }
    public static void startLogUpdate(){
       
         try {
               Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("INSERT INTO eventlog (eventname, hostname, winners, `when`) VALUES (?, ?, ?, ?)")) {                               
               Timestamp ts = new Timestamp(System.currentTimeMillis()); 
                ps.setString(1, e.getEventName());
                ps.setString(2, e.getHost().getPlayer().getName());
                ps.setString(3, "notdone");   
                ps.setTimestamp(4, ts);
                ps.execute();
                ps.close();
            }
        } catch (SQLException e) {
            System.out.print("Error inserting eventlog: " + e);
        } 
    } public static void endLogUpdate(){    
                try {
               Connection con = DatabaseConnection.getConnection();
             //  con.setAutoCommit(false);
            try (PreparedStatement ps = con.prepareStatement("UPDATE eventlog SET winners = ?, `when` = ? WHERE winners = ?")) {
                Timestamp ts = new Timestamp(System.currentTimeMillis());             
                ps.setString(1, "None"); // Until we have an insert winner method.  
                ps.setTimestamp(2,ts);
                ps.setString(3, "notdone");
                ps.executeUpdate();
                ps.close();
           
               
            }
        } catch (SQLException e) {
            System.out.print("Error inserting eventlog: " + e);
        }
        
    }
    public static String eventMessage(int time, int round) {
        return "A(n) " + e.getEventName() + " event" + ((round != 1) ? " round " + round : "") + " has started on Channel " + e.getChannel() + " by GM " + e.getHost().getPlayer().getName() + ". You have " + time + " seconds left to @join.";
    }
    public void lastEvent(MapleCharacter player){     
       
        String howlongago = "", lastevent = "The last event was ";
            ResultSet rs;
            try { 
                        PreparedStatement ps;
                        Connection con = DatabaseConnection.getConnection();
                        ps = con.prepareStatement("SELECT * FROM eventlog ORDER BY id DESC LIMIT 1");
                        rs = ps.executeQuery();
                        while (rs.next()) {
                          howlongago = calculatedTime(rs.getTimestamp("when").getTime());  
                          lastevent+= rs.getString("eventname") + " hosted by " + rs.getString("hostname") + ", " + howlongago;
                           
                        }
                    } catch (SQLException exc) {
                        System.out.print("Problem with getting lastevent:" + exc);
                    }
            if(!howlongago.equals(""))
           player.dropMessage(6,lastevent);
            else
                player.dropMessage(6,"There hasnt been any events recently.");
    }
    public void eventLog(MapleCharacter player, int limit){
        List<String>lastevents = new ArrayList<>();
        int counter = 1;
        String howlongago = "";
            ResultSet rs;
            try { 
                        PreparedStatement ps;
                        Connection con = DatabaseConnection.getConnection();
                        ps = con.prepareStatement("SELECT * FROM eventlog ORDER BY id DESC LIMIT " + limit);
                        rs = ps.executeQuery();
                        while (rs.next()) {
                          howlongago = calculatedTime(rs.getTimestamp("when").getTime());  
                          lastevents.add(counter + ". " + rs.getString("eventname") + " was hosted by " + rs.getString("hostname") + ", " + howlongago);
                          counter++; 
                        }
                    } catch (SQLException exc) {
                        System.out.print("Problem with getting eventlog:" + exc);
                    }
            player.dropMessage(6,"[Event Log]");
            for(int i = 0; i < lastevents.size();i++)
               player.dropMessage(6,lastevents.get(i)); 
    }
    public String calculatedTime(long millis){
        long event = millis;
        long current = new Timestamp(System.currentTimeMillis()).getTime();
        long dtime = current - event;
        String time = "",cut1="",cut2="";
        int days= 0,hours = 0,minutes = 0,seconds = 0,lastcomma = 0;
        
        while(dtime >= 86400000){ // Checking for days.
            days++;
            dtime-= 86400000;
        }
        while(dtime >= 3600000){ // Checking for hours.
            hours++;
            dtime-= 3600000;
        }
        while(dtime >= 60000){ // Checking for minutes.
            minutes++;
            dtime-= 60000;
        }
        while(dtime >= 1000){
            seconds++;
            dtime -= 1000;
        }
        //  time = days + " day(s), " + hours + " hour(s), " + minutes + " minute(s) and " + seconds + " second(s) ago.";
        time = days + " days, " + hours + " hours, " + minutes + " minutes, " + seconds + " seconds, ";
       
        switch(days){
            case 0:
                time = time.replace(days + " days, ", "");
                break;                 
             case 1:
                 time = time.replace("days","day");
                break;
                 
        }
         
        switch(hours){
            case 0:
                time = time.replace(hours + " hours, ", "");
                break;                 
             case 1:
                 time = time.replace("hours","hour");
                break;
                 
        }        
        switch(minutes){
            case 0:
                time = time.replace(minutes + " minutes, ", "");
                break;                 
             case 1:
                 time = time.replace("minutes","minute");
                break;
                 
        }
         switch(seconds){
            case 0:
                time = time.replace(seconds + " seconds, ", "");
                break;                 
             case 1:
                 time = time.replace("seconds","second");
                break;
                 
        }
         time = time.substring(0, time.length()-2);
         if(time.contains(",")){
         lastcomma = time.lastIndexOf(",");
         
         cut1 = time.substring(0, lastcomma);
         cut2 = time.substring(lastcomma+1);
         time = cut1 + " and" + cut2;  
         }
         time += " ago.";
                   
        return time;
        
                
    }
}
