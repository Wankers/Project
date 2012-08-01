package admincommands;

import gameserver.model.gameobjects.player.Player;
import gameserver.network.loginserver.LoginServer;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.Util;
import gameserver.utils.chathandlers.ChatCommand;
import gameserver.world.World;

/**
 * Admin revoke command.
 * 
 * @author Cyrakuse
 * @modified By Aionchs-Wylovech
 */
public class Revoke extends ChatCommand {

	public Revoke() {
		super("revoke");
	}

	@Override
	public void execute(Player admin, String... params) {
		if (params.length != 2) {
			PacketSendUtility.sendMessage(admin, "syntax //revoke <characterName> <acceslevel | membership>");
			return;
		}

		int type = 0;
		if (params[1].toLowerCase().equals("acceslevel")) {
			type = 1;
		}
		else if (params[1].toLowerCase().equals("membership")) {
			type = 2;
		}
		else {
			PacketSendUtility.sendMessage(admin, "syntax //revoke <characterName> <acceslevel | membership>");
			return;
		}

		Player player = World.getInstance().findPlayer(Util.convertName(params[0]));
		if (player == null) {
			PacketSendUtility.sendMessage(admin, "The specified player is not online.");
			return;
		}
		LoginServer.getInstance().sendLsControlPacket(player.getAcountName(), player.getName(), admin.getName(), 0, type);
	}

	@Override
	public void onFail(Player player, String message) {
		PacketSendUtility.sendMessage(player, "syntax //revoke <characterName> <acceslevel | membership>");
	}
}
