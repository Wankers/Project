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

import gameserver.network.PacketWriteHelper;
import gameserver.network.aion.iteminfo.ItemInfoBlob.ItemBlobType;

/**
 * ItemInfo blob entry (contains detailed item info).
 * 
 * @author -Nemesiss-
 *
 */
public abstract class ItemBlobEntry extends PacketWriteHelper{
	private final ItemBlobType type;
	private ItemBlobEntry nextBlob;
	ItemInfoBlob parent;

	ItemBlobEntry(ItemBlobType type) {
		this.type = type;
	}

	void setParent(ItemInfoBlob parent) {
		this.parent = parent;
	}

	void addBlobEntry(ItemBlobEntry ent) {
		if(nextBlob == null)
			nextBlob = ent;
		else
			nextBlob.addBlobEntry(ent);
	}

	@Override
	protected	void writeMe(ByteBuffer buf) {
		writeC(buf, type.getEntryId());
		writeThisBlob(buf);
		if(nextBlob != null)
			nextBlob.writeMe(buf);
	}

	public abstract void writeThisBlob(ByteBuffer buf);
}
