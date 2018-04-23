//package client.occupations.powers.OccupationPower;
//
//import tools.Randomizer;
//import client.MapleCharacter;
//
//public class MoreLootsPower extends OccupationPower {
//
//	final int chance;
//	final int amountl, amounth;
//
//	/***
//	 * 
//	 * @param id
//	 * @param name
//	 * @param amount of extra loots (lo bound)
//	 * @param amount of extra loots (hi bound)
//	 * @param chance
//	 */
//	public MoreLootsPower(int id, String name, int amountl, int amounth, int chance) {
//		super(id, name, -1, null);
//
//		this.amountl = amountl;
//		this.amounth = amounth;
//		this.chance = chance;
//	}
//	
//	@Override
//	public int getBonusLoots() {
//		return Randomizer.next(100) < chance ? Randomizer.next(amountl, amounth) : 0;
//	}
//	
//	@Override
//	public boolean doPower(MapleCharacter chr, MapleCharacter victim, String[] args) {
//		return false;
//	}
//
//}
