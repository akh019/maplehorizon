//package client.occupations.powers.OccupationPower;
//
//import client.MapleCharacter;
//import client.Timestamps;
//
//public class SuicideBombPower  extends OccupationPower {
//	
//	int price;
//	
//	public SuicideBombPower(int id, String name, int cooldown, int price) {
//		super(id, name, cooldown, Timestamps.player.SUICIDE_BOMB_POWER);
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
//		chr.getMap().spawnBomb(chr, 0);
//		return true;
//	}
//
//}
