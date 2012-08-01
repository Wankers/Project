package admincommands;

import commons.database.dao.DAOManager;
import gameserver.dao.InGameShopDAO;
import gameserver.dataholders.DataManager;
import gameserver.model.gameobjects.VisibleObject;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.ingameshop.InGameShopEn;
import gameserver.model.templates.item.ItemTemplate;
import gameserver.network.aion.serverpackets.SM_TOLL_INFO;
import gameserver.network.loginserver.LoginServer;
import gameserver.network.loginserver.serverpackets.SM_ACCOUNT_TOLL_INFO;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.Util;
import gameserver.utils.chathandlers.ChatCommand;
import gameserver.utils.idfactory.IDFactory;
import gameserver.world.World;

/**
 * @author xTz
 */
public class Gameshop extends ChatCommand {

	public Gameshop() {
		super("gameshop");
	}

	@Override
	public void execute(Player admin, String... params) {
		if (params.length == 0 || params.length > 7) {
			onFail(admin, null);
			return;
		}
		int itemId, count, price, category, list, toll;
                int id = IDFactory.getInstance().nextId();
		Player player = null;

		if ("delete".startsWith(params[0])) {
			try {
				itemId = Integer.parseInt(params[1]);
				category = Integer.parseInt(params[2]);
				list = Integer.parseInt(params[3]);
			}
			catch (NumberFormatException e) {
				PacketSendUtility.sendMessage(admin, "<itemId, category, list> values must be an integer.");
				return;
			}
			DAOManager.getDAO(InGameShopDAO.class).deleteIngameShopItem(itemId, category, list - 1);
			PacketSendUtility.sendMessage(admin, "You remove [item:" + itemId + "]");
		}
		else if ("add".startsWith(params[0])) {
			try {
				itemId = Integer.parseInt(params[1]);
				count = Integer.parseInt(params[2]);
				price = Integer.parseInt(params[3]);
				category = Integer.parseInt(params[4]);
				list = Integer.parseInt(params[5]);
			}
			catch (NumberFormatException e) {
				PacketSendUtility.sendMessage(admin, "<itemId, count, price, category, list> values must be an integer.");
				return;
			}
			String description = Util.convertName(params[6]);

			if (list < 1) {
				PacketSendUtility.sendMessage(admin, "<list> : minium is 1.");
				return;
			}

			if (category < 3 || category > 19) {
				PacketSendUtility.sendMessage(admin, "<category> : minimum is 3, maximum is 19.");
				return;
			}

			ItemTemplate itemTemplate = DataManager.ITEM_DATA.getItemTemplate(itemId);
			if (itemTemplate == null) {
				PacketSendUtility.sendMessage(admin, "Item id is incorrect: " + itemId);
				return;
			}

			DAOManager.getDAO(InGameShopDAO.class).saveIngameShopItem(id, itemId, count, price, category, list - 1, 1, description);
            PacketSendUtility.sendMessage(admin, "You added [item:" + itemId + "]");
		}
		else if ("deleteranking".startsWith(params[0])) {
			try {
				itemId = Integer.parseInt(params[1]);
			}
			catch (NumberFormatException e) {
				PacketSendUtility.sendMessage(admin, "<itemId> value must be an integer.");
				return;
			}
			DAOManager.getDAO(InGameShopDAO.class).deleteIngameShopItem(itemId, -1, -1);
            PacketSendUtility.sendMessage(admin, "You removed from Ranking Sales [item:" + itemId + "]");
		}
		else if ("addranking".startsWith(params[0])) {
			try {
				itemId = Integer.parseInt(params[1]);
				count = Integer.parseInt(params[2]);
				price = Integer.parseInt(params[3]);
			}
			catch (NumberFormatException e) {
				PacketSendUtility.sendMessage(admin, "<itemId, count, price> value must be an integer.");
				return;
			}
			String description = Util.convertName(params[4]);

			ItemTemplate itemTemplate = DataManager.ITEM_DATA.getItemTemplate(itemId);

			if (itemTemplate == null) {
				PacketSendUtility.sendMessage(admin, "Item id is incorrect: " + itemId);
				return;
			}

            DAOManager.getDAO(InGameShopDAO.class).saveIngameShopItem(id, itemId, count, price, -1, -1, 0, description);
			PacketSendUtility.sendMessage(admin, "You remove from Ranking Sales [item:" + itemId + "]");
		}
		else if ("settoll".startsWith(params[0])) {
			if (params.length == 3) {
				try {
					toll = Integer.parseInt(params[2]);
				}
				catch (NumberFormatException e) {
					PacketSendUtility.sendMessage(admin, "<toll> value must be an integer.");
					return;
				}

				String name = Util.convertName(params[1]);

				player = World.getInstance().findPlayer(name);
				if (player == null) {
					PacketSendUtility.sendMessage(admin, "The specified player is not online.");
					return;
				}

				if (LoginServer.getInstance().sendPacket(new SM_ACCOUNT_TOLL_INFO(toll, player.getAcountName()))) {
					player.getClientConnection().getAccount().setToll(toll);
					PacketSendUtility.sendPacket(player, new SM_TOLL_INFO(toll));
					PacketSendUtility.sendMessage(admin, "Tolls setted to " + toll + ".");
				}
				else
					PacketSendUtility.sendMessage(admin, "ls communication error.");
			}
			if (params.length == 2) {
				try {
					toll = Integer.parseInt(params[1]);
				}
				catch (NumberFormatException e) {
					PacketSendUtility.sendMessage(admin, "<toll> value must be an integer.");
					return;
				}

				if (toll < 0) {
					PacketSendUtility.sendMessage(admin, "<toll> must > 0.");
					return;
				}

				VisibleObject target = admin.getTarget();
				if (target == null) {
					PacketSendUtility.sendMessage(admin, "You should select a target first!");
					return;
				}

				if (target instanceof Player) {
					player = (Player) target;
				}

				if (LoginServer.getInstance().sendPacket(new SM_ACCOUNT_TOLL_INFO(toll, player.getAcountName()))) {
					player.getClientConnection().getAccount().setToll(toll);
					PacketSendUtility.sendPacket(player, new SM_TOLL_INFO(toll));
					PacketSendUtility.sendMessage(admin, "Tolls setted to " + toll + ".");
				}
				else
					PacketSendUtility.sendMessage(admin, "ls communication error.");
			}
		}
		else if ("addtoll".startsWith(params[0])) {
			if (params.length == 3) {
				try {
					toll = Integer.parseInt(params[2]);
				}
				catch (NumberFormatException e) {
					PacketSendUtility.sendMessage(admin, "<toll> value must be an integer.");
					return;
				}
				
				if (toll < 0) {
					PacketSendUtility.sendMessage(admin, "<toll> must > 0.");
					return;
				}

				String name = Util.convertName(params[1]);

				player = World.getInstance().findPlayer(name);
				if (player == null) {
					PacketSendUtility.sendMessage(admin, "The specified player is not online.");
					return;
				}

				PacketSendUtility.sendMessage(admin, "You added " + toll + " tolls to Player: " + name);
				InGameShopEn.getInstance().addToll(player, toll);
			}
			if (params.length == 2) {
				try {
					toll = Integer.parseInt(params[1]);
				}
				catch (NumberFormatException e) {
					PacketSendUtility.sendMessage(admin, "<toll> value must be an integer.");
					return;
				}

				VisibleObject target = admin.getTarget();
				if (target == null) {
					PacketSendUtility.sendMessage(admin, "You should select a target first!");
					return;
				}

				if (target instanceof Player) {
					player = (Player) target;
				}

				PacketSendUtility.sendMessage(admin, "You added " + toll + " tolls to Player: " + player.getName());
				InGameShopEn.getInstance().addToll(player, toll);
			}
		}
		else {
			PacketSendUtility.sendMessage(admin,
				"You can use only, addtoll, settoll, deleteranking, addranking, delete or add.");
		}
	}

	@Override
	public void onFail(Player player, String message) {
		PacketSendUtility.sendMessage(player, "No parameters detected please use:\n"
			+ "//gameshop add <itemId> <count> <price> <category> <list> <description>\n"
			+ "//gameshop delete <itemId> <category> <list>\n"
			+ "//gameshop addranking <itemId> <count> <price> <description>\n" + "//gameshop deleteranking <itemId>\n"
			+ "//gameshop settoll <target|player> <toll>\n" + "//gameshop addtoll <target|player> <toll>");
	}
}
