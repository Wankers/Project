/*
 *  This file is part of Aion Extreme Emulator <aion-core.net>.
 *
 *  Aion Extreme Emulator is a free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Aion Extreme Emulator is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Aion Extreme Emulator. If not, see <http://www.gnu.org/licenses/>.
 */
package gameserver.model.team2.group;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import commons.callbacks.metadata.GlobalCallback;
import gameserver.configs.main.GroupConfig;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.team2.common.events.PlayerLeavedEvent.LeaveReson;
import gameserver.model.team2.common.events.ShowBrandEvent;
import gameserver.model.team2.common.events.TeamKinahDistributionEvent;
import gameserver.model.team2.common.legacy.GroupEvent;
import gameserver.model.team2.common.legacy.LootGroupRules;
import gameserver.model.team2.group.callback.AddPlayerToGroupCallback;
import gameserver.model.team2.group.callback.PlayerGroupCreateCallback;
import gameserver.model.team2.group.callback.PlayerGroupDisbandCallback;
import gameserver.model.team2.group.events.ChangeGroupLeaderEvent;
import gameserver.model.team2.group.events.ChangeGroupLootRulesEvent;
import gameserver.model.team2.group.events.GroupDisbandEvent;
import gameserver.model.team2.group.events.PlayerConnectedEvent;
import gameserver.model.team2.group.events.PlayerDisconnectedEvent;
import gameserver.model.team2.group.events.PlayerEnteredEvent;
import gameserver.model.team2.group.events.PlayerGroupInvite;
import gameserver.model.team2.group.events.PlayerGroupLeavedEvent;
import gameserver.model.team2.group.events.PlayerGroupStopMentoringEvent;
import gameserver.model.team2.group.events.PlayerGroupUpdateEvent;
import gameserver.model.team2.group.events.PlayerStartMentoringEvent;
import gameserver.network.aion.serverpackets.SM_QUESTION_WINDOW;
import gameserver.restrictions.RestrictionsManager;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.ThreadPoolManager;
import gameserver.utils.TimeUtil;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import javolution.util.FastMap;

/**
 * @author ATracer
 */
public class PlayerGroupService {

	private static final Logger log = LoggerFactory.getLogger(PlayerGroupService.class);

	private static final Map<Integer, PlayerGroup> groups = new ConcurrentHashMap<Integer, PlayerGroup>();
	private static final AtomicBoolean offlineCheckStarted = new AtomicBoolean();
        private static FastMap<Integer, PlayerGroup> groupMembers;

	public static final void inviteToGroup(final Player inviter, final Player invited) {
		if (canInvite(inviter, invited)) {
			PlayerGroupInvite invite = new PlayerGroupInvite(inviter, invited);
			if (invited.getResponseRequester().putRequest(SM_QUESTION_WINDOW.STR_REQUEST_GROUP_INVITE, invite)) {
				PacketSendUtility.sendPacket(invited, new SM_QUESTION_WINDOW(SM_QUESTION_WINDOW.STR_REQUEST_GROUP_INVITE, 0,
					inviter.getName()));
			}
		}
	}
        private PlayerGroupService() {
        groupMembers = new FastMap<Integer, PlayerGroup>();
       // playerGroup = new FastMap<Integer, ScheduledFuture<?>>();
       }

	public static final boolean canInvite(Player inviter, Player invited) {
		return RestrictionsManager.canInviteToGroup(inviter, invited);
	}

	@GlobalCallback(PlayerGroupCreateCallback.class)
	public static final PlayerGroup createGroup(Player leader, Player invited) {
		PlayerGroup newGroup = new PlayerGroup(new PlayerGroupMember(leader));
		groups.put(newGroup.getTeamId(), newGroup);
		addPlayer(newGroup, leader);
		addPlayer(newGroup, invited);
		if (offlineCheckStarted.compareAndSet(false, true)) {
			initializeOfflineCheck();
		}
		return newGroup;
	}

	private static void initializeOfflineCheck() {
		ThreadPoolManager.getInstance().scheduleAtFixedRate(new OfflinePlayerChecker(), 1000, 30 * 1000);
	}

	@GlobalCallback(AddPlayerToGroupCallback.class)
	public static final void addPlayerToGroup(PlayerGroup group, Player invited) {
		group.addMember(new PlayerGroupMember(invited));
	}

	/**
	 * Change group's loot rules and notify team members
	 */
	public static final void changeGroupRules(PlayerGroup group, LootGroupRules lootRules) {
		group.onEvent(new ChangeGroupLootRulesEvent(group, lootRules));
	}

	/**
	 * Player entered world - search for non expired group
	 */
	public static final void onPlayerLogin(Player player) {
		for (PlayerGroup group : groups.values()) {
			PlayerGroupMember member = group.getMember(player.getObjectId());
			if (member != null) {
				group.onEvent(new PlayerConnectedEvent(group, player));
			}
		}
	}

