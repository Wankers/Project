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

import gameserver.network.chatserver.CsClientPacket;
import gameserver.network.chatserver.CsPacketHandler;
import gameserver.network.chatserver.ChatServerConnection.State;
import gameserver.network.chatserver.clientpackets.CM_CS_AUTH_RESPONSE;
import gameserver.network.chatserver.clientpackets.CM_CS_PLAYER_AUTH_RESPONSE;

/**
 * @author ATracer
 */
public class CsPacketHandlerFactory {

	private CsPacketHandler handler = new CsPacketHandler();

	/**
	 * @param injector
	 */
	public CsPacketHandlerFactory() {
		addPacket(new CM_CS_AUTH_RESPONSE(0x00), State.CONNECTED);
		addPacket(new CM_CS_PLAYER_AUTH_RESPONSE(0x01), State.AUTHED);
	}

	/**
	 * @param prototype
	 * @param states
	 */
	private void addPacket(CsClientPacket prototype, State... states) {
		handler.addPacketPrototype(prototype, states);
	}

	/**
	 * @return handler
	 */
	public CsPacketHandler getPacketHandler() {
		return handler;
	}
}
