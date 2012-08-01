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
package gameserver.network.loginserver.serverpackets;

import java.util.List;

import commons.network.IPRange;
import gameserver.configs.network.IPConfig;
import gameserver.configs.network.NetworkConfig;
import gameserver.network.loginserver.LoginServerConnection;
import gameserver.network.loginserver.LsServerPacket;

/**
 * This is authentication packet that gs will send to login server for registration.
 * 
 * @author -Nemesiss-
 */
public class SM_GS_AUTH extends LsServerPacket {
	public SM_GS_AUTH() {
		super(0x00);
	}
	
	@Override
	protected void writeImpl(LoginServerConnection con) {
		writeC(NetworkConfig.GAMESERVER_ID);
		writeC(IPConfig.getDefaultAddress().length);
		writeB(IPConfig.getDefaultAddress());

		List<IPRange> ranges = IPConfig.getRanges();
		int size = ranges.size();
		writeD(size);
		for (int i = 0; i < size; i++) {
			IPRange ipRange = ranges.get(i);
			byte[] min = ipRange.getMinAsByteArray();
			byte[] max = ipRange.getMaxAsByteArray();
			writeC(min.length);
			writeB(min);
			writeC(max.length);
			writeB(max);
			writeC(ipRange.getAddress().length);
			writeB(ipRange.getAddress());
		}

		writeH(NetworkConfig.GAME_PORT);
		writeD(NetworkConfig.MAX_ONLINE_PLAYERS);
		writeS(NetworkConfig.LOGIN_PASSWORD);
	}
}
