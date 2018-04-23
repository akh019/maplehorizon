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
package server.maps;

import bots.BernardManager;
import client.BuddyList;
import client.BuddylistEntry;
import client.MapleBuffStat;
import client.MapleCharacter;
import client.MapleClient;
import client.MapleDisease;
import client.MapleStat;
import client.autoban.AutobanFactory;
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import client.inventory.MaplePet;
import client.status.MonsterStatus;
import client.status.MonsterStatusEffect;
import constants.ItemConstants;
import constants.ServerConstants;
import java.awt.Point;
import java.awt.Rectangle;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;
import net.server.Server;
import net.server.channel.Channel;
import scripting.map.MapScriptManager;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.MaplePortal;
import server.MapleStatEffect;
import server.MapleTrade;
import server.TimerManager;
import server.events.gm.MapleCoconut;
import server.events.gm.MapleFitness;
import server.events.gm.MapleOla;
import server.events.gm.MapleOxQuiz;
import server.events.gm.MapleSnowball;
import server.life.MapleLifeFactory;
import server.life.MapleLifeFactory.selfDestruction;
import server.life.MapleMonster;
import server.life.MapleMonsterInformationProvider;
import server.life.MapleNPC;
import server.life.MobSkillFactory;
import server.life.MonsterDropEntry;
import server.life.MonsterGlobalDropEntry;
import server.life.SpawnPoint;
import server.partyquest.MonsterCarnival;
import server.partyquest.MonsterCarnivalParty;
import server.partyquest.Pyramid;
import tools.FilePrinter;
import tools.MaplePacketCreator;
import tools.Pair;
import tools.Randomizer;
import client.MapleAchievementType;
import client.events.Bongo;
import java.util.logging.Level;
import java.util.logging.Logger;
import scripting.event.OlympicsHandler;

public class MapleMap {

    private static final List<MapleMapObjectType> rangedMapobjectTypes = Arrays.asList(MapleMapObjectType.SHOP, MapleMapObjectType.ITEM, MapleMapObjectType.NPC, MapleMapObjectType.MONSTER, MapleMapObjectType.DOOR, MapleMapObjectType.SUMMON, MapleMapObjectType.REACTOR);
    private Map<Integer, MapleMapObject> mapobjects = new LinkedHashMap<>();
    private Collection<SpawnPoint> monsterSpawn = Collections.synchronizedList(new LinkedList<SpawnPoint>());
    private AtomicInteger spawnedMonstersOnMap = new AtomicInteger(0);
    private Collection<MapleCharacter> characters = new LinkedHashSet<>();
    private Map<Integer, MaplePortal> portals = new HashMap<>();

    private transient ScheduledFuture<?> cancelBomb;
    private transient ScheduledFuture<?> cancelFTJ;
    private transient ScheduledFuture<?> cancelinnerFTJ;
    private transient ScheduledFuture<?> cancelElimDoom;
    private transient ScheduledFuture<?> cancelimpbob;
    private transient ScheduledFuture<?> cancelDelay;
    private Map<Integer, Integer> backgroundTypes = new HashMap<>();
    private List<Rectangle> areas = new ArrayList<>();
    private MapleFootholdTree footholds = null;
    private int mapid;
    private int partylimit= 6;
    private boolean isplimit = false;
    private AtomicInteger runningOid = new AtomicInteger(100);
    private int returnMapId;
    private int channel, world;
    private int ptstowin = 0;
    private byte monsterRate;   
    private boolean chalkpointsOn = false;
    private boolean chalkAllowed = true;
    private boolean chalkClosable = true;
    private boolean taggerOn = false;
    private boolean aggroOn = false;
    private boolean elimdoomOn = false;
    private boolean ftjOn = false;
    private boolean canstartFTJon = false;
    private boolean jeweldied = false;
    private boolean bombermapOn = false;
    private boolean taggermapOn = false;
    //   private boolean fixbuggedOn = true;
    private boolean hitmanOn = false;
    //   private boolean revplayerafterOn = false;
    private boolean ntiOn = false;
    private boolean blinkOn = false;
    private boolean cblinkOn = false;
    private boolean unscrambleOn = false;
    private boolean miniunscrambleOn = false;
    private boolean compunscrambleOn = false;
    private boolean speedtypeOn = false;
    private boolean scatOn = false;
    private boolean mobkillOn = false;
    private boolean bombkillOn = false;
    private boolean chosentagger = false;
    private boolean clock;
    private boolean boat;
    private boolean docked;
    private MapleCharacter scatGuy;
    private List<MapleNPC> tempnpcs = new ArrayList<>();
    private List<MapleCharacter> bernards = new ArrayList<>();
    private BuddyList[] savedbls;
    private String[] cabt; // Chararacters Affected By Tagger
    private String mapName;
    private String miniunscramhost = "", compunscramhost = "";
    
    private int akPositionA = -1;
    private int akPositionB = -1;
    private boolean autoKill;
    private double tagRange;
            
    private String streetName;
    private MapleMapEffect mapEffect = null;
    private boolean everlast = false;
    private int forcedReturnMap = 999999999;
    private long timeLimit;
    private int decHP = 0;
    private int protectItem = 0;
    private int wantedobjid = 0;
    private boolean town;
    private MapleOxQuiz ox;
    private boolean isOxQuiz = false;
    private boolean dropsOn = true;
    private String onFirstUserEnter;
    private String onUserEnter;
    private String tagger;
    private MapleCharacter closest;
    private int fieldType;
    private int fieldLimit = 0;
    private int mobCapacity = -1;
    private ScheduledFuture<?> mapMonitor = null;
    private Pair<Integer, String> timeMob = null;
    private short mobInterval = 5000;
    private boolean allowSummons = true; // All maps should have this true at the beginning
    // HPQ
    private int riceCakes = 0;
    private int bunnyDamage = 0;
    // events
    private boolean eventstarted = false, isMuted = false;
    private MapleSnowball snowball0 = null;
    private MapleSnowball snowball1 = null;
    private MapleCoconut coconut;
    //locks
    private final ReadLock chrRLock;
    private final WriteLock chrWLock;
    private final ReadLock objectRLock;
    private final WriteLock objectWLock;
    // olympics
    private boolean olympics = false;
    private List<MapleCharacter> teamRed;
    private List<MapleCharacter> teamBlue;
    
    // bongo
    private Bongo bongo;

    public MapleMap(int mapid, int world, int channel, int returnMapId, float monsterRate) {
        this.mapid = mapid;
        this.channel = channel;
        this.world = world;
        this.returnMapId = returnMapId;
        this.monsterRate = (byte) Math.ceil(monsterRate);
        if (this.monsterRate == 0) {
            this.monsterRate = 1;
        }
        final ReentrantReadWriteLock chrLock = new ReentrantReadWriteLock(true);
        chrRLock = chrLock.readLock();
        chrWLock = chrLock.writeLock();

        final ReentrantReadWriteLock objectLock = new ReentrantReadWriteLock(true);
        objectRLock = objectLock.readLock();
        objectWLock = objectLock.writeLock();
    }

    public ReadLock getCharacterReadLock() {
        return chrRLock;
    }

    public WriteLock getCharacterWriteLock() {
        return chrWLock;
    }

    public void broadcastMessage(MapleCharacter source, final byte[] packet) {
        chrRLock.lock();
        try {
            for (MapleCharacter chr : characters) {
                if (chr != source) {
                    chr.getClient().announce(packet);
                }
            }
        } finally {
            chrRLock.unlock();
        }
    }

    public void broadcastGMMessage(MapleCharacter source, final byte[] packet) {
        chrRLock.lock();
        try {
            for (MapleCharacter chr : characters) {
                if (chr != source && (chr.gmLevel() > source.gmLevel())) {
                    chr.getClient().announce(packet);
                }
            }
        } finally {
            chrRLock.unlock();
        }
    }

    public void toggleDrops() {
        this.dropsOn = !dropsOn;
    }

    public List<MapleMapObject> getMapObjectsInRect(Rectangle box, List<MapleMapObjectType> types) {
        objectRLock.lock();
        final List<MapleMapObject> ret = new LinkedList<>();
        try {
            for (MapleMapObject l : mapobjects.values()) {
                if (types.contains(l.getType())) {
                    if (box.contains(l.getPosition())) {
                        ret.add(l);
                    }
                }
            }
        } finally {
            objectRLock.unlock();
        }
        return ret;
    }

    public int getId() {
        return mapid;
    }

    public MapleMap getReturnMap() throws SQLException {
        return Server.getInstance().getWorld(world).getChannel(channel).getMapFactory().getMap(returnMapId);
    }

    public int getReturnMapId() {
        return returnMapId;
    }

    public void setReactorState() {
        objectRLock.lock();
        try {
            for (MapleMapObject o : mapobjects.values()) {
                if (o.getType() == MapleMapObjectType.REACTOR) {
                    if (((MapleReactor) o).getState() < 1) {
                        ((MapleReactor) o).setState((byte) 1);
                        broadcastMessage(MaplePacketCreator.triggerReactor((MapleReactor) o, 1));
                    }
                }
            }
        } finally {
            objectRLock.unlock();
        }
    }

    public int getForcedReturnId() {
        return forcedReturnMap;
    }

    public MapleMap getForcedReturnMap() throws SQLException {
        return Server.getInstance().getWorld(world).getChannel(channel).getMapFactory().getMap(forcedReturnMap);
    }

    public void setForcedReturnMap(int map) {
        this.forcedReturnMap = map;
    }

    public long getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    public int getTimeLeft() {
        return (int) ((timeLimit - System.currentTimeMillis()) / 1000);
    }

    public int getCurrentPartyId() {
        for (MapleCharacter chr : this.getCharacters()) {
            if (chr.getPartyId() != -1) {
                return chr.getPartyId();
            }
        }
        return -1;
    }

    public void addMapObject(MapleMapObject mapobject) {
        objectWLock.lock();
        try {
            int curOID = getUsableOID();
            mapobject.setObjectId(curOID);
            this.mapobjects.put(curOID, mapobject);
        } finally {
            objectWLock.unlock();
        }
    }

    private void spawnAndAddRangedMapObject(MapleMapObject mapobject, DelayedPacketCreation packetbakery) {
        spawnAndAddRangedMapObject(mapobject, packetbakery, null);
    }

    private void spawnAndAddRangedMapObject(MapleMapObject mapobject, DelayedPacketCreation packetbakery, SpawnCondition condition) {
        chrRLock.lock();
        objectWLock.lock();
        try {
            int curOID = getUsableOID();
            mapobject.setObjectId(curOID);
            this.mapobjects.put(curOID, mapobject);
            for (MapleCharacter chr : characters) {
                if (condition == null || condition.canSpawn(chr)) {
                    if (chr.getPosition().distanceSq(mapobject.getPosition()) <= 722500) {
                        packetbakery.sendPackets(chr.getClient());
                        chr.addVisibleMapObject(mapobject);
                    }
                }
            }
        } finally {
            chrRLock.unlock();
            objectWLock.unlock();
        }
    }

    private int getUsableOID() {
        if (runningOid.incrementAndGet() > 2000000000) {
            runningOid.set(1000);
        }
        objectRLock.lock();
        try {
            if (mapobjects.containsKey(runningOid.get())) {
                while (mapobjects.containsKey(runningOid.incrementAndGet()));
            }
        } finally {
            objectRLock.unlock();
        }

        return runningOid.get();
    }

    public void removeMapObject(int num) {
        objectWLock.lock();
        try {
            this.mapobjects.remove(Integer.valueOf(num));
        } finally {
            objectWLock.unlock();
        }
    }

    public void removeMapObject(final MapleMapObject obj) {
        removeMapObject(obj.getObjectId());
    }

    private Point calcPointBelow(Point initial) {
        MapleFoothold fh = footholds.findBelow(initial);
        if (fh == null) {
            return null;
        }
        int dropY = fh.getY1();
        if (!fh.isWall() && fh.getY1() != fh.getY2()) {
            double s1 = Math.abs(fh.getY2() - fh.getY1());
            double s2 = Math.abs(fh.getX2() - fh.getX1());
            double s5 = Math.cos(Math.atan(s2 / s1)) * (Math.abs(initial.x - fh.getX1()) / Math.cos(Math.atan(s1 / s2)));
            if (fh.getY2() < fh.getY1()) {
                dropY = fh.getY1() - (int) s5;
            } else {
                dropY = fh.getY1() + (int) s5;
            }
        }
        return new Point(initial.x, dropY);
    }

    public Point calcDropPos(Point initial, Point fallback) {
        Point ret = calcPointBelow(new Point(initial.x, initial.y - 85));
        if (ret == null) {
            return fallback;
        }
        return ret;
    }

    private void dropFromMonster(final MapleCharacter chr, final MapleMonster mob) {
        if (mob.dropsDisabled() || !dropsOn) {
            return;
        }
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        final byte droptype = (byte) (mob.getStats().isExplosiveReward() ? 3 : mob.getStats().isFfaLoot() ? 2 : chr.getParty() != null ? 1 : 0);
        final int mobpos = mob.getPosition().x;
        int chServerrate = chr.getDropRate();
        Item idrop;
        byte d = 1;
        Point pos = new Point(0, mob.getPosition().y);

        Map<MonsterStatus, MonsterStatusEffect> stati = mob.getStati();
        if (stati.containsKey(MonsterStatus.SHOWDOWN)) {
            chServerrate *= (stati.get(MonsterStatus.SHOWDOWN).getStati().get(MonsterStatus.SHOWDOWN).doubleValue() / 100.0 + 1.0);
        }

        final MapleMonsterInformationProvider mi = MapleMonsterInformationProvider.getInstance();
        final List<MonsterDropEntry> dropEntry = new ArrayList<>(mi.retrieveDrop(mob.getId()));
       
        Collections.shuffle(dropEntry);
        for (final MonsterDropEntry de : dropEntry) {            
            if (Randomizer.nextInt(999999) < de.chance * chServerrate) {
                if (droptype == 3) {
                    pos.x = (int) (mobpos + (d % 2 == 0 ? (40 * (d + 1) / 2) : -(40 * (d / 2))));
                } else {
                    pos.x = (int) (mobpos + ((d % 2 == 0) ? (25 * (d + 1) / 2) : -(25 * (d / 2))));
                }
                if (de.itemId == 0) { // meso
                    int mesos = Randomizer.nextInt(de.Maximum - de.Minimum) + de.Minimum;

                    if (mesos > 0) {
                        if (chr.getBuffedValue(MapleBuffStat.MESOUP) != null) {
                            mesos = (int) (mesos * chr.getBuffedValue(MapleBuffStat.MESOUP).doubleValue() / 100.0);
                        }
                        spawnMesoDrop(mesos * chr.getMesoRate(), calcDropPos(pos, mob.getPosition()), mob, chr, false, droptype);
                    }
                } else {
                    if (ItemConstants.getInventoryType(de.itemId) == MapleInventoryType.EQUIP) {
                        idrop = ii.randomizeStats((Equip) ii.getEquipById(de.itemId));
                    } else {
                        idrop = new Item(de.itemId, (short) 0, (short) (de.Maximum != 1 ? Randomizer.nextInt(de.Maximum - de.Minimum) + de.Minimum : 1));
                    }
                    spawnDrop(idrop, calcDropPos(pos, mob.getPosition()), mob, chr, droptype, de.questid);
                }
                d++;
            }
        }
        final List<MonsterGlobalDropEntry> globalEntry = mi.getGlobalDrop();
        // Global Drops
        for (final MonsterGlobalDropEntry de : globalEntry) {
            if (Randomizer.nextInt(999999) < de.chance) {
                if (droptype == 3) {
                    pos.x = (int) (mobpos + (d % 2 == 0 ? (40 * (d + 1) / 2) : -(40 * (d / 2))));
                } else {
                    pos.x = (int) (mobpos + ((d % 2 == 0) ? (25 * (d + 1) / 2) : -(25 * (d / 2))));
                }
                if (de.itemId == 0) {
                    //chr.getCashShop().gainCash(1, 80);
                } else {
                    if (ItemConstants.getInventoryType(de.itemId) == MapleInventoryType.EQUIP) {
                        idrop = ii.randomizeStats((Equip) ii.getEquipById(de.itemId));
                    } else {
                        idrop = new Item(de.itemId, (short) 0, (short) (de.Maximum != 1 ? Randomizer.nextInt(de.Maximum - de.Minimum) + de.Minimum : 1));
                    }
                    spawnDrop(idrop, calcDropPos(pos, mob.getPosition()), mob, chr, droptype, de.questid);
                    d++;
                }
            }
        }
    }

