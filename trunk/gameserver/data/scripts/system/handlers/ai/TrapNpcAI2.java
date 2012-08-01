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
package ai;

import gameserver.ai2.AI2Actions;
import gameserver.ai2.AIName;
import gameserver.ai2.AIState;
import gameserver.ai2.NpcAI2;
import gameserver.ai2.poll.AIAnswer;
import gameserver.ai2.poll.AIAnswers;
import gameserver.ai2.poll.AIQuestion;
import gameserver.model.gameobjects.Creature;
import gameserver.model.gameobjects.VisibleObject;
import gameserver.utils.ThreadPoolManager;
import gameserver.world.knownlist.Visitor;

/**
 * @author ATracer
 */
@AIName("trap")
public class TrapNpcAI2 extends NpcAI2 {
	public static int EVENT_SET_TRAP_RANGE = 1;
	
	private int trapRange = 0;
	
	@Override
	protected void handleCreatureMoved(Creature creature) {
		tryActivateTrap(creature);
	}

	@Override
	protected void handleSpawned() {
		getKnownList().doUpdate();
		getKnownList().doOnAllObjects(new Visitor<VisibleObject>() {

			@Override
			public void visit(VisibleObject object) {
				if (!(object instanceof Creature))
					return;
				Creature creature = (Creature) object;
				tryActivateTrap(creature);
			}
		});
		super.handleSpawned();
	}

	private void tryActivateTrap(Creature creature) {
		if (!creature.getLifeStats().isAlreadyDead() && isInRange(creature, trapRange)) {

			Creature creator = getCreator();
			if (!creator.isEnemy(creature))
				return;

			if (setStateIfNot(AIState.FIGHT)) {
				AI2Actions.targetCreature(this, creature);
				AI2Actions.useSkill(this, getSkillList().getRandomSkill().getSkillId());
				ThreadPoolManager.getInstance().schedule(new TrapDelete(this), 5000);
			}
		}
	}
	
	@Override
	protected void handleCustomEvent(int eventId, Object... args) {
		if(eventId == EVENT_SET_TRAP_RANGE)
			trapRange = (Integer) args[0];
	}

	@Override
	public boolean isMoveSupported() {
		return false;
	}

	@Override
	protected AIAnswer pollInstance(AIQuestion question) {
		switch (question) {
			case SHOULD_DECAY:
				return AIAnswers.NEGATIVE;
			case SHOULD_RESPAWN:
				return AIAnswers.NEGATIVE;
			case SHOULD_REWARD:
				return AIAnswers.NEGATIVE;
			default:
				return null;
		}
	}

	private static final class TrapDelete implements Runnable {

		private TrapNpcAI2 ai;

		TrapDelete(TrapNpcAI2 ai) {
			this.ai = ai;
		}

		@Override
		public void run() {
			AI2Actions.deleteOwner(ai);
			ai = null;
		}

	}

}