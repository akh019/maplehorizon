package client;

public enum MapleAchievementType {
	JOIN_SERVER(1, "Why, hello there", "Join the Server", 2),
	PURCHASE_HOUSE(2, "GOT DA DEEEEEEEEEEEE(d)", "Purchase a House", 5), // TODO
	
	/*REACH_LEVEL_120(5, "Now do this everyday", "Reach Level 120", 15),
	REACH_LEVEL_200(6, "Rinse and Repeat", "Reach Level 200", 30),
	REACH_LEVEL_250(7, "No-life", "Reach Level 250", 45, true),
	
	REACH_CHAOS_LEVEL_30(10, null, "Reach Level 30 in Chaos World", 15, true), // TODO
	REACH_CHAOS_LEVEL_70(11, null, "Reach Level 70 in Chaos World", 30, true), // TODO
	REACH_CHAOS_LEVEL_120(12, null, "Reach Level 120 in Chaos World", 45, true, true), // TODO
	REACH_CHAOS_LEVEL_200(13, null, "Reach Level 200 in Chaos World", 60, true, true), // TODO
	REACH_CHAOS_LEVEL_250(13, null, "Reach Level 250 in Chaos World", 75, true, true), // TODO*/
	
	REACH_100_REBIRTHS(5, "Now do this everyday", "Reach 100 rebirths", 15),
	REACH_500_REBIRTHS(6, "Rinse and Repeat", "Reach 500 rebirths", 30),
	REACH_1000_REBIRTHS(7, "What is love???????", "Reach 1000 rebirths", 45),
	REACH_2500_REBIRTHS(8, "No-life", "Reach 2500 rebirths", 60),
	REACH_5000_REBIRTHS(9, "nameplez", "Reach 5000 rebirths", 75),
	
	REACH_FAME_100(20, "B > fame@@@", "Reach 100 Fame", 40),
	REACH_FAME_250(21, "Lf > Attention", "Reach 250 Fame", 60),
	REACH_FAME_500(22, "much fame", "Reach 500 Fame", 80),
	REACH_FAME_1000(23, "Celebrity", "Reach 1000 Fame", 100, true, true),
	REACH_FAME_10000(24, "Can't get enough", "Reach 10000 Fame", 140, true, true),
	REACH_FAME_20000(25, "Livin' for the Applause", "Reach 20000 Fame", 160, true, true),
	REACH_FAME_30000(26, "Fame Monster", "Reach 30000 Fame", 180, true, true),
	
	//DEAL_9999_DAMAGE(30, "Nine thousand, nine hundred and ninety-nine", "Deal 9,999 damage to a monster", 10, true), // TODO
	//DEAL_99999_DAMAGE(31, "Ninety-nine thousand, nine hundred and ninety-nine", "Deal 99,999 damage to a monster", 20, true), // TODO
	//DEAL_999999_DAMAGE(32, "999,999", "Deal 999,999 damage to a monster", 40, true, true), // TODO
	
	WIN_RPS_GAME(40, "Rock, Paper, Scissors!", "Win at Rock-Paper-Scissors game", 5, true), // TODO
	WIN_OMOK_GAME(41, "No different from Tic-Tac-Toe", "Win at Omok game", 5), // TODO
	WIN_MEMORY_GAME(42, "Oh, the Rememberer", "Win at Match Cards game", 5), // TODO
	
	PARTICIPATE_COCONUT_HARVEST_EVENT(45, "C-O-C-O-N-U-T", "Participate in Coconut Harvest event", 7, true, false, 7),
	PARTICIPATE_OLA_OLA_EVENT(46, "Ola Ola!", "Participate in Ola-Ola event", 7, true, false, 7),
	//PARTICIPATE_OX_QUIZ_EVENT(47, "Not the real Ox", "Participate in OX Quiz event", 7, true, false, 7), // TODO
	PARTICIPATE_SNOWBALL_EVENT(48, "Roll, Roll, Roll your Snowball!", "Participate in Snowball event", 7, true, false, 7),
	
