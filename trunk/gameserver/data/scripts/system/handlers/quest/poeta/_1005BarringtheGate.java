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

import gameserver.model.EmotionId;
import gameserver.model.EmotionType;
import gameserver.model.gameobjects.Npc;
import gameserver.model.gameobjects.player.Player;
import gameserver.network.aion.serverpackets.SM_EMOTION;
import gameserver.network.aion.serverpackets.SM_USE_OBJECT;
import gameserver.questEngine.handlers.QuestHandler;
import gameserver.questEngine.model.QuestDialog;
import gameserver.questEngine.model.QuestEnv;
import gameserver.questEngine.model.QuestState;
import gameserver.questEngine.model.QuestStatus;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.ThreadPoolManager;

/**
 * @author MrPoke
 */
public class _1005BarringtheGate extends QuestHandler {

	private final static int questId = 1005;

	public _1005BarringtheGate() {
		super(questId);
	}

	@Override
	public void register() {
		int[] talkNpcs = { 203067, 203081, 790001, 203085, 203086, 700080, 700081, 700082, 700083 };
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerOnLevelUp(questId);
		for (int id : talkNpcs)
			qe.registerQuestNpc(id).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null)
			return false;

		int var = qs.getQuestVarById(0);
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();

		if (qs.getStatus() == QuestStatus.START) {
			if (targetId == 203067) {
				switch (env.getDialog()) {
					case START_DIALOG:
						if (var == 0)
							return sendQuestDialog(env, 1011);
					case STEP_TO_1:
						if (var == 0) {
							qs.setQuestVarById(0, var + 1);
							updateQuestStatus(env);
							sendQuestSelectionDialog(env);
							return true;
						}
				}
			}
			else if (targetId == 203081) {
				switch (env.getDialog()) {
					case START_DIALOG:
						if (var == 1)
							return sendQuestDialog(env, 1352);
					case STEP_TO_2:
						if (var == 1) {
							qs.setQuestVarById(0, var + 1);
							updateQuestStatus(env);
							sendQuestSelectionDialog(env);
							return true;
						}
				}
			}
			else if (targetId == 790001) {
				switch (env.getDialog()) {
					case START_DIALOG:
						if (var == 2)
							return sendQuestDialog(env, 1693);
					case STEP_TO_3:
						if (var == 2) {
							qs.setQuestVarById(0, var + 1);
							updateQuestStatus(env);
							sendQuestSelectionDialog(env);
							return true;
						}
				}
			}
			else if (targetId == 203085) {
				switch (env.getDialog()) {
					case START_DIALOG:
						if (var == 3)
							return sendQuestDialog(env, 2034);
					case STEP_TO_4:
						if (var == 3) {
							qs.setQuestVarById(0, var + 1);
							updateQuestStatus(env);
							sendQuestSelectionDialog(env);
							return true;
						}
				}
			}
			else if (targetId == 203086) {
				switch (env.getDialog()) {
					case START_DIALOG:
						if (var == 4)
							return sendQuestDialog(env, 2375);
					case STEP_TO_5:
						if (var == 4) {
							qs.setQuestVarById(0, var + 1);
							updateQuestStatus(env);
							sendQuestSelectionDialog(env);
							return true;
						}
				}
			}
			else if (targetId == 700081) {
				if (var == 5) {
					destroy(6, env);
					return false;
				}
			}
			else if (targetId == 700082) {
				if (var == 6) {
					destroy(7, env);
					return false;
				}
			}
			else if (targetId == 700083) {
				if (var == 7) {
					destroy(8, env);
					return false;
				}
			}
			else if (targetId == 700080) {
				if (var == 8) {
					destroy(-1, env);
					return false;
				}
			}

		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203067) {
				if (env.getDialog() == QuestDialog.USE_OBJECT)
					return sendQuestDialog(env, 2716);
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}

	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		int[] quests = { 1100, 1001, 1002, 1003, 1004 };
		return defaultOnZoneMissionEndEvent(env, quests);
	}

	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		int[] quests = { 1100, 1001, 1002, 1003, 1004 };
		return defaultOnLvlUpEvent(env, quests, true);
	}

	private void destroy(final int var, final QuestEnv env) {
		final int targetObjectId = env.getVisibleObject().getObjectId();

		final Player player = env.getPlayer();
		PacketSendUtility.sendPacket(player, new SM_USE_OBJECT(player.getObjectId(), targetObjectId, 3000, 1));
		PacketSendUtility
			.broadcastPacket(player, new SM_EMOTION(player, EmotionType.NEUTRALMODE2, 0, targetObjectId), true);
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				if (player.getTarget() == null || player.getTarget().getObjectId() != targetObjectId)
					return;
				PacketSendUtility.sendPacket(player, new SM_USE_OBJECT(player.getObjectId(), targetObjectId, 3000, 0));
				PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.START_LOOT, 0, targetObjectId),
					true);
				sendEmotion(env, player, EmotionId.STAND, true);
				QuestState qs = player.getQuestStateList().getQuestState(questId);
				if (var != -1)
					qs.setQuestVarById(0, var);
				else {
					playQuestMovie(env, 21);
					qs.setStatus(QuestStatus.REWARD);
				}
				updateQuestStatus(env);
			}
		}, 3000);
	}
}
