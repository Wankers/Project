package quest.reshanta;

import java.util.HashMap;
import java.util.Map;

import commons.utils.Rnd;
import gameserver.model.gameobjects.player.Player;
import gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import gameserver.questEngine.handlers.QuestHandler;
import gameserver.questEngine.model.QuestDialog;
import gameserver.questEngine.model.QuestEnv;
import gameserver.questEngine.model.QuestState;
import gameserver.questEngine.model.QuestStatus;
import gameserver.services.QuestService;
import gameserver.services.item.ItemService;
import gameserver.utils.PacketSendUtility;

/**
 * @author Wakizashi
 * @reworked vlog
 */
public class _2717SilverForTheFountain extends QuestHandler {

	private final static int questId = 2717;
	private final Map<Integer, Integer> rewards = new HashMap<Integer, Integer>();

	public _2717SilverForTheFountain() {
		super(questId);
		rewards.put(186000031, 2);
		rewards.put(186000030, 1);
		rewards.put(182205668, 1);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(730143).addOnQuestStart(questId);
		qe.registerQuestNpc(730143).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		
		//check to avoid players from powerleveling with l2ph
		if (player.getLevel() < 55 || player.getWorldId() != 400010000)
			return false;
				
		int targetId = env.getTargetId();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		QuestDialog dialog = env.getDialog();

		if (qs == null || qs.getStatus() == QuestStatus.NONE || qs.canRepeat()) {
			if (targetId == 730143) { // Primum Coin Fountain
				switch (dialog) {
					case START_DIALOG: {
						if(player.getCommonData().getLevel() >= 55)
						return sendQuestDialog(env, 1011);
					}
					case STEP_TO_1: {
						long silverMedals = player.getInventory().getItemCountByItemId(186000031);
						if (silverMedals > 0) {
							if (!player.getInventory().isFull()) {
								if (QuestService.startQuest(env)) {
									changeQuestStep(env, 0, 0, true);
									return sendQuestDialog(env, 5);
								}
							}
							else {
								PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_FULL_INVENTORY);
								return sendQuestSelectionDialog(env);
							}
						}
						else {
							return sendQuestSelectionDialog(env);
						}
					}
				}
			}
		}
		else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 730143) { // Primum Coin Fountain
				if (dialog == QuestDialog.SELECT_NO_REWARD) {
					if (QuestService.finishQuest(env)) {
						if(removeQuestItem(env, 186000031, 1)) {
							if (isRewardSuccessful()) {
								switch (determineReward()) {
									case 0: {
										ItemService.addItem(player, 186000031, rewards.get(186000031));
										break;
									}
									case 1: {
										ItemService.addItem(player, 186000030, rewards.get(186000030));
										break;
									}
									case 2: {
										ItemService.addItem(player, 182205668, rewards.get(182205668));
										break;
									}
								}
								return sendQuestDialog(env, 1008);
							}
							else {
								ItemService.addItem(player, 182005206, 1);
								return sendQuestDialog(env, 1008);
							}
						}
					}
				}
				else {
					return QuestService.abandonQuest(player, questId);
				}
			}
		}
		return false;
	}
	
	/** Based on retail tests. Tester: Tibald */
	private boolean isRewardSuccessful() {
		// 303 total
		// 174 rusty
		return Rnd.get(1, 100) > 57; // 43% success
	}
	
	/** Based on retail tests. Tester: Tibald */
	private int determineReward() {
		// 129 total
		// 56 gold 43%
		// 67 silver 52%
		// 6 quartz 5%
		int random = Rnd.get(1, 100);
		if (random <= 43) {
			return 1;
		}
		else if (random > 43 && random < 96) {
			return 0;
		}
		else {
			return 2;
		}
	}
}
