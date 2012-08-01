package admincommands;

import gameserver.model.gameobjects.player.Player;
import gameserver.services.abyss.AbyssRankUpdateService;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.chathandlers.ChatCommand;

/**
 * @author ATracer
 */
public class Ranking extends ChatCommand {

	public Ranking() {
		super("ranking");
	}

	@Override
	public void execute(Player admin, String... params) {
		if (params.length == 0) {
			onFail(admin, null);
		}
		else if ("update".equalsIgnoreCase(params[0])) {
			AbyssRankUpdateService.getInstance().performUpdate();
		}
	}

	@Override
	public void onFail(Player player, String message) {
		PacketSendUtility.sendMessage(player, "syntax //ranking update");
	}
}
