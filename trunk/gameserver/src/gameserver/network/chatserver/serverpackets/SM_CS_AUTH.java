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
package gameserver.network.chatserver.serverpackets;

import gameserver.configs.network.IPConfig;
import gameserver.configs.network.NetworkConfig;
import gameserver.network.chatserver.ChatServerConnection;
import gameserver.network.chatserver.CsServerPacket;

/**
 * @author ATracer
 */
public class SM_CS_AUTH extends CsServerPacket {

	public SM_CS_AUTH() {
		super(0x00);
	}

	@Override
	protected void writeImpl(ChatServerConnection con) {
		writeC(NetworkConfig.GAMESERVER_ID);
		writeC(IPConfig.getDefaultAddress().length);
		writeB(IPConfig.getDefaultAddress());
		writeS(NetworkConfig.CHAT_PASSWORD);
	}
}