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
package gameserver.network.aion.clientpackets;

import java.util.ArrayList;
import java.util.Collection;

import gameserver.model.gameobjects.Item;
import gameserver.model.gameobjects.Npc;
import gameserver.model.gameobjects.player.Player;
import gameserver.network.aion.AionClientPacket;
import gameserver.network.aion.AionConnection.State;
import gameserver.services.item.ItemChargeService;

/**
 * @author ATracer
 */
public class CM_CHARGE_ITEM extends AionClientPacket {

	private int targetNpcObjectId;
	private int chargeLevel;
	private Collection<Integer> itemIds;

	public CM_CHARGE_ITEM(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	@Override
	protected void readImpl() {
		targetNpcObjectId = readD();
		chargeLevel = readC();
		int itemsSize = readH();
		itemIds = new ArrayList<Integer>();
		for (int i = 0; i < itemsSize; i++) {
			itemIds.add(readD());
		}

	}

	@Override
	protected void runImpl() {
		Player player = getConnection().getActivePlayer();
		if (!player.isTargeting(targetNpcObjectId)) {
			return; // TODO audit?
		}
		
		for (int itemObjId : itemIds) {
			Item item = player.getInventory().getItemByObjId(itemObjId);
			if (item != null) {
				int itemChargeLevel = item.getChargeLevel();
				int possibleChargeLevel = Math.min(itemChargeLevel, chargeLevel);
				if (possibleChargeLevel > 0) {
					if (ItemChargeService.processPayment(player, item, possibleChargeLevel))
						ItemChargeService.chargeItem(player, item, possibleChargeLevel);
				}
			}
		}
	}

}