    private void spawnDrop(final Item idrop, final Point dropPos, final MapleMonster mob, final MapleCharacter chr, final byte droptype, final short questid) {
        final MapleMapItem mdrop = new MapleMapItem(idrop, dropPos, mob, chr, droptype, false, questid);
        mdrop.setDropTime(System.currentTimeMillis());
        spawnAndAddRangedMapObject(mdrop, new DelayedPacketCreation() {
            @Override
            public void sendPackets(MapleClient c) {
                if (questid <= 0 || (c.getPlayer().getQuestStatus(questid) == 1 && c.getPlayer().needQuestItem(questid, idrop.getItemId()))) {
                    c.announce(MaplePacketCreator.dropItemFromMapObject(mdrop, mob.getPosition(), dropPos, (byte) 1));
                }
            }
        }, null);

        TimerManager.getInstance().schedule(new ExpireMapItemJob(mdrop), 180000);
        activateItemReactors(mdrop, chr.getClient());
    }
    public void setPartyLimit(int num){
      partylimit = num;  
    }
    public int getPartyLimit(){
        return partylimit;
    }
    public void setPartyLimitBol(boolean yes){
        isplimit = yes;
    }
    public boolean isPartyLimit(){
        return isplimit;
    }
    public void setTaggerinBL(MapleCharacter tagger) {

        savedbls = new BuddyList[tagger.getMap().getCharacters().size()];
        cabt = new String[tagger.getMap().getCharacters().size()];
        BuddylistEntry tagbl = new BuddylistEntry(tagger.getName(), "tagger", tagger.getId(), tagger.getClient().getChannel(), true);
        int[] buddyids;
        int i = 0;
        for (MapleCharacter a1 : tagger.getMap().getCharacters()) // O(N)
        {
            savedbls[i] = a1.getBuddylist(); // Saving bl into the map
            buddyids = a1.getBuddylist().getBuddyIds();
            for (int j = 0; j < buddyids.length; j++) {
                a1.getBuddylist().remove(buddyids[j]); // Removing players bl
            }
            a1.getBuddylist().put(tagbl);
            i++;
        }
        chosentagger = true;
    }

    public BuddyList[] getBLofAffectedbyTagger() {
        return savedbls;
    }

    public String[] getNamesofAffectedbyTagger() {
        return cabt;
    }

    public boolean didChooseTagger() {
        return chosentagger;
    }

    public void setChoosenTagger(boolean off) {
        chosentagger = off;
    }

    public void setChalkpoints(boolean yes) {
        chalkpointsOn = yes;
    }

    public boolean ChalkpointsOn() {
        return chalkpointsOn;
    }

    public void insertChalkpoints(int pts) {
        ptstowin = pts;
    }

    public int getPointstowin() {
        return ptstowin;
    }

    public void setHitman(boolean yes) {
        hitmanOn = yes;
    }

    public boolean hitmanOn() {
        return hitmanOn;
    }

    public void setBombermap(boolean yes) {
        bombermapOn = yes;
    }

    public boolean bombermapOn() {
        return bombermapOn;
    }

    public void setTaggermap(boolean yes) {
        taggermapOn = yes;
    }

    public boolean taggermapOn() {
        return taggermapOn;
    }

    public void setTaggerMode(boolean yes) {
        taggerOn = yes;

    }

    public void setTaggerName(String name) {
        tagger = name;

    }

    public String getTaggerName() {
        return tagger;

    }

    public boolean taggerOn() {
        return taggerOn;
    }

    /*  public void setAggro(boolean yes)
    {
        aggroOn = yes;
    }
    
    public boolean aggroOn()
    {
        return aggroOn;
    } */

    public void setChalk(boolean yes) {
        chalkAllowed = yes;
    }

    public boolean isChalkAllowed() {
        return chalkAllowed;
    }

    public void setClosable(boolean yes) {
        chalkClosable = yes;
    }

    public boolean isChalkClosable() {
        return chalkClosable;
    }

    public void setNti(boolean yes) {
        ntiOn = yes;
    }

    public boolean ntiOn() {
        return ntiOn;
    }

    public void setScat(boolean yes, MapleCharacter player) {
        scatOn = yes;
        scatGuy = player;
    }

    public MapleCharacter scatGuy() {
        return scatGuy;
    }

    public boolean scatOn() {
        return scatOn;
    }

    public void setSpeedtype(boolean yes) {
        speedtypeOn = yes;
    }

    public boolean speedtypeOn() {
        return speedtypeOn;
    }

    public void setUnscramble(boolean yes) {
        unscrambleOn = yes;
    }

    public boolean unscrambleOn() {
        return unscrambleOn;
    }

    public void setCompunscramble(boolean yes) {
        compunscrambleOn = yes;
        // compunscramhost = 
    }

    public boolean compunscrambleOn() {
        return compunscrambleOn;
    }

    public boolean compunscrambleHost() {
        return compunscrambleOn;
    }

    public void setMiniunscramble(boolean yes, String host) {
        miniunscrambleOn = yes;
        miniunscramhost = host;
    }

    public boolean miniunscrambleOn() {
        return miniunscrambleOn;
    }

    public String getMiniunscramblehost() {
        return miniunscramhost;
    }

    public void setBlink(boolean yes) {
        blinkOn = yes;
    }

    public boolean blinkOn() {
        return blinkOn;
    }

    public void setCblink(boolean yes) {
        cblinkOn = yes;
    }

    public boolean cblinkOn() {
        return cblinkOn;
    }

    public void setMobkill(boolean yes) {
        mobkillOn = yes;
    }

    public boolean mobkillOn() {
        return mobkillOn;
    }

    public void setBombkill(boolean yes) {
        bombkillOn = yes;
    }

    public boolean bombkillOn() {
        return bombkillOn;
    }

    public void closestToX(MapleCharacter player) {
        int min = 10000, dist, pos; // Large number, too large for maps
        String name = "";
        for (MapleCharacter a1 : player.getMap().getCharacters()) {
            dist = a1.getPosition().x - player.getPosition().x;
            if (min > Math.abs(dist) && !a1.getName().equals(player.getName())) {
                min = Math.abs(dist);
                pos = a1.getPosition().x;
                name = a1.getName();
            }
        }
        if (min != 10000) {
            player.getMap().broadcastMessage(MaplePacketCreator.sendYellowTip(name + " was closest to " + player.getName() + " | " + min + " pixels apart"));
        } else {
            player.getMap().broadcastMessage(MaplePacketCreator.sendYellowTip("There's no one around you ," + player.getName()));
        }

    }

    public void deleteTempNpcs() {
        for (int i = 0; i < tempnpcs.size(); i++) {
            removeMapObject(tempnpcs.get(i));
        }

        tempnpcs.clear();

        /*  while(!tempnpcs.isEmpty()){
            removeMapObject(tempnpcs.get(counter));
            tempnpcs.remove(counter);
            counter++;
       } */
        //   broadcastGMMessage(MaplePacketCreator.serverNotice(5, tempnpcs.size() + "")); 
        broadcastGMMessage(MaplePacketCreator.serverNotice(5, "Done."));
    }

    public void addTempNpc(MapleNPC temp) {
        tempnpcs.add(temp);
    }

    public void deleteBernards() {
        for (int i = 0; i < bernards.size(); i++) {
            removePlayer(bernards.get(i));
        }

        bernards.clear();
        BernardManager.instance.getBernardList().clear();

        /*  while(!tempnpcs.isEmpty()){
            removeMapObject(tempnpcs.get(counter));
            tempnpcs.remove(counter);
            counter++;
       } */
        //   broadcastGMMessage(MaplePacketCreator.serverNotice(5, tempnpcs.size() + "")); 
        //   broadcastGMMessage(MaplePacketCreator.serverNotice(5, "Done.")); 
    }

    public void addBernard(MapleCharacter avatar) {
        bernards.add(avatar);
    }

    public boolean mobsAlive(MapleCharacter player) {
        boolean mobalive = false;
        int mobsinmap = 0;
        if (player.getMap().getMonsters().size() == 0) {
            mobsinmap = 0;
        } else {
            mobsinmap = player.getMap().getMonsters().size();
        }

        //   player.dropMessage(6,mobsinmap + "");  
        int k = 0;
        while (!mobalive && k < mobsinmap) {
            if (player.getMap().getMonsters().get(k) != null) {
                mobalive = true;
            }
            k++;

        }

        return mobalive;
    }

    public void startFlyingbobs(final MapleCharacter player, int bobnum) {

        final List<MapleCharacter> players = new ArrayList<>();
        player.dropMessage(5, "Heres a tip: you should multiple the number of snails u want to chase each player by the amount of players & enter that number");
        MapleLifeFactory.setAggro(true);
        for (int i = 0; i < bobnum; i++) {
            player.getMap().spawnMonsterOnGroudBelow(MapleLifeFactory.getMonster(9700039), player.getPosition());
        }

        cancelBomb = TimerManager.getInstance().register(new Runnable() {
            @Override
            public void run() {
                int randnum;
                Random rand = new Random();
                for (MapleCharacter a1 : player.getMap().getCharacters()) {
                    if (a1.isAlive()) { // After its done we need to add !ai.isGM()
                        players.add(a1);
                    }
                }
                /*       
         if (players.size() > 1) 
         {
             for(MapleMonster m1 : player.getMap().getMonsters()){
                 if(m1.getController() != null) {
                 if(!m1.getController().isAlive()){
                    randnum = rand.nextInt(players.size());
                    m1.setController(players.get(randnum));
                 }
               }
                 else{
                     randnum = rand.nextInt(players.size());
                    m1.setController(players.get(randnum)); 
                 }
             } */


                if (players.size() < 2) {
                    MapleLifeFactory.setAggro(false);
                    List<MapleMapObject> monsters = player.getMap().getMapObjectsInRange(player.getPosition(), Double.POSITIVE_INFINITY, Arrays.asList(MapleMapObjectType.MONSTER));
                    for (MapleMapObject monstermo : monsters) {
                        MapleMonster monster = (MapleMonster) monstermo;
                        if (!monster.getStats().isFriendly()) {
                            try {
                                player.getMap().killMonster(monster, player, true);
                            } catch (SQLException ex) {
                                ex.printStackTrace();
                            }
                        }
                    }

                    player.getMap().broadcastMessage(MaplePacketCreator.serverNotice(6, "[Flying Bobs] " + players.get(0).getName() + " won!"));
                    cancelBombs();
                }
                players.removeAll(players);
            }
        }, 1000);
    }

    public void canstartFTJ(final MapleCharacter player) {
        String havefull = "";
        for (MapleCharacter a1 : player.getMap().getCharacters()) {
            if (a1.getInventory(MapleInventoryType.ETC).isFull()) {
                havefull += a1.getName() + ", ";
            }
        }
        if (!havefull.equals("")) {
            havefull = havefull.substring(0, havefull.length() - 2);
            player.getMap().broadcastMessage(MaplePacketCreator.sendYellowTip("[FTJ] The following players have full etc: " + havefull + ".   We ask that you drop an item from your etc"));
            canstartFTJon = false;
        } else {
            player.getMap().broadcastMessage(MaplePacketCreator.sendYellowTip("[FTJ] All good! All of the players here have free space in their inventory!"));
            player.dropMessage(6, "Please retype the command to start the event, Dont forget to turn on !bombkill and !bombermap");

            canstartFTJon = true;
        }
    }

    public boolean iscanstartFTJon() {
        return canstartFTJon;
    }

    public void startFindtheJewel(final MapleCharacter player, final int timelimit) // Jewel id: 4001024
    {

        // Part 0 - Checking if all of the players have free slots in their inventory
        if (player.getMap().canstartFTJon) {
            player.getMap().setFTJ(true);
            final MapleCharacter gm = player;
            final MapleMap gmmap = player.getMap();

            final int jewelid = 4001024;
            final Item jewel = new Item(jewelid, (short) 0, (short) 1);
            jewel.setOwner(gm.getName());

            int randnum;

            Random rand = new Random();
            List<MapleCharacter> players = new ArrayList<>();
            //  Part 1 - Removing the jewel from players inventories who mightve gotten it via external ways
            // player.dropMessage(5,"Passed Check isFull()");
            for (MapleCharacter a1 : player.getMap().getCharacters()) {
                if (a1.getInventory(MapleInventoryType.ETC).findById(jewelid) != null) {
                    short quantity = a1.getInventory(MapleInventoryType.ETC).findById(jewelid).getQuantity();
                    MapleInventoryManipulator.removeById(a1.getClient(), MapleInventoryType.ETC, jewelid, quantity, false, false);
                }
            }
            // Part 2 - Inserting the jewel into a random players inventory   
            //  player.dropMessage(5,"Passed Remove jewel from inv");
            for (MapleCharacter a1 : player.getMap().getCharacters()) {
                if(!a1.isGM() || a1.isTemp())
                players.add(a1);
            }

            randnum = rand.nextInt(players.size());

            //  players.get(randnum).getInventory(MapleInventoryType.ETC).addItem(jewel);        
            MapleInventoryManipulator.addById(players.get(randnum).getClient(), jewelid, (short) 1, gm.getName(), 0);
            player.getMap().broadcastMessage(MaplePacketCreator.sendYellowTip("[FTJ] " + players.get(randnum) + " has the jewel! The one to hold the jewel after the time ends is the winner!"));
            // player.dropMessage(5,"Passed Insert jewel into inv");

            // Part 3 - Activating !playerbombermap and !bombkillon & Starting the clock
            player.getMap().broadcastMessage(MaplePacketCreator.getClock(timelimit));
            /*  final Timer fivemin = new Timer();
         fivemin.scheduleAtFixedRate(new TimerTask(){ */

            cancelinnerFTJ = TimerManager.getInstance().register(new Runnable() {
                @Override
                public void run() {
                    //  player.dropMessage(5,"Checking..");
                    MapleCharacter hasit = null;
                    boolean onground = false;
                    int randnum1;
                    Random rand1 = new Random();
                    List<MapleCharacter> playersinmap = new ArrayList<>();
                    List<MapleMapObject> objectsinmap = new ArrayList<>();
                    // Reviving dead players
                    for (MapleCharacter e1 : gmmap.getCharacters()) {
                        if(!e1.isGM() || e1.isTemp())
                        if (!e1.isAlive()) {
                            //    player.dropMessage(6,e1.getName() + " is dead");
                            e1.Revplayerafter(4, e1);
                        }
                    }
 
                    // Checking if a player has it
                    for (MapleCharacter b1 : gmmap.getCharacters()) {
                        if(!b1.isGM() || b1.isTemp())
                        if (b1.getInventory(MapleInventoryType.ETC).findById(jewelid) != null) { // Remeber to change jewelid in ChangeMap and Login handl;lers
                            hasit = b1;
                            if (gmmap.didJeweldied()) {
                                gmmap.setJeweldead(false);
                                gmmap.broadcastMessage(MaplePacketCreator.serverNotice(6, "[FTJ] " + b1.getName() + " has picked up the jewel!"));
                            }
                        }
                    }
                    // Checking if its on the ground
                    for (MapleMapObject o1 : gmmap.getMapObjects()) {
                        if (o1.getObjectId() == gmmap.getWantedObjId()) {
                            onground = true;
                        }
                    }

                    if (!onground) {
                        for (MapleMapObject o1 : gmmap.getMapObjects()) {
                            if (o1.getObjectId() != gmmap.getWantedObjId()) {
                                clearDrops();
                            }
                        }
                    }

                    // In the case of someone dcing or leaving the map, meaning that its not on the ground nor in anyones inventory
                    if (hasit == null && !onground) { // Note to self: Check to see if it indeed applies to dropped items and only to them
                        for (MapleCharacter c1 : gmmap.getCharacters()) {
                            if (c1.isAlive() && (!c1.isGM() || c1.isTemp())) {
                                playersinmap.add(c1);
                            }
                        }
                        randnum1 = rand1.nextInt(playersinmap.size());
                        MapleInventoryManipulator.addById(playersinmap.get(randnum1).getClient(), jewelid, (short) 1, gm.getName(), 0);
                        gmmap.broadcastMessage(MaplePacketCreator.serverNotice(6, "[FTJ] The player who had the jewel dced, left the map or dropped it!" + playersinmap.get(randnum1).getName() + " now has it!"));
                        hasit = playersinmap.get(randnum1);
                    }
                    // If a player dies, he'll drop
                    if (hasit != null) {
                        if (hasit.getTrade() != null) {
                            MapleTrade.cancelTrade(hasit); // To avoid transaction of the jewel in any form
                        }
                        if (!hasit.isAlive()) {
                            MapleInventoryManipulator.removeById(hasit.getClient(), MapleInventoryType.ETC, jewelid, (short) 1, true, false);

                            hasit.getMap().spawnItemDrop(hasit, hasit, jewel, hasit.getPosition(), true, true);
                            for (MapleMapObject o2 : gmmap.getMapObjects()) {
                                objectsinmap.add(o2);
                            }

                            gmmap.setWantedObjId(objectsinmap.get(objectsinmap.size() - 1).getObjectId());
                            gmmap.broadcastMessage(MaplePacketCreator.serverNotice(6, "[FTJ] " + hasit.getName() + " has died! The jewels on the floor!!"));
                            gmmap.setJeweldead(true);
                        }
                    }

                }
            }, 10);

            cancelFTJ = TimerManager.getInstance().schedule(new Runnable() { //  I lazed out with making a new identical method
                @Override
                public void run() {
                    MapleCharacter winner = null;
                    for (MapleCharacter a1 : gmmap.getCharacters()) {
                        if (a1.getInventory(MapleInventoryType.ETC).findById(jewelid) != null) {
                            winner = a1;
                        }
                    }
                    if (winner == null) {
                        gmmap.broadcastMessage(MaplePacketCreator.sendYellowTip("[FTJ] There are no winners! No one has the jewel!"));
                    } else {
                        gmmap.broadcastMessage(MaplePacketCreator.sendYellowTip("[FTJ] " + winner.getName() + " has the jewel and is the winner!"));
                    }

                    gmmap.clearDrops();
                    gmmap.canstartFTJon = false;
                    gmmap.setFTJ(false);
                    //fivemin.cancel();
                    cancelinnerFTJs();
                    cancelFTJs();

                }
            }, timelimit * 1000);

        }
    }

