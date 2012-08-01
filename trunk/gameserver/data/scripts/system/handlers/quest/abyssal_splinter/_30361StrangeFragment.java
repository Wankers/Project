package quest.abyssal_splinter;

import gameserver.model.gameobjects.Item;
import gameserver.model.gameobjects.player.Player;
import gameserver.questEngine.handlers.HandlerResult;
import gameserver.questEngine.handlers.QuestHandler;
import gameserver.questEngine.model.QuestDialog;
import gameserver.questEngine.model.QuestEnv;
import gameserver.questEngine.model.QuestState;
import gameserver.questEngine.model.QuestStatus;
import gameserver.services.QuestService;

/**
 * @author Rikka
 */
public class _30361StrangeFragment extends QuestHandler {

	private final static int questId = 30361;

	public _30361StrangeFragment() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(278033).addOnTalkEvent(questId);
		qe.registerQuestNpc(279029).addOnTalkEvent(questId);
		qe.registerQuestNpc(260265).addOnTalkEvent(questId);
		qe.registerQuestItem(182209820, questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		QuestDialog dialog = env.getDialog();
		int targetId = env.getTargetId();
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			return false;
		}
		else if (qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			switch (targetId) {
				case 278033: { // Erik
					switch (dialog) {
						case START_DIALOG: {
							if (var == 0) {
								return sendQuestDialog(env, 1352);
							}
						}
						case STEP_TO_1: {
							return defaultCloseDialog(env, 0, 1);
						}
					}
					break;
				}
				case 279029: { // Lugbug
					switch (dialog) {
						case START_DIALOG: {
							if (var == 1) {
								return sendQuestDialog(env, 1693);
							}
						}
						case STEP_TO_2: {
							return defaultCloseDialog(env, 1, 2, true, false); // reward
						}
					}
					break;
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 260265) { // Gwal
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}

	@Override
	public HandlerResult onItemUseEvent(QuestEnv env, Item item) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (QuestService.startQuest(env)) {
				removeQuestItem(env, 182209820, 1);
				return HandlerResult.fromBoolean(sendQuestDialog(env, 4));
			}
		}
		return HandlerResult.FAILED;
	}
}
