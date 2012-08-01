package admincommands;

import gameserver.model.gameobjects.player.Player;
import gameserver.model.gameobjects.state.CreatureSeeState;
import gameserver.network.aion.serverpackets.SM_PLAYER_STATE;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.chathandlers.ChatCommand;

/**
 * @author Mathew
 */
public class See extends ChatCommand {

	public See() {
		super("see");
	}

	@Override
	public void execute(Player admin, String... params) {
		if (admin.getSeeState() < 2) {
			admin.setSeeState(CreatureSeeState.SEARCH2);
			PacketSendUtility.broadcastPacket(admin, new SM_PLAYER_STATE(admin), true);
			PacketSendUtility.sendMessage(admin, "You got vision.");
		}
		else {
			admin.setSeeState(CreatureSeeState.NORMAL);
			PacketSendUtility.broadcastPacket(admin, new SM_PLAYER_STATE(admin), true);
			PacketSendUtility.sendMessage(admin, "You lost vision.");
		}
	}

	@Override
	public void onFail(Player player, String message) {
		PacketSendUtility.sendMessage(player, "Syntax: //see");
	}
}
