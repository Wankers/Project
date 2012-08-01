/**
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

import java.util.ArrayList;

import commons.database.dao.DAO;
import gameserver.model.tasks.TaskFromDB;

/**
 * @author Divinity
 */
public abstract class TaskFromDBDAO implements DAO {

	/**
	 * Return all tasks from DB
	 * 
	 * @return all tasks
	 */
	public abstract ArrayList<TaskFromDB> getAllTasks();

	/**
	 * Set the last activation to NOW()
	 */
	public abstract void setLastActivation(final int id);

	/**
	 * Returns class name that will be uses as unique identifier for all DAO classes
	 * 
	 * @return class name
	 */
	@Override
	public final String getClassName() {
		return TaskFromDBDAO.class.getName();
	}
}
