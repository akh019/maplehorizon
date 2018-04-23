//package client.occupations.OccupationFactory.Occupations;
//
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.HashMap;
//import java.util.Map;
//
//import client.occupations.powers.OccupationPower.OccupationPowerFactory;
//
//public class OccupationFactory {
//
//	private static final OccupationFactory instance = new OccupationFactory();
//
//	private Map<Integer, OccupationInfo> occupations = new HashMap<>();
//	private OccupationPowerFactory powerFactory;
//
//	public OccupationFactory() {
//		this.powerFactory = new OccupationPowerFactory(this);
//
//		initialize();
//	}
//
//	public void initialize() {
//		occupations.clear();
//
//		occupations.put(Occupations.Suckup.id,
//				new OccupationInfo(Occupations.Suckup, Occupations.Suckup.name)
//						.addPower(1, powerFactory.VAC1)
//						.addPower(2, powerFactory.VAC2)
//						.addPower(3, powerFactory.VAC3)
//						.addPower(4, powerFactory.VAC4)
//						.addPower(5, powerFactory.VAC5)
//						.addPower(6, powerFactory.VAC6)
//						.addPower(7, powerFactory.VAC7)
//						.addPower(8, powerFactory.VAC8)
//						.addPower(9, powerFactory.VAC9)
//						.addPower(10, powerFactory.VAC10)
//						.setDescription("Gets powerful loot VACing capabilities as well as increased meso and drop rates.")
//						.setExpGainingMethod("Gain exp by VACing items"));
//
//		occupations.put(Occupations.Huntsman.id,
//				new OccupationInfo(Occupations.Huntsman, Occupations.Huntsman.name)
//						.addPower(1, powerFactory.HUNTSMAN_ILI1)
//						.addPower(2, powerFactory.HUNTSMAN_ILI2)
//						.addPower(3, powerFactory.HUNTSMAN_ILI3)
//						.addPower(4, powerFactory.HUNTSMAN_ILI4)
//						.addPower(5, powerFactory.HUNTSMAN_ILI5)
//						.addPower(6, powerFactory.HUNTSMAN_ILI6)
//						.addPower(7, powerFactory.HUNTSMAN_ILI7)
//						.addPower(8, powerFactory.HUNTSMAN_ILI8)
//						.addPower(9, powerFactory.HUNTSMAN_ILI9)
//						.addPower(10, powerFactory.HUNTSMAN_ILI1)
//						.setDescription("Gets more drops, helps chefs hunt items for their recipes")
//						.setExpGainingMethod("Gain exp for each extra loots per monster item drop"));
//
//		occupations.put(Occupations.Craftsman.id,
//				new OccupationInfo(Occupations.Craftsman, Occupations.Craftsman.name)
//						.addPower(1, powerFactory.OPEN_BAKE_NPC)
//						.setDescription("Can bake items of all kinds (Naricain elixirs, onyx apples, rare mounts and chairs, and even powerful RSIs)")
//						.setExpGainingMethod("Gain exp by crafting items"));
//
//		occupations.put(Occupations.Slacker.id,
//				new OccupationInfo(Occupations.Slacker, Occupations.Slacker.name)
//						.addPower(1, powerFactory.SPAWN_BOMB_POWER1)
//						.addPower(2, powerFactory.SUICIDE_BOMB_POWER)
//						.addPower(3, powerFactory.SPAWN_BOMB_POWER2)
//						.addPower(4, powerFactory.VICTIM_TALK_POWER)
//						.addPower(5, powerFactory.KILL_POWER)
//						.addPower(6, powerFactory.SPAWN_BOMB_POWER3)
//						.addPower(7, powerFactory.MOLEST_POWER)
//						.addPower(8, powerFactory.STUN_POWER)
//						.addPower(9, powerFactory.SEDUCE_POWER)
//						.addPower(10, powerFactory.AIRSTRIKE_POWER)
//						.setDescription("Gets various powers to be used for socializing")
//						.setExpGainingMethod("Gain exp by using your powers and also staying in henesys."));
//	}
//
//	public OccupationInfo getOccupation(int id) {
//		return occupations.get(id);
//	}
//
//	public Collection<OccupationInfo> getAllOccupations() {
//		return new ArrayList<OccupationInfo>(occupations.values());
//	}
//
//	public static OccupationFactory getInstance() {
//		return instance;
//	}
//
//	public enum Occupations {
//		Suckup(0, "Suckup"),
//		Huntsman(1, "Huntsman"),
//		Craftsman(2, "Craftsman"),
//		Slacker(3, "Slacker"),
//		Lumberman(4, "Lumberjack"),
//		Mineworker(5, "Mineworker");
//
//		Occupations(int id, String name) {
//			this.id = id;
//			this.name = name;
//		}
//
//		public final int id;
//		public final String name;
//
//		public Occupations getById(int id) {
//			for (Occupations occ : Occupations.values()) {
//				if (occ.id == id)
//					return occ;
//			}
//
//			return null;
//		}
//
//	}
//
//}
