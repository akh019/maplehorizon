//package client.occupations.powers.OccupationPower;
//
//import server.life.MobSkillFactory;
//import client.MapleCharacter;
//import client.MapleDisease;
//import client.Timestamps;
//
//public class VictimDiseasePower extends OccupationPower {
//	
//	int price;
//	int type;
//	
//	public VictimDiseasePower(int id, String name, int type, int cooldown, int price) {
//		super(id, name, cooldown, Timestamps.player.VICTIM_DISEASE_POWER);
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
//		if (type == 0) {
//			victim.giveDebuff(MapleDisease.STUN, MobSkillFactory.getMobSkill(123, 1));
//			victim.dropMessage(5, chr.getName() + " has stunned you!");
//		} else if (type == 1) {
//			victim.giveDebuff(MapleDisease.SEDUCE, MobSkillFactory.getMobSkill(128, 1));
//			victim.dropMessage(5, chr.getName() + " has seduced you!");
//		} else {
//			return false;
//		}
//		
//		return true;
//	}
//
//}
