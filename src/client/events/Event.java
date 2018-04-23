 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.events;

import client.MapleCharacter;
import client.MapleClient;
import constants.ServerConstants;
import java.awt.Point;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import scripting.event.EventHandler;
import server.TimerManager;
import server.maps.MapleMap;
import tools.MaplePacketCreator;

/**
 *
 * @author Administrator
 */
public class Event {

    private String eventName;
    private MapleClient host;
    private int channel = 1;
    private long startTime;
    private Point spawnPoint = null;
    private boolean ongoing;
    private boolean gateOpen;
    private boolean warpDead = true;
    private boolean jtOpen = false;
    private int round;
   /// private int[]eventListtimes = new int[5];
  //  private String[]eventList = new String[5];
    private MapleMap map;
    private int jobChangeTo = 0; // beginner
    public HashMap<String, Boolean> participants = new HashMap<String, Boolean>();
    public HashMap<String, Integer> winners = new HashMap<String, Integer>();
    public List<String> roundWinners = new ArrayList<>();
    private Scoreboard scoreboard;
    
    public static int EVENT_TIMER = 90;

    public Event(MapleClient host) {
        this.host = host;
        this.map = host.getPlayer().getMap();
        this.channel = host.getChannel();
        this.spawnPoint = host.getPlayer().getPosition();
        this.warpDead = true;
        this.round = 0;
        this.scoreboard = new Scoreboard();
    }
    
    public Scoreboard getScoreboard() {
        return this.scoreboard;
    }
    
    public void updateChannelAcknowledgement() {
        host.getChannelServer().setEvent(this);
    }
    
    public void setEventName(String name) {
        this.eventName = name;
    }
    
    public void setSpawnPoint(Point p) {
        this.spawnPoint = p;
    }
    
    public void changeSpawnState() {
        this.warpDead = !this.warpDead;
    }
    
    public void changeGateState() {
        this.gateOpen = !this.gateOpen;
    }
    
    public void setStartTime(long start) {
        this.startTime = start;
    }
    
    public String getEventName() {
        return this.eventName;
    }
    
    public Point getSpawnPoint() {
        return this.spawnPoint;
    }
    
    public boolean getSpawnState() {
        return this.warpDead;
    }
    
    public boolean getGateState() {
        return this.gateOpen;
    }
    
    public int getChannel() {
        return this.channel;
    }
    
    public long getStartTime() {
        return this.startTime;
    }
    
    public boolean isEventOngoing() {
        return this.ongoing;
    }
    
    public MapleClient getHost() {
        return this.host;
    }
    
    public MapleMap getMap() {
        return this.map;
    }
    
    public boolean dieOnJoin() {
        return this.warpDead;
    }
    public boolean canJt(){
        return this.jtOpen;
    }
    public void setDeathonJoin(boolean yes){
        this.warpDead = yes;
    }
    public void setJt(boolean on){
        this.jtOpen = on;
    }
    public int getRound() {
        return this.round;
    }
    
    public void setRound(int round) {
        this.round = round;
    } /*
    public String[] getLogInfo(){
        return this.eventList;
    }
     public int[] getLogTimes(){
        return this.eventListtimes;
    }
     public void setLogInfo(String[] newlist){
        eventList = newlist;
    }
     public void setLogTimes(int[] newtimes){
        eventListtimes = newtimes;
    }
     */
    private boolean canStartEvent() {
        if (host == null
                || map == null
                || ongoing
                || channel < 1)
            return false;
        return true;
    }
    
    public void broadcastInfoToHost() {
        MapleCharacter mObject = host.getPlayer();
        if(mObject != null) {
            mObject.dropMessage(6, "[" + this.eventName + " hosted by " + mObject.getName() + "]");
            mObject.dropMessage(6, "Hosted on channel: " + this.channel);
            mObject.dropMessage(6, "Warp dead state: " + (this.warpDead ? "warping dead" : "warping alive"));
            mObject.dropMessage(6, "Event spawn point: (" + spawnPoint.x + ", " + spawnPoint.y + ")");
            mObject.dropMessage(6, "Can launch event?" + (ongoing ? "event is already ongoing" : (canStartEvent() ? "Yes" : "Something went wrong, please recreate your event.")));
        }
        else System.out.println("What?");
    }
}