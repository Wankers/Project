package admincommands;

import commons.database.dao.DAOManager;
import gameserver.dao.PlayerDAO;
import gameserver.model.gameobjects.player.Player;
import gameserver.services.PunishmentService;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.Util;
import gameserver.utils.chathandlers.ChatCommand;

/**
 * @author nrg
 */
public class UnBanChar extends ChatCommand {

	public UnBanChar() {
		super("unbanchar");
	}

	@Override
	public void execute(Player admin, String... params) {
		if (params == null || params.length < 1) {
			PacketSendUtility.sendMessage(admin, "Syntax: //unbanchar <player>");
			return;
		}

		// Banned player must be offline
		String name = Util.convertName(params[0]);
		int playerId = DAOManager.getDAO(PlayerDAO.class).getPlayerIdByName(name);
		if (playerId == 0) {
			PacketSendUtility.sendMessage(admin, "Player " + name + " was not found!");
			PacketSendUtility.sendMessage(admin, "Syntax: //unbanchar <player>");
			return;
		}

		PacketSendUtility.sendMessage(admin, "Character " + name + " is not longer banned!");
		
    PunishmentService.unbanChar(playerId);
	}

	@Override
	public void onFail(Player player, String message) {
		PacketSendUtility.sendMessage(player, "Syntax: //unban <player> [account|ip|full]");
	}
}
