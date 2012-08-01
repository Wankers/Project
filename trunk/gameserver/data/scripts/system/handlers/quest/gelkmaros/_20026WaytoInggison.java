/*
 * This file is part of Aion Lightning <aion-core.net>.
 *
 *  Aion Lightning is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Aion Lightning is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Aion Lightning.  If not, see <http://www.gnu.org/licenses/>.
 */
package quest.gelkmaros;

import gameserver.model.gameobjects.Npc;
import gameserver.model.gameobjects.player.Player;
import gameserver.questEngine.handlers.QuestHandler;
import gameserver.questEngine.model.QuestDialog;
import gameserver.questEngine.model.QuestEnv;
import gameserver.questEngine.model.QuestState;
import gameserver.questEngine.model.QuestStatus;

/**
 * @author Gigi
 */
public class _20026WaytoInggison extends QuestHandler {

	private final static int questId = 20026;

	public _20026WaytoInggison() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerOnLevelUp(questId);
		int[] npcs = { 799364, 799365 };
		for (int npc : npcs)
			qe.registerQuestNpc(npc).addOnTalkEvent(questId);
	}

	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env);
	}

	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env, 20000, true);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null)
			return false;

		int var = qs.getQuestVarById(0);
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();

		if (qs.getStatus() == QuestStatus.START) {
			if (targetId == 799364) {
				switch (env.getDialog()) {
					case START_DIALOG:
						if (var == 0)
							return sendQuestDialog(env, 1011);
					case STEP_TO_1:
						return defaultCloseDialog(env, 0, 1); // 1
				}
			}
			else if (targetId == 799365) {
				switch (env.getDialog()) {
					case START_DIALOG:
						if (var == 1)
							return sendQuestDialog(env, 1352);
					case SET_REWARD:
						return defaultCloseDialog(env, 1, 1, true, true); // reward
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 799365) {
				if (env.getDialog() == QuestDialog.START_DIALOG)
					return sendQuestDialog(env, 10002);
				else {
					return sendQuestEndDialog(env);
				}
			}
		}
		return false;
	}
}