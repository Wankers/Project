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
package gameserver.network.aion.clientpackets;

import gameserver.configs.main.GSConfig;
import gameserver.model.gameobjects.player.Player;
import gameserver.network.aion.AionClientPacket;
import gameserver.network.aion.AionConnection.State;
import gameserver.network.aion.serverpackets.SM_PONG;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.audit.AuditLogger;

/**
 * @author -Nemesiss- modified by Undertrey
 */
public class CM_PING extends AionClientPacket {

	/**
	 * Constructs new instance of <tt>CM_PING </tt> packet
	 * 
	 * @param opcode
	 */
	public CM_PING(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void readImpl() {
		readH(); // unk
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl() {
		Player player = getConnection().getActivePlayer();
		long lastMS = getConnection().getLastPingTimeMS();

		if (lastMS > 0 && player != null) {
			long pingInterval = System.currentTimeMillis() - lastMS;
			// PingInterval should be 3min (180000ms)
			if (pingInterval < GSConfig.PING_INTERVAL * 1000) {// client timer cheat
				AuditLogger.info(player, "Possible client timer cheat kicking player: " + pingInterval + ", ip=" + getConnection().getIP());
				if (GSConfig.SECURITY_ENABLE) {
					PacketSendUtility.sendMessage(player, "You have been triggered Speed Hack detection so you're disconnected.");
					getConnection().closeNow();
				}
			}
		}
		getConnection().setLastPingTimeMS(System.currentTimeMillis());
		sendPacket(new SM_PONG());
	}
}
