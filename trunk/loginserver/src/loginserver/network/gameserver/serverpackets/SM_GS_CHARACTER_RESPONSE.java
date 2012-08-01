/*
 * This file is part of Aion Extreme  Emulator <aion-core.net>.
 *
 *  This is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This software is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser Public License
 *  along with this software.  If not, see <http://www.gnu.org/licenses/>.
 */
package loginserver.network.gameserver.serverpackets;

import loginserver.network.gameserver.GsConnection;
import loginserver.network.gameserver.GsServerPacket;


/**
 * @author cura
 */
public class SM_GS_CHARACTER_RESPONSE extends GsServerPacket {

	private final int accountId;

	/**
	 * @param accountId
	 */
	public SM_GS_CHARACTER_RESPONSE(int accountId) {
		this.accountId = accountId;
	}

	@Override
	protected void writeImpl(GsConnection con) {
		writeC(8);
		writeD(accountId);
	}
}
