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
package gameserver.services.drop;

import commons.objects.filter.ObjectFilter;
import gameserver.configs.main.DropConfig;
import gameserver.model.DescriptionId;
import gameserver.model.EmotionType;
import gameserver.model.TaskId;
import gameserver.model.drop.DropItem;
import gameserver.model.gameobjects.DropNpc;
import gameserver.model.gameobjects.Item;
import gameserver.model.gameobjects.Npc;
import gameserver.model.gameobjects.VisibleObject;
import gameserver.model.gameobjects.player.InRoll;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.gameobjects.state.CreatureState;
import gameserver.model.team2.common.legacy.LootGroupRules;
import gameserver.model.team2.common.legacy.LootRuleType;
import gameserver.model.templates.item.ItemQuality;
import gameserver.model.templates.item.ItemTemplate;
import gameserver.network.aion.serverpackets.*;
import gameserver.services.RespawnService;
import gameserver.services.item.ItemInfoService;
import gameserver.services.item.ItemPacketService.ItemUpdateType;
import gameserver.services.item.ItemService;
import gameserver.services.item.ItemService.ItemAddPredicate;
import gameserver.taskmanager.tasks.TemporaryTradeTimeTask;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.ThreadPoolManager;
import gameserver.world.World;
import gameserver.world.knownlist.Visitor;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ATracer, xTz
 */
public class DropService {

	private static final Logger log = LoggerFactory.getLogger(DropService.class);
	private static final ItemAddPredicate DEFAULT_DROP_PREDICATE = new DropAddPredicate();

	public static DropService getInstance() {
		return SingletonHolder.instance;
	}

