/*
 * This file is part of Aion Extreme Emulator  <aion-core.net>
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

import commons.network.util.ThreadPoolManager;
import gameserver.ai2.AIName;
import gameserver.model.EmotionType;
import gameserver.model.TaskId;
import gameserver.model.gameobjects.player.ActionItemNpc;
import gameserver.model.gameobjects.player.Player;
import gameserver.network.aion.serverpackets.SM_EMOTION;
import gameserver.network.aion.serverpackets.SM_USE_OBJECT;
import gameserver.utils.PacketSendUtility;

/**
 *
 * @author xTz
 */
@AIName("actionportal")
public class ActionPortalNpcAI2 extends PortalDialogAI2 {

	@Override
	protected void handleDialogStart(Player player) {
		player.getActionItemNpc().setCondition(1, 0, getTalkDelay());
		handleUseItemStart(player);
	}

	protected void handleUseItemStart(final Player player) {
		final ActionItemNpc actionItem = player.getActionItemNpc();
		PacketSendUtility.sendPacket(player,new SM_USE_OBJECT(player.getObjectId(), getObjectId(), actionItem.getTalkDelay(), actionItem.getStartCondition()));
		PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.START_QUESTLOOT, 0, getObjectId()), true);
		player.getController().addTask(TaskId.ACTION_ITEM_NPC, ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.END_QUESTLOOT, 0, getObjectId()), true);
				PacketSendUtility.sendPacket(player, new SM_USE_OBJECT(player.getObjectId(), getObjectId(), actionItem.getTalkDelay(), actionItem.getEndCondition()));
				handleUseItemFinish(player);
			}
		}, actionItem.getTalkDelay()));
	}

	protected void handleUseItemFinish(Player player) {
		super.handleDialogStart(player);
	}

	public int getTalkDelay() {
		return getObjectTemplate().getTalkDelay() * 1000;
	}
}
