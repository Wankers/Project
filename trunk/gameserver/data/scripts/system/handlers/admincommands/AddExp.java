package admincommands;

import gameserver.model.gameobjects.VisibleObject;
import gameserver.model.gameobjects.player.Player;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.chathandlers.ChatCommand;

/**
 * @author Wakizashi
 */
public class AddExp extends ChatCommand {

	public AddExp() {
		super("addexp");
	}

	@Override
	public void execute(Player player, String... params) {
		if (params.length != 1) {
			onFail(player, null);
			return;
		}

		Player target = null;
		VisibleObject creature = player.getTarget();

		if (player.getTarget() instanceof Player) {
			target = (Player) creature;
		}

		String paramValue = params[0];
		long exp;
		try {
			exp = Long.parseLong(paramValue);
		}
		catch (NumberFormatException e) {
			PacketSendUtility.sendMessage(player, "<exp> must be an Integer");
			return;
		}

		exp += target.getCommonData().getExp();
		target.getCommonData().setExp(exp);
		PacketSendUtility.sendMessage(player, "You added " + params[0] + " exp points to the target.");
	}

	@Override
	public void onFail(Player player, String message) {
		PacketSendUtility.sendMessage(player, "syntax //addexp <exp>");
	}
}
