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
package gameserver.model.team2.group.events;

import gameserver.model.gameobjects.player.Player;
import gameserver.model.team2.TeamEvent;
import gameserver.model.team2.common.legacy.GroupEvent;
import gameserver.model.team2.group.PlayerGroup;
import gameserver.model.team2.group.PlayerGroupService;
import gameserver.network.aion.serverpackets.SM_GROUP_MEMBER_INFO;
import gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import gameserver.utils.PacketSendUtility;
import com.google.common.base.Predicate;

/**
 * @author ATracer
 */
public class PlayerDisconnectedEvent implements Predicate<Player>, TeamEvent {

	private final PlayerGroup group;
	private final Player player;

	public PlayerDisconnectedEvent(PlayerGroup group, Player player) {
		this.group = group;
		this.player = player;
	}

	/**
	 * Player should be in group before disconnection
	 */
	@Override
	public boolean checkCondition() {
		return group.hasMember(player.getObjectId());
	}

	@Override
	public void handleEvent() {
		if (group.onlineMembers() <= 1) {
			PlayerGroupService.disband(group);
		}
		else {
			if (player.equals(group.getLeader().getObject())) {
				group.onEvent(new ChangeGroupLeaderEvent(group));
			}
			group.applyOnMembers(this);
		}
	}

	@Override
	public boolean apply(Player member) {
		if (!member.equals(player)) {
			PacketSendUtility.sendPacket(member, SM_SYSTEM_MESSAGE.STR_PARTY_HE_BECOME_OFFLINE(player.getName()));
			PacketSendUtility.sendPacket(member, new SM_GROUP_MEMBER_INFO(group, player, GroupEvent.DISCONNECTED));
			// disconnect other group members on logout? check
			PacketSendUtility.sendPacket(player, new SM_GROUP_MEMBER_INFO(group, member, GroupEvent.DISCONNECTED));
		}
		return true;
	}

}
