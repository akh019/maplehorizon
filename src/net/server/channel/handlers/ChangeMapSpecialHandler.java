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

import client.MapleClient;
import client.inventory.MapleInventoryType;
import net.AbstractMaplePacketHandler;
import server.MapleInventoryManipulator;
import server.MaplePortal;
import server.MapleTrade;
import tools.MaplePacketCreator;
import tools.data.input.SeekableLittleEndianAccessor;

public final class ChangeMapSpecialHandler extends AbstractMaplePacketHandler {
    @Override
    public final void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        slea.readByte();
        String startwp = slea.readMapleAsciiString();
        slea.readShort();
        if(c.getPlayer().getMapId() == 130010100){
                    c.getPlayer().dropMessage(5,"You're not allowed to enter this map.");
                    return;
                }
        if(c.getPlayer().getMap().isFTJ()){
                    if(c.getPlayer().getInventory(MapleInventoryType.ETC).findById(4001024) != null){
                        short quantity = c.getPlayer().getInventory(MapleInventoryType.ETC).findById(4001024).getQuantity();
                        MapleInventoryManipulator.removeById(c.getPlayer().getClient(), MapleInventoryType.ETC, 4001024, quantity, false, false);
                                }
                }
         c.getPlayer().deleteOwner("SANDBOX", c.getPlayer());
        MaplePortal portal = c.getPlayer().getMap().getPortal(startwp);
        if (portal == null || c.getPlayer().portalDelay() > System.currentTimeMillis() || c.getPlayer().getBlockedPortals().contains(portal.getScriptName())) {
            c.announce(MaplePacketCreator.enableActions());
            return;
        }
		if (c.getPlayer().isBanned()) {
			return;
		}
		if (c.getPlayer().getTrade() != null) {
			MapleTrade.cancelTrade(c.getPlayer());
		}
        portal.enterPortal(c);   
    }
}
