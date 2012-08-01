package playercommands;

import java.util.Collection;

import gameserver.model.gameobjects.player.Player;
import gameserver.questEngine.QuestEngine;
import gameserver.questEngine.model.QuestEnv;
import gameserver.questEngine.model.QuestState;
import gameserver.questEngine.model.QuestStatus;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.chathandlers.ChatCommand;

/**
 * Checks all LOCKED missions for start conditions immediately And starts them, if conditions are fulfilled
 * 
 * @author vlog
 */
public class cmd_mcheck extends ChatCommand {

	public cmd_mcheck() {
		super("mcheck");
	}

	@Override
	public void execute(Player player, String... params) {
		Collection<QuestState> qsl = player.getQuestStateList().getAllQuestState();
		for (QuestState qs : qsl)
			if (qs.getStatus() == QuestStatus.LOCKED) {
				int questId = qs.getQuestId();
				QuestEngine.getInstance().onLvlUp(new QuestEnv(null, player, questId, 0));
			}
		PacketSendUtility.sendMessage(player, "Missions checked successfully");
	}

	@Override
	public void onFail(Player player, String message) {
		// TODO Auto-generated method stub
	}
}
