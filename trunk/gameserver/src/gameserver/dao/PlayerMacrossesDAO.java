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
import gameserver.model.gameobjects.player.MacroList;

/**
 * Macrosses DAO
 * <p/>
 * Created on: 13.07.2009 17:05:56
 * 
 * @author Aquanox
 */
public abstract class PlayerMacrossesDAO implements DAO {

	/**
	 * Returns unique identifier for PlayerMacroDAO
	 * 
	 * @return unique identifier for PlayerMacroDAO
	 */
	@Override
	public final String getClassName() {
		return PlayerMacrossesDAO.class.getName();
	}

	/**
	 * Returns a list of macrosses for player
	 * 
	 * @param playerId
	 *          Player object id.
	 * @return a list of macrosses for player
	 */
	public abstract MacroList restoreMacrosses(int playerId);

	/**
	 * Add a macro information into database
	 * 
	 * @param playerId
	 *          player object id
	 * @param macroPosition
	 *          macro order # of player
	 * @param macro
	 *          macro contents.
	 */
	public abstract void addMacro(int playerId, int macroPosition, String macro);

	/**
	 * Update a macro information into database
	 * 
	 * @param playerId
	 *          player object id
	 * @param macroPosition
	 *          macro order # of player
	 * @param macro
	 *          macro contents.
	 */
	public abstract void updateMacro(int playerId, int macroPosition, String macro);

	/**
	 * Remove macro in database
	 * 
	 * @param playerId
	 *          player object id
	 * @param macroPosition
	 *          order of macro in macro list
	 */
	public abstract void deleteMacro(int playerId, int macroPosition);
}
