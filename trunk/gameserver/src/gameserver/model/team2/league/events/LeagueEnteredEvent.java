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
package gameserver.model.team2.league.events;

import gameserver.model.team2.TeamEvent;
import gameserver.model.team2.alliance.PlayerAlliance;
import gameserver.model.team2.league.League;
import gameserver.model.team2.league.LeagueMember;
import gameserver.model.team2.league.LeagueService;
import gameserver.network.aion.serverpackets.SM_ALLIANCE_INFO;
import gameserver.network.aion.serverpackets.SM_SHOW_BRAND;
import com.google.common.base.Predicate;

/**
 * @author ATracer
 */
public class LeagueEnteredEvent implements Predicate<LeagueMember>, TeamEvent {

	private final League league;
	private final PlayerAlliance invitedAlliance;

	public LeagueEnteredEvent(League league, PlayerAlliance alliance) {
		this.league = league;
		this.invitedAlliance = alliance;
	}

	/**
	 * Entered alliance should not be in league yet
	 */
	@Override
	public boolean checkCondition() {
		return !league.hasMember(invitedAlliance.getObjectId());
	}

	@Override
	public void handleEvent() {
		LeagueService.addAllianceToLeague(league, invitedAlliance);
		league.apply(this);
	}

	@Override
	public boolean apply(LeagueMember member) {
		PlayerAlliance alliance = member.getObject();
		alliance.sendPacket(new SM_ALLIANCE_INFO(alliance, SM_ALLIANCE_INFO.LEAGUE_ENTERED, league.getLeaderObject()
			.getLeader().getName()));
		alliance.sendPacket(new SM_SHOW_BRAND(0, 0));
		return true;
	}

}
