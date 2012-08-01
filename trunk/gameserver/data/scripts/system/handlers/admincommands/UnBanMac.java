package admincommands;

import gameserver.model.gameobjects.player.Player;
import gameserver.network.BannedMacManager;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.chathandlers.ChatCommand;

/**
 * @author KID
 */
public class UnBanMac extends ChatCommand {

	public UnBanMac() {
		super("unbanmac");
	}

	@Override
	public void execute(Player player, String... params) {
		if (params == null || params.length < 1) {
			onFail(player, null);
			return;
		}

		String address = params[0];
		boolean result = BannedMacManager.getInstance().unbanAddress(address,
			"uban;mac=" + address + ", " + player.getObjectId() + "; admin=" + player.getName());
		if (result)
			PacketSendUtility.sendMessage(player, "mac " + address + " has unbanned");
		else
			PacketSendUtility.sendMessage(player, "mac " + address + " is not banned");
	}

	@Override
	public void onFail(Player player, String message) {
		PacketSendUtility.sendMessage(player, "Syntax: //unbanmac <mac>");
	}
}
