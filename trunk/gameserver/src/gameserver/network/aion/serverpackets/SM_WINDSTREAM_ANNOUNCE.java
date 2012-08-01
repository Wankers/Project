/**
 * This file is part of Aion Extreme Emulator  <aion-core.net>
 *
 *  Aion Extreme Emulator is a free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Aion Extreme Emulator is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Lesser Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser Public License
 *  along with Aion Extreme Emulator. If not, see <http://www.gnu.org/licenses/>.
 */
package gameserver.network.aion.serverpackets;

import gameserver.network.aion.AionConnection;
import gameserver.network.aion.AionServerPacket;

/**
 * @author LokiReborn
 * 
 */
public class SM_WINDSTREAM_ANNOUNCE extends AionServerPacket {
	private int bidirectional;
	private int mapId;
	private int streamId;
	private int boost;
	
	public SM_WINDSTREAM_ANNOUNCE(int bidirectional, int mapId, int streamId, int boost) {
		this.bidirectional = bidirectional;
		this.mapId = mapId;
		this.streamId = streamId;
		this.boost = boost;
	}

	@Override
	protected void writeImpl(AionConnection con) {
		writeD(bidirectional);
		writeD(mapId);
		writeD(streamId);
		writeC(boost);
	}
}
