package admincommands;

import commons.database.dao.DAOManager;
import gameserver.dao.PlayerDAO;
import gameserver.global.additions.MessagerAddition;
import gameserver.model.gameobjects.player.Player;
import gameserver.network.loginserver.LoginServer;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.Util;
import gameserver.utils.chathandlers.ChatCommand;
import gameserver.world.World;

/**
 * @author Watson
 */
public class Ban extends ChatCommand {

	public Ban() {
		super("ban");
	}

	@Override
	public void execute(Player admin, String... params) {
		if (params == null || params.length < 1) {
			PacketSendUtility.sendMessage(admin, "Syntax: //ban <player> [account|ip|full] [time in minutes]");
			return;
		}

		// We need to get player's account ID
		String name = Util.convertName(params[0]);
		int accountId = 0;
		String accountIp = "";

		// First, try to find player in the World
		Player player = World.getInstance().findPlayer(name);
		if (player != null) {
			accountId = player.getClientConnection().getAccount().getId();
			accountIp = player.getClientConnection().getIP();
		}

		// Second, try to get account ID of offline player from database
		if (accountId == 0)
			accountId = DAOManager.getDAO(PlayerDAO.class).getAccountIdByName(name);

		// Third, fail
		if (accountId == 0) {
			PacketSendUtility.sendMessage(admin, "Player " + name + " was not found!");
			PacketSendUtility.sendMessage(admin, "Syntax: //ban <player> [account|ip|full] [time in minutes]");
			return;
		}

		byte type = 3; // Default: full
		if (params.length > 1) {
			// Smart Matching
			String stype = params[1].toLowerCase();
			if (("account").startsWith(stype))
				type = 1;
			else if (("ip").startsWith(stype))
				type = 2;
			else if (("full").startsWith(stype))
				type = 3;
			else {
				PacketSendUtility.sendMessage(admin, "Syntax: //ban <player> [account|ip|full] [time in minutes]");
				return;
			}
		}

		int time = 0; // Default: infinity
		if (params.length > 2) {
			try {
				time = Integer.parseInt(params[2]);
			}
			catch (NumberFormatException e) {
				PacketSendUtility.sendMessage(admin, "Syntax: //ban <player> [account|ip|full] [time in minutes]");
				return;
			}
		}
        MessagerAddition.global(name + " was banned upon " + time + " min\nBan Type:" + type + "\n Account IP : "+ accountIp);
		LoginServer.getInstance().sendBanPacket(type, accountId, accountIp, time, admin.getObjectId());
	}

	@Override
	public void onFail(Player player, String message) {
		PacketSendUtility.sendMessage(player, "Syntax: //ban <player> [account|ip|full] [time in minutes]");
	}
}
