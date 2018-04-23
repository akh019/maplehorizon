package client;

public enum MapleProfessionType {
	NONE(0),
	MINER(1), // 50% more ore drops?
	FISHER(2),// 50% more rare fish?
	HUNTER(3),// 50% more drop?
	HENEHOE(4);// 50% more whore?
	
	private int id;
	private MapleProfessionType(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	
	public static MapleProfessionType getById(int id) {
		for (MapleProfessionType m : MapleProfessionType.values()) {
		    if (m.id == id) {
		    	return m;
		    }
		}
		return null;
	}
}
