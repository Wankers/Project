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
package quest.poeta;

import gameserver.model.gameobjects.Npc;
import gameserver.model.gameobjects.player.Player;
import gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import gameserver.questEngine.handlers.QuestHandler;
import gameserver.questEngine.model.QuestEnv;
import gameserver.questEngine.model.QuestState;
import gameserver.questEngine.model.QuestStatus;
import gameserver.utils.PacketSendUtility;

/**
 * @author MrPoke
 */
public class _1001TheKerubThreat extends QuestHandler {

	private final static int questId = 1001;

	public _1001TheKerubThreat() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(210670).addOnKillEvent(questId);
		qe.registerQuestNpc(203071).addOnTalkEvent(questId);
		qe.registerQuestNpc(203067).addOnTalkEvent(questId);
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerOnLevelUp(questId);
	}

	@Override
	public boolean onKillEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null)
			return false;

		int var = qs.getQuestVarById(0);
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();

		if (qs.getStatus() != QuestStatus.START)
			return false;
		if (targetId == 210670) {
			if (var > 0 && var < 6) {
				qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
				updateQuestStatus(env);
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
		return defaultOnLvlUpEvent(env, 1100, true);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null)
			return false;

		int var = qs.getQuestVarById(0);
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();

		if (qs.getStatus() == QuestStatus.START) {
			if (targetId == 203071) {
				switch (env.getDialog()) {
					case SELECT_ACTION_1012:
						playQuestMovie(env, 15);
						return false;
					case START_DIALOG:
						if (var == 0)
							return sendQuestDialog(env, 1011);
						else if (var == 6)
							return sendQuestDialog(env, 1352);
						else if (var == 7)
							return sendQuestDialog(env, 1693);
						return false;
					case STEP_TO_3:
					case CHECK_COLLECTED_ITEMS:
						if (var == 7) {
							long itemCount = player.getInventory().getItemCountByItemId(182200001);
							if (itemCount >= 5) {
								if (env.getDialogId() == 34) {
									return sendQuestDialog(env, 1694);
								}
								else {
									removeQuestItem(env, 182200001, itemCount);
									qs.setQuestVarById(0, var + 1);
									qs.setStatus(QuestStatus.REWARD);
									updateQuestStatus(env);
									PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
									return true;
								}
							}
							else
								return sendQuestDialog(env, 1779);
						}
						return true;
					case STEP_TO_1:
					case STEP_TO_2:
						if (var == 0 || var == 6) {
							qs.setQuestVarById(0, var + 1);
							updateQuestStatus(env);
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
						}
						return true;
					default:
						return false;
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203067) {
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}
}
