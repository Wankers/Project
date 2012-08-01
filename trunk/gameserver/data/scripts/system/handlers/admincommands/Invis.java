package admincommands;

import gameserver.model.gameobjects.player.Player;
import gameserver.model.gameobjects.state.CreatureVisualState;
import gameserver.network.aion.serverpackets.SM_PLAYER_STATE;
import gameserver.skillengine.effect.AbnormalState;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.chathandlers.ChatCommand;

/**
 * @author Divinity
 */
public class Invis extends ChatCommand {

	public Invis() {
		super("invis");
	}

	@Override
	public void execute(Player player, String... params) {
		if (player.getVisualState() < 3) {
			player.getEffectController().setAbnormal(AbnormalState.HIDE.getId());
			player.setVisualState(CreatureVisualState.HIDE3);
			PacketSendUtility.broadcastPacket(player, new SM_PLAYER_STATE(player), true);
			PacketSendUtility.sendMessage(player, "You are invisible.");
		}
		else {
			player.getEffectController().unsetAbnormal(AbnormalState.HIDE.getId());
			player.unsetVisualState(CreatureVisualState.HIDE3);
			PacketSendUtility.broadcastPacket(player, new SM_PLAYER_STATE(player), true);
			PacketSendUtility.sendMessage(player, "You are visible.");
		}
	}

	@Override
	public void onFail(Player player, String message) {
		// TODO Auto-generated method stub
	}
}
