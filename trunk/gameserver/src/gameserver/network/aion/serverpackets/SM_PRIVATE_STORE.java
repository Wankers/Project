/*
 * This file is part of aion-unique <www.aion-unique.com>.
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
 *  along with Aion Extreme Emulator. If not, see <http://www.gnu.org/licenses/>.
 */
package gameserver.network.aion.serverpackets;

import java.util.LinkedHashMap;

import gameserver.model.gameobjects.Item;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.gameobjects.player.PrivateStore;
import gameserver.model.trade.TradePSItem;
import gameserver.network.aion.AionConnection;
import gameserver.network.aion.AionServerPacket;
import gameserver.network.aion.iteminfo.ItemInfoBlob;

/**
 * @author Simple
 */
public class SM_PRIVATE_STORE extends AionServerPacket {

	private Player player;
	/** Private store Information **/
	private PrivateStore store;

	public SM_PRIVATE_STORE(PrivateStore store, Player player) {
		this.player = player;
		this.store = store;
	}

	@Override
	protected void writeImpl(AionConnection con) {
		if (store != null) {
			Player storePlayer = store.getOwner();
			LinkedHashMap<Integer, TradePSItem> soldItems = store.getSoldItems();

			writeD(storePlayer.getObjectId());
			writeH(soldItems.size());
			for (Integer itemObjId : soldItems.keySet()) {
				Item item = storePlayer.getInventory().getItemByObjId(itemObjId);
				TradePSItem tradeItem = store.getTradeItemByObjId(itemObjId);
				long price = tradeItem.getPrice();
				writeD(itemObjId);
				writeD(item.getItemTemplate().getTemplateId());
				writeH((int) tradeItem.getCount());
				writeD((int) price);

				ItemInfoBlob itemInfoBlob = ItemInfoBlob.getFullBlob(player, item);
				itemInfoBlob.writeMe(getBuf());
			}
		}
	}
}
