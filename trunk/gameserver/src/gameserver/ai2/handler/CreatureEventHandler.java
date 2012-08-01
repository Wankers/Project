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

import gameserver.ai2.AIState;
import gameserver.ai2.NpcAI2;
import gameserver.ai2.event.AIEventType;
import gameserver.ai2.poll.AIQuestion;
import gameserver.model.gameobjects.Creature;
import gameserver.model.gameobjects.Npc;
import gameserver.utils.MathUtil;
import gameserver.world.geo.GeoService;

/**
 * @author ATracer
 */
public class CreatureEventHandler {

	/**
	 * @param npcAI
	 * @param creature
	 */
	public static void onCreatureMoved(NpcAI2 npcAI, Creature creature) {
		checkAggro(npcAI, creature);
	}

	/**
	 * @param npcAI
	 * @param creature
	 */
	public static void onCreatureSee(NpcAI2 npcAI, Creature creature) {
		checkAggro(npcAI, creature);
	}

	/**
	 * @param ai
	 * @param creature
	 */
	protected static void checkAggro(NpcAI2 ai, Creature creature) {
		Npc owner = ai.getOwner();
		if (creature.getLifeStats().isAlreadyDead()) {
			return;
		}
		if (!owner.canSee(creature)) {
			return;
		}
		if (!owner.getActiveRegion().isMapRegionActive()) {
			return;
		}
		if (!ai.isInState(AIState.FIGHT) && MathUtil.isIn3dRange(owner, creature, owner.getAggroRange())) {
			if (ai.poll(AIQuestion.CAN_SHOUT)) {
				ShoutEventHandler.onSee(ai, creature);
			}
			if (owner.isAggressiveTo(creature)) {
				if (GeoService.getInstance().canSee(owner, creature)) {
					ai.onCreatureEvent(AIEventType.CREATURE_AGGRO, creature);
				}
			}
		}
	}

}
