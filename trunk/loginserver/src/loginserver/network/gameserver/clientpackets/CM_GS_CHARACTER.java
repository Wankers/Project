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
package loginserver.network.gameserver.clientpackets;

import loginserver.GameServerInfo;
import loginserver.controller.AccountController;
import loginserver.network.gameserver.GsClientPacket;

/**
 * @author cura
 */
public class CM_GS_CHARACTER extends GsClientPacket {

	private int accountId;
	private int characterCount;

	@Override
	protected void readImpl() {
		accountId = readD();
		characterCount = readC();
	}

	@Override
	protected void runImpl() {
		GameServerInfo gsi = this.getConnection().getGameServerInfo();

		AccountController.addGSCharacterCountFor(accountId, gsi.getId(), characterCount);

		if (AccountController.hasAllGSCharacterCounts(accountId))
			AccountController.sendServerListFor(accountId);
	}
}
