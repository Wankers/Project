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
package gameserver.network.loginserver.clientpackets;

import commons.database.dao.DAOManager;
import gameserver.dao.PlayerDAO;
import gameserver.network.loginserver.LsClientPacket;
import gameserver.network.loginserver.serverpackets.SM_GS_CHARACTER;

/**
 * @author cura
 */
public class CM_GS_CHARACTER_RESPONSE extends LsClientPacket {

	public CM_GS_CHARACTER_RESPONSE(int opCode) {
		super(opCode);
	}

	private int accountId;

	@Override
	public void readImpl() {
		accountId = readD();
	}

	@Override
	public void runImpl() {
		int characterCount = DAOManager.getDAO(PlayerDAO.class).getCharacterCountOnAccount(accountId);
		sendPacket(new SM_GS_CHARACTER(accountId, characterCount));
	}
}
