/*
 *  This file is part of Zetta-Core Engine <http://www.zetta-core.org>.
 *
 *  Zetta-Core is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published
 *  by the Free Software Foundation, either version 3 of the License,
 *  or (at your option) any later version.
 *
 *  Zetta-Core is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a  copy  of the GNU General Public License
 *  along with Zetta-Core.  If not, see <http://www.gnu.org/licenses/>.
 */
package quest.brusthonin;

import gameserver.model.gameobjects.Npc;
import gameserver.model.gameobjects.player.Player;
import gameserver.questEngine.handlers.QuestHandler;
import gameserver.questEngine.model.QuestDialog;
import gameserver.questEngine.model.QuestEnv;
import gameserver.questEngine.model.QuestState;
import gameserver.questEngine.model.QuestStatus;

/**
 * @author Nephis
 */
public class _4004TheSeedsOfHope extends QuestHandler {

	private final static int questId = 4004;

	public _4004TheSeedsOfHope() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(205128).addOnQuestStart(questId); // Randet
		qe.registerQuestNpc(205128).addOnTalkEvent(questId); // Randet
		qe.registerQuestNpc(700340).addOnTalkEvent(questId); // Earth Mound
	}

	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);

		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 205128) { // Randet
				if (env.getDialog() == QuestDialog.START_DIALOG)
					return sendQuestDialog(env, 4762);
				else
					return sendQuestStartDialog(env);
			}
		}
		else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 205128) {
				if (env.getDialog() == QuestDialog.USE_OBJECT)
					return sendQuestDialog(env, 10002);
				else
					return sendQuestEndDialog(env);
			}
		}
		else if (qs != null && qs.getStatus() == QuestStatus.START) {
			final int var = qs.getQuestVarById(0);
			switch (targetId) {
				case 700340: { // Earth Mound
					if (qs != null && env.getDialog() == QuestDialog.USE_OBJECT) {
						if (var < 4) {
							return useQuestObject(env, var, var + 1, false, true);
						}
						else if (var == 4) {
							return useQuestObject(env, 4, 4, true, true); // reward
						}
					}
				}
			}
		}
		return false;
	}
}
