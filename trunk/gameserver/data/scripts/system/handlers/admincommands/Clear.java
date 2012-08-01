package admincommands;

import gameserver.model.gameobjects.player.Player;
import gameserver.services.FindGroupService;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.chathandlers.ChatCommand;

/**
 * @author KID
 */
public class Clear extends ChatCommand {

	public Clear() {
		super("clear");
	}

	@Override
	public void execute(Player admin, String... params) {
		if(params[0].equalsIgnoreCase("groups")) {
			PacketSendUtility.sendMessage(admin, "Not implemented, if need this - pm to AT");
		}
		else if(params[0].equalsIgnoreCase("allys")) {
			PacketSendUtility.sendMessage(admin, "Not implemented, if need this - pm to AT");
		}
		else if(params[0].equalsIgnoreCase("findgroup")){
			FindGroupService.getInstance().clean();
		}
	}

	@Override
	public void onFail(Player player, String message) {
		PacketSendUtility.sendMessage(player, "<usage //clear groups | allys | findgroup");
	}
}
