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
package gameserver.taskmanager.tasks;

import javolution.util.FastMap;

import gameserver.model.gameobjects.Creature;
import gameserver.taskmanager.AbstractPeriodicTaskManager;

/**
 * @author ATracer
 */
public class PlayerMoveTaskManager extends AbstractPeriodicTaskManager {

	private final FastMap<Integer, Creature> movingPlayers = new FastMap<Integer, Creature>().shared();

	private PlayerMoveTaskManager() {
		super(200);
	}

	public void addPlayer(Creature player) {
		movingPlayers.put(player.getObjectId(), player);
	}

	public void removePlayer(Creature player) {
		movingPlayers.remove(player.getObjectId());
	}

	@Override
	public void run() {
		for (FastMap.Entry<Integer, Creature> e = movingPlayers.head(), mapEnd = movingPlayers.tail(); (e = e.getNext()) != mapEnd;) {
			Creature player = e.getValue();
			player.getMoveController().moveToDestination();
		}
	}

	public static final PlayerMoveTaskManager getInstance() {
		return SingletonHolder.INSTANCE;
	}

	private static final class SingletonHolder {

		private static final PlayerMoveTaskManager INSTANCE = new PlayerMoveTaskManager();
	}
}
