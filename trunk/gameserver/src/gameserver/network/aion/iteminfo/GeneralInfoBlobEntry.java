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
import gameserver.network.aion.iteminfo.ItemInfoBlob.ItemBlobType;


/**
 * This blob entry is sent with ALL items. (unless partial blob is constructed, ie: sending equip slot only) It is the first
 * and only block for non-equipable items, and the last blob for EquipableItems
 *
 * @author -Nemesiss-
 *
 */
public class GeneralInfoBlobEntry extends ItemBlobEntry{

	GeneralInfoBlobEntry() {
		super(ItemBlobType.GENERAL_INFO);
	}

	@Override
	public
	void writeThisBlob(ByteBuffer buf) {//TODO what with kinah?
		Item item = parent.item;
		writeH(buf, item.getItemMask(parent.player));
		writeQ(buf, item.getItemCount());
		writeS(buf, item.getItemCreator());// Creator name
		writeC(buf, 0);
		writeD(buf, item.getExpireTimeRemaining()); // Disappears time
		writeH(buf, 0);
		writeH(buf, 0);
		writeD(buf, item.getTemporaryExchangeTimeRemaining());
		writeH(buf, item.getUnSeal());
		writeD(buf, 0);
	}
}
