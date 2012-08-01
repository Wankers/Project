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
 * @author Mr.Poke
 * @reworked vlog
 */
public class _2237AFertileField extends QuestHandler {

	private final static int questId = 2237;

	public _2237AFertileField() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(203629).addOnQuestStart(questId);
		qe.registerQuestNpc(203629).addOnTalkEvent(questId);
		qe.registerQuestNpc(700145).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		int targetId = env.getTargetId();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		QuestDialog dialog = env.getDialog();

		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 203629) { // Daike
				if (dialog == QuestDialog.START_DIALOG) {
					return sendQuestDialog(env, 1011);
				}
				else {
					return sendQuestStartDialog(env);
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.START) {
			switch (targetId) {
				case 700145: { // Fertilizer Sack
					if (dialog == QuestDialog.USE_OBJECT) {
						return true; // loot
					}
					break;
				}
				case 203629: { // Daike
					switch (dialog) {
						case START_DIALOG: {
							return sendQuestDialog(env, 2375);
						}
						case CHECK_COLLECTED_ITEMS: {
							return checkQuestItems(env, 0, 0, true, 5, 2716);
						}
						case FINISH_DIALOG: {
							return sendQuestSelectionDialog(env);
						}
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203629) { // Daike
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}
}
