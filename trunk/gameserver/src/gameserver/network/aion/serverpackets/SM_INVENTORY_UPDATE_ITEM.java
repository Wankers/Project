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

import gameserver.model.gameobjects.Item;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.templates.item.ItemTemplate;
import gameserver.network.aion.AionConnection;
import gameserver.network.aion.AionServerPacket;
import gameserver.network.aion.iteminfo.ItemInfoBlob;
import gameserver.network.aion.iteminfo.ItemInfoBlob.ItemBlobType;
import gameserver.services.item.ItemPacketService.ItemUpdateType;

/**
 * @author ATracer
 * @author -Nemesiss-
 */
public class SM_INVENTORY_UPDATE_ITEM extends AionServerPacket {

	private final Player player;
	private final Item item;
	private final ItemUpdateType updateType;
	private boolean stones = false;
	private boolean fusion = false;
	private boolean armsFusion = false;

	public SM_INVENTORY_UPDATE_ITEM(Player player, Item item) {
		this(player, item, ItemUpdateType.DEFAULT);
	}
	
	public SM_INVENTORY_UPDATE_ITEM(Player player, Item item, ItemUpdateType updateType) {
		this.player = player;
		this.item = item;
		this.updateType = updateType;
	}	

	@Override
	protected void writeImpl(AionConnection con) {
		ItemTemplate itemTemplate = item.getItemTemplate();

		writeD(item.getObjectId());
		writeNameId(itemTemplate.getNameId());

		ItemInfoBlob itemInfoBlob;
		switch (updateType) {
			case EQUIP_UNEQUIP:
				itemInfoBlob = new ItemInfoBlob(player, item);
				itemInfoBlob.addBlobEntry(ItemBlobType.EQUIPPED_SLOT);
				break;
			default:
				itemInfoBlob = ItemInfoBlob.getFullBlob(player, item);
				break;
		}
		itemInfoBlob.writeMe(getBuf());

		if (updateType.isSendable())
			writeH(updateType.getMask());
	}
}
