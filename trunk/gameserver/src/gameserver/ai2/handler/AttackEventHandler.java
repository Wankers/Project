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
import gameserver.ai2.AISubState;
import gameserver.ai2.NpcAI2;
import gameserver.ai2.manager.AttackManager;
import gameserver.ai2.manager.EmoteManager;
import gameserver.ai2.manager.WalkManager;
import gameserver.ai2.poll.AIQuestion;
import gameserver.model.gameobjects.Creature;
import gameserver.model.gameobjects.Npc;

/**
 * @author ATracer
 */
public class AttackEventHandler {

	/**
	 * @param npcAI
	 * @param creature
	 */
	public static void onAttack(NpcAI2 npcAI, Creature creature) {
		if (npcAI.isLogging()) {
			AI2Logger.info(npcAI, "onAttack");
		}
		if (creature == null || creature.getLifeStats().isAlreadyDead()) {
			return;
		}
		// TODO lock or better switch
		if (npcAI.isInState(AIState.RETURNING)) {
			// TODO add to aggrolist?
			return;
		}
		if (npcAI.isInState(AIState.WALKING)) {
			WalkManager.stopWalking(npcAI);
		}
		npcAI.getOwner().getGameStats().renewLastAttackedTime();
		if (npcAI.setStateIfNot(AIState.FIGHT)) {
			if (npcAI.isLogging()) {
				AI2Logger.info(npcAI, "onAttack() -> startAttacking");
			}
			npcAI.setSubStateIfNot(AISubState.NONE);
			npcAI.getOwner().setTarget(creature);
			AttackManager.startAttacking(npcAI);
			if (npcAI.poll(AIQuestion.CAN_SHOUT))
				ShoutEventHandler.onAttackBegin(npcAI, (Creature) npcAI.getOwner().getTarget());
		}
	}

	/**
	 * @param npcAI
	 */
	public static void onForcedAttack(NpcAI2 npcAI) {
		onAttack(npcAI, (Creature) npcAI.getOwner().getTarget());
	}

	/**
	 * @param npcAI
	 */
	public static void onAttackComplete(NpcAI2 npcAI) {
		if (npcAI.isLogging()) {
			AI2Logger.info(npcAI, "onAttackComplete: " + npcAI.getOwner().getGameStats().getLastAttackTimeDelta());
		}
		npcAI.getOwner().getGameStats().renewLastAttackTime();
		AttackManager.scheduleNextAttack(npcAI);
	}

	/**
	 * @param npcAI
	 */
	public static void onFinishAttack(NpcAI2 npcAI) {
		if (npcAI.isLogging()) {
			AI2Logger.info(npcAI, "onFinishAttack");
		}
		Npc npc = npcAI.getOwner();
		EmoteManager.emoteStopAttacking(npc);
		npc.getLifeStats().startResting();
		npc.getAggroList().clear();
		if (npcAI.poll(AIQuestion.CAN_SHOUT))
			ShoutEventHandler.onAttackEnd(npcAI);
		npc.setTarget(null);
		npc.setSkillNumber(0);
	}
}
