/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.events;

import client.MapleClient;
import client.MapleStat;
import client.events.EventHandler2;
import scripting.event.EventHandler;
import client.events.Event;
import java.sql.SQLException;
import tools.MaplePacketCreator;

/**
 *
 * @author Roy
 */
public class EventCommands {
  public static EventHandler2 sqlupdate = new EventHandler2();
    public static boolean isEventOngoing(MapleClient c) {
        return c.getChannelServer().getEvent() != null && !c.getChannelServer().getEvent().isEventOngoing();
    }

    public static boolean isEventCreated(MapleClient c) {
        Event e = c.getChannelServer().getEvent();
        if (e == null) {
            c.getPlayer().dropMessage(6, "An event hasn't been created. please type !event help for further instructions.");
            return false;
        }
        return true;
    }

    public static boolean canCastEventCommand(MapleClient c) {
        Event e = c.getChannelServer().getEvent();
        if (!e.getHost().getPlayer().getName().equals(c.getPlayer().getName())) {
            c.getPlayer().dropMessage(6, "You did not create the event, please speak to " + e.getHost().getPlayer().getName());
            return false;
        }
        return true;
    }

    /* !event new */
    public static boolean createEvent(MapleClient c) {
        if (c.getChannelServer().getEvent() != null) {
            c.getPlayer().dropMessage("An event is already created or running!");
            return false;
        }
        Event e = new Event(c);
        e.updateChannelAcknowledgement();
        c.getPlayer().dropMessage("You've successfully created an event. Please type '!event help' for further instructions.");
        return true;
    }

    /* !event help */
    public static void eventHelp(MapleClient c) {
        c.getPlayer().dropMessage(6, "[MapleInfinity Event Commands System]");
        c.getPlayer().dropMessage(6, "!event new - creates an event");
        c.getPlayer().dropMessage(6, "!event name <name> - sets a name for the event");
        c.getPlayer().dropMessage(6, "!event timer <30, 45, 60, 75, 90> - sets the gate timer to x secondsn (default is 60 seconds)");
        c.getPlayer().dropMessage(6, "!event portal - sets the spawn point at the host's position");
        c.getPlayer().dropMessage(6, "!tk e/d - kills the player when joins event / not.");
        c.getPlayer().dropMessage(6, "!event end - ends the event");
        c.getPlayer().dropMessage(6, "!event kill - kills the event (use in case of bugs, glitches and inform developers)");
    }

    /* !event name */
    public static boolean eventName(MapleClient c, String name) {
        if (!isEventCreated(c) || !canCastEventCommand(c)) {
            return false;
        }
        Event e = c.getChannelServer().getEvent();
        e.setEventName(name);
        c.getPlayer().dropMessage("You've successfully set the event name to: " + name);
        e.updateChannelAcknowledgement();
        return true;
    }

    /* !event point */
    public static boolean setSpawnPoint(MapleClient c) {
        if (!isEventCreated(c) || !canCastEventCommand(c)) {
            return false;
        }
        Event e = c.getChannelServer().getEvent();
        e.setSpawnPoint(c.getPlayer().getPosition());
        c.getPlayer().dropMessage("You've successfully set a new spawn point to this event.");
        e.updateChannelAcknowledgement();
        return true;
    }

    public static boolean setTimer(MapleClient c, int time) {
        if (!isEventCreated(c) || !canCastEventCommand(c)) {
            return false;
        }
        if (time != 45 && time != 60 && time != 75 && time != 90) {
            c.getPlayer().dropMessage("Failed. Read !event help for instructions.");
            return false;
        }
        Event.EVENT_TIMER = time;
        c.getPlayer().dropMessage("You've successfully set the event timer to: " + time + " seconds");
        return true;
    }

    public static boolean eventEnd(MapleClient c) {
        if (!isEventCreated(c) || !canCastEventCommand(c)) {
            return false;
        }
        c.getWorldServer().broadcastPacket(MaplePacketCreator.serverNotice(6, "The event has ended. Thank you for participating!"));
        EventHandler2.endLogUpdate();
        EventHandler2.stopTimer();
        EventHandler2.e = null;
        c.getChannelServer().setEvent(null);
        return true;
    } 
    public static boolean eventStart(MapleClient c) {
        if (!isEventCreated(c) || !canCastEventCommand(c)) {
            return false;
        }
        EventHandler2.e = c.getChannelServer().getEvent();        
        EventHandler2.startEvent(Event.EVENT_TIMER, c);
        EventHandler2.startLogUpdate();
        
        
        
        return true;
    }

    public static boolean nextRound(MapleClient c) {
        if (!isEventCreated(c) || !canCastEventCommand(c)) {
            return false;
        }
        EventHandler2.startEvent(Event.EVENT_TIMER, c);
        return true;
    }

    /* @join || @joinevent */
    public static boolean playerJoin(MapleClient c) throws SQLException {
        if (!isEventOngoing(c)) {
            c.getPlayer().dropMessage(6, "An event is not running currently, please see @lastevent.");
            return false;
        }
        Event e = c.getChannelServer().getEvent();
        if (!e.getGateState()) {
            c.getPlayer().dropMessage("The event gate for " + e.getEventName() + " are currently closed. Please wait for the next round.");
            return false;
        } else {
            c.getPlayer().changeMap(e.getMap(), e.getSpawnPoint());
            c.getPlayer().cancelAllBuffs(false);
            c.getPlayer().dispelDebuffs(true);
            if (e.dieOnJoin()) {
                c.getPlayer().setHp(0);
                c.getPlayer().updateSingleStat(MapleStat.HP, 0);
            }
            c.getPlayer().dropMessage(6, "You have successfully joined " + e.getEventName() + ". Good luck!");
            return true;
        }
    }   
}
