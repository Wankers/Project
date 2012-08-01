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
package gameserver.services.item;

import java.util.Collection;
import java.util.Collections;

import gameserver.model.DescriptionId;
import gameserver.model.EmotionType;
import gameserver.model.gameobjects.Creature;
import gameserver.model.gameobjects.Item;
import gameserver.model.gameobjects.Npc;
import gameserver.model.gameobjects.PersistentState;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.gameobjects.player.RequestResponseHandler;
import gameserver.model.items.ChargeInfo;
import gameserver.network.aion.serverpackets.SM_EMOTION;
import gameserver.network.aion.serverpackets.SM_INVENTORY_UPDATE_ITEM;
import gameserver.network.aion.serverpackets.SM_QUESTION_WINDOW;
import gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import gameserver.utils.PacketSendUtility;
import gameserver.world.World;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

/**
 * @author ATracer
 */
public class ItemChargeService {

	/**
	 * @return collection of items for conditioning
	 */
	public static final Collection<Item> filterItemsToCondition(Player player, Item selectedItem) {
		if (selectedItem != null) {
			return Collections.singletonList(selectedItem);
		}
		return Collections2.filter(player.getEquipment().getEquippedItems(), new Predicate<Item>() {

			@Override
			public boolean apply(Item item) {
				return item.getChargeLevel() != 0;
			}
		});
	}

	public static final void startChargingEquippedItems(final Player player) {
		
		final Collection<Item> filteredItems = filterItemsToCondition(player, null);
		long payAmount = 0;
		for (Item item : filteredItems) {
			payAmount += getPayAmountForService(item, item.getChargeLevel());
		}
		
		RequestResponseHandler request = new RequestResponseHandler(player) {

			@Override
			public void acceptRequest(Creature requester, Player responder) {
				for (Item item : filteredItems) {
					chargeItem(player, item, item.getChargeLevel());
				}
			}

			@Override
			public void denyRequest(Creature requester, Player responder) {
				// Nothing Happens
			}

		};

		if (player.getResponseRequester().putRequest(903026, request))
			PacketSendUtility.sendPacket(player, new SM_QUESTION_WINDOW(903026, player.getObjectId(), String.valueOf(payAmount)));
	}

	public static void chargeItems(Player player, Collection<Item> items, int level) {
		for (Item item : items) {
			chargeItem(player, item, level);
		}
	}

	public static void chargeItem(Player player, Item item, int level) {
		int currentCharge = item.getChargePoints();
		switch (level) {
			case 1:
				item.getConditioningInfo().updateChargePoints(ChargeInfo.LEVEL1 - currentCharge);
				break;
			case 2:
				item.getConditioningInfo().updateChargePoints(ChargeInfo.LEVEL2 - currentCharge);
				break;
		}
		PacketSendUtility.sendPacket(player, new SM_INVENTORY_UPDATE_ITEM(player, item));
		player.getEquipment().setPersistentState(PersistentState.UPDATE_REQUIRED);
		player.getInventory().setPersistentState(PersistentState.UPDATE_REQUIRED);
		PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_ITEM_CHARGE_SUCCESS(new DescriptionId(item.getNameID()), level));
		player.getGameStats().updateStatsVisually();
	}

	/**
	 * Pay for conditioning of item
	 */
	public static boolean processPayment(Player player, Item item, int level) {
		long requiredKinah = getPayAmountForService(item, level);
		if (player.getInventory().getKinah() < requiredKinah) {
			return false;
		}
		player.getInventory().decreaseKinah(requiredKinah);
		return true;
	}

	public static long getPayAmountForService(Item item, int chargeLevel) {
		switch (chargeLevel) {
			case 1:
				return item.getItemTemplate().getChargePrice1() / 2;
			case 2:
				switch (getNextChargeLevel(item)) {
					case 1:
						return (item.getItemTemplate().getChargePrice1() + item.getItemTemplate().getChargePrice2()) / 2;
					case 2:
						return item.getItemTemplate().getChargePrice2() / 2;
				}
		}
		return 0;
	}

	private static int getNextChargeLevel(Item item) {
		int charge = item.getChargePoints();
		if (charge < ChargeInfo.LEVEL1) {
			return 1;
		}
		if (charge < ChargeInfo.LEVEL2) {
			return 2;
		}
		throw new IllegalArgumentException("Invalid carge level");
	}

}
