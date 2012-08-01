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
import gameserver.ai2.manager.EmoteManager;
import gameserver.model.gameobjects.Npc;
import gameserver.model.templates.spawns.SpawnTemplate;
import gameserver.model.templates.zone.Point2D;
import gameserver.spawnengine.WalkerGroup;

/**
 * @author ATracer
 */
public class ReturningEventHandler {

	/**
	 * @param npcAI
	 */
	public static void onNotAtHome(NpcAI2 npcAI) {
		if (npcAI.isLogging()) {
			AI2Logger.info(npcAI, "onNotAtHome");
		}
		if (npcAI.setStateIfNot(AIState.RETURNING)) {
			if (npcAI.isLogging()) {
				AI2Logger.info(npcAI, "returning and restoring");
			}
			EmoteManager.emoteStartReturning(npcAI.getOwner());
			Npc npc = (Npc) npcAI.getOwner();
			SpawnTemplate spawn =npc.getSpawn();
			Point2D dest = null;
			Point2D origin = new Point2D(spawn.getX(), spawn.getY());
			if (npc.getWalkerGroup() != null) {
				npc.getWalkerGroup().setStep(npc, 1);
				dest = WalkerGroup.getLinePoint(new Point2D(npc.getX(), npc.getY()), origin, npc.getWalkerGroupShift());
			}
			else {
				dest = origin;
			}
			npcAI.getOwner().getMoveController().moveToPoint(dest.getX(), dest.getY(), spawn.getZ());
		}
	}

	/**
	 * @param npcAI
	 */
	public static void onBackHome(NpcAI2 npcAI) {
		if (npcAI.isLogging()) {
			AI2Logger.info(npcAI, "onBackHome");
		}
		if (npcAI.setStateIfNot(AIState.IDLE)) {
			EmoteManager.emoteStartIdling(npcAI.getOwner());
			ThinkEventHandler.thinkIdle(npcAI);
		}
	}

}
