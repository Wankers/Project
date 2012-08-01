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
package gameserver.ai2.manager;

import gameserver.ai2.AI2Logger;
import gameserver.ai2.AISubState;
import gameserver.ai2.AttackIntention;
import gameserver.ai2.NpcAI2;
import gameserver.ai2.event.AIEventType;
import gameserver.model.gameobjects.Creature;
import gameserver.model.gameobjects.Npc;

/**
 * @author ATracer
 */
public class AttackManager {

	/**
	 * @param npcAI
	 */
	public static void startAttacking(NpcAI2 npcAI) {
		if (npcAI.isLogging()) {
			AI2Logger.info(npcAI, "AttackManager: startAttacking");
		}
		EmoteManager.emoteStartAttacking(npcAI.getOwner());
		scheduleNextAttack(npcAI);
	}

	/**
	 * @param npcAI
	 */
	public static void scheduleNextAttack(NpcAI2 npcAI) {
		if (npcAI.isLogging()) {
			AI2Logger.info(npcAI, "AttackManager: scheduleNextAttack");
		}
		// don't start attack while in casting substate
		AISubState subState = npcAI.getSubState();
		if (subState == AISubState.NONE) {
			chooseAttack(npcAI, npcAI.getOwner().getGameStats().getNextAttackInterval());
		}
		else {
			if (npcAI.isLogging()) {
				AI2Logger.info(npcAI, "Will not choose attack in substate" + subState);
			}
		}
	}

	/**
	 * choose attack type
	 */
	protected static void chooseAttack(NpcAI2 npcAI, int delay) {
		AttackIntention attackIntention = npcAI.chooseAttackIntention();
		if (npcAI.isLogging()) {
			AI2Logger.info(npcAI, "AttackManager: chooseAttack " + attackIntention + " delay " + delay);
		}
		switch (attackIntention) {
			case SIMPLE_ATTACK:
				SimpleAttackManager.performAttack(npcAI, delay);
				break;
			case SKILL_ATTACK:
				SkillAttackManager.performAttack(npcAI, delay);
				break;
			case FINISH_ATTACK:
				npcAI.think();
				break;
		}
	}

	/**
	 * @param npcAI
	 */
	public static void targetTooFar(NpcAI2 npcAI) {
		Npc npc = npcAI.getOwner();
		if (npcAI.isLogging()) {
			AI2Logger.info(npcAI, "AttackManager: attackTimeDelta " + npc.getGameStats().getLastAttackTimeDelta());
		}
		
		// switch target if there is more hated creature
		if (npc.getGameStats().getLastChangeTargetTimeDelta() > 5) {
			Creature mostHated = npc.getAggroList().getMostHated();
			if (mostHated != null && !mostHated.getLifeStats().isAlreadyDead() && !npc.isTargeting(mostHated.getObjectId())) {
				if (npcAI.isLogging()) {
					AI2Logger.info(npcAI, "AttackManager: switching target during chase");
				}
				npcAI.onCreatureEvent(AIEventType.TARGET_CHANGED, mostHated);
				return;
			}
		}
		
		if(checkGiveupDistance(npcAI)){
			npcAI.onGeneralEvent(AIEventType.TARGET_GIVEUP);
			return;
		}
		// try to move to target if npc was attacked or attacked target less than 20 secs ago
		if (npc.getGameStats().getLastAttackTimeDelta() < 20 || npc.getGameStats().getLastAttackedTimeDelta() < 20) {
			if (npcAI.isMoveSupported()) {
				npc.getMoveController().moveToTargetObject();
				return;
			}
		}
		npcAI.onGeneralEvent(AIEventType.TARGET_GIVEUP);
	}

	private static boolean checkGiveupDistance(NpcAI2 npcAI) {
		Npc npc = npcAI.getOwner();
		// if target run away too far
		float distanceToTarget = npc.getDistanceToTarget();
		if (npcAI.isLogging()) {
			AI2Logger.info(npcAI, "AttackManager: distanceToTarget " + distanceToTarget);
		}
		//TODO may be ask AI too
		int chaseTarget = npc.isBoss() ? 50 : npc.getPosition().getWorldMapInstance().getTemplate().getAiInfo()
			.getChaseTarget();
		if (distanceToTarget > chaseTarget) {
			return true;
		}
		double distanceToHome = npc.getDistanceToSpawnLocation();
		// if npc is far away from home
		int chaseHome = npc.isBoss() ? 150 : npc.getPosition().getWorldMapInstance().getTemplate().getAiInfo()
			.getChaseHome();
		if (distanceToHome > chaseHome) {
			return true;
		}
		// start thinking about home after 100 meters and no attack for 10 seconds
		if (distanceToHome > chaseHome / 2 && npc.getGameStats().getLastAttackedTimeDelta() > 10) {
			return true;
		}
		return false;
	}

}