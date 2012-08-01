package playercommands;

import gameserver.model.gameobjects.player.Player;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.chathandlers.ChatCommand;

/**
 * @author Wakizashi
 */
public class cmd_noexp extends ChatCommand {

	public cmd_noexp() {
		super("noexp");
	}

	@Override
	public void execute(Player player, String... params) {
		if (player.getCommonData().getNoExp()) {
			player.getCommonData().setNoExp(false);
			PacketSendUtility.sendMessage(player, "Experience rewards are reactivated !");
		}
		else {
			player.getCommonData().setNoExp(true);
			PacketSendUtility.sendMessage(player, "Experience rewards are desactivated !");
		}
	}

	@Override
	public void onFail(Player player, String message) {
		// TODO Auto-generated method stub
	}
}
