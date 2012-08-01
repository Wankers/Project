package loginserver.network.gameserver.clientpackets;

import loginserver.controller.PremiumController;
import loginserver.network.gameserver.GsClientPacket;

/**
 * @author KID
 */
public class CM_PREMIUM_CONTROL extends GsClientPacket {

	private int accountId;
	private int requestId;
	private int requiredCost;
	private byte serverId;

	@Override
	protected void readImpl() {
		accountId = readD();
		requestId = readD();
		requiredCost = readD();
		serverId = (byte)readC();
	}
	
	@Override
	protected void runImpl() {
		PremiumController.getController().requestBuy(accountId, requestId, requiredCost, serverId);
	}
}