    public void setJeweldead(boolean yes) {
        jeweldied = yes;
    }

    public boolean didJeweldied() {
        return jeweldied;
    }

    public boolean isFTJ() {
        return ftjOn;
    }

    public void setFTJ(boolean yes) {
        ftjOn = yes;
    }

    public void setWantedObjId(int id) {
        wantedobjid = id;
    }

    public int getWantedObjId() {
        return wantedobjid;
    }

    public void deathLog(final MapleCharacter player) {
        int deathlogshows = 5;
        int[] min = new int[deathlogshows];
        boolean alrappear = false;
        boolean end = false;
        int amountofplayers = 0;
        for (int i = 0; i < min.length; i++) {
            min[i] = 99;
        }
        String[] players = new String[deathlogshows];
        String deathlog = "";

        for (int j = 0; j < min.length && !end; j++) {
            for (MapleCharacter a1 : player.getMap().getCharacters()) {
                alrappear = false;
                for (int i = 0; i < players.length; i++) {
                    if (a1.getName() == players[i]) {
                        alrappear = true;
                    }
                }
                if (!alrappear) {
                    if (a1.getEstimatedtime() <= min[j]) {
                        min[j] = a1.getEstimatedtime();
                        players[j] = a1.getName();
                    }
                }

            }
            if (min[j] == 99) {
                end = true;
                amountofplayers = j;
            }
        }
        if (!end) {
            amountofplayers = deathlogshows;
        }
        if (amountofplayers == 0) {
            player.dropMessage(6, "Nobody had died in the last 60 seconds.");
        } else {
            player.dropMessage(6, "[Death Log]");
            for (int k = 0; k < amountofplayers; k++) {
                if (min[k] > 1) {
                    player.dropMessage(6, k + 1 + ". " + players[k] + " died " + min[k] + " seconds ago.");
                } else {
                    player.dropMessage(6, k + 1 + ". " + players[k] + " died " + min[k] + " second ago.");
                }
            }

            // deathlog = deathlog.substring(0,deathlog.length()-2);
        }
        // player.dropMessage(6,deathlog);

        /*
        for(MapleCharacter a1 : player.getMap().getCharacters())
            if(a1.getEstimatedtime() <= min[0]){
                min[0] = (int)a1.getEstimatedtime();
                players[0] = a1.getName();
            }
        for(MapleCharacter a1 : player.getMap().getCharacters())
            if(a1.getEstimatedtime() <= min[1] && a1.getName().equals(players[0])){
                min[1] = (int)a1.getEstimatedtime();
                players[1] = a1.getName();
            }
         for(MapleCharacter a1 : player.getMap().getCharacters())
            if(a1.getEstimatedtime() <= min[2] && a1.getName().equals(players[0]) && a1.getName().equals(players[1])){
                min[2] = (int)a1.getEstimatedtime();
                players[2] = a1.getName();
            }
          for(MapleCharacter a1 : player.getMap().getCharacters())
            if(a1.getEstimatedtime() <= min[3] && a1.getName().equals(players[0]) && a1.getName().equals(players[1]) && a1.getName().equals(players[2])){
                min[3] = (int)a1.getEstimatedtime();
                players[3] = a1.getName();
            }
           for(MapleCharacter a1 : player.getMap().getCharacters())
            if(a1.getEstimatedtime() <= min[4] && a1.getName().equals(players[0]) && a1.getName().equals(players[1]) && a1.getName().equals(players[2]) && a1.getName().equals(players[3])){
                min[4] = (int)a1.getEstimatedtime();
                players[4] = a1.getName();
            } */
    }

    public void startElimDoom(final MapleCharacter player) {
        final Random rand = new Random();
        int x, y;
        final MapleMap gmmap = player.getMap(); // Incase the gm disconencts
        int wait = 0;
        Point pos;
        final List<MapleCharacter> players = new ArrayList<>();
        /*  MapleMonster monster;
        monster.setHp(1); */

        for (MapleCharacter a1 : player.getMap().getCharacters()) // spawning the boxes do the red line shit and try to debug and see wht happens yep
        {
            if (a1.isAlive() && (!a1.isGM() || a1.isTemp())) { // After its done we need to add !ai.isGM()
                players.add(a1);
                /*
             x = rand.nextInt(2250 + 1530) - 1530; // Elim of Doom map id is 109010100
             y = rand.nextInt(210 + 3306) - 3306; 
                 */

                x = rand.nextInt(gmmap.getFootholds().getX2() - 30 - gmmap.getFootholds().getX1()) + gmmap.getFootholds().getX1() + 30;
                while (x > gmmap.getFootholds().getX2() || x < gmmap.getFootholds().getX1()) {
                    x = rand.nextInt(gmmap.getFootholds().getX2() - 30 - gmmap.getFootholds().getX1()) + gmmap.getFootholds().getX1() + 30;
                }

                y = rand.nextInt(gmmap.getFootholds().getY2() - 30 - gmmap.getFootholds().getX1()) + gmmap.getFootholds().getX1() + 30;
                while (y > gmmap.getFootholds().getX2() || y < gmmap.getFootholds().getX1()) {
                    y = rand.nextInt(gmmap.getFootholds().getY2() - 30 - gmmap.getFootholds().getY1()) + gmmap.getFootholds().getY1() + 30;
                }

                pos = new Point(x, y);
                if (wait > 0) {
                    player.dropMessage(5, x + " , " + y);
                    player.getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(9500365), pos);
                }
                wait = 1;

            }

        }

        player.getMap().broadcastMessage(MaplePacketCreator.sendYellowTip("[Elim Box of Doom] Break the boxes inorder to kill other contestants! Last player alive will be declared as the winner!"));
        elimdoomOn = true;

        cancelElimDoom = TimerManager.getInstance().register(new Runnable() {
            @Override
            public void run() {

                if (players.size() == 1) {
                    gmmap.broadcastMessage(MaplePacketCreator.serverNotice(6, players.get(0).getName() + " won!"));
                    elimdoomOn = false;
                    try {
                        elimDoomFin(player);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                } else {
                    players.removeAll(players);
                    for (MapleCharacter a1 : gmmap.getCharacters()) {
                        if (a1.isAlive()) // After its done we need to add !ai.isGM()
                        {
                            players.add(a1);
                        }

                    }
                }

            }
        }, 500);
    }

    public void startCBlink(final MapleCharacter player, int amount, final int timer) {
        Random rand = new Random();

        int space = amount * 30, charrand = 0, dist = 0;
        String code = "", answer = "", revanswer = "";
        Point pos;
        final MapleMap gmmap = player.getMap(); // Incase the gm disconencts
        Item droppedletter;

        for (int i = 0; i < amount; i++) {
            charrand = rand.nextInt(25); // Letters in the alphabet 0-25
            answer += (char) (charrand + 97);

            if (charrand < 10) {
                code = "399100" + charrand;
            } else {
                code = "39910" + charrand;
            }
            droppedletter = new Item(Integer.parseInt(code), (short) 0, (short) 0, 1);
            pos = new Point(player.getPosition().x + space / 2 - dist, player.getPosition().y);
            player.getMap().spawnItemDrop(player, player, droppedletter, pos, false, true);
            dist += 30; // Space between letters, while "space" is the space cut out from the map given to spawn letters                          

        }
        for (int i = amount - 1; i > -1; i--) {
            revanswer += answer.charAt(i);
        }
        player.dropMessage(5, revanswer);
        player.getMap().broadcastMessage(MaplePacketCreator.sendYellowTip("[CBlink] Type out the letters! you may still win the round even if the letters disappeared!"));
        ServerConstants.cblinkAnswer = revanswer;
        player.getMap().setCblink(true);
        player.getMap().broadcastMessage(MaplePacketCreator.getClock(timer));
        cancelBomb = TimerManager.getInstance().schedule(new Runnable() { //  I lazed out with making a new identical method
            @Override
            public void run() {
                gmmap.clearDrops();
                cancelBombs();

            }
        }, timer * 1000);

    }

    public void startDodgeBomb(final MapleCharacter player, final int level) // Only for plat 16 atm.
    {

        final Random rand = new Random();
        final List<MapleCharacter> players = new ArrayList<>();
        player.dropMessage(5, "Remember to activate autokill on the intended platform");

        int delay = 1000;
        if (level == 0) {
            delay = 2000;
        } else if (level == 1) {
            delay = 1000;
        } else if (level == 2) {
            delay = 500;
        } else if (level == 3) {
            delay = 100;
        } else {
            delay = 1000;
        }

        cancelBomb = TimerManager.getInstance().register(new Runnable() { // I lazed out creating an identical method.
            @Override
            public void run() {

                for (MapleCharacter a1 : player.getMap().getCharacters()) {
                    if (a1.isAlive() && (!a1.isGM() || a1.isTemp())) { // After its done we need to add !ai.isGM()
                        players.add(a1);
                    }
                }
                if (players.size() > 1) // srr i wanna add something WAIT
                {
                    int num = rand.nextInt(1138) - 500;
                    player.dropMessage(5, "rand x pos: " + num);
                    if (num > 648 || num < -290) {
                        num = rand.nextInt(648) - 290;
                    }
                    player.dropMessage(5, "rand x pos after recalibration: " + num);
                    player.getMap().spawnBombOnGroudBelow(9300166, num, -206);
                    players.removeAll(players);
                } else {
                    players.get(0).getMap().broadcastMessage(MaplePacketCreator.serverNotice(6, players.get(0).getName() + " won!"));
                    cancelBombs();
                }
            }
        }, delay);

    }

    public void startDodgeBob(final MapleCharacter player) {
        final Random rand = new Random();
        final List<MapleCharacter> players = new ArrayList<>();
        player.dropMessage(5, "Remember to activate mobkill and autokill on the platform ur intending for");
        player.getMap().broadcastMessage(MaplePacketCreator.sendYellowTip("[DodgeBob] Avoid touching the bobs to win, Last player alive will be declared as the winner!"));
        for (int i = 0; i < 15; i++) {
            player.getMap().spawnMonsterOnGroudBelow(MapleLifeFactory.getMonster(9400551), player.getPosition());
        }
        cancelBomb = TimerManager.getInstance().register(new Runnable() { // I lazed out, didnt wanna create the exact same method under a diff name.
            @Override
            public void run() {
                for (MapleCharacter a1 : player.getMap().getCharacters()) {
                    if (a1.isAlive()  && (!a1.isGM() || a1.isTemp())) { // After its done we need to add !ai.isGM()
                        players.add(a1);
                    }
                }
                if (players.size() < 2) // srr i wanna add something WAIT
                {
                    players.get(0).getMap().broadcastMessage(MaplePacketCreator.serverNotice(6, players.get(0).getName() + " won!"));
                    cancelBombs();
                }
                players.removeAll(players);

            }
        }, 500);

    }

