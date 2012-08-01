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
package gameserver.network.loginserver.clientpackets;

import gameserver.network.loginserver.LoginServer;
import gameserver.network.loginserver.LsClientPacket;

/**
 * This packet is request kicking player.
 * 
 * @author -Nemesiss-
 */
public class CM_REQUEST_KICK_ACCOUNT extends LsClientPacket {

	public CM_REQUEST_KICK_ACCOUNT(int opCode) {
		super(opCode);
	}

	/**
	 * account id of account that login server request to kick.
	 */
	private int accountId;

	@Override
	public void readImpl() {
		accountId = readD();
	}

	@Override
	public void runImpl() {
		LoginServer.getInstance().kickAccount(accountId);
	}
}
