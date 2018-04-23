//package client.occupations.powers.OccupationPower;
//
//import client.MapleCharacter;
//import client.Timestamps;
//
//public class SpawnBombPower extends OccupationPower {
//	
//	int price;
//	
//	public SpawnBombPower(int id, String name, int cooldown, int price) {
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
//		chr.getMap().spawnBomb(chr, 3);
//		return true;
//	}
//
//}
