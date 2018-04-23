package client;

import client.inventory.Item;
import client.inventory.MapleInventoryType;

public class MapleClone {
	
	private MapleCharacter clone;
	
	public MapleClone(MapleCharacter initiator, int id) {
		MapleCharacter clone = new MapleCharacter();
		clone.setHair(initiator.getHair());
		clone.setFace(initiator.getFace());
		clone.setSkinColor(initiator.getSkinColor());
		clone.setId(initiator.getId() + 100000 + id);
		clone.setLevel(initiator.getLevel());
		clone.setJob(initiator.getJob());
		clone.setMap(initiator.getMap());
		clone.setPosition(initiator.getPosition());
		clone.silentGiveBuffs(initiator.getAllBuffs());
		clone.setName(initiator.getName());
		
		clone.setClient(initiator.getClient());
		
		for(Item equip : initiator.getInventory(MapleInventoryType.EQUIPPED)){
			clone.getInventory(MapleInventoryType.EQUIPPED).addFromDB(equip);
        }
		
		clone.setClone(true);
		clone.getMap().addClone(clone);
		this.clone = clone;
	}
	
	public MapleCharacter getClone() {
		return clone;
	}
	
}
