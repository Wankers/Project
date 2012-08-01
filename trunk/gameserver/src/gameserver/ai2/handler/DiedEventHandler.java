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
import gameserver.ai2.poll.AIQuestion;
import gameserver.model.gameobjects.Npc;

/**
 * @author ATracer
 */
public class DiedEventHandler {

	public static void onDie(NpcAI2 npcAI) {
		if (npcAI.isLogging()) {
			AI2Logger.info(npcAI, "onDie");
		}

		onSimpleDie(npcAI);

		Npc owner = npcAI.getOwner();
		owner.setTarget(null);
	}

	public static void onSimpleDie(NpcAI2 npcAI) {
		if (npcAI.isLogging()) {
			AI2Logger.info(npcAI, "onSimpleDie");
		}

		if (npcAI.poll(AIQuestion.CAN_SHOUT))
			ShoutEventHandler.onDied(npcAI);

		npcAI.setStateIfNot(AIState.DIED);
		npcAI.setSubStateIfNot(AISubState.NONE);
		npcAI.getOwner().getAggroList().clear();
	}

}
