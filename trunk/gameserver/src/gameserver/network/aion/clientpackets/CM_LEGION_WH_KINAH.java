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
package gameserver.network.aion.clientpackets;

import gameserver.model.gameobjects.player.Player;
import gameserver.model.items.storage.StorageType;
import gameserver.model.team.legion.Legion;
import gameserver.network.aion.AionClientPacket;
import gameserver.network.aion.AionConnection.State;
import gameserver.services.LegionService;

/**
 * @author ATracer
 */
public class CM_LEGION_WH_KINAH extends AionClientPacket {

	public CM_LEGION_WH_KINAH(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	private long legionKinah;
	private int operation;

	@Override
	protected void readImpl() {
		this.legionKinah = readQ();
		this.operation = readC();
	}

	@Override
	protected void runImpl() {
		final Player activePlayer = getConnection().getActivePlayer();

		Legion legion = activePlayer.getLegion();
		if (legion != null) {
			switch (operation) {
				case 0:
					LegionService.getInstance().getLegionKinah(activePlayer, legionKinah);
					break;
				case 1:
					LegionService.getInstance().addLegionKinah(activePlayer, legionKinah);
					break;
			}
		}
	}

}
