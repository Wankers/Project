package playercommands;

import gameserver.configs.main.CustomConfig;
import gameserver.model.Race;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.items.storage.Storage;
import gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import gameserver.services.player.PlayerChatService;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.chathandlers.ChatCommand;
import gameserver.world.World;
import org.apache.commons.lang.StringUtils;

/**
 * @author Shepper
 */
public class cmd_faction extends ChatCommand {

	public cmd_faction() {
		super("faction");
	}

	@Override
	public void execute(Player player, String... params) {
		Storage sender = player.getInventory();

		if (!CustomConfig.FACTION_CMD_CHANNEL) {
			PacketSendUtility.sendMessage(player, "The command is disabled.");
			return;
		}

		if (params == null || params.length < 1) {
			PacketSendUtility.sendMessage(player, "syntax .faction <message>");
			return;
		}

		if (player.getWorldId() == 510010000 || player.getWorldId() == 520010000) {
			PacketSendUtility.sendMessage(player, "You can't talk in Prison.");
			return;
		}
		else if (player.isGagged()) {
			PacketSendUtility.sendMessage(player, "You are gaged, you can't talk.");
			return;
		}

		if (!CustomConfig.FACTION_FREE_USE) {
			if (sender.getKinah() > CustomConfig.FACTION_USE_PRICE)
				sender.decreaseKinah(CustomConfig.FACTION_USE_PRICE);
			else {
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_NOT_ENOUGH_MONEY);
				return;
			}
		}

		String message = StringUtils.join(params, " ");

		if (!PlayerChatService.isFlooding(player)) {
			message = player.getName() + ": " + message;
			for (Player a : World.getInstance().getAllPlayers()) {
				if (a.getAccessLevel() > 1)
					PacketSendUtility.sendMessage(a, (player.getRace() == Race.ASMODIANS ? "[A] " : "[E] ") + message);
				else if (a.getRace() == player.getRace())
					PacketSendUtility.sendMessage(a, message);
			}
		}

	}

	@Override
	public void onFail(Player player, String message) {
		// TODO Auto-generated method stub
	}

}