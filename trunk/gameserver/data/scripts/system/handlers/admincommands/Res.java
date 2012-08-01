package admincommands;

import gameserver.model.gameobjects.VisibleObject;
import gameserver.model.gameobjects.player.Player;
import gameserver.network.aion.serverpackets.SM_RESURRECT;
import gameserver.services.player.PlayerReviveService;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.chathandlers.ChatCommand;

/**
 * @author Sarynth
 */
public class Res extends ChatCommand {

	public Res() {
		super("res");
	}

	@Override
	public void execute(Player admin, String... params) {
		final VisibleObject target = admin.getTarget();
		if (target == null) {
			PacketSendUtility.sendMessage(admin, "No target selected.");
			return;
		}

		if (!(target instanceof Player)) {
			PacketSendUtility.sendMessage(admin, "You can only resurrect other players.");
			return;
		}

		final Player player = (Player) target;
		if (!player.getLifeStats().isAlreadyDead()) {
			PacketSendUtility.sendMessage(admin, "That player is already alive.");
			return;
		}

		// Default action is to prompt for resurrect.
		if (params == null || params.length == 0 || ("prompt").startsWith(params[0])) {
			player.setPlayerResActivate(true);
			PacketSendUtility.sendPacket(player, new SM_RESURRECT(admin));
			return;
		}

		if (("instant").startsWith(params[0])) {
			PlayerReviveService.skillRevive(player);
			return;
		}

		PacketSendUtility.sendMessage(admin, "[Resurrect] Usage: target player and use //res <instant|prompt>");
	}

	@Override
	public void onFail(Player player, String message) {
		// TODO Auto-generated method stub
	}
}
