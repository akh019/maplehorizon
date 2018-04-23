//package client.occupations.powers.OccupationPower;
//
//import server.MapleInventoryManipulator;
//import client.MapleCharacter;
//import client.MapleClient;
//import java.sql.Timestamp;
//import client.inventory.MapleInventoryType;
//
//public abstract class OccupationPower {
//
//	Timestamp.player timestamp;
//	final int id, cooldown;
//	String name, usage;
//
//	// set cooldown to -1 for no cooldown
//	public OccupationPower(int id, String name, int delay, Timestamp.player timestamp) {
//		this.id = id;
//		this.name = name;
//		this.cooldown = delay;
//		this.timestamp = timestamp;
//	}
//
//	/**
//	 * @return The id of this power
//	 */
//	public int getId() {
//		return id;
//	}
//
//	/**
//	 * @return The name of this power
//	 */
//	public String getName() {
//		return name;
//	}
//
//	/**
//	 * @return A string that explains the usage of this power
//	 */
//	public String getUsage() {
//		return usage;
//	}
//
//	/**
//	 * @return The amount of cookies to use this power
//	 */
//	public int getPrice() {
//		return 0;
//	}
//
//	/**
//	 * @return The cooldown in seconds
//	 */
//	public int getCooldown() {
//		return cooldown;
//	}
//
//	/**
//	 * Executes the power with no victim or commandline arguments
//	 *
//	 * @param chr source using the power
//	 * @return The result
//	 */
//	public int execute(final MapleCharacter chr) {
//		return execute(chr.getClient(), false, null);
//	}
//
//	/**
//	 * This method will do the main checks, most of the time,
//	 * it does not have to be overriden or modified as this is
//	 * applicable in a lot of cases and should do most of the time
//	 * <p>
//	 * -2: exception in power execution
//	 * -1: power not found
//	 * 0: power executed succesfully
//	 * 1: power execution failed (player's occupation is not high level enough)
//	 * 2: power execution failed (still on cooldown)
//	 * 3: power execution failed (not enough cookies)
//	 * 4: power execution failed (no victim specified)
//	 * 5: power execution failed (victim not found)
//	 */
//	public int execute(final MapleClient c, final boolean target, final String[] splitted) {
//		final MapleCharacter chr = c.getPlayer();
//		try {
//			if (cooldown > 0 ? checkCooldown(c) : true) {
//				if (checkCookies(c)) {
//					MapleInventoryManipulator.removeById(c, MapleInventoryType.ETC, 4032061, getPrice(), false, false);
//					MapleCharacter victim = null;
//					if (target) {
//						if (splitted.length < 2) {
//							chr.dropMessage(5, "You must specify a victim");
//							return 4;
//						}
//
//						victim = c.getWorldServer().getPlayerStorage().getCharacterByName(splitted[1]);
//
//						if (victim == null) {
//							chr.dropMessage(5, "Victim not found!");
//							return 5;
//						}
//					}
//					if (doPower(chr, victim, splitted)) {
//						if (this.timestamp.id > -1) {
//							chr.setTimestamp(this.timestamp, System.currentTimeMillis());
//						}
//					}
//					return 0;
//				} else {
//					chr.dropMessage(5, "You do not have enough cookies for this occupation power. (Required: " + getPrice() + " cookies)");
//					return 3;
//				}
//			} else {
//				long remaining = (cooldown * 1000) - (System.currentTimeMillis() - c.getPlayer().getTimestamp(this.timestamp));
//				String remainingTime = remainingTimeToString(remaining);
//				chr.dropMessage(5, "This occupation power is still on cooldown. (remaining: " + remainingTime + ")");
//				return 2;
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			return -2;
//		}
//	}
//
//	protected boolean checkCooldown(final MapleClient c) {
//		if (c.getPlayer().isGM()) {
//			return true;
//		}
//
//		return (System.currentTimeMillis() - c.getPlayer().getTimestamp(this.timestamp) > (cooldown * 1000));
//	}
//
//	protected boolean checkCookies(final MapleClient c) {
//		return (c.getPlayer().getItemQuantity(4032061, false) >= getPrice());
//	}
//
//	protected String remainingTimeToString(final long remaining) {
//		StringBuilder sb = new StringBuilder();
//
//		long sec = remaining / 1000;
//		long min = sec / 60;
//		long hr = min / 60;
//		long dy = hr / 24;
//
//		long millisToS = remaining - (1000 * sec);
//		long secToS = sec - (60 * min);
//		long minToS = min - (60 * hr);
//		long hrToS = hr - (24 * dy);
//
//		String SMillis = String.valueOf(millisToS);
//		try {
//			SMillis = SMillis.substring(0, 2);
//		} catch (Exception e) {
//		}
//
//		sb.append(hrToS).append("h ");
//		sb.append(minToS).append("min ");
//		sb.append(secToS).append(".").append(SMillis).append("secs ");
//
//		return sb.toString();
//	}
//
//	/**
//	 * @return The amount of bonus loots
//	 */
//	public int getBonusLoots() {
//		return 0;
//	}
//
//	/**
//	 * @param chr    The player executing the power
//	 * @param victim The victim if any
//	 * @param args   The arguments (if used from a command)
//	 * @return If the power was executed successfuly (true == success!)
//	 */
//	public abstract boolean doPower(final MapleCharacter chr, final MapleCharacter victim, String[] args);
//
//}
