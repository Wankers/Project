/*
 *  This file is part of Aion Extreme Emulator <aion-core.net>.
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

import java.util.Collection;
import java.util.Collections;

import gameserver.model.gameobjects.Item;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.templates.item.ItemTemplate;
import gameserver.network.aion.AionConnection;
import gameserver.network.aion.AionServerPacket;
import gameserver.network.aion.iteminfo.ItemInfoBlob;

/**
 * @author kosyachok
 */
public class SM_WAREHOUSE_INFO extends AionServerPacket {

	private int warehouseType;
	private Collection<Item> itemList;
	private boolean firstPacket;
	private int expandLvl;
	private Player player;

	public SM_WAREHOUSE_INFO(Collection<Item> items, int warehouseType, int expandLvl, boolean firstPacket, Player player) {
		this.warehouseType = warehouseType;
		this.expandLvl = expandLvl;
		this.firstPacket = firstPacket;
		if (items == null)
			this.itemList = Collections.emptyList();
		else
			this.itemList = items;
		this.player = player;
	}

	@Override
	protected void writeImpl(AionConnection con) {
		writeC(warehouseType);
		writeC(firstPacket ? 1 : 0);
		writeC(expandLvl); // warehouse expand (0 - 9)
		writeH(0);
		writeH(itemList.size());
		for (Item item : itemList)
			writeItemInfo(item);
	}

	private void writeItemInfo(Item item) {
		ItemTemplate itemTemplate = item.getItemTemplate();

		writeD(item.getObjectId());
		writeD(itemTemplate.getTemplateId());
		writeC(0x2B); // some item info (4 - weapon, 7 - armor, 8 - rings, 17 - bottles)
		writeH(0x24);
		writeNameId(itemTemplate.getNameId());

		ItemInfoBlob itemInfoBlob = ItemInfoBlob.getFullBlob(player, item);
		itemInfoBlob.writeMe(getBuf());

		writeH(item.isEquipped() ? 255 : item.getEquipmentSlot()); // FF FF equipment
	}
}
