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

import bots.BernardManager;
import client.MapleCharacter;
import client.MapleClient;
import client.MapleStat;
import java.awt.Point;
import java.util.Calendar;
import java.util.List;
import net.server.channel.handlers.AbstractMovementPacketHandler;
import server.movement.LifeMovementFragment;
import tools.MaplePacketCreator;
import tools.data.input.SeekableLittleEndianAccessor;

public final class MovePlayerHandler extends AbstractMovementPacketHandler {

    public final void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        slea.skip(9);

        final List<LifeMovementFragment> res = parseMovement(slea);
        final List<LifeMovementFragment> bres = res;
        Calendar current = Calendar.getInstance();
        c.getPlayer().updateAfk(current);
        if (c.getPlayer().getMap().getAutoKill()) {
            boolean kill = false;
            if(c.getPlayer().getMap().getAKPositionB() != -1 && c.getPlayer().getMap().getAKPositionA() != -1) {
                if(c.getPlayer().getPosition().y >= c.getPlayer().getMap().getAKPositionB()) {
                    kill = true;
                } else if (c.getPlayer().getPosition().y <= c.getPlayer().getMap().getAKPositionA()) {
                    kill = true;
                }
            } else if (c.getPlayer().getMap().getAKPositionB() != -1) {
                if(c.getPlayer().getPosition().y >= c.getPlayer().getMap().getAKPositionB()) {
                    kill = true;
                }
            } else if (c.getPlayer().getMap().getAKPositionA() != -1) {
                if (c.getPlayer().getPosition().y <= c.getPlayer().getMap().getAKPositionA()) {
                    kill = true;
                }
            }
            if(kill) {
                if ((!c.getPlayer().isGM() || c.getPlayer().isTemp()) && c.getPlayer().isAlive()) {
                    c.getPlayer().setHp(0);
                    c.getPlayer().updateSingleStat(MapleStat.HP, 0);
                }
            }
        }
        if (res != null) {
            updatePosition(res, c.getPlayer(), 0);
            if (c.getPlayer().getBernardid() != 0) { // Bernard Movement -Start

                if (BernardManager.instance.getBernard(c.getPlayer().getBernardid()).isFollowing()) {

                    int thisbern = c.getPlayer().getBernardid();
                    updatePosition(bres, BernardManager.instance.getBernard(thisbern), 0);
                    // Point pos = new Point(c.getPlayer().getPosition().x - 50, c.getPlayer().getPosition().y);
                    c.getPlayer().getMap().movePlayer(BernardManager.instance.getBernard(thisbern).getAvatar(), c.getPlayer().getPosition());

                    c.getPlayer().getMap().broadcastMessage(BernardManager.instance.getBernard(thisbern).getAvatar(), MaplePacketCreator.movePlayer(BernardManager.instance.getBernard(thisbern).getId(), bres));

                }
            } // Bernard Movement - End
            c.getPlayer().getMap().movePlayer(c.getPlayer(), c.getPlayer().getPosition());
            if (c.getPlayer().isHidden()) {
                c.getPlayer().getMap().broadcastGMMessage(c.getPlayer(), MaplePacketCreator.movePlayer(c.getPlayer().getId(), res), false);
            } else {
                c.getPlayer().getMap().broadcastMessage(c.getPlayer(), MaplePacketCreator.movePlayer(c.getPlayer().getId(), res), false);
            }
           if(c.getPlayer().getHp() < 1 && System.currentTimeMillis() - c.getPlayer().getLastDeath() < 500) {
                c.getPlayer().setHpMp(0);
                c.getChannelServer().broadcastGMPacket(MaplePacketCreator.serverNotice(6, "[ALERT] " + c.getPlayer().getName() + " was attempting to move while dead."));
            }   
            /*   if (c.getPlayer().getPosition().y == AutoKill.PositionY && AutoKill.isOn && c.getPlayer().getMapId() == AutoKill.AutoKillMap && c.getPlayer().getPosition().y <= (AutoKill.PositionY + 20) && c.getPlayer().getPosition().y >= (AutoKill.PositionY - 20))
            {
                c.getPlayer().setHpMp(0);
                c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.earnTitleMessage(c.getPlayer().getName() + " has died to autokill."));
            } I moved it lower*/

        }
    }
}
