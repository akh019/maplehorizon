/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.minigames.solo;

import client.MapleCharacter;
import client.MapleDisease;
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import java.awt.Point;
import java.sql.SQLException;
import java.util.concurrent.ScheduledFuture;
import server.TimerManager;
import server.life.MapleLifeFactory;
import server.life.MobSkillFactory;
import server.maps.MapleMap;
import server.minigames.SoloGameInstance;
import tools.MaplePacketCreator;

/**
 *
 * @author Danny
 * TODO: return map, minigame points, database
 */
public class PandaJump extends SoloGameInstance {
    
    private final MapleCharacter chr;
    private final MapleMap map;
    private int score;
    private boolean side;
    private ScheduledFuture<?> pJump = null;
    private final int mobid = 9300270;
    private final Point left = new Point(-323, 1235);
    private final Point right = new Point(420, 1235);
    
    public PandaJump(MapleCharacter chr, MapleMap map) {
        this.chr = chr;
        this.map = map;
    }

    @Override
    public void displayRules() {
        antiCheat();
        getPlayer().dropMessage(6, "[PandaJump] HOW TO PLAY: ");
        getPlayer().dropMessage(6, "[PandaJump] Dodge the Pandas that spawn from the left and right side!");
        getPlayer().announce(MaplePacketCreator.getClock(5));
        TimerManager.getInstance().schedule(new Runnable() {
            public void run() {
                getPlayer().announce(MaplePacketCreator.showEffect("killing/first/start"));
                startGame();
            }
        }, 5000);
    }

    @Override
    public void startGame() {
        if(pJump == null) {
            pJump = TimerManager.getInstance().register(new Runnable() {
                public void run() {
                    gameFunctions();
                }
            }, 10000);
        }
    }

    @Override
    public void endGame() {
        if(pJump != null) {
            pJump.cancel(true);
            pJump = null;
        }      
        try {
            getPlayer().changeMap(0);
        } catch (SQLException ex) {
            //
        }
        if (score > 2) {
            //getPlayer().gainMGPoints(score);
        }
        getPlayer().announce(MaplePacketCreator.sendHint("#e[PANDAJUMP]#n\r\nYou survived: #b" + score + " rounds.#k\r\nYou have gained: #b" + (score < 3 ? 0 : score) + " minigame points.", 200, 5));
        if (score > 0) {
            saveScore();
        }
        getPlayer().dispelDebuffs(true);
        getPlayer().setGameManager(null);
    }

    @Override
    public MapleCharacter getPlayer() {
        return chr;
    }
    
    @Override
    public MapleMap getMap() {
        return map;
    }

    @Override
    public void compareTextToAnswer(String text) {
        // Unused
    }
    
    private void gameFunctions() {
        getPlayer().cancelAllBuffs(false); // Just incase they manage to use skills/buffs again.
        increaseScore();
        getPlayer().announce(MaplePacketCreator.sendHint("#e[PANDAJUMP]#n\r\nStarting round " + score + ".", 200, 5));
        if (side) {
            map.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(mobid), left);
            side = false;
        } else {
            map.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(mobid), right);
            side = true;
        }
    }

    private void increaseScore() {
        score++;
    }
    
    private void antiCheat() {
        getPlayer().cancelAllBuffs(false);
        if (getPlayer().getJob().getId() / 100 == 3 || getPlayer().getJob().getId() / 100 == 13) { 
            getPlayer().dropMessage(5, "[PandaJump] Please change your job.");
            endGame();
            return;
        }
        for (Item item : getPlayer().getInventory(MapleInventoryType.EQUIPPED)) {
            Equip equip = (Equip) item;
            if (equip.getSpeed() > 0 || equip.getJump() > 0) {
                getPlayer().dropMessage(5, "[PandaJump] Please take off any speed / jump items.");
                endGame();
                return;
            }
        }
        getPlayer().givePermDebuff(MapleDisease.SEAL, MobSkillFactory.getMobSkill(120, 1));
    }
    
    private void saveScore() {
        //
    }
    
}
