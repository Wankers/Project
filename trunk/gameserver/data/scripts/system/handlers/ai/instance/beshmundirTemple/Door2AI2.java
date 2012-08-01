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
 *  along with Aion Extreme Emulator.  If not, see <http://www.gnu.org/licenses/>.
 */
package ai.instance.beshmundirTemple;

import ai.ActionItemNpcAI2;

import gameserver.ai2.AI2Actions;
import gameserver.ai2.AIName;
import gameserver.model.Race;
import gameserver.model.gameobjects.player.Player;
import gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import gameserver.questEngine.model.QuestState;
import gameserver.questEngine.model.QuestStatus;
import gameserver.utils.PacketSendUtility;

/**
 * @author Gigi
 */
@AIName("door2")
public class Door2AI2 extends ActionItemNpcAI2 {

	@Override
	protected void handleDialogStart(Player player) {
		QuestState qsneedasmo = player.getQuestStateList().getQuestState(30311);
		QuestState qsneedelyos = player.getQuestStateList().getQuestState(30211);
		player.getActionItemNpc().setCondition(1, 0, getTalkDelay());
		if (player.getRace() == Race.ELYOS) {
			if (qsneedelyos != null && qsneedelyos.getStatus() != QuestStatus.NONE) {// TODO: Only one player in group has to																																		// have this quest
				super.handleUseItemStart(player);
			}
			else {
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 27));
			}
		}
		else {
			if (qsneedasmo != null && qsneedasmo.getStatus() != QuestStatus.NONE) { // TODO: Only one player in group has to																																			// have this quest
				super.handleUseItemStart(player);
			}
			else {
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 27));
			}
		}
	}

	@Override
	protected void handleUseItemFinish(Player player) {
		AI2Actions.deleteOwner(this);
	}
}
