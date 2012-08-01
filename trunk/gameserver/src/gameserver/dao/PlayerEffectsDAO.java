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
package gameserver.dao;

import commons.database.dao.DAO;
import gameserver.model.gameobjects.player.Player;

/**
 * @author ATracer
 */
public abstract class PlayerEffectsDAO implements DAO {

	/**
	 * Returns unique identifier for PlayerEffectsDAO
	 * 
	 * @return unique identifier for PlayerEffectsDAO
	 */
	@Override
	public final String getClassName() {
		return PlayerEffectsDAO.class.getName();
	}

	/**
	 * @param player
	 */
	public abstract void loadPlayerEffects(Player player);

	/**
	 * @param player
	 */
	public abstract void storePlayerEffects(Player player);

}
