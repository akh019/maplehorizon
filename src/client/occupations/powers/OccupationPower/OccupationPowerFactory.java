//package client.occupations.powers.OccupationPower;
//
//import client.occupations.OccupationFactory;
//
//public class OccupationPowerFactory {
//
//	OccupationFactory occufactory;
//
//	// Using same IDs is for overriding (Occupation leveling up grants access to a better version of the power for example)
//
//	// Suckup
//	public final ItemVacPower VAC1 = new ItemVacPower(0, "Item Vac Level I", 60 * 2, (int) Math.pow(50, 2));
//	public final ItemVacPower VAC2 = new ItemVacPower(0, "Item Vac Level II", 60 * 2, (int) Math.pow(150, 2));
//	public final ItemVacPower VAC3 = new ItemVacPower(0, "Item Vac Level III", 60 + 30, (int) Math.pow(200, 2));
//	public final ItemVacPower VAC4 = new ItemVacPower(0, "Item Vac Level IV", 60 + 30, (int) Math.pow(300, 2));
//	public final ItemVacPower VAC5 = new ItemVacPower(0, "Item Vac Level V", 60, (int) Math.pow(420, 2));
//	public final ItemVacPower VAC6 = new ItemVacPower(0, "Item Vac Level VI", 60, (int) Math.pow(560, 2));
//	public final ItemVacPower VAC7 = new ItemVacPower(0, "Item Vac Level VII", 30, (int) Math.pow(740, 2));
//	public final ItemVacPower VAC8 = new ItemVacPower(0, "Item Vac Level VIII", 30, (int) Math.pow(900, 2));
//	public final ItemVacPower VAC9 = new ItemVacPower(0, "Item Vac Level IX", 20, (int) Math.pow(1500, 2));
//	public final ItemVacPower VAC10 = new ItemVacPower(0, "Item Vac Level X", 15, (int) Math.pow(4000, 2));
//
//	// Huntsman
//	public final MoreLootsPower HUNTSMAN_ILI1 = new MoreLootsPower(0, "More loots Level I", 1, 1, 50);
//	public final MoreLootsPower HUNTSMAN_ILI2 = new MoreLootsPower(0, "More loots Level II", 1, 2, 25);
//	public final MoreLootsPower HUNTSMAN_ILI3 = new MoreLootsPower(0, "More loots Level III", 1, 3, 30);
//	public final MoreLootsPower HUNTSMAN_ILI4 = new MoreLootsPower(0, "More loots Level IV", 2, 2, 35);
//	public final MoreLootsPower HUNTSMAN_ILI5 = new MoreLootsPower(0, "More loots Level V", 2, 3, 35);
//	public final MoreLootsPower HUNTSMAN_ILI6 = new MoreLootsPower(0, "More loots Level VI", 3, 3, 35);
//	public final MoreLootsPower HUNTSMAN_ILI7 = new MoreLootsPower(0, "More loots Level VII", 3, 4, 35);
//	public final MoreLootsPower HUNTSMAN_ILI8 = new MoreLootsPower(0, "More loots Level VIII", 3, 5, 35);
//	public final MoreLootsPower HUNTSMAN_ILI9 = new MoreLootsPower(0, "More loots Level IX", 4, 5, 40);
//	public final MoreLootsPower HUNTSMAN_ILI10 = new MoreLootsPower(0, "More loots Level X", 4, 6, 50);
//
//	// Cook
//	public final OpenBakeNPCPower OPEN_BAKE_NPC = new OpenBakeNPCPower(0, "Open bake npc");
//
//	// Slacker
//	public final SpawnBombPower SPAWN_BOMB_POWER1 = new SpawnBombPower(0, "Bomb power level I", 60 * 5, 30);
//	public final SpawnBombPower SPAWN_BOMB_POWER2 = new SpawnBombPower(0, "Bomb power level II", 60 * 4, 25);
//	public final SpawnBombPower SPAWN_BOMB_POWER3 = new SpawnBombPower(0, "Bomb power level II", 60 * 3, 20);
//	public final SuicideBombPower SUICIDE_BOMB_POWER = new SuicideBombPower(10, "Suicide Bomb", 60 * 5, 30);
//	public final VictimTalkPower VICTIM_TALK_POWER = new VictimTalkPower(20, "Victim Talk", 60, 30);
//	public final VictimKillPower KILL_POWER = new VictimKillPower(30, "Victim Kill", 60 * 4, 80);
//	public final VictimMolestPower MOLEST_POWER = new VictimMolestPower(40, "Victim Molest", 60 * 5, 50);
//	public final VictimDiseasePower STUN_POWER = new VictimDiseasePower(50, "Victim Stun", 0, 60 * 4, 50);
//	public final VictimDiseasePower SEDUCE_POWER = new VictimDiseasePower(60, "Victim Seduce", 1, 60 * 4, 50);
//	public final AirStrikePower AIRSTRIKE_POWER = new AirStrikePower(70, "Airstrike", 60 * 10, 120);
//	
//	public OccupationPowerFactory(OccupationFactory occufactory) {
//		this.occufactory = occufactory;
//	}
//}
