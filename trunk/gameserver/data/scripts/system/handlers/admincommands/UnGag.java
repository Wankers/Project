package admincommands;

import java.util.concurrent.Future;

import gameserver.model.TaskId;
import gameserver.model.gameobjects.player.Player;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.Util;
import gameserver.utils.chathandlers.ChatCommand;
import gameserver.world.World;

/**
 * @author Watson
 */
public class UnGag extends ChatCommand {

	public UnGag() {
		super("ungag");
	}

	@Override
	public void execute(Player admin, String... params) {
		if (params == null || params.length < 1) {
			PacketSendUtility.sendMessage(admin, "Syntax: //ungag <player>");
			return;
		}

		String name = Util.convertName(params[0]);
		Player player = World.getInstance().findPlayer(name);
		if (player == null) {
			PacketSendUtility.sendMessage(admin, "Player " + name + " was not found!");
			PacketSendUtility.sendMessage(admin, "Syntax: //ungag <player>");
			return;
		}

		player.setGagged(false);
		Future<?> task = player.getController().getTask(TaskId.GAG);
		if (task != null)
			player.getController().cancelTask(TaskId.GAG);
		PacketSendUtility.sendMessage(player, "You have been ungagged");

		PacketSendUtility.sendMessage(admin, "Player " + name + " ungagged");
	}

	@Override
	public void onFail(Player player, String message) {
		PacketSendUtility.sendMessage(player, "Syntax: //ungag <player>");
	}
}
