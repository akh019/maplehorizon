package constants;

import client.MapleJob;
import constants.skills.Aran;

/*
 * @author kevintjuh93
 */
public class GameConstants {

    public static int getHiddenSkill(final int skill) {
        switch (skill) {
            case Aran.HIDDEN_FULL_DOUBLE:
            case Aran.HIDDEN_FULL_TRIPLE:
                return Aran.FULL_SWING;
            case Aran.HIDDEN_OVER_DOUBLE:
            case Aran.HIDDEN_OVER_TRIPLE:
                return Aran.OVER_SWING;
        }
        return skill;
    }
    public final static int MAX_LEVEL_NORMAL = 200;
	public final static int MAX_LEVEL_CYGNUS = 200; //120
	public final static boolean MODIFIED_SKILLS = false;
	
	public final static int POTION_DISCOUNT_RATE = 0;
	
	public static final String MAX_LEVEL_INCREMENT_MESSAGE = "[Congrats] %s has reached Level %s! %s more levels to go!";
    public static final String ACHIEVEMENT_MESSAGE = "[Congrats] %s has achieved the '%s' achievement! Congratulations!";
    
    
    public static boolean isBlockedWarpMap(int map) {
		if (isFishingMap(map))
			return true;
		switch (map) {
		case 10000:
		case 930000800: // Jail
			return true;
		}
		return false;
	}
    
    public static boolean isBlockedIOC(int id) {
		if ((id >= 1140000 && id < 1200000) || (id >= 1930000 && id <= 2000000) || (id >= 3010000 && id < 3020000))
			return true;
		if ((id / 1000) % 10 > 3)
			return true;
		switch (id) {
		case 1002140: // wizet hat
		case 1003142: // wizet hat
		case 5220082: // random wizet hat
		case 1002959: // jr gm hat
		case 1042003: // wizet suit
		case 1062007: // wizet pants
		case 1322013: // wizet suitcase
			return true;
		}
		return false;
	}
    
    public static String getCustomScript(int npc) {
		if (npc >= 9100100 && npc <= 9100117)
			return "c_gachapon" + (npc - 9100100);
		switch (npc) {
                case 2084002:
			return "c_patronShop";
                case 9000157:
			return "c_bootCamp0";
		case 2084000:
			return "c_bootCamp1";    
		case 9010000:
			return "c_gmTool";
                case 9001108:
			return "c_profInfo";
                case 2101:    
			return "c_tutorial1";   
                case 9201052:
			return "c_smegaPrefix";    
                case 2141004:
                        return "Ioc_Trader";
		}
		return null;
	}
    
    
    public static int getCustomMobEXP(int id) {
		switch (id) {
		case 9302000:
			return 25;
		case 9302001:
			return 600;
		case 9302002:
			return 12000;
		case 9302003:
			return 560000;
		}
		return -1;
	}
    
    public static int getSkillBook(final int job) {
        if (job >= 2210 && job <= 2218) {
             return job - 2209;
        }
        return 0;
    }
    // restart maybe?
    
    public static boolean isAranSkills(final int skill) {
    	return Aran.FULL_SWING == skill || Aran.OVER_SWING == skill || Aran.COMBO_TEMPEST == skill || Aran.COMBO_PENRIL == skill || Aran.COMBO_DRAIN == skill 
    			|| Aran.HIDDEN_FULL_DOUBLE == skill || Aran.HIDDEN_FULL_TRIPLE == skill || Aran.HIDDEN_OVER_DOUBLE == skill || Aran.HIDDEN_OVER_TRIPLE == skill
    			|| Aran.COMBO_SMASH == skill || Aran.DOUBLE_SWING  == skill || Aran.TRIPLE_SWING == skill;
    }
    
  
    
    public static boolean isAran(final int job) {
        return job == 2000 || (job >= 2100 && job <= 2112);
    }
    
    public static boolean isInJobTree(int skillId, int jobId) {
    	int skill = skillId / 10000;
    	if ((jobId - skill) + skill == jobId) {
    		return true;
    	}
    	return false;
    }
    
    public static boolean isPqSkill(final int skill) {
    	return skill >= 20001013 && skill <= 20000018 || skill  % 10000000 == 1020 || skill == 10000013 || skill  % 10000000 >= 1009 && skill % 10000000 <= 1011;  
    }
    
    public static boolean bannedBindSkills(final int skill) {
    	return isAranSkills(skill) || isPqSkill(skill);
    }

    public static boolean isGMSkills(final int skill) {
    	return skill >= 9001000 && skill <= 9101008 || skill >= 8001000 && skill <= 8001001; 
    }
    
    public static boolean isDojo(int mapid) {
        return mapid >= 925020100 && mapid <= 925023814;
    }
    
    public static boolean isPyramid(int mapid) {
    	return mapid >= 926010010 & mapid <= 930010000;
    }
    
    public static boolean isPQSkillMap(int mapid) {
    	return isDojo(mapid) || isPyramid(mapid);
    }
    
    public static boolean isFinisherSkill(int skillId) {
        return skillId > 1111002 && skillId < 1111007 || skillId == 11111002 || skillId == 11111003;
    }
    
    public static boolean isFishingMap(int map) {
		return map >= 970020000 && map <= 970020005;
	}
	
	public static boolean isBeginnerJob(int jobid) {
		return !(jobid % 1000 > 0);
	}
    
    
    
	public static boolean hasSPTable(MapleJob job) {
        switch (job) {
			case EVAN:
            case EVAN1:
            case EVAN2:
            case EVAN3:
            case EVAN4:
            case EVAN5:
            case EVAN6:
            case EVAN7:
            case EVAN8:
            case EVAN9:
            case EVAN10:
                return true;
            default:
                return false;
        }
    }
}
