package client;

public enum MaplePerkType {
	NONE		(0, 250, 1, 1, 1),
	EXP_WHORE	(1, 350, 1, 1, 1),
	DROP_WHORE	(2, 250, 3, 1, 1),
	MESO_WHORE	(3, 250, 1, 3, 1),
	NX_WHORE	(4, 250, 1, 1, 3),
	PATRON_WHORE(5, 275, 2, 2, 2, true);
	
	private int id, expRate, dropRate, mesoRate, nxRate;
	private boolean patron;
	private MaplePerkType(int id, int exp, int drop, int meso, int nx) {
		this(id, exp, drop, meso, nx, false);
	}
	
	private MaplePerkType(int id, int exp, int drop, int meso, int nx, boolean patron) {
		this.id = id;
		this.expRate = exp;
		this.dropRate = drop;
		this.mesoRate = meso;
		this.nxRate = nx;
		this.patron = patron;
	}
	
	public int getId() {
		return id;
	}
	
	public int getExpRate() {
		return expRate;
	}
	
	public int getDropRate() {
		return dropRate;
	}
	
	public int getMesoRate() {
		return mesoRate;
	}
	
	public int getNXRate() {
		return nxRate;
	}
	
	public boolean requirePatron() {
		return patron;
	}
	
	public static MaplePerkType getById(int id) {
		for (MaplePerkType m : MaplePerkType.values()) {
		    if (m.id == id) {
		    	return m;
		    }
		}
		return null;
	}
}
