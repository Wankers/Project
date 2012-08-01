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

/**
 * @author Ben
 */
public abstract class ServerVariablesDAO implements DAO {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String getClassName() {
		return ServerVariablesDAO.class.getName();
	}

	/**
	 * Loads the server variables stored in the database
	 * 
	 * @returns variable stored in database
	 */
	public abstract int load(String var);

	/**
	 * Stores the server variables
	 */
	public abstract boolean store(String var, int value);

}
