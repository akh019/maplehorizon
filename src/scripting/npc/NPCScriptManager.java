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
package scripting.npc;

import client.MapleCharacter;
import client.MapleClient;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.HashMap;
import java.util.Map;

import javax.script.Invocable;
import javax.script.ScriptException;

import scripting.AbstractScriptManager;
import server.life.MapleLifeFactory;
import tools.FilePrinter;
import tools.MaplePacketCreator;

/**
 *
 * @author Matze
 */
public class NPCScriptManager extends AbstractScriptManager {

    private Map<MapleClient, NPCConversationManager> cms = new HashMap<>();
    private Map<MapleClient, Invocable> scripts = new HashMap<>();
    private static NPCScriptManager instance = new NPCScriptManager();

    public synchronized static NPCScriptManager getInstance() {
        return instance;
    }

     public void start(MapleClient c, int npc, String filename, MapleCharacter chr) {
    	start(c, npc, filename, chr, null);
    }

    public void start(MapleClient c, int npc, String filename, MapleCharacter chr, String args) {
        try {
        	NPCConversationManager cm = new NPCConversationManager(c, npc, filename);
            if (cms.containsKey(c)) {
                dispose(c);
                return;
            }
            cms.put(c, cm);
            Invocable iv = null;
            if (filename != null) { 
                iv = getInvocable("npc/world" + c.getWorld() + "/" + filename + ".js", c);
            } else {
            	filename = null;
            	cm.setScript(null);
                iv = getInvocable("npc/world" + c.getWorld() + "/" + npc + ".js", c);
            }
            
            if (iv == null || NPCScriptManager.getInstance() == null) {
            	FilePrinter.printError(FilePrinter.NPC + (filename == null ? npc : filename) + ".txt", "Script not found.");
            	notice(c, getCM(c).getNpc(), getCM(c).getScript());
                dispose(c);
                return;
            }
            engine.put("cm", cm);
            scripts.put(c, iv);
            try {
            	if (args != null)
            		iv.invokeFunction("start", args);
            	else
            		iv.invokeFunction("start");
            } catch (final NoSuchMethodException nsme2) {
                try {
                    iv.invokeFunction("start", chr);
                } catch (final NoSuchMethodException nsma) {
                    iv.invokeFunction("action", (byte) 1, (byte) 0, 0);
                }
            }
        } catch (final Exception e) {
            FilePrinter.printError(FilePrinter.NPC + (filename == null ? npc : filename) + ".txt", e);
            notice(c, npc, filename);
            dispose(c);
        }
    }

    public void action(MapleClient c, byte mode, byte type, int selection) {
        Invocable iv = scripts.get(c);
        if (iv != null) {
            try {
                iv.invokeFunction("action", mode, type, selection);
            } catch (Exception e) {
                if (getCM(c) != null) {
                    FilePrinter.printError(FilePrinter.NPC + (getCM(c).getScript() == null ? getCM(c).getNpc() : getCM(c).getScript()) + ".txt", e);
                    notice(c, getCM(c).getNpc(), getCM(c).getScript());
                }
                dispose(c);//lol this should be last, not notice fags
            }
        }
    }

    public void dispose(NPCConversationManager cm) {
        MapleClient c = cm.getClient();
        cms.remove(c);
        scripts.remove(c);
        resetContext("npc/world" + c.getWorld() + "/" + (cm.getScript() == null ? cm.getNpc() : cm.getScript()) + ".js", c);
    }

    public void dispose(MapleClient c) {
        if (cms.get(c) != null) {
            dispose(cms.get(c));
        }
    }

    public NPCConversationManager getCM(MapleClient c) {
        return cms.get(c);
    }
    
    private void notice(MapleClient c, int id, String filename) {
        if (c != null) {
            c.getPlayer().dropMessage(1, "An unknown error has occurred executing this script.\r\n" + (filename == null ? id : filename) + ", " + id);
        }
    }
}
