/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scripting.event;

import client.MapleCharacter;

/**
 *
 * @author Administrator
 */
public class Fishing {   
    
    public int fishLevel(MapleCharacter player){
        if(player.getFishexp() < 10)
            return 0;
        else if(player.getFishexp() < 50)
            return 1;
        else if(player.getFishexp() < 100)
            return 2;
        else if(player.getFishexp() < 200)
            return 3;
        else if(player.getFishexp() < 500)
            return 4;
        else if(player.getFishexp() < 1000)
            return 5;
        else if(player.getFishexp() < 2000)
            return 6;
        else if(player.getFishexp() < 3000)
            return 7;
        else if(player.getFishexp() < 5000)
            return 8;
        else if(player.getFishexp() < 10000)
            return 9;
        else if(player.getFishexp() < 13337)
            return 10;
        else if(player.getFishexp() < 20000)
            return 11;
        else if(player.getFishexp() < 30000)
            return 12;
        else if(player.getFishexp() < 50000)
            return 13;
        else if(player.getFishexp() < 100000)
            return 14;
        else if(player.getFishexp() < 1000000)
            return 15;
        else 
            return 16;
        
    }
    public int fishLevelDB(int exp){
        if(exp < 10)
            return 0;
        else if(exp < 50)
            return 1;
        else if(exp < 100)
            return 2;
        else if(exp < 200)
            return 3;
        else if(exp < 500)
            return 4;
        else if(exp < 2000)
            return 6;
        else if(exp < 3000)
            return 7;
        else if(exp < 5000)
            return 8;
        else if(exp < 10000)
            return 9;
        else if(exp < 13337)
            return 10;
        else if(exp < 20000)
            return 11;
        else if(exp < 30000)
            return 12;
        else if(exp < 50000)
            return 13;
        else if(exp < 100000)
            return 14;
        else if(exp < 1000000)
            return 15;
        else 
            return 16;
        
    }
    public int fishLevelCap(MapleCharacter player){
        if(player.getFishexp() < 10)
            return 10;
        else if(player.getFishexp() < 50)
            return 50;
        else if(player.getFishexp() < 100)
            return 100;
        else if(player.getFishexp() < 200)
            return 200;
        else if(player.getFishexp() < 500)
            return 500;
        else if(player.getFishexp() < 1000)
            return 1000;
        else if(player.getFishexp() < 2000)
            return 2000;
        else if(player.getFishexp() < 3000)
            return 3000;
        else if(player.getFishexp() < 5000)
            return 5000;
        else if(player.getFishexp() < 10000)
            return 10000;
        else if(player.getFishexp() < 13337)
            return 13337;
        else if(player.getFishexp() < 20000)
            return 20000;
        else if(player.getFishexp() < 30000)
            return 30000;
        else if(player.getFishexp() < 50000)
            return 50000;
        else if(player.getFishexp() < 100000)
            return 100000;
        else if(player.getFishexp() < 1000000)
            return 1000000;
        else 
            return 1000000;
    }
    
    
}
