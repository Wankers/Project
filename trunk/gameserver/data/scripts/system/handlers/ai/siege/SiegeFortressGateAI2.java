/*
 * This file is part of Aion Extreme Emulator  <aion-core.net>.
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
package ai.siege;

import gameserver.ai2.AI2Actions;
import gameserver.ai2.AI2Request;
import gameserver.ai2.AIName;
import gameserver.ai2.NpcAI2;
import gameserver.ai2.poll.AIAnswer;
import gameserver.ai2.poll.AIAnswers;
import gameserver.ai2.poll.AIQuestion;
import gameserver.model.gameobjects.Creature;
import gameserver.model.gameobjects.player.Player;
import gameserver.services.teleport.TeleportService;
import gameserver.utils.MathUtil;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.PositionUtil;

/**
 * @author Source
 */
@AIName("fortressgate")
public class SiegeFortressGateAI2 extends NpcAI2 {

	@Override
	protected void handleDialogStart(Player player) {
		AI2Actions.addRequest(this, player, 160017, 0, new AI2Request() {

			@Override
			public void acceptRequest(Creature requester, Player responder) {
				if (MathUtil.isInRange(requester, responder, 10))
					TeleportService.moveToTargetWithDistance(requester, responder,
						PositionUtil.isBehind(requester, responder) ? 0 : 1, 3);
				else
					PacketSendUtility.sendBrightYellowMessageOnCenter(responder, "You too far away");
			}
		});
	}

	@Override
	protected void handleDialogFinish(Player player) {
	}

	@Override
	protected AIAnswer pollInstance(AIQuestion question) {
		switch (question) {
			case SHOULD_DECAY:
				return AIAnswers.NEGATIVE;
			case SHOULD_RESPAWN:
				return AIAnswers.NEGATIVE;
			default:
				return null;
		}
	}
}
