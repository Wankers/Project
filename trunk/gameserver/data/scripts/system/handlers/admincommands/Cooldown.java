package admincommands;

import gameserver.model.gameobjects.player.Player;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.chathandlers.ChatCommand;

/**
 * @author Cura
 */
public class Cooldown extends ChatCommand {

	public Cooldown() {
		super("cooldown");
	}

	@Override
	public void execute(Player player, String... params) {
		if (player.isCoolDownZero()) {
			PacketSendUtility.sendMessage(player, "Cooldown time of all skills has been recovered.");
			player.setCoolDownZero(false);
		}
		else {
			PacketSendUtility.sendMessage(player, "Cooldown time of all skills is set to 0.");
			player.setCoolDownZero(true);
		}
	}

	@Override
	public void onFail(Player player, String message) {
		// TODO Auto-generated method stub
	}
}
