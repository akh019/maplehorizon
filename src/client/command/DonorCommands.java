/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client.command;

import client.inventory.MaplePet;
import client.BuddylistEntry;
import client.MapleCharacter;
import client.MapleClient;
import client.MapleJob;
import client.MapleSkinColor;
import client.MapleStat;
import client.Skill;
import client.SkillFactory;
import static client.command.EventGMCommands.event;
import client.events.EventCommands;
import client.events.EventHandler2;
import client.inventory.MapleInventoryType;
import java.sql.PreparedStatement;
import constants.GameConstants;
import constants.ServerConstants;
import java.awt.Point;
import java.io.File;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;
import net.server.Server;
import net.server.channel.Channel;
import net.server.world.MaplePartyCharacter;
import provider.MapleData;
import provider.MapleDataProviderFactory;
import scripting.event.EventHandler;
import scripting.event.Fishing;
import scripting.event.Ranking;
import scripting.npc.NPCScriptManager;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.MaplePortal;
import server.gachapon.MapleGachapon.Gachapon;
import server.life.MapleLifeFactory;
import server.life.MapleMonster;
import server.life.MapleMonsterInformationProvider;
import server.life.MonsterDropEntry;
import server.maps.MapleMap;
import server.maps.MapleMapItem;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import tools.DatabaseConnection;
import tools.FilePrinter;
import tools.MaplePacketCreator;
import tools.Pair;
import tools.Randomizer;
import tools.StringUtil;


/**
 *
 * @author KaiSheng
 */