    public void startImpossibleBob(final MapleCharacter player) {
        player.getMap().broadcastMessage(MaplePacketCreator.sendYellowTip("[Impossible Bob] Get the furthest you can without touching the bobs! the last players each round will be eliminated!"));
        for (MapleCharacter a1 : player.getMap().getCharacters()) {
            a1.setHp(30000);
            a1.updateSingleStat(MapleStat.HP, 30000);
        }
        for (int i = 0; i < 15; i++) {
            player.getMap().spawnMonsterOnGroudBelow(MapleLifeFactory.getMonster(9400551), player.getPosition());
        }

        // player.getMap().updateMonsterController(MapleLifeFactory.getMonster(9400551));
        player.getMap().broadcastMessage(MaplePacketCreator.getClock(15));
        cancelimpbob = TimerManager.getInstance().register(new Runnable() {
            @Override
            public void run() {
                int min1 = 10000;
                int min2 = 10000;
                String name1 = "null1";
                String name2 = "null2";
                try {
                    Thread.sleep(14000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }

                for (MapleCharacter a1 : player.getMap().getCharacters()) {
                    a1.setHp(0);
                    a1.updateSingleStat(MapleStat.HP, 0);
                }
                for (MapleCharacter a1 : player.getMap().getCharacters()) {
                    if (a1.getPosition().x < min1) {
                        min1 = a1.getPosition().x;
                        name1 = a1.getName();
                    }

                }
                for (MapleCharacter a1 : player.getMap().getCharacters()) {
                    if (a1.getPosition().x < min2 && !(a1.getName().equals(name1))) {
                        min2 = a1.getPosition().x;
                        name2 = a1.getName();
                    }
                }
                if (min1 == 10000) {
                    player.getMap().broadcastMessage(MaplePacketCreator.sendYellowTip("There are no winners!"));
                } else {
                    if (min2 == 10000) {
                        player.getMap().broadcastMessage(MaplePacketCreator.sendYellowTip(name1 + " died at pos: X=" + min1));
                    } else {
                        player.getMap().broadcastMessage(MaplePacketCreator.sendYellowTip(name1 + " died at pos: X=" + min1 + " | " + name2 + " died at pos: X=" + min2));
                    }
                }
                impbobFin();

            }
        }, 100);

    }

    public void startTestBombs(final MapleCharacter player) { // wait wat... the method is right here?

        final Random rand = new Random();
        final List<MapleCharacter> players = new ArrayList<>();
        player.getMap().broadcastMessage(MaplePacketCreator.sendYellowTip("[Raining Bombs] Avoid the bombs that spawn at your feet, last player alive will be declared as the winner!"));

        cancelBomb = TimerManager.getInstance().register(new Runnable() {
            @Override
            public void run() {
                int number;
                MapleCharacter bombedplayer;
                for (MapleCharacter a1 : player.getMap().getCharacters()) {
                    if (a1.isAlive()  && (!a1.isGM() || a1.isTemp())) { // After its done we need to add !ai.isGM()
                        players.add(a1);
                    }
                }
                if (players.size() > 1) // srr i wanna add something WAIT
                {
                    number = rand.nextInt(players.size());
                    bombedplayer = players.get(number);
                    bombedplayer.getMap().spawnBombOnGroundBelow(MapleLifeFactory.getMonster(9300166), bombedplayer.getPosition());
                    players.removeAll(players);
                } else {
                    players.get(0).getMap().broadcastMessage(MaplePacketCreator.serverNotice(6, players.get(0).getName() + " won!"));
                    cancelBombs();
                }
            }
        }, 1000);

    }

    public void warpafterDelay(final int mapid, final MapleCharacter victim) {
        cancelDelay = TimerManager.getInstance().schedule(new Runnable() { //  I lazed out with making a new identical method
            @Override
            public void run() {

                try {
                    victim.changeMap(mapid);
                } catch (SQLException ex) {
                    Logger.getLogger(MapleMap.class.getName()).log(Level.SEVERE, null, ex);
                }

                cancelDelays();
            }
        }, 750);
    }

    public final void cancelBombs() {
        if (cancelBomb != null) {

            cancelBomb.cancel(false);
            cancelBomb = null;
        }
    }

    public final void cancelDelays() {
        if (cancelDelay != null) {

            cancelDelay.cancel(false);
            cancelDelay = null;
        }
    }

    public final void cancelFTJs() {
        if (cancelFTJ != null) {

            cancelFTJ.cancel(false);
            cancelFTJ = null;
        }
    }

    public final void cancelinnerFTJs() {
        if (cancelinnerFTJ != null) {

            cancelinnerFTJ.cancel(false);
            cancelinnerFTJ = null;
        }
    }

    public final void elimDoomFin(MapleCharacter player) throws SQLException {
        if (cancelElimDoom != null) {
            List<MapleMapObject> monsters = player.getMap().getMapObjectsInRange(player.getPosition(), Double.POSITIVE_INFINITY, Arrays.asList(MapleMapObjectType.MONSTER));
            MapleMap map = player.getMap();
            for (MapleMapObject monstermo : monsters) {
                MapleMonster monster = (MapleMonster) monstermo;
                map.killMonster(monster, player, true);

            }

            cancelElimDoom.cancel(false);
            cancelElimDoom = null;
        }
    }

    public final void impbobFin() {
        if (cancelimpbob != null) {

            cancelimpbob.cancel(false);
            cancelimpbob = null;
        }
    }

    public void addClone(final MapleCharacter chr) {
        if (chr.isClone()) {
            chrWLock.lock();
            try {
                this.characters.add(chr);
            } finally {
                chrWLock.unlock();
            }
            broadcastMessage(chr, MaplePacketCreator.spawnPlayerMapobject(chr), false);
            objectWLock.lock();
            try {
                this.mapobjects.put(Integer.valueOf(chr.getObjectId()), chr);
            } finally {
                objectWLock.unlock();
            }
        }
    }

    public final void spawnMesoDrop(final int meso, final Point position, final MapleMapObject dropper, final MapleCharacter owner, final boolean playerDrop, final byte droptype) {
        final Point droppos = calcDropPos(position, position);
        final MapleMapItem mdrop = new MapleMapItem(meso, droppos, dropper, owner, droptype, playerDrop);
        mdrop.setDropTime(System.currentTimeMillis());

        spawnAndAddRangedMapObject(mdrop, new DelayedPacketCreation() {
            @Override
            public void sendPackets(MapleClient c) {
                c.announce(MaplePacketCreator.dropItemFromMapObject(mdrop, dropper.getPosition(), droppos, (byte) 1));
            }
        }, null);

        TimerManager.getInstance().schedule(new ExpireMapItemJob(mdrop), 180000);
    }

    public final void disappearingItemDrop(final MapleMapObject dropper, final MapleCharacter owner, final Item item, final Point pos) {
        final Point droppos = calcDropPos(pos, pos);
        final MapleMapItem drop = new MapleMapItem(item, droppos, dropper, owner, (byte) 1, false);
        broadcastMessage(MaplePacketCreator.dropItemFromMapObject(drop, dropper.getPosition(), droppos, (byte) 3), drop.getPosition());

    }

    public MapleMonster getMonsterById(int id) {
        objectRLock.lock();
        try {
            for (MapleMapObject obj : mapobjects.values()) {
                if (obj.getType() == MapleMapObjectType.MONSTER) {
                    if (((MapleMonster) obj).getId() == id) {
                        return (MapleMonster) obj;
                    }
                }
            }
        } finally {
            objectRLock.unlock();
        }
        return null;
    }

    public int countMonster(int id) {
        int count = 0;
        for (MapleMapObject m : getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY, Arrays.asList(MapleMapObjectType.MONSTER))) {
            MapleMonster mob = (MapleMonster) m;
            if (mob.getId() == id) {
                count++;
            }
        }
        return count;
    }

    public boolean damageMonster(final MapleCharacter chr, final MapleMonster monster, final int damage) throws SQLException {
        List<MapleCharacter> players = new ArrayList<>();
        int randnum;
        MapleCharacter elimplayer;
        Random rand = new Random();
        if (monster.getId() == 8800000) {
            for (MapleMapObject object : chr.getMap().getMapObjects()) {
                MapleMonster mons = chr.getMap().getMonsterByOid(object.getObjectId());
                if (mons != null) {
                    if (mons.getId() >= 8800003 && mons.getId() <= 8800010) {
                        return true;
                    }
                }
            }
        }
        if (monster.isAlive()) {
            boolean killed = false;
            monster.monsterLock.lock();
            try {
                if (!monster.isAlive()) {
                    return false;
                }
                Pair<Integer, Integer> cool = monster.getStats().getCool();
                if (cool != null) {
                    Pyramid pq = (Pyramid) chr.getPartyQuest();
                    if (pq != null) {
                        if (damage > 0) {
                            if (damage >= cool.getLeft()) {
                                if ((Math.random() * 100) < cool.getRight()) {
                                    pq.cool();
                                } else {
                                    pq.kill();
                                }
                            } else {
                                pq.kill();
                            }
                        } else {
                            pq.miss();
                        }
                        killed = true;
                    }
                }
                if (damage > 0) {
                    monster.damage(chr, damage);
                    if (!monster.isAlive()) {  // monster just died
                        //killMonster(monster, chr, true);
                        if (monster.getId() == 9500365 && elimdoomOn) {
                            for (MapleCharacter a1 : chr.getMap().getCharacters()) {
                                if (a1.isAlive() && !a1.getName().equals(chr.getName())) {
                                    players.add(a1);
                                }

                            }
                            randnum = rand.nextInt(players.size());
                            elimplayer = players.get(randnum);
                            elimplayer.setHp(0);
                            elimplayer.updateSingleStat(MapleStat.HP, 0);

                        }
                        killed = true;
                    }
                } else if (monster.getId() >= 8810002 && monster.getId() <= 8810009) {
                    for (MapleMapObject object : chr.getMap().getMapObjects()) {
                        MapleMonster mons = chr.getMap().getMonsterByOid(object.getObjectId());
                        if (mons != null) {
                            if (monster.isAlive() && (monster.getId() >= 8810010 && monster.getId() <= 8810017)) {
                                if (mons.getId() == 8810018) {
                                    killMonster(mons, chr, true);
                                }
                            }
                        }
                    }
                }
            } finally {
                monster.monsterLock.unlock();
            }
            if (monster.getStats().selfDestruction() != null && monster.getStats().selfDestruction().getHp() > -1) {// should work ;p
                if (monster.getHp() <= monster.getStats().selfDestruction().getHp()) {
                    killMonster(monster, chr, true, false, monster.getStats().selfDestruction().getAction());
                    return true;
                }
            }
            if (killed) {
                killMonster(monster, chr, true);
            }
            return true;
        }
        return false;
    }

    public List<MapleMonster> getMonsters() {
        List<MapleMonster> mobs = new ArrayList<MapleMonster>();
        for (MapleMapObject object : this.getMapObjects()) {
            mobs.add(this.getMonsterByOid(object.getObjectId()));
        }
        return mobs;
    }

    public void killMonster(final MapleMonster monster, final MapleCharacter chr, final boolean withDrops) throws SQLException {
        killMonster(monster, chr, withDrops, false, 1);
    }

    public void killMonster(final MapleMonster monster, final MapleCharacter chr, final boolean withDrops, final boolean secondTime, int animation) throws SQLException {
        if (monster.getId() == 9300166) {
            final MapleCharacter roy = chr.getClient().getChannelServer().getPlayerStorage().getCharacterByName("dev");
            final MapleCharacter kanwar = chr.getClient().getChannelServer().getPlayerStorage().getCharacterByName("kanwar");
                                if(roy != null) {
                                    
                                    broadcastMessage(MaplePacketCreator.getChatText(roy.getId(), "ALLAH AKBAR!!!!", false, 1));
                                }
                                if(kanwar != null) {
                                    
                                    broadcastMessage(MaplePacketCreator.getChatText(kanwar.getId(), "ALLAH AKBAR!!!!", false, 1));
                                }
        }
        if (monster.getId() == 8810018 && !secondTime) {
            TimerManager.getInstance().schedule(new Runnable() {
                @Override
                public void run() {
                    try {
                        killMonster(monster, chr, withDrops, true, 1);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                    killAllMonsters();
                }
            }, 3000);
            return;
        }
        if (chr == null) {
            spawnedMonstersOnMap.decrementAndGet();
            monster.setHp(0);
            broadcastMessage(MaplePacketCreator.killMonster(monster.getObjectId(), animation), monster.getPosition());
            removeMapObject(monster);
            return;
        }
        /*if (chr.getQuest(MapleQuest.getInstance(29400)).getStatus().equals(MapleQuestStatus.Status.STARTED)) {
         if (chr.getLevel() >= 120 && monster.getStats().getLevel() >= 120) {
         //FIX MEDAL SHET
         } else if (monster.getStats().getLevel() >= chr.getLevel()) {
         }
         }*/
        int buff = monster.getBuffToGive();
        if (buff > -1) {
            MapleItemInformationProvider mii = MapleItemInformationProvider.getInstance();
            for (MapleMapObject mmo : this.getAllPlayer()) {
                MapleCharacter character = (MapleCharacter) mmo;
                if (character.isAlive()) {
                    MapleStatEffect statEffect = mii.getItemEffect(buff);
                    character.getClient().announce(MaplePacketCreator.showOwnBuffEffect(buff, 1));
                    broadcastMessage(character, MaplePacketCreator.showBuffeffect(character.getId(), buff, 1), false);
                    statEffect.applyTo(character);
                }
            }
        }

        List<MonsterDropEntry> dropEntry = new ArrayList<>();

        if (mapid == 450005100) {
            dropEntry.add(new MonsterDropEntry(4031466, 600, 1, 1, (short) -1));
        }

        if (monster.getId() == 8810018) {
            for (Channel cserv : Server.getInstance().getWorld(world).getChannels()) {
                for (MapleCharacter player : cserv.getPlayerStorage().getAllCharacters()) {
                    if (player.getMapId() == 240000000) {
                        player.message("Mysterious power arose as I heard the powerful cry of the Nine Spirit Baby Dragon.");
                    }
                    player.dropMessage("To the crew that have finally conquered Horned Tail after numerous attempts, I salute thee! You are the true heroes of Leafre!!");
                    if (player.isGM()) {
                        player.message("[GM-Message] Horntail was killed by : " + chr.getName());
                    }
                }
            }
        }
        switch (monster.getId()) {
            case 8510000:
            case 8520000:
                chr.completeAchievement(MapleAchievementType.DEFEAT_PIANUS);
                break;
            case 8500002:
                chr.completeAchievement(MapleAchievementType.DEFEAT_PAPULATUS);
                break;
            case 8800003:
                chr.completeAchievement(MapleAchievementType.DEFEAT_ZAKUM);
                break;
            case 8810018:
                chr.completeAchievement(MapleAchievementType.DEFEAT_HORNTAIL);
                break;
        }

        spawnedMonstersOnMap.decrementAndGet();
        monster.setHp(0);
        broadcastMessage(MaplePacketCreator.killMonster(monster.getObjectId(), animation), monster.getPosition());
        //if (monster.getStats().selfDestruction() == null) {//FUU BOMBS D:
        removeMapObject(monster);
        //}
        if (monster.getCP() > 0 && chr.getCarnival() != null) {
            chr.getCarnivalParty().addCP(chr, monster.getCP());
            chr.announce(MaplePacketCreator.updateCP(chr.getCP(), chr.getObtainedCP()));
            broadcastMessage(MaplePacketCreator.updatePartyCP(chr.getCarnivalParty()));
            //they drop items too ):
        }
        if (monster.getId() >= 8800003 && monster.getId() <= 8800010) {
            boolean makeZakReal = true;
            Collection<MapleMapObject> objects = getMapObjects();
            for (MapleMapObject object : objects) {
                MapleMonster mons = getMonsterByOid(object.getObjectId());
                if (mons != null) {
                    if (mons.getId() >= 8800003 && mons.getId() <= 8800010) {
                        makeZakReal = false;
                        break;
                    }
                }
            }
            if (makeZakReal) {
                for (MapleMapObject object : objects) {
                    MapleMonster mons = chr.getMap().getMonsterByOid(object.getObjectId());
                    if (mons != null) {
                        if (mons.getId() == 8800000) {
                            makeMonsterReal(mons);
                            updateMonsterController(mons);
                            break;
                        }
                    }
                }
            }
        }
        MapleCharacter dropOwner = monster.killBy(chr);
        if (withDrops && !monster.dropsDisabled()) {
            if (dropOwner == null) {
                dropOwner = chr;
            }
            if(mapid != 130010110)
                dropFromMonster(dropOwner, monster);
        }
    }

    public void killFriendlies(MapleMonster mob) throws SQLException {
        this.killMonster(mob, (MapleCharacter) getAllPlayer().get(0), false);
    }

    public void killMonster(int monsId) throws SQLException {
        for (MapleMapObject mmo : getMapObjects()) {
            if (mmo instanceof MapleMonster) {
                if (((MapleMonster) mmo).getId() == monsId) {
                    this.killMonster((MapleMonster) mmo, (MapleCharacter) getAllPlayer().get(0), false);
                }
            }
        }
    }

    public void monsterCloakingDevice() {
        for (MapleMapObject monstermo : getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY, Arrays.asList(MapleMapObjectType.MONSTER))) {
            MapleMonster monster = (MapleMonster) monstermo;
            broadcastMessage(MaplePacketCreator.makeMonsterInvisible(monster));
        }
    }

