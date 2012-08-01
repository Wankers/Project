package admincommands;

import gameserver.dataholders.DataManager;
import gameserver.model.gameobjects.player.Player;
import gameserver.services.AdminService;
import gameserver.services.item.ItemService;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.Util;
import gameserver.utils.chathandlers.ChatCommand;
import gameserver.world.World;

/**
 * @author Phantom, ATracer
 */

public class Add extends ChatCommand {

	public Add() {
		super("add");
	}

	@Override
	public void execute(Player player, String... params) {
		int itemId = 0;
		long itemCount = 1;
		Player receiver = null;

		try {
			itemId = Integer.parseInt(params[0]);

			if (params.length == 2) {
				itemCount = Long.parseLong(params[1]);
			}
			receiver = player;
		}
		catch (NumberFormatException e) {
			receiver = World.getInstance().findPlayer(Util.convertName(params[0]));
			if (receiver == null) {
				PacketSendUtility.sendMessage(player, "Could not find a player by that name.");
				return;
			}

			try {
				itemId = Integer.parseInt(params[1]);

				if (params.length == 3) {
					itemCount = Long.parseLong(params[2]);
				}
			}
			catch (NumberFormatException ex) {
				PacketSendUtility.sendMessage(player, "You must give number to itemid.");
				return;
			}
			catch (Exception ex2) {
				PacketSendUtility.sendMessage(player, "Occurs an error.");
				return;
			}
		}

		if (DataManager.ITEM_DATA.getItemTemplate(itemId) == null) {
			PacketSendUtility.sendMessage(player, "Item id is incorrect: " + itemId);
			return;
		}
		
		if(!AdminService.getInstance().canOperate(player, receiver, itemId, "command //add"))
			return;

		long count = ItemService.addItem(receiver, itemId, itemCount);

		if (count == 0) {
			if (params.length == 3) {
				PacketSendUtility.sendMessage(player, "You successfully gave" + params[2] + " x " + "[item:" + itemId + "]" + " to "
					+ params[0] + ".");
				PacketSendUtility.sendMessage(receiver, "You received an item " + params[2] + " x " + "[item:" + itemId + "]"
					+ " from the admin " + player.getName() + ".");
			}
			else
				PacketSendUtility.sendMessage(player, "You successfully gave 1 " + "[item:" + itemId + "]" + " to "
					+ params[0] + ".");
		}
		else {
			PacketSendUtility.sendMessage(player, "Item couldn't be added");
		}
	}

	@Override
	public void onFail(Player player, String message) {
		PacketSendUtility.sendMessage(player, "syntax //add <player> <item ID> <quantity>");
		PacketSendUtility.sendMessage(player, "syntax //add <item ID> <quantity>");
		PacketSendUtility.sendMessage(player, "syntax //add <item ID>");
	}
}
