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

import gameserver.model.gameobjects.Creature;
import gameserver.network.aion.AionConnection;
import gameserver.network.aion.AionServerPacket;

/**
 * @author Sweetkr
 */
public class SM_FORCED_MOVE extends AionServerPacket {

	private Creature creature;
	private int objectId;
	private float x;
	private float y;
	private float z;

	public SM_FORCED_MOVE(Creature creature, Creature target) {
		this(creature, target.getObjectId(), target.getX(), target.getY(), target.getZ());
	}
	public SM_FORCED_MOVE(Creature creature, int objectId, float x, float y, float z) {
		this.creature = creature;
		this.objectId = objectId;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeImpl(AionConnection con) {
		writeD(creature.getObjectId());
		writeD(objectId);//targets objectId
		writeC(16); // unk
		writeF(x);
		writeF(y);
		writeF(z);
	}
}
