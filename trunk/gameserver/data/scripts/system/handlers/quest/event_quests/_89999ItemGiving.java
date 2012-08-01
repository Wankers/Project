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
package quest.event_quests;

import gameserver.configs.main.EventsConfig;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.items.storage.Storage;
import gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import gameserver.questEngine.handlers.QuestHandler;
import gameserver.questEngine.model.QuestEnv;
import gameserver.utils.PacketSendUtility;

/**
 * @author Rolandas
 */
public class _89999ItemGiving extends QuestHandler {

	private final static int questId = 89999;

	public _89999ItemGiving() {
		super(questId);
	}

	@Override
	public void register() {
		// Juice
		qe.registerQuestNpc(799702).addOnTalkEvent(questId); // Laylin (elyos)
		qe.registerQuestNpc(799703).addOnTalkEvent(questId); // Ronya (asmodian)
		// Cakes
		qe.registerQuestNpc(798414).addOnTalkEvent(questId); // Brios (elyos)
		qe.registerQuestNpc(798416).addOnTalkEvent(questId); // Bothen (asmodian)
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		int itemId = 0;
		Player player = env.getPlayer();

		if (env.getTargetId() == 799703 || env.getTargetId() == 799702)
			itemId = EventsConfig.EVENT_GIVEJUICE;
		else if (env.getTargetId() == 798416 || env.getTargetId() == 798414)
			itemId = EventsConfig.EVENT_GIVECAKE;

		if (itemId == 0)
			return false;

		int targetId = env.getVisibleObject().getObjectId();
		switch (env.getDialog()) {
			case USE_OBJECT:
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetId, 1011, 0));
				return true;
			case SELECT_ACTION_1012: {
				Storage inventory = player.getInventory();
				if (inventory.getItemCountByItemId(itemId) > 0) {
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetId, 1097, 0));
					return true;
				}
				else {
					if (giveQuestItem(env, itemId, 1))
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetId, 1012, 0));
					return true;
				}
			}
		}
		return false;

	}
}