	COMPLETE_LUDIBRIUM_MAZE_PQ(60, "Stick to the Left!", "Complete the Ludibrium Maze Party Quest", 20), // TODO
	COMPLETE_CRIMSON_WOOD_KEEP_PQ(61, "Well that was Easy", "Complete the Crimsonwood Keep Party Quest", 25, true), // TODO
	COMPLETE_AMORIAN_CHALLENGE(65, "<3", "Complete the Amorian Challenge", 30, true), // TODO
	COMPLETE_SHARENIAN_GQ(66, "Archaeologist", "Complete the Sharenian Guild Quest", 30, true), // TODO
	
	EASTER_EGG_1(800, "How innovative", "Fish in the Toilet", 20, true),
	
	DEFEAT_PIANUS(900, "Fillet-O-Fish", "Defeat Pianus", 25),
	DEFEAT_PAPULATUS(901, "What time is it?", "Defeat Papulatus", 30),
	DEFEAT_ZAKUM(902, "Eight-fingered not Octopus", "Defeat Zakum", 60, true, true),
	DEFEAT_HORNTAIL(903, "IT'S A DRAAAGOOOOOOOON!", "Defeat Horntail", 90, true, true),
	DEFEAT_PINK_BEAN(904, "Bean Pink'd", "Defeat Pink Bean", 120, true, true), // TODO
	
	DAILY_GAIN_A_REBIRTH(1000, "Daily dose of rebirthing", "Gain a Rebirth (Daily)", 1, false, false, 1),
	DAILY_COMPLETE_LUDIBRIUM_MAZE_PQ(1010, "Stuck on the Left", "Complete the Ludibrium Maze Party Quest (Daily)", 5, false, false, 1),
	DAILY_COMPLETE_CRIMSON_WOOD_KEEP_PQ(1011, "Was that too hard?", "Complete the Crimsonwood Keep Party Quest (Daily)", 10, false, false, 1),
	DAILY_COMPLETE_AMORIAN_CHALLENGE(1015, "I love you too?", "Complete the Amorian Challenge (Daily)", 15, false, false, 1),
	DAILY_COMPLETE_SHARENIAN_GQ(1016, "Reanimated Skeleton Dude", "Complete the Sharenian Guild Quest (Daily)", 15, false, false, 1),
	
	TOP_REBIRTHER(2000, "Blasting through the Ranks", "Achieve #1 on the Rankings.", 1, true, true, 1);
	
	private int id;
	private String name;
	private String desc;
	private int amount;
	
	private boolean hide = false;
	private boolean notice = false;
	
	private int reset = -1;
	
	private MapleAchievementType(int id, String name, String desc, int amount) {
		this.id = id;
		this.name = name;
        this.desc = desc;
        this.amount = amount;
	}
	
	private MapleAchievementType(int id, String name, String desc, int amount, boolean hide) {
		this.id = id;
		this.name = name;
        this.desc = desc;
        this.amount = amount;
        this.hide = hide;
	}
	
	private MapleAchievementType(int id, String name, String desc, int amount, boolean hide, boolean notice) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.amount = amount;
        this.hide = hide;
        this.notice = notice;
    }
	
	private MapleAchievementType(int id, String name, String desc, int amount, boolean hide, boolean notice, int reset) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.amount = amount;
        this.hide = hide;
        this.notice = notice;
        this.reset = reset;
    }
	
	public int getId() {
        return id;
    }
	
	public String getName() {
		return name;
	}
	
	public String getDesc() {
		return desc;
	}
	
	public int getAmount() {
		return amount;
	}
	
	public boolean getHide() {
		return hide;
	}
	
	public boolean getNotice() {
		return notice;
	}
	
	public int getReset() {
		return reset;
	}
	
	public static MapleAchievementType getByName(String name) {
		for (MapleAchievementType m : MapleAchievementType.values()) {
		    if (m.name == name || m.name() == name) {
		    	return m;
		    }
		}
		return null;
	}
	
	public static MapleAchievementType getById(int id) {
		for (MapleAchievementType m : MapleAchievementType.values()) {
		    if (m.id == id) {
		    	return m;
		    }
		}
		return null;
	}
}
