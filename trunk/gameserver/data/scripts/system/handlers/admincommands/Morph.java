package admincommands;

import gameserver.model.gameobjects.player.Player;
import gameserver.network.aion.serverpackets.SM_TRANSFORM;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.chathandlers.ChatCommand;

/**
 * @author ATracer
 * @modified By aionchs- Wylovech
 */
public class Morph extends ChatCommand {

	public Morph() {
		super("morph");
	}

	@Override
	public void execute(Player admin, String... params) {
		if (params == null || params.length != 1) {
			PacketSendUtility.sendMessage(admin, "syntax //morph <NPC Id | cancel> ");
			return;
		}

		Player target = admin;
		int param = 0;

		if (admin.getTarget() instanceof Player)
			target = (Player) admin.getTarget();

		if (!("cancel").startsWith(params[0].toLowerCase())) {
			try {
				param = Integer.parseInt(params[0]);

			}
			catch (NumberFormatException e) {
				PacketSendUtility.sendMessage(admin, "Parameter must be an integer, or cancel.");
				return;
			}
		}

		if ((param != 0 && param < 200000) || param > 298021) {
			PacketSendUtility.sendMessage(admin, "Something wrong with the NPC Id!");
			return;
		}

		target.setTransformedModelId(param);
		PacketSendUtility.broadcastPacketAndReceive(target, new SM_TRANSFORM(target, true));

		if (param == 0) {
			if (target.equals(admin)) {
				PacketSendUtility.sendMessage(target, "Morph successfully cancelled.");
			}
			else {
				PacketSendUtility.sendMessage(target, "Your morph has been cancelled by " + admin.getName() + ".");
				PacketSendUtility.sendMessage(admin, "You have cancelled " + target.getName() + "'s morph.");
			}
		}
		else {
			if (target.equals(admin)) {
				PacketSendUtility.sendMessage(target, "Successfully morphed to npcId " + param + ".");
			}
			else {
				PacketSendUtility.sendMessage(target, admin.getName() + " morphs you into an NPC form.");
				PacketSendUtility.sendMessage(admin, "You morph " + target.getName() + " to npcId " + param + ".");
			}
		}
	}

	@Override
	public void onFail(Player player, String message) {
		PacketSendUtility.sendMessage(player, "syntax //morph <NPC Id | cancel> ");
	}
}
