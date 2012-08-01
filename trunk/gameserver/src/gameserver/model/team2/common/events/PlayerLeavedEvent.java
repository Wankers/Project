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

import org.apache.commons.lang.StringUtils;

import gameserver.model.gameobjects.player.Player;
import gameserver.model.team2.TeamEvent;
import gameserver.model.team2.TeamMember;
import gameserver.model.team2.TemporaryPlayerTeam;
import com.google.common.base.Predicate;

/**
 * @author ATracer
 */
public abstract class PlayerLeavedEvent<TM extends TeamMember<Player>, T extends TemporaryPlayerTeam<TM>> implements
	Predicate<TM>, TeamEvent {

	public static enum LeaveReson {
		BAN,
		LEAVE,
		LEAVE_TIMEOUT,
		DISBAND;
	}

	protected final T team;
	protected final Player leavedPlayer;
	protected final LeaveReson reason;
	protected final TM leavedTeamMember;
	protected final String banPersonName;

	public PlayerLeavedEvent(T alliance, Player player) {
		this(alliance, player, LeaveReson.LEAVE);
	}

	public PlayerLeavedEvent(T alliance, Player player, LeaveReson reason) {
		this(alliance, player, reason, StringUtils.EMPTY);
	}

	public PlayerLeavedEvent(T team, Player player, LeaveReson reason, String banPersonName) {
		this.team = team;
		this.leavedPlayer = player;
		this.reason = reason;
		this.leavedTeamMember = team.getMember(player.getObjectId());
		this.banPersonName = banPersonName;
	}

	/**
	 * Player should be in team to broadcast this event
	 */
	@Override
	public boolean checkCondition() {
		return team.hasMember(leavedPlayer.getObjectId());
	}

}
