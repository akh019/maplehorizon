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
package net;

public enum SendOpcode {

    LOGIN_STATUS(0x00),
    GUEST_ID_LOGIN(0x01),
    ACCOUNT_INFO(0x02),//I guess this was in v83 too :)
    SERVERSTATUS(0x03),//CHECK_USER_LIMIT_RESULT
    GENDER_DONE(0x04),//SET_ACCOUNT_RESULT
    CONFIRM_EULA_RESULT(0x05),
    CHECK_PINCODE(0x06),
    UPDATE_PINCODE(0x07),
    
    VIEW_ALL_CHAR(0x08),
    SELECT_CHARACTER_BY_VAC(0x09),
    
    SERVERLIST(0x0A),
    CHARLIST(0x0B),
    SERVER_IP(0x0C),
    CHAR_NAME_RESPONSE(0x0D),
    ADD_NEW_CHAR_ENTRY(0x0E),
    DELETE_CHAR_RESPONSE(0x0F),
    CHANGE_CHANNEL(0x10),
    PING(0x11),
    KOREAN_INTERNET_CAFE_SHIT(0x12),//Useless ignore it.
    CHANNEL_SELECTED(0x14),
    HACKSHIELD_REQUEST(0x15),//maybe this is RELOG_RESPONSE, can't care less
    RELOG_RESPONSE(0x16),
    CHECK_CRC_RESULT(0x19),
    LAST_CONNECTED_WORLD(0x1A),
    RECOMMENDED_WORLD_MESSAGE(0x1B),
    CHECK_SPW_RESULT(0x1C),
    
    
    /*CWvsContext::OnPacket*/
    INVENTORY_OPERATION(0x1D),
    INVENTORY_GROW(0x1E),
    STAT_CHANGED(0x1F),
    GIVE_BUFF(0x20),
    CANCEL_BUFF(0x21),
    FORCED_STAT_SET(0x22),
    FORCED_STAT_RESET(0x23),
    UPDATE_SKILLS(0x24),
    SKILL_USE_RESULT(0x25),
    FAME_RESPONSE(0x26),
    SHOW_STATUS_INFO(0x27),
    OPEN_FULL_CLIENT_DOWNLOAD_LINK(0x28),
    MEMO_RESULT(0x29),
    MAP_TRANSFER_RESULT(0x2A),
    ANTI_MACRO_RESULT(0x2B),
    CLAIM_RESULT(0x2D),
    CLAIM_AVAILABLE_TIME(0x2E),
    CLAIM_STATUS_CHANGED(0x2F),
    SET_TAMING_MOB_INFO(0x30),
    QUEST_CLEAR(0x31),
    ENTRUSTED_SHOP_CHECK_RESULT(0x32),
    SKILL_LEARN_ITEM_RESULT(0x33),
    GATHER_ITEM_RESULT(0x34),
    SORT_ITEM_RESULT(0x35),
    SUE_CHARACTER_RESULT(0x37),
    TRADE_MONEY_LIMIT(0x39),
    SET_GENDER(0x3A),
    GUILD_BBS_PACKET(0x3B),
    CHAR_INFO(0x3D),
    PARTY_OPERATION(0x3E),
    BUDDYLIST(0x3F),
    GUILD_OPERATION(0x41),
    ALLIANCE_OPERATION(0x42),
    SPAWN_PORTAL(0x43),
    SERVERMESSAGE(0x44),
    INCUBATOR_RESULT(0x45),
    SHOP_SCANNER_RESULT(0x46),
    SHOP_LINK_RESULT(0x47),
    
    MARRIAGE_REQUEST(0x48),
    MARRIAGE_RESULT(0x49),
    WEDDING_GIFT_RESULT(0x4A),
    NOTIFY_MARRIED_PARTNER_MAP_TRANSFER(0x4B),
    
    CASH_PET_FOOD_RESULT(0x4C),
    SET_WEEK_EVENT_MESSAGE(0x4D),
    SET_POTION_DISCOUNT_RATE(0x4E),
    
