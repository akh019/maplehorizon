//package client.occupations.OccupationFactory.Occupations;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Map.Entry;
//import java.util.Collection;
//import java.util.Collections;
//
//import tools.MaplePacketCreator;
//import tools.Pair;
//import client.MapleCharacter;
//import client.occupations.OccupationFactory.Occupations;
//import client.occupations.powers.OccupationPower;
//
//public class OccupationEntry {
//
//	private int level, exp;
//	private OccupationInfo occu;
//	private MapleCharacter chr;
//
//	public OccupationEntry(MapleCharacter owner, OccupationInfo occu, int level, int exp) {
//		this.chr = owner;
//		this.occu = occu;
//		this.level = level;
//		this.exp = exp;
//
//		validate();
//	}
//
//	public void validate() {
//		while (exp >= getInfo().getExpNeededForNextLevel(level)) {
//			levelup();
//		}
//	}
//
//	public void gainExp(int amount) {
//		exp += amount;
//		
//		if (getInfo().getId() != Occupations.Suckup.id)
//			chr.dropMessage(-1, "You have gained " + amount + " occupation exp (progress: " + (Math.round((float) exp / (float) getInfo().getExpNeededForNextLevel(level) * 100f * 100f) / 100f) + "%)");
//
//		validate();
//	}
//
//	public void levelup() {
//		exp -= getInfo().getExpNeededForNextLevel(level);
//		level++;
//		if (chr != null && chr.getClient() != null && chr.getClient().isLoggedIn()) {
//			if (chr.getMap() != null) {
//				chr.getMap().broadcastMessage(MaplePacketCreator.showForeignEffect(chr.getId(), 15));
//				chr.dropMessage(-1, "Your occupation is now level " + level + "!");
//				chr.dropMessage(5, "Your occupation is now level " + level + "!");
//			}
//		}
//	}
//
//	public OccupationInfo getInfo() {
//		return occu;
//	}
//
//	public int getLevel() {
//		return level;
//	}
//
//	public int getExp() {
//		return exp;
//	}
//
//	public String getPowersString() {
//		StringBuilder sb = new StringBuilder();
//
//		// <id, <level, power>>
//		Map<Integer, Pair<Integer, OccupationPower>> map = getActivePowers();
//
//		int clevel = -1;
//
//		for (Entry<Integer, Pair<Integer, OccupationPower>> entry : map.entrySet()) {
//			if (entry.getValue().left != clevel) {
//				clevel = entry.getValue().left;
//				sb.append("#b#eLevel " + clevel + "#n#k").append("\r\n\r\n");
//
//			}
//			sb.append("#e#r" + entry.getValue().right.getName() + "#k#n");
//
//			if (entry.getValue().right.getCooldown() > 0) {
//				sb.append("\r\n#dcooldown:#k " + (entry.getValue().right.getCooldown() / 1000) + " seconds");
//				sb.append("\r\n");
//			}
//
//			if (entry.getValue().right.getPrice() > 0) {
//				sb.append("#Price:#k " + entry.getValue().right.getPrice());
//				sb.append("\r\n");
//			}
//
//			sb.append("#dUsage:#k " + entry.getValue().right.getUsage());
//			sb.append("\r\n");
//
//			sb.append("\r\n");
//		}
//
//		return sb.toString();
//	}
//
//	/***
//	 * @return Returns the active powers in the form of Map<id, <level, power>>
//	 */
//	public Map<Integer, Pair<Integer, OccupationPower>> getActivePowers() {
//		List<Pair<Integer, OccupationPower>> activePowers = occu.powers.stream().filter(e -> e.left <= level).collect(Collectors.toList());
//		Map<Integer, Pair<Integer, OccupationPower>> map = new HashMap<>();
//		activePowers.forEach(e -> map.put(e.right.getId(), new Pair<>(e.left, e.right)));
//		return map;
//	}
//
//	public int getBonusLoots() {
//		Map<Integer, Pair<Integer, OccupationPower>> activePowers = getActivePowers();
//		int n = 0;
//
//		for (Pair<Integer, OccupationPower> pair : activePowers.values()) {
//			n += pair.right.getBonusLoots();
//		}
//
//		return n;
//	}
//
//	public static OccupationEntry generateEntry(MapleCharacter owner, int occuid, int level, int exp) {
//		return new OccupationEntry(owner, OccupationFactory.getInstance().getOccupation(occuid), level, exp);
//	}
//
//}
