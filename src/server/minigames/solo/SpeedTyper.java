/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.minigames.solo;

import client.MapleCharacter;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ScheduledFuture;
import scripting.npc.NPCScriptManager;
import server.TimerManager;
import server.maps.MapleMap;
import server.minigames.SoloGameInstance;
import tools.MaplePacketCreator;
import tools.Randomizer;

/**
 *
 * @author Danny
 * FML
 * TODO: database, minigame points, return map, word display tech
 */
public class SpeedTyper extends SoloGameInstance {
    
    private final MapleCharacter chr;
    private final MapleMap map;
    private List<String> corrwords = new ArrayList<>(); // List of correct words.
    private List<String> wrongwords = new ArrayList<>(); // List of wrong words.
    private List<String> dictionary = new ArrayList<>(); // Dictionary txt file.
    private String word = null;
    private ScheduledFuture<?> speed = null;
    private int errors;
    private int characters;
    private int wpm = 0;
    private double accuracy = 0;
    
    public SpeedTyper(MapleCharacter chr, MapleMap map) {
        this.chr = chr;
        this.map = map;
    }   

    @Override
    public void displayRules() {
        populateDictionary();
        getPlayer().dropMessage(6, "[SpeedTyper] Game starts in 5 seconds.");
        getPlayer().dropMessage(6, "[SpeedTyper] HOW TO PLAY: ");
        getPlayer().dropMessage(6, "[SpeedTyper] Your objective is to type the words that appear on the screen.");
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
        if(speed == null) {
            speed = TimerManager.getInstance().schedule(new Runnable() {
                public void run() {
                    endGame();
                }
            }, 60000);
        }
    }

    @Override
    public void compareTextToAnswer(String text) {
        if(word != null) {
            if(errors < 4) {
                if(text.equals(word)) {
                    corrwords.add(word);
                } else {
                    wrongwords.add(word);
                    errors++;
                }
                characters += text.length();
                gameFunctions();
            } else {
                if(speed != null) {
                    speed.cancel(true);
                    speed = null;
                }
                try {
                    getPlayer().changeMap(0);
                } catch (SQLException ex) {
                    //
                }
                getPlayer().dropMessage(5, "[SpeedTyper] You entered too many incorrect words.");
                corrwords.clear();
                corrwords = null;
                wrongwords.clear();
                wrongwords = null;
                dictionary.clear();
                dictionary = null;
                getPlayer().setGameManager(null);
                getPlayer().dispelDebuffs(true);
            }
        }
    }

    @Override
    public void endGame() {
        if(speed != null) {
            speed.cancel(true);
            speed = null;
        }
        try {
            getPlayer().changeMap(0);
        } catch (SQLException ex) {
            //
        }
        String output = "";
        doCalculations();
        output += "\t\t\t\t\t#e#b[SpeedTyper]#n";
        output += "\r\n\r\nCorrect words (" + corrwords.size() + "): #k" + (corrwords.size() > 0 ? getCorrectWords() : "None") + ".";
        output += "\r\n\r\n#bIncorrect words(or out of sequence) (" + wrongwords.size() + "): #k" + (wrongwords.size() > 0 ? getWrongWords() : "None") + ".";
        output += "\r\n\r\n#bWords per minute: #k" + wpm + ".";
        output += "\r\n\r\n#bAccuracy: #k" + accuracy + "%.";
        getPlayer().announce(MaplePacketCreator.getNPCTalk(1012008, (byte) 0, output, "00 00", (byte) 0));
        getPlayer().getClient().announce(MaplePacketCreator.enableActions());
        NPCScriptManager.getInstance().dispose(getPlayer().getClient());
        if (wpm > 0) {
            saveScore();
        }
        corrwords.clear();
        corrwords = null;
        wrongwords.clear();
        wrongwords = null;
        dictionary.clear();
        dictionary = null;
        getPlayer().setGameManager(null);
        getPlayer().dispelDebuffs(true);
    }
    
    private void gameFunctions() {
        int random = Randomizer.nextInt(dictionary.size());
        word = dictionary.get(random);
        getPlayer().announce(MaplePacketCreator.earnTitleMessage(word)); // temp?
    }
    
    private void populateDictionary() {
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File("dictionary.txt"));
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        if(scanner != null) {
            while(scanner.hasNextLine()) {
                dictionary.add(scanner.nextLine());
            }
        }
    }
    
    private void doCalculations() {
        int minutes = (60000 / 1000) / 60; // Time in minutes.
        int incorrectwords = wrongwords.size(); // Wrong words.
        double entries = characters / 5; // Total words divided by 5(5characters = 1 word for wpm).
        double grosswpm = entries / minutes; // Gross WPM with no errors.
        double netwpm = (grosswpm - incorrectwords) / minutes; // Net WPM with errors.
        wpm = (int) netwpm;
        accuracy = (int) (netwpm / grosswpm * 100); // Accuracy calculation.
    }
    
    private String getCorrectWords() {
        StringBuilder r = new StringBuilder();
        for (int gr = 0; gr < corrwords.size(); gr++) {
            r.append(corrwords.get(gr));
            r.append(", ");
        }
        return corrwords.toString().substring(1, r.toString().length() - 1);
    }
    
    private String getWrongWords() {
        StringBuilder w = new StringBuilder();
        for (int gw = 0; gw < wrongwords.size(); gw++) {
            w.append(wrongwords.get(gw));
            w.append(", ");
        }
        return wrongwords.toString().substring(1, w.toString().length() - 1);
    }

    private void saveScore() {
        //
    }
    
}
