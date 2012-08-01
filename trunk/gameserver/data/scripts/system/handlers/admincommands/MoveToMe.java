package admincommands;

import gameserver.model.gameobjects.player.Player;
import gameserver.services.teleport.TeleportService;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.Util;
import gameserver.utils.chathandlers.ChatCommand;
import gameserver.world.World;

/**
 * Admin movetome command.
 * 
 * @author Cyrakuse
 */
public class MoveToMe extends ChatCommand {

	public MoveToMe() {
		super("movetome");
	}

	@Override
	public void execute(Player player, String... params) {
		if (params == null || params.length < 1) {
			PacketSendUtility.sendMessage(player, "syntax //movetome <characterName>");
			return;
		}

		Player playerToMove = World.getInstance().findPlayer(Util.convertName(params[0]));
		if (playerToMove == null) {
			PacketSendUtility.sendMessage(player, "The specified player is not online.");
			return;
		}

		if (playerToMove == player) {
			PacketSendUtility.sendMessage(player, "Cannot use this command on yourself.");
			return;
		}

		TeleportService.teleportTo(playerToMove, player.getWorldId(), player.getInstanceId(), player.getX(), player.getY(),
			player.getZ(), player.getHeading(), 3000, true);
		PacketSendUtility.sendMessage(player, "Teleported player " + playerToMove.getName() + " to your location.");
		PacketSendUtility.sendMessage(playerToMove, "You have been teleported by " + player.getName() + ".");
	}

	@Override
	public void onFail(Player player, String message) {
		PacketSendUtility.sendMessage(player, "syntax //movetome <characterName>");
	}
}
