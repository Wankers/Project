package admincommands;

import gameserver.model.gameobjects.player.Player;
import gameserver.services.PunishmentService;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.Util;
import gameserver.utils.chathandlers.ChatCommand;
import gameserver.world.World;

/**
 * @author lord_rex Command: //sprison <player> <delay>(minutes) This command is sending player to prison.
 */
public class SPrison extends ChatCommand {

	public SPrison() {
		super("sprison");
	}

	@Override
	public void execute(Player admin, String... params) {
		if (params.length < 2) {
			sendInfo(admin);
			return;
		}

		try {
			Player playerToPrison = World.getInstance().findPlayer(Util.convertName(params[0]));
			int delay = Integer.parseInt(params[1]);
			
			String reason = Util.convertName(params[2]);
			for(int itr = 3; itr < params.length; itr++)
				reason += " "+params[itr];

			if (playerToPrison != null) {
				PunishmentService.setIsInPrison(playerToPrison, true, delay, reason);
				PacketSendUtility.sendMessage(admin, "Player " + playerToPrison.getName() + " sent to prison for " + delay
					+ " because " + reason + ".");
			}
		}
		catch (Exception e) {
			sendInfo(admin);
		}
	
	}

	@Override
	public void onFail(Player player, String message) {
		sendInfo(player);
	}
	
	private void sendInfo(Player player) {
		PacketSendUtility.sendMessage(player, "syntax //sprison <player> <delay> <reason>");
	}
}
