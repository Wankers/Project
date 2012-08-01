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

import gameserver.model.gameobjects.player.Player;
import gameserver.model.team2.alliance.PlayerAlliance;
import gameserver.model.team2.alliance.PlayerAllianceMember;
import gameserver.model.team2.common.events.AlwaysTrueTeamEvent;
import gameserver.model.team2.common.legacy.PlayerAllianceEvent;
import gameserver.network.aion.serverpackets.SM_ALLIANCE_MEMBER_INFO;
import gameserver.utils.PacketSendUtility;
import com.google.common.base.Predicate;

/**
 * @author ATracer
 */
public class PlayerAllianceUpdateEvent extends AlwaysTrueTeamEvent implements Predicate<PlayerAllianceMember> {

	private final PlayerAlliance alliance;
	private final Player player;
	private final PlayerAllianceEvent allianceEvent;
	private final PlayerAllianceMember updateMember;

	public PlayerAllianceUpdateEvent(PlayerAlliance alliance, Player player, PlayerAllianceEvent allianceEvent) {
		this.alliance = alliance;
		this.player = player;
		this.allianceEvent = allianceEvent;
		this.updateMember = alliance.getMember(player.getObjectId());
	}

	@Override
	public void handleEvent() {
		switch (allianceEvent) {
			case MOVEMENT:
			case UPDATE:
				alliance.apply(this);
				break;
			default:
				// Unsupported
				break;
		}

	}

	@Override
	public boolean apply(PlayerAllianceMember member) {
		if (!member.getObjectId().equals(player.getObjectId())) {
			PacketSendUtility.sendPacket(member.getObject(), new SM_ALLIANCE_MEMBER_INFO(updateMember, allianceEvent));
		}
		return true;
	}

}
