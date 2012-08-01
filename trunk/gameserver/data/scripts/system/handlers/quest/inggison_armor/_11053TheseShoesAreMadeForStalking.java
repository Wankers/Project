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
package quest.inggison_armor;

import gameserver.model.gameobjects.player.Player;
import gameserver.questEngine.handlers.QuestHandler;
import gameserver.questEngine.model.QuestDialog;
import gameserver.questEngine.model.QuestEnv;
import gameserver.questEngine.model.QuestState;
import gameserver.questEngine.model.QuestStatus;

/**
 * @author vlog
 * @modified Gigi
 */
public class _11053TheseShoesAreMadeForStalking extends QuestHandler {

	private final static int questId = 11053;

	public _11053TheseShoesAreMadeForStalking() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(799015).addOnQuestStart(questId);
		qe.registerQuestNpc(799015).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		QuestDialog dialog = env.getDialog();
		int targetId = env.getTargetId();
		if (qs == null || qs.canRepeat()) {
			if (targetId == 799015) {
				if (dialog == QuestDialog.START_DIALOG) {
					return sendQuestDialog(env, 1011);
				}
				else {
					return sendQuestStartDialog(env);
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.START) {
			if (targetId == 799015) {
				if (dialog == QuestDialog.START_DIALOG) {
					return sendQuestDialog(env, 2375);
				}
				else if (dialog == QuestDialog.CHECK_COLLECTED_ITEMS) {
					long itemCount = player.getInventory().getItemCountByItemId(182206838);
					if (player.getInventory().tryDecreaseKinah(50000) && itemCount > 29) {
						player.getInventory().decreaseByItemId(182206838, 30);
						changeQuestStep(env, 0, 0, true);
						return sendQuestDialog(env, 5);
					}
					else
						return sendQuestDialog(env, 2716);
				}
				else if (dialog == QuestDialog.FINISH_DIALOG) {
					return defaultCloseDialog(env, 0, 0);
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 799015)
				return sendQuestEndDialog(env);
		}
		return false;
	}
}
