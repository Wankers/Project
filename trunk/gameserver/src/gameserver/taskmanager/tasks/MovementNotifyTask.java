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

import gameserver.ai2.event.AIEventType;
import gameserver.model.gameobjects.Creature;
import gameserver.model.gameobjects.Npc;
import gameserver.model.gameobjects.VisibleObject;
import gameserver.taskmanager.AbstractFIFOPeriodicTaskManager;
import gameserver.world.knownlist.VisitorWithOwner;

/**
 * @author ATracer
 */
public class MovementNotifyTask extends AbstractFIFOPeriodicTaskManager<Creature> {

	private static final class SingletonHolder {

		private static final MovementNotifyTask INSTANCE = new MovementNotifyTask();
	}

	public static MovementNotifyTask getInstance() {
		return SingletonHolder.INSTANCE;
	}

	private final MoveNotifier MOVE_NOTIFIER = new MoveNotifier();

	public MovementNotifyTask() {
		super(500);
	}

	@Override
	protected void callTask(Creature creature) {
		creature.getKnownList().doOnAllNpcsWithOwner(MOVE_NOTIFIER);
	}

	@Override
	protected String getCalledMethodName() {
		return "notifyOnMove()";
	}

	private class MoveNotifier implements VisitorWithOwner<Npc, VisibleObject> {

		@Override
		public void visit(Npc object, VisibleObject owner) {
			object.getAi2().onCreatureEvent(AIEventType.CREATURE_MOVED, (Creature) owner);
		}

	}
}
