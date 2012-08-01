package admincommands;

import gameserver.model.gameobjects.player.Player;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.chathandlers.ChatCommand;

/**
 * @author xTz
 */
public class Ring extends ChatCommand {

	public Ring() {
		super("ring");
	}

	@Override
	public void execute(Player admin, String... params) {
		double direction = Math.toRadians(admin.getHeading()) - 0.5f;
		if (direction < 0) {
			direction += 2f;
		}
		float x1 = (float) (Math.cos(Math.PI * direction) * 6);
		float y1 = (float) (Math.sin(Math.PI * direction) * 6);
		PacketSendUtility.sendMessage(admin, "center:" + admin.getX() + " " + admin.getY() + " " + admin.getZ());
		PacketSendUtility.sendMessage(admin, "p1:" + admin.getX() + " " + admin.getY() + " " + (admin.getZ() + 6f));
		PacketSendUtility.sendMessage(admin, "p2:" + (admin.getX() + x1) + " " + (admin.getY() + y1) + " " + admin.getZ());
	}

	@Override
	public void onFail(Player player, String message) {
		PacketSendUtility.sendMessage(player, "syntax; //ring");
	}

}