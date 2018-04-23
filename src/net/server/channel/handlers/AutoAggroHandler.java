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
package net.server.channel.handlers;

import client.MapleCharacter;
import client.MapleClient;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.AbstractMaplePacketHandler;
import server.life.MapleMonster;
import tools.data.input.SeekableLittleEndianAccessor;

public final class AutoAggroHandler extends AbstractMaplePacketHandler {

    @Override
    public final void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        int oid = slea.readInt();
        MapleMonster monster = c.getPlayer().getMap().getMonsterByOid(oid);
        
        if(c.getPlayer().isHidden())
            return; // Don't auto aggro GM's in hide...
       
        if (monster != null && monster.getController() != null) {
            
            if (!monster.isControllerHasAggro()) {
                if (c.getPlayer().getMap().getCharacterById(monster.getController().getId()) == null) {
                    monster.switchController(c.getPlayer(), true);
                } else {
                    monster.switchController(monster.getController(), true);
                }
            } else if (c.getPlayer().getMap().getCharacterById(monster.getController().getId()) == null) {
                monster.switchController(c.getPlayer(), true);
            } 
            if(!monster.getController().isAlive()){
               /* int randnum=0;
                Random rand = new Random();
                List<MapleCharacter>players = new ArrayList<>();
                for(MapleCharacter a1 : monster.getController().getMap().getCharacters()){
                    if(a1.isAlive())
                      players.add(a1);
                }
                randnum = rand.nextInt(players.size()); 
                monster.switchController(players.get(randnum), true); // This should work */ // Undercoding
             }
        } else if (monster != null && monster.getController() == null) {
            monster.switchController(c.getPlayer(), true);
        }
    }
}
