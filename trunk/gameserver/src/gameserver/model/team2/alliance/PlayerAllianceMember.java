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
package gameserver.model.team2.alliance;

import gameserver.model.gameobjects.player.Player;
import gameserver.model.team2.PlayerTeamMember;
import gameserver.model.gameobjects.player.PlayerCommonData;

/**
 * @author ATracer
 */
public class PlayerAllianceMember extends PlayerTeamMember {

	private Player player;
	private int allianceId;

	public PlayerAllianceMember(Player player) {
		super(player);
	}

	public int getAllianceId() {
		return allianceId;
	}

	public void setAllianceId(int allianceId) {
		this.allianceId = allianceId;
	}

	public final PlayerAllianceGroup getPlayerAllianceGroup() {
		return getObject().getPlayerAllianceGroup2();
	}

	public final void setPlayerAllianceGroup(PlayerAllianceGroup playerAllianceGroup) {
		getObject().setPlayerAllianceGroup2(playerAllianceGroup);
	}

	public Player getPlayer()
	{
		return player;
	}
}
