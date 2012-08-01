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
package gameserver.network.aion.iteminfo;

import java.nio.ByteBuffer;

import gameserver.model.gameobjects.Item;
import gameserver.model.items.ItemSlot;
import gameserver.network.aion.iteminfo.ItemInfoBlob.ItemBlobType;

/**
 * This blob is sent for weapons. It keeps info about slots that weapon can be equipped to.
 * 
 * @author -Nemesiss-
 *
 */
public class WeaponInfoBlobEntry extends ItemBlobEntry{

	WeaponInfoBlobEntry() {
		super(ItemBlobType.SLOTS_WEAPON);
	}

	@Override
	public
	void writeThisBlob(ByteBuffer buf) {
		Item item = parent.item;

		writeD(buf, ItemSlot.getSlotsFor(item.getItemTemplate().getItemSlot())[0].getSlotIdMask());
		writeD(buf, item.hasFusionedItem() ? 0x00 : 0x02);//TODO! isn't it secondary slot?
		//writeH(buf, item.getUnSeal() ? 1 : 0);
	}
}