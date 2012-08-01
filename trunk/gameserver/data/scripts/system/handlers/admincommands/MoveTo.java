package admincommands;

import gameserver.model.gameobjects.player.Player;
import gameserver.services.teleport.TeleportService;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.chathandlers.ChatCommand;
import gameserver.world.WorldMapType;

/**
 * Admin moveto command
 * 
 * @author KID
 */
public class MoveTo extends ChatCommand {

	public MoveTo() {
		super("moveto");
	}

	@Override
	public void execute(Player admin, String... params) {
		if (params == null || params.length < 4) {
			PacketSendUtility.sendMessage(admin, "syntax //moveto worldId X Y Z");
			return;
		}

		int worldId;
		float x, y, z;

		try {
			worldId = Integer.parseInt(params[0]);
			x = Float.parseFloat(params[1]);
			y = Float.parseFloat(params[2]);
			z = Float.parseFloat(params[3]);
		}
		catch (NumberFormatException e) {
			PacketSendUtility.sendMessage(admin, "All the parameters should be numbers");
			return;
		}

		if (WorldMapType.getWorld(worldId) == null) {
			PacketSendUtility.sendMessage(admin, "Illegal WorldId %d " + worldId);
		}
		else {
			TeleportService.teleportTo(admin, worldId, x, y, z, 0);
			PacketSendUtility.sendMessage(admin, "Teleported to " + x + " " + y + " " + z + " [" + worldId + "]");
		}
	}

	@Override
	public void onFail(Player player, String message) {
		PacketSendUtility.sendMessage(player, "syntax //moveto worldId X Y Z");
	}
}
