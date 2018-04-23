package client;

public enum MaplePlagueType {
	NORMAL(false),
	INFECTED(true),
	OVERTIME_INFECTOR(false),
	OVERTIME_INFECTED(false);
	
	boolean debuffable;
	private MaplePlagueType(boolean debuffable) {
		this.debuffable = debuffable;
	};
}
