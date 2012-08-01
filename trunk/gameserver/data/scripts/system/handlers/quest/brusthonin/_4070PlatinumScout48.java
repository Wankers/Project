package quest.brusthonin;

import gameserver.dataholders.DataManager;
import gameserver.dataholders.QuestsData;
import gameserver.model.PlayerClass;
import gameserver.model.gameobjects.Npc;
import gameserver.model.gameobjects.player.Player;
import gameserver.questEngine.handlers.QuestHandler;
import gameserver.questEngine.model.QuestDialog;
import gameserver.questEngine.model.QuestEnv;
import gameserver.questEngine.model.QuestState;
import gameserver.questEngine.model.QuestStatus;
import gameserver.services.QuestService;

/**
 * @author Vincas
 */
public class _4070PlatinumScout48 extends QuestHandler {

	static QuestsData questsData = DataManager.QUEST_DATA;
	private final static int questId = 4070;

	public _4070PlatinumScout48() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(205161).addOnQuestStart(questId);
		qe.registerQuestNpc(205161).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		if (player.getLevel() <= 46)
			return false;
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);

		if (targetId == 205161) {
			if (qs == null || qs.getStatus() == QuestStatus.NONE) {
				if (env.getDialogId() == 2) {
					PlayerClass playerClass = player.getCommonData().getPlayerClass();
					if (playerClass == PlayerClass.ASSASSIN || playerClass == PlayerClass.RANGER
						|| playerClass == PlayerClass.SCOUT) {
						QuestService.startQuest(env);
						return sendQuestDialog(env, 1011);
					}
					else {
						return sendQuestDialog(env, 3739);
					}
				}
			}
			else if (qs != null && qs.getStatus() == QuestStatus.START) {
				if (env.getDialogId() == 2) {
					return sendQuestDialog(env, 1011);
				}
				else if (env.getDialog() == QuestDialog.STEP_TO_1) {
					if (player.getInventory().getItemCountByItemId(186000010) >= 2000) {
						qs.setQuestVarById(1, 0);
						removeQuestItem(env, 186000010, 2000);
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						return sendQuestDialog(env, 5);
					}
					else {
						return sendQuestDialog(env, 1009);
					}
				}
				else if (env.getDialog() == QuestDialog.STEP_TO_2) {
					if (player.getInventory().getItemCountByItemId(186000010) >= 400) {
						qs.setQuestVarById(1, 1);
						removeQuestItem(env, 186000010, 400);
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						return sendQuestDialog(env, 6);
					}
					else {
						return sendQuestDialog(env, 1009);
					}
				}
				else if (env.getDialog() == QuestDialog.STEP_TO_3) {
					if (player.getInventory().getItemCountByItemId(186000010) >= 1000) {
						qs.setQuestVarById(1, 2);
						removeQuestItem(env, 186000010, 1000);
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						return sendQuestDialog(env, 7);
					}
					else {
						return sendQuestDialog(env, 1009);
					}
				}
				else if (env.getDialog() == QuestDialog.STEP_TO_4) {
					if (player.getInventory().getItemCountByItemId(186000010) >= 200) {
						qs.setQuestVarById(1, 3);
						removeQuestItem(env, 186000010, 200);
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						return sendQuestDialog(env, 8);
					}
					else {
						return sendQuestDialog(env, 1009);
					}
				}
			}
			else if (qs.getStatus() == QuestStatus.COMPLETE) {
				if (env.getDialogId() == 2) {
					if (qs.canRepeat()) {
						QuestService.startQuest(env);
						return sendQuestDialog(env, 1011);
					}
					else
						return sendQuestDialog(env, 1008);
				}
			}
			else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}
}
