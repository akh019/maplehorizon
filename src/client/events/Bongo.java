/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.events;

import client.MapleCharacter;
import client.MapleClient;
import client.MapleDisease;
import constants.ServerConstants;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ScheduledFuture;
import server.TimerManager;
import server.life.MapleLifeFactory;
import server.life.MobSkillFactory;
import server.maps.MapleMap;
import tools.MaplePacketCreator;

/**
 *
 * @author Roy
 */
public class Bongo {
    
    public enum BongoType {
        BONGO,
        REVERSE_BONGO
    }
    
    private MapleMap map;
    
    public static ScheduledFuture<?> bongoTimer;
    public static ScheduledFuture<?> bongoInterval;
    public static ScheduledFuture<?> victimTimer;
    
    public static final int MAX_NUM = 3;
    
    private MapleCharacter chosen;
    private boolean chosenRight = false;
    
    public Bongo(MapleMap map) {
        this.map = map;
    }
    
    public MapleMap getMap() {
        return this.map;
    }
    
    public MapleCharacter getChosen() { return chosen; }
    public void setChosen(MapleCharacter chosen) { this.chosen = chosen; }
    
    public boolean chosenRight() { return this.chosenRight; }
    public void changeRightState() { this.chosenRight = !this.chosenRight; }
    
