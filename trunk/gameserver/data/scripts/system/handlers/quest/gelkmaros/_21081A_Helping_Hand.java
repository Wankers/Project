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
package quest.gelkmaros;

import gameserver.model.gameobjects.player.Player;
import gameserver.questEngine.handlers.QuestHandler;
import gameserver.questEngine.model.QuestDialog;
import gameserver.questEngine.model.QuestEnv;
import gameserver.questEngine.model.QuestState;
import gameserver.questEngine.model.QuestStatus;

/**
 * @author zhkchi
 */
public class _21081A_Helping_Hand extends QuestHandler {

	private final static int questId = 21081;

	public _21081A_Helping_Hand() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(799225).addOnQuestStart(questId); // Richelle
		qe.registerQuestNpc(799225).addOnTalkEvent(questId); // Richelle
		qe.registerQuestNpc(799332).addOnTalkEvent(questId); // Agovard
		qe.registerQuestNpc(799217).addOnTalkEvent(questId); // Renato
		qe.registerQuestNpc(799202).addOnTalkEvent(questId); // Ipses
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		QuestDialog dialog = env.getDialog();
		int targetId = env.getTargetId();

		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 799225) {
				switch (dialog) {
					case START_DIALOG:
						return sendQuestDialog(env, 1011);
					default: {
						return sendQuestStartDialog(env, 182214016, 1);
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.START) {
			switch (targetId) {
				case 799332: // Brontes
				{
					switch (dialog) {
						case START_DIALOG: {
							return sendQuestDialog(env, 1353);
						}
						case SELECT_ACTION_1353: {
							return sendQuestDialog(env, 1353);
						}
						case STEP_TO_1: {
							return defaultCloseDialog(env, 0, 1);
						}
					}
				}
				case 799217: // Pilipides
				{
					switch (dialog) {
						case START_DIALOG: {
							return sendQuestDialog(env, 1693);
						}
						case SELECT_ACTION_1694: {
							return sendQuestDialog(env, 1694);
						}
						case STEP_TO_2: {
							return defaultCloseDialog(env, 1, 2);
						}
					}
				}
				case 799202: // Drenia
				{
					switch (dialog) {
						case START_DIALOG: {
							return sendQuestDialog(env, 2375);
						}
						case SELECT_REWARD: {
							return defaultCloseDialog(env, 2, 3, true, true);
						}
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 799202) // Drenia
			{
				switch (env.getDialogId()) {
					case 1009: {
						return sendQuestDialog(env, 5);
					}
					default:
						return sendQuestEndDialog(env);
				}
			}
		}
		return false;
	}
}