    BRIDLE_MOB_CATCH_FAIL(0x4F),
    IMITATED_NPC_RESULT(0x50),
    IMITATED_NPC_DATA(0x51),
    LIMITED_NPC_DISABLE_INFO(0x52),
    MONSTER_BOOK_SET_CARD(0x53),
    MONSTER_BOOK_SET_COVER(0x54),
    HOUR_CHANGED(0x55),
    MINIMAP_ON_OFF(0x56),
    CONSULT_AUTHKEY_UPDATE(0x57),
    CLASS_COMPETITION_AUTHKEY_UPDATE(0x58),
    WEB_BOARD_AUTHKEY_UPDATE(0x59),
    SESSION_VALUE(0x5A),
    PARTY_VALUE(0x5B),
    FIELD_SET_VARIABLE(0x5C),
    BONUS_EXP_CHANGED(0x5D),//pendant of spirit etc (guess, not sure about the opcode in v83)
    
    FAMILY_CHART_RESULT(0x5E),
    FAMILY_INFO_RESULT(0x5F),
    FAMILY_RESULT(0x60),
    FAMILY_JOIN_REQUEST(0x61),
    FAMILY_JOIN_REQUEST_RESULT(0x62),
    FAMILY_JOIN_ACCEPTED(0x63),
    FAMILY_PRIVILEGE_LIST(0x64),
    FAMILY_FAMOUS_POINT_INC_RESULT(0x65),
    FAMILY_NOTIFY_LOGIN_OR_LOGOUT(0x66), //? is logged in. LOLWUT
    FAMILY_SET_PRIVILEGE(0x67),
    FAMILY_SUMMON_REQUEST(0x68),
    
    NOTIFY_LEVELUP(0x69),
    NOTIFY_MARRIAGE(0x6A),
    NOTIFY_JOB_CHANGE(0x6B),
    //SET_BUY_EQUIP_EXT(0x6C),//lol?
    MAPLE_TV_USE_RES(0x6D), //It's not blank, It's a popup nibs
    AVATAR_MEGAPHONE_RESULT(0x6E),//bot useless..
    SET_AVATAR_MEGAPHONE(0x6F),
    CLEAR_AVATAR_MEGAPHONE(0x70),
    CANCEL_NAME_CHANGE_RESULT(0x71),
    CANCEL_TRANSFER_WORLD_RESULT(0x72),
    DESTROY_SHOP_RESULT(0x73),
    FAKE_GM_NOTICE(0x74),//bad asses
    SUCCESS_IN_USE_GACHAPON_BOX(0x75),
    NEW_YEAR_CARD_RES(0x76),
    RANDOM_MORPH_RES(0x77),
    CANCEL_NAME_CHANGE_BY_OTHER(0x78),
    SET_BUY_EQUIP_EXT(0x79),
    SCRIPT_PROGRESS_MESSAGE(0x7A),
    DATA_CRC_CHECK_FAILED(0x7B),
    MACRO_SYS_DATA_INIT(0x7C),
    
    /*CStage::OnPacket*/
    SET_FIELD(0x7D),
    SET_ITC(0x7E),
    SET_CASH_SHOP(0x7F),
    
    /*CField::OnPacket*/
    SET_BACK_EFFECT(0x80),
    SET_MAP_OBJECT_VISIBLE(0x81),//CMapLoadable::OnSetMapObjectVisible O_O
    CLEAR_BACK_EFFECT(0x82),
    BLOCKED_MAP(0x83),//TransferFieldRequestIgnored
    BLOCKED_SERVER(0x84),
    FORCED_MAP_EQUIP(0x85),//FIELD_SPECIFIC_DATA
    MULTICHAT(0x86),
    WHISPER(0x87),
    SPOUSE_CHAT(0x88),
    SUMMON_ITEM_INAVAILABLE(0x89), //You can't use it in this map
    
    FIELD_EFFECT(0x8A),
    FIELD_OBSTACLE_ONOFF(0x8B),
    FIELD_OBSTACLE_ONOFF_STATUS(0x8C),
    FIELD_OBSTACLE_ALL_RESET(0x8D),
    BLOW_WEATHER(0x8E),
    PLAY_JUKEBOX(0x8F),

