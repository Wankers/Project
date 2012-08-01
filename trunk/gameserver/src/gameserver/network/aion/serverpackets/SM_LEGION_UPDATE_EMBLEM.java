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


import gameserver.model.team.legion.LegionEmblemType;
import gameserver.network.aion.AionConnection;
import gameserver.network.aion.AionServerPacket;

/**
 * @author Simple modified cura
 */
public class SM_LEGION_UPDATE_EMBLEM extends AionServerPacket {

	/** Legion emblem information **/
	private int legionId;
	private int emblemId;
	private int color_r;
	private int color_g;
	private int color_b;
	private LegionEmblemType emblemType;

	/**
	 * This constructor will handle legion emblem info
	 * 
	 * @param legionId
	 * @param emblemId
	 * @param color_r
	 * @param color_g
	 * @param color_b
	 * @param emblemType
	 */
	public SM_LEGION_UPDATE_EMBLEM(int legionId, int emblemId, int color_r, int color_g, int color_b,
		LegionEmblemType emblemType) {
		this.legionId = legionId;
		this.emblemId = emblemId;
		this.color_r = color_r;
		this.color_g = color_g;
		this.color_b = color_b;
		this.emblemType = emblemType;
	}

	@Override
	protected void writeImpl(AionConnection con) {
		writeD(legionId);
		writeC(emblemId);
		writeC(emblemType.getValue());
		writeC(0xFF); // Fixed
		writeC(color_r);
		writeC(color_g);
		writeC(color_b);
	}
}
