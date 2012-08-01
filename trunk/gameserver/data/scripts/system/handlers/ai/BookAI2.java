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

import gameserver.ai2.AIName;
import gameserver.ai2.NpcAI2;
import gameserver.model.gameobjects.player.Player;
import gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import gameserver.questEngine.model.QuestDialog;
import gameserver.utils.PacketSendUtility;

/**
 * @author Rolandas
 */
@AIName("book")
public class BookAI2 extends NpcAI2 {

	@Override
	protected void handleDialogStart(Player player) {
		Integer targetId = player.getTarget().getObjectId();
		PacketSendUtility.sendPacket(player, 
			new SM_DIALOG_WINDOW(targetId, QuestDialog.SELECT_ACTION_1011.id(), 0));
	}
}
