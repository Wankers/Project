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
package quest.altgard;

import gameserver.model.gameobjects.Npc;
import gameserver.model.gameobjects.player.Player;
import gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import gameserver.questEngine.handlers.QuestHandler;
import gameserver.questEngine.model.QuestDialog;
import gameserver.questEngine.model.QuestEnv;
import gameserver.questEngine.model.QuestState;
import gameserver.questEngine.model.QuestStatus;
import gameserver.utils.PacketSendUtility;

/**
 * @author Ritsu
 * 
 */
public class _2247TheGergersDisguise extends QuestHandler
{

	private final static int	questId	= 2247;

	public _2247TheGergersDisguise()

	{
		super(questId);
	}

	@Override
	public void register()
	{
		qe.registerQuestNpc(203645).addOnQuestStart(questId);
		qe.registerQuestNpc(203645).addOnTalkEvent(questId);
		qe.registerQuestNpc(798039).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env)
	{
		final Player player = env.getPlayer();
		int targetId = 0;
		if(env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		QuestDialog dialog = env.getDialog();
		
		if(qs == null || qs.getStatus() == QuestStatus.NONE){
			if(targetId == 203645){
				if(dialog == QuestDialog.START_DIALOG)
					return sendQuestDialog(env, 1011);
				else
					return sendQuestStartDialog(env);
			}
		}
		else if (qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if(targetId == 798039){
				switch (dialog){
					case START_DIALOG:
						return sendQuestDialog(env, 1352);
					case STEP_TO_1:
						if(var == 0){
							qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
							updateQuestStatus(env);
							if (!giveQuestItem(env, 182203231, 1))
								return true;
							PacketSendUtility
								.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
							return true;
						}
				}
			}
			if(targetId == 203645){
				switch (dialog){
					case START_DIALOG:
						if(var == 1){
							qs.setQuestVar(2);
							qs.setStatus(QuestStatus.REWARD);
							updateQuestStatus(env);
							return sendQuestDialog(env, 2375);
						}
					case SELECT_REWARD:
						if(var == 2){
							removeQuestItem(env, 182203231, 1);
							return sendQuestEndDialog(env);
						}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD){
			if(targetId == 203645){
				removeQuestItem(env, 182203231, 1);
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}
}

