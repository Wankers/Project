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


import gameserver.model.gameobjects.player.BlockList;
import gameserver.model.gameobjects.player.BlockedPlayer;
import gameserver.network.aion.AionConnection;
import gameserver.network.aion.AionServerPacket;

/**
 * Packet responsible for telling a player his block list
 * 
 * @author Ben
 */
public class SM_BLOCK_LIST extends AionServerPacket {

	@Override
	protected void writeImpl(AionConnection con) {
		BlockList list = con.getActivePlayer().getBlockList();
		writeH(list.getSize());
		writeC(0); // Unk
		for (BlockedPlayer player : list) {
			writeS(player.getName());
			writeS(player.getReason());
		}
	}
}
