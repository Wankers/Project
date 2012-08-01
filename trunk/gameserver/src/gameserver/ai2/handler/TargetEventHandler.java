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
package gameserver.ai2.handler;

import gameserver.ai2.AI2Logger;
import gameserver.ai2.AIState;
import gameserver.ai2.NpcAI2;
import gameserver.ai2.event.AIEventType;
import gameserver.ai2.manager.AttackManager;
import gameserver.ai2.manager.FollowManager;
import gameserver.ai2.manager.WalkManager;
import gameserver.model.gameobjects.Creature;
import gameserver.model.gameobjects.VisibleObject;

/**
 * @author ATracer
 */
public class TargetEventHandler {

	/**
	 * @param npcAI
	 */
	public static void onTargetReached(NpcAI2 npcAI) {
		if (npcAI.isLogging()) {
			AI2Logger.info(npcAI, "onTargetReached");
		}

		AIState currentState = npcAI.getState();
		switch (currentState) {
			case FIGHT:
				npcAI.getOwner().getMoveController().abortMove();
				AttackManager.scheduleNextAttack(npcAI);
				break;
			case RETURNING:
				npcAI.getOwner().getMoveController().abortMove();
				npcAI.onGeneralEvent(AIEventType.BACK_HOME);
				break;
			case WALKING:
				WalkManager.targetReached(npcAI);
				break;
			case FOLLOWING:
				npcAI.getOwner().getMoveController().abortMove();
				break;
			case FEAR://TODO remove this state
				npcAI.getOwner().getMoveController().abortMove();
				break;
		}
	}

	/**
	 * @param npcAI
	 */
	public static void onTargetTooFar(NpcAI2 npcAI) {
		if (npcAI.isLogging()) {
			AI2Logger.info(npcAI, "onTargetTooFar");
		}
		switch (npcAI.getState()) {
			case FIGHT:
				AttackManager.targetTooFar(npcAI);
				break;
			case FOLLOWING:
				FollowManager.targetTooFar(npcAI);
				break;
			case FEAR:
				break;
			default:
				if (npcAI.isLogging()) {
					AI2Logger.info(npcAI, "default onTargetTooFar");
				}
		}
	}

	/**
	 * @param npcAI
	 */
	public static void onTargetGiveup(NpcAI2 npcAI) {
		if (npcAI.isLogging()) {
			AI2Logger.info(npcAI, "onTargetGiveup");
		}
		VisibleObject target = npcAI.getOwner().getTarget();
		if (target != null) {
			npcAI.getOwner().getAggroList().stopHating(target);
		}
		if (npcAI.isMoveSupported()) {
			npcAI.getOwner().getMoveController().abortMove();
		}
		npcAI.think();
	}

	/**
	 * @param npcAI
	 */
	public static void onTargetChange(NpcAI2 npcAI, Creature creature) {
		if (npcAI.isLogging()) {
			AI2Logger.info(npcAI, "onTargetChange");
		}
		if (npcAI.isInState(AIState.FIGHT)) {
			npcAI.getOwner().setTarget(creature);
			AttackManager.scheduleNextAttack(npcAI);
		}
	}
}
