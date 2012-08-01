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
package quest.sanctum;

import gameserver.model.gameobjects.player.Player;
import gameserver.questEngine.handlers.QuestHandler;
import gameserver.questEngine.model.QuestDialog;
import gameserver.questEngine.model.QuestEnv;
import gameserver.questEngine.model.QuestState;
import gameserver.questEngine.model.QuestStatus;


/**
 * @author zhkchi
 *
 */
public class _1938BlackCloudFakery extends QuestHandler
{
	private final static int	questId	= 1938;
	private final static int[]	npcs = {203703, 279001, 279008};
	
	public _1938BlackCloudFakery()
	{
		super(questId);
	}

	@Override
	public void register()
	{
		qe.registerQuestNpc(203703).addOnQuestStart(questId);
		for(int npc: npcs)
			qe.registerQuestNpc(npc).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env)
	{
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
		QuestDialog dialog = env.getDialog();
		
		if(sendQuestNoneDialog(env, 203703))
			return true;

		if(qs == null)
			return false;

		int var = qs.getQuestVarById(0);
		
		if(qs.getStatus() == QuestStatus.START)
		{
			switch(targetId)
			{
				case 279001:
					switch(dialog)
					{
						case START_DIALOG:
							if(var == 0)
								return sendQuestDialog(env, 1352);
						case STEP_TO_1:
							return defaultCloseDialog(env, 0, 1);
					}
					break;
				case 279008:
					switch(dialog)
					{
						case START_DIALOG:
							if(var == 1)
								return sendQuestDialog(env, 1693);
						case STEP_TO_2:
							return defaultCloseDialog(env, 1, 2, true, false);
					}
					break;
			}
		}
		return sendQuestRewardDialog(env, 203703, 2375);
	}
}