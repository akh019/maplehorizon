package net.server.channel.handlers;

import constants.ItemConstants;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import tools.MaplePacketCreator;
import tools.Randomizer;
import tools.data.input.SeekableLittleEndianAccessor;
import client.MapleClient;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import net.AbstractMaplePacketHandler;

public class UseTreasureChestHandler  extends AbstractMaplePacketHandler {

	@Override
	public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
		slea.readShort();
		int itemId = slea.readInt();
		if (c.getPlayer().haveItem(itemId)) {
			if (itemId >= 4280000 && itemId <= 4280003) {
				int type = itemId - 4280000;
				if (c.getPlayer().haveItem(5490000 + type)) {
					int[] item;
					if (type < 2) {
						if (Randomizer.nextInt() <= 75)
							item = ItemConstants.NORMAL_T_CHEST_REWARDS[(int) (Math.random() * ItemConstants.NORMAL_T_CHEST_REWARDS.length)];
						else
							item = ItemConstants.RARE_T_CHEST_REWARDS[(int) (Math.random() * ItemConstants.RARE_T_CHEST_REWARDS.length)];
					} else {
						if (Randomizer.nextInt() <= 75)
							item = ItemConstants.RARE_T_CHEST_REWARDS[(int) (Math.random() * ItemConstants.RARE_T_CHEST_REWARDS.length)];
						else
							item = ItemConstants.SUPER_RARE_T_CHEST_REWARDS[(int) (Math.random() * ItemConstants.SUPER_RARE_T_CHEST_REWARDS.length)];
					}
					int quantity = item[1];
					MapleInventoryManipulator.addById(c, item[0], (short) quantity);
					MapleInventoryManipulator.removeById(c, MapleInventoryType.ETC, itemId, 1, false, false);
					MapleInventoryManipulator.removeById(c, MapleInventoryType.CASH, 5490000 + type, 1, false, false);
					//c.getChannelServer().broadcastPacket(MaplePacketCreator.serverNotice(6, c.getPlayer().getName() + " got " + item[1] + "x " + MapleItemInformationProvider.getInstance().getName(item[0]) + " from the Treasure Box Gachapon! Congratulations!"));
					Item derp;
					if (MapleItemInformationProvider.getInstance().getInventoryType(item[0]) == MapleInventoryType.EQUIP) {
						derp = MapleItemInformationProvider.getInstance().getEquipById(item[0]);
					} else {
						derp = new Item(item[0], (byte)0, (short)quantity);
					}
					c.getChannelServer().broadcastPacket(MaplePacketCreator.gachaponMessage(derp, "Treasure Box", c.getPlayer()));
					c.getPlayer().dropMessage("You have gained " + quantity + "x " + MapleItemInformationProvider.getInstance().getName(item[0]) + " from the Treasure Chest!");
				} else {
					c.getPlayer().dropMessage("You do not have the Master Key to open the Box.");
				}
			}
		}
	}

}
