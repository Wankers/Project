/**
 * This file is part of aion-unique <aion-unique.smfnew.com>.
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
package gameserver.network.aion.clientpackets;

import gameserver.model.DescriptionId;
import gameserver.model.gameobjects.Item;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.items.storage.Storage;
import gameserver.model.templates.item.EquipType;
import gameserver.network.aion.AionClientPacket;
import gameserver.network.aion.AionConnection.State;
import gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import gameserver.services.item.ItemPacketService.ItemDeleteType;
import gameserver.utils.PacketSendUtility;

/**
 * @author Avol
 */
public class CM_DELETE_ITEM extends AionClientPacket {

	public int itemObjectId;

	public CM_DELETE_ITEM(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	@Override
	protected void readImpl() {
		itemObjectId = readD();
	}

	@Override
	protected void runImpl() {

		Player player = getConnection().getActivePlayer();
		Storage inventory = player.getInventory();
		Item item = inventory.getItemByObjId(itemObjectId);

		if (item != null) {
			if (!item.getItemTemplate().isBreakable() && !isEquipable(item)) {
				PacketSendUtility.sendPacket(player,
					SM_SYSTEM_MESSAGE.STR_UNBREAKABLE_ITEM(new DescriptionId(item.getNameID())));
			}
			else {
				inventory.delete(item, ItemDeleteType.DISCARD);
			}
		}
	}
	
	// We should allow the destruction/drop of equip-able items etc from surveys even though they are masked as indestructable / unbreakable
	public boolean isEquipable(Item item){
		if(item.getEquipmentType() == EquipType.ARMOR || item.getEquipmentType() == EquipType.WEAPON){
			return true;
		}else
			return false;
	}
}
