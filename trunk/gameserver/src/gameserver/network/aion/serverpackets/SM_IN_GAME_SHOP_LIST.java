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

import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.Collection;
import java.util.List;

import javolution.util.FastList;

import gameserver.model.gameobjects.player.Player;
import gameserver.model.ingameshop.IGItem;
import gameserver.model.ingameshop.InGameShopEn;
import gameserver.network.aion.AionConnection;
import gameserver.network.aion.AionServerPacket;

/**
 * @author xTz, KID
 */
public class SM_IN_GAME_SHOP_LIST extends AionServerPacket {

	private Player player;
	private int nrList;
	private int salesRanking;
	private TIntObjectHashMap<FastList<IGItem>> allItems = new TIntObjectHashMap<FastList<IGItem>>();

	public SM_IN_GAME_SHOP_LIST(Player player, int nrList, int salesRanking) {
		this.player = player;
		this.nrList = nrList;
		this.salesRanking = salesRanking;
	}

	@Override
	protected void writeImpl(AionConnection con) {
		List<IGItem> inAllItems;
		Collection<IGItem> items = null;
		int category = player.getNrCategoryInGameShop();
		if (salesRanking == 1) {
			items = InGameShopEn.getInstance().getItems();
			int i = 0;
			int r = 9;
			int f = 0;
			for (IGItem a : items) {
				if(category != 2)
					if(a.getCategory() != category)
						continue;
				
				if (i == r) {
					r += 9;
					f++;
				}
				FastList<IGItem> template = allItems.get(f);
				if (template == null) {
					template = FastList.newInstance();
					allItems.put(f, template);
				}
				template.add(a);
				i++;
			}

			inAllItems = allItems.get(nrList);

			writeD(salesRanking);
			writeD(nrList);
			writeD(items.size());
			writeH(inAllItems == null ? 0 : inAllItems.size());
			if (inAllItems != null)
				for (IGItem item : inAllItems)
					writeD(item.getObjectId());
		}
		else {
			FastList<Integer> salesRankingItems = InGameShopEn.getInstance().getTopSales(category);
			writeD(salesRanking);
			writeD(nrList);
			writeD((InGameShopEn.getInstance().getMaxList(category) + 1) * 9);
			writeH(salesRankingItems.size());
			for (int id : salesRankingItems)
				writeD(id);
					
			FastList.recycle(salesRankingItems);
		}
	}
}
