/*
 * This file is part of Aion Extreme Emulator  <aion-core.net>.
 *
 *  Aion Extreme Emulator is a free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Aion Extreme Emulator is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Aion Extreme Emulator.  If not, see <http://www.gnu.org/licenses/>.
 */
package gameserver.network.aion.clientpackets;

import gameserver.configs.main.InGameShopConfig;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.ingameshop.InGameShopEn;
import gameserver.network.aion.AionClientPacket;
import gameserver.network.aion.AionConnection.State;
import gameserver.network.aion.serverpackets.SM_IN_GAME_SHOP_CATEGORY_LIST;
import gameserver.network.aion.serverpackets.SM_IN_GAME_SHOP_ITEM;
import gameserver.network.aion.serverpackets.SM_IN_GAME_SHOP_LIST;
import gameserver.network.aion.serverpackets.SM_TOLL_INFO;
import gameserver.utils.PacketSendUtility;

/**
 * @author xTz, KID
 */
public class CM_IN_GAME_SHOP_INFO extends AionClientPacket {
	private int actionId;
	private int categoryId;
	private int listInCategory;
	private String senderName;
	private String senderMessage;

	public CM_IN_GAME_SHOP_INFO(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	@Override
	protected void readImpl() {
		actionId = readC();
		categoryId = readD();
		listInCategory = readD();
		senderName = readS();
		senderMessage = readS();
	}

	@Override
	protected void runImpl() {
		if (InGameShopConfig.ENABLE_IN_GAME_SHOP) {
			Player player = getConnection().getActivePlayer();

			switch (actionId) {
				case 0x01:
					PacketSendUtility.sendPacket(player, new SM_IN_GAME_SHOP_ITEM(player, categoryId));
					break;
				case 0x08://showcat
					if (categoryId > 1)
						player.setNrCategoryInGameShop(categoryId);

					PacketSendUtility.sendPacket(player, new SM_IN_GAME_SHOP_LIST(player, listInCategory, 1));
					PacketSendUtility.sendPacket(player, new SM_IN_GAME_SHOP_LIST(player, listInCategory, 0));
					break;
				case 0x10:
					PacketSendUtility.sendPacket(player, new SM_IN_GAME_SHOP_CATEGORY_LIST());
					PacketSendUtility.sendPacket(player, new SM_TOLL_INFO(player.getClientConnection().getAccount().getToll()));
					break;
				case 0x20: //buy
					InGameShopEn.getInstance().acceptRequest(player, categoryId);
					break;
				case 0x40: //gift
					InGameShopEn.getInstance().sendRequest(player, senderName, senderMessage, categoryId);
					break;
			}
		}
	}
}
