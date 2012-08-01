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


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import commons.utils.Rnd;
import loginserver.controller.AccountController;
import loginserver.model.Account;
import loginserver.model.ReconnectingAccount;
import loginserver.network.gameserver.GsClientPacket;
import loginserver.network.gameserver.serverpackets.SM_ACCOUNT_RECONNECT_KEY;

/**
 * This packet is sended by GameServer when player is requesting fast reconnect to login server. LoginServer in response
 * will send reconectKey.
 * 
 * @author -Nemesiss-
 */
public class CM_ACCOUNT_RECONNECT_KEY extends GsClientPacket {

	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(CM_ACCOUNT_RECONNECT_KEY.class);
	/**
	 * accoundId of account that will be reconnecting.
	 */
	private int accountId;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void readImpl() {
		accountId = readD();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl() {
		int reconectKey = Rnd.nextInt();
		Account acc = this.getConnection().getGameServerInfo().removeAccountFromGameServer(accountId);
		if (acc == null)
			log.info("This shouldnt happend! [Error]");
		else
			AccountController.addReconnectingAccount(new ReconnectingAccount(acc, reconectKey));
		sendPacket(new SM_ACCOUNT_RECONNECT_KEY(accountId, reconectKey));
	}
}
