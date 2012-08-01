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
import gameserver.questEngine.model.QuestDialog;
import gameserver.questEngine.model.QuestEnv;
import gameserver.questEngine.model.QuestState;
import gameserver.questEngine.model.QuestStatus;
import gameserver.services.QuestService;
import gameserver.utils.PacketSendUtility;

/**
 * @author MrPoke
 */
public class _1111InsomniaMedicine extends QuestHandler {

	private final static int questId = 1111;

	public _1111InsomniaMedicine() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(203075).addOnQuestStart(questId);
		qe.registerQuestNpc(203075).addOnTalkEvent(questId);
		qe.registerQuestNpc(203061).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (targetId == 203075) {
			if (qs == null) {
				if (env.getDialog() == QuestDialog.START_DIALOG)
					return sendQuestDialog(env, 1011);
				else
					return sendQuestStartDialog(env);
			}
			else if (qs.getStatus() == QuestStatus.REWARD) {
				if (env.getDialog() == QuestDialog.USE_OBJECT) {
					if (qs.getQuestVarById(0) == 2) {
						removeQuestItem(env, 182200222, 1);
						return sendQuestDialog(env, 2375);
					}
					else if (qs.getQuestVarById(0) == 3) {
						removeQuestItem(env, 182200221, 1);
						return sendQuestDialog(env, 2716);
					}
					return false;
				}
				else if (env.getDialogId() == 1009)
					return sendQuestDialog(env, qs.getQuestVarById(0) + 3);
				else if (env.getDialogId() == 18) {
					QuestService.finishQuest(env, qs.getQuestVarById(0) - 2);
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
					return true;
				}
			}
		}
		else if (targetId == 203061) {
			if (env.getDialog() == QuestDialog.START_DIALOG) {
				if (qs.getQuestVarById(0) == 0)
					return sendQuestDialog(env, 1352);
				else if (qs.getQuestVarById(0) == 1)
					return sendQuestDialog(env, 1353);
				return false;
			}
			else if (env.getDialogId() == 34) {
				if (QuestService.collectItemCheck(env, true)) {
					qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
					updateQuestStatus(env);
					return sendQuestDialog(env, 1353);
				}
				else
					return sendQuestDialog(env, 1693);
			}
			else if (env.getDialog() == QuestDialog.STEP_TO_1 && qs.getStatus() != QuestStatus.COMPLETE
				&& qs.getStatus() != QuestStatus.NONE) {
				if (!giveQuestItem(env, 182200222, 1))
					return true;
				qs.setQuestVarById(0, 2);
				qs.setStatus(QuestStatus.REWARD);
				updateQuestStatus(env);
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
				return true;
			}
			else if (env.getDialog() == QuestDialog.STEP_TO_2 && qs.getStatus() != QuestStatus.COMPLETE
				&& qs.getStatus() != QuestStatus.NONE) {
				if (!giveQuestItem(env, 182200221, 1))
					return true;
				qs.setQuestVarById(0, 3);
				qs.setStatus(QuestStatus.REWARD);
				updateQuestStatus(env);
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
				return true;
			}
		}
		return false;
	}
}
