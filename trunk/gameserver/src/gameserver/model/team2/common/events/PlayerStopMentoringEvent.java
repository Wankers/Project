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
package gameserver.model.team2.common.events;

import gameserver.model.gameobjects.player.Player;
import gameserver.model.team2.TeamMember;
import gameserver.model.team2.TemporaryPlayerTeam;
import gameserver.network.aion.serverpackets.SM_ABYSS_RANK_UPDATE;
import gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import gameserver.utils.PacketSendUtility;
import com.google.common.base.Predicate;

/**
 * @author ATracer
 */
public abstract class PlayerStopMentoringEvent<T extends TemporaryPlayerTeam<? extends TeamMember<Player>>> extends
	AlwaysTrueTeamEvent implements Predicate<Player> {

	protected final T team;
	protected final Player player;

	public PlayerStopMentoringEvent(T team, Player player) {
		this.team = team;
		this.player = player;
	}

	@Override
	public void handleEvent() {
		player.setMentor(false);
		PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_MENTOR_END);
		team.applyOnMembers(this);
		PacketSendUtility.broadcastPacketAndReceive(player, new SM_ABYSS_RANK_UPDATE(2, player));
	}

	@Override
	public boolean apply(Player member) {
		if (!player.equals(member)) {
			PacketSendUtility.sendPacket(member, SM_SYSTEM_MESSAGE.STR_MSG_MENTOR_END_PARTYMSG(player.getName()));
		}
		sendGroupPacketOnMentorEnd(member);
		return true;
	}

	/**
	 * @param member
	 */
	protected abstract void sendGroupPacketOnMentorEnd(Player member);
}
