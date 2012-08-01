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
import gameserver.network.aion.serverpackets.SM_SHOW_BRAND;
import gameserver.utils.PacketSendUtility;
import com.google.common.base.Predicate;

/**
 * @author ATracer
 */
public class ShowBrandEvent<T extends TemporaryPlayerTeam<? extends TeamMember<Player>>> extends AlwaysTrueTeamEvent
	implements Predicate<Player> {

	private final T team;
	private final int targetObjId;
	private final int brandId;

	public ShowBrandEvent(T team, int targetObjId, int brandId) {
		this.team = team;
		this.targetObjId = targetObjId;
		this.brandId = brandId;
	}

	@Override
	public void handleEvent() {
		team.applyOnMembers(this);
	}

	@Override
	public boolean apply(Player member) {
		PacketSendUtility.sendPacket(member, new SM_SHOW_BRAND(brandId, targetObjId));
		return true;
	}

}
