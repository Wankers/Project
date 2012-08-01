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
package quest.kromedes_trial;

import gameserver.model.PlayerClass;
import gameserver.model.Race;
import gameserver.model.gameobjects.Npc;
import gameserver.model.gameobjects.player.Player;
import gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import gameserver.questEngine.handlers.QuestHandler;
import gameserver.questEngine.model.QuestDialog;
import gameserver.questEngine.model.QuestEnv;
import gameserver.questEngine.model.QuestState;
import gameserver.questEngine.model.QuestStatus;
import gameserver.services.QuestService;
import gameserver.utils.PacketSendUtility;

/**
 * @author VladimirZ
 */
public class _18621KaligasCollection extends QuestHandler {

	private final static int questId = 18621;

	public _18621KaligasCollection() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(730329).addOnQuestStart(questId);
		qe.registerQuestNpc(730329).addOnActionItemEvent(questId);
		qe.registerQuestNpc(730329).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (targetId == 730329) {
			PlayerClass playerClass = player.getCommonData().getPlayerClass();
			if ((playerClass == PlayerClass.MAGE || playerClass == PlayerClass.PRIEST || playerClass == PlayerClass.GLADIATOR
				|| playerClass == PlayerClass.WARRIOR || playerClass == PlayerClass.SCOUT) && player.getCommonData().getRace() == Race.ELYOS) {
				if (qs == null || qs.getStatus() == QuestStatus.NONE || qs.canRepeat()) {
					if (env.getDialog() == QuestDialog.USE_OBJECT)
						return sendQuestDialog(env, 1011);
					else
						return sendQuestStartDialog(env);
				}
				else if (qs != null && qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 0) {
					if (env.getDialog() == QuestDialog.USE_OBJECT)
						return sendQuestDialog(env, 2375);
					else if (env.getDialogId() == 34) {
						if (player.getInventory().getItemCountByItemId(185000102) >= 1) {
							removeQuestItem(env, 185000102, 1);
							qs.setStatus(QuestStatus.REWARD);
							qs.setQuestVar(1);
							qs.setCompleteCount(0);
							updateQuestStatus(env);
							return sendQuestDialog(env, 5);
						}
						else
							return sendQuestDialog(env, 2716);
					}
				}
				else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
					int var = qs.getQuestVarById(0);
					switch (env.getDialog()) {
						case USE_OBJECT:
							if (var == 1)
								return sendQuestDialog(env, 5);
						case SELECT_NO_REWARD:
							QuestService.finishQuest(env, qs.getQuestVars().getQuestVars() - 1);
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
							return true;
					}
				}
			}
			return false;
		}
		return false;
	}
}
