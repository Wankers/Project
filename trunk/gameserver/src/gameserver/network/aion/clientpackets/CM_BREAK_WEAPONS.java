/**
 * This file is part of aion-unique <aion-unique.smfnew.com>.
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
import gameserver.services.ArmsfusionService;

/**
 * @author zdead
 */
public class CM_BREAK_WEAPONS extends AionClientPacket {

	public CM_BREAK_WEAPONS(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	private int weaponToBreakUniqueId;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void readImpl() {
		readD();
		weaponToBreakUniqueId = readD();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl() {
		ArmsfusionService.breakWeapons(getConnection().getActivePlayer(), weaponToBreakUniqueId);
	}
}
