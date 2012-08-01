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


import gameserver.network.aion.AionConnection;
import gameserver.network.aion.AionServerPacket;
import gameserver.services.item.ItemPacketService.ItemDeleteType;

/**
 * @author kosyachok
 */
public class SM_DELETE_WAREHOUSE_ITEM extends AionServerPacket {

	private int warehouseType;
	private int itemObjId;
	private ItemDeleteType deleteType;

	public SM_DELETE_WAREHOUSE_ITEM(int warehouseType, int itemObjId, ItemDeleteType deleteType) {
		this.warehouseType = warehouseType;
		this.itemObjId = itemObjId;
		this.deleteType = deleteType;
	}

	@Override
	protected void writeImpl(AionConnection con) {
		writeC(warehouseType);
		writeD(itemObjId);
		writeC(deleteType.getMask());
	}

}
