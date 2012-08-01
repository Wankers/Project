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
package gameserver.questEngine.handlers.template;

import javolution.util.FastMap;

import gameserver.model.gameobjects.player.Player;
import gameserver.questEngine.handlers.QuestHandler;
import gameserver.questEngine.handlers.models.QuestSkillData;
import gameserver.questEngine.model.QuestDialog;
import gameserver.questEngine.model.QuestEnv;
import gameserver.questEngine.model.QuestState;
import gameserver.questEngine.model.QuestStatus;

/**
 * @author vlog
 */
public class SkillUse extends QuestHandler {

	private final int questId;
	private final int startNpc;
	private final int endNpc;
	private final FastMap<Integer, QuestSkillData> qsd;

	public SkillUse(int questId, int startNpc, int endNpc, FastMap<Integer, QuestSkillData> qsd) {
		super(questId);
		this.questId = questId;
		this.startNpc = startNpc;
		if (endNpc != 0) {
			this.endNpc = endNpc;
		}
		else {
			this.endNpc = startNpc;
		}
		this.qsd = qsd;
	}

	@Override
	public void register() {
		qe.registerQuestNpc(startNpc).addOnQuestStart(questId);
		qe.registerQuestNpc(startNpc).addOnTalkEvent(questId);
		if (endNpc != startNpc) {
			qe.registerQuestNpc(endNpc).addOnTalkEvent(questId);
		}
		for (int skillId : qsd.keySet()) {
			qe.registerQuestSkill(skillId, questId);
		}
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		QuestDialog dialog = env.getDialog();
		int targetId = env.getTargetId();

		if (qs == null || qs.getStatus() == QuestStatus.NONE || qs.canRepeat()) {
			if (targetId == startNpc) {
				if (dialog == QuestDialog.START_DIALOG) {
					return sendQuestDialog(env, 4762);
				}
				else {
					return sendQuestStartDialog(env);
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (targetId == endNpc) {
				if (dialog == QuestDialog.START_DIALOG) {
					return sendQuestDialog(env, 10002);
				}
				else if (dialog == QuestDialog.SELECT_REWARD) {
					changeQuestStep(env, var, var, true); // reward
					return sendQuestDialog(env, 5);
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == endNpc) {
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}

	@Override
	public boolean onUseSkillEvent(QuestEnv env, int skillId) {
		return defaultOnUseSkillEvent(env, qsd.get(skillId).getStartVar(), qsd.get(skillId).getEndVar(),
			qsd.get(skillId).getVarNum());
	}
}
