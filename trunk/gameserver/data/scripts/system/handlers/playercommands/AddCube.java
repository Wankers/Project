package playercommands;

import gameserver.model.gameobjects.player.Player;
import gameserver.services.CubeExpandService;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.Util;
import gameserver.utils.chathandlers.ChatCommand;
import gameserver.world.World;

/**
 * @author Kamui
 *
 */
public class AddCube extends ChatCommand {


	public AddCube() {
		super("cube");
	}

	@Override
	public void execute(Player player, String... params) {
            if (player.getNpcExpands() < 9) {
                    CubeExpandService.expand(player, true);
                    PacketSendUtility.sendMessage(player, "Vous venez de recevoir toutes les extensions de votre inventaire.");
            }
            else {
                    PacketSendUtility.sendMessage(player, "Aucune extension n'est disponible pour votre inventaire.");
            }
	}
	
	@Override
	public void onFail(Player admin, String message) {
		PacketSendUtility.sendMessage(admin, "Syntaxe : .cube");
	}
}
