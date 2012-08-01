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
package quest.beluslan;

import gameserver.model.gameobjects.Item;
import gameserver.model.gameobjects.player.Player;
import gameserver.questEngine.handlers.HandlerResult;
import gameserver.questEngine.handlers.QuestHandler;
import gameserver.questEngine.model.QuestDialog;
import gameserver.questEngine.model.QuestEnv;
import gameserver.questEngine.model.QuestState;
import gameserver.questEngine.model.QuestStatus;
import gameserver.services.QuestService;
import gameserver.world.zone.ZoneName;

/**
 * Talk with Hod (204701).<br>
 * Talk with Gwendolin (204785).<br>
 * Talk with Hisui (278003).<br>
 * Talk with Glati (278088) (182204318).<br>
 * Go to Leibo Island (400010001), gather Aether, and bring it (182204319) to Gwendolin.<br>
 * Destroy Field Suppressors around the Observatory (700290) (3).<br>
 * Repair the Aetheric Field Maintaining Device (700293).<br>
 * Report to Hod.<br>
 * 
 * @author kecimis
 * @reworked vlog
 */
public class _2060RestoringBeluslanObservatory extends QuestHandler {

	private final static int questId = 2060;

	public _2060RestoringBeluslanObservatory() {
		super(questId);
	}

	@Override
	public void register() {
		int[] npcs = { 204701, 204785, 278003, 278088, 700293 };
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerOnLevelUp(questId);
		qe.registerQuestItem(182204318, questId);
		qe.registerQuestNpc(700290).addOnKillEvent(questId);
		for (int npc : npcs) {
			qe.registerQuestNpc(npc).addOnTalkEvent(questId);
		}
	}

	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env);
	}

	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env, 2500, true);
	}

	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null)
			return false;
		int var = qs.getQuestVarById(0);
		QuestDialog dialog = env.getDialog();
		int targetId = env.getTargetId();

		if (qs.getStatus() == QuestStatus.START) {
			switch (targetId) {
				case 204701: { // Hod
					switch (dialog) {
						case START_DIALOG: {
							if (var == 0) {
								return sendQuestDialog(env, 1011);
							}
						}
						case STEP_TO_1: {
							return defaultCloseDialog(env, 0, 1); // 1
						}
					}
					break;
				}
				case 204785: { // Gwendolin
					switch (dialog) {
						case START_DIALOG: {
							if (var == 1) {
								return sendQuestDialog(env, 1352);
							}
							else if (var == 4) {
								if (QuestService.collectItemCheck(env, false)) {
									return sendQuestDialog(env, 2375);
								}
								else {
									giveQuestItem(env, 182204318, 1); // give another bottle
									return sendQuestDialog(env, 2461);
								}
							}
						}
						case STEP_TO_2: {
							return defaultCloseDialog(env, 1, 2); // 2
						}
						case STEP_TO_5: {
							return defaultCloseDialog(env, 4, 5, 0, 0, 182204319, 1); // 5
						}
						case FINISH_DIALOG: {
							if (var == 4) {
								return sendQuestSelectionDialog(env);
							}
						}
					}
					break;
				}
				case 278003: { // Hisui
					switch (dialog) {
						case START_DIALOG: {
							if (var == 2) {
								return sendQuestDialog(env, 1693);
							}
						}
						case STEP_TO_3: {
							return defaultCloseDialog(env, 2, 3); // 3
						}
					}
					break;
				}
				case 278088: { // Glati
					switch (dialog) {
						case START_DIALOG: {
							if (var == 3) {
								return sendQuestDialog(env, 2034);
							}
						}
						case STEP_TO_4: {
							return defaultCloseDialog(env, 3, 4, 182204318, 1, 0, 0); // 4
						}
					}
					break;
				}
				case 700293: { // Aetheric Field Maintaining Device
					if (dialog == QuestDialog.USE_OBJECT) {
						if (var == 8) {
							return useQuestObject(env, 8, 8, true, 0); // reward
						}
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 204701) { // Hod
				if (env.getDialog() == QuestDialog.USE_OBJECT) {
					return sendQuestDialog(env, 10002);
				}
				else {
					return sendQuestEndDialog(env);
				}
			}
		}
		return false;
	}

	@Override
	public HandlerResult onItemUseEvent(final QuestEnv env, Item item) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (var == 4) {
				if (player.isInsideZone(ZoneName.AB1_ITEMUSEAREA_Q2060)) {
					return HandlerResult.fromBoolean(useQuestItem(env, item, 4, 4, false, 182204319, 1, 0)); // 4 + aether bottle
				}
			}
		}
		return HandlerResult.FAILED;
	}

	@Override
	public boolean onKillEvent(QuestEnv env) {
		return defaultOnKillEvent(env, 700290, 5, 8); // 6, 7, 8
	}
}
