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
package gameserver.world.container;

import java.util.Map;

import javolution.util.FastMap;

import gameserver.model.gameobjects.Kisk;
import gameserver.model.gameobjects.player.Player;
import gameserver.world.exceptions.DuplicateAionObjectException;

/**
 * @author Sarynth
 */
public class KiskContainer {

	private final Map<Integer, Kisk> kiskByPlayerObjectId = new FastMap<Integer, Kisk>().shared();

	public void add(Kisk kisk, Player player) {
		if (this.kiskByPlayerObjectId.put(player.getObjectId(), kisk) != null)
			throw new DuplicateAionObjectException();
	}

	public Kisk get(Player player) {
		return this.kiskByPlayerObjectId.get(player.getObjectId());
	}

	public void remove(Player player) {
		this.kiskByPlayerObjectId.remove(player.getObjectId());
	}

	/**
	 * @return
	 */
	public int getCount() {
		return this.kiskByPlayerObjectId.size();
	}
}
