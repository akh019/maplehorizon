package bots;

import java.util.ArrayList;
import java.util.List;

import server.maps.MapleMap;

public class BernardManager {

    public static final BernardManager instance = new BernardManager();
    public List<Bernard> bernards = new ArrayList<>();
    public int runningId = 0;
    public boolean bernardmove = false;
   

    public Bernard createBernard(int id, MapleMap map, int x, int y) {
        Bernard bernard = new Bernard(id, map, x, y);
        this.bernards.add(bernard);
        return bernard;
    }

    public Bernard getBernard(int id) {
        for(int i = 0; i < bernards.size() ; i++)
            if(bernards.get(i).getId() == id)
                return bernards.get(i);
        return null;
        
       
    }
    public void setBernardmove(boolean yes){
        bernardmove = yes;
    }
    public boolean isBernardmove(){
        return bernardmove;
    }
    public List<Bernard> getBernardList(){
       return this.bernards; 
    }    

    public Bernard getLastBernard() {
        if(bernards.isEmpty())
            return null;
        return bernards.get(bernards.size() - 1);
    }
}
