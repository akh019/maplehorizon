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
package net.server.world;


import client.BuddyList;
import client.BuddyList.BuddyAddResult;
import client.BuddyList.BuddyOperation;
import client.BuddylistEntry;
import client.MapleCharacter;
import client.MapleFamily;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import net.server.PlayerStorage;
import net.server.Server;
import net.server.channel.Channel;
import net.server.channel.CharacterIdChannelPair;
import net.server.guild.MapleGuild;
import net.server.guild.MapleGuildCharacter;
import net.server.guild.MapleGuildSummary;
import tools.DatabaseConnection;
import tools.MaplePacketCreator;
import server.TimerManager;

/**
 *
 * @author kevintjuh93
 */
public class World {

    private int id, flag, exprate, droprate, mesorate, bossdroprate;
    private String eventmsg;
    private List<Channel> channels = new ArrayList<>();
    private Map<Integer, MapleParty> parties = new HashMap<>();
    private AtomicInteger runningPartyId = new AtomicInteger();
    private Map<Integer, MapleMessenger> messengers = new HashMap<>();
    private AtomicInteger runningMessengerId = new AtomicInteger();
    private Map<Integer, MapleFamily> families = new LinkedHashMap<>();
    private Map<Integer, MapleGuildSummary> gsStore = new HashMap<>();
    private PlayerStorage players = new PlayerStorage();
    private String voteQuestion = "";
    private List<String> voteOptions = new ArrayList<>();
    private List<Integer> voteCounts = new ArrayList<>();
    private HashSet<MapleCharacter> voters = new HashSet<>();
    private boolean voteStarted = false;
    private boolean playertrivia = false;

    public World(int world, int flag, String eventmsg, int exprate, int droprate, int mesorate, int bossdroprate) {
        this.id = world;
        this.flag = flag;
        this.eventmsg = eventmsg;
        this.exprate = exprate;
        this.droprate = droprate;
        this.mesorate = mesorate;
        this.bossdroprate = bossdroprate;
        runningPartyId.set(1);
        runningMessengerId.set(1);
    }

    public List<Channel> getChannels() {
        return channels;
    }

    public Channel getChannel(int channel) {
        return channels.get(channel - 1);
    }

    public void addChannel(Channel channel) {
        channels.add(channel);
    }

    public void removeChannel(int channel) {
        channels.remove(channel);
    }

    public void setFlag(byte b) {
        this.flag = b;
    }

    public int getFlag() {
        return flag;
    }

    public String getEventMessage() {
        return eventmsg;
    }

    public int getExpRate() {
        return exprate;
    }

    public void setExpRate(int exp) {
        //System.out.println("Setting server EXP Rate to " + exp + "x.");
        this.exprate = exp;
        for (MapleCharacter chr : getPlayerStorage().getAllCharacters()) {
            chr.setRates();
        }
    }

    public int getDropRate() {
        return droprate;
    }

    public void setDropRate(int drop) {
        this.droprate = drop;
    }

    public int getMesoRate() {
        return mesorate;
    }

    public void setMesoRate(int meso) {
        this.mesorate = meso;
    }

    public int getBossDropRate() {
        return bossdroprate;
    }

    public PlayerStorage getPlayerStorage() {
        return players;
    }

    public void removePlayer(MapleCharacter chr) {
        channels.get(chr.getClient().getChannel() - 1).removePlayer(chr);
        players.removePlayer(chr.getId());
    }

    public int getId() {
        return id;
    }

    public void setPlayerTrivia(boolean on) {
        this.playertrivia = on;
    }

    public boolean isPlayerTrivia() {
        return this.playertrivia;
    }

    public void addFamily(int id, MapleFamily f) {
        synchronized (families) {
            if (!families.containsKey(id)) {
                families.put(id, f);
            }
        }
    }

    public MapleFamily getFamily(int id) {
        synchronized (families) {
            if (families.containsKey(id)) {
                return families.get(id);
            }
            return null;
        }
    }

    public MapleGuild getGuild(MapleGuildCharacter mgc) {
        int gid = mgc.getGuildId();
        MapleGuild g;
        g = Server.getInstance().getGuild(gid, mgc.getWorld(), mgc);
        if (gsStore.get(gid) == null) {
            gsStore.put(gid, new MapleGuildSummary(g));
        }
        return g;
    }

