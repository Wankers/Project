package admincommands;

import gameserver.model.gameobjects.player.Player;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.chathandlers.ChatCommand;

public class Whisper extends ChatCommand {

	public Whisper() {
		super("whisper");
	}

	@Override
	public void execute(Player admin, String... params) {

		if(params[0].equalsIgnoreCase("off")) {
			admin.setUnWispable();
			PacketSendUtility.sendMessage(admin, "Accepting Whisper : OFF");
		}
		else if (params[0].equalsIgnoreCase("on")) {
			admin.setWispable();
			PacketSendUtility.sendMessage(admin, "Accepting Whisper : ON");
		}
	}	

	@Override
	public void onFail(Player player, String message) {
		PacketSendUtility.sendMessage(player, "syntax //whisper [on for wispable / off for unwispable]");
	}
}