	/**
	 * @param npcUniqueId
	 */
	public void scheduleFreeForAll(final int npcUniqueId) {
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				DropNpc dropNpc = DropRegistrationService.getInstance().getDropRegistrationMap().get(npcUniqueId);
				if (dropNpc != null) {
					DropRegistrationService.getInstance().getDropRegistrationMap().get(npcUniqueId).startFreeForAll();
					VisibleObject npc = World.getInstance().findVisibleObject(npcUniqueId);
					if (npc != null && npc.isSpawned()) {
						PacketSendUtility.broadcastPacket(npc, new SM_LOOT_STATUS(npcUniqueId, 0));
					}
				}
			}
		}, 240000);
	}

	/**
	 * After NPC respawns - drop should be unregistered //TODO more correct - on despawn
	 * 
	 * @param npc
	 */
	public void unregisterDrop(Npc npc) {
		Integer npcObjId = npc.getObjectId();
		Map<Integer, DropNpc> dropRegmap = DropRegistrationService.getInstance().getDropRegistrationMap();
		DropRegistrationService.getInstance().geCurrentDropMap().remove(npcObjId);

		if (dropRegmap.containsKey(npcObjId)) {
			dropRegmap.remove(npcObjId);
		}
	}

	/**
	 * When player clicks on dead NPC to request drop list
	 * 
	 * @param player
	 * @param npcId
	 */
	public void requestDropList(Player player, int npcId) {
		DropNpc dropNpc = DropRegistrationService.getInstance().getDropRegistrationMap().get(npcId);
		if (player == null || dropNpc == null) {
			return;
		}

		if (!dropNpc.containsKey(player.getObjectId()) && !dropNpc.isFreeForAll()) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_LOOT_NO_RIGHT);
			return;
		}

		if (dropNpc.isBeingLooted()) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_LOOT_FAIL_ONLOOTING);
			return;
		}

		dropNpc.setBeingLooted(player);
		VisibleObject visObj = World.getInstance().findVisibleObject(npcId);
		if(visObj instanceof Npc) {
			Npc npc = ((Npc)visObj);
			ScheduledFuture<?> decayTask = (ScheduledFuture<?>)npc.getController().cancelTask(TaskId.DECAY);
			if(decayTask != null)	{
				long reamingDecayTime = decayTask.getDelay(TimeUnit.MILLISECONDS);
				dropNpc.setReamingDecayTime(reamingDecayTime);
			}
		}

		Set<DropItem> dropItems = DropRegistrationService.getInstance().geCurrentDropMap().get(npcId);

		if (dropItems == null) {
			dropItems = Collections.emptySet();
		}

		PacketSendUtility.sendPacket(player, new SM_LOOT_ITEMLIST(npcId, dropItems, player));
		PacketSendUtility.sendPacket(player, new SM_LOOT_STATUS(npcId, 2));
		player.unsetState(CreatureState.ACTIVE);
		player.setState(CreatureState.LOOTING);
		player.setLootingNpcOid(npcId);
		PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.START_LOOT, 0, npcId), true);
	}

	/**
	 * This method will change looted corpse to not in use
	 * 
	 * @param player
	 * @param npcId
	 */
	public void closeDropList(Player player, int npcId) {
		final DropNpc dropNpc = DropRegistrationService.getInstance().getDropRegistrationMap().get(npcId);
		if (dropNpc == null)
			return;

		player.unsetState(CreatureState.LOOTING);
		player.setState(CreatureState.ACTIVE);
		player.setLootingNpcOid(0);

		PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.END_LOOT, 0, npcId), true);

		if(dropNpc.getBeingLooted() != player)
			return;//cheater :)

		Set<DropItem> dropItems = DropRegistrationService.getInstance().geCurrentDropMap().get(npcId);
		dropNpc.setBeingLooted(null);

		Npc npc = (Npc) World.getInstance().findVisibleObject(npcId);
		if (npc != null) {
			if (dropItems == null || dropItems.isEmpty()) {
				npc.getController().onDelete();
				return;
			}

			Future<?> decayTask = RespawnService.scheduleDecayTask(npc, dropNpc.getReamingDecayTime());
			npc.getController().addTask(TaskId.DECAY, decayTask);

			LootGroupRules lootGrouRules = player.getLootGroupRules();
			if (lootGrouRules != null && dropNpc.getInRangePlayers().size() > 1 && dropNpc.getPlayersObjectId().size() == 1) {
				LootRuleType lrt = lootGrouRules.getLootRule();
				if (lrt != LootRuleType.FREEFORALL) {
					for (Player member : dropNpc.getInRangePlayers()) {
						if (member != null) {
							Integer object = member.getObjectId();
							dropNpc.setPlayerObjectId(object);
						}
					}
					DropRegistrationService.getInstance().setItemsToWinner(dropItems, 0);
				}
			}
			if (dropNpc.isFreeForAll()) {
				PacketSendUtility.broadcastPacket(npc, new SM_LOOT_STATUS(npcId, 0));
			}
			else {
				PacketSendUtility.broadcastPacket(player, new SM_LOOT_STATUS(npcId, 0), true, new ObjectFilter<Player>() {

					@Override
					public boolean acceptObject(Player object) {
						return dropNpc.containsKey(object.getObjectId());
					}
				});
			}
		}
	}

	public boolean canDistribute(Player player, DropItem requestedItem) {
		int npcId = requestedItem.getNpcObj();
		final DropNpc dropNpc = DropRegistrationService.getInstance().getDropRegistrationMap().get(npcId);
		if (dropNpc == null) {
			return false;
		}
		int itemId  = requestedItem.getDropTemplate().getItemId();
		ItemQuality quality = ItemInfoService.getQuality(itemId);
		LootGroupRules lootGrouRules = player.getLootGroupRules();
		if (lootGrouRules == null) {
			return true;
		}

		if (itemId != 182400001) {
			lootGrouRules = player.getLootGroupRules();
			if (dropNpc.getGroupSize() > 1) {
				dropNpc.setDistributionId(lootGrouRules.getAutodistribution().getId());
				dropNpc.setDistributionType(lootGrouRules.getQualityRule(quality));
			}
			else
				dropNpc.setDistributionId(0);
			if (dropNpc.getDistributionId() > 1 && dropNpc.getDistributionType()) {
				boolean containDropItem = lootGrouRules.containDropItem(requestedItem);
				if (lootGrouRules.getItemsToBeDistributed().isEmpty() || containDropItem) {
					dropNpc.setCurrentIndex(requestedItem.getIndex());
					for (Player member : dropNpc.getInRangePlayers()) {
						Player finalPlayer = World.getInstance().findPlayer(member.getObjectId());
						if (finalPlayer != null && finalPlayer.isOnline()) {
							dropNpc.addPlayerStatus(finalPlayer);
							finalPlayer.setInRoll(new InRoll(npcId, itemId, requestedItem.getIndex(), dropNpc.getDistributionId()));
							PacketSendUtility.sendPacket(finalPlayer, new SM_GROUP_LOOT(finalPlayer.getCurrentTeamId(), 0, itemId,
								npcId, dropNpc.getDistributionId(), 1, requestedItem.getIndex()));
						}
					}
					lootGrouRules.setPlayersInRoll(dropNpc.getInRangePlayers(), dropNpc.getDistributionId() == 2 ? 17000
						: 32000, requestedItem.getIndex(), npcId);
					if (!containDropItem) {
						lootGrouRules.addItemToBeDistributed(requestedItem);
					}
					return false;
				}
				else {
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE
						.STR_MSG_LOOT_ALREADY_DISTRIBUTING_ITEM(new DescriptionId(ItemInfoService.getNameId(itemId))));
					if (!containDropItem) {
						lootGrouRules.addItemToBeDistributed(requestedItem);
					}
					return false;
				}
			}
		}
		return true;
	}

	public void requestDropItem(Player player, int npcId, int itemIndex) {

		Set<DropItem> dropItems = DropRegistrationService.getInstance().geCurrentDropMap().get(npcId);
		DropNpc dropNpc = DropRegistrationService.getInstance().getDropRegistrationMap().get(npcId);
		DropItem requestedItem = null;
		// drop was unregistered
		if (dropItems == null || dropNpc == null) {
			return;
		}

		synchronized (dropItems) {
			for (DropItem dropItem : dropItems)
				if (dropItem.getIndex() == itemIndex) {
					requestedItem = dropItem;
					break;
				}
		}

		if (requestedItem == null) {
			log.warn("Null requested item {} {}", player.getObjectId(), itemIndex);
			return;
		}

		// fix exploit
		if (!requestedItem.isDistributeItem() && !dropNpc.containsKey(player.getObjectId()) && !dropNpc.isFreeForAll()) {
			return;
		}

		int itemId = requestedItem.getDropTemplate().getItemId();
		long currentDropItemCount = requestedItem.getCount();
		ItemQuality quality = ItemInfoService.getQuality(itemId);
		LootGroupRules lootGrouRules = player.getLootGroupRules();
		if (lootGrouRules != null && !requestedItem.isDistributeItem() && !requestedItem.isFreeForAll()) {
			if (lootGrouRules.containDropItem(requestedItem)) {
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1390219));
				return;
			}
			requestedItem.setNpcObj(npcId);
			if (!canDistribute(player, requestedItem)) {
				return;
			}
		}

		if (itemId == 182400001) {
			// to do distribution
			currentDropItemCount = ItemService.addItem(player, itemId, currentDropItemCount, DEFAULT_DROP_PREDICATE);
		}
		else if (!player.isInGroup2() && !player.isInAlliance2() && !requestedItem.isItemWonNotCollected()
			&& dropNpc.getDistributionId() == 0) {
			currentDropItemCount = ItemService.addItem(player, itemId, currentDropItemCount, DEFAULT_DROP_PREDICATE);
			uniqueDropAnnounce(player, requestedItem);
		}
		else if (!requestedItem.isDistributeItem()) {
			if (player.isInGroup2() || player.isInAlliance2()) {
				lootGrouRules = player.getLootGroupRules();
				if (lootGrouRules.isMisc(quality)) {
					Collection<Player> members = dropNpc.getInRangePlayers();

					if (members.size() > lootGrouRules.getNrMisc()) {
						lootGrouRules.setNrMisc(lootGrouRules.getNrMisc() + 1);
					}
					else {
						lootGrouRules.setNrMisc(1);
					}

					int i = 0;
					for (Player p : members) {
						i++;
						if (i == lootGrouRules.getNrMisc()) {
							requestedItem.setWinningPlayer(p);
							break;
						}
					}
				}
				else {
					requestedItem.setWinningPlayer(player);
				}
			}
			else if (requestedItem.getWinningPlayer() == null) {
				requestedItem.setWinningPlayer(player);
			}

			if (requestedItem.getWinningPlayer() != null) {
				currentDropItemCount = ItemService.addItem(requestedItem.getWinningPlayer(), itemId, currentDropItemCount,
						new TempTradeDropPredicate(dropNpc));

				winningNormalActions(player, npcId, requestedItem);
				uniqueDropAnnounce(player, requestedItem);
			}
		}

		// handles distribution of item to correct player and messages accordingly
		if (requestedItem.isDistributeItem()) {
			if (player != requestedItem.getWinningPlayer() && requestedItem.isItemWonNotCollected()) {
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_LOOT_ANOTHER_OWNER_ITEM);
				return;
			}
			else if (requestedItem.getWinningPlayer().getInventory().isFull()) {
				PacketSendUtility.sendPacket(requestedItem.getWinningPlayer(), SM_SYSTEM_MESSAGE.STR_MSG_DICE_INVEN_ERROR);
				requestedItem.isItemWonNotCollected(true);
				return;
			}

			currentDropItemCount = ItemService.addItem(requestedItem.getWinningPlayer(), itemId, currentDropItemCount,
				new TempTradeDropPredicate(dropNpc));

			switch (dropNpc.getDistributionId()) {
				case 2:
					winningRollActions(requestedItem.getWinningPlayer(), itemId, npcId);
					break;
				case 3:
					winningBidActions(requestedItem.getWinningPlayer(), npcId, requestedItem.getHighestValue());
			}

			uniqueDropAnnounce(player, requestedItem);
		}

		if (currentDropItemCount <= 0) {
			synchronized (dropItems) {
				dropItems.remove(requestedItem);
			}
		}
		else
			requestedItem.setCount(currentDropItemCount);

			resendDropList(dropNpc.getBeingLooted(), npcId, dropItems);
	}

	private void resendDropList(Player player, int npcId, Set<DropItem> dropItems) {
		Npc npc = (Npc) World.getInstance().findVisibleObject(npcId);
		if (dropItems.size() != 0) {
			if (player != null) {
				PacketSendUtility.sendPacket(player, new SM_LOOT_ITEMLIST(npcId, dropItems, player));
			}
		}
		else {
			if (player != null) {
				PacketSendUtility.sendPacket(player, new SM_LOOT_STATUS(npcId, 3));
				player.unsetState(CreatureState.LOOTING);
				player.setState(CreatureState.ACTIVE);
				PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.END_LOOT, 0, npcId), true);
			}
			if (npc != null) {
				npc.getController().onDelete();
			}
		}
	}

	/**
	 * @param Displays
	 *          messages when item gained via ROLLED
	 */
	private void winningRollActions(Player player, int itemId, int npcId) {
		PacketSendUtility.sendPacket(player,
			SM_SYSTEM_MESSAGE.STR_MSG_LOOT_GET_ITEM_ME(new DescriptionId(ItemInfoService.getNameId(itemId))));

		if (player.isInGroup2() || player.isInAlliance2()) {
			for (Player member : DropRegistrationService.getInstance().getDropRegistrationMap().get(npcId).getInRangePlayers()) {
				if (member != null && !player.equals(member) && member.isOnline()) {
					PacketSendUtility.sendPacket(
						member,
						SM_SYSTEM_MESSAGE.STR_MSG_LOOT_GET_ITEM_OTHER(player.getName(),
							new DescriptionId(ItemInfoService.getNameId(itemId))));
				}
			}
		}
	}

	/**
	 * @param Displays
	 *          messages/removes and shares kinah when item gained via BID
	 */
	private void winningBidActions(Player player, int npcId, long highestValue) {
		DropNpc dropNpc = DropRegistrationService.getInstance().getDropRegistrationMap().get(npcId);

		if (highestValue > 0) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_PAY_ACCOUNT_ME(highestValue));
			player.getInventory().decreaseKinah(highestValue);
		}

		if (player.isInGroup2() || player.isInAlliance2())
			for (Player member : dropNpc.getInRangePlayers())
				if (member != null && !player.equals(member) && member.isOnline()) {
					PacketSendUtility.sendPacket(member, SM_SYSTEM_MESSAGE.STR_MSG_PAY_ACCOUNT_OTHER(player.getName(), highestValue));
					long distributeKinah = highestValue / (dropNpc.getGroupSize() - 1);
					member.getInventory().increaseKinah(distributeKinah);
					PacketSendUtility.sendPacket(member, SM_SYSTEM_MESSAGE.STR_MSG_PAY_DISTRIBUTE(highestValue, dropNpc.getGroupSize() - 1, distributeKinah));
				}
	}

	private void winningNormalActions(Player player, int npcId, DropItem requestedItem) {
		DropNpc dropNpc = DropRegistrationService.getInstance().getDropRegistrationMap().get(npcId);
		if (player == null || dropNpc == null)
			return;

		int itemId = requestedItem.getDropTemplate().getItemId();
		if (player.isInGroup2() || player.isInAlliance2()) {
			for (Player member : dropNpc.getInRangePlayers()) {
				if (member != null && !requestedItem.getWinningPlayer().equals(member) && member.isOnline())
					PacketSendUtility.sendPacket(member, SM_SYSTEM_MESSAGE.STR_MSG_GET_ITEM_PARTYNOTICE(requestedItem.getWinningPlayer().getName(), new DescriptionId(ItemInfoService.getNameId(itemId))));
			}
		}
	}

	public void see(final Player player, Npc owner) {
		final int id = owner.getObjectId();
		final DropNpc dropNpc = DropRegistrationService.getInstance().getDropRegistrationMap().get(id);

		if (dropNpc == null)
			return;

		if (dropNpc.containsKey(player.getObjectId()) || dropNpc.isFreeForAll()) {
			ThreadPoolManager.getInstance().schedule(new Runnable() {

				@Override
				public void run() {
					PacketSendUtility.sendPacket(player, new SM_LOOT_STATUS(id, 0));
				}
			}, 5000);
		}
	}

	private void uniqueDropAnnounce(final Player player, final DropItem requestedItem) {
		if (DropConfig.ENABLE_UNIQUE_DROP_ANNOUNCE && !player.getInventory().isFull()) {
			final ItemTemplate itemTemplate = ItemInfoService.getItemTemplate(requestedItem.getDropTemplate().getItemId());

			if (itemTemplate.getItemQuality() == ItemQuality.UNIQUE || itemTemplate.getItemQuality() == ItemQuality.EPIC) {
				final String lastGetName = requestedItem.getWinningPlayer() != null ? requestedItem.getWinningPlayer().getName() : player.getName();
				final int pObjectId = player.getObjectId();
				final int pRaceId = player.getRace().getRaceId();
				final int pMapId = player.getWorldId();
				final int pInstance = player.isInInstance() ? player.getInstanceId() : 0;

				World.getInstance().doOnAllPlayers(new Visitor<Player>() {

					@Override
					public void visit(Player other) {

						int oObjectId = other.getObjectId();
						int oRaceId = other.getRace().getRaceId();
						int oMapId = other.getWorldId();
						int oInstance = other.isInInstance() ? other.getInstanceId() : 0;

						if (oObjectId != pObjectId && other.isSpawned() && oRaceId == pRaceId && oMapId == pMapId && oInstance == pInstance)	{
							PacketSendUtility.sendPacket(other, new SM_SYSTEM_MESSAGE(1390003, lastGetName, "[item: " + requestedItem.getDropTemplate().getItemId() + "]"));
						}
					}
				});
			}
		}
	}

	private static class DropAddPredicate extends ItemAddPredicate {

		@Override
		public ItemUpdateType getUpdateType(Item input) {
			return input.getItemTemplate().isKinah() ? ItemUpdateType.INC_KINAH_LOOT : ItemUpdateType.INC_LOOT;
		}
	}

	private static final class TempTradeDropPredicate extends DropAddPredicate {

		private final DropNpc dropNpc;

		private TempTradeDropPredicate(DropNpc dropNpc) {
			this.dropNpc = dropNpc;
		}

		@Override
		public boolean apply(Item input) {
			if (dropNpc.getPlayersObjectId().size() > 1) {
				ItemTemplate template = input.getItemTemplate();
				if (template.getTempExchangeTime() != 0) {
					input.setTemporaryExchangeTime((int) (System.currentTimeMillis() / 1000)
						+ (template.getTempExchangeTime() * 60));
					TemporaryTradeTimeTask.getInstance().addTask(input, dropNpc.getPlayersObjectId());
				}
				return true;
			}
			return false;
		}

	}

	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder {

		protected static final DropService instance = new DropService();
	}
}