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
package server.life;

/**
 *
 * @author Matze
 */


public class MapleNPCStats {
    private String name, script;
    private boolean parcel, storage, storebank;

    public MapleNPCStats(String name) {
        this.name = name;
    }
    
    public String getScript() {
        return script;
    }
    
    public void setScript(String script) {
    	this.script = script;
    }
    
    public boolean isParcel() {
		return parcel;
	}

	public void setParcel(boolean parcel) {
		this.parcel = parcel;
	}

	public boolean isStorage() {
		return storage;
	}
        
        public boolean isStorebank() {
		return storebank;
	}

	public void setStorebank(boolean storebank) {
		this.storebank = storebank;
	}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
