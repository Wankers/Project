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
package gameserver.network.aion.serverpackets;


import gameserver.network.aion.AionConnection;
import gameserver.network.aion.AionServerPacket;

/**
 * This packet is used to update current hp and max hp values.
 * 
 * @author Luno
 */
public class SM_STATUPDATE_HP extends AionServerPacket {

	private int currentHp;
	private int maxHp;

	/**
	 * @param currentHp
	 * @param maxHp
	 */
	public SM_STATUPDATE_HP(int currentHp, int maxHp) {
		this.currentHp = currentHp;
		this.maxHp = maxHp;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeImpl(AionConnection con) {
		writeD(currentHp);
		writeD(maxHp);
	}

}
