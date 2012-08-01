package admincommands;

import gameserver.cache.HTMLCache;
import gameserver.model.gameobjects.player.Player;
import gameserver.services.HTMLService;
import gameserver.utils.chathandlers.ChatCommand;

/**
 * @author Phantom, ATracer
 */
public class Admin extends ChatCommand {

	public Admin() {
		super("admin");
	}

	@Override
	public void execute(Player player, String... params) {
		HTMLService.showHTML(player, HTMLCache.getInstance().getHTML("commands.xhtml"));
	}

	@Override
	public void onFail(Player player, String message) {
		// TODO Auto-generated method stub
	}
}
