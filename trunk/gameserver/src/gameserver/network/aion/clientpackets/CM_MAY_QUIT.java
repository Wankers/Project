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

import gameserver.network.aion.AionClientPacket;
import gameserver.network.aion.AionConnection.State;

/**
 * @author xavier Packet sent by client when player may quit game in 10 seconds
 */
public class CM_MAY_QUIT extends AionClientPacket {

	/**
	 * @param opcode
	 */
	public CM_MAY_QUIT(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	/*
	 * (non-Javadoc)
	 * @see commons.network.packet.BaseClientPacket#readImpl()
	 */
	@Override
	protected void readImpl() {
		// empty
	}

	/*
	 * (non-Javadoc)
	 * @see commons.network.packet.BaseClientPacket#runImpl()
	 */
	@Override
	protected void runImpl() {
		// Nothing to do
	}

}
