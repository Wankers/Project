/*
 * This file is part of aion-unique <www.aion-unique.com>.
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
package gameserver.model.templates.item.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import commons.utils.Rnd;
import gameserver.dataholders.DataManager;
import gameserver.model.Race;
import gameserver.model.TaskId;
import gameserver.model.gameobjects.Item;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.templates.item.ExtractedItemsCollection;
import gameserver.model.templates.item.RandomItem;
import gameserver.model.templates.item.ResultedItem;
import gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import gameserver.services.item.ItemService;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.ThreadPoolManager;

/**
 * @author oslo(a00441234)
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DecomposeAction")
public class DecomposeAction extends AbstractItemAction {

	private static final Logger log = LoggerFactory.getLogger(DecomposeAction.class);

	private static final int USAGE_DELAY = 3000;
	private static Map<Integer, int[]> manastones = new HashMap<Integer, int[]>();
	static {

		manastones.put(10, new int[] { 167000226, 167000227, 167000228, 167000229, 167000230, 167000231, 167000232,
			167000233, 167000235 });
		manastones.put(20, new int[] { 167000258, 167000259, 167000260, 167000261, 167000263, 167000264, 167000265,
			167000267, 167000418, 167000419, 167000420, 167000421, 167000423, 167000424, 167000425, 167000427 });
		manastones.put(30, new int[] { 167000290, 167000291, 167000292, 167000293, 167000294, 167000295, 167000296,
			167000297, 167000299, 167000450, 167000451, 167000452, 167000453, 167000454, 167000455, 167000456, 167000457,
			167000459 });
		manastones.put(40, new int[] { 167000322, 167000323, 167000324, 167000325, 167000327, 167000328, 167000329,
			167000331, 167000482, 167000483, 167000484, 167000485, 167000487, 167000488, 167000489, 167000491, 167000539,
			167000540 });
		manastones.put(50, new int[] { 167000354, 167000355, 167000356, 167000357, 167000358, 167000359, 167000360,
			167000361, 167000363, 167000514, 167000515, 167000516, 167000517, 167000518, 167000519, 167000520, 167000521,
			167000522, 167000541, 167000542 });
		manastones.put(60, new int[] { 167000543, 167000544, 167000545, 167000546, 167000547, 167000548, 167000549,
			167000550, 167000551, 167000552, 167000553, 167000554, 167000555, 167000556, 167000557, 167000558, 167000560,
			167000561 });
	}

	@Override
	public boolean canAct(Player player, Item parentItem, Item targetItem) {
		List<ExtractedItemsCollection> itemsCollections = DataManager.DECOMPOSABLE_ITEMS_DATA.getInfoByItemId(parentItem.getItemId());
		if (itemsCollections == null || itemsCollections.isEmpty()) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_DECOMPOSE_ITEM_INVALID_STANCE(parentItem.getNameID()));
			return false;
		}
		if (player.getInventory().getFreeSlots() < calcMaxCountOfSlots(itemsCollections)) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_DECOMPOSE_ITEM_INVENTORY_IS_FULL);
			return false;
		}
		return true;
	}

	@Override
	public void act(final Player player, final Item parentItem, final Item targetItem) {
		player.getController().cancelUseItem();
		List<ExtractedItemsCollection> itemsCollections = DataManager.DECOMPOSABLE_ITEMS_DATA.getInfoByItemId(parentItem
			.getItemId());

		Collection<ExtractedItemsCollection> levelSuitableItems = filterItemsByLevel(player, itemsCollections);
		final ExtractedItemsCollection selectedCollection = selectItemByChance(levelSuitableItems);

		PacketSendUtility.broadcastPacketAndReceive(player,
			new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), parentItem.getObjectId(), parentItem.getItemId(), USAGE_DELAY,
				0, 0));
		player.getController().addTask(TaskId.ITEM_USE, ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				boolean validAction = postValidate(player, parentItem);
				if (validAction) {
					if (selectedCollection.getItems().size() > 0) {
						for (ResultedItem resultItem : selectedCollection.getItems()) {
							if (canAcquire(player, resultItem)) {
								ItemService.addItem(player, resultItem.getItemId(), resultItem.getResultCount());
							}
						}
						PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_DECOMPOSE_ITEM_SUCCEED(parentItem.getNameID()));
					}
					else if (selectedCollection.getRandomItems().size() > 0) {
						for (RandomItem randomItem : selectedCollection.getRandomItems()) {
							if (randomItem.getType() != null) {
								int randomId = 0;
								int i = 0;
								switch (randomItem.getType()) {
									case ENCHANTMENT: {
										do {
											int itemLvl = parentItem.getItemTemplate().getLevel();
											randomId = 166000000 + itemLvl + Rnd.get(15);
											i++;
											if (i > 50) {
												randomId = 0;
												log.warn("DecomposeAction random item id not found. " + parentItem.getItemId());
												break;
											}
										}
										while (!ItemService.checkRandomTemplate(randomId));
										break;
									}
									case MANASTONE: {
										int itemLvl = parentItem.getItemTemplate().getLevel();
										//TODO lvl 50 stack lvl 60 manastone?
										int[] stones = manastones.get(itemLvl);
										if (stones == null){
											log.warn("DecomposeAction random item id not found. "+parentItem.getItemTemplate().getTemplateId());
											return;
										}
										randomId = stones[Rnd.get(stones.length)];
											//randomId = Rnd.get(167000226, 167000561);
										if(!ItemService.checkRandomTemplate(randomId)){
												log.warn("DecomposeAction random item id not found. " + randomId);
												return;
										}
										break;
									}
									case ANCIENTITEMS: {
										do {
											randomId = Rnd.get(186000051, 186000069);
											i++;
											if (i > 50) {
												randomId = 0;
												log.warn("DecomposeAction random item id not found. " + parentItem.getItemId());
												break;
											}
										}
										while (!ItemService.checkRandomTemplate(randomId));
										break;
									}
								}
								if (randomId != 0 && randomId != 167000524)
									ItemService.addItem(player, randomId, randomItem.getCount());
							}
						}
					}
				}
				PacketSendUtility.broadcastPacketAndReceive(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(),
					parentItem.getObjectId(), parentItem.getItemId(), 0, validAction ? 1 : 2, 0));
			}

			private boolean canAcquire(Player player, ResultedItem resultItem) {
				Race race = resultItem.getRace();
				if (race != Race.PC_ALL && !race.equals(player.getRace())) {
					return false;
				}
				return true;
			}

			boolean postValidate(Player player, Item parentItem) {
				if (!canAct(player, parentItem, targetItem)) {
					return false;
				}
				if (player.getLifeStats().isAlreadyDead() || !player.isSpawned()) {
					return false;
				}
				if (!player.getInventory().decreaseByObjectId(parentItem.getObjectId(), 1)) {
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_DECOMPOSE_ITEM_NO_TARGET_ITEM);
					return false;
				}
				if (selectedCollection.getItems().isEmpty() && selectedCollection.getRandomItems().isEmpty()) {
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_DECOMPOSE_ITEM_FAILED(parentItem.getNameID()));
					return false;
				}
				return true;
			}
		}, USAGE_DELAY));
	}

	/**
	 * Add to result collection only items wich suits player's level
	 */
	private Collection<ExtractedItemsCollection> filterItemsByLevel(Player player,
		List<ExtractedItemsCollection> itemsCollections) {
		int playerLevel = player.getLevel();
		Collection<ExtractedItemsCollection> result = new ArrayList<ExtractedItemsCollection>();
		for (ExtractedItemsCollection collection : itemsCollections) {
			if (collection.getMinLevel() > playerLevel) {
				continue;
			}
			if (collection.getMaxLevel() > 0 && collection.getMaxLevel() < playerLevel) {
				continue;
			}
			result.add(collection);
		}
		return result;
	}

	/**
	 * Select only 1 item based on chance attributes
	 */
	private ExtractedItemsCollection selectItemByChance(Collection<ExtractedItemsCollection> itemsCollections) {
		int sumOfChances = calcSumOfChances(itemsCollections);
		int currentSum = 0;
		int rnd = Rnd.get(0, sumOfChances - 1);
		ExtractedItemsCollection selectedCollection = null;
		for (ExtractedItemsCollection collection : itemsCollections) {
			currentSum += collection.getChance();
			if (rnd < currentSum) {
				selectedCollection = collection;
				break;
			}
		}
		return selectedCollection;
	}

	private int calcMaxCountOfSlots(Collection<ExtractedItemsCollection> itemsCollections) {
		int maxCount = 0;
		for (ExtractedItemsCollection collection : itemsCollections)
			if (collection.getItems().size() > maxCount)
				maxCount = collection.getItems().size();
		return maxCount;
	}

	private int calcSumOfChances(Collection<ExtractedItemsCollection> itemsCollections) {
		int sum = 0;
		for (ExtractedItemsCollection collection : itemsCollections)
			sum += collection.getChance();
		return sum;
	}
}
