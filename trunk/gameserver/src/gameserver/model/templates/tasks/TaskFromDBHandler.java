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
package gameserver.model.templates.tasks;

import commons.database.dao.DAOManager;
import gameserver.dao.TaskFromDBDAO;

/**
 * @author Divinity
 */
public abstract class TaskFromDBHandler implements Runnable {

	protected int id;
	protected String params[];

	/**
	 * Task's id
	 * 
	 * @param int
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Task's param(s)
	 * 
	 * @param params
	 *          String[]
	 */
	public void setParam(String params[]) {
		this.params = params;
	}

	/**
	 * The task's name This allow to check with the table column "task"
	 */
	public abstract String getTaskName();

	/**
	 * Check if the task's parameters are valid
	 * 
	 * @return true if valid, false otherwise
	 */
	public abstract boolean isValid();

	/**
	 * Retuns {@link gameserver.dao.TaskFromDBDAO} , just a shortcut
	 * 
	 * @return {@link gameserver.dao.TaskFromDBDAO}
	 */
	protected void setLastActivation() {
		DAOManager.getDAO(TaskFromDBDAO.class).setLastActivation(id);
	}
}
