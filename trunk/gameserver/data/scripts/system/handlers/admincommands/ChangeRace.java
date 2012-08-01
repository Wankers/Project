package admincommands;

import gameserver.model.Race;
import gameserver.model.gameobjects.player.Player;
import gameserver.network.aion.serverpackets.SM_PLAYER_INFO;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.chathandlers.ChatCommand;

/**
* @author ginho1
*
*/
public class ChangeRace extends ChatCommand {
	public ChangeRace() {
		super("changerace");
	}

	@Override
	public void execute(Player admin, String... params) {

		if(admin.getCommonData().getRace() == Race.ELYOS)
			admin.getCommonData().setRace(Race.ASMODIANS);
		else
			admin.getCommonData().setRace(Race.ELYOS);

		admin.clearKnownlist();
		PacketSendUtility.sendPacket(admin, new SM_PLAYER_INFO(admin, false));
		admin.updateKnownlist();
	}
}
