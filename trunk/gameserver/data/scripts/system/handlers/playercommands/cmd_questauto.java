package playercommands;

import org.apache.commons.lang.ArrayUtils;

import gameserver.model.gameobjects.player.Player;
import gameserver.network.aion.serverpackets.SM_QUEST_ACTION;
import gameserver.questEngine.model.QuestState;
import gameserver.questEngine.model.QuestStatus;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.chathandlers.ChatCommand;

/**
 * @author ATracer
 */
public class cmd_questauto extends ChatCommand {

	/**
	 * put quests for automation here (new int[]{1245,1345,7895})
	 */
	private final int[] questIds = new int[] {};

	public cmd_questauto() {
		super("questauto");
	}

	@Override
	public void execute(Player player, String... params) {
		if (params == null || params.length < 1) {
			PacketSendUtility.sendMessage(player, "syntax .questauto <questid>");
			return;
		}

		int questId = 0;
		try {
			questId = Integer.parseInt(params[0]);
		}
		catch (Exception ex) {
			PacketSendUtility.sendMessage(player, "wrong quest id");
			return;
		}
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null || qs.getStatus() != QuestStatus.START) {
			PacketSendUtility.sendMessage(player, "quest is not started");
			return;
		}

		if (!ArrayUtils.contains(questIds, questId)) {
			PacketSendUtility.sendMessage(player, "this quest is not supported");
			return;
		}

		qs.setStatus(QuestStatus.REWARD);
		PacketSendUtility
			.sendPacket(player, new SM_QUEST_ACTION(questId, qs.getStatus(), qs.getQuestVars().getQuestVars()));
	}

	@Override
	public void onFail(Player player, String message) {
		// TODO Auto-generated method stub
	}
}
