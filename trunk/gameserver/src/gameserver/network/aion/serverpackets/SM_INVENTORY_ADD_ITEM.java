/*
 * This file is part of aion-unique <aionunique.com>.
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

import java.util.List;

import gameserver.model.gameobjects.Item;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.items.storage.ItemStorage;
import gameserver.model.templates.item.ItemTemplate;
import gameserver.network.aion.AionConnection;
import gameserver.network.aion.AionServerPacket;
import gameserver.network.aion.iteminfo.ItemInfoBlob;

/**
 * @author ATracer
 */
public class SM_INVENTORY_ADD_ITEM extends AionServerPacket {

	private final List<Item> items;
	private final int size;
	private Player player;

	public SM_INVENTORY_ADD_ITEM(List<Item> items, Player player) {
		this.player = player;
		this.items = items;
		this.size = items.size();
	}

	@Override
	protected void writeImpl(AionConnection con) {
		//TODO! why its not use ItemAddType!?
		// 0x1C after buy, 0x35 after quest, 0x40 questionnaire;
		int mask = (size == 1 && items.get(0).getEquipmentSlot() != ItemStorage.FIRST_AVAILABLE_SLOT) ? 0x07 : 0x19;
		writeH(mask); //
		writeH(size); // number of entries
		for (Item item : items)
			writeItemInfo(item);
	}

	private void writeItemInfo(Item item) {
		ItemTemplate itemTemplate = item.getItemTemplate();

		writeD(item.getObjectId());
		writeD(itemTemplate.getTemplateId());
		writeNameId(itemTemplate.getNameId());

		ItemInfoBlob itemInfoBlob = ItemInfoBlob.getFullBlob(player, item);
		itemInfoBlob.writeMe(getBuf());

		writeH(item.isEquipped() ? 255 : item.getEquipmentSlot()); // FF FF equipment
		writeC(0x00);//isEquiped?
	}
}
