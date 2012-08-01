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

import commons.database.dao.DAOManager;
import loginserver.dao.AccountDAO;
import loginserver.dao.PremiumDAO;
import loginserver.model.Account;
import loginserver.network.gameserver.GsClientPacket;

/**
 * @author xTz
 */
public class CM_ACCOUNT_TOLL_INFO extends GsClientPacket {

	private int toll;

	private String accountName;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void readImpl() {
		toll = readD();
		accountName = readS();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl() {
		Account account = DAOManager.getDAO(AccountDAO.class).getAccount(accountName);

		if (account != null)
			DAOManager.getDAO(PremiumDAO.class).updatePoints(account.getId(), toll, 0);
	}
}
