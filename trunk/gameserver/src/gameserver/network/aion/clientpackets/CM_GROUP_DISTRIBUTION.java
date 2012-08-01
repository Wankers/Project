/*
 * This file is part of aion-unique <aion-unique.com>.
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
package gameserver.network.aion.clientpackets;

import gameserver.model.gameobjects.player.Player;
import gameserver.model.team2.alliance.PlayerAllianceService;
import gameserver.model.team2.group.PlayerGroupService;
import gameserver.network.aion.AionClientPacket;
import gameserver.network.aion.AionConnection.State;
import gameserver.restrictions.RestrictionsManager;

/**
 * @author Lyahim, Simple, xTz
 */
public class CM_GROUP_DISTRIBUTION extends AionClientPacket {

	private long amount;
	private int partyType;

	public CM_GROUP_DISTRIBUTION(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	@Override
	protected void readImpl() {
		amount = readQ();
		partyType = readC();
	}

	@Override
	protected void runImpl() {
		if (amount < 2)
			return;

		Player player = getConnection().getActivePlayer();

		if (!RestrictionsManager.canTrade(player))
			return;

		switch (partyType) {
			case 1:
				if (player.isInAlliance2()) {
					PlayerAllianceService.distributeKinahInGroup(player, amount);
				}
				else {
					PlayerGroupService.distributeKinah(player, amount);
				}
				break;
			case 2:
				PlayerAllianceService.distributeKinah(player, amount);
				break;
		}
	}
}
