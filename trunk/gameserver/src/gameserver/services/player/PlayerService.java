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
package gameserver.services.player;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import commons.database.dao.DAOManager;
import commons.utils.GenericValidator;
import gameserver.configs.main.CacheConfig;
import gameserver.configs.main.GSConfig;
import gameserver.controllers.FlyController;
import gameserver.controllers.PlayerController;
import gameserver.controllers.effect.PlayerEffectController;
import gameserver.dao.AbyssRankDAO;
import gameserver.dao.BlockListDAO;
import gameserver.dao.CraftCooldownsDAO;
import gameserver.dao.FriendListDAO;
import gameserver.dao.InventoryDAO;
import gameserver.dao.ItemCooldownsDAO;
import gameserver.dao.ItemStoneListDAO;
import gameserver.dao.MailDAO;
import gameserver.dao.MotionDAO;
import gameserver.dao.OldNamesDAO;
import gameserver.dao.PlayerAppearanceDAO;
import gameserver.dao.PlayerBindPointDAO;
import gameserver.dao.PlayerCooldownsDAO;
import gameserver.dao.PlayerDAO;
import gameserver.dao.PlayerEffectsDAO;
import gameserver.dao.PlayerEmotionListDAO;
import gameserver.dao.PlayerLifeStatsDAO;
import gameserver.dao.PlayerMacrossesDAO;
import gameserver.dao.PlayerNpcFactionsDAO;
import gameserver.dao.PlayerPunishmentsDAO;
import gameserver.dao.PlayerQuestListDAO;
import gameserver.dao.PlayerRecipesDAO;
import gameserver.dao.PlayerSettingsDAO;
import gameserver.dao.PlayerSkillListDAO;
import gameserver.dao.PlayerTitleListDAO;
import gameserver.dao.PlayerVarsDAO;
import gameserver.dao.PortalCooldownsDAO;
import gameserver.dataholders.DataManager;
import gameserver.dataholders.PlayerInitialData;
import gameserver.dataholders.PlayerInitialData.LocationData;
import gameserver.dataholders.PlayerInitialData.PlayerCreationData;
import gameserver.dataholders.PlayerInitialData.PlayerCreationData.ItemType;
import gameserver.model.account.Account;
import gameserver.model.account.PlayerAccountData;
import gameserver.model.gameobjects.Item;
import gameserver.model.gameobjects.PersistentState;
import gameserver.model.gameobjects.player.Equipment;
import gameserver.model.gameobjects.player.MacroList;
import gameserver.model.gameobjects.player.Mailbox;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.gameobjects.player.PlayerAppearance;
import gameserver.model.gameobjects.player.PlayerCommonData;
import gameserver.model.items.ItemSlot;
import gameserver.model.items.storage.PlayerStorage;
import gameserver.model.items.storage.Storage;
import gameserver.model.items.storage.StorageType;
import gameserver.model.skill.PlayerSkillList;
import gameserver.model.stats.calc.functions.PlayerStatFunctions;
import gameserver.model.stats.listeners.TitleChangeListener;
import gameserver.model.team.legion.LegionMember;
import gameserver.model.templates.item.ItemTemplate;
import gameserver.services.LegionService;
import gameserver.services.PunishmentService.PunishmentType;
import gameserver.services.SkillLearnService;
import gameserver.services.instance.InstanceService;
import gameserver.services.item.ItemFactory;
import gameserver.services.item.ItemService;
import gameserver.utils.collections.cachemap.CacheMap;
import gameserver.utils.collections.cachemap.CacheMapFactory;
import gameserver.world.World;
import gameserver.world.WorldPosition;
import gameserver.world.knownlist.KnownList;
import gameserver.world.knownlist.Visitor;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * This class is designed to do all the work related with loading/storing players.<br>
 * Same with storing, {@link #storePlayer(gameserver.model.gameobjects.player.Player)} stores all player
 * data like appearance, items, etc...
 * 
 * @author SoulKeeper, Saelya, Cura
 */
public class PlayerService {

	private static final CacheMap<Integer, Player> playerCache = CacheMapFactory.createSoftCacheMap("Player", "player");

	/**
	 * Checks if name is already taken or not
	 * 
	 * @param name
	 *          character name
	 * @return true if is free, false in other case
	 */
	public static boolean isFreeName(String name) {
		return !DAOManager.getDAO(PlayerDAO.class).isNameUsed(name);
	}

	public static boolean isOldName(String name) {
		return DAOManager.getDAO(OldNamesDAO.class).isOldName(name);
	}

	/**
	 * Checks if a name is valid. It should contain only english letters
	 * 
	 * @param name
	 *          character name
	 * @return true if name is valid, false overwise
	 */
	public static boolean isValidName(String name) {
		return GSConfig.CHAR_NAME_PATTERN.matcher(name).matches();
	}

	/**
	 * Stores newly created player
	 * 
	 * @param player
	 *          player to store
	 * @return true if character was successful saved.
	 */
	public static boolean storeNewPlayer(Player player, String accountName, int accountId) {
		return DAOManager.getDAO(PlayerDAO.class).saveNewPlayer(player.getCommonData(), accountId, accountName)
			&& DAOManager.getDAO(PlayerAppearanceDAO.class).store(player)
			&& DAOManager.getDAO(PlayerSkillListDAO.class).storeSkills(player)
			&& DAOManager.getDAO(InventoryDAO.class).store(player);
	}

	/**
	 * Stores player data into db
	 * 
	 * @param player
	 */
	public static void storePlayer(Player player) {
		DAOManager.getDAO(PlayerDAO.class).storePlayer(player);
		DAOManager.getDAO(PlayerSkillListDAO.class).storeSkills(player);
		DAOManager.getDAO(PlayerSettingsDAO.class).saveSettings(player);
		DAOManager.getDAO(PlayerQuestListDAO.class).store(player);
		DAOManager.getDAO(AbyssRankDAO.class).storeAbyssRank(player);
		DAOManager.getDAO(PlayerPunishmentsDAO.class).storePlayerPunishments(player, PunishmentType.PRISON);
		DAOManager.getDAO(PlayerPunishmentsDAO.class).storePlayerPunishments(player, PunishmentType.GATHER);
		DAOManager.getDAO(InventoryDAO.class).store(player);
		DAOManager.getDAO(ItemStoneListDAO.class).save(player);
		DAOManager.getDAO(MailDAO.class).storeMailbox(player);
		DAOManager.getDAO(PortalCooldownsDAO.class).storePortalCooldowns(player);
		DAOManager.getDAO(CraftCooldownsDAO.class).storeCraftCooldowns(player);
		DAOManager.getDAO(PlayerNpcFactionsDAO.class).storeNpcFactions(player);
	}

	/**
	 * Returns the player with given objId (if such player exists)
	 * 
	 * @param playerObjId
	 * @param account
	 * @return Player
	 */
	public static Player getPlayer(int playerObjId, Account account) {
		Player player = playerCache.get(playerObjId);
		if (player != null)
			return player;

		/**
		 * Player common data and appearance should be already loaded in account
		 */

		PlayerAccountData playerAccountData = account.getPlayerAccountData(playerObjId);
		PlayerCommonData pcd = playerAccountData.getPlayerCommonData();
		PlayerAppearance appearance = playerAccountData.getAppereance();

		player = new Player(new PlayerController(), pcd, appearance, account);

		LegionMember legionMember = LegionService.getInstance().getLegionMember(player.getObjectId());
		if (legionMember != null)
			player.setLegionMember(legionMember);

		MacroList macroses = DAOManager.getDAO(PlayerMacrossesDAO.class).restoreMacrosses(playerObjId);
		player.setMacroList(macroses);

		player.setSkillList(DAOManager.getDAO(PlayerSkillListDAO.class).loadSkillList(playerObjId));
		player.setKnownlist(new KnownList(player));
		player.setFriendList(DAOManager.getDAO(FriendListDAO.class).load(player));
		player.setBlockList(DAOManager.getDAO(BlockListDAO.class).load(player));
		player.setTitleList(DAOManager.getDAO(PlayerTitleListDAO.class).loadTitleList(playerObjId));
		DAOManager.getDAO(PlayerSettingsDAO.class).loadSettings(player);
		DAOManager.getDAO(AbyssRankDAO.class).loadAbyssRank(player);
		DAOManager.getDAO(PlayerNpcFactionsDAO.class).loadNpcFactions(player);
		DAOManager.getDAO(MotionDAO.class).loadMotionList(player);
		player.setVars(DAOManager.getDAO(PlayerVarsDAO.class).load(player.getObjectId()));
		Equipment equipment = DAOManager.getDAO(InventoryDAO.class).loadEquipment(player);
		ItemService.loadItemStones(equipment.getEquippedItemsWithoutStigma());
		equipment.setOwner(player);
		player.setEquipment(equipment);
		player.setEffectController(new PlayerEffectController(player));
		player.setFlyController(new FlyController(player));
		PlayerStatFunctions.addPredefinedStatFunctions(player);

		player.setQuestStateList(DAOManager.getDAO(PlayerQuestListDAO.class).load(player));
		player.setRecipeList(DAOManager.getDAO(PlayerRecipesDAO.class).load(player.getObjectId()));

		/**
		 * Account warehouse should be already loaded in account
		 */
		Storage accWarehouse = account.getAccountWarehouse();
		player.setStorage(accWarehouse, StorageType.ACCOUNT_WAREHOUSE);

		Storage inventory = DAOManager.getDAO(InventoryDAO.class).loadStorage(playerObjId, StorageType.CUBE);
		ItemService.loadItemStones(inventory.getItems());

		player.setStorage(inventory, StorageType.CUBE);

		for (int petBagId = 32; petBagId < 36; petBagId++) {
			Storage petBag = DAOManager.getDAO(InventoryDAO.class).loadStorage(playerObjId,
				StorageType.getStorageTypeById(petBagId));
			ItemService.loadItemStones(petBag.getItems());

			player.setStorage(petBag, StorageType.getStorageTypeById(petBagId));
		}

		Storage warehouse = DAOManager.getDAO(InventoryDAO.class).loadStorage(playerObjId, StorageType.REGULAR_WAREHOUSE);
		ItemService.loadItemStones(warehouse.getItems());

		player.setStorage(warehouse, StorageType.REGULAR_WAREHOUSE);

		/**
		 * Apply equipment stats (items and manastones were loaded in account)
		 */
		player.getEquipment().onLoadApplyEquipmentStats();

		DAOManager.getDAO(PlayerPunishmentsDAO.class).loadPlayerPunishments(player, PunishmentType.PRISON);
		DAOManager.getDAO(PlayerPunishmentsDAO.class).loadPlayerPunishments(player, PunishmentType.GATHER);

		// update passive stats after effect controller, stats and equipment are initialized
		player.getController().updatePassiveStats();
		// load saved effects
		DAOManager.getDAO(PlayerEffectsDAO.class).loadPlayerEffects(player);
		// load saved player cooldowns
		DAOManager.getDAO(PlayerCooldownsDAO.class).loadPlayerCooldowns(player);
		// load item cooldowns
		DAOManager.getDAO(ItemCooldownsDAO.class).loadItemCooldowns(player);
		// load portal cooldowns
		DAOManager.getDAO(PortalCooldownsDAO.class).loadPortalCooldowns(player);
		// load bind point
		DAOManager.getDAO(PlayerBindPointDAO.class).loadBindPoint(player);
		// load craft cooldowns
		DAOManager.getDAO(CraftCooldownsDAO.class).loadCraftCooldowns(player);

		if (player.getCommonData().getTitleId() > 0) {
			TitleChangeListener.onTitleChange(player.getGameStats(), player.getCommonData().getTitleId(), true);
		}

		DAOManager.getDAO(PlayerLifeStatsDAO.class).loadPlayerLifeStat(player);
		DAOManager.getDAO(PlayerEmotionListDAO.class).loadEmotions(player);
		
		// analyze current instance
		InstanceService.onPlayerLogin(player);
		
		if (CacheConfig.CACHE_PLAYERS)
			playerCache.put(playerObjId, player);

		return player;
	}

	/**
	 * This method is used for creating new players
	 * 
	 * @param playerCommonData
	 * @param playerAppearance
	 * @param account
	 * @return Player
	 */
	public static Player newPlayer(PlayerCommonData playerCommonData, PlayerAppearance playerAppearance, Account account) {
		PlayerInitialData playerInitialData = DataManager.PLAYER_INITIAL_DATA;
		LocationData ld = playerInitialData.getSpawnLocation(playerCommonData.getRace());

		WorldPosition position = World.getInstance().createPosition(ld.getMapId(), ld.getX(), ld.getY(), ld.getZ(),
			ld.getHeading(), 0);
		playerCommonData.setPosition(position);

		Player newPlayer = new Player(new PlayerController(), playerCommonData, playerAppearance, account);

		// Starting skills
		newPlayer.setSkillList(new PlayerSkillList());
		SkillLearnService.addNewSkills(newPlayer);

		// Starting items
		PlayerCreationData playerCreationData = playerInitialData.getPlayerCreationData(playerCommonData.getPlayerClass());
		Storage playerInventory = new PlayerStorage(StorageType.CUBE);
		Storage regularWarehouse = new PlayerStorage(StorageType.REGULAR_WAREHOUSE);
		Storage accountWarehouse = new PlayerStorage(StorageType.ACCOUNT_WAREHOUSE);
		Equipment equipment = new Equipment(newPlayer);
		if(playerCreationData != null) { // player transfer
			List<ItemType> items = playerCreationData.getItems();
			for (ItemType itemType : items) {
				int itemId = itemType.getTemplate().getTemplateId();
				Item item = ItemFactory.newItem(itemId, itemType.getCount());
				if (item == null)
					continue;

				// When creating new player - all equipment that has slot values will be equipped
				// Make sure you will not put into xml file more items than possible to equip.
				ItemTemplate itemTemplate = item.getItemTemplate();

				if ((itemTemplate.isArmor() || itemTemplate.isWeapon())
					&& !(equipment.isSlotEquipped(itemTemplate.getItemSlot()))) {
					item.setEquipped(true);
					ItemSlot[] itemSlots = ItemSlot.getSlotsFor(itemTemplate.getItemSlot());
					item.setEquipmentSlot(itemSlots[0].getSlotIdMask());
					equipment.onLoadHandler(item);
				}
				else
					playerInventory.onLoadHandler(item);
			}
		}
		newPlayer.setStorage(playerInventory, StorageType.CUBE);
		newPlayer.setStorage(regularWarehouse, StorageType.REGULAR_WAREHOUSE);
		newPlayer.setStorage(accountWarehouse, StorageType.ACCOUNT_WAREHOUSE);
		newPlayer.setEquipment(equipment);
		newPlayer.setMailbox(new Mailbox(newPlayer));

		for (int petBagId = 32; petBagId < 36; petBagId++) {
			Storage petBag = new PlayerStorage(StorageType.getStorageTypeById(petBagId));
			newPlayer.setStorage(petBag, StorageType.getStorageTypeById(petBagId));
		}
		
		/**
		 * Mark inventory and equipment as UPDATE_REQUIRED to be saved during character creation
		 */
		playerInventory.setPersistentState(PersistentState.UPDATE_REQUIRED);
		equipment.setPersistentState(PersistentState.UPDATE_REQUIRED);
		return newPlayer;
	}

	/**
	 * Cancel Player deletion process if its possible.
	 * 
	 * @param accData
	 *          PlayerAccountData
	 * @return True if deletion was successful canceled.
	 */
	public static boolean cancelPlayerDeletion(PlayerAccountData accData) {
		if (accData.getDeletionDate() == null)
			return true;

		if (accData.getDeletionDate().getTime() > System.currentTimeMillis()) {
			accData.setDeletionDate(null);
			storeDeletionTime(accData);
			return true;
		}
		return false;
	}

	/**
	 * Starts player deletion process if its possible. If deletion is possible character should be deleted after 5
	 * minutes.
	 * 
	 * @param accData
	 *          PlayerAccountData
	 */
	public static void deletePlayer(PlayerAccountData accData) {
		if (accData.getDeletionDate() != null)
			return;

		accData.setDeletionDate(new Timestamp(System.currentTimeMillis() + 5 * 60 * 1000));
		storeDeletionTime(accData);
	}

	/**
	 * Completely removes player from database
	 * 
	 * @param playerId
	 *          id of player to delete from db
	 */
	public static void deletePlayerFromDB(int playerId) {
		DAOManager.getDAO(PlayerDAO.class).deletePlayer(playerId);
		DAOManager.getDAO(InventoryDAO.class).deletePlayerItems(playerId);
		DAOManager.getDAO(PlayerLifeStatsDAO.class).deletePlayerLifeStat(playerId);
		DAOManager.getDAO(OldNamesDAO.class).deleteNames(playerId);
	}

	/**
	 * Updates deletion time in database
	 * 
	 * @param accData
	 *          PlayerAccountData
	 */
	private static void storeDeletionTime(PlayerAccountData accData) {
		DAOManager.getDAO(PlayerDAO.class).updateDeletionTime(accData.getPlayerCommonData().getPlayerObjId(),
			accData.getDeletionDate());
	}

	/**
	 * @param objectId
	 * @param creationDate
	 */
	public static void storeCreationTime(int objectId, Timestamp creationDate) {
		DAOManager.getDAO(PlayerDAO.class).storeCreationTime(objectId, creationDate);
	}

	/**
	 * Add macro for player
	 * 
	 * @param player
	 *          Player
	 * @param macroOrder
	 *          Macro order
	 * @param macroXML
	 *          Macro XML
	 */
	public static void addMacro(Player player, int macroOrder, String macroXML) {
		if (player.getMacroList().addMacro(macroOrder, macroXML)) {
			DAOManager.getDAO(PlayerMacrossesDAO.class).addMacro(player.getObjectId(), macroOrder, macroXML);
		}
		else {
			DAOManager.getDAO(PlayerMacrossesDAO.class).updateMacro(player.getObjectId(), macroOrder, macroXML);
		}
	}

	/**
	 * Remove macro with specified index from specified player
	 * 
	 * @param player
	 *          Player
	 * @param macroOrder
	 *          Macro order index
	 */
	public static void removeMacro(Player player, int macroOrder) {
		if (player.getMacroList().removeMacro(macroOrder)) {
			DAOManager.getDAO(PlayerMacrossesDAO.class).deleteMacro(player.getObjectId(), macroOrder);
		}
	}

	/**
	 * Gets a player ONLY if he is in the cache
	 * 
	 * @return Player or null if not cached
	 */
	public static Player getCachedPlayer(int playerObjectId) {
		return playerCache.get(playerObjectId);
	}

	public static String getPlayerName(Integer objectId){
		return getPlayerNames(Collections.singleton(objectId)).get(objectId);
	}

	public static Map<Integer, String> getPlayerNames(Collection<Integer> playerObjIds){

		// if there is no ids - return just empty map
		if(GenericValidator.isBlankOrNull(playerObjIds)){
			return Collections.emptyMap();
		}

		final Map<Integer, String> result = Maps.newHashMap();

		// Copy ids to separate set
		// It's dangerous to modify input collection, can have side results
		final Set<Integer> playerObjIdsCopy = Sets.newHashSet(playerObjIds);

		// Get names of all online players
		// Certain names can be changed in runtime
		// this should prevent errors
		World.getInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player object) {
				if(playerObjIdsCopy.contains(object.getObjectId())){
					result.put(object.getObjectId(), object.getName());
					playerObjIdsCopy.remove(object.getObjectId());
				}
			}
		});

		result.putAll(DAOManager.getDAO(PlayerDAO.class).getPlayerNames(playerObjIdsCopy));
		return result;
	}
}
