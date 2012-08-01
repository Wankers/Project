/*
 *  This file is part of Zetta-Core Engine <http://www.zetta-core.org>.
 *
 *  Zetta-Core is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published
 *  by the Free Software Foundation, either version 3 of the License,
 *  or (at your option) any later version.
 *
 *  Zetta-Core is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a  copy  of the GNU General Public License
 *  along with Zetta-Core.  If not, see <http://www.gnu.org/licenses/>.
 */
package quest.esoterrace;

import gameserver.model.gameobjects.Item;
import gameserver.model.gameobjects.player.Player;
import gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import gameserver.questEngine.handlers.HandlerResult;
import gameserver.questEngine.handlers.QuestHandler;
import gameserver.questEngine.model.QuestDialog;
import gameserver.questEngine.model.QuestEnv;
import gameserver.questEngine.model.QuestState;
import gameserver.questEngine.model.QuestStatus;
import gameserver.services.QuestService;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.ThreadPoolManager;

/**
 * @author Vincas
 *
 */
public class _28405KexkrasPast extends QuestHandler
{
	public static final int questId = 28405;
	public static final int npcLuigur = 799558,
		npcRelyt = 799557;

	public _28405KexkrasPast()
	{
		super(questId);
	}

	@Override
	public void register()
	{
		qe.registerQuestNpc(npcLuigur).addOnTalkEvent(questId);
		qe.registerQuestNpc(npcRelyt).addOnTalkEvent(questId);
		qe.registerQuestItem(182215014, questId);
	}

	public boolean onDialogEvent(QuestEnv env)
	{
		Player player = env.getPlayer();

		if (targetId == 0) 
		{
			if(env.getDialog() == QuestDialog.ACCEPT_QUEST)
			{
				QuestService.startQuest(env);
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(0, 0));
				return true;
			}
		}

		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if(qs == null)
			return false;

		if (qs.getStatus() == QuestStatus.START)
		{
			switch (env.getTargetId())
			{
				case npcLuigur:
					if (qs.getQuestVarById(0) == 0)
					{
						if(env.getDialog() == QuestDialog.START_DIALOG)
							return sendQuestDialog(env, 1352);
						else if(env.getDialog() == QuestDialog.STEP_TO_1)
							return defaultCloseDialog(env, 0, 1,182215025,1,182215014,1);
					}
				case npcRelyt:
					if (qs.getQuestVarById(0) == 1)
					{
						if(env.getDialog() == QuestDialog.START_DIALOG)
							return sendQuestDialog(env, 2375);
						else if(env.getDialog() == QuestDialog.SELECT_REWARD)
							removeQuestItem(env, 182215025, 1);
						return defaultCloseDialog(env, 1, 2, true, true);
					}
			}
		}
		return sendQuestRewardDialog(env, npcRelyt, 0);
	}

	@Override
	public HandlerResult onItemUseEvent(final QuestEnv env, Item item) 
	{
		final Player player = env.getPlayer();
		final int id = item.getItemTemplate().getTemplateId();
		final int itemObjId = item.getObjectId();

		if (id != 182215014)
			return HandlerResult.FAILED;
		PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), itemObjId, id, 3000, 0,
			0), true);
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), itemObjId, id, 0,
					1, 0), true);
				sendQuestDialog(env, 4);
			}
		}, 3000);
		return HandlerResult.SUCCESS;
	}
}
