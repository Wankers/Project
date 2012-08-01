package weddingcommands;

import gameserver.model.gameobjects.player.Player;
import gameserver.services.teleport.TeleportService;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.chathandlers.ChatCommand;
import gameserver.world.World;

/**
 * @author synchro2
 */
public class cometome extends ChatCommand {

	public cometome() {
		super("cometome");
	}

	@Override
	public void execute(Player player, String... params) {

		Player partner = player.findPartner();
		if (partner == null) {
			PacketSendUtility.sendMessage(player, "Not online.");
			return;
		}
		if (player.getWorldId() == 510010000 || player.getWorldId() == 520010000) {
			PacketSendUtility.sendMessage(player, "You can't use this command on prison.");
			return;
		}
		if (partner.getWorldId() == 510010000 || partner.getWorldId() == 520010000) {
			PacketSendUtility.sendMessage(player, "You can't teleported " + partner.getName() +", your partner is on prison.");
			return;
		}
		if (partner.getWorldId() == 300350000 || partner.getWorldId() == 300360000 || partner.getWorldId() == 300420000 || partner.getWorldId() == 300430000) {
			PacketSendUtility.sendMessage(player, "You can't use this command in PvP Arena.");
			return;
		}

		TeleportService.teleportTo(partner, player.getWorldId(), player.getInstanceId(), player.getX(), player.getY(),
			player.getZ(), player.getHeading(), 3000, true);
		PacketSendUtility.sendMessage(player, partner.getName() + " teleported to you.");
	}

	@Override
	public void onFail(Player player, String message) {
		PacketSendUtility.sendMessage(player, "Failed");
	}
}