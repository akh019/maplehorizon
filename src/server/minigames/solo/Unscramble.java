/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.minigames.solo;

import client.MapleCharacter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import server.TimerManager;
import server.maps.MapleMap;
import server.minigames.SoloGameInstance;
import tools.MaplePacketCreator;
import tools.Randomizer;

/**
 *
 * @author Danny
 * TODO: redo trivia methods, database, minigame points, return map
 */
public class Unscramble extends SoloGameInstance {
    
    private final MapleCharacter chr;
    private final MapleMap map;
    private int score;
    private String answer = null;
    private String scrambled;
    private ScheduledFuture<?> unscram = null;
    private final String[] words = {"expected", "rolling", "blanket", "pillows", "king", "failure", "obedience", "crash", "allusion", "illusion", "negativity", "cascade",
        "reflex", "cancer", "enter", "undying", "case", "basket", "timer", "romance", "revolution", "program", "computer", "neon", "engraving",
        "chivalry", "routine", "maggot", "lucid", "toxicity", "national", "astrology", "yabber", "pennies", "reaction", "toxicology", "justice", "criminal", "crime", "jurisdiction",
        "alleged", "allegory", "larceny", "theft", "robbery", "motor", "vehicle", "loosen", "description", "gruesome", "basket", "gothic", "elevation", "election", "knight", "victim", "building",
        "burn", "carbon", "dioxide", "jewel", "necklace", "choker", "husky", "name", "ecstatic", "stove", "burning", "temperature", "philosophy", "existence", "duckling", "imagine", "mega",
        "unreasonable", "forgiving", "space", "enjoy", "heroes", "levels", "sugar", "remorse", "empathy", "sympathy", "funk", "table", "medication", "water", "granite", "copper", "diamond", "abandonment",
        "adipocere", "admission", "confession", "identification", "imagery", "aggravated", "assault", "arch", "delta", "core", "felony", "bait", "battery", "blind", "reporting", "sexual", "caliber",
        "cadaveric", "characteristics", "decision", "child", "plotting", "technology", "loop", "intent", "investigation", "curtilage", "statute",
        "custodial", "arrest", "interrogation", "cyber", "direct", "question", "acid", "domestic", "elder", "elimination", "embezzlement", "emotional", "entrapment", "exceptional", "force", "exclusionary",
        "felony", "interview", "exploitation", "whorl", "anthropology", "entomology", "science", "biology", "chemistry", "flipping", "atom", "anion", "cation", "covalent", "irony", "bonds", "forgery",
        "property", "document", "fraudulent", "intent", "motive", "legal", "possession", "reasonable", "effort", "amendment", "fraud",
        "doctrine", "grand", "invasion", "homicide", "hostage", "situation", "food", "negotiable", "punishment", "death", "sentence", "preserve", "recovery", "item", "indirect", "inference", "contrast",
        "reluctant", "information", "suspected", "involvement", "unwilling", "manslaughter", "limited", "citizen", "felonious", "removal", "leading", "driving", "petty", "state", "deprive", "permanently",
        "accessories", "leading", "macrophotography", "maltreatment", "neglect", "physical", "involuntary", "voluntary", "material", "decay", "photograph", "specific", "subject", "error",
        "memory", "weakness", "incident", "retroactive", "rationalization", "warning", "misdemeanor", "surveillance", "murder", "warrant", "communication", "luster", "proceedings", "parallel", "passive",
        "majority", "opinion", "ordinance", "tail", "open", "pilferage", "bargaining", "baseline", "compass", "rectangular", "coordinate", "triangulation", "postmortem", "lividity", "presumptive", "probable", "raid",
        "nectarine", "pomegranate", "island", "composite", "wrongful", "residential", "commercial", "street", "driver", "routine", "safe", "shrinkage", "slamming", "grab",
        "acquaintance", "intimidate", "syndrome", "testimony", "poison", "toxicology", "voyeurism", "witness", "continue", "making", "multiple", "market", "grocery", "apple", "banana"};
    
    
    public Unscramble(MapleCharacter chr, MapleMap map) {
        this.chr = chr;
        this.map = map;
    }

    @Override
    public void displayRules() {
        getPlayer().dropMessage(6, "[Usncramble] Game starts in 5 seconds.");
        getPlayer().dropMessage(6, "[Unscramble] HOW TO PLAY: ");
        getPlayer().dropMessage(6, "[Unscramble] Your goal is to win as many unscramble rounds as possible in 1 minute.");
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
        if(unscram == null) {
            unscram = TimerManager.getInstance().schedule(new Runnable() {
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
                getPlayer().announce(MaplePacketCreator.sendHint("#e[UNSCRAMBLE]#n\r\nYou have entered the word correctly. \r\nYour score is now: #b" + score + ".", 200, 5));
            } else {
                getPlayer().announce(MaplePacketCreator.sendHint("#e[UNSCRAMBLE]#n\r\nYou have entered the word incorrectly.", 200, 5));
            }
        } 
    }

    @Override
    public void endGame() {
        if(unscram != null) {
            unscram.cancel(true);
            unscram = null;
        }      
        try {
            getPlayer().changeMap(0);
        } catch (SQLException ex) {
            //
        }
        if (score > 2) {
            //getPlayer().gainMGPoints(score);
        }
        getPlayer().announce(MaplePacketCreator.sendHint("#e[UNSCRAMBLE]#n\r\nYour final score was " + score + ".", 200, 5));
        if (score > 0) {
            saveScore();
        }
        getPlayer().dispelDebuffs(true);
        getPlayer().setGameManager(null);
    }
    
    private void gameFunctions() {
        int random = Randomizer.nextInt(words.length);
        answer = words[random];
        List<Character> scr = new ArrayList<Character>();
        for(int i = 0; i < answer.length(); i++) {
            scr.add(answer.charAt(i));
        }
        Collections.shuffle(scr);
        StringBuilder s = new StringBuilder("");
        for (int i = 0; i < scr.size(); i++) {
            s.append(scr.get(i));
        }
        scrambled = s.toString();
        getPlayer().announce(MaplePacketCreator.sendYellowTip("[Unscramble] Unscramble: " + scrambled));
    }
    
    private void increaseScore() {
        score++;
    }
    
    private void saveScore() {
        //
    }
    
}
