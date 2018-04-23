//package client.occupations.OccupationFactory.Occupations;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import tools.Pair;
//import client.occupations.OccupationFactory.Occupations;
//import client.occupations.powers.OccupationPower;
//
//public class OccupationInfo {
//
//	private int id;
//	private String name, description, expgainingmethod;
//	public List<Pair<Integer, OccupationPower>> powers = new ArrayList<>();
//
//	public OccupationInfo(Occupations occupation, String name) {
//		this.id = occupation.id;
//		this.name = name;
//	}
//
//	public int getId() {
//		return id;
//	}
//
//	public String getName() {
//		return name;
//	}
//
//	public String getDescription() {
//		return description;
//	}
//
//	public String getExpGainingMethod() {
//		return expgainingmethod;
//	}
//	
//	public OccupationInfo addPower(int level, OccupationPower power) {
//		powers.add(new Pair<>(level, power));
//		return this;
//	}
//
//	public int getExpNeededForNextLevel(int x) {
//		x += 1;
//
//		if (id == Occupations.Suckup.id)
//			return (int) Math.floor(x * x * x * x * 0.7f * 6);
//		else if (id == Occupations.Huntsman.id)
//			return (int) Math.floor((6 * (Math.sqrt((Math.pow(x, 5)))) / 1.5) + x * 26);
//		else if (id == Occupations.Craftsman.id)
//			return (int) Math.floor((6 * (Math.sqrt((Math.pow(x, 5)))) / 1.5) + x * 26);
//		else if (id == Occupations.Slacker.id)
//			return (int) Math.floor((x * 2) * x + x + 5);
//		else
//			return Integer.MAX_VALUE;
//	}
//
//	public OccupationInfo setDescription(String desc) {
//		this.description = desc;
//		return this;
//	}
//	
//	public OccupationInfo setExpGainingMethod(String method) {
//		this.expgainingmethod = method;
//		return this;
//	}
//
//	public int getExp() {
//		return 0;
//	}
//
//	public int getMesos() {
//		return 0;
//	}
//
//	public int getDrops() {
//		return 0;
//	}
//}
