package playercommands;

import gameserver.model.gameobjects.player.Player;
import gameserver.model.ingameshop.InGameShopEn;
import gameserver.services.abyss.AbyssPointsService;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.chathandlers.ChatCommand;

/**
 * @author Tiger
 */
public class cmd_toll extends ChatCommand {

	public cmd_toll() {
		super("toll");
	}

	@Override
	public void execute(Player player, String... params) {
		if (params == null || params.length < 2) {
			PacketSendUtility.sendMessage(player, ".toll <ap | kinah> <value>" + "\nAp 1,000:1 : Kinah 150,000:1");
			return;
		}
		int toll;
		try {
			toll = Integer.parseInt(params[1]);
		}
		catch (NumberFormatException e) {
			return;
		}
		if(toll > 1000){
			PacketSendUtility.sendMessage(player, "Too large.");
			return;
		}
		if (params[0].equals("ap") && toll > 0) {
			int PlayerAbyssPoints = player.getAbyssRank().getAp();
			int pointsLost = (toll * 1000);
			if (PlayerAbyssPoints < pointsLost) {
				PacketSendUtility.sendMessage(player, "You don't have enough Ap.");
				return;
			}
			AbyssPointsService.addAp(player, -pointsLost);
			addtoll(player, toll);
		} else if (params[0].equals("kinah") && toll > 0) {
			int pointsLost = (toll * 150000);
			if (player.getInventory().getKinah() < pointsLost) {
				PacketSendUtility.sendMessage(player, "You don't have enough Kinah.");
				return;
			}
			player.getInventory().decreaseKinah(pointsLost);
			addtoll(player, toll);
		} else {
			PacketSendUtility.sendMessage(player, "value is incorrect.");
			return;
		}
	}

	private void addtoll(Player player, int toll) {
		InGameShopEn.getInstance().addToll(player, toll);
	}

	@Override
	public void onFail(Player player, String message) {
		// TODO Auto-generated method stub
	}
}