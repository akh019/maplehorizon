//package client.occupations.powers.OccupationPower;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//import server.maps.MapleMapObjectType;
//import client.MapleCharacter;
//import java.sql.Timestamp;
//
//public class ItemVacPower extends OccupationPower {
//
//	final List<Integer> BLOCKED_MAPS = Arrays.asList(100000000);
//	final int range;
//
//	/***
//	 * @param id
//	 * @param name
//	 * @param cooldown (in seconds)
//	 * @param range (non squared)
//	 */
//	public ItemVacPower(int id, String name, int cooldown, int range) {
//		super(id, name, cooldown, Timestamps.player.POWER_ITEMVAC);
//
//		this.range = range;
//		this.usage = "@itemvac";
//	}
//
//	@Override
//	public int execute(MapleCharacter chr) {
//		return super.execute(chr);
//	}
//
//	@Override
//	public boolean doPower(MapleCharacter chr, MapleCharacter victim, String[] args) {
//		if (BLOCKED_MAPS.contains(chr.getMapId())) {
//			chr.dropMessage(5, "You cannot use this in this map!");
//			return false;
//		}
//
//		if (chr.getMap().getMapObjectsInRange(chr.getPosition(), range, Arrays.asList(MapleMapObjectType.ITEM)).size() == 0) {
//			chr.dropMessage(5, "There aren't any items to loot in range!");
//			return false;
//		} else {
//			chr.vacItems(range);
//			return true;
//		}
//	}
//
//}
