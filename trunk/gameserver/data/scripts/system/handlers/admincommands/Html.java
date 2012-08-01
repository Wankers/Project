package admincommands;

import gameserver.cache.HTMLCache;
import gameserver.model.gameobjects.player.Player;
import gameserver.services.HTMLService;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.chathandlers.ChatCommand;

/**
 * @author lord_rex
 */
public class Html extends ChatCommand {

	public Html() {
		super("html");
	}

	@Override
	public void execute(Player player, String... params) {
		if (params == null || params.length < 1) {
			PacketSendUtility.sendMessage(player, "Usage: //html <reload|show>");
			return;
		}

		if (params[0].equals("reload")) {
			HTMLCache.getInstance().reload(true);
			PacketSendUtility.sendMessage(player, HTMLCache.getInstance().toString());
		}
		else if (params[0].equals("show"))
			if (params.length >= 2)
				HTMLService.showHTML(player, HTMLCache.getInstance().getHTML(params[1] + ".xhtml"));
			else
				PacketSendUtility.sendMessage(player, "Usage: //html show <filename>");
	}

	@Override
	public void onFail(Player player, String message) {
		PacketSendUtility.sendMessage(player, "Usage: //html <reload|show>");
	}
}
