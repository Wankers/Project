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

import gameserver.model.gameobjects.player.Player;
import gameserver.questEngine.handlers.QuestHandler;
import gameserver.questEngine.model.QuestActionType;
import gameserver.questEngine.model.QuestDialog;
import gameserver.questEngine.model.QuestEnv;
import gameserver.questEngine.model.QuestState;
import gameserver.questEngine.model.QuestStatus;

/**
 * @author MrPoke
 * @reworked vlog
 */
public class ItemCollecting extends QuestHandler {

	private final int questId;
	private final int startNpcId;
	private final int startNpcId2;
	private final int actionItemId;
	private final int actionItem2Id;
	private final int actionItem3Id;
	private final int actionItem4Id;
	private final int endNpcId;
	private final int endNpcId2;
	private final int questMovie;

	public ItemCollecting(int questId, int startNpcId, int startNpcId2, int actionItemId, int actionItem2Id, int actionItem3Id,
		int actionItem4Id, int endNpcId, int endNpcId2, int questMovie) {
		super(questId);
		this.questId = questId;
		this.startNpcId = startNpcId;
		if (startNpcId2 != 0) {
			this.startNpcId2 = startNpcId2;
		}
		else {
			this.startNpcId2 = startNpcId;
		}
		this.actionItemId = actionItemId;
		this.actionItem2Id = actionItem2Id;
		this.actionItem3Id = actionItem3Id;
		this.actionItem4Id = actionItem4Id;
		if (endNpcId != 0) {
			this.endNpcId = endNpcId;
		}
		else {
			this.endNpcId = startNpcId;
		}
		if (endNpcId2 != 0) {
			this.endNpcId2 = endNpcId2;
		}
		else {
			this.endNpcId2 = this.startNpcId2;
		}
		this.questMovie = questMovie;
	}

	@Override
	public void register() {
		if (startNpcId != 0) {
			qe.registerQuestNpc(startNpcId).addOnQuestStart(questId);
			qe.registerQuestNpc(startNpcId).addOnTalkEvent(questId);
		}
		if (startNpcId2 != 0) {
			qe.registerQuestNpc(startNpcId2).addOnQuestStart(questId);
			qe.registerQuestNpc(startNpcId2).addOnTalkEvent(questId);
		}
		if (actionItemId != 0) {
			qe.registerQuestNpc(actionItemId).addOnTalkEvent(questId);
			qe.registerCanAct(questId, actionItemId);
		}
		if (actionItem2Id != 0) {
			qe.registerQuestNpc(actionItem2Id).addOnTalkEvent(questId);
			qe.registerCanAct(questId, actionItem2Id);
		}
		if (actionItem3Id != 0) {
			qe.registerQuestNpc(actionItem3Id).addOnTalkEvent(questId);
			qe.registerCanAct(questId, actionItem3Id);
		}
		if (actionItem4Id != 0) {
			qe.registerQuestNpc(actionItem4Id).addOnTalkEvent(questId);
			qe.registerCanAct(questId, actionItem4Id);
		}
		if (endNpcId != startNpcId) {
			qe.registerQuestNpc(endNpcId).addOnTalkEvent(questId);
		}
		if (endNpcId2 != startNpcId2) {
			qe.registerQuestNpc(endNpcId2).addOnTalkEvent(questId);
		}
	}

	@Override
	public boolean onCanAct(QuestEnv env, QuestActionType questEventType, Object... objects) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		return qs != null && qs.getStatus() == QuestStatus.START;
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		QuestDialog dialog = env.getDialog();
		int targetId = env.getTargetId();

		if (qs == null || qs.getStatus() == QuestStatus.NONE || qs.canRepeat()) {
			if (startNpcId == 0 || targetId == startNpcId || targetId == startNpcId2) {
				switch (dialog) {
					case START_DIALOG: {
						return sendQuestDialog(env, 1011);
					}
					case SELECT_ACTION_1012: {
						if (questMovie != 0) {
							playQuestMovie(env, questMovie);
						}
						return sendQuestDialog(env, 1012);
					}
					default: {
						return sendQuestStartDialog(env);
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (targetId == endNpcId || targetId == endNpcId2) {
				switch (dialog) {
					case START_DIALOG: {
						return sendQuestDialog(env, 2375);
					}
					case CHECK_COLLECTED_ITEMS: {
						return checkQuestItems(env, var, var, true, 5, 2716); // reward
					}
					case FINISH_DIALOG: {
						return sendQuestSelectionDialog(env);
					}
				}
			}
			else if (targetId != 0
				&& (targetId == actionItemId || targetId == actionItem2Id || targetId == actionItem3Id || targetId == actionItem4Id)) {
				return true; // looting
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == endNpcId || targetId == endNpcId2) {
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}
}
