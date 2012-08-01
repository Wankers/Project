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
package gameserver.questEngine.handlers.template;

import gameserver.model.gameobjects.player.Player;
import gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import gameserver.questEngine.handlers.QuestHandler;
import gameserver.questEngine.handlers.models.WorkOrdersData;
import gameserver.questEngine.model.QuestDialog;
import gameserver.questEngine.model.QuestEnv;
import gameserver.questEngine.model.QuestState;
import gameserver.questEngine.model.QuestStatus;
import gameserver.services.QuestService;
import gameserver.services.RecipeService;
import gameserver.services.item.ItemService;
import gameserver.utils.PacketSendUtility;

/**
 * @author Mr. Poke
 */
public class WorkOrders extends QuestHandler {

	private final WorkOrdersData workOrdersData;

	/**
	 * @param questId
	 */
	public WorkOrders(WorkOrdersData workOrdersData) {
		super(workOrdersData.getId());
		this.workOrdersData = workOrdersData;
	}

	@Override
	public void register() {
		qe.registerQuestNpc(workOrdersData.getStartNpcId()).addOnQuestStart(workOrdersData.getId());
		qe.registerQuestNpc(workOrdersData.getStartNpcId()).addOnTalkEvent(workOrdersData.getId());
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		int targetId = env.getTargetId();
		if (targetId == workOrdersData.getStartNpcId()) {
			QuestState qs = player.getQuestStateList().getQuestState(workOrdersData.getId());
			if (qs == null || qs.getStatus() == QuestStatus.NONE || qs.canRepeat()) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						return sendQuestDialog(env, 4);
					}
					case ACCEPT_QUEST: {
						if (RecipeService.validateNewRecipe(player, workOrdersData.getRecipeId()) != null) {
							if (QuestService.startQuest(env)) {
								if (ItemService.addQuestItems(player, workOrdersData.getGiveComponent())) {
									RecipeService.addRecipe(player, workOrdersData.getRecipeId(), false);
									PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 0));
								}
								return true;
							}
						}
					}
				}
			}
			else if (qs.getStatus() == QuestStatus.START) {
				if (env.getDialog() == QuestDialog.START_DIALOG) {
					int var = qs.getQuestVarById(0);
					if (QuestService.collectItemCheck(env, false)) {
						changeQuestStep(env, var, var, true); // reward
						return sendQuestDialog(env, 5);
					}
					else {
						return sendQuestSelectionDialog(env);
					}
				}
			}
			else if (qs.getStatus() == QuestStatus.REWARD) {
				if (QuestService.collectItemCheck(env, true)) {
					player.getRecipeList().deleteRecipe(player, workOrdersData.getRecipeId());
					return sendQuestEndDialog(env);
				}
			}
		}
		return false;
	}
}