public class DonorCommands {
  
        
    public static boolean executeDonorCommandLv1(MapleClient c, String[] splitted, char heading) throws SQLException { //Donator
    MapleCharacter player = c.getPlayer();
        Channel cserv = c.getChannelServer();
        
        if (splitted[0].equalsIgnoreCase("1")
				|| splitted[0].equalsIgnoreCase("2")
				|| splitted[0].equalsIgnoreCase("3")
				|| splitted[0].equalsIgnoreCase("4")) {
			int amount = Integer.parseInt(splitted[1]);
			boolean str = splitted[0].equalsIgnoreCase("1");
			boolean Int = splitted[0].equalsIgnoreCase("2");
			boolean luk = splitted[0].equalsIgnoreCase("3");
			boolean dex = splitted[0].equalsIgnoreCase("4");
			if (amount > 0 && amount <= player.getRemainingAp()
					&& amount <= 32763 || amount < 0 && amount >= -32763
					&& Math.abs(amount) + player.getRemainingAp() <= 32767) {
				if (str && amount + player.getStr() <= 32767
						&& amount + player.getStr() >= 4) {
					player.setStr(player.getStr() + amount);
					player.updateSingleStat(MapleStat.STR, player.getStr());
					player.setRemainingAp(player.getRemainingAp() - amount);
					player.updateSingleStat(MapleStat.AVAILABLEAP,
							player.getRemainingAp());
				} else if (Int && amount + player.getInt() <= 32767
						&& amount + player.getInt() >= 4) {
					player.setInt(player.getInt() + amount);
					player.updateSingleStat(MapleStat.INT, player.getInt());
					player.setRemainingAp(player.getRemainingAp() - amount);
					player.updateSingleStat(MapleStat.AVAILABLEAP,
							player.getRemainingAp());
				} else if (luk && amount + player.getLuk() <= 32767
						&& amount + player.getLuk() >= 4) {
					player.setLuk(player.getLuk() + amount);
					player.updateSingleStat(MapleStat.LUK, player.getLuk());
					player.setRemainingAp(player.getRemainingAp() - amount);
					player.updateSingleStat(MapleStat.AVAILABLEAP,
							player.getRemainingAp());
				} else if (dex && amount + player.getDex() <= 32767
						&& amount + player.getDex() >= 4) {
					player.setDex(player.getDex() + amount);
					player.updateSingleStat(MapleStat.DEX, player.getDex());
					player.setRemainingAp(player.getRemainingAp() - amount);
					player.updateSingleStat(MapleStat.AVAILABLEAP,
							player.getRemainingAp());
				} else {
					player.dropMessage("Please make sure the stat you are trying to raise is not over 32,767 or under 4.");
				}
			} else {
				player.dropMessage("Please make sure your AP is not over 32,767 and you have enough to distribute.");
			}
                        } else if (splitted[0].equals("dbuff")) {
			int[] array = { 2001003, 2301004, 1001003, 2001002, 1101006,
					1101007, 3221002, 1301007, 2201001, 2121004, 1221003,
					2111005, 2311003, 1121002, 4211005, 3121002, 3121008,
					4211003, 1121000, 2311003, 1101004, 1101006, 4101004,
					4111001, 2111005, 1111002, 2321005, 4101003, 4201002,
					1321010, 1121002, 1120003, 4111002, 1005 };
			for (int i = 0; i < array.length; i++) {
					SkillFactory
							.getSkill(array[i])
							.getEffect(
									SkillFactory.getSkill(array[i])
											.getMaxLevel()).applyTo(player);
                        }
                        } else if (splitted[0].equalsIgnoreCase("dnotice")) {
			if (!player.isCommandcooldown(30)) {
				if (splitted.length > 1) {
						Server.getInstance().broadcastMessage(MaplePacketCreator.serverNotice(6, "[Donor] " + StringUtil.joinStringFrom(splitted, 1)));
					} else {
						c.getSession()
								.write(MaplePacketCreator
										.sendYellowTip("Syntax: @dnotice <message>"));
					}
			} else {
				c.getSession().write(MaplePacketCreator.sendYellowTip("You can't use it too often, Its every 30 seconds"));
			}
                        } else if (splitted[0].equals("dannoy")) {
			MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
			if (!victim.inEventMap()) {
					if (victim.getGMLevel()) {
						if (!player.isCommandcooldown(600)) {
							victim.dropMessage(1,
									"XPXPXPXPXPXPPXPX LOLOLOLOLOL XDXDXDXDXD LOLOL");
							victim.dropMessage(1,
									"PXPXPXPPXPX LOLOLOL XDXDXDXD LOLOL");
							victim.dropMessage(1, "PXPPXPX LOLOL XDXD LOLOL");
							victim.dropMessage(1,
									"XPXPXPXPXPXPPXPXPXPXP LOLOLOLOLOLOL XDXDXDXDXDXD LOLOL");
							victim.dropMessage(1,
									"XPXPXPXPXPX LOLOLOLOL XDXDXDXD LOLOL");
							victim.dropMessage(1,
									"XPXPXPXPPXPPXPX LOLOLLLOL XDXDDXDXD LOLOL");
							victim.dropMessage(1,
									"XPXPXPXPXPXPPXPXPXPXP LOLOLOLOLOOLOLL XDXDXDXDXDXDXD LOLOLOLOL");
							victim.dropMessage("You were annoyed by "
									+ c.getPlayer() + ".");
							player.dropMessage("You annoyed that person!");
						} else {
							player.dropMessage("The command is fun, yeah. But, you gotta wait 10 minutes to use it again!");
						}
					} else {
						player.dropMessage("You can't use that command on a GM!");
					}
                        } else {
				player.dropMessage("You can't use that command in this map!");
			}
                        } else if (splitted[0].equals("dnpc")) {
			if (!player.inJail()) {
					player.changeMap(30001);
			} else {
				player.dropMessage("You may not use this command in this map");
			}

		} else if (splitted[0].equals("dmap")) {
			if (!player.inJail()) {
					NPCScriptManager.getInstance().start(c, 1061007, null, null);
			} else {
				player.dropMessage("You may not use this command in this map.");
			}

                        } else if (splitted[0].equals("dcommands")
				|| splitted[0].equals("dcommand")
				|| splitted[0].equals("dhelp")) {
				player.dropMessage("[MapleDivinity's Donor Commands]");
				player.dropMessage("@dnotice <message> - A world message with [Donor].");
				player.dropMessage("@dbuff - Gives you donator buff!");
				player.dropMessage("@dautorbe/c/a - To auto-rebirth to Explorer, Cygnus, and Aran respectively.");
				player.dropMessage("@ditemvac - Itemvac.");
				player.dropMessage("@dautomarble - Automatically buys a Blackhole Marble when u have 2b mesos!");
				player.dropMessage("@dwarp - Warps you to a map or person!");
				player.dropMessage("@dnpc - Warps you to the Donor NPC Map!");
				player.dropMessage("@dmap - Warps you to the Donor Spawner!");
				player.dropMessage("@dannoy - Annoys someone for 1 DP!");
		} else if (splitted[0].equals("dautomarble")) {
				player.autoMeso = !player.autoMeso;
				player.dropMessage(player.autoMeso ? "Auto-marble is on!"
						: "Auto-marble is off!");
		} else if (splitted[0].equals("ditemvac") || splitted[0].equals("dvac")) {
			List<MapleMapObject> items = player.getMap().getMapObjectsInRange(player.getPosition(), Double.POSITIVE_INFINITY, Arrays.asList(MapleMapObjectType.ITEM));
			for (MapleMapObject item : items) {
				MapleMapItem mapItem = (MapleMapItem) item;
					if (player.getMap().getCharacters().size() <= 2) {
						if (mapItem.getMeso() > 0) {
							player.gainMeso(mapItem.getMeso(), true);
							if (c.getPlayer().getMeso() >= 2000000000) {
								int itemID = 4001190;
								short quantity = 1;
								MapleInventoryManipulator.addById(c, itemID,
										quantity);
								c.getPlayer()
										.dropMessage(
												"[Auto Marble] You've earned a Blackhole Marble! Enjoy!");
								c.getPlayer().gainMeso(-1999999999, true, true,
										false);

							}
						} else if (mapItem.getItem().getItemId() >= 5000000
								&& mapItem.getItem().getItemId() <= 5000100) {
							int petId = MaplePet.createPet(mapItem.getItem().getItemId());
							if (petId == -1) {
							}
							MapleInventoryManipulator.addById(c, mapItem
									.getItem().getItemId(), mapItem.getItem()
									.getQuantity(), null, petId);
						} else {
							MapleInventoryManipulator.addFromDrop(c,
									mapItem.getItem(), true);
						}
						mapItem.setPickedUp(true);
						player.getMap().removeMapObject(item); // just incase ?
						player.getMap().broadcastMessage(
								MaplePacketCreator.removeItemFromMap(
										mapItem.getObjectId(), 2,
										player.getId()), mapItem.getPosition());
					} else {
						player.dropMessage("You cannot use this command with more than 2 people on the map.");
					}
			}

               } else if (splitted[0].equalsIgnoreCase("dautorbe")) {
				player.autoRebirth = !player.autoRebirth;
				player.dropMessage(player.autoRebirth ? "Auto rebirth to [Explorer] is on!"
						: "Auto rebirth to [Explorer] is off!");
               
		} else if (splitted[0].equalsIgnoreCase("dautorbc")) {
				player.autoRebirth = !player.autoRebirth;
				player.dropMessage(player.autoRebirth ? "Auto rebirth to [Cygnus] is on!"
						: "Auto rebirth to [Cygnus] is off!");
		} else if (splitted[0].equalsIgnoreCase("dautorba")) {
				player.autoRebirth = !player.autoRebirth;
				player.dropMessage(player.autoRebirth ? "Auto rebirth to [Aran] is on!"
						: "Auto rebirth to [Aran] is off!");
                }
        return false;
    }
}

