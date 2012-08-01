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
import java.util.Set;

import gameserver.model.gameobjects.Item;
import gameserver.model.items.ItemStone;
import gameserver.model.items.ManaStone;
import gameserver.model.stats.calc.functions.StatFunction;
import gameserver.network.aion.iteminfo.ItemInfoBlob.ItemBlobType;

/**
 * This blob sends info about mana stones.
 * 
 * @author -Nemesiss-
 *
 */
public class ManaStoneInfoBlobEntry extends ItemBlobEntry{

	ManaStoneInfoBlobEntry() {
		super(ItemBlobType.MANA_SOCKETS);
	}

	@Override
	public
	void writeThisBlob(ByteBuffer buf) {
		Item item = parent.item;

		writeC(buf, item.isSoulBound() ? 1 : 0);
		writeC(buf, item.getEnchantLevel()); // enchant (1-15)
		writeD(buf, item.getItemSkinTemplate().getTemplateId());
		writeC(buf, item.getOptionalSocket());

		writeItemStones(buf);

		ItemStone god = item.getGodStone();
		writeD(buf, god == null ? 0 : god.getItemId());

		writeD(buf, 0);

		writeD(buf, 0);// unk 1.5.1.9
		writeD(buf, 0);// unk 2.7
		writeC(buf, 0);//unk
	}

	/**
	 * Writes manastones : 6C - statenum mask, 6H - value
	 * 
	 * @param item
	 */
	private void writeItemStones(ByteBuffer buf) {
		Item item = parent.item;
		int count = 0;

		if (item.hasManaStones()) {
			Set<ManaStone> itemStones = item.getItemStones();

			for (ManaStone itemStone : itemStones) {
				if (count == 6)
					break;

				StatFunction modifier = itemStone.getFirstModifier();
				if (modifier != null) {
					count++;
					writeH(buf, modifier.getName().getItemStoneMask());
				}
			}
			skip(buf, (6 - count) * 2);
			count = 0;
			for (ManaStone itemStone : itemStones) {
				if (count == 6)
					break;

				StatFunction modifier = itemStone.getFirstModifier();
				if (modifier != null) {
					count++;
					writeH(buf, modifier.getValue());
				}
			}
			skip(buf, (6 - count) * 2);
		}
		else {
			skip(buf, 24);
		}
		// for now max 6 stones - write some junk
	}
}