    public MapleGuildSummary getGuildSummary(int gid, int wid) {
        if (gsStore.containsKey(gid)) {
            return gsStore.get(gid);
        } else {
            MapleGuild g = Server.getInstance().getGuild(gid, wid, null);
            if (g != null) {
                gsStore.put(gid, new MapleGuildSummary(g));
            }
            return gsStore.get(gid);
        }
    }

    public void updateGuildSummary(int gid, MapleGuildSummary mgs) {
        gsStore.put(gid, mgs);
    }

    public void reloadGuildSummary() {
        MapleGuild g;
        Server server = Server.getInstance();
        for (int i : gsStore.keySet()) {
            g = server.getGuild(i, getId(), null);
            if (g != null) {
                gsStore.put(i, new MapleGuildSummary(g));
            } else {
                gsStore.remove(i);
            }
        }
    }

    public void setGuildAndRank(List<Integer> cids, int guildid, int rank, int exception) {
        for (int cid : cids) {
            if (cid != exception) {
                setGuildAndRank(cid, guildid, rank);
            }
        }
    }

    public void setOfflineGuildStatus(int guildid, int guildrank, int cid) {
        try {
            try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("UPDATE characters SET guildid = ?, guildrank = ? WHERE id = ?")) {
                ps.setInt(1, guildid);
                ps.setInt(2, guildrank);
                ps.setInt(3, cid);
                ps.execute();
            }
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    public void setGuildAndRank(int cid, int guildid, int rank) {
        MapleCharacter mc = getPlayerStorage().getCharacterById(cid);
        if (mc == null) {
            return;
        }
        boolean bDifferentGuild;
        if (guildid == -1 && rank == -1) {
            bDifferentGuild = true;
        } else {
            bDifferentGuild = guildid != mc.getGuildId();
            mc.setGuildId(guildid);
            mc.setGuildRank(rank);
            mc.saveGuildStatus();
        }
        if (bDifferentGuild) {
            mc.getMap().broadcastMessage(mc, MaplePacketCreator.removePlayerFromMap(cid), false);
            mc.getMap().broadcastMessage(mc, MaplePacketCreator.spawnPlayerMapobject(mc), false);
        }
    }

    public void changeEmblem(int gid, List<Integer> affectedPlayers, MapleGuildSummary mgs) {
        updateGuildSummary(gid, mgs);
        sendPacket(affectedPlayers, MaplePacketCreator.guildEmblemChange(gid, mgs.getLogoBG(), mgs.getLogoBGColor(), mgs.getLogo(), mgs.getLogoColor()), -1);
        setGuildAndRank(affectedPlayers, -1, -1, -1);	//respawn player
    }

    public void sendPacket(List<Integer> targetIds, final byte[] packet, int exception) {
        MapleCharacter c;
        for (int i : targetIds) {
            if (i == exception) {
                continue;
            }
            c = getPlayerStorage().getCharacterById(i);
            if (c != null) {
                c.getClient().announce(packet);
            }
        }
    }

    public MapleParty createParty(MaplePartyCharacter chrfor) {
        int partyid = runningPartyId.getAndIncrement();
        MapleParty party = new MapleParty(partyid, chrfor);
        parties.put(party.getId(), party);
        return party;
    }

    public MapleParty getParty(int partyid) {
        return parties.get(partyid);
    }

    public MapleParty disbandParty(int partyid) {
        return parties.remove(partyid);
    }

    public void updateParty(MapleParty party, PartyOperation operation, MaplePartyCharacter target) {
        for (MaplePartyCharacter partychar : party.getMembers()) {
            MapleCharacter chr = getPlayerStorage().getCharacterByName(partychar.getName());
            if (chr != null) {
                if (operation == PartyOperation.DISBAND) {
                    chr.setParty(null);
                    chr.setMPC(null);
                } else {
                    chr.setParty(party);
                    chr.setMPC(partychar);
                }
                chr.getClient().announce(MaplePacketCreator.updateParty(chr.getClient().getChannel(), party, operation, target));
            }
        }
        switch (operation) {
            case LEAVE:
            case EXPEL:
                MapleCharacter chr = getPlayerStorage().getCharacterByName(target.getName());
                if (chr != null) {
                    chr.getClient().announce(MaplePacketCreator.updateParty(chr.getClient().getChannel(), party, operation, target));
                    chr.setParty(null);
                    chr.setMPC(null);
                }
            default:
                break;
        }
    }

    public void updateParty(int partyid, PartyOperation operation, MaplePartyCharacter target) {
        MapleParty party = getParty(partyid);
        if (party == null) {
            throw new IllegalArgumentException("no party with the specified partyid exists");
        }
        switch (operation) {
            case JOIN:
                party.addMember(target);
                break;
            case EXPEL:
            case LEAVE:
                party.removeMember(target);
                break;
            case DISBAND:
                disbandParty(partyid);
                break;
            case SILENT_UPDATE:
            case LOG_ONOFF:
                party.updateMember(target);
                break;
            case CHANGE_LEADER:
                party.setLeader(target);
                break;
            default:
                System.out.println("Unhandeled updateParty operation " + operation.name());
        }
        updateParty(party, operation, target);
    }

    public int find(String name) {
        int channel = -1;
        MapleCharacter chr = getPlayerStorage().getCharacterByName(name);
        if (chr != null) {
            channel = chr.getClient().getChannel();
        }
        return channel;
    }

    public int find(int id) {
        int channel = -1;
        MapleCharacter chr = getPlayerStorage().getCharacterById(id);
        if (chr != null) {
            channel = chr.getClient().getChannel();
        }
        return channel;
    }

    public void partyChat(MapleParty party, String chattext, String namefrom) {
        for (MaplePartyCharacter partychar : party.getMembers()) {
            if (!(partychar.getName().equals(namefrom))) {
                MapleCharacter chr = getPlayerStorage().getCharacterByName(partychar.getName());
                if (chr != null) {
                    chr.getClient().announce(MaplePacketCreator.multiChat(namefrom, chattext, 1));
                }
            }
        }
    }

    public void buddyChat(int[] recipientCharacterIds, int cidFrom, String nameFrom, String chattext) {
        PlayerStorage playerStorage = getPlayerStorage();
        for (int characterId : recipientCharacterIds) {
            MapleCharacter chr = playerStorage.getCharacterById(characterId);
            if (chr != null) {
                if (chr.getBuddylist().containsVisible(cidFrom)) {
                    chr.getClient().announce(MaplePacketCreator.multiChat(nameFrom, chattext, 0));
                }
            }
        }
    }

    public CharacterIdChannelPair[] multiBuddyFind(int charIdFrom, int[] characterIds) {
        List<CharacterIdChannelPair> foundsChars = new ArrayList<>(characterIds.length);
        for (Channel ch : getChannels()) {
            for (int charid : ch.multiBuddyFind(charIdFrom, characterIds)) {
                foundsChars.add(new CharacterIdChannelPair(charid, ch.getId()));
            }
        }
        return foundsChars.toArray(new CharacterIdChannelPair[foundsChars.size()]);
    }

    public MapleMessenger getMessenger(int messengerid) {
        return messengers.get(messengerid);
    }

    public void leaveMessenger(int messengerid, MapleMessengerCharacter target) {
        MapleMessenger messenger = getMessenger(messengerid);
        if (messenger == null) {
            throw new IllegalArgumentException("No messenger with the specified messengerid exists");
        }
        int position = messenger.getPositionByName(target.getName());
        messenger.removeMember(target);
        removeMessengerPlayer(messenger, position);
    }

    public void messengerInvite(String sender, int messengerid, String target, int fromchannel) {
        if (isConnected(target)) {
            MapleMessenger messenger = getPlayerStorage().getCharacterByName(target).getMessenger();
            if (messenger == null) {
                getPlayerStorage().getCharacterByName(target).getClient().announce(MaplePacketCreator.messengerInvite(sender, messengerid));
                MapleCharacter from = getChannel(fromchannel).getPlayerStorage().getCharacterByName(sender);
                from.getClient().announce(MaplePacketCreator.messengerNote(target, 4, 1));
            } else {
                MapleCharacter from = getChannel(fromchannel).getPlayerStorage().getCharacterByName(sender);
                from.getClient().announce(MaplePacketCreator.messengerChat(sender + " : " + target + " is already using Maple Messenger"));
            }
        }
    }

    public void addMessengerPlayer(MapleMessenger messenger, String namefrom, int fromchannel, int position) {
        for (MapleMessengerCharacter messengerchar : messenger.getMembers()) {
            MapleCharacter chr = getPlayerStorage().getCharacterByName(messengerchar.getName());
            if (chr == null) {
                continue;
            }
            if (!messengerchar.getName().equals(namefrom)) {
                MapleCharacter from = getChannel(fromchannel).getPlayerStorage().getCharacterByName(namefrom);
                chr.getClient().announce(MaplePacketCreator.addMessengerPlayer(namefrom, from, position, (byte) (fromchannel - 1)));
                from.getClient().announce(MaplePacketCreator.addMessengerPlayer(chr.getName(), chr, messengerchar.getPosition(), (byte) (messengerchar.getChannel() - 1)));
            } else {
                chr.getClient().announce(MaplePacketCreator.joinMessenger(messengerchar.getPosition()));
            }
        }
    }

    public void removeMessengerPlayer(MapleMessenger messenger, int position) {
        for (MapleMessengerCharacter messengerchar : messenger.getMembers()) {
            MapleCharacter chr = getPlayerStorage().getCharacterByName(messengerchar.getName());
            if (chr != null) {
                chr.getClient().announce(MaplePacketCreator.removeMessengerPlayer(position));
            }
        }
    }

    public void messengerChat(MapleMessenger messenger, String chattext, String namefrom) {
        String from = "";
        String to1 = "";
        String to2 = "";
        for (MapleMessengerCharacter messengerchar : messenger.getMembers()) {
            if (!(messengerchar.getName().equals(namefrom))) {
                MapleCharacter chr = getPlayerStorage().getCharacterByName(messengerchar.getName());
                if (chr != null) {
                    chr.getClient().announce(MaplePacketCreator.messengerChat(chattext));
                    if (to1.equals("")) {
                        to1 = messengerchar.getName();
                    } else if (to2.equals("")) {
                        to2 = messengerchar.getName();
                    }
                }
            } else {
                from = messengerchar.getName();
            }
        }
    }

    public void declineChat(String target, String namefrom) {
        if (isConnected(target)) {
            MapleCharacter chr = getPlayerStorage().getCharacterByName(target);
            if (chr != null && chr.getMessenger() != null) {
                chr.getClient().announce(MaplePacketCreator.messengerNote(namefrom, 5, 0));
            }
        }
    }

    public void updateMessenger(int messengerid, String namefrom, int fromchannel) {
        MapleMessenger messenger = getMessenger(messengerid);
        int position = messenger.getPositionByName(namefrom);
        updateMessenger(messenger, namefrom, position, fromchannel);
    }

    public void updateMessenger(MapleMessenger messenger, String namefrom, int position, int fromchannel) {
        for (MapleMessengerCharacter messengerchar : messenger.getMembers()) {
            Channel ch = getChannel(fromchannel);
            if (!(messengerchar.getName().equals(namefrom))) {
                MapleCharacter chr = ch.getPlayerStorage().getCharacterByName(messengerchar.getName());
                if (chr != null) {
                    chr.getClient().announce(MaplePacketCreator.updateMessengerPlayer(namefrom, getChannel(fromchannel).getPlayerStorage().getCharacterByName(namefrom), position, (byte) (fromchannel - 1)));
                }
            }
        }
    }

    public void silentLeaveMessenger(int messengerid, MapleMessengerCharacter target) {
        MapleMessenger messenger = getMessenger(messengerid);
        if (messenger == null) {
            throw new IllegalArgumentException("No messenger with the specified messengerid exists");
        }
        messenger.addMember(target, target.getPosition());
    }

    public void joinMessenger(int messengerid, MapleMessengerCharacter target, String from, int fromchannel) {
        MapleMessenger messenger = getMessenger(messengerid);
        if (messenger == null) {
            throw new IllegalArgumentException("No messenger with the specified messengerid exists");
        }
        messenger.addMember(target, target.getPosition());
        addMessengerPlayer(messenger, from, fromchannel, target.getPosition());
    }

    public void silentJoinMessenger(int messengerid, MapleMessengerCharacter target, int position) {
        MapleMessenger messenger = getMessenger(messengerid);
        if (messenger == null) {
            throw new IllegalArgumentException("No messenger with the specified messengerid exists");
        }
        messenger.addMember(target, position);
    }

    public MapleMessenger createMessenger(MapleMessengerCharacter chrfor) {
        int messengerid = runningMessengerId.getAndIncrement();
        MapleMessenger messenger = new MapleMessenger(messengerid, chrfor);
        messengers.put(messenger.getId(), messenger);
        return messenger;
    }

    public boolean isConnected(String charName) {
        return getPlayerStorage().getCharacterByName(charName) != null;
    }

    public void whisper(String sender, String target, int channel, String message) {
        if (isConnected(target)) {
            getPlayerStorage().getCharacterByName(target).getClient().announce(MaplePacketCreator.getWhisper(sender, channel, message));
        }
    }

    public BuddyAddResult requestBuddyAdd(String addName, int channelFrom, int cidFrom, String nameFrom) {
        MapleCharacter addChar = getPlayerStorage().getCharacterByName(addName);
        if (addChar != null) {
            BuddyList buddylist = addChar.getBuddylist();
            if (buddylist.isFull()) {
                return BuddyAddResult.BUDDYLIST_FULL;
            }
            if (!buddylist.contains(cidFrom)) {
                buddylist.addBuddyRequest(addChar.getClient(), cidFrom, nameFrom, channelFrom);
            } else if (buddylist.containsVisible(cidFrom)) {
                return BuddyAddResult.ALREADY_ON_LIST;
            }
        }
        return BuddyAddResult.OK;
    }

    public void buddyChanged(int cid, int cidFrom, String name, int channel, BuddyOperation operation) {
        MapleCharacter addChar = getPlayerStorage().getCharacterById(cid);
        if (addChar != null) {
            BuddyList buddylist = addChar.getBuddylist();
            switch (operation) {
                case ADDED:
                    if (buddylist.contains(cidFrom)) {
                        buddylist.put(new BuddylistEntry(name, "Default Group", cidFrom, channel, true));
                        addChar.getClient().announce(MaplePacketCreator.updateBuddyChannel(cidFrom, (byte) (channel - 1)));
                    }
                    break;
                case DELETED:
                    if (buddylist.contains(cidFrom)) {
                        buddylist.put(new BuddylistEntry(name, "Default Group", cidFrom, (byte) -1, buddylist.get(cidFrom).isVisible()));
                        addChar.getClient().announce(MaplePacketCreator.updateBuddyChannel(cidFrom, (byte) -1));
                    }
                    break;
            }
        }
    }

    public void loggedOff(String name, int characterId, int channel, int[] buddies) {
        updateBuddies(characterId, channel, buddies, true);
    }

    public void loggedOn(String name, int characterId, int channel, int buddies[]) {
        updateBuddies(characterId, channel, buddies, false);
    }

    private void updateBuddies(int characterId, int channel, int[] buddies, boolean offline) {
        PlayerStorage playerStorage = getPlayerStorage();
        for (int buddy : buddies) {
            MapleCharacter chr = playerStorage.getCharacterById(buddy);
            if (chr != null) {
                BuddylistEntry ble = chr.getBuddylist().get(characterId);
                if (ble != null && ble.isVisible()) {
                    int mcChannel;
                    if (offline) {
                        ble.setChannel((byte) -1);
                        mcChannel = -1;
                    } else {
                        ble.setChannel(channel);
                        mcChannel = (byte) (channel - 1);
                    }
                    chr.getBuddylist().put(ble);
                    chr.getClient().announce(MaplePacketCreator.updateBuddyChannel(ble.getCharacterId(), mcChannel));
                }
            }
        }
    }

    public boolean setVoteQuestion(String vQuestion) {
        if (voteStarted) {
            return false;
        }
        voteQuestion = vQuestion;
        return true;
    }

    public String getVoteQuestion() {
        return voteQuestion;
    }

    public boolean addVoteOption(String newVOption) {
        if (voteStarted) {
            return false;
        }
        voteOptions.add(newVOption);
        voteCounts.add(0);
        return true;
    }

    public boolean clearVoteOptions() {
        if (voteStarted) {
            return false;
        }
        voteOptions.clear();
        voteCounts.clear();
        return true;
    }

    public String[] getAllVoteOptions() {
        return voteOptions.toArray(new String[voteOptions.size()]);
    }

    public int[] getAllVoteCounts() {
        int result[] = new int[voteCounts.size()];
        for (int i = 0; i < voteCounts.size(); i++) {
            result[i] = voteCounts.get(i);
        }
        return result;
    }

    public boolean vote(int optionNumber, MapleCharacter thisVoter) {
        if (!voteStarted) {
            return false;
        }
        optionNumber -= 1; // I switched around the if's so it first checks if the player inserted an invalid option and then see if he already voted. -Iced
        if (optionNumber >= voteCounts.size() || optionNumber < 0) {
            thisVoter.dropMessage("There are only " + String.valueOf(voteCounts.size()) + " option" + (voteCounts.size() == 1 ? "" : "s") + " for this poll.");
            for (int i = 0; i < voteOptions.size(); i++) {
                thisVoter.dropMessage("[OPTION " + String.valueOf(i + 1) + "] " + voteOptions.get(i));
            }
            return false;
        }
        if (!voters.add(thisVoter)) {
            thisVoter.dropMessage("You have already placed your vote!");
            return false;
        }
        voteCounts.set(optionNumber, voteCounts.get(optionNumber) + 1);
        thisVoter.message("Voted successfully for " + voteOptions.get(optionNumber) + ".");
        return true;
    }

    public boolean isVoteStarted() {
        return voteStarted;
    }

    public void startVote(int updateInterval) {
        if (voteStarted) {
            return;
        }
        if (voteQuestion.equals("")) { // I've added this small thing so it doesn't start a poll without a question. -Iced        
            return;
        }
        voteStarted = true;

        for (MapleCharacter chr : players.getAllCharacters()) {
            chr.dropMessage(6, "[POLL] " + voteQuestion);
            for (int i = 0; i < voteOptions.size(); i++) {
                chr.dropMessage(6, "[OPTION " + String.valueOf(i + 1) + "] " + voteOptions.get(i));
            }
            chr.dropMessage("Use the command @vote <option number> to place your votes.");
        }

        /*TimerManager.getInstance().schedule(new Runnable() {
                 [MENTION=2000004426]Override[/MENTION]
            public void run() {
                voteStarted = false;
            }
        }, updateInterval);*/
        updateVote(updateInterval);
    }

    public void startVote() {
        startVote(60 * 1000);
    }

    private void updateVote(final int updateInterval) {
        TimerManager.getInstance().schedule(new Runnable() {
            public void run() {
                if (voteStarted == false) {
                    return;
                }
                broadcastPacket(MaplePacketCreator.serverNotice(6, "[POLL] " + voteQuestion));
                for (int i = 0; i < voteOptions.size(); i++) {
                    broadcastPacket(MaplePacketCreator.serverNotice(6, "[OPTION " + String.valueOf(i + 1) + "] " + voteOptions.get(i) + " - Voted: " + voteCounts.get(i)));
                }
                broadcastPacket(MaplePacketCreator.serverNotice(6, "Use the command @vote <option number> to place your votes."));
                updateVote(updateInterval);
            }
        }, updateInterval);
    }

    public void endVote() {
        if (!voteStarted) {
            return;
        }
        voteStarted = false;

        for (MapleCharacter chr : getPlayerStorage().getAllCharacters()) {
            chr.dropMessage(6, "[POLL] The poll has ended. The following are the results to the question: " + voteQuestion);
            for (int i = 0; i < voteOptions.size(); i++) {
                chr.dropMessage(6, "[OPTION " + String.valueOf(i + 1) + "] " + voteOptions.get(i) + " - Voted: " + voteCounts.get(i));
            }
        }

        voteCounts.clear();  //I've decided to add this, if the GM decides not to clear the poll but instead, repeat it, itll still clear votes. -Iced
        for (int i = 0; i < voteOptions.size(); i++) {
            voteCounts.add(0);
        }
        voters = new HashSet<>();

    }

    public void clearVoting() {
        voteQuestion = "";
        voteOptions = new ArrayList<>();
        voteCounts = new ArrayList<>();
        voters = new HashSet<>();
        voteStarted = false;
    }

    public void setServerMessage(String msg) {
        for (Channel ch : channels) {
            ch.setServerMessage(msg);
        }
    }

    public void broadcastPacket(final byte[] data) {
        for (MapleCharacter chr : players.getAllCharacters()) {
            chr.announce(data);
        }
    }

    public final void shutdown() {
        for (Channel ch : getChannels()) {
            ch.shutdown();
        }
        players.disconnectAll();
        
    }
}
   