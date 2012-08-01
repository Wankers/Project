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
import gameserver.model.gameobjects.Npc;
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
 * @author Ritsu
 *
 */
public class _2533BeritrasCurse extends QuestHandler
{

	private final static int   questId   = 2533;

	public _2533BeritrasCurse()
	{
		super(questId);
	}

	@Override
	public void register()
	{
		qe.registerQuestNpc(204801).addOnQuestStart(questId); //Gigrite
		qe.registerQuestNpc(204801).addOnTalkEvent(questId);
		qe.registerQuestItem(182204425, questId);//Empty Durable Potion Bottle
		qe.registerOnQuestTimerEnd(questId);
	}

	@Override
	public HandlerResult onItemUseEvent(final QuestEnv env, Item item) 
	{
		
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) 
		{
			if (player.isInsideZone(ZoneName.BERITRAS_WEAPON_220040000)){
				QuestService.questTimerStart(env, 300);
				return HandlerResult.fromBoolean(useQuestItem(env, item, 0, 1, false, 182204426, 1, 0));
			}
		}
		return HandlerResult.SUCCESS; // ??
	}

	@Override
	public boolean onDialogEvent(QuestEnv env)
	{
		final Player player = env.getPlayer();
		int targetId = 0;
		if(env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		QuestDialog dialog = env.getDialog();
		
		if(qs == null || qs.getStatus() == QuestStatus.NONE){
			if (targetId == 204801){
				if (dialog == QuestDialog.START_DIALOG)
					return sendQuestDialog(env, 4762);
				else if (dialog == QuestDialog.ACCEPT_QUEST){
					if (!giveQuestItem(env, 182204425, 1))
						return true;
					return sendQuestStartDialog(env);
				}
				else
					return sendQuestStartDialog(env);
			}
		}
		else if (qs.getStatus() == QuestStatus.START){
			int var = qs.getQuestVarById(0);
			if(targetId == 204801){
				switch (dialog){
					case START_DIALOG:
						if (var == 1){
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						return sendQuestDialog(env, 1352);
						}
					case SELECT_REWARD:{
						QuestService.questTimerEnd(env);
						return sendQuestDialog(env, 5);
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD){
			if (targetId == 204801){
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}
	
	@Override
	public boolean onQuestTimerEndEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			removeQuestItem(env, 182204426, 1);
			QuestService.abandonQuest(player, questId);
			player.getController().updateNearbyQuests();
			return true;
		}
		return false;
	}
}
