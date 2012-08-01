package playercommands;

import gameserver.model.Wedding;
import gameserver.model.gameobjects.player.Player;
import gameserver.services.WeddingService;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.chathandlers.ChatCommand;

/**
 * @author synchro2
 */
public class cmd_answer extends ChatCommand {

	public cmd_answer() {
		super("answer");
	}

	@Override
	public void execute(Player player, String... params) {
		Wedding wedding = WeddingService.getInstance().getWedding(player);

		if (params == null || params.length != 1) {
			PacketSendUtility.sendMessage(player, "syntax .answer yes/no.");
			return;
		}
		
		if (player.getWorldId() == 510010000 || player.getWorldId() == 520010000) {
			PacketSendUtility.sendMessage(player, "You can't use this command on prison.");
			return;
		}

		if (wedding == null) {
			PacketSendUtility.sendMessage(player, "Wedding not started.");
		}

		if (params[0].toLowerCase().equals("yes")) {
			PacketSendUtility.sendMessage(player, "You accept.");
			WeddingService.getInstance().acceptWedding(player);
		}

		if (params[0].toLowerCase().equals("no")) {
			PacketSendUtility.sendMessage(player, "You decide.");
			WeddingService.getInstance().cancelWedding(player);
		}

	}

	@Override
	public void onFail(Player player, String message) {
		PacketSendUtility.sendMessage(player, "syntax .answer yes/no.");
	}
}