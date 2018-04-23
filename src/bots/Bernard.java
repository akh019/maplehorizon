package bots;

import java.awt.Point;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import server.maps.AbstractAnimatedMapleMapObject;
import server.maps.MapleMap;
import server.maps.MapleMapObjectType;
import server.movement.AbsoluteLifeMovement;
import server.movement.LifeMovementFragment;
import tools.MaplePacketCreator;
import tools.Randomizer;
import client.MapleCharacter;
import client.MapleClient;
import net.server.channel.handlers.MovePlayerHandler;
import org.apache.mina.core.session.IoSession;
import server.TimerManager;
import tools.MockIOSession;

/*
 * Author: Troxied
 * */
public class Bernard extends AbstractAnimatedMapleMapObject {

    public static int runningId = 0;
    public MapleClient client;
    public MapleCharacter avatar;
    public ScheduledFuture<?> future;
    public int id;
    public MapleMap map;
    public boolean following = false;

    public Bernard(int id, MapleMap map, int x, int y) {
        client = new MapleClient(null, null, new MockIOSession());
     //  client = new MapleClient(null,null, new Session());
   

        try {
            avatar = MapleCharacter.loadCharFromDB(id, client, true);
            
        } catch (SQLException e) {
            System.out.println("Error loading bernard avatar: " + e);
        }
        this.id = id;
        this.map = map;
        avatar.setName("Android");
        avatar.setMap(map);
        avatar.setId(id);
        avatar.setPosition(new Point(x, y));
    }

    public Bernard(MapleCharacter source) {
        this.avatar = source;
    }

    @Override
    public void sendSpawnData(MapleClient client) {
        client.announce(MaplePacketCreator.spawnPlayerMapobject(avatar));
    }

    @Override
    public void sendDestroyData(MapleClient client) {
    }

    @Override
    public MapleMapObjectType getType() {
        return MapleMapObjectType.PLAYER;
    }
    public int getId(){
        return id;
    }
    public boolean isFollowing(){
       return following; 
    }
    public void setFollowing(boolean yes){
        following = yes;
    }
    public MapleCharacter getAvatar(){
        return avatar;
    }

    public void interpolateTo(Point pos) {
        
        List<LifeMovementFragment> res = createInterpolateTo(pos);
        // List<LifeMovementFragment> res = createFall();
      //  avatar.getMap().broadcastMessage(MaplePacketCreator.serverNotice(6,"Bernards Map ID: " + avatar.getMap().getId()));
        avatar.getMap().broadcastMessage(avatar, MaplePacketCreator.movePlayer(id, res), false);
      //  avatar.getMap().broadcastMesasge(avatar,MaplePacketCreator.)
    }
    public void interpolateCont(final MapleCharacter player){
        TimerManager.getInstance().register(new Runnable() {
            @Override
            public void run() {
                
                avatar.getMap().broadcastMessage(avatar, MaplePacketCreator.movePlayer(id, createInterpolateTo(player.getPosition())), false);
            
            }
        }, 1000);
    }

    public List<LifeMovementFragment> createInterpolateTo(Point pos) {
        List<LifeMovementFragment> res = new ArrayList<>();
        short xpos = (short) (pos.x - 30);
        short ypos = (short) (pos.y);
       // short xwobble = 2000;
      //  short ywobble = 200;
        short xwobble = 0;
     short ywobble = 0;
        short unk = 0;
        byte newstate = 0;
        short duration = 2000;
        AbsoluteLifeMovement alm = new AbsoluteLifeMovement((byte) 0, new Point(xpos, ypos), duration, newstate);
        alm.setUnk(unk);
        alm.setPixelsPerSecond(new Point(xwobble, ywobble));
        res.add(alm);
        return res;
    }

    private List<LifeMovementFragment> createFall() {
        List<LifeMovementFragment> res = new ArrayList<LifeMovementFragment>();
        res.add(new AbsoluteLifeMovement((byte) 0, new Point(-193, -56), 120, (byte) 3));
        ((AbsoluteLifeMovement) res.get(res.size() - 1)).setPixelsPerSecond(new Point(-125, 0));
        return res;
    }
}
