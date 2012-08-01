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
import gameserver.model.team2.common.events.PlayerLeavedEvent.LeaveReson;
import gameserver.model.team2.group.PlayerGroup;
import com.google.common.base.Predicate;

/**
 * @author ATracer
 */
public class GroupDisbandEvent extends AlwaysTrueTeamEvent implements Predicate<Player> {

	private final PlayerGroup group;

	/**
	 * @param group
	 */
	public GroupDisbandEvent(PlayerGroup group) {
		this.group = group;
	}

	@Override
	public void handleEvent() {
		group.applyOnMembers(this);
	}

	@Override
	public boolean apply(Player player) {
		group.onEvent(new PlayerGroupLeavedEvent(group, player, LeaveReson.DISBAND));
		return true;
	}

}
