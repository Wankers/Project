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
import gameserver.model.team2.common.events.AlwaysTrueTeamEvent;
import gameserver.model.team2.common.legacy.GroupEvent;
import gameserver.model.team2.group.PlayerFilters.MentorSuiteFilter;
import gameserver.model.team2.group.PlayerGroup;
import gameserver.network.aion.serverpackets.SM_ABYSS_RANK_UPDATE;
import gameserver.network.aion.serverpackets.SM_GROUP_MEMBER_INFO;
import gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.audit.AuditLogger;
import com.google.common.base.Predicate;

/**
 * @author ATracer
 */
public class PlayerStartMentoringEvent extends AlwaysTrueTeamEvent implements Predicate<Player> {

	private final PlayerGroup group;
	private final Player player;

	public PlayerStartMentoringEvent(PlayerGroup group, Player player) {
		this.group = group;
		this.player = player;
	}

	@Override
	public void handleEvent() {
		if (group.filterMembers(new MentorSuiteFilter(player)).size() == 0) {
			AuditLogger.info(player, "Send fake start mentoring packet");
			return;
		}
		player.setMentor(true);
		PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_MENTOR_START);
		group.applyOnMembers(this);
		PacketSendUtility.broadcastPacketAndReceive(player, new SM_ABYSS_RANK_UPDATE(2, player));
	}

	@Override
	public boolean apply(Player member) {
		if (!player.equals(member)) {
			PacketSendUtility.sendPacket(member, SM_SYSTEM_MESSAGE.STR_MSG_MENTOR_START_PARTYMSG(player.getName()));
		}
		PacketSendUtility.sendPacket(member, new SM_GROUP_MEMBER_INFO(group, player, GroupEvent.MOVEMENT));
		return true;
	}
}
