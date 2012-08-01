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

import java.util.Collection;

import gameserver.model.gameobjects.Item;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.templates.item.ItemTemplate;
import gameserver.network.aion.AionConnection;
import gameserver.network.aion.AionServerPacket;
import gameserver.network.aion.iteminfo.ItemInfoBlob;
import gameserver.services.RepurchaseService;

/**
 * @author xTz, KID
 */
public class SM_REPURCHASE extends AionServerPacket {

	private Player player;
	private final int targetObjectId;
	private final Collection<Item> items;

	public SM_REPURCHASE(Player player, int npcId) {
		this.player = player;
		this.targetObjectId = npcId;
		items = RepurchaseService.getInstance().getRepurchaseItems(player.getObjectId());
	}

	@Override
	protected void writeImpl(AionConnection con) {
		writeD(targetObjectId);
		writeD(1);
		writeH(items.size());

		for (Item item : items) {
			ItemTemplate itemTemplate = item.getItemTemplate();

			writeD(item.getObjectId());
			writeD(itemTemplate.getTemplateId());
			writeNameId(itemTemplate.getNameId());

			ItemInfoBlob itemInfoBlob = ItemInfoBlob.getFullBlob(player, item);
			itemInfoBlob.writeMe(getBuf());

			writeQ(item.getRepurchasePrice());
		}
	}
}