    public void softKillAllMonsters() {
        for (MapleMapObject monstermo : getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY, Arrays.asList(MapleMapObjectType.MONSTER))) {
            MapleMonster monster = (MapleMonster) monstermo;
            if (monster.getStats().isFriendly()) {
                continue;
            }
            spawnedMonstersOnMap.decrementAndGet();
            monster.setHp(0);
            removeMapObject(monster);
        }
    }

    public void killAllMonstersNotFriendly() {
        for (MapleMapObject monstermo : getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY, Arrays.asList(MapleMapObjectType.MONSTER))) {
            MapleMonster monster = (MapleMonster) monstermo;
            if (monster.getStats().isFriendly()) {
                continue;
            }
            spawnedMonstersOnMap.decrementAndGet();
            monster.setHp(0);
            broadcastMessage(MaplePacketCreator.killMonster(monster.getObjectId(), true), monster.getPosition());
            removeMapObject(monster);
        }
    }
    

    public void killAllMonsters() {
        for (MapleMapObject monstermo : getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY, Arrays.asList(MapleMapObjectType.MONSTER))) {
            MapleMonster monster = (MapleMonster) monstermo;
            spawnedMonstersOnMap.decrementAndGet();
            monster.setHp(0);
            broadcastMessage(MaplePacketCreator.killMonster(monster.getObjectId(), true), monster.getPosition());
            removeMapObject(monster);
        }
    }
    public List<MapleMapObject> getCharactersAsMapObjects() {
		return getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY,
				Arrays.asList(MapleMapObjectType.PLAYER));
	}

    public List<MapleMapObject> getAllPlayer() {
        return getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY, Arrays.asList(MapleMapObjectType.PLAYER));
    }

    public void destroyReactor(int oid) {
        final MapleReactor reactor = getReactorByOid(oid);
        TimerManager tMan = TimerManager.getInstance();
        broadcastMessage(MaplePacketCreator.destroyReactor(reactor));
        reactor.setAlive(false);
        removeMapObject(reactor);
        reactor.setTimerActive(false);
        if (reactor.getDelay() > 0) {
            tMan.schedule(new Runnable() {
                @Override
                public void run() {
                    respawnReactor(reactor);
                }
            }, reactor.getDelay());
        }
    }

    public void resetReactors() {
        objectRLock.lock();
        try {
            for (MapleMapObject o : mapobjects.values()) {
                if (o.getType() == MapleMapObjectType.REACTOR) {
                    final MapleReactor r = ((MapleReactor) o);
                    r.setState((byte) 0);
                    r.setTimerActive(false);
                    broadcastMessage(MaplePacketCreator.triggerReactor(r, 0));
                }
            }
        } finally {
            objectRLock.unlock();
        }
    }

    public void shuffleReactors() {
        List<Point> points = new ArrayList<>();
        objectRLock.lock();
        try {
            for (MapleMapObject o : mapobjects.values()) {
                if (o.getType() == MapleMapObjectType.REACTOR) {
                    points.add(((MapleReactor) o).getPosition());
                }
            }
            Collections.shuffle(points);
            for (MapleMapObject o : mapobjects.values()) {
                if (o.getType() == MapleMapObjectType.REACTOR) {
                    ((MapleReactor) o).setPosition(points.remove(points.size() - 1));
                }
            }
        } finally {
            objectRLock.unlock();
        }
    }

    public MapleReactor getReactorById(int Id) {
        objectRLock.lock();
        try {
            for (MapleMapObject obj : mapobjects.values()) {
                if (obj.getType() == MapleMapObjectType.REACTOR) {
                    if (((MapleReactor) obj).getId() == Id) {
                        return (MapleReactor) obj;
                    }
                }
            }
            return null;
        } finally {
            objectRLock.unlock();
        }
    }

    /**
     * Automagically finds a new controller for the given monster from the chars
     * on the map...
     *
     * @param monster
     */
    public void updateMonsterController(MapleMonster monster) {
        monster.monsterLock.lock();
        try {
            if (!monster.isAlive()) {
                return;
            }
            if (monster.getController() != null) {
                if (monster.getController().getMap() != this) {
                    monster.getController().stopControllingMonster(monster);
                } else {
                    return;
                }
            }
            int mincontrolled = -1;
            MapleCharacter newController = null;
            chrRLock.lock();
            try {
                for (MapleCharacter chr : characters) {
                    if (!chr.isHidden() && (chr.getControlledMonsters().size() < mincontrolled || mincontrolled == -1)) {
                        mincontrolled = chr.getControlledMonsters().size();
                        newController = chr;
                    }
                }
            } finally {
                chrRLock.unlock();
            }
            if (newController != null) {// was a new controller found? (if not no one is on the map)
                if (monster.isFirstAttack()) {
                    newController.controlMonster(monster, true);
                    monster.setControllerHasAggro(true);
                    monster.setControllerKnowsAboutAggro(true);
                } else {
                    newController.controlMonster(monster, false);
                }
            }
        } finally {
            monster.monsterLock.unlock();
        }
    }

    public Collection<MapleMapObject> getMapObjects() {
        return Collections.unmodifiableCollection(mapobjects.values());
    }

    public boolean containsNPC(int npcid) {
        if (npcid == 9000066) {
            return true;
        }
        objectRLock.lock();
        try {
            for (MapleMapObject obj : mapobjects.values()) {
                if (obj.getType() == MapleMapObjectType.NPC) {
                    if (((MapleNPC) obj).getId() == npcid) {
                        return true;
                    }
                }
            }
        } finally {
            objectRLock.unlock();
        }
        return false;
    }

    public MapleMapObject getMapObject(int oid) {
        return mapobjects.get(oid);
    }

    /**
     * returns a monster with the given oid, if no such monster exists returns
     * null
     *
     * @param oid
     * @return
     */
    public MapleMonster getMonsterByOid(int oid) {
        MapleMapObject mmo = getMapObject(oid);
        if (mmo == null) {
            return null;
        }
        if (mmo.getType() == MapleMapObjectType.MONSTER) {
            return (MapleMonster) mmo;
        }
        return null;
    }

    public MapleReactor getReactorByOid(int oid) {
        MapleMapObject mmo = getMapObject(oid);
        if (mmo == null) {
            return null;
        }
        return mmo.getType() == MapleMapObjectType.REACTOR ? (MapleReactor) mmo : null;
    }

    public MapleReactor getReactorByName(String name) {
        objectRLock.lock();
        try {
            for (MapleMapObject obj : mapobjects.values()) {
                if (obj.getType() == MapleMapObjectType.REACTOR) {
                    if (((MapleReactor) obj).getName().equals(name)) {
                        return (MapleReactor) obj;
                    }
                }
            }
        } finally {
            objectRLock.unlock();
        }
        return null;
    }

    public void spawnMonsterOnGroudBelow(int id, int x, int y) {
        MapleMonster mob = MapleLifeFactory.getMonster(id);
        spawnMonsterOnGroundBelow(mob, new Point(x, y));
    }

    public void spawnBombOnGroudBelow(int id, int x, int y) {
        MapleMonster mob = MapleLifeFactory.getMonster(id);
        spawnBombOnGroundBelow(mob, new Point(x, y));
    }

    public void spawnMonsterOnGroudBelow(MapleMonster mob, Point pos) {
        spawnMonsterOnGroundBelow(mob, pos);

    }

    public void spawnBombOnGroundBelow(MapleMonster mob, Point pos) {
        Point spos = new Point(pos.x, pos.y - 1);
        spos = calcPointBelow(spos);
        spos.y--;
        mob.setPosition(spos);
        spawnBomb(mob);

    }

    public void spawnMonsterOnGroundBelow(MapleMonster mob, Point pos) {
        Point spos = new Point(pos.x, pos.y - 1);
        spos = calcPointBelow(spos);
        spos.y--;
        mob.setPosition(spos);
        spawnMonster(mob);
    }

    public void spawnCPQMonster(MapleMonster mob, Point pos, int team) {
        Point spos = new Point(pos.x, pos.y - 1);
        spos = calcPointBelow(spos);
        spos.y--;
        mob.setPosition(spos);
        mob.setTeam(team);
        spawnMonster(mob);
    }

    public void addBunnyHit() {
        bunnyDamage++;
        if (bunnyDamage > 5) {
            broadcastMessage(MaplePacketCreator.serverNotice(6, "The Moon Bunny is feeling sick. Please protect it so it can make delicious rice cakes."));
            bunnyDamage = 0;
        }
    }

    private void monsterItemDrop(final MapleMonster m, final Item item, long delay) {
        final ScheduledFuture<?> monsterItemDrop = TimerManager.getInstance().register(new Runnable() {
            @Override
            public void run() {
                if (MapleMap.this.getMonsterById(m.getId()) != null && !MapleMap.this.getAllPlayer().isEmpty()) {
                    if (item.getItemId() == 4001101) {
                        MapleMap.this.riceCakes++;
                        MapleMap.this.broadcastMessage(MaplePacketCreator.serverNotice(6, "The Moon Bunny made rice cake number " + (MapleMap.this.riceCakes)));
                    }
                    spawnItemDrop(m, (MapleCharacter) getAllPlayer().get(0), item, m.getPosition(), false, false);
                }
            }
        }, delay, delay);
        if (getMonsterById(m.getId()) == null) {
            monsterItemDrop.cancel(true);
        }
    }

    public void spawnFakeMonsterOnGroundBelow(MapleMonster mob, Point pos) {
        Point spos = getGroundBelow(pos);
        mob.setPosition(spos);
        spawnFakeMonster(mob);
    }

    public Point getGroundBelow(Point pos) {
        Point spos = new Point(pos.x, pos.y - 3); // Using -3 fixes issues with spawning pets causing a lot of issues.
        spos = calcPointBelow(spos);
        spos.y--;//shouldn't be null!
        return spos;
    }

    public void spawnRevives(final MapleMonster monster) {
        monster.setMap(this);

        spawnAndAddRangedMapObject(monster, new DelayedPacketCreation() {
            @Override
            public void sendPackets(MapleClient c) {

                c.announce(MaplePacketCreator.spawnMonster(monster, false));
            }
        });
        updateMonsterController(monster);
        spawnedMonstersOnMap.incrementAndGet();
    }

    public void spawnMonster(final MapleMonster monster) { // Welp,

        if (mobCapacity != -1 && mobCapacity == spawnedMonstersOnMap.get()) {
            return;//PyPQ
        }
        monster.setMap(this);
        if (!monster.getMap().getAllPlayer().isEmpty()) {
            MapleCharacter chr = (MapleCharacter) getAllPlayer().get(0);
            if (monster.getEventInstance() == null && chr.getEventInstance() != null) {
                chr.getEventInstance().registerMonster(monster);
            }
        }
        /* if(monster.getMap().aggroOn()){
            Random rand = new Random();
            int randnum;
            List<MapleCharacter>players = new ArrayList<>();            
            for(MapleCharacter a1 : monster.getMap().getCharacters())
            {
                if(a1.isAlive())
                 players.add(a1);
            }
            randnum = rand.nextInt(players.size());
            monster.switchController(players.get(randnum), true);            
        }*/

        spawnAndAddRangedMapObject(monster, new DelayedPacketCreation() {
            @Override
            public void sendPackets(MapleClient c) {
                c.announce(MaplePacketCreator.spawnMonster(monster, true));

            }
        }, null);
        updateMonsterController(monster);

        if (monster.getDropPeriodTime() > 0) { //9300102 - Watchhog, 9300061 - Moon Bunny (HPQ)
            if (monster.getId() == 9300102) {
                monsterItemDrop(monster, new Item(4031507, (short) 0, (short) 1), monster.getDropPeriodTime());
            } else if (monster.getId() == 9300061) {
                monsterItemDrop(monster, new Item(4001101, (short) 0, (short) 1), monster.getDropPeriodTime() / 3);
            } else {
                FilePrinter.printError(FilePrinter.UNHANDLED_EVENT, "UNCODED TIMED MOB DETECTED: " + monster.getId());
            }
        }
        spawnedMonstersOnMap.incrementAndGet();
        final selfDestruction selfDestruction = monster.getStats().selfDestruction();
        if (monster.getStats().removeAfter() > 0 || selfDestruction != null && selfDestruction.getHp() < 0) {
            if (selfDestruction == null) {
                TimerManager.getInstance().schedule(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            killMonster(monster, null, false);
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    }
                }, monster.getStats().removeAfter() * 1000);
            } else {
                TimerManager.getInstance().schedule(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            killMonster(monster, null, false, false, selfDestruction.getAction());
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    }
                }, selfDestruction.removeAfter() * 1000);
            }
        }
    }

    public void spawnBomb(final MapleMonster monster) {
        monster.setMap(this);
        //same here
        //   synchronized (this.mapobjects) {
        spawnAndAddRangedMapObject(monster, new DelayedPacketCreation() {

            @Override
            public void sendPackets(MapleClient c) {
                c.getSession().write(MaplePacketCreator.spawnMonster(monster, true));
                if (monster.getId() == 9300166 || monster.getId() == 8810026) {
                    TimerManager.getInstance().schedule(new Runnable() {

                        @Override
                        public void run() {
                            try {
                                killMonster(monster, (MapleCharacter) getAllPlayer().get(0), false, false, 4);
                            } catch (SQLException ex) {
                                ex.printStackTrace();
                            }
                        }
                    }, 1500);
                }
            }
        }, null);
        updateMonsterController(monster);
        // }
        if (monster.getDropPeriodTime() > 0) { //9300102 - Watchhog, 9300061 - Moon Bunny (HPQ)
            if (monster.getId() == 9300102) {
                monsterItemDrop(monster, new Item(4031507, (byte) 0, (short) 1), monster.getDropPeriodTime());
            } else if (monster.getId() == 9300061) {
                monsterItemDrop(monster, new Item(4001101, (byte) 0, (short) 1), monster.getDropPeriodTime() / 3);
            } else {
                System.out.println("UNCODED TIMED MOB DETECTED: " + monster.getId());
            }
        }
        spawnedMonstersOnMap.incrementAndGet();
    }

    /*
     public void spawnBombwithDelay(final MapleMonster monster, final int Delay) {
        monster.setMap(this);
        //same here
        //   synchronized (this.mapobjects) {
        spawnAndAddRangedMapObject(monster, new DelayedPacketCreation() {

            @Override
            public void sendPackets(MapleClient c) {
                c.getSession().write(MaplePacketCreator.spawnMonster(monster, true));
                if (monster.getId() == 9300166 || monster.getId() == 8810026) {
                    TimerManager.getInstance().schedule(new Runnable() {

                        @Override
                        public void run() {
                            killMonster(monster, (MapleCharacter) getAllPlayer().get(0), false, false, 4);
                        }
                    }, Delay*1000);
                }
            }
        }, null);
        updateMonsterController(monster);
        // }
        if (monster.getDropPeriodTime() > 0) { //9300102 - Watchhog, 9300061 - Moon Bunny (HPQ)
            if (monster.getId() == 9300102) {
                monsterItemDrop(monster, new Item(4031507, (byte) 0, (short) 1), monster.getDropPeriodTime());
            } else if (monster.getId() == 9300061) {
                monsterItemDrop(monster, new Item(4001101, (byte) 0, (short) 1), monster.getDropPeriodTime() / 3);
            } else {
                System.out.println("UNCODED TIMED MOB DETECTED: " + monster.getId());
            }
        }
        spawnedMonstersOnMap.incrementAndGet();
    } */
    public void spawnDojoMonster(final MapleMonster monster) {
        Point[] pts = {new Point(140, 0), new Point(190, 7), new Point(187, 7)};
        spawnMonsterWithEffect(monster, 15, pts[Randomizer.nextInt(3)]);
    }

    public void spawnMonsterWithEffect(final MapleMonster monster, final int effect, Point pos) {
        monster.setMap(this);
        Point spos = new Point(pos.x, pos.y - 1);
        spos = calcPointBelow(spos);
        spos.y--;
        monster.setPosition(spos);
        if (mapid < 925020000 || mapid > 925030000) {
            monster.disableDrops();
        }
        spawnAndAddRangedMapObject(monster, new DelayedPacketCreation() {
            @Override
            public void sendPackets(MapleClient c) {
                c.announce(MaplePacketCreator.spawnMonster(monster, true, effect));
            }
        });
        if (monster.hasBossHPBar()) {
            broadcastMessage(monster.makeBossHPBarPacket(), monster.getPosition());
        }
        updateMonsterController(monster);

        spawnedMonstersOnMap.incrementAndGet();
    }

    public void spawnFakeMonster(final MapleMonster monster) {
        monster.setMap(this);
        monster.setFake(true);
        spawnAndAddRangedMapObject(monster, new DelayedPacketCreation() {
            @Override
            public void sendPackets(MapleClient c) {
                c.announce(MaplePacketCreator.spawnFakeMonster(monster, 0));
            }
        });

        spawnedMonstersOnMap.incrementAndGet();
    }

    public void makeMonsterReal(final MapleMonster monster) {
        monster.setFake(false);
        broadcastMessage(MaplePacketCreator.makeMonsterReal(monster));
        updateMonsterController(monster);
    }

    public void spawnReactor(final MapleReactor reactor) {
        reactor.setMap(this);
        spawnAndAddRangedMapObject(reactor, new DelayedPacketCreation() {
            @Override
            public void sendPackets(MapleClient c) {
                c.announce(reactor.makeSpawnData());
            }
        });

    }

    private void respawnReactor(final MapleReactor reactor) {
        reactor.setState((byte) 0);
        reactor.setAlive(true);
        spawnReactor(reactor);
    }

    public void spawnDoor(final MapleDoor door) {
        spawnAndAddRangedMapObject(door, new DelayedPacketCreation() {
            @Override
            public void sendPackets(MapleClient c) {
                c.announce(MaplePacketCreator.spawnDoor(door.getOwner().getId(), door.getTargetPosition(), false));
                if (door.getOwner().getParty() != null && (door.getOwner() == c.getPlayer() || door.getOwner().getParty().containsMembers(c.getPlayer().getMPC()))) {
                    c.announce(MaplePacketCreator.partyPortal(door.getTown().getId(), door.getTarget().getId(), door.getTargetPosition()));
                }
                c.announce(MaplePacketCreator.spawnPortal(door.getTown().getId(), door.getTarget().getId(), door.getTargetPosition()));
                c.announce(MaplePacketCreator.enableActions());
            }
        }, new SpawnCondition() {
            @Override
            public boolean canSpawn(MapleCharacter chr) {
                return chr.getMapId() == door.getTarget().getId() || chr == door.getOwner() && chr.getParty() == null;
            }
        });

    }

    public List<MapleCharacter> getPlayersInRange(Rectangle box, List<MapleCharacter> chr) {
        List<MapleCharacter> character = new LinkedList<>();
        chrRLock.lock();
        try {
            for (MapleCharacter a : characters) {
                if (chr.contains(a.getClient().getPlayer())) {
                    if (box.contains(a.getPosition())) {
                        character.add(a);
                    }
                }
            }
            return character;
        } finally {
            chrRLock.unlock();
        }
    }

    public void spawnSummon(final MapleSummon summon) {
        spawnAndAddRangedMapObject(summon, new DelayedPacketCreation() {
            @Override
            public void sendPackets(MapleClient c) {
                if (summon != null) {
                    c.announce(MaplePacketCreator.spawnSummon(summon, true));
                }
            }
        }, null);
    }

    public void spawnMist(final MapleMist mist, final int duration, boolean poison, boolean fake, boolean recovery) {
        addMapObject(mist);
        broadcastMessage(fake ? mist.makeFakeSpawnData(30) : mist.makeSpawnData());
        TimerManager tMan = TimerManager.getInstance();
        final ScheduledFuture<?> poisonSchedule;
        if (poison) {
            Runnable poisonTask = new Runnable() {
                @Override
                public void run() {
                    List<MapleMapObject> affectedMonsters = getMapObjectsInBox(mist.getBox(), Collections.singletonList(MapleMapObjectType.MONSTER));
                    for (MapleMapObject mo : affectedMonsters) {
                        if (mist.makeChanceResult()) {
                            MonsterStatusEffect poisonEffect = new MonsterStatusEffect(Collections.singletonMap(MonsterStatus.POISON, 1), mist.getSourceSkill(), null, false);
                            ((MapleMonster) mo).applyStatus(mist.getOwner(), poisonEffect, true, duration);
                        }
                    }
                }
            };
            poisonSchedule = tMan.register(poisonTask, 2000, 2500);
        } else if (recovery) {
            Runnable poisonTask = new Runnable() {
                @Override
                public void run() {
                    List<MapleMapObject> players = getMapObjectsInBox(mist.getBox(), Collections.singletonList(MapleMapObjectType.PLAYER));
                    for (MapleMapObject mo : players) {
                        if (mist.makeChanceResult()) {
                            MapleCharacter chr = (MapleCharacter) mo;
                            if (mist.getOwner().getId() == chr.getId() || mist.getOwner().getParty() != null && mist.getOwner().getParty().containsMembers(chr.getMPC())) {
                                chr.addMP((int) mist.getSourceSkill().getEffect(chr.getSkillLevel(mist.getSourceSkill().getId())).getX() * chr.getMp() / 100);
                            }
                        }
                    }
                }
            };
            poisonSchedule = tMan.register(poisonTask, 2000, 2500);
        } else {
            poisonSchedule = null;
        }
        tMan.schedule(new Runnable() {
            @Override
            public void run() {
                removeMapObject(mist);
                if (poisonSchedule != null) {
                    poisonSchedule.cancel(false);
                }
                broadcastMessage(mist.makeDestroyData());
            }
        }, duration);
    }

    public final void spawnItemDrop(final MapleMapObject dropper, final MapleCharacter owner, final Item item, Point pos, final boolean ffaDrop, final boolean playerDrop) {
        final Point droppos = calcDropPos(pos, pos);
        final MapleMapItem drop = new MapleMapItem(item, droppos, dropper, owner, (byte) (ffaDrop ? 2 : 0), playerDrop);

        drop.setDropTime(System.currentTimeMillis());

        spawnAndAddRangedMapObject(drop, new DelayedPacketCreation() {
            @Override
            public void sendPackets(MapleClient c) {
                c.announce(MaplePacketCreator.dropItemFromMapObject(drop, dropper.getPosition(), droppos, (byte) 1));
            }
        }, null);
        broadcastMessage(MaplePacketCreator.dropItemFromMapObject(drop, dropper.getPosition(), droppos, (byte) 0));

        if (!everlast) {
            TimerManager.getInstance().schedule(new ExpireMapItemJob(drop), 180000);
            activateItemReactors(drop, owner.getClient());
        }
    }

    private void activateItemReactors(final MapleMapItem drop, final MapleClient c) {
        final Item item = drop.getItem();

        for (final MapleMapObject o : getAllReactor()) {
            final MapleReactor react = (MapleReactor) o;

            if (react.getReactorType() == 100) {
                if (react.getReactItem((byte) 0).getLeft() == item.getItemId() && react.getReactItem((byte) 0).getRight() == item.getQuantity()) {

                    if (react.getArea().contains(drop.getPosition())) {
                        if (!react.isTimerActive()) {
                            TimerManager.getInstance().schedule(new ActivateItemReactor(drop, react, c), 5000);
                            react.setTimerActive(true);
                            break;
                        }
                    }
                }
            }
        }
    }

    public final List<MapleMapObject> getAllReactor() {
        return getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY, Arrays.asList(MapleMapObjectType.REACTOR));
    }

    public void startMapEffect(String msg, int itemId) {
        startMapEffect(msg, itemId, 30000);
    }

    public void startMapEffect(String msg, int itemId, long time) {
        if (mapEffect != null) {
            return;
        }
        mapEffect = new MapleMapEffect(msg, itemId);
        broadcastMessage(mapEffect.makeStartData());
        TimerManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                broadcastMessage(mapEffect.makeDestroyData());
                mapEffect = null;
            }
        }, time);
    }

    public void addPlayer(final MapleCharacter chr) {
        chrWLock.lock();
        try {
            characters.add(chr);
        } finally {
            chrWLock.unlock();
        }
        chr.setMapId(mapid);
        if (onFirstUserEnter.length() != 0 && !chr.hasEntered(onFirstUserEnter, mapid) && MapScriptManager.getInstance().scriptExists(onFirstUserEnter, true)) {
            if (getAllPlayer().size() <= 1) {
                chr.enteredScript(onFirstUserEnter, mapid);
                MapScriptManager.getInstance().getMapScript(chr.getClient(), onFirstUserEnter, true);
            }
        }
        if (onUserEnter.length() != 0) {
            if (onUserEnter.equals("cygnusTest") && (mapid < 913040000 || mapid > 913040006)) {
                chr.saveLocation("INTRO");
            }
            MapScriptManager.getInstance().getMapScript(chr.getClient(), onUserEnter, false);
        }
        if (FieldLimit.CANNOTUSEMOUNTS.check(fieldLimit) && chr.getBuffedValue(MapleBuffStat.MONSTER_RIDING) != null) {
            chr.cancelEffectFromBuffStat(MapleBuffStat.MONSTER_RIDING);
            chr.cancelBuffStats(MapleBuffStat.MONSTER_RIDING);
        }
        if (mapid == 923010000 && getMonsterById(9300102) == null) { // Kenta's Mount Quest
            spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(9300102), new Point(77, 426));
        } else if (mapid == 910010200) {  // Henesys Party Quest Bonus
            chr.announce(MaplePacketCreator.getClock(60 * 5));
            TimerManager.getInstance().schedule(new Runnable() {

                @Override
                public void run() {
                    if (chr.getMapId() == 910010200) {

                        try {
                            chr.changeMap(910010400);
                        } catch (SQLException ex) {
                            Logger.getLogger(MapleMap.class.getName()).log(Level.SEVERE, null, ex);
                        }

                    }
                }
            }, 5 * 60 * 1000);
        } else if (mapid == 200090060) { // To Rien
            chr.announce(MaplePacketCreator.getClock(60));
            TimerManager.getInstance().schedule(new Runnable() {

                @Override
                public void run() {
                    if (chr.getMapId() == 200090060) {

                        try {
                            chr.changeMap(140020300);
                        } catch (SQLException ex) {
                            Logger.getLogger(MapleMap.class.getName()).log(Level.SEVERE, null, ex);
                        }

                    }
                }
            }, 60 * 1000);
        } else if (mapid == 200090070) { // To Lith Harbor
            chr.announce(MaplePacketCreator.getClock(60));
            TimerManager.getInstance().schedule(new Runnable() {

                @Override
                public void run() {
                    if (chr.getMapId() == 200090070) {
                        try {
                            chr.changeMap(104000000, 3);
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }, 60 * 1000);
        } else if (mapid == 200090030) { // To Ereve (SkyFerry)
            chr.getClient().announce(MaplePacketCreator.getClock(60));
            TimerManager.getInstance().schedule(new Runnable() {

                @Override
                public void run() {
                    if (chr.getMapId() == 200090030) {

                        try {
                            chr.changeMap(130000210);
                        } catch (SQLException ex) {
                            Logger.getLogger(MapleMap.class.getName()).log(Level.SEVERE, null, ex);
                        }

                    }
                }
            }, 60 * 1000);
        } else if (mapid == 200090031) { // To Victoria Island (SkyFerry)
            chr.getClient().announce(MaplePacketCreator.getClock(60));
            TimerManager.getInstance().schedule(new Runnable() {

                @Override
                public void run() {
                    if (chr.getMapId() == 200090031) {

                        try {
                            chr.changeMap(101000400);
                        } catch (SQLException ex) {
                            Logger.getLogger(MapleMap.class.getName()).log(Level.SEVERE, null, ex);
                        }

                    }
                }
            }, 60 * 1000);
        } else if (mapid == 200090021) { // To Orbis (SkyFerry)
            chr.getClient().announce(MaplePacketCreator.getClock(60));
            TimerManager.getInstance().schedule(new Runnable() {

                @Override
                public void run() {
                    if (chr.getMapId() == 200090021) {

                        try {
                            chr.changeMap(200000161);
                        } catch (SQLException ex) {
                            Logger.getLogger(MapleMap.class.getName()).log(Level.SEVERE, null, ex);
                        }

                    }
                }
            }, 60 * 1000);
        } else if (mapid == 200090020) { // To Ereve From Orbis (SkyFerry)
            chr.getClient().announce(MaplePacketCreator.getClock(60));
            TimerManager.getInstance().schedule(new Runnable() {

                @Override
                public void run() {
                    if (chr.getMapId() == 200090020) {

                        try {
                            chr.changeMap(130000210);
                        } catch (SQLException ex) {
                            Logger.getLogger(MapleMap.class.getName()).log(Level.SEVERE, null, ex);
                        }

                    }
                }
            }, 60 * 1000);
        } else if (mapid == 103040400) {
            if (chr.getEventInstance() != null) {
                chr.getEventInstance().movePlayer(chr);
            }
        } else if (MapleMiniDungeon.isDungeonMap(mapid)) {
            final MapleMiniDungeon dungeon = MapleMiniDungeon.getDungeon(mapid);
            chr.getClient().announce(MaplePacketCreator.getClock(30 * 60));
            TimerManager.getInstance().schedule(new Runnable() {

                @Override
                public void run() {
                    if (MapleMiniDungeon.isDungeonMap(chr.getMapId())) {

                        try {
                            chr.changeMap(dungeon.getBase());
                        } catch (SQLException ex) {
                            Logger.getLogger(MapleMap.class.getName()).log(Level.SEVERE, null, ex);
                        }

                    }
                }
            }, 30 * 60 * 1000);
        }
        MaplePet[] pets = chr.getPets();
        for (int i = 0; i < chr.getPets().length; i++) {
            if (pets[i] != null) {
                pets[i].setPos(getGroundBelow(chr.getPosition()));
                chr.announce(MaplePacketCreator.showPet(chr, pets[i], false, false));
            } else {
                break;
            }
        }
        if (chr.isHidden()) {
            broadcastGMMessage(chr, MaplePacketCreator.spawnPlayerMapobject(chr), false);
            chr.announce(MaplePacketCreator.getGMEffect(0x10, (byte) 1));

            List<Pair<MapleBuffStat, Integer>> dsstat = Collections.singletonList(new Pair<MapleBuffStat, Integer>(MapleBuffStat.DARKSIGHT, 0));
            broadcastGMMessage(chr, MaplePacketCreator.giveForeignBuff(chr.getId(), dsstat), false);
        } else {
            broadcastMessage(chr, MaplePacketCreator.spawnPlayerMapobject(chr), false);
        }

        sendObjectPlacement(chr.getClient());
        if (isStartingEventMap() && !eventStarted()) {
            chr.getMap().getPortal("join00").setPortalStatus(false);
        }
        if (hasForcedEquip()) {
            chr.getClient().announce(MaplePacketCreator.showForcedEquip(-1));
        }
        if (specialEquip()) {
            chr.getClient().announce(MaplePacketCreator.coconutScore(0, 0));
            chr.getClient().announce(MaplePacketCreator.showForcedEquip(chr.getTeam()));
        }
        objectWLock.lock();
        try {
            this.mapobjects.put(Integer.valueOf(chr.getObjectId()), chr);
        } finally {
            objectWLock.unlock();
        }
        if (chr.getPlayerShop() != null) {
            addMapObject(chr.getPlayerShop());
        }

        final MapleDragon dragon = chr.getDragon();
        if (dragon != null) {
            dragon.setPosition(chr.getPosition());
            this.addMapObject(dragon);
            if (chr.isHidden()) {
                this.broadcastGMMessage(chr, MaplePacketCreator.spawnDragon(dragon));
            } else {
                this.broadcastMessage(chr, MaplePacketCreator.spawnDragon(dragon));
            }
        }

        MapleStatEffect summonStat = chr.getStatForBuff(MapleBuffStat.SUMMON);
        if (summonStat != null) {
            MapleSummon summon = chr.getSummons().get(summonStat.getSourceId());
            summon.setPosition(chr.getPosition());
            chr.getMap().spawnSummon(summon);
            updateMapObjectVisibility(chr, summon);
        }
        if (mapEffect != null) {
            mapEffect.sendStartData(chr.getClient());
        }
        chr.getClient().announce(MaplePacketCreator.resetForcedStats());
        if (mapid == 914000200 || mapid == 914000210 || mapid == 914000220) {
            chr.getClient().announce(MaplePacketCreator.aranGodlyStats());
        }
        if (chr.getEventInstance() != null && chr.getEventInstance().isTimerStarted()) {
            chr.getClient().announce(MaplePacketCreator.getClock((int) (chr.getEventInstance().getTimeLeft() / 1000)));
        }
        if (chr.getFitness() != null && chr.getFitness().isTimerStarted()) {
            chr.getClient().announce(MaplePacketCreator.getClock((int) (chr.getFitness().getTimeLeft() / 1000)));
        }

        if (chr.getOla() != null && chr.getOla().isTimerStarted()) {
            chr.getClient().announce(MaplePacketCreator.getClock((int) (chr.getOla().getTimeLeft() / 1000)));
        }

        if (mapid == 109060000) {
            chr.announce(MaplePacketCreator.rollSnowBall(true, 0, null, null));
        }

        MonsterCarnival carnival = chr.getCarnival();
        MonsterCarnivalParty cparty = chr.getCarnivalParty();
        if (carnival != null && cparty != null && (mapid == 980000101 || mapid == 980000201 || mapid == 980000301 || mapid == 980000401 || mapid == 980000501 || mapid == 980000601)) {
            chr.getClient().announce(MaplePacketCreator.getClock((int) (carnival.getTimeLeft() / 1000)));
            chr.getClient().announce(MaplePacketCreator.startCPQ(chr, carnival.oppositeTeam(cparty)));
        }
        if (hasClock()) {
            Calendar cal = Calendar.getInstance();
            chr.getClient().announce((MaplePacketCreator.getClockTime(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND))));
        }
        if (hasBoat() == 2) {
            chr.getClient().announce((MaplePacketCreator.boatPacket(true)));
        } else if (hasBoat() == 1 && (chr.getMapId() != 200090000 || chr.getMapId() != 200090010)) {
            chr.getClient().announce(MaplePacketCreator.boatPacket(false));
        }
        chr.receivePartyMemberHP();
    }

    public MaplePortal findClosestPortal(Point from) {
        MaplePortal closest = null;
        double shortestDistance = Double.POSITIVE_INFINITY;
        for (MaplePortal portal : portals.values()) {
            double distance = portal.getPosition().distanceSq(from);
            if (distance < shortestDistance) {
                closest = portal;
                shortestDistance = distance;
            }
        }
        return closest;
    }

    public MaplePortal getRandomSpawnpoint() {
        List<MaplePortal> spawnPoints = new ArrayList<>();
        for (MaplePortal portal : portals.values()) {
            if (portal.getType() >= 0 && portal.getType() <= 2) {
                spawnPoints.add(portal);
            }
        }
        MaplePortal portal = spawnPoints.get(new Random().nextInt(spawnPoints.size()));
        return portal != null ? portal : getPortal(0);
    }

    public void removePlayer(MapleCharacter chr) {
        chrWLock.lock();
        try {
            characters.remove(chr);
        } finally {
            chrWLock.unlock();
        }
        removeMapObject(chr.getObjectId());
        if (!chr.isHidden()) {
            broadcastMessage(MaplePacketCreator.removePlayerFromMap(chr.getId()));
        } else {
            broadcastGMMessage(MaplePacketCreator.removePlayerFromMap(chr.getId()));
        }

        for (MapleMonster monster : chr.getControlledMonsters()) {
            monster.setController(null);
            monster.setControllerHasAggro(false);
            monster.setControllerKnowsAboutAggro(false);
            updateMonsterController(monster);
        }
        chr.leaveMap();
        chr.cancelMapTimeLimitTask();
        for (MapleSummon summon : chr.getSummons().values()) {
            if (summon.isStationary()) {
                chr.cancelBuffStats(MapleBuffStat.PUPPET);
            } else {
                removeMapObject(summon);
            }
        }
        if (chr.getDragon() != null) {
            removeMapObject(chr.getDragon());
            if (chr.isHidden()) {
                this.broadcastGMMessage(chr, MaplePacketCreator.removeDragon(chr.getId()));
            } else {
                this.broadcastMessage(chr, MaplePacketCreator.removeDragon(chr.getId()));
            }
        }
    }

    public void broadcastMessage(final byte[] packet) {
        broadcastMessage(null, packet, Double.POSITIVE_INFINITY, null);
    }

    public void broadcastGMMessage(final byte[] packet) {
        broadcastGMMessage(null, packet, Double.POSITIVE_INFINITY, null);
    }

    /**
     * Nonranged. Repeat to source according to parameter.
     *
     * @param source
     * @param packet
     * @param repeatToSource
     */
    public void broadcastMessage(MapleCharacter source, final byte[] packet, boolean repeatToSource) {
        broadcastMessage(repeatToSource ? null : source, packet, Double.POSITIVE_INFINITY, source.getPosition());
    }

    /**
     * Ranged and repeat according to parameters.
     *
     * @param source
     * @param packet
     * @param repeatToSource
     * @param ranged
     */
    public void broadcastMessage(MapleCharacter source, final byte[] packet, boolean repeatToSource, boolean ranged) {
        broadcastMessage(repeatToSource ? null : source, packet, ranged ? 722500 : Double.POSITIVE_INFINITY, source.getPosition());
    }

    /**
     * Always ranged from Point.
     *
     * @param packet
     * @param rangedFrom
     */
    public void broadcastMessage(final byte[] packet, Point rangedFrom) {
        broadcastMessage(null, packet, 722500, rangedFrom);
    }

    /**
     * Always ranged from point. Does not repeat to source.
     *
     * @param source
     * @param packet
     * @param rangedFrom
     */
    public void broadcastMessage(MapleCharacter source, final byte[] packet, Point rangedFrom) {
        broadcastMessage(source, packet, 722500, rangedFrom);
    }

    private void broadcastMessage(MapleCharacter source, final byte[] packet, double rangeSq, Point rangedFrom) {
        chrRLock.lock();
        try {
            for (MapleCharacter chr : characters) {
                if (chr != source) {
                    if (rangeSq < Double.POSITIVE_INFINITY) {
                        if (rangedFrom.distanceSq(chr.getPosition()) <= rangeSq) {
                            chr.getClient().announce(packet);
                        }
                    } else {
                        chr.getClient().announce(packet);
                    }
                }
            }
        } finally {
            chrRLock.unlock();
        }
    }

    private boolean isNonRangedType(MapleMapObjectType type) {
        switch (type) {
            case NPC:
            case PLAYER:
            case HIRED_MERCHANT:
            case PLAYER_NPC:
            case DRAGON:
            case MIST:
                return true;
            default:
                return false;
        }
    }

    private void sendObjectPlacement(MapleClient mapleClient) {
        MapleCharacter chr = mapleClient.getPlayer();
        objectRLock.lock();
        try {
            for (MapleMapObject o : mapobjects.values()) {
                if (o.getType() == MapleMapObjectType.SUMMON) {
                    MapleSummon summon = (MapleSummon) o;
                    if (summon.getOwner() == chr) {
                        if (chr.getSummons().isEmpty() || !chr.getSummons().containsValue(summon)) {
                            objectWLock.lock();
                            try {
                                mapobjects.remove(o);
                            } finally {
                                objectWLock.unlock();
                            }
                            continue;
                        }
                    }
                }
                if (isNonRangedType(o.getType())) {
                    o.sendSpawnData(mapleClient);
                } else if (o.getType() == MapleMapObjectType.MONSTER) {
                    updateMonsterController((MapleMonster) o);
                }
            }
        } finally {
            objectRLock.unlock();
        }
        if (chr != null) {
            for (MapleMapObject o : getMapObjectsInRange(chr.getPosition(), 722500, rangedMapobjectTypes)) {
                if (o.getType() == MapleMapObjectType.REACTOR) {
                    if (((MapleReactor) o).isAlive()) {
                        o.sendSpawnData(chr.getClient());
                        chr.addVisibleMapObject(o);
                    }
                } else {
                    o.sendSpawnData(chr.getClient());
                    chr.addVisibleMapObject(o);
                }
            }
        }
    }

    public List<MapleMapObject> getMapObjectsInRange(Point from, double rangeSq, List<MapleMapObjectType> types) {
        List<MapleMapObject> ret = new LinkedList<>();
        objectRLock.lock();
        try {
            for (MapleMapObject l : mapobjects.values()) {
                if (types.contains(l.getType())) {
                    if (from.distanceSq(l.getPosition()) <= rangeSq) {
                        ret.add(l);
                    }
                }
            }
            return ret;
        } finally {
            objectRLock.unlock();
        }
    }

    public List<MapleMapObject> getMapObjectsInBox(Rectangle box, List<MapleMapObjectType> types) {
        List<MapleMapObject> ret = new LinkedList<>();
        objectRLock.lock();
        try {
            for (MapleMapObject l : mapobjects.values()) {
                if (types.contains(l.getType())) {
                    if (box.contains(l.getPosition())) {
                        ret.add(l);
                    }
                }
            }
            return ret;
        } finally {
            objectRLock.unlock();
        }
    }

    public void addPortal(MaplePortal myPortal) {
        portals.put(myPortal.getId(), myPortal);
    }

    public MaplePortal getPortal(String portalname) {
        for (MaplePortal port : portals.values()) {
            if (port.getName().equals(portalname)) {
                return port;
            }
        }
        return null;
    }

    public MaplePortal getPortal(int portalid) {
        return portals.get(portalid);
    }

    public void addMapleArea(Rectangle rec) {
        areas.add(rec);
    }

    public List<Rectangle> getAreas() {
        return new ArrayList<>(areas);
    }

    public Rectangle getArea(int index) {
        return areas.get(index);
    }

    public void setFootholds(MapleFootholdTree footholds) {
        this.footholds = footholds;
    }

    public MapleFootholdTree getFootholds() {
        return footholds;
    }

    /**
     * it's threadsafe, gtfo :D
     *
     * @param monster
     * @param mobTime
     */
    public void addMonsterSpawn(MapleMonster monster, int mobTime, int team) {
        Point newpos = calcPointBelow(monster.getPosition());
        newpos.y -= 1;
        SpawnPoint sp = new SpawnPoint(monster, newpos, !monster.isMobile(), mobTime, mobInterval, team);
        monsterSpawn.add(sp);
        if (sp.shouldSpawn() || mobTime == -1) {// -1 does not respawn and should not either but force ONE spawn
            spawnMonster(sp.getMonster());
        }

    }

    public Collection<MapleCharacter> getCharacters() {
        return Collections.unmodifiableCollection(this.characters);
    }

    public MapleCharacter getCharacterById(int id) {
        chrRLock.lock();
        try {
            for (MapleCharacter c : this.characters) {
                if (c.getId() == id) {
                    return c;
                }
            }
        } finally {
            chrRLock.unlock();
        }
        return null;
    }
   
    private void updateMapObjectVisibility(MapleCharacter chr, MapleMapObject mo) {
        if (!chr.isMapObjectVisible(mo)) { // monster entered view range
            if (mo.getType() == MapleMapObjectType.SUMMON || mo.getPosition().distanceSq(chr.getPosition()) <= 722500) {
                chr.addVisibleMapObject(mo);
                mo.sendSpawnData(chr.getClient());
            }
        } else if (mo.getType() != MapleMapObjectType.SUMMON && mo.getPosition().distanceSq(chr.getPosition()) > 722500) {
            chr.removeVisibleMapObject(mo);
            mo.sendDestroyData(chr.getClient());
        }
    }

    public void moveMonster(MapleMonster monster, Point reportedPos) {
        monster.setPosition(reportedPos);
        chrRLock.lock();
        try {
            for (MapleCharacter chr : characters) {
                updateMapObjectVisibility(chr, monster);
            }
        } finally {
            chrRLock.unlock();
        }
    }

    public void movePlayer(MapleCharacter player, Point newPosition) {
        player.setPosition(newPosition);
        Collection<MapleMapObject> visibleObjects = player.getVisibleMapObjects();
        MapleMapObject[] visibleObjectsNow = visibleObjects.toArray(new MapleMapObject[visibleObjects.size()]);
        try {
            for (MapleMapObject mo : visibleObjectsNow) {
                if (mo != null) {
                    if (mapobjects.get(mo.getObjectId()) == mo) {
                        updateMapObjectVisibility(player, mo);
                    } else {
                        player.removeVisibleMapObject(mo);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (MapleMapObject mo : getMapObjectsInRange(player.getPosition(), 722500, rangedMapobjectTypes)) {
            if (!player.isMapObjectVisible(mo)) {
                mo.sendSpawnData(player.getClient());
                player.addVisibleMapObject(mo);
            }
        }
    }

    public MaplePortal findClosestSpawnpoint(Point from) {
        MaplePortal closest = null;
        double shortestDistance = Double.POSITIVE_INFINITY;
        for (MaplePortal portal : portals.values()) {
            double distance = portal.getPosition().distanceSq(from);
            if (portal.getType() >= 0 && portal.getType() <= 2 && distance < shortestDistance && portal.getTargetMapId() == 999999999) {
                closest = portal;
                shortestDistance = distance;
            }
        }
        return closest;
    }

    public Collection<MaplePortal> getPortals() {
        return Collections.unmodifiableCollection(portals.values());
    }

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setClock(boolean hasClock) {
        this.clock = hasClock;
    }

    public boolean hasClock() {
        return clock;
    }

    public void setTown(boolean isTown) {
        this.town = isTown;
    }

    public boolean isTown() {
        return town;
    }

    public boolean isMuted() {
        return isMuted;
    }

    public void setMuted(boolean mute) {
        isMuted = mute;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public void setEverlast(boolean everlast) {
        this.everlast = everlast;
    }

    public boolean getEverlast() {
        return everlast;
    }

    public int getSpawnedMonstersOnMap() {
        return spawnedMonstersOnMap.get();
    }

    public void setMobCapacity(int capacity) {
        this.mobCapacity = capacity;
    }

    public void setBackgroundTypes(HashMap<Integer, Integer> backTypes) {
        backgroundTypes.putAll(backTypes);
    }

    // not really costly to keep generating imo
    public void sendNightEffect(MapleCharacter mc) {
        for (Entry<Integer, Integer> types : backgroundTypes.entrySet()) {
            if (types.getValue() >= 3) { // 3 is a special number
                mc.announce(MaplePacketCreator.changeBackgroundEffect(true, types.getKey(), 0));
            }
        }
    }

    public void broadcastNightEffect() {
        chrRLock.lock();
        try {
            for (MapleCharacter c : characters) {
                sendNightEffect(c);
            }
        } finally {
            chrRLock.unlock();
        }
    }

    public MapleCharacter getCharacterByName(String name) {
        chrRLock.lock();
        try {
            for (MapleCharacter c : this.characters) {
                if (c.getName().toLowerCase().equals(name.toLowerCase())) {
                    return c;
                }
            }
        } finally {
            chrRLock.unlock();
        }
        return null;
    }

    public void killAllMonsters(boolean drop) {
		List<MapleMapObject> players = null;
		if (drop) {
			players = getAllPlayer();
		}
		List<MapleMapObject> monsters = getMapObjectsInRange(new Point(0, 0),
				Double.POSITIVE_INFINITY,
				Arrays.asList(MapleMapObjectType.MONSTER));
		for (MapleMapObject monstermo : monsters) {
			MapleMonster monster = (MapleMonster) monstermo;
			spawnedMonstersOnMap.decrementAndGet();
			monster.setHp(0);
			broadcastMessage(
					MaplePacketCreator.killMonster(monster.getObjectId(), true),
					monster.getPosition());
			removeMapObject(monster);
			if (drop) {
				int random = (int) Math.random() * (players.size());
				dropFromMonster((MapleCharacter) players.get(random), monster);
			}
		}
	}
    

    private class ExpireMapItemJob implements Runnable {

        private MapleMapItem mapitem;

        public ExpireMapItemJob(MapleMapItem mapitem) {
            this.mapitem = mapitem;
        }

        @Override
        public void run() {
            if (mapitem != null && mapitem == getMapObject(mapitem.getObjectId())) {
                mapitem.itemLock.lock();
                try {
                    if (mapitem.isPickedUp()) {
                        return;
                    }
                    MapleMap.this.broadcastMessage(MaplePacketCreator.removeItemFromMap(mapitem.getObjectId(), 0, 0), mapitem.getPosition());
                    mapitem.setPickedUp(true);
                } finally {
                    mapitem.itemLock.unlock();
                    MapleMap.this.removeMapObject(mapitem);
                }
            }
        }
    }

    private class ActivateItemReactor implements Runnable {

        private MapleMapItem mapitem;
        private MapleReactor reactor;
        private MapleClient c;

        public ActivateItemReactor(MapleMapItem mapitem, MapleReactor reactor, MapleClient c) {
            this.mapitem = mapitem;
            this.reactor = reactor;
            this.c = c;
        }

        @Override
        public void run() {
            if (mapitem != null && mapitem == getMapObject(mapitem.getObjectId())) {
                mapitem.itemLock.lock();
                try {
                    TimerManager tMan = TimerManager.getInstance();
                    if (mapitem.isPickedUp()) {
                        return;
                    }
                    MapleMap.this.broadcastMessage(MaplePacketCreator.removeItemFromMap(mapitem.getObjectId(), 0, 0), mapitem.getPosition());
                    MapleMap.this.removeMapObject(mapitem);
                    reactor.hitReactor(c);
                    reactor.setTimerActive(false);
                    if (reactor.getDelay() > 0) {
                        tMan.schedule(new Runnable() {
                            @Override
                            public void run() {
                                reactor.setState((byte) 0);
                                broadcastMessage(MaplePacketCreator.triggerReactor(reactor, 0));
                            }
                        }, reactor.getDelay());
                    }
                } finally {
                    mapitem.itemLock.unlock();
                }
            }
        }
    }

    public void instanceMapRespawn() {
        final int numShouldSpawn = (short) ((monsterSpawn.size() - spawnedMonstersOnMap.get()));//Fking lol'd
        if (numShouldSpawn > 0) {
            List<SpawnPoint> randomSpawn = new ArrayList<>(monsterSpawn);
            Collections.shuffle(randomSpawn);
            int spawned = 0;
            for (SpawnPoint spawnPoint : randomSpawn) {
                spawnMonster(spawnPoint.getMonster());
                spawned++;
                if (spawned >= numShouldSpawn) {
                    break;
                }
            }
        }
    }

    public void respawn() {
        if (characters.isEmpty()) {
            return;
        }
        short numShouldSpawn = (short) ((monsterSpawn.size() - spawnedMonstersOnMap.get()));//Fking lol'd
        if (numShouldSpawn > 0) {
            List<SpawnPoint> randomSpawn = new ArrayList<>(monsterSpawn);
            Collections.shuffle(randomSpawn);
            short spawned = 0;
            for (SpawnPoint spawnPoint : randomSpawn) {
                if (spawnPoint.shouldSpawn()) {
                    spawnMonster(spawnPoint.getMonster());
                    spawned++;
                }
                if (spawned >= numShouldSpawn) {
                    break;
                }
            }
        }
    }

    private static interface DelayedPacketCreation {

        void sendPackets(MapleClient c);
    }

    private static interface SpawnCondition {

        boolean canSpawn(MapleCharacter chr);
    }

    public int getHPDec() {
        return decHP;
    }

    public void setHPDec(int delta) {
        decHP = delta;
    }

    public int getHPDecProtect() {
        return protectItem;
    }

    public void setHPDecProtect(int delta) {
        this.protectItem = delta;
    }

    private int hasBoat() {
        return docked ? 2 : (boat ? 1 : 0);
    }

    public void setBoat(boolean hasBoat) {
        this.boat = hasBoat;
    }

    public void setDocked(boolean isDocked) {
        this.docked = isDocked;
    }

    public void broadcastGMMessage(MapleCharacter source, final byte[] packet, boolean repeatToSource) {
        broadcastGMMessage(repeatToSource ? null : source, packet, Double.POSITIVE_INFINITY, source.getPosition());
    }

    private void broadcastGMMessage(MapleCharacter source, final byte[] packet, double rangeSq, Point rangedFrom) {
        chrRLock.lock();
        try {
            for (MapleCharacter chr : characters) {
                if (chr != source && chr.isGM()) {
                    if (rangeSq < Double.POSITIVE_INFINITY) {
                        if (rangedFrom.distanceSq(chr.getPosition()) <= rangeSq) {
                            chr.getClient().announce(packet);
                        }
                    } else {
                        chr.getClient().announce(packet);
                    }
                }
            }
        } finally {
            chrRLock.unlock();
        }
    }

    public void broadcastNONGMMessage(MapleCharacter source, final byte[] packet, boolean repeatToSource) {
        chrRLock.lock();
        try {
            for (MapleCharacter chr : characters) { 
                if (chr != source && (!chr.isGM() || chr.isTemp())) {
                    chr.getClient().announce(packet);
                }
            }
        } finally {
            chrRLock.unlock();
        }
    }

    public MapleOxQuiz getOx() {
        return ox;
    }

    public void setOx(MapleOxQuiz set) {
        this.ox = set;
    }

    public void setOxQuiz(boolean b) {
        this.isOxQuiz = b;
    }

    public boolean isOxQuiz() {
        return isOxQuiz;
    }

    public void setOnUserEnter(String onUserEnter) {
        this.onUserEnter = onUserEnter;
    }

    public String getOnUserEnter() {
        return onUserEnter;
    }

    public void setOnFirstUserEnter(String onFirstUserEnter) {
        this.onFirstUserEnter = onFirstUserEnter;
    }

    public String getOnFirstUserEnter() {
        return onFirstUserEnter;
    }

    private boolean hasForcedEquip() {
        return fieldType == 81 || fieldType == 82;
    }

    public void setFieldType(int fieldType) {
        this.fieldType = fieldType;
    }

    public void clearDrops(MapleCharacter player) {
        List<MapleMapObject> items = player.getMap().getMapObjectsInRange(player.getPosition(), Double.POSITIVE_INFINITY, Arrays.asList(MapleMapObjectType.ITEM));
        for (MapleMapObject i : items) {
            player.getMap().removeMapObject(i);
            player.getMap().broadcastMessage(MaplePacketCreator.removeItemFromMap(i.getObjectId(), 0, player.getId()));
        }
    }

    public void clearDrops() {
        for (MapleMapObject i : getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY, Arrays.asList(MapleMapObjectType.ITEM))) {
            removeMapObject(i);
            this.broadcastMessage(MaplePacketCreator.removeItemFromMap(i.getObjectId(), 0, 0));
        }
    }

    public void addMapTimer(int time) {
        timeLimit = System.currentTimeMillis() + (time * 1000);
        broadcastMessage(MaplePacketCreator.getClock(time));
        mapMonitor = TimerManager.getInstance().register(new Runnable() {
            @Override
            public void run() {
                if (timeLimit != 0 && timeLimit < System.currentTimeMillis()) {
                    try {
                        warpEveryone(getForcedReturnId());
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
                if (getCharacters().isEmpty()) {
                    resetReactors();
                    killAllMonsters();
                    clearDrops();
                    timeLimit = 0;
                    if (mapid >= 922240100 && mapid <= 922240119) {
                        toggleHiddenNPC(9001108);
                    }
                    mapMonitor.cancel(true);
                    mapMonitor = null;
                }
            }
        }, 1000);
    }

    public void setFieldLimit(int fieldLimit) {
        this.fieldLimit = fieldLimit;
    }

    public int getFieldLimit() {
        return fieldLimit;
    }

    public void resetRiceCakes() {
        this.riceCakes = 0;
    }

    public void allowSummonState(boolean b) {
        MapleMap.this.allowSummons = b;
    }

    public boolean getSummonState() {
        return MapleMap.this.allowSummons;
    }

    public void warpEveryone(int to) throws SQLException {
        List<MapleCharacter> players;
        chrRLock.lock();
        try {
            players = new ArrayList<>(getCharacters());
        } finally {
            chrRLock.unlock();
        }

        for (MapleCharacter chr : players) {
            chr.changeMap(to);
        }
    }

    // BEGIN EVENTS
    public void setSnowball(int team, MapleSnowball ball) {
        switch (team) {
            case 0:
                this.snowball0 = ball;
                break;
            case 1:
                this.snowball1 = ball;
                break;
            default:
                break;
        }
    }

    public MapleSnowball getSnowball(int team) {
        switch (team) {
            case 0:
                return snowball0;
            case 1:
                return snowball1;
            default:
                return null;
        }
    }

    private boolean specialEquip() {//Maybe I shouldn't use fieldType :\
        return fieldType == 4 || fieldType == 19;
    }

    public void setCoconut(MapleCoconut nut) {
        this.coconut = nut;
    }

    public MapleCoconut getCoconut() {
        return coconut;
    }

    public void warpOutByTeam(int team, int mapid) throws SQLException {
        List<MapleCharacter> chars = new ArrayList<>(getCharacters());

        for (MapleCharacter chr : chars) {
            if (chr != null) {
                if (chr.getTeam() == team) {
                    chr.changeMap(mapid);
                }
            }
        }
    }

    public void startEvent(final MapleCharacter chr) {
        if (this.mapid == 109080000 && getCoconut() == null) {
            setCoconut(new MapleCoconut(this));
            coconut.startEvent();
        } else if (this.mapid == 109040000) {
            chr.setFitness(new MapleFitness(chr));
            chr.getFitness().startFitness();
        } else if (this.mapid == 109030101 || this.mapid == 109030201 || this.mapid == 109030301 || this.mapid == 109030401) {
            chr.setOla(new MapleOla(chr));
            chr.getOla().startOla();
        } else if (this.mapid == 109020001 && getOx() == null) {
            setOx(new MapleOxQuiz(this));
            getOx().sendQuestion();
            setOxQuiz(true);
        } else if (this.mapid == 109060000 && getSnowball(chr.getTeam()) == null) {
            setSnowball(0, new MapleSnowball(0, this));
            setSnowball(1, new MapleSnowball(1, this));
            getSnowball(chr.getTeam()).startEvent();
        }
    }

    public boolean eventStarted() {
        return eventstarted;
    }

    public void startEvent() {
        this.eventstarted = true;
    }

    public void setEventStarted(boolean event) {
        this.eventstarted = event;
    }

    public String getEventNPC() {
        StringBuilder sb = new StringBuilder();
        sb.append("Talk to ");
        if (mapid == 60000) {
            sb.append("Paul!");
        } else if (mapid == 104000000) {
            sb.append("Jean!");
        } else if (mapid == 200000000) {
            sb.append("Martin!");
        } else if (mapid == 220000000) {
            sb.append("Tony!");
        } else {
            return null;
        }
        return sb.toString();
    }

    public boolean hasEventNPC() {
        return this.mapid == 60000 || this.mapid == 104000000 || this.mapid == 200000000 || this.mapid == 220000000;
    }

    public boolean isStartingEventMap() {
        return this.mapid == 109040000 || this.mapid == 109020001 || this.mapid == 109010000 || this.mapid == 109030001 || this.mapid == 109030101;
    }

    public boolean isEventMap() {
        return this.mapid >= 109010000 && this.mapid < 109050000 || this.mapid > 109050001 && this.mapid <= 109090000;
    }

    public boolean isTutMap() {
        return this.mapid == 450005010 || this.mapid == 104000000 || this.mapid == 200000000 || this.mapid == 220000000;
    }

    public void timeMob(int id, String msg) {
        timeMob = new Pair<>(id, msg);
    }

    public Pair<Integer, String> getTimeMob() {
        return timeMob;
    }

    public void toggleHiddenNPC(int id) {
        for (MapleMapObject obj : mapobjects.values()) {
            if (obj.getType() == MapleMapObjectType.NPC) {
                MapleNPC npc = (MapleNPC) obj;
                if (npc.getId() == id) {
                    npc.setHide(!npc.isHidden());
                    if (!npc.isHidden()) //Should only be hidden upon changing maps
                    {
                        broadcastMessage(MaplePacketCreator.spawnNPC(npc));
                    }
                }
            }
        }
    }

    public void setMobInterval(short interval) {
        this.mobInterval = interval;
    }

    public short getMobInterval() {
        return mobInterval;
    }
    
    public int getAKPositionB() {
        return akPositionB;
    }
    
    public void setAKPositionB(int pos) {
        akPositionB = pos;
    }
    
    public int getAKPositionA() {
        return akPositionA;
    }
    
    public void setAKPositionA(int pos) {
        akPositionA = pos;
    }
    
    public boolean getAutoKill() {
        return autoKill;
    }
    
    public void setAutoKill(boolean ak) {
        autoKill = ak;
    }
    public int getMonsterCount() {
		List<MapleMapObject> monsters = getMapObjectsInRange(new Point(0, 0),
				Double.POSITIVE_INFINITY,
				Arrays.asList(MapleMapObjectType.MONSTER));
		return monsters.size();
	}
    
    public void setTagRange(double range) {
        tagRange = range;
    }

    public double getTagRange() {
        return tagRange;
    }
    
    public boolean getOlympicsState() {
        return this.olympics;
    }
    
    public void changeOlympicsState() {
        this.olympics = !this.olympics;
    }
    
    /* Olympics */
    public void shuffleOlympicTeams() throws SQLException {
        List<MapleMapObject> allPlayers = getAllPlayer();
         Collections.shuffle(allPlayers);
        this.teamRed = new ArrayList<MapleCharacter>();
        this.teamBlue = new ArrayList<MapleCharacter>();
        for (MapleMapObject mmo : allPlayers) {
            if (mmo instanceof MapleCharacter) {
                MapleCharacter mc = (MapleCharacter)mmo;
                if(getAllPlayer().indexOf(mc) % 2 == 0) teamRed.add((MapleCharacter)mc);
                else teamBlue.add((MapleCharacter)mc);
            }
        }
        tagPlayersByTeam();
    }
    
    private void tagPlayersByTeam() throws SQLException {
        if(teamRed.isEmpty() || teamBlue.isEmpty()) return;
        for(MapleCharacter mc : teamRed) {
            if(mc == null) continue;
            else {
                mc.setTeam(0);
                mc.eqpByTeam();
            }
        }
        for(MapleCharacter mc : teamBlue) {
            if(mc == null) continue;
            else {
                mc.setTeam(1);
               mc.eqpByTeam();
            }
        }
        rewarpMap();
    }
    
    public void endOlympics() {
        if(!olympics) return;
        if(teamRed.isEmpty() || teamBlue.isEmpty()) return;
        for(MapleCharacter mc : teamRed) {
            if(mc == null) continue;
            else mc.setTeam(-1);
        }
        for(MapleCharacter mc : teamBlue) {
            if(mc == null) continue;
            else mc.setTeam(-1);
        }
        teamRed = null;
        teamBlue = null;
    }
    
    public void rewarpMap() throws SQLException {
        //todo: warp by sides
        if(!olympics) return;
        if(teamRed.isEmpty() || teamBlue.isEmpty()) return;
        for(MapleCharacter mc : teamRed) {
            if(mc == null) continue;
            else mc.changeMap(mc.getMap());
        }
        for(MapleCharacter mc : teamBlue) {
            if(mc == null) continue;
            else mc.changeMap(mc.getMap());
        }
    }
    
    public void setBongo(Bongo bongo) { 
        this.bongo = bongo; 
    }
    
    public Bongo getBongo() { 
        return this.bongo; 
    }
}
