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

import net.AbstractMaplePacketHandler;
import scripting.npc.NPCScriptManager;
import server.life.MapleNPC;
import server.maps.MapleMapObject;
import server.maps.PlayerNPCs;
import tools.MaplePacketCreator;
import tools.data.input.SeekableLittleEndianAccessor;
import client.MapleClient;
import client.autoban.AutobanFactory;

public final class NPCTalkHandler extends AbstractMaplePacketHandler {
    public final void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        if (!c.getPlayer().isAlive()) {
            c.announce(MaplePacketCreator.enableActions());
            return;
        }
        int oid = slea.readInt();
        if (NPCScriptManager.getInstance().getCM(c) != null) {
        	c.announce(MaplePacketCreator.enableActions());
        	NPCScriptManager.getInstance().getCM(c).dispose();
        }
        MapleMapObject obj = c.getPlayer().getMap().getMapObject(oid);
        double distance = obj.getPosition().distance(c.getPlayer().getPosition());
        if (distance < 600) {
	        if (obj instanceof MapleNPC) {
	            MapleNPC npc = (MapleNPC) obj;
	            if (npc.getStats().isParcel()) {
	                c.announce(MaplePacketCreator.sendDuey((byte) 8, DueyHandler.loadItems(c.getPlayer())));
	            } else if (npc.getStats().isStorage()) {
	            	c.getPlayer().getStorage().sendStorage(c, npc.getId());
	            } else if (npc.getStats().isStorebank()) {
	            	NPCScriptManager.getInstance().start(c, 9030000, null, null);
	            } else if (npc.hasShop()) {
	                if (c.getPlayer().getShop() != null) {
	                    return;
	                }
	                npc.sendShop(c);
	            } else {
	                if (c.getCM() != null || c.getQM() != null) {
	                    c.announce(MaplePacketCreator.enableActions());
	                    return;
	                }
	                //NPCScriptManager.getInstance().start(c, npc.getId(), null, null);
	                if (npc.getScript() == "c_JQReward") {
	                	if (distance < 200)
	                		NPCScriptManager.getInstance().start(c, npc.getId(), npc.getScript(), null);
	                	else
	                		c.getPlayer().message("You are too far away.");
	                } else
	                	NPCScriptManager.getInstance().start(c, npc.getId(), npc.getScript(), null);
	            }
	        } else if (obj instanceof PlayerNPCs) {
	            NPCScriptManager.getInstance().start(c, ((PlayerNPCs) obj).getId(), ((PlayerNPCs) obj).getName(), null);
	        }
        } else
        	c.getPlayer().getAutobanManager().addPoint(AutobanFactory.TALK_FARAWAY_NPC, "id: " + obj.getObjectId() + ", distance: " + distance + ", expected: <600");
    }
}