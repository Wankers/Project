/**
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

import java.util.Map;

import gameserver.model.gameobjects.player.Player;
import gameserver.network.aion.AionConnection;
import gameserver.network.aion.AionServerPacket;

/**
 * Packet with macro list.
 * 
 * @author -Nemesiss-
 */
public class SM_MACRO_LIST extends AionServerPacket {

	private Player player;
	private boolean secondPart;

	/**
	 * Constructs new <tt>SM_MACRO_LIST </tt> packet
	 */
	public SM_MACRO_LIST(Player player, boolean secondPart) {
		this.player = player;
		this.secondPart = secondPart;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeImpl(AionConnection con) {
		writeD(player.getObjectId());// player id
		
		Map<Integer, String> macrosToSend = player.getMacroList().getMarcosPart(secondPart);
		int size = macrosToSend.size();
		
		if(secondPart) {
			writeC(0x00);
			size *= -1;
		}
		else {
			writeC(0x01);
		}
		
		writeH(size);
		
		if (size != 0) {
			for (Map.Entry<Integer, String> entry : macrosToSend.entrySet()) {
				writeC(entry.getKey());// order
				writeS(entry.getValue());// xml
			}
		}
	}
}
