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
import gameserver.model.team2.common.events.PlayerStopMentoringEvent;
import gameserver.model.team2.common.legacy.GroupEvent;
import gameserver.model.team2.group.PlayerGroup;
import gameserver.network.aion.serverpackets.SM_GROUP_MEMBER_INFO;
import gameserver.utils.PacketSendUtility;

/**
 * @author ATracer
 */
public class PlayerGroupStopMentoringEvent extends PlayerStopMentoringEvent<PlayerGroup> {

	/**
	 * @param group
	 * @param player
	 */
	public PlayerGroupStopMentoringEvent(PlayerGroup group, Player player) {
		super(group, player);
	}

	@Override
	protected void sendGroupPacketOnMentorEnd(Player member) {
		PacketSendUtility.sendPacket(member, new SM_GROUP_MEMBER_INFO(team, player, GroupEvent.MOVEMENT));
	}

}
