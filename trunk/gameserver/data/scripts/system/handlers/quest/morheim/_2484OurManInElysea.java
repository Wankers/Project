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
package quest.morheim;

import gameserver.model.gameobjects.Npc;
import gameserver.model.gameobjects.player.Player;
import gameserver.questEngine.handlers.QuestHandler;
import gameserver.questEngine.model.QuestDialog;
import gameserver.questEngine.model.QuestEnv;
import gameserver.questEngine.model.QuestState;
import gameserver.questEngine.model.QuestStatus;

/**
 * @author Mr.Poke remod by Nephis and quest helper team
 */
public class _2484OurManInElysea extends QuestHandler {

	private final static int questId = 2484;

	public _2484OurManInElysea() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(204407).addOnQuestStart(questId);
		qe.registerQuestNpc(204407).addOnTalkEvent(questId);
		qe.registerQuestNpc(700267).addOnTalkEvent(questId);
		qe.registerQuestNpc(203331).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 204407) {
				if (env.getDialog() == QuestDialog.START_DIALOG)
					return sendQuestDialog(env, 4762);
				else if (env.getDialogId() == 1002) {
					if (giveQuestItem(env, 182204205, 1))
						return sendQuestStartDialog(env);
					else
						return true;
				}
				else
					return sendQuestStartDialog(env);
			}
		}
		else if (qs != null && qs.getStatus() == QuestStatus.START) {
			switch (targetId) {
				case 700267:
					if (qs.getQuestVarById(0) == 0 && env.getDialog() == QuestDialog.USE_OBJECT) {
						qs.setQuestVarById(0, 1);
						updateQuestStatus(env);
						removeQuestItem(env, 182204205, 1);
					}
				case 203331: {
					if (qs.getQuestVarById(0) == 1) {
						if (env.getDialogId() == 18)
							return sendQuestDialog(env, 5);
						else if (env.getDialog() == QuestDialog.START_DIALOG) {
							qs.setStatus(QuestStatus.REWARD);
							updateQuestStatus(env);
							return sendQuestDialog(env, 5);
						}
						else
							return sendQuestEndDialog(env);
					}
				}
			}
		}
		else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203331)
				return sendQuestEndDialog(env);
		}
		return false;
	}
}