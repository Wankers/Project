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
package gameserver.network.aion.serverpackets;


import gameserver.network.aion.AionConnection;
import gameserver.network.aion.AionServerPacket;

/**
 * @author Mr. Poke
 */
public class SM_CRAFT_ANIMATION extends AionServerPacket {

	private int senderObjectId;
	private int targetObjectId;
	private int skillId;
	private int action;

	/**
	 * @param senderObjectId
	 * @param targetObjectId
	 * @param skillId
	 * @param action
	 */
	public SM_CRAFT_ANIMATION(int senderObjectId, int targetObjectId, int skillId, int action) {
		this.senderObjectId = senderObjectId;
		this.targetObjectId = targetObjectId;
		this.skillId = skillId;
		this.action = action;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeImpl(AionConnection con) {
		writeD(senderObjectId);
		writeD(targetObjectId);
		writeH(skillId);
		writeC(action);
	}
}