	/**
	 * Player leaved world - set last online on member
	 */
	public static final void onPlayerLogout(Player player) {
		PlayerGroup group = player.getPlayerGroup2();
		if (group != null) {
			PlayerGroupMember member = group.getMember(player.getObjectId());
			member.updateLastOnlineTime();
			group.onEvent(new PlayerDisconnectedEvent(group, player));
		}
	}

	/**
	 * Update group members to some event of player
	 */
	public static final void updateGroup(Player player, GroupEvent groupEvent) {
		PlayerGroup group = player.getPlayerGroup2();
		if (group != null) {
			group.onEvent(new PlayerGroupUpdateEvent(group, player, groupEvent));
		}
	}

	/**
	 * Add player to group
	 */
	public static final void addPlayer(PlayerGroup group, Player player) {
		Preconditions.checkNotNull(group, "Group should not be null");
		group.onEvent(new PlayerEnteredEvent(group, player));
	}

	/**
	 * Remove player from group (normal leave, or kick offline player)
	 */
	public static final void removePlayer(Player player) {
		PlayerGroup group = player.getPlayerGroup2();
		if (group != null) {
			group.onEvent(new PlayerGroupLeavedEvent(group, player));
		}
	}

	/**
	 * Remove player from group (ban)
	 */
	public static final void banPlayer(Player bannedPlayer, Player banGiver) {
		Preconditions.checkNotNull(bannedPlayer, "Banned player should not be null");
		Preconditions.checkNotNull(banGiver, "Bangiver player should not be null");
		PlayerGroup group = banGiver.getPlayerGroup2();
		if (group != null) {
			if (group.hasMember(bannedPlayer.getObjectId())) {
				group.onEvent(new PlayerGroupLeavedEvent(group, bannedPlayer, LeaveReson.BAN, banGiver.getName()));
			}
			else {
				log.warn("TEAM2: banning player not in group {}", group.onlineMembers());
			}
		}
	}

	/**
	 * Disband group by removing all players one by one
	 */
	@GlobalCallback(PlayerGroupDisbandCallback.class)
	public static void disband(PlayerGroup group) {
		Preconditions.checkState(group.onlineMembers() <= 1, "Can't disband group with more than one online member");
		groups.remove(group.getTeamId());
		group.onEvent(new GroupDisbandEvent(group));
	}

	/**
	 * Share specific amount of kinah between group members
	 */
	public static void distributeKinah(Player player, long kinah) {
		PlayerGroup group = player.getPlayerGroup2();
		if (group != null) {
			group.onEvent(new TeamKinahDistributionEvent<PlayerGroup>(group, player, kinah));
		}
	}

	/**
	 * Show specific mark on top of player
	 */
	public static void showBrand(Player player, int targetObjId, int brandId) {
		PlayerGroup group = player.getPlayerGroup2();
		if (group != null) {
			group.onEvent(new ShowBrandEvent<PlayerGroup>(group, targetObjId, brandId));
		}
	}

	public static void changeLeader(Player player) {
		PlayerGroup group = player.getPlayerGroup2();
		if (group != null) {
			group.onEvent(new ChangeGroupLeaderEvent(group, player));
		}
	}

	/**
	 * Start mentoring in group
	 */
	public static void startMentoring(Player player) {
		PlayerGroup group = player.getPlayerGroup2();
		if (group != null) {
			group.onEvent(new PlayerStartMentoringEvent(group, player));
		}
	}

	/**
	 * Stop mentoring in group
	 */
	public static void stopMentoring(Player player) {
		PlayerGroup group = player.getPlayerGroup2();
		if (group != null) {
			group.onEvent(new PlayerGroupStopMentoringEvent(group, player));
		}
	}

	public static final void cleanup() {
		log.info(getServiceStatus());
		groups.clear();
	}

	public static final String getServiceStatus() {
		return "Number of groups: " + groups.size();
	}

	public static final PlayerGroup searchGroup(Integer playerObjId) {
		for (PlayerGroup group : groups.values()) {
			if (group.hasMember(playerObjId)) {
				return group;
			}
		}
		return null;
	}

	public static class OfflinePlayerChecker implements Runnable, Predicate<PlayerGroupMember> {

		private PlayerGroup currentGroup;

		@Override
		public void run() {
			for (PlayerGroup group : groups.values()) {
				currentGroup = group;
				group.apply(this);
			}
			currentGroup = null;
		}

		@Override
		public boolean apply(PlayerGroupMember member) {
			if (!member.isOnline() && TimeUtil.isExpired(member.getLastOnlineTime() + GroupConfig.GROUP_REMOVE_TIME * 1000)) {
				// TODO LEAVE_TIMEOUT type
				currentGroup.onEvent(new PlayerGroupLeavedEvent(currentGroup, member.getObject()));
			}
			return true;
		}

	}
        public static void addGroupMemberToCache(Player player) {
                  if (!groupMembers.containsKey(player.getObjectId())) {
            groupMembers.put(player.getObjectId(), player.getPlayerGroup2());
            }
         }

       private void removeGroupMemberFromCache(int playerObjId) {
        if (groupMembers.containsKey(playerObjId)) {
            groupMembers.remove(playerObjId);
          }
       }

}