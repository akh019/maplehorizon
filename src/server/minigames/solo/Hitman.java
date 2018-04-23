/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.minigames.solo;

import client.MapleCharacter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import server.TimerManager;
import server.maps.MapleMap;
import server.minigames.SoloGameInstance;
import tools.DatabaseConnection;
import tools.MaplePacketCreator;

/**
 *
 * @author Danny
 * TODO: redo trivia methods, database, minigame points, return map
 */
public class Hitman extends SoloGameInstance {
    
    private final MapleCharacter chr;
    private final MapleMap map;
    private int score;
    private String answer = null;
    private ScheduledFuture<?> hitman = null;
    
    public Hitman(MapleCharacter chr, MapleMap map) {
        this.chr = chr;
        this.map = map;
    }

    @Override
    public void displayRules() {
        getPlayer().dropMessage(6, "[Hitman] Game starts in 5 seconds.");
        getPlayer().dropMessage(6, "[Hitman] HOW TO PLAY: ");
        getPlayer().dropMessage(6, "[Hitman] Your goal is to win as many hitman rounds as possible in 1 minute.");
        getPlayer().announce(MaplePacketCreator.getClock(5));
        TimerManager.getInstance().schedule(new Runnable() {
            public void run() {
                getPlayer().announce(MaplePacketCreator.showEffect("killing/first/start"));
                startGame();
            }
        }, 5000);
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
    public void startGame() {
        gameFunctions();
        if(hitman == null) {
            hitman = TimerManager.getInstance().schedule(new Runnable() {
                public void run() {
                    endGame();
                }
            }, 60000);
        }
    }    
    
    @Override
    public void compareTextToAnswer(String text) {
        if(answer != null && !answer.isEmpty()) {
            if(text.equals(answer)) {
                answer = null;
                increaseScore();
                gameFunctions();
                getPlayer().announce(MaplePacketCreator.sendHint("#e[HITMAN]#n\r\nYou have entered the text correctly. \r\nYour score is now: #b" + score + ".", 200, 5));
            } else {
                getPlayer().announce(MaplePacketCreator.sendHint("#e[HITMAN]#n\r\nYou have entered the text incorrectly.", 200, 5));
            }
        }
    }

    @Override
    public void endGame() {
        if(hitman != null) {
            hitman.cancel(true);
            hitman = null;
        }      
        try {
            getPlayer().changeMap(0);
        } catch (SQLException ex) {
            //
        }
        if (score > 2) {
            //getPlayer().gainMGPoints(score);
        }
        getPlayer().announce(MaplePacketCreator.sendHint("#e[HITMAN]#n\r\nYour final score was " + score + ".", 200, 5));
        if (score > 0) {
            saveScore();
        }
        getPlayer().dispelDebuffs(true);
        getPlayer().setGameManager(null);
    }
    
    private void gameFunctions() {
        int amount = 1 + (int) (Math.random() * 15);
        List<String> players = new ArrayList<>();
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT name FROM characters order by rand() limit " + amount);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                //if(StringUtil.isSpam(rs.getString("name"))) { // I don't like this but ok
                players.add(MapleCharacter.makeMapleReadable(rs.getString("name")));
                //}
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Collections.shuffle(players);
        StringBuilder pl = new StringBuilder("");
        StringBuilder plp = new StringBuilder();
        for (int p = 0; p < amount; p++) {
            pl.append(players.get(p));
            pl.append(" ");
            plp.append(players.get(p));
            plp.append(", ");
        }
        getPlayer().announce(MaplePacketCreator.sendYellowTip("[Hitman] Player(s) chosen are: "));
        getPlayer().announce(MaplePacketCreator.sendYellowTip("[Hitman] " + plp.toString().substring(0, (plp.toString().length() - 2)) + "."));
        answer = pl.toString().substring(0, pl.toString().length() - 1);
        getPlayer().getMap().setHitman(true);
    }

    private void increaseScore() {
        score++;
    }
    
    private void saveScore() {
        //
    }
    
}
