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
package gameserver.network.aion.serverpackets;


import gameserver.network.aion.AionConnection;
import gameserver.network.aion.AionServerPacket;

/**
 * @author alexa026
 */
public class SM_LOOT_STATUS extends AionServerPacket {

	private int targetObjectId;
	private int state;

	public SM_LOOT_STATUS(int targetObjectId, int state) {
		this.targetObjectId = targetObjectId;
		this.state = state;
	}

	/**
	 * {@inheritDoc} dc
	 */

	@Override
	protected void writeImpl(AionConnection con) {
		writeD(targetObjectId);
		writeC(state);
	}
}
