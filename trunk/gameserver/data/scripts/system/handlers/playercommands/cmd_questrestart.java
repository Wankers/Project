package playercommands;

import gameserver.model.gameobjects.player.Player;
import gameserver.network.aion.serverpackets.SM_QUEST_ACTION;
import gameserver.questEngine.model.QuestState;
import gameserver.questEngine.model.QuestStatus;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.chathandlers.ChatCommand;

/**
 * @author ginho1
 *
 */
public class cmd_questrestart extends ChatCommand {

	public cmd_questrestart() {
		super("questrestart");
	}

	@Override
	public void execute(Player player, String... params) {

		if (params == null || params.length < 1) {
			PacketSendUtility.sendMessage(player, "syntax .questrestart <quest id>");
			return;
		}

		int id;
		try {
			id = Integer.valueOf(params[0]);
		}
		catch (NumberFormatException e)	{
			PacketSendUtility.sendMessage(player, "syntax .questrestart <quest id>");
			return;
		}

		QuestState qs = player.getQuestStateList().getQuestState(id);

		if (qs == null || id == 1006 || id == 2008) {
			PacketSendUtility.sendMessage(player, "Quest [quest: "+id+"] can't be restarted.");
			return;
		}

		if (qs.getStatus() == QuestStatus.START || qs.getStatus() == QuestStatus.REWARD) {
			if(qs.getQuestVarById(0) != 0) {
				qs.setStatus(QuestStatus.START);
				qs.setQuestVar(0);
				PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(id, qs.getStatus(), qs.getQuestVars().getQuestVars()));
				PacketSendUtility.sendMessage(player, "Quest [quest: "+id+"] restarted.");
			} else
				PacketSendUtility.sendMessage(player, "Quest [quest: "+id+"] can't be restarted.");
		} else	{
			PacketSendUtility.sendMessage(player, "Quest [quest: "+id+"] can't be restarted.");
		}
	}

	@Override
	public void onFail(Player player, String message) {
		// TODO Auto-generated method stub
	}
}
