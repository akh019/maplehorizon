/*
	This file is part of the OdinMS Maple Story Server
    Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
		       Matthias Butz <matze@odinms.de>
		       Jan Christian Meyer <vimes@odinms.de>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation version 3 as published by
    the Free Software Foundation. You may not use, modify or distribute
    this program under any other version of the GNU Affero General Public
    License.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package constants;

import tools.Randomizer;
import client.inventory.MapleInventoryType;

/**
 *
 * @author Jay Estrella
 */
public final class ItemConstants {
    public final static int LOCK = 0x01;
    public final static int SPIKES = 0x02;
    public final static int COLD = 0x04;
    public final static int UNTRADEABLE = 0x08;
    public final static int KARMA = 0x10;
    public final static int PET_COME = 0x80;
    public final static int ACCOUNT_SHARING = 0x100;
    public final static float ITEM_ARMOR_EXP = 1 / 350000;
    public static final float ITEM_WEAPON_EXP = 1 / 700000;

    public final static boolean EXPIRING_ITEMS = true;

    public static int getFlagByInt(int type) {
        if (type == 128) {
            return PET_COME;
        } else if (type == 256) {
            return ACCOUNT_SHARING;
        }
        return 0;
    }

    public static boolean isThrowingStar(int itemId) {
        return itemId / 10000 == 207;
    }

    public static boolean isBullet(int itemId) {
        return itemId / 10000 == 233;
    }

    public static boolean isRechargable(int itemId) {
        return isThrowingStar(itemId) || isBullet(itemId);
    }

    public static boolean isArrowForCrossBow(int itemId) {
        return itemId / 1000 == 2061;
    }

    public static boolean isArrowForBow(int itemId) {
        return itemId / 1000 == 2060;
    }

    public static boolean isPet(int itemId) {
        return itemId / 1000 == 5000;
    }

    public static MapleInventoryType getInventoryType(final int itemId) {
	final byte type = (byte) (itemId / 1000000);
	if (type < 1 || type > 5) {
	    return MapleInventoryType.UNDEFINED;
	}
	return MapleInventoryType.getByType(type);
    }
    public static int[][] NORMAL_T_CHEST_REWARDS = {
    	{4030002, Randomizer.nextInt(5)}, // Tetris
    	{4030003, Randomizer.nextInt(5)}, // Tetris
    	{4030004, Randomizer.nextInt(5)}, // Tetris
    	{4030005, Randomizer.nextInt(5)}, // Tetris
    	{5220000, Randomizer.rand(2, 4)}, // Gachapon Ticket
    	{5062000, Randomizer.rand(2, 4)}, // Miracle Cube
    	{4000038, 1}, // Event Trophy
    	{5062001, 1}, // P. Miracle Cube
    	{1003982, 1}, // idk bunny hat
    	{1004168, 1}, // idk
    	{1004167, 1}, // idk
    	{1004162, 1}, // idk
    	{1004161, 1}, // idk
    	{3010300, 1}, // Orange Potion Chair
    	{1003542, 1}, // Macaroon
    	{1003814, 1},
    	{1003844, 1},
    	{1003845, 1},
    	{1003846, 1},
    	{1003847, 1},
    	{1003848, 1},
    	{1003849, 1},
    	{1003850, 1},
    	{1003851, 1},
    	{1003852, 1},
    	{1003836, 1},
    	{1003837, 1},
    	{1003838, 1},
    	{1003839, 1},
    	{1002919, 1}, // Sheep Hat
    	{1002936, 1}, // Black Sheep Hat
    	{1003698, 1}, // VIP Hat
    	{1012363, 1}, // Generate Mark
    	{1382144, 1}, // Glow weapon
    	{1402130, 1}, // Glow weapon
    	{1422090, 1}, // Glow weapon
    	//{1439218, 1}, // Glow weapon // Bugged
    	{1452148, 1}, // Glow weapon
    	{1462138, 1}, // Glow weapon
    	{1492121, 1}, // Glow weapon
    	{1042279, 1}, // Korean Consonant Shirt
    	{1042280, 1}, // Korean Consonant Shirt
    	{1042281, 1}, // Korean Consonant Shirt
    	{1042282, 1}, // Korean Consonant Shirt
    	{1032181, 1}, // Sakura Earrings
    	{1142866, 1}, // Ordinary Luck Medal
    	{1142868, 1}, // Terrible Luck Medal
    	{1142869, 1}, // Worst Luck Medal
    	{1142844, 1}, // Free Leech Medal
    	{1142845, 1}, // Free Hugs Medal
    	{1142846, 1}, // Free Mesos Medal
    	{1142785, 1}, // Spring Medal
    	{1142786, 1}, // Autumn Medal
    	{1142787, 1}, // Winter Medal
    	{1142788, 1}, // Summer Medal
    	{1142553, 1}, // First Furious Step
    	{1142554, 1} // Personified Rage
    };
    
