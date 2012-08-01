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

import gameserver.configs.main.InGameShopConfig;
import gameserver.network.aion.AionConnection;
import gameserver.network.aion.AionServerPacket;

/**
 * @author xTz
 */
public class SM_IN_GAME_SHOP_CATEGORY_LIST extends AionServerPacket {

	@Override
	protected void writeImpl(AionConnection con) {
		writeD(2);
		writeH(17); // Category Size
		writeD(3); // Category ID
		writeS(InGameShopConfig.CATEGORY_3); // Category Name
		writeD(4);
		writeS(InGameShopConfig.CATEGORY_4);
		writeD(5);
		writeS(InGameShopConfig.CATEGORY_5);
		writeD(6);
		writeS(InGameShopConfig.CATEGORY_6);
		writeD(7);
		writeS(InGameShopConfig.CATEGORY_7);
		writeD(8);
		writeS(InGameShopConfig.CATEGORY_8);
		writeD(9);
		writeS(InGameShopConfig.CATEGORY_9);
		writeD(10);
		writeS(InGameShopConfig.CATEGORY_10);
		writeD(11);
		writeS(InGameShopConfig.CATEGORY_11);
		writeD(12);
		writeS(InGameShopConfig.CATEGORY_12);
		writeD(13);
		writeS(InGameShopConfig.CATEGORY_13);
		writeD(14);
		writeS(InGameShopConfig.CATEGORY_14);
		writeD(15);
		writeS(InGameShopConfig.CATEGORY_15);
		writeD(16);
		writeS(InGameShopConfig.CATEGORY_16);
		writeD(17);
		writeS(InGameShopConfig.CATEGORY_17);
		writeD(18);
		writeS(InGameShopConfig.CATEGORY_18);
		writeD(19);
		writeS(InGameShopConfig.CATEGORY_19);
	}
}