    public static void startBongo(MapleMap map, int stage) {
        if (stage == 1) {
            map.setMuted(true);
            map.broadcastMessage(MaplePacketCreator.sendYellowTip("[Bongo Rules] Welcome to Bongo! The Bongo game will take place only on the 'O' platforms, if you happen to fall, you lose! You will now have "
                + "30 seconds to pick a platform, make sure you do because after 30 seconds autokill will be turned on and you will be stunned aswell, a GM will not rewarp you." 
            + " Follow the event insturctions to avoid further problems, make sure if you choose the correct number, name a player who's participating the event and is alive. Good luck!"));
            map.broadcastMessage(MaplePacketCreator.getClock(30));
        }
        if (stage == 2) {
            map.broadcastMessage(MaplePacketCreator.serverNotice(6, "[Bongo] Mind that if you pick the randomized number, you will get to pick a player to bomb."));
            map.broadcastMessage(MaplePacketCreator.serverNotice(6, "[Bongo] Let the bongo games begin in 15 seconds! Good luck!"));
            map.setBongo(new Bongo(map));
        }
        final MapleMap m = map;
        final int st = stage;
        bongoTimer = TimerManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                if(st == 1) {
                    stunMap(m);
                    AKStatus(m);
                    m.setMuted(false);
                    startBongo(m, 2);
                }
                if(st == 2) {
                    m.broadcastMessage(MaplePacketCreator.getClock(15));
                    initiateBongoInterval(m);
                }
            }
        }, ((stage == 1) ? 30000 : 1));
    }
    
    public static void getRandInt(MapleMap map) {
        Random rand = new Random();
        int randint = rand.nextInt((MAX_NUM -1) + 1) + 1;
        ServerConstants.bongoAnswer = randint;
    }
    
    public static void initiateBongoInterval(final MapleMap map) {
        if(bongoInterval != null) {
            bongoInterval.cancel(true);
            bongoInterval = null;
        }
        bongoInterval = TimerManager.getInstance().register(new Runnable() {
            @Override
            public void run() {
                if(getAliveCount(map) <= 1) announceWinner(map, getLastAlive(map));
                else {
                    map.getBongo().setChosen(Bongo.randomIGNAlive(map));
                }
            }
        }, 15000, 15000);
    }
    
    public static void announceWinner(MapleMap map, MapleCharacter winner) {
        map.setBongo(null);
        if(winner != null)
            map.broadcastMessage(MaplePacketCreator.serverNotice(6, "[Bongo] Congratulations to " + winner.getName() + " for winning the bongo event. Please wait for a GM for further instructions."));
        else
            map.broadcastMessage(MaplePacketCreator.serverNotice(6, "[Bongo]] No one has won this round, unfortunately, noobs."));
        bongoInterval.cancel(true);
        for (MapleCharacter a1 : map.getCharacters()) {
                a1.dispelDebuffs(true);
            }
        for (MapleCharacter chrs : map.getCharacters()) {
                chrs.setHpMp(30000);
            }
        map.setAutoKill(false);
        
    }
    
    public static void chooseVictimTimer(MapleMap map) {
        map.broadcastMessage(MaplePacketCreator.getClock(10));
        final MapleMap m = map;
        victimTimer = TimerManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                if(getAliveCount(m) <= 1) announceWinner(m, getLastAlive(m));
                else {
                    m.getBongo().changeRightState();
                            m.broadcastMessage(MaplePacketCreator.serverNotice(6, "[Bongo] You failed to type a correct ign in time, moving along in 3 seconds!"));
                            Bongo.initiateBongoInterval(m);
                            Bongo.getRandInt(m);
                        TimerManager.getInstance().schedule(new Runnable() {
                                @Override
                                public void run() {
                                     m.getBongo().setChosen(Bongo.randomIGNAlive(m));
                                }
                            }, 3000);
                }
            }
        }, 10000);
    }
    
    public static MapleCharacter randomIGNAlive(MapleMap map) {
        if(getAliveCount(map) <= 1) announceWinner(map, getLastAlive(map));
        if(map.getBongo() == null) {
            bongoTimer.cancel(true);
            victimTimer.cancel(true);
            
            return null;
        }
        Bongo.getRandInt(map); Bongo.getRandInt(map); Bongo.getRandInt(map); Bongo.getRandInt(map); Bongo.getRandInt(map);
        if(bongoInterval.isCancelled()) initiateBongoInterval(map);
        map.broadcastMessage(MaplePacketCreator.getClock(15));
        Random rand = new Random();            
        Collection<MapleCharacter> chars = map.getCharacters();
        List<MapleCharacter> charlist = new ArrayList<>();
        for(MapleCharacter a1 : chars)
            if(/*!a1.isGM() &&*/ a1.isAlive())
                charlist.add(a1);
        
        if(charlist.size() > 0) {
            int randnum = rand.nextInt(charlist.size());
            for(MapleCharacter mc : map.getCharacters()) {
                if(mc.getName().equals(charlist.get(randnum).getName()))
                    mc.yellowMessage("[Bongo] You were chosen! Pick a number between 1-3.");
                else mc.dropMessage(6, "[Bongo] Random player chosen: " + charlist.get(randnum).getName() + ". Note that you have 15 seconds! (type a number 1-3).");
            }
            return charlist.get(randnum);
        }
        
        bongoInterval.cancel(true);
        return null;
    }
    
    public static void bombRandom(MapleMap map, MapleCharacter random) {
        random.getMap().broadcastMessage(MaplePacketCreator.serverNotice(6, "[Bongo] Say bye bye " + random.getName()));
        map.spawnBombOnGroundBelow(MapleLifeFactory.getMonster(9300166), random.getPosition());
    }
    
    public static boolean bombChosen(MapleClient c, String chosen) {
        c.getPlayer().getMap().getBongo().changeRightState();
        MapleCharacter victim;
        if(chosen.equals("r")) victim = getRandomAlive(c.getPlayer().getMap());
        else victim = c.getChannelServer().getPlayerStorage().getCharacterByName(chosen);
        if(victim == null ||/* victim.isGM() || */!victim.isAlive()) return false;
        for (MapleCharacter cr : c.getPlayer().getMap().getCharacters()) {
            c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.getChatText(cr.getId(), "YOU WERE CHOSEN " + victim.getName() + ", YOU WERE CHOSEN, NOW BOMB US, THE CHOSEN ONE!", false, 1));
        }
        victim.getMap().spawnBombOnGroundBelow(MapleLifeFactory.getMonster(9300166), victim.getPosition());
        String addon = chosen.equals("r") ? "You were randomly chosen to die, you are very unlucky, " + victim.getName() + "." : "You were chosen to go to Allah, " + chosen + ", by " + c.getPlayer().getName() + ".";
        victim.getMap().broadcastMessage(MaplePacketCreator.serverNotice(6, "[Bongo] " + addon + " Starting again in 10 seconds."));
        return true;
    }
    
    public static int getAliveCount(MapleMap map) {
        Collection<MapleCharacter> chars = map.getCharacters();
        List<MapleCharacter> charlist = new ArrayList<>();
        for(MapleCharacter a1 : chars)
            if(/*!a1.isGM() &&*/ a1.isAlive())
                charlist.add(a1); 
        
        return charlist.size();
    }
    
    public static MapleCharacter getLastAlive(MapleMap map) {
        Collection<MapleCharacter> chars = map.getCharacters();
        List<MapleCharacter> charlist = new ArrayList<>();
        for(MapleCharacter a1 : chars)
            if(/*!a1.isGM() &&*/ a1.isAlive())
                charlist.add(a1); 
        
        return (charlist.isEmpty()) ? null : charlist.get(0);
    }
    
    public static MapleCharacter getRandomAlive(MapleMap map) {
        Collection<MapleCharacter> chars = map.getCharacters();
        List<MapleCharacter> charlist = new ArrayList<>();
        for(MapleCharacter a1 : chars)
            if(/*!a1.isGM() &&*/ a1.isAlive())
                charlist.add(a1); 
        
        return charlist.get(new Random().nextInt(charlist.size()));
    }
    
    public static void stunMap(MapleMap map) {
        for (MapleCharacter victim : map.getCharacters()) {
                if (victim != null && victim.gmLevel() < 3) {
                    victim.setChair(0);
                    victim.getClient().getSession().write(MaplePacketCreator.cancelChair(-1));
                    victim.getMap().broadcastMessage(victim, MaplePacketCreator.showChair(victim.getId(), 0), false);
                    victim.givePermDebuff(MapleDisease.STUN, MobSkillFactory.getMobSkill(123, 1));
                }
            }
    }
    
    public static void AKStatus(MapleMap map) {
        if(!map.getAutoKill()) {
            map.setAKPositionB(274);
            map.setAutoKill(true);
        } else {
            map.setAutoKill(false);
        }
    }
    
    public static String participantsToString(MapleCharacter c) {
        MapleMap map = c.getMap();
        Collection<MapleCharacter> chars = map.getCharacters();
        List<MapleCharacter> charlist = new ArrayList<>();
        String x = "Participants: ";
        for(MapleCharacter a1 : chars)
            if(/*!a1.isGM() &&*/ a1.isAlive())
                x += MapleCharacter.makeMapleReadable(a1.getName()) + ", ";
        
        return x;
    }
}
