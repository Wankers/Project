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
package quest.gelkmaros_armor;

import gameserver.model.gameobjects.player.Player;
import gameserver.questEngine.handlers.QuestHandler;
import gameserver.questEngine.model.QuestDialog;
import gameserver.questEngine.model.QuestEnv;
import gameserver.questEngine.model.QuestState;
import gameserver.questEngine.model.QuestStatus;


/**
 * @author zhkchi
 *
 */
public class _21053DramataDrama extends QuestHandler {

	private final static int questId = 21053;

	public _21053DramataDrama() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(799320).addOnQuestStart(questId);
		qe.registerQuestNpc(799320).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		QuestDialog dialog = env.getDialog();
		int targetId = env.getTargetId();
		
		if(qs == null || qs.getStatus() == QuestStatus.NONE || qs.canRepeat()){
			if (targetId == 799320) {
				switch (dialog) {
					case START_DIALOG:
						return sendQuestDialog(env, 1011);
					case SELECT_ACTION_1012: {
						return sendQuestDialog(env, 1012);
					}
					case ASK_ACCEPTION: {
						return sendQuestDialog(env, 4);
					}
					case ACCEPT_QUEST: {
						return sendQuestStartDialog(env);
					}
					case REFUSE_QUEST: {
						return sendQuestDialog(env, 1004);
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (targetId == 799320) {
				switch (dialog) {
					case START_DIALOG: {
						return sendQuestDialog(env, 2375);
					}
					case SELECT_ACTION_2034: {
						return sendQuestDialog(env, 2034);
					}
					case CHECK_COLLECTED_ITEMS: {
						return checkQuestItems(env, var, var, true, 5, 2716);
					}
					case FINISH_DIALOG: {
						return sendQuestSelectionDialog(env);
					}
					
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 799320) {
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}
}
