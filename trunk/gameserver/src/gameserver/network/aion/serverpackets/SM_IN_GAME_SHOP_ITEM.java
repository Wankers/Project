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
package gameserver.network.aion.serverpackets;

import gameserver.model.gameobjects.player.Player;
import gameserver.model.ingameshop.IGItem;
import gameserver.model.ingameshop.InGameShopEn;
import gameserver.network.aion.AionConnection;
import gameserver.network.aion.AionServerPacket;

/**
 * @author xTz, KID
 */
public class SM_IN_GAME_SHOP_ITEM extends AionServerPacket {

	private IGItem item;
	public SM_IN_GAME_SHOP_ITEM(Player player, int objectItem) {
		item = InGameShopEn.getInstance().getIGItem(objectItem);
	}

	@Override
	protected void writeImpl(AionConnection con) {
		writeD(item.getObjectId()); // nrItem
		writeD(item.getItemPrice()); // price
		writeD(0);
		writeH(0);
		writeD(item.getItemId()); // itemId
		writeD(item.getItemCount()); // itemCount
		writeD(0); // unk
		writeD(0); // unk
		writeD(0); // unk
		writeD(0); // unk
		writeD(0); // unk
		writeH(0); // unk
		writeC(0); // unk
		writeS(item.getDescription());
		writeH(0);
	}
}
