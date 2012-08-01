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

import gameserver.model.gameobjects.Npc;
import gameserver.model.gameobjects.player.Player;
import gameserver.questEngine.handlers.QuestHandler;
import gameserver.questEngine.model.QuestDialog;
import gameserver.questEngine.model.QuestEnv;
import gameserver.questEngine.model.QuestState;
import gameserver.questEngine.model.QuestStatus;
import gameserver.services.QuestService;

/**
 * Escort Groken (203608) to the sailboat (700178). Talk with Manir (203607).
 * 
 * @author Mr. Poke
 * @reworked vlog
 */
public class _2290GrokensEscape extends QuestHandler {

	private final static int questId = 2290;

	public _2290GrokensEscape() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(203608).addOnQuestStart(questId);
		qe.registerQuestNpc(203608).addOnTalkEvent(questId);
		qe.registerQuestNpc(700178).addOnTalkEvent(questId);
		qe.registerQuestNpc(203607).addOnTalkEvent(questId);
		qe.registerQuestNpc(203608).addOnLostTargetEvent(questId);
		qe.registerQuestNpc(203608).addOnReachTargetEvent(questId);
	}

	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);

		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 203608) { // Groken
				if (env.getDialog() == QuestDialog.START_DIALOG)
					return sendQuestDialog(env, 1011);
				if (env.getDialogId() == 1007)
					return sendQuestDialog(env, 4);
				if (env.getDialogId() == 1002)
					return sendQuestDialog(env, 1003);
				if (env.getDialogId() == 1003)
					return sendQuestDialog(env, 1004);
				if (env.getDialogId() == 1008)
					return sendQuestSelectionDialog(env);
				if (env.getDialogId() == 1012) {
					if (QuestService.startQuest(env)) {
						return defaultStartFollowEvent(env, 700178, 0, 1); // 1
					}
				}
				else
					return sendQuestStartDialog(env);
			}
		}
		else if (qs.getStatus() == QuestStatus.START) {
			if (targetId == 203608) { // Groken
				if (env.getDialog() == QuestDialog.START_DIALOG && qs.getQuestVarById(0) == 0) {
					return defaultStartFollowEvent(env, 700178, 0, 1); // 1
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203607) { // Manir
				if (env.getDialog() == QuestDialog.START_DIALOG)
					return sendQuestDialog(env, 1693);
				else
					return sendQuestEndDialog(env);
			}
		}
		return false;
	}

	@Override
	public boolean onNpcReachTargetEvent(QuestEnv env) {
		playQuestMovie(env, 69);
		return defaultFollowEndEvent(env, 1, 1, true); // reward
	}

	@Override
	public boolean onNpcLostTargetEvent(QuestEnv env) {
		return defaultFollowEndEvent(env, 1, 0, false); // 0
	}
}