    ADMIN_RESULT(0x90),
    OX_QUIZ(0x91),//QUIZ
    GMEVENT_INSTRUCTIONS(0x92),//DESC
    CLOCK(0x93),
    CONTI_MOVE(0x94),
    CONTI_STATE(0x95),
    SET_QUEST_CLEAR(0x96),
    SET_QUEST_TIME(0x97),
    WARN_MESSAGE(0x98),
    SET_OBJECT_STATE(0x99),
    STOP_CLOCK(0x9A),
    ARIANT_ARENA_SHOW_RESULT(0x9B),
    PYRAMID_GAUGE(0x9D),
    PYRAMID_SCORE(0x9E),
    SPAWN_PLAYER(0xA0),
    REMOVE_PLAYER_FROM_MAP(0xA1),
    CHATTEXT(0xA2), //0
    CHATTEXT1(0xA3), //1
    CHALKBOARD(0xA4),
    UPDATE_CHAR_BOX(0xA5),
    SHOW_CONSUME_EFFECT(0xA6),
    SHOW_SCROLL_EFFECT(0xA7),
    
    SPAWN_PET(0xA8),
    MOVE_PET(0xAA),
    PET_CHAT(0xAB),
    PET_NAMECHANGE(0xAC),
    PET_SHOW(0xAD),
    PET_COMMAND(0xAE),
    SPAWN_SPECIAL_MAPOBJECT(0xAF),
    REMOVE_SPECIAL_MAPOBJECT(0xB0),
    MOVE_SUMMON(0xB1),
    SUMMON_ATTACK(0xB2),
    DAMAGE_SUMMON(0xB3),
    SUMMON_SKILL(0xB4),
    SPAWN_DRAGON(0xB5),
    MOVE_DRAGON(0xB6),
    REMOVE_DRAGON(0xB7),
    MOVE_PLAYER(0xB9),
    CLOSE_RANGE_ATTACK(0xBA),
    RANGED_ATTACK(0xBB),
    MAGIC_ATTACK(0xBC),
    ENERGY_ATTACK(0xBD),
    SKILL_EFFECT(0xBE),
    CANCEL_SKILL_EFFECT(0xBF),
    DAMAGE_PLAYER(0xC0),
    FACIAL_EXPRESSION(0xC1),
    SHOW_ITEM_EFFECT(0xC2),
    SHOW_CHAIR(0xC4),
    UPDATE_CHAR_LOOK(0xC5),
    SHOW_FOREIGN_EFFECT(0xC6),
    GIVE_FOREIGN_BUFF(0xC7),
    CANCEL_FOREIGN_BUFF(0xC8),
    UPDATE_PARTYMEMBER_HP(0xC9),
    CANCEL_CHAIR(0xCD),
    SHOW_ITEM_GAIN_INCHAT(0xCE),
    DOJO_WARP_UP(0xCF),
    LUCKSACK_PASS(0xD0),
    LUCKSACK_FAIL(0xD1),
    MESO_BAG_MESSAGE(0xD2),
    UPDATE_QUEST_INFO(0xD3),
    PLAYER_HINT(0xD6),
    KOREAN_EVENT(0xDB),
    OPEN_UI(0xDC),
    LOCK_UI(0xDD),
    DISABLE_UI(0xDE),
    SPAWN_GUIDE(0xDF),
    TALK_GUIDE(0xE0),
    SHOW_COMBO(0xE1),
    COOLDOWN(0xEA),
    SPAWN_MONSTER(0xEC),
    KILL_MONSTER(0xED),
    SPAWN_MONSTER_CONTROL(0xEE),
    MOVE_MONSTER(0xEF),
    MOVE_MONSTER_RESPONSE(0xF0),
    APPLY_MONSTER_STATUS(0xF2),
    CANCEL_MONSTER_STATUS(0xF3),
    RESET_MONSTER_ANIMATION(0xF4),//LOL? o.o
    //Something with mob, but can't figure out00
    DAMAGE_MONSTER(0xF6),
    ARIANT_THING(0xF9),
    SET_NPC_SCRIPTABLE(0x107),
    SHOW_MONSTER_HP(0xFA),
    SHOW_DRAGGED(0xFB),//CATCH
    CATCH_MONSTER(0xFC),
    SHOW_MAGNET(0xFD),
    SPAWN_NPC(0x101),
    REMOVE_NPC(0x102),
    SPAWN_NPC_REQUEST_CONTROLLER(0x103),
    NPC_ACTION(0x104),
    SPAWN_HIRED_MERCHANT(0x109),
    DESTROY_HIRED_MERCHANT(0x10A),
    UPDATE_HIRED_MERCHANT(0x10B),
    DROP_ITEM_FROM_MAPOBJECT(0x10C),
    REMOVE_ITEM_FROM_MAP(0x10D),
    KITE_MESSAGE(0x10E),
    KITE(0x10F),
    SPAWN_MIST(0x111),
    REMOVE_MIST(0x112),
    SPAWN_DOOR(0x113),
    REMOVE_DOOR(0x114),
    REACTOR_HIT(0x115),
    REACTOR_SPAWN(0x117),
    REACTOR_DESTROY(0x118),
    SNOWBALL_STATE(0x119),
    HIT_SNOWBALL(0x11A),
    SNOWBALL_MESSAGE(0x11B),
    LEFT_KNOCK_BACK(0x11C),
    COCONUT_HIT(0x11D),
    COCONUT_SCORE(0x11E),
    GUILD_BOSS_HEALER_MOVE(0x11F),
    GUILD_BOSS_PULLEY_STATE_CHANGE(0x120),
    MONSTER_CARNIVAL_START(0x121),
    MONSTER_CARNIVAL_OBTAINED_CP(0x122),
    MONSTER_CARNIVAL_PARTY_CP(0x123),
    MONSTER_CARNIVAL_SUMMON(0x124),
    MONSTER_CARNIVAL_MESSAGE(0x125),
    MONSTER_CARNIVAL_DIED(0x126),
    MONSTER_CARNIVAL_LEAVE(0x127),
    
