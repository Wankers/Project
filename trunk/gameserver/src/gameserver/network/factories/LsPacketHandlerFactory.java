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
package gameserver.network.factories;

import gameserver.network.loginserver.LoginServerConnection.State;
import gameserver.network.loginserver.LsClientPacket;
import gameserver.network.loginserver.LsPacketHandler;
import gameserver.network.loginserver.clientpackets.CM_ACCOUNT_RECONNECT_KEY;
import gameserver.network.loginserver.clientpackets.CM_ACOUNT_AUTH_RESPONSE;
import gameserver.network.loginserver.clientpackets.CM_BAN_RESPONSE;
import gameserver.network.loginserver.clientpackets.CM_GS_AUTH_RESPONSE;
import gameserver.network.loginserver.clientpackets.CM_GS_CHARACTER_RESPONSE;
import gameserver.network.loginserver.clientpackets.CM_LS_CONTROL_RESPONSE;
import gameserver.network.loginserver.clientpackets.CM_LS_PING;
import gameserver.network.loginserver.clientpackets.CM_MACBAN_LIST;
import gameserver.network.loginserver.clientpackets.CM_PREMIUM_RESPONSE;
import gameserver.network.loginserver.clientpackets.CM_PTRANSFER_RESPONSE;
import gameserver.network.loginserver.clientpackets.CM_REQUEST_KICK_ACCOUNT;

/**
 * @author Luno
 */
public class LsPacketHandlerFactory {

	private LsPacketHandler handler = new LsPacketHandler();

	public static final LsPacketHandlerFactory getInstance() {
		return SingletonHolder.instance;
	}

	/**
	 * @param loginServer
	 */
	private LsPacketHandlerFactory() {
		addPacket(new CM_ACCOUNT_RECONNECT_KEY(0x03), State.AUTHED);
		addPacket(new CM_ACOUNT_AUTH_RESPONSE(0x01), State.AUTHED);
		addPacket(new CM_GS_AUTH_RESPONSE(0x00), State.CONNECTED);
		addPacket(new CM_REQUEST_KICK_ACCOUNT(0x02), State.AUTHED);
		addPacket(new CM_LS_CONTROL_RESPONSE(0x04), State.AUTHED);
		addPacket(new CM_BAN_RESPONSE(0x05), State.AUTHED);
		addPacket(new CM_GS_CHARACTER_RESPONSE(0x08), State.AUTHED);
		addPacket(new CM_MACBAN_LIST(9), State.AUTHED);
		addPacket(new CM_PREMIUM_RESPONSE(10), State.AUTHED);
		addPacket(new CM_LS_PING(11), State.AUTHED);
		addPacket(new CM_PTRANSFER_RESPONSE(12), State.AUTHED);
	}

	private void addPacket(LsClientPacket prototype, State... states) {
		handler.addPacketPrototype(prototype, states);
	}

	public LsPacketHandler getPacketHandler() {
		return handler;
	}

	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder {

		protected static final LsPacketHandlerFactory instance = new LsPacketHandlerFactory();
	}
}
