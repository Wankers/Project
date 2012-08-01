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
package gameserver.model.team2.alliance.events;

import gameserver.dataholders.DataManager;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.team2.alliance.PlayerAlliance;
import gameserver.model.team2.alliance.PlayerAllianceMember;
import gameserver.model.team2.alliance.PlayerAllianceService;
import gameserver.model.team2.common.events.PlayerLeavedEvent;
import gameserver.model.team2.common.legacy.PlayerAllianceEvent;
import gameserver.model.templates.portal.PortalTemplate;
import gameserver.network.aion.serverpackets.SM_ALLIANCE_INFO;
import gameserver.network.aion.serverpackets.SM_ALLIANCE_MEMBER_INFO;
import gameserver.network.aion.serverpackets.SM_LEAVE_GROUP_MEMBER;
import gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import gameserver.services.instance.InstanceService;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.ThreadPoolManager;

/**
 * @author ATracer
 */
public class PlayerAllianceLeavedEvent extends PlayerLeavedEvent<PlayerAllianceMember, PlayerAlliance> {

	public PlayerAllianceLeavedEvent(PlayerAlliance alliance, Player player) {
		super(alliance, player);
	}

	public PlayerAllianceLeavedEvent(PlayerAlliance team, Player player, PlayerLeavedEvent.LeaveReson reason,
		String banPersonName) {
		super(team, player, reason, banPersonName);
	}

	public PlayerAllianceLeavedEvent(PlayerAlliance alliance, Player player, PlayerLeavedEvent.LeaveReson reason) {
		super(alliance, player, reason);
	}

	@Override
	public void handleEvent() {
		team.removeMember(leavedPlayer.getObjectId());
		team.getViceCaptainIds().remove(leavedPlayer.getObjectId());

		if (leavedPlayer.isOnline()) {
			PacketSendUtility.sendPacket(leavedPlayer, new SM_LEAVE_GROUP_MEMBER());
		}

		team.apply(this);

		switch (reason) {
			case BAN:
			case LEAVE:
			case LEAVE_TIMEOUT:
				if (team.onlineMembers() <= 1) {
					PlayerAllianceService.disband(team);
				}
				else {
					if (leavedPlayer.equals(team.getLeader().getObject())) {
						team.onEvent(new ChangeAllianceLeaderEvent(team));
					}
				}
				if (reason == LeaveReson.BAN) {
					PacketSendUtility.sendPacket(leavedPlayer, SM_SYSTEM_MESSAGE.STR_FORCE_BAN_ME(banPersonName));
				}

				break;
			case DISBAND:
				PacketSendUtility.sendPacket(leavedPlayer, SM_SYSTEM_MESSAGE.STR_PARTY_ALLIANCE_DISPERSED);
				break;
		}

		if (leavedPlayer.isInInstance()) {
			ThreadPoolManager.getInstance().schedule(new Runnable() {

				@Override
				public void run() {
					if (!leavedPlayer.isInAlliance2()) {
						PortalTemplate portalTemplate = DataManager.PORTAL_DATA.getInstancePortalTemplate(
							leavedPlayer.getWorldId(), leavedPlayer.getRace());
						if (portalTemplate != null && portalTemplate.getPlayerSize() > 6) {
							InstanceService.moveToEntryPoint(leavedPlayer, portalTemplate, true);
						}
					}
				}

			}, 10000);
		}
	}

	@Override
	public boolean apply(PlayerAllianceMember member) {
		Player player = member.getObject();

		PacketSendUtility.sendPacket(player, new SM_ALLIANCE_MEMBER_INFO(leavedTeamMember, PlayerAllianceEvent.LEAVE));
		PacketSendUtility.sendPacket(player, new SM_ALLIANCE_INFO(team));

		switch (reason) {
			case LEAVE_TIMEOUT:
				PacketSendUtility.sendPacket(player,
					SM_SYSTEM_MESSAGE.STR_PARTY_ALLIANCE_HE_LEAVED_PARTY(leavedPlayer.getName()));
				break;
			case LEAVE:
				PacketSendUtility.sendPacket(player,
					SM_SYSTEM_MESSAGE.STR_PARTY_ALLIANCE_HE_LEAVED_PARTY(leavedPlayer.getName()));
				break;
			case DISBAND:
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_PARTY_ALLIANCE_DISPERSED);
				break;
			case BAN:
				PacketSendUtility
					.sendPacket(player, SM_SYSTEM_MESSAGE.STR_FORCE_BAN_HIM(banPersonName, leavedPlayer.getName()));
				break;
		}

		return true;
	}

}
