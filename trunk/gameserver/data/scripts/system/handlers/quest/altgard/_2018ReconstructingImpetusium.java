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
import gameserver.services.QuestService;

/**
 * Talk with Gulkalla (203649). Destroy Hero Spirits (210588, 210722) (3). Go back to Gulkalla. Bring Umkata's Three
 * Tokens (700097) to Umkata's Grave (700098), summon the spirit of Umkata (210752, spawn), and destroy it. Report back
 * to Gulkalla.
 * 
 * @author Mr. Poke
 * @modified Gigi
 * @reworked vlog
 */
public class _2018ReconstructingImpetusium extends QuestHandler {

	private final static int questId = 2018;

	public _2018ReconstructingImpetusium() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerOnLevelUp(questId);
		qe.registerQuestNpc(203649).addOnTalkEvent(questId);
		qe.registerQuestNpc(700097).addOnTalkEvent(questId);
		qe.registerQuestNpc(700098).addOnTalkEvent(questId);
		qe.registerQuestNpc(210588).addOnKillEvent(questId);
		qe.registerQuestNpc(210722).addOnKillEvent(questId);
		qe.registerQuestNpc(210752).addOnKillEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null)
			return false;
		int var = qs.getQuestVarById(0);
		int targetId = env.getTargetId();

		if (qs.getStatus() == QuestStatus.START) {
			switch (targetId) {
				case 203649: { // Gulkalla
					switch (env.getDialog()) {
						case START_DIALOG: {
							if (var == 0) {
								return sendQuestDialog(env, 1011);
							}
							else if (var == 4) {
								return sendQuestDialog(env, 1352);
							}
							else if (var == 7) {
								return sendQuestDialog(env, 1693);
							}
						}
						case STEP_TO_1: {
							return defaultCloseDialog(env, 0, 1); // 1
						}
						case STEP_TO_2: {
							return defaultCloseDialog(env, 4, 5); // 5
						}
						case SELECT_REWARD: {
							changeQuestStep(env, 7, 7, true); // reward
							return sendQuestDialog(env, 5);
						}
					}
					break;
				}
				case 700097: { // Umkata's Jewel Box
					if (env.getDialog() == QuestDialog.USE_OBJECT && var == 5) {
						return true; // loot
					}
					break;
				}
				case 700098: { // Umkata's Grave
					switch (env.getDialog()) {
						case USE_OBJECT: {
							if (var == 5) {
								return sendQuestDialog(env, 2034);
							}
						}
						case CHECK_COLLECTED_ITEMS: {
							if (QuestService.collectItemCheck(env, false)) { // don't remove yet
								QuestService.addNewSpawn(220030000, player.getInstanceId(), 210752, 2889.9834f, 1741.3108f, 254.75f,
									(byte) 0);
								return closeDialogWindow(env);
							}
							else {
								return sendQuestDialog(env, 2120);
							}
						}
						case FINISH_DIALOG: {
							return closeDialogWindow(env);
						}
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203649) { // Gulkalla
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}

	@Override
	public boolean onKillEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null || qs.getStatus() != QuestStatus.START)
			return false;
		int var = qs.getQuestVarById(0);
		if (var >= 1 && var < 4) {
			int[] npcs = { 210588, 210722 };
			return defaultOnKillEvent(env, npcs, var, var + 1); // 2 - 4
		}
		else if (var == 5) {
			if (env.getTargetId() == 210752) {
				qs.setQuestVar(7); // 7
				updateQuestStatus(env);
				QuestService.collectItemCheck(env, true); // now remove collected items
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env);
	}

	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env, 2200, true);
	}
}
