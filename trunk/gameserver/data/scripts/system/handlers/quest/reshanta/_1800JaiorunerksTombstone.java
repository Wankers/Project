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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 * along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package quest.reshanta;

import gameserver.model.gameobjects.player.Player;
import gameserver.questEngine.handlers.QuestHandler;
import gameserver.questEngine.model.QuestDialog;
import gameserver.questEngine.model.QuestEnv;
import gameserver.questEngine.model.QuestState;
import gameserver.questEngine.model.QuestStatus;

/**
 * @author Gigi
 * @reworked vlog
 */
public class _1800JaiorunerksTombstone extends QuestHandler {

	private final static int questId = 1800;

	public _1800JaiorunerksTombstone() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(279016).addOnQuestStart(questId);
		qe.registerQuestNpc(279016).addOnTalkEvent(questId);
		qe.registerQuestNpc(730141).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		int targetId = env.getTargetId();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		QuestDialog dialog = env.getDialog();

		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 279016) { // Vindachinerk
				if (dialog == QuestDialog.START_DIALOG) {
					return sendQuestDialog(env, 1011);
				}
				else {
					return sendQuestStartDialog(env, 182202163, 1);
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			switch (targetId) {
				case 730141: { // Jaiorunerk's Tomb
					switch (dialog) {
						case START_DIALOG: {
							if (var == 0) {
								if (player.getInventory().getItemCountByItemId(182202163) > 0) {
									return sendQuestDialog(env, 1352);
								}
							}
						}
						case STEP_TO_1: {
							removeQuestItem(env, 182202163, 1);
							changeQuestStep(env, 0, 1, false);
							return closeDialogWindow(env);
						}
					}
					break;
				}
				case 279016: { // Vindachinerk
					switch (dialog) {
						case START_DIALOG: {
							if (var == 1) {
								return sendQuestDialog(env, 2375);
							}
						}
						case SELECT_REWARD: {
							changeQuestStep(env, 1, 1, true); // reward
							return sendQuestDialog(env, 5);
						}
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 279016) { // Vindachinerk
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}
}
