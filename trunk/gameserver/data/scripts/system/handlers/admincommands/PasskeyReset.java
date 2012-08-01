package admincommands;

import commons.database.dao.DAOManager;
import gameserver.dao.PlayerDAO;
import gameserver.dao.PlayerPasskeyDAO;
import gameserver.model.gameobjects.player.Player;
import gameserver.network.loginserver.LoginServer;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.Util;
import gameserver.utils.chathandlers.ChatCommand;

/**
 * @author cura
 */
public class PasskeyReset extends ChatCommand {

	public PasskeyReset() {
		super("passkeyreset");
	}

	@Override
	public void execute(Player player, String... params) {
		if (params == null || params.length < 1) {
			PacketSendUtility.sendMessage(player, "syntax: //passkeyreset <player> <passkey>");
			return;
		}

		String name = Util.convertName(params[0]);
		int accountId = DAOManager.getDAO(PlayerDAO.class).getAccountIdByName(name);
		if (accountId == 0) {
			PacketSendUtility.sendMessage(player, "player " + name + " can't find!");
			PacketSendUtility.sendMessage(player, "syntax: //passkeyreset <player> <passkey>");
			return;
		}

		try {
			Integer.parseInt(params[1]);
		}
		catch (NumberFormatException e) {
			PacketSendUtility.sendMessage(player, "parameters should be number!");
			return;
		}

		String newPasskey = params[1];
		if (!(newPasskey.length() > 5 && newPasskey.length() < 9)) {
			PacketSendUtility.sendMessage(player, "passkey is 6~8 digits!");
			return;
		}

		DAOManager.getDAO(PlayerPasskeyDAO.class).updateForcePlayerPasskey(accountId, newPasskey);
		LoginServer.getInstance().sendBanPacket((byte) 2, accountId, "", -1, player.getObjectId());
	}

	@Override
	public void onFail(Player player, String message) {
		PacketSendUtility.sendMessage(player, "syntax: //passkeyreset <player> <passkey>");
	}
}
