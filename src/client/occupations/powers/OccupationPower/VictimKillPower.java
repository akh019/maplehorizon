//package client.occupations.powers.OccupationPower;
//
//import client.MapleCharacter;
//import client.Timestamps;
//
//public class VictimKillPower extends OccupationPower {
//	
//	int price;
//	
//	public VictimKillPower(int id, String name, int cooldown, int price) {
//		super(id, name, cooldown, Timestamps.player.SPAWN_BOMB_POWER);
//		
//		this.price = price;
//	}
//	
//	@Override
//	public int getPrice() {
//		return price;
//	}
//
//	@Override
//	public boolean doPower(MapleCharacter chr, MapleCharacter victim, String[] args) {
//		if (victim.isGM()) {
//			chr.talk("DISREGARD THAT I SUCK COCKS");
//			chr.dropMessage(5, "This power cannot be used on a GM!");
//			return false;
//		}
//		
//		if (!victim.isAlive()) {
//			chr.dropMessage("The victim is already dead!");
//			return false;
//		}
//		victim.dropMessage(5, chr.getName() + " has killed you!");
//		victim.die();
//		return true;
//	}
//
//}
