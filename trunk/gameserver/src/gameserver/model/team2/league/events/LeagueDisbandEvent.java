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

import gameserver.model.team2.alliance.PlayerAlliance;
import gameserver.model.team2.common.events.AlwaysTrueTeamEvent;
import gameserver.model.team2.league.League;
import gameserver.model.team2.league.events.LeagueLeftEvent.LeaveReson;
import com.google.common.base.Predicate;

/**
 * @author ATracer
 */
public class LeagueDisbandEvent extends AlwaysTrueTeamEvent implements Predicate<PlayerAlliance> {

	private final League league;

	public LeagueDisbandEvent(League league) {
		this.league = league;
	}

	@Override
	public void handleEvent() {
		league.applyOnMembers(this);
	}

	@Override
	public boolean apply(PlayerAlliance alliance) {
		league.onEvent(new LeagueLeftEvent(league, alliance, LeaveReson.DISBAND));
		return true;
	}

}
