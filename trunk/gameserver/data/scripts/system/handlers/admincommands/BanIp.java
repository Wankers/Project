package admincommands;

import gameserver.model.gameobjects.player.Player;
import gameserver.network.loginserver.LoginServer;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.chathandlers.ChatCommand;

/**
 * @author Watson
 */
public class BanIp extends ChatCommand {

	public BanIp() {
		super("banip");
	}

	@Override
	public void execute(Player player, String... params) {
		if (params == null || params.length < 1) {
			PacketSendUtility.sendMessage(player, "Syntax: //banip <mask> [time in minutes]");
			return;
		}

		String mask = params[0];

		int time = 0; // Default: infinity
		if (params.length > 1) {
			try {
				time = Integer.parseInt(params[1]);
			}
			catch (NumberFormatException e) {
				onFail(player, e.getMessage());
				return;
			}
		}

		LoginServer.getInstance().sendBanPacket((byte) 2, 0, mask, time, player.getObjectId());
	}

	@Override
	public void onFail(Player player, String message) {
		PacketSendUtility.sendMessage(player, "Syntax: //banip <mask> [time in minutes]");
	}
}
