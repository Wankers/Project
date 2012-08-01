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

import static gameserver.services.item.ItemPacketService.sendItemDeletePacket;
import static gameserver.services.item.ItemPacketService.sendStorageUpdatePacket;

import java.util.List;

import gameserver.model.gameobjects.Item;
import gameserver.model.gameobjects.PersistentState;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.items.storage.IStorage;
import gameserver.model.items.storage.StorageType;
import gameserver.services.ExchangeService;
import gameserver.services.LegionService;
import gameserver.services.item.ItemPacketService.ItemDeleteType;

/**
 * @author ATracer
 */
public class ItemMoveService {

	public static void moveItem(Player player, int itemObjId, byte sourceStorageType, byte destinationStorageType,
		short slot) {
		if (ExchangeService.getInstance().isPlayerInExchange(player))
			return;

		IStorage sourceStorage = player.getStorage(sourceStorageType);
		Item item = player.getStorage(sourceStorageType).getItemByObjId(itemObjId);

		if (item == null)
			return;

		if (sourceStorageType == destinationStorageType) {
			moveInSameStorage(sourceStorage, item, slot);
			return;
		}

		if (sourceStorageType != destinationStorageType
			&& (ItemRestrictionService.isItemRestrictedTo(player, item, destinationStorageType) || ItemRestrictionService
				.isItemRestrictedFrom(player, item, sourceStorageType))) {
			sendStorageUpdatePacket(player, StorageType.getStorageTypeById(sourceStorageType), item);
			return;
		}
		
		 long count = item.getItemCount();
         if (player.isLegionMember() && sourceStorageType == StorageType.LEGION_WAREHOUSE.getId()) {
             String itemName = item.getItemId() + ":" + count;
             LegionService.getInstance().addOrRemoveWarehouseItem(player, itemName, false);
            } 
         else if (player.isLegionMember() && destinationStorageType == StorageType.LEGION_WAREHOUSE.getId()) {
            String itemName = item.getItemId() + ":" + count;
            LegionService.getInstance().addOrRemoveWarehouseItem(player, itemName, true);
            }               

		IStorage targetStorage = player.getStorage(destinationStorageType);
		if (slot == -1) {
			if (item.getItemTemplate().isStackable()) {
				List<Item> sameItems = targetStorage.getItemsByItemId(item.getItemId());
				for (Item sameItem : sameItems) {
					long itemCount = item.getItemCount();
					if (itemCount == 0) {
						break;
					}
					// we can merge same stackable items
					ItemSplitService.mergeStacks(sourceStorage, targetStorage, item, sameItem, itemCount);
				}
			}
		}
		if (!targetStorage.isFull() && item.getItemCount() > 0) {
			sourceStorage.remove(item);
			sendItemDeletePacket(player, StorageType.getStorageTypeById(sourceStorageType), item, ItemDeleteType.MOVE);
			item.setEquipmentSlot(slot);
			targetStorage.add(item);
		}
	}

	/**
	 * @param storage
	 * @param item
	 * @param slot
	 */
	private static void moveInSameStorage(IStorage storage, Item item, short slot) {
		storage.setPersistentState(PersistentState.UPDATE_REQUIRED);
		item.setEquipmentSlot(slot);
		item.setPersistentState(PersistentState.UPDATE_REQUIRED);
	}

	public static void switchItemsInStorages(Player player, byte sourceStorageType, int sourceItemObjId,
		byte replaceStorageType, int replaceItemObjId) {
		IStorage sourceStorage = player.getStorage(sourceStorageType);
		IStorage replaceStorage = player.getStorage(replaceStorageType);

		Item sourceItem = sourceStorage.getItemByObjId(sourceItemObjId);
		if (sourceItem == null)
			return;

		Item replaceItem = replaceStorage.getItemByObjId(replaceItemObjId);
		if (replaceItem == null)
			return;

		// restrictions checks
		if (ItemRestrictionService.isItemRestrictedFrom(player, sourceItem, sourceStorageType)
			|| ItemRestrictionService.isItemRestrictedFrom(player, replaceItem, replaceStorageType)
			|| ItemRestrictionService.isItemRestrictedTo(player, sourceItem, replaceStorageType)
			|| ItemRestrictionService.isItemRestrictedTo(player, replaceItem, sourceStorageType))
			return;

		int sourceSlot = sourceItem.getEquipmentSlot();
		int replaceSlot = replaceItem.getEquipmentSlot();

		sourceItem.setEquipmentSlot(replaceSlot);
		replaceItem.setEquipmentSlot(sourceSlot);

		sourceStorage.remove(sourceItem);
		replaceStorage.remove(replaceItem);

		// correct UI update order is 1)delete items 2) add items
		sendItemDeletePacket(player, StorageType.getStorageTypeById(sourceStorageType), sourceItem, ItemDeleteType.MOVE);
		sendItemDeletePacket(player, StorageType.getStorageTypeById(replaceStorageType), replaceItem, ItemDeleteType.MOVE);
		sourceStorage.add(replaceItem);
		replaceStorage.add(sourceItem);
	}
}
