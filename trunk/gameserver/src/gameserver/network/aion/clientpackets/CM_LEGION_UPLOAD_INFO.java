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
import gameserver.model.team.legion.LegionEmblemType;
import gameserver.network.aion.AionClientPacket;
import gameserver.network.aion.AionConnection.State;
import gameserver.services.LegionService;

/**
 * @author Simple modified cura
 */
public class CM_LEGION_UPLOAD_INFO extends AionClientPacket {

	/** Emblem related information **/
	private int totalSize;
	private int red;
	private int green;
	private int blue;

	/**
	 * @param opcode
	 */
	public CM_LEGION_UPLOAD_INFO(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	@Override
	protected void readImpl() {
		totalSize = readD();
		readC(); // 0xFF
		red = readC();
		green = readC();
		blue = readC();
	}

	@Override
	protected void runImpl() {
		final Player activePlayer = getConnection().getActivePlayer();

		LegionService.getInstance().uploadEmblemInfo(activePlayer, totalSize, red, green, blue, LegionEmblemType.CUSTOM);
	}
}
