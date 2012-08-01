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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 * along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
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
import gameserver.world.zone.ZoneName;

/**
 * @author Rhys2002
 * @modified & reworked Gigi, vlog
 */
public class _2053AMissingFather extends QuestHandler {

	private final static int questId = 2053;

	public _2053AMissingFather() {
		super(questId);
	}

	@Override
	public void register() {
		int[] npcs = { 204707, 204749, 204800, 700359, 730108 };
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerOnLevelUp(questId);
		qe.registerQuestItem(182204305, questId);
		qe.registerOnEnterZone(ZoneName.MALEK_MINE_220040000, questId);
		for (int npc : npcs) {
			qe.registerQuestNpc(npc).addOnTalkEvent(questId);
		}
	}

	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		QuestDialog dialog = env.getDialog();
		if (qs == null) {
			return false;
		}
		int var = qs.getQuestVarById(0);
		int targetId = env.getTargetId();

		if (qs.getStatus() == QuestStatus.START) {
			if (targetId == 204707) { // Mani
				switch (dialog) {
					case START_DIALOG: {
						if (var == 0) {
							return sendQuestDialog(env, 1011);
						}
						else if (var == 5) {
							return sendQuestDialog(env, 2716);
						}
					}
					case STEP_TO_1: {
						return defaultCloseDialog(env, 0, 1); // 1
					}
					case STEP_TO_6: {
						return defaultCloseDialog(env, 5, 6, 0, 0, 182204306, 1); // 6
					}
				}
			}
			else if (targetId == 204749) { // Paeru
				switch (dialog) {
					case START_DIALOG: {
						if (var == 1) {
							return sendQuestDialog(env, 1352);
						}
					}
					case STEP_TO_2: {
						return defaultCloseDialog(env, 1, 2, 182204305, 1, 0, 0); // 2
					}
				}
			}
			else if (targetId == 730108) { // Strahein's Liquor Bottle
				switch (dialog) {
					case USE_OBJECT: {
						if (var == 4) {
							return sendQuestDialog(env, 2375);
						}
					}
					case STEP_TO_5: {
						return defaultCloseDialog(env, 4, 5, 182204306, 1, 182204305, 1); // 5
					}
				}
			}
			else if (targetId == 204800) { // Hammel
				switch (dialog) {
					case START_DIALOG: {
						if (var == 6) {
							return sendQuestDialog(env, 3057);
						}
					}
					case STEP_TO_7: {
						return defaultCloseDialog(env, 6, 7); // 7
					}
				}
			}
			else if (targetId == 700359) { // Secret Port Entrance
				if (dialog == QuestDialog.USE_OBJECT) {
					if (var == 7) {
						if (player.getInventory().getItemCountByItemId(182204307) > 0) {
							return useQuestObject(env, 7, 7, true, 0, 0, 0, 182204307, 1, 0, false); // reward
						}
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 204707) { // Mani
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
			if (var == 2) {
				// TODO: readable text dialog
				return HandlerResult.fromBoolean(useQuestItem(env, item, 2, 3, false)); // 3
			}
		}
		return HandlerResult.FAILED;
	}

	@Override
	public boolean onEnterZoneEvent(QuestEnv env, ZoneName name) {
		Player player = env.getPlayer();
		if (player == null)
			return false;
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (var == 3) {
				changeQuestStep(env, 3, 4, false); // 4
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
		return defaultOnLvlUpEvent(env, 2500, true);
	}
}