    public static int[][] RARE_T_CHEST_REWARDS = {
    	{4030006, Randomizer.nextInt(3)}, // Tetris
    	{4030007, Randomizer.nextInt(3)}, // Tetris
    	{4030008, Randomizer.nextInt(3)}, // Tetris
    	{5220000, Randomizer.rand(5, 7)}, // Gachapon Ticket
    	{5062000, Randomizer.rand(4, 6)}, // Miracle Cube
    	{5062001, Randomizer.rand(2, 4)}, // P. Miracle Cube
    	{4000038, 2}, // Event Trophy
    	{1003541, 1}, // Bunny hat
    	{5220082, 1}, // Mount Gachapon Ticket
    	{1003980, 1}, // idk bunny hat
    	{1003981, 1}, // idk bunny hat
    	{1004049, 1}, // Black Beanie idk
    	{1004050, 1}, // Beige Headband
    	{1004051, 1}, // Brown Headband
    	{1052708, 1}, // Black Overall 1
    	{1004170, 1}, // Asuna Hat
    	{1004169, 1}, // Kirito Hat
    	{1004160, 1}, // Ichigo Mask (Black)
    	{3010376, 1}, // Red Potion Chair
    	{3010377, 1}, // Blue Potion Chair
    	{1702432, 1}, // Sxy changing item
    	{1702444, 1}, // BLACK MAGIC STAFF
    	{1003543, 1}, // Macaroon
    	{1003544, 1}, // Macaroon
    	{1003545, 1}, // Macaroon
    	{1003546, 1}, // Macaroon
    	{1003810, 1},
    	{1003811, 1},
    	{1003812, 1},
    	{1003813, 1},
    	{1003697, 1}, // VIP Hat
    	{1102674, 1}, // SOOOSHEEEE
    	{3010023, 1}, // Brown Cola Chair
    	{3010023, 1}, // White Cola Chair
    	{1003759, 1}, // blue kitty
    	{1003760, 1}, // pink kitty
    	{1003793, 1}, // Sakura Pin
    	{1003794, 1}, // Sakura Samurai Hat
    	{1142891, 1}, // Ink Medal
    	{1142892, 1}, // Feather Medal
    	{1142884, 1}, // Pedophile Medal
    	{1142885, 1}, // Pervert Medal
    	{1142886, 1}, // Punk Medal
    	{1142887, 1}, // Wizard Medal
    	{1142864, 1}, // Great Luck
    	{1142865, 1}, // Good Luck
    	{1142792, 1}, // I dont know what to write here Medal
    	{1142555, 1}, // Abyss Retaliator
    	{1142556, 1} // Raging Conqueror
    };
    
    public static int[][] SUPER_RARE_T_CHEST_REWARDS = {
    	{5220000, Randomizer.rand(8, 9)}, // Gachapon Ticket
    	{5220082, 2}, // Mount Gachapon Ticket
    	{5062001, Randomizer.rand(4, 6)}, // P. Miracle Cube
    	{5062002, Randomizer.rand(2, 4)}, // S. Miracle Cube
    	{1052709, 1}, // Black Overall 2
    	{1004159, 1}, // Ichigo Mask
    	{1004166, 1}, // Sasuke
    	{1004165, 1}, // Naruto
    	{1003843, 1}, // Fox Mask
    	{3010301, 1}, // Elixir Chair
    	{3010375, 1}, // Power Elixir Chair
    	{3010416, 1}, // Giant Yeti Chair
    	{3010417, 1}, // Giant Pepe Chair
    	{1702453, 1}, // Blue Lightning
    	{1702456, 1}, // Green Blob
    	{1702433, 1}, // Fireeeeee 
    	{1702457, 1}, // RAINBOW LOLLIPOP
    	{1702375, 1}, // SEASHELL
    	{1003547, 1}, // Macaroon
    	{1003865, 1}, // Starlight Wing
    	{1003696, 1}, // VIP Hat
    	{1142893, 1}, // Novel Writer Medal
    	{1142863, 1}, // Excellent Luck
    	{1142557, 1} // Ultimate Retaliator
    };
}
