/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.minigames;

import client.MapleCharacter;
import java.lang.reflect.InvocationTargetException;
import server.maps.MapleMap;

/**
 *
 * @author Danny
 * Structured like AE's
 */
public class GameManager {
    
    private final MapleCharacter chr;
    private final MapleCharacter opponent;
    private final MapleMap map;
    private SoloGameInstance gInstance = null;
    private PVPGameInstance pInstance = null;
    
    public GameManager(MapleCharacter chr, MapleMap map) {
        this.chr = chr;
        this.opponent = null;
        this.map = map;
    }
    
    public GameManager(MapleCharacter chr, MapleCharacter opponent, MapleMap map) {
        this.chr = chr;
        this.opponent = opponent;
        this.map = map;
    }
    
    public SoloGameInstance getCurrentGame() {
        return gInstance;
    }
    
    public PVPGameInstance getCurrentPvpGame() {
        return pInstance;
    }
    
    public void startGame(Class c, boolean pvp) {
        if(c != null) {
            try {
                if(pvp) {
                    pInstance = (PVPGameInstance) c.getDeclaredConstructor(MapleCharacter.class, MapleCharacter.class, MapleMap.class).newInstance(chr, opponent, map);
                } else {
                    gInstance = (SoloGameInstance) c.getDeclaredConstructor(MapleCharacter.class, MapleMap.class).newInstance(chr, map);
                }
            } catch (IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | SecurityException | InvocationTargetException ex) {
                ex.printStackTrace();
                return;
            }
            if(pvp) {
                pInstance.displayRules(); // Do I have to cast this? Hmmm...
            } else {
                gInstance.displayRules(); // Do I have to cast this? Hmmm...
            }
        } else {
            System.out.println("Class is null");
        }
    }
    
}