    ARIANT_ARENA_USER_SCORE(0x129),
    SHEEP_RANCH_INFO(0x12B),
    SHEEP_RANCH_CLOTHES(0x12C),
    ARIANT_SCORE(0x12D),
    HORNTAIL_CAVE(0x12E),
    ZAKUM_SHRINE(0x12F),
    NPC_TALK(0x130),
    OPEN_NPC_SHOP(0x131),
    CONFIRM_SHOP_TRANSACTION(0x132),
    ADMIN_SHOP_MESSAGE(0x133),//lame :P
    ADMIN_SHOP(0x134),
    STORAGE(0x135),
    FREDRICK_MESSAGE(0x136),
    FREDRICK(0x137),
    RPS_GAME(0x138),
    MESSENGER(0x139),
    PLAYER_INTERACTION(0x13A),
    
    TOURNAMENT(0x13B),
    TOURNAMENT_MATCH_TABLE(0x13C),
    TOURNAMENT_SET_PRIZE(0x13D),
    TOURNAMENT_UEW(0x13E),
    TOURNAMENT_CHARACTERS(0x13F),//they never coded this :|
    
    WEDDING_PROGRESS(0x140),//byte step, int groomid, int brideid
    WEDDING_CEREMONY_END(0x141),
    
    PARCEL(0x142),
    
    CHARGE_PARAM_RESULT(0x143),
    QUERY_CASH_RESULT(0x144),
    CASHSHOP_OPERATION(0x145),
    BalloonMsg(214),
    
    KEYMAP(0x14F),
    AUTO_HP_POT(0x150),
    AUTO_MP_POT(0x151),
    SEND_TV(0x155),
    REMOVE_TV(0x156),
    ENABLE_TV(0x157),
    MTS_OPERATION2(0x15B),
    MTS_OPERATION(0x15C),
    VICIOUS_HAMMER(0x162);
    private int code = -2;

    private SendOpcode(int code) {
        this.code = code;
    }

    public int getValue() {
        return code;
    }
}
