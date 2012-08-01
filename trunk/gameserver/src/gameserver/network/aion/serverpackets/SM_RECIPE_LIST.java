/*
 * This file is part of aion-unique <aionu-unique.org>.
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

import java.util.Set;

import gameserver.network.aion.AionConnection;
import gameserver.network.aion.AionServerPacket;

/**
 * @author lord_rex
 */
public class SM_RECIPE_LIST extends AionServerPacket {

	private Integer[] recipeIds;
	private int count;

	public SM_RECIPE_LIST(Set<Integer> recipeIds) {
		this.recipeIds = recipeIds.toArray(new Integer[recipeIds.size()]);
		this.count = recipeIds.size();
	}

	@Override
	protected void writeImpl(AionConnection con) {
		writeH(count);
		for (int id : recipeIds) {
			writeD(id);
			writeC(0);
		}
	}
}
