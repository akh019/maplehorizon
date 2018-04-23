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
package server.life;

import client.MapleCharacter;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import provider.MapleData;
import provider.MapleDataProvider;
import provider.MapleDataProviderFactory;
import provider.MapleDataTool;
import provider.wz.MapleDataType;
import tools.Pair;
import tools.StringUtil;

public class MapleLifeFactory {

    private static MapleDataProvider data = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("wzpath") + "/Mob.wz"));
    private final static MapleDataProvider stringDataWZ = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("wzpath") + "/String.wz"));
    private static MapleData mobStringData = stringDataWZ.getData("Mob.img");
    private static MapleData npcStringData = stringDataWZ.getData("Npc.img");
    private static Map<Integer, MapleMonsterStats> monsterStats = new HashMap<>();
    private static boolean aggro = false;

    public static AbstractLoadedMapleLife getLife(int id, String type) {
        if (type.equalsIgnoreCase("n")) {
            return getNPC(id);
        } else if (type.equalsIgnoreCase("m")) {
            return getMonster(id);
        } else {
            System.out.println("Unknown Life type: " + type);
            return null;
        }
    }
    public static boolean isAggro(){
        return aggro;
    }
    public static void setAggro(boolean on){
        monsterStats.clear();
        aggro = on;
    }
    public static MapleMonster getMonster(int mid) {
        MapleMonsterStats stats = monsterStats.get(Integer.valueOf(mid));
        
        if (stats == null || mid == 9500365 || isAggro()) {
            MapleData monsterData = data.getData(StringUtil.getLeftPaddedStr(Integer.toString(mid) + ".img", '0', 11));
            if (monsterData == null) {
                return null;
            }
            MapleData monsterInfoData = monsterData.getChildByPath("info");
            stats = new MapleMonsterStats();
            if(mid == 9500365)
             stats.setHp(1);
            else
             stats.setHp(MapleDataTool.getIntConvert("maxHP", monsterInfoData));
            stats.setFriendly(MapleDataTool.getIntConvert("damagedByMob", monsterInfoData, 0) == 1);
            stats.setPADamage(MapleDataTool.getIntConvert("PADamage", monsterInfoData));
            stats.setPDDamage(MapleDataTool.getIntConvert("PDDamage", monsterInfoData));
            stats.setMADamage(MapleDataTool.getIntConvert("MADamage", monsterInfoData));
            stats.setMDDamage(MapleDataTool.getIntConvert("MDDamage", monsterInfoData));  
            stats.setMp(MapleDataTool.getIntConvert("maxMP", monsterInfoData, 0));
        //    if()
            stats.setExp(MapleDataTool.getIntConvert("exp", monsterInfoData, 0));
            stats.setLevel(MapleDataTool.getIntConvert("level", monsterInfoData));
            stats.setRemoveAfter(MapleDataTool.getIntConvert("removeAfter", monsterInfoData, 0));
            stats.setBoss(MapleDataTool.getIntConvert("boss", monsterInfoData, 0) > 0);
            stats.setExplosiveReward(MapleDataTool.getIntConvert("explosiveReward", monsterInfoData, 0) > 0);
            stats.setFfaLoot(MapleDataTool.getIntConvert("publicReward", monsterInfoData, 0) > 0);
            stats.setUndead(MapleDataTool.getIntConvert("undead", monsterInfoData, 0) > 0);
            stats.setName(MapleDataTool.getString(mid + "/name", mobStringData, "MISSINGNO"));
            stats.setBuffToGive(MapleDataTool.getIntConvert("buff", monsterInfoData, -1));
            if(mid == 9400121)
                System.out.print(MapleDataTool.getIntConvert("getCP", monsterInfoData, 0));
            stats.setCP(MapleDataTool.getIntConvert("getCP", monsterInfoData, 0));
            stats.setRemoveOnMiss(MapleDataTool.getIntConvert("removeOnMiss", monsterInfoData, 0) > 0);

            MapleData special = monsterInfoData.getChildByPath("coolDamage");
            if (special != null) {
                int coolDmg = MapleDataTool.getIntConvert("coolDamage", monsterInfoData);
                int coolProb = MapleDataTool.getIntConvert("coolDamageProb", monsterInfoData, 0);
                stats.setCool(new Pair<>(coolDmg, coolProb));
            }
            special = monsterInfoData.getChildByPath("loseItem");
            if (special != null) {
                for (MapleData liData : special.getChildren()) {
                    stats.addLoseItem(new loseItem(MapleDataTool.getInt(liData.getChildByPath("id")), (byte) MapleDataTool.getInt(liData.getChildByPath("prop")), (byte) MapleDataTool.getInt(liData.getChildByPath("x"))));
                }
            }
            special = monsterInfoData.getChildByPath("selfDestruction");
            if (special != null) {
                stats.setSelfDestruction(new selfDestruction((byte) MapleDataTool.getInt(special.getChildByPath("action")), MapleDataTool.getIntConvert("removeAfter", special, -1), MapleDataTool.getIntConvert("hp", special, -1)));
            }
            
            MapleData firstAttackData = monsterInfoData.getChildByPath("firstAttack");                       
            int firstAttack = 0;
            if (firstAttackData != null) {
                if (firstAttackData.getType() == MapleDataType.FLOAT) {
                    firstAttack = Math.round(MapleDataTool.getFloat(firstAttackData));
                } else {
                    firstAttack = MapleDataTool.getInt(firstAttackData);
                }
            }
            if(!isAggro())
             stats.setFirstAttack(firstAttack > 0);
            else
                stats.setFirstAttack(true);
            stats.setDropPeriod(MapleDataTool.getIntConvert("dropItemPeriod", monsterInfoData, 0) * 10000);
            
            stats.setTagColor(MapleDataTool.getIntConvert("hpTagColor", monsterInfoData, 0));
            stats.setTagBgColor(MapleDataTool.getIntConvert("hpTagBgcolor", monsterInfoData, 0));

            for (MapleData idata : monsterData) {
                if (!idata.getName().equals("info")) {
                    int delay = 0;
                    for (MapleData pic : idata.getChildren()) {
                        delay += MapleDataTool.getIntConvert("delay", pic, 0);
                    }
                    stats.setAnimationTime(idata.getName(), delay);
                }
            }
            MapleData reviveInfo = monsterInfoData.getChildByPath("revive");
            if (reviveInfo != null) {
                List<Integer> revives = new LinkedList<>();
                for (MapleData data_ : reviveInfo) {
                    revives.add(MapleDataTool.getInt(data_));
                }
                stats.setRevives(revives);
            }
            decodeElementalString(stats, MapleDataTool.getString("elemAttr", monsterInfoData, ""));
            MapleData monsterSkillData = monsterInfoData.getChildByPath("skill");
            if (monsterSkillData != null) {
                int i = 0;
                List<Pair<Integer, Integer>> skills = new ArrayList<>();
                while (monsterSkillData.getChildByPath(Integer.toString(i)) != null) {
                    skills.add(new Pair<>(Integer.valueOf(MapleDataTool.getInt(i + "/skill", monsterSkillData, 0)), Integer.valueOf(MapleDataTool.getInt(i + "/level", monsterSkillData, 0))));
                    i++;
                }
                stats.setSkills(skills);
            }
            MapleData banishData = monsterInfoData.getChildByPath("ban");
            if (banishData != null) {
                stats.setBanishInfo(new BanishInfo(MapleDataTool.getString("banMsg", banishData), MapleDataTool.getInt("banMap/0/field", banishData, -1), MapleDataTool.getString("banMap/0/portal", banishData, "sp")));
            }
            monsterStats.put(Integer.valueOf(mid), stats);
        }
        
        MapleMonster ret = new MapleMonster(mid, stats);
    /*    if(ret.getMap().aggroOn()){
            Random rand = new Random();
            int randnum;
            List<MapleCharacter>players = new ArrayList<>();            
            for(MapleCharacter a1 : ret.getMap().getCharacters())
            {
                if(a1.isAlive())
                 players.add(a1);
            }
            randnum = rand.nextInt(players.size());
            ret.switchController(players.get(randnum), true);            
        }  */
        return ret;
    }

    private static void decodeElementalString(MapleMonsterStats stats, String elemAttr) {
        for (int i = 0; i < elemAttr.length(); i += 2) {
            stats.setEffectiveness(Element.getFromChar(elemAttr.charAt(i)), ElementalEffectiveness.getByNumber(Integer.valueOf(String.valueOf(elemAttr.charAt(i + 1)))));
        }
    }

    public static MapleNPC getNPC(int nid) {
        return new MapleNPC(nid, new MapleNPCStats(MapleDataTool.getString(nid + "/name", npcStringData, "MISSINGNO")));
    }

    public static class BanishInfo {

        private int map;
        private String portal, msg;

        public BanishInfo(String msg, int map, String portal) {
            this.msg = msg;
            this.map = map;
            this.portal = portal;
        }

        public int getMap() {
            return map;
        }

        public String getPortal() {
            return portal;
        }

        public String getMsg() {
            return msg;
        }
    }

    public static class loseItem {

        private int id;
        private byte chance, x;

        private loseItem(int id, byte chance, byte x) {
            this.id = id;
            this.chance = chance;
            this.x = x;
        }

        public int getId() {
            return id;
        }

        public byte getChance() {
            return chance;
        }

        public byte getX() {
            return x;
        }
    }

    public static class selfDestruction {

        private byte action;
        private int removeAfter;
        private int hp;

        private selfDestruction(byte action, int removeAfter, int hp) {
            this.action = action;
            this.removeAfter = removeAfter;
            this.hp = hp;
        }

        public int getHp() {
            return hp;
        }
        
        public byte getAction() {
            return action;
        }

        public int removeAfter() {
            return removeAfter;
        }
    }
}