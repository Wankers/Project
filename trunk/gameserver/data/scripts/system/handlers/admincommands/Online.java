package admincommands;

import commons.database.dao.DAOManager;
import gameserver.dao.PlayerDAO;
import gameserver.model.gameobjects.player.Player;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.chathandlers.ChatCommand;

/**
 * @author VladimirZ
 */
public class Online extends ChatCommand {

	public Online() {
		super("online");
	}

	@Override
	public void execute(Player admin, String... params) {

		int playerCount = DAOManager.getDAO(PlayerDAO.class).getOnlinePlayerCount();

		if (playerCount == 1) {
			PacketSendUtility.sendMessage(admin, "There is " + (playerCount) + " player online !");
		}
		else {
			PacketSendUtility.sendMessage(admin, "There are " + (playerCount) + " players online !");
		}
	}

	@Override
	public void onFail(Player player, String message) {
		PacketSendUtility.sendMessage(player, "Syntax: //online");
	}
}
