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
package quest.altgard;

import gameserver.model.gameobjects.player.Player;
import gameserver.questEngine.handlers.QuestHandler;
import gameserver.questEngine.model.QuestDialog;
import gameserver.questEngine.model.QuestEnv;
import gameserver.questEngine.model.QuestState;
import gameserver.questEngine.model.QuestStatus;

/**
 * @author Mr. Poke fix by Nephis and quest helper team.
 * @reworked vlog
 */
public class _2232TheBrokenHoneyJar extends QuestHandler {

	private final static int questId = 2232;

	public _2232TheBrokenHoneyJar() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(203613).addOnQuestStart(questId);
		qe.registerQuestNpc(203613).addOnTalkEvent(questId);
		qe.registerQuestNpc(203622).addOnTalkEvent(questId);
		qe.registerQuestNpc(700061).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
		int targetId = env.getTargetId();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		QuestDialog dialog = env.getDialog();

		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 203613) { // Gilungk
				if (dialog == QuestDialog.START_DIALOG) {
					return sendQuestDialog(env, 1011);
				}
				else {
					return sendQuestStartDialog(env);
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (targetId == 203613) { // Gilungk
				switch (dialog) {
					case START_DIALOG: {
						if (var == 1) {
							return sendQuestDialog(env, 2375);
						}
					}
					case CHECK_COLLECTED_ITEMS: {
						return checkQuestItems(env, 1, 1, true, 5, 2716); // reward
					}
					case FINISH_DIALOG: {
						return sendQuestSelectionDialog(env);
					}
				}
			}
			else if (targetId == 700061) { // Beehive
				if (dialog == QuestDialog.USE_OBJECT) {
					return true; // loot
				}
			}
			else if (targetId == 203622) { // Tatural
				if (dialog == QuestDialog.START_DIALOG) {
					if (var == 0) {
						return sendQuestDialog(env, 1352);
					}
				}
				else if (env.getDialog() == QuestDialog.STEP_TO_1) {
					return defaultCloseDialog(env, 0, 1); // 1
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203613) { // Gilungk
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}
}
