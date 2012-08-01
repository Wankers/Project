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
package gameserver.network.chatserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import commons.network.packet.BaseClientPacket;

/**
 * @author ATracer 
 */
public abstract class CsClientPacket extends BaseClientPacket<ChatServerConnection> implements Cloneable {

	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(CsClientPacket.class);

	/**
	 * Constructs new client packet with specified opcode. If using this constructor, user must later manually set buffer
	 * and connection.
	 * 
	 * @param opcode
	 *          packet id
	 */
	protected CsClientPacket(int opcode) {
		super(opcode);
	}

	/**
	 * run runImpl catching and logging Throwable.
	 */
	@Override
	public final void run() {
		try {
			runImpl();
		}
		catch (Throwable e) {
			log.warn("error handling ls (" + getConnection().getIP() + ") message " + this, e);
		}
	}

	/**
	 * Send new LsServerPacket to connection that is owner of this packet. This method is equivalent to:
	 * getConnection().sendPacket(msg);
	 * 
	 * @param msg
	 */
	protected void sendPacket(CsServerPacket msg) {
		getConnection().sendPacket(msg);
	}

	/**
	 * Clones this packet object.
	 * 
	 * @return CsClientPacket
	 */
	public CsClientPacket clonePacket() {
		try {
			return (CsClientPacket) super.clone();
		}
		catch (CloneNotSupportedException e) {
			return null;
		}
	}
}