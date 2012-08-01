package admincommands;

import gameserver.model.gameobjects.VisibleObject;
import gameserver.model.gameobjects.player.Player;
import gameserver.network.BannedMacManager;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.chathandlers.ChatCommand;

/**
 * @author KID
 */
public class BanMac extends ChatCommand {

	public BanMac() {
		super("banmac");
	}

	@Override
	public void execute(Player player, String... params) {
		if (params == null || params.length < 1) {
			PacketSendUtility.sendMessage(player, "Syntax: //banmac <mac> [time in minutes]");
			return;
		}

		String address = params[0];

		int time = 0; // Default: infinity
		if (params.length > 1) {
			try {
				time = Integer.parseInt(params[1]);
			}
			catch (NumberFormatException e) {
				PacketSendUtility.sendMessage(player, "Syntax: //banmac <mac> [time in minutes]");
				return;
			}
		}

		VisibleObject target = player.getTarget();
		String targetName = "direct_type";
		if (target != null && target instanceof Player) {
			if (target.getObjectId() == player.getObjectId()) {
				PacketSendUtility.sendMessage(player, "omg, disselect yourself please.");
				return;
			}

			Player targetpl = (Player) target;
			address = targetpl.getClientConnection().getMacAddress();
			targetName = targetpl.getName();
			targetpl.getClientConnection().closeNow();
		}

		BannedMacManager.getInstance().banAddress(address, System.currentTimeMillis() + time * 60000,
			"author=" + player.getName() + ", " + player.getObjectId() + "; target=" + targetName);
	}

	@Override
	public void onFail(Player player, String message) {
		PacketSendUtility.sendMessage(player, "Syntax: //banmac <mac> [time in minutes]");
	}

}
