package admincommands;

import java.util.NoSuchElementException;

import gameserver.model.gameobjects.player.Player;
import gameserver.services.PunishmentService;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.Util;
import gameserver.utils.chathandlers.ChatCommand;
import gameserver.world.World;

/**
 * @author lord_rex Command: //rprison <player> This command is removing player from prison.
 */
public class RPrison extends ChatCommand {

	public RPrison() {
		super("rprison");
	}

	@Override
	public void execute(Player admin, String... params) {
		if (params.length == 0 || params.length > 2) {
			PacketSendUtility.sendMessage(admin, "syntax //rprison <player>");
			return;
		}

		try {
			Player playerFromPrison = World.getInstance().findPlayer(Util.convertName(params[0]));

			if (playerFromPrison != null) {
				PunishmentService.setIsInPrison(playerFromPrison, false, 0, "");
				PacketSendUtility.sendMessage(admin, "Player " + playerFromPrison.getName() + " removed from prison.");
			}
		}
		catch (NoSuchElementException nsee) {
			PacketSendUtility.sendMessage(admin, "Usage: //rprison <player>");
		}
		catch (Exception e) {
			PacketSendUtility.sendMessage(admin, "Usage: //rprison <player>");
		}
	}

	@Override
	public void onFail(Player player, String message) {
		PacketSendUtility.sendMessage(player, "syntax //rprison <player>");
	}
}
