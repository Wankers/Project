
/**
 * @author Angelis,Thenice and TheLink
 */

package playercommands;

import gameserver.dao.ShopDAO;
import gameserver.model.gameobjects.player.Player;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.chathandlers.ChatCommand;
import commons.database.dao.DAOManager;


public class Shop extends ChatCommand {

	public Shop() {
		super("shop");
	}

        @Override
	public void execute(Player player, String... params) {
		if (!DAOManager.getDAO(ShopDAO.class).getAllItem(player)) {
			PacketSendUtility.sendMessage(player, "Inventaire plein !");
		}
	}
	
        
        @Override
	public void onFail(Player player, String message) {
		PacketSendUtility.sendMessage(player, "Syntax: .shop ");
	}
}