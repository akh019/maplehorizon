/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.minigames;

import client.MapleCharacter;
import server.maps.MapleMap;

/**
 *
 * @author Danny
 */
public abstract class SoloGameInstance {
    
    /**
     *
     * Displays rules to the player(s), and continues to start the game.
     */    
    public abstract void displayRules();
    
    /**
     *
     * @return the MapleCharacter object.
     */  
    public abstract MapleCharacter getPlayer();
    
    /**
     * 
     * @return the MapleMap object
     */
    public abstract MapleMap getMap();
    
    /**
     *
     * Start the game.
     */  
    public abstract void startGame();
    
    
    /**
     * Used in any text based games.
     * @param text Text the player enters.
     */
    public abstract void compareTextToAnswer(String text);
    
    /**
     *
     * Ends the game.
     */  
    public abstract void endGame();
    
}
