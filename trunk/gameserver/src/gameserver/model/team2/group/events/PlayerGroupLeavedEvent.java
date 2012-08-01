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

import gameserver.dataholders.DataManager;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.team2.common.events.PlayerLeavedEvent;
import gameserver.model.team2.common.legacy.GroupEvent;
import gameserver.model.team2.group.PlayerGroup;
import gameserver.model.team2.group.PlayerGroupMember;
import gameserver.model.team2.group.PlayerGroupService;
import gameserver.model.templates.portal.PortalTemplate;
import gameserver.network.aion.serverpackets.SM_GROUP_MEMBER_INFO;
import gameserver.network.aion.serverpackets.SM_LEAVE_GROUP_MEMBER;
import gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import gameserver.services.instance.InstanceService;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.ThreadPoolManager;

/**
 * @author ATracer
 */
public class PlayerGroupLeavedEvent extends PlayerLeavedEvent<PlayerGroupMember, PlayerGroup> {

	public PlayerGroupLeavedEvent(PlayerGroup alliance, Player player) {
		super(alliance, player);
	}

	public PlayerGroupLeavedEvent(PlayerGroup team, Player player, PlayerLeavedEvent.LeaveReson reason,
		String banPersonName) {
		super(team, player, reason, banPersonName);
	}

	public PlayerGroupLeavedEvent(PlayerGroup alliance, Player player, PlayerLeavedEvent.LeaveReson reason) {
		super(alliance, player, reason);
	}

	@Override
	public void handleEvent() {
		team.removeMember(leavedPlayer.getObjectId());

		if (leavedPlayer.isMentor()) {
			team.onEvent(new PlayerGroupStopMentoringEvent(team, leavedPlayer));
		}

		team.apply(this);

		PacketSendUtility.sendPacket(leavedPlayer, new SM_LEAVE_GROUP_MEMBER());
		switch (reason) {
			case BAN:
			case LEAVE:
				// PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_PARTY_SECEDE); // client side?
				if (team.onlineMembers() <= 1) {
					PlayerGroupService.disband(team);
				}
				else {
					if (leavedPlayer.equals(team.getLeader().getObject())) {
						team.onEvent(new ChangeGroupLeaderEvent(team));
					}
				}
				if (reason == LeaveReson.BAN) {
					PacketSendUtility.sendPacket(leavedPlayer, SM_SYSTEM_MESSAGE.STR_PARTY_YOU_ARE_BANISHED);
				}
				break;
			case DISBAND:
				PacketSendUtility.sendPacket(leavedPlayer, SM_SYSTEM_MESSAGE.STR_PARTY_IS_DISPERSED);
				break;
		}

		if (leavedPlayer.isInInstance()) {
			ThreadPoolManager.getInstance().schedule(new Runnable() {

				@Override
				public void run() {
					if (!leavedPlayer.isInGroup2()) {
						PortalTemplate portalTemplate = DataManager.PORTAL_DATA.getInstancePortalTemplate(
							leavedPlayer.getWorldId(), leavedPlayer.getRace());
						if (portalTemplate != null && portalTemplate.getPlayerSize() == 6)
							InstanceService.moveToEntryPoint(leavedPlayer, portalTemplate, true);
					}
				}
			}, 10000);
		}
	}

	@Override
	public boolean apply(PlayerGroupMember member) {
		Player player = member.getObject();
		PacketSendUtility.sendPacket(player, new SM_GROUP_MEMBER_INFO(team, leavedPlayer, GroupEvent.LEAVE));

		switch (reason) {
			case LEAVE:
			case DISBAND:
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_PARTY_HE_LEAVE_PARTY(leavedPlayer.getName()));
				break;
			case BAN:
				// TODO find out empty strings (Retail has +2 empty strings
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_PARTY_HE_IS_BANISHED(leavedPlayer.getName()));
				break;
		}

		return true;
	}

}
