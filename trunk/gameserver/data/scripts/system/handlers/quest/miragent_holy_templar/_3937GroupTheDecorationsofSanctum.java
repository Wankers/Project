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
package quest.miragent_holy_templar;

import gameserver.model.gameobjects.player.Player;
import gameserver.questEngine.handlers.QuestHandler;
import gameserver.questEngine.model.QuestDialog;
import gameserver.questEngine.model.QuestEnv;
import gameserver.questEngine.model.QuestState;
import gameserver.questEngine.model.QuestStatus;

/**
 * @author Gigi
 */
public class _3937GroupTheDecorationsofSanctum extends QuestHandler {

	private final static int questId = 3937;

	public _3937GroupTheDecorationsofSanctum() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(203708).addOnQuestStart(questId);
		qe.registerQuestNpc(203708).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		QuestDialog dialog = env.getDialog();
		int targetId = env.getTargetId();

		if (qs == null || (qs.getStatus() == QuestStatus.NONE || qs.getStatus() == QuestStatus.COMPLETE)) {
			if (targetId == 203708) {
				if (dialog == QuestDialog.START_DIALOG)
					return sendQuestDialog(env, 4762);
				else
					return sendQuestStartDialog(env);
			}
		}

		if (qs == null)
			return false;
			
		int var = qs.getQuestVarById(0);

		if (qs.getStatus() == QuestStatus.START) {
			switch (targetId) {
				case 203708:
					switch (dialog) {
						case START_DIALOG:
							if (var == 0)
								return sendQuestDialog(env, 1011);
							else if (var == 1)
								return sendQuestDialog(env, 1352);
						case STEP_TO_1:
							return defaultCloseDialog(env, 0, 1); // 1
						case CHECK_COLLECTED_ITEMS:
							long itemCount1 = player.getInventory().getItemCountByItemId(182206095);
							if (itemCount1 >= 1) {
								removeQuestItem(env, 182206095, 1);
								changeQuestStep(env, 1, 1, true);
								return sendQuestDialog(env, 5);
							}
							else
								return sendQuestDialog(env, 10001);
					}
					break;
				default:
					return sendQuestStartDialog(env);
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203708)
				return sendQuestEndDialog(env);
		}
		return false;
	}
}
