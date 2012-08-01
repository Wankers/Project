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

import gameserver.ai2.AIName;
import gameserver.ai2.NpcAI2;
import gameserver.model.DescriptionId;
import gameserver.model.gameobjects.Creature;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.gameobjects.player.RequestResponseHandler;
import gameserver.network.aion.serverpackets.SM_QUESTION_WINDOW;
import gameserver.utils.PacketSendUtility;

/**
 * @author Source
 */
@AIName("siege_gaterepair")
public class GateRepairAI2 extends NpcAI2 {

	@Override
	protected void handleDialogStart(final Player player) {
		RequestResponseHandler gaterepair = new RequestResponseHandler(player) {

			@Override
			public void acceptRequest(Creature requester, Player responder) {
				RequestResponseHandler repairstone = new RequestResponseHandler(player) {

					@Override
					public void acceptRequest(Creature requester, Player responder) {
						onActivate(player);
					}

					@Override
					public void denyRequest(Creature requester, Player responder) {
						// Nothing Happens
					}

				};
				if (player.getResponseRequester().putRequest(160016, repairstone))
					PacketSendUtility.sendPacket(player, new SM_QUESTION_WINDOW(160016, player.getObjectId(), new DescriptionId(2 * 716568 + 1)));
			}

			@Override
			public void denyRequest(Creature requester, Player responder) {
				// Nothing Happens
			}

		};

		if (player.getResponseRequester().putRequest(160027, gaterepair))
			PacketSendUtility.sendPacket(player, new SM_QUESTION_WINDOW(160027, player.getObjectId()));
	}

	@Override
	protected void handleDialogFinish(Player player) {
	}

	public void onActivate(Player player) {
		// Stert repair process
	}

}