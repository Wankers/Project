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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javolution.util.FastList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import commons.database.dao.DAOManager;
import commons.versionning.Version;
import gameserver.GameServer;
import gameserver.cache.HTMLCache;
import gameserver.configs.administration.AdminConfig;
import gameserver.configs.main.CustomConfig;
import gameserver.configs.main.EventsConfig;
import gameserver.configs.main.GSConfig;
import gameserver.configs.main.HTMLConfig;
import gameserver.configs.main.PeriodicSaveConfig;
import gameserver.dao.AbyssRankDAO;
import gameserver.dao.InventoryDAO;
import gameserver.dao.ItemStoneListDAO;
import gameserver.dao.PlayerDAO;
import gameserver.dao.PlayerPasskeyDAO;
import gameserver.dao.PlayerPunishmentsDAO;
import gameserver.dao.PlayerQuestListDAO;
import gameserver.dao.PlayerSkillListDAO;
import gameserver.dao.WeddingDAO;
import gameserver.model.ChatType;
import gameserver.model.TaskId;
import gameserver.model.account.Account;
import gameserver.model.account.CharacterBanInfo;
import gameserver.model.account.CharacterPasskey.ConnectType;
import gameserver.model.account.PlayerAccountData;
import gameserver.model.gameobjects.Item;
import gameserver.model.gameobjects.PersistentState;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.gameobjects.player.PlayerCommonData;
import gameserver.model.gameobjects.player.emotion.Emotion;
import gameserver.model.gameobjects.player.motion.Motion;
import gameserver.model.gameobjects.player.title.Title;
import gameserver.model.gameobjects.state.CreatureSeeState;
import gameserver.model.gameobjects.state.CreatureVisualState;
import gameserver.model.items.storage.IStorage;
import gameserver.model.items.storage.Storage;
import gameserver.model.items.storage.StorageType;
import gameserver.model.skill.PlayerSkillEntry;
import gameserver.model.team2.alliance.PlayerAllianceService;
import gameserver.model.team2.group.PlayerGroupService;
import gameserver.network.aion.AionConnection;
import gameserver.network.aion.serverpackets.SM_ABYSS_RANK;
import gameserver.network.aion.serverpackets.SM_CHANNEL_INFO;
import gameserver.network.aion.serverpackets.SM_CHARACTER_SELECT;
import gameserver.network.aion.serverpackets.SM_CUBE_UPDATE;
import gameserver.network.aion.serverpackets.SM_EMOTION_LIST;
import gameserver.network.aion.serverpackets.SM_ENTER_WORLD_CHECK;
import gameserver.network.aion.serverpackets.SM_GAME_TIME;
import gameserver.network.aion.serverpackets.SM_INFLUENCE_RATIO;
import gameserver.network.aion.serverpackets.SM_INSTANCE_INFO;
import gameserver.network.aion.serverpackets.SM_INVENTORY_INFO;
import gameserver.network.aion.serverpackets.SM_ITEM_COOLDOWN;
import gameserver.network.aion.serverpackets.SM_MACRO_LIST;
import gameserver.network.aion.serverpackets.SM_MESSAGE;
import gameserver.network.aion.serverpackets.SM_MOTION;
import gameserver.network.aion.serverpackets.SM_PLAYER_SPAWN;
import gameserver.network.aion.serverpackets.SM_PLAYER_STATE;
import gameserver.network.aion.serverpackets.SM_PRICES;
import gameserver.network.aion.serverpackets.SM_QUEST_COMPLETED_LIST;
import gameserver.network.aion.serverpackets.SM_QUEST_LIST;
import gameserver.network.aion.serverpackets.SM_QUIT_RESPONSE;
import gameserver.network.aion.serverpackets.SM_RECIPE_LIST;
import gameserver.network.aion.serverpackets.SM_SIEGE_LOCATION_INFO;
import gameserver.network.aion.serverpackets.SM_SKILL_COOLDOWN;
import gameserver.network.aion.serverpackets.SM_SKILL_LIST;
import gameserver.network.aion.serverpackets.SM_STATS_INFO;
import gameserver.network.aion.serverpackets.SM_TITLE_INFO;
import gameserver.network.aion.serverpackets.SM_UI_SETTINGS;
import gameserver.questEngine.model.QuestState;
import gameserver.questEngine.model.QuestStatus;
import gameserver.services.AutoGroupService2;
import gameserver.services.BrokerService;
import gameserver.services.ClassChangeService;
import gameserver.services.EventService;
import gameserver.services.HTMLService;
import gameserver.services.KiskService;
import gameserver.services.LegionService;
import gameserver.services.MailService;
import gameserver.services.PetitionService;
import gameserver.services.PunishmentService;
import gameserver.services.PunishmentService.PunishmentType;
import gameserver.services.SiegeService;
import gameserver.services.StigmaService;
import gameserver.services.SurveyService;
import gameserver.services.abyss.AbyssSkillService;
import gameserver.services.reward.RewardService;
import gameserver.services.teleport.TeleportService;
import gameserver.services.toypet.PetService;
import gameserver.services.transfers.PlayerTransferService;
import gameserver.skillengine.effect.AbnormalState;
import gameserver.taskmanager.tasks.ExpireTimerTask;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.ThreadPoolManager;
import gameserver.utils.audit.AuditLogger;
import gameserver.utils.audit.GMService;
import gameserver.utils.rates.Rates;
import gameserver.world.World;
import java.sql.Timestamp;

/**
 * @author ATracer
 */
public final class PlayerEnterWorldService {

	private static final Logger log = LoggerFactory.getLogger(PlayerEnterWorldService.class);
	private static final String serverName = "Bienvenue sur " + GSConfig.SERVER_NAME + "!";
	private static final String serverIntro = "Veuillez vous rappeler s'il vous plaît que :";
	private static final String serverInfo;
	private static final String alInfo;
	private static final Set<Integer> pendingEnterWorld = new HashSet<Integer>();

	static {
		String infoBuffer;
		String alBuffer;

		infoBuffer = "Annonce : " + GSConfig.SERVER_NAME + " ne vous demandera jamais votre mot de passe.\n";
		infoBuffer += "Annonce : La publicité pour un autre serveur est interdite.";

		alBuffer = "=============================\n";
		alBuffer += "Developpé par Aion-Extreme\nhttp://www.aion-core.net\n";
		alBuffer += "Copyright 2012\n";

		if (GSConfig.SERVER_MOTD_DISPLAYREV) {
			alBuffer += "-----------------------------\n";
			alBuffer += "Server Revision: " + String.format("%-6s", new Version(GameServer.class).getRevision()) + "\n";
		}
		alBuffer += "=============================\n";
		alBuffer += "Amusez vous bien et profiter de votre séjour sur notre serveur.";

		serverInfo = infoBuffer;
		alInfo = alBuffer;

		infoBuffer = null;
		alBuffer = null;
	}

	/**
	 * @param objectId
	 * @param client
	 */
	public static final void startEnterWorld(final int objectId, final AionConnection client) {
		// check if char is banned
		PlayerAccountData playerAccData = client.getAccount().getPlayerAccountData(objectId);
		Timestamp lastOnline = playerAccData.getPlayerCommonData().getLastOnline();
		if (lastOnline != null) {
			if (System.currentTimeMillis() - lastOnline.getTime() < (GSConfig.CHARACTER_REENTRY_TIME * 1000)) {
				client.sendPacket(new SM_ENTER_WORLD_CHECK((byte) 6)); // 20 sec time
				return;
			}
		}
		CharacterBanInfo cbi = client.getAccount().getPlayerAccountData(objectId).getCharBanInfo();
		if (cbi != null) {
			if (cbi.getEnd() > System.currentTimeMillis() / 1000) {
				client.close(new SM_QUIT_RESPONSE(), false);
				return;
			}
			else {
				DAOManager.getDAO(PlayerPunishmentsDAO.class).unpunishPlayer(objectId, PunishmentType.CHARBAN);
			}
		}
		// passkey check
		if (GSConfig.PASSKEY_ENABLE && !client.getAccount().getCharacterPasskey().isPass()) {
			showPasskey(objectId, client);
		}
		else {
			validateAndEnterWorld(objectId, client);
		}
	}

	/**
	 * @param objectId
	 * @param client
	 */
	private static final void showPasskey(final int objectId, final AionConnection client) {
		client.getAccount().getCharacterPasskey().setConnectType(ConnectType.ENTER);
		client.getAccount().getCharacterPasskey().setObjectId(objectId);
		boolean isExistPasskey = DAOManager.getDAO(PlayerPasskeyDAO.class).existCheckPlayerPasskey(
				client.getAccount().getId());

		if (!isExistPasskey)
			client.sendPacket(new SM_CHARACTER_SELECT(0));
		else
			client.sendPacket(new SM_CHARACTER_SELECT(1));
	}

	/**
	 * @param objectId
	 * @param client
	 */
	private static final void validateAndEnterWorld(final int objectId, final AionConnection client) {
		synchronized (pendingEnterWorld) {
			if (pendingEnterWorld.contains(objectId)) {
				log.warn("Skipping enter world " + objectId);
				return;
			}
			pendingEnterWorld.add(objectId);
		}
		int delay = 0;
		// double checked enter world
		if (World.getInstance().findPlayer(objectId) != null) {
			delay = 15000;
			log.warn("Postponed enter world " + objectId);
		}
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				try {
					Player player = World.getInstance().findPlayer(objectId);
					if (player != null) {
						AuditLogger.info(player, "Duplicate player in world");
						client.close(new SM_QUIT_RESPONSE(), false);
						return;
					}
					enterWorld(client, objectId);
				}
				catch (Throwable ex) {
					log.error("Error during enter world " + objectId, ex);
				}
				finally {
					synchronized (pendingEnterWorld) {
						pendingEnterWorld.remove(objectId);
					}
				}
			}

		}, delay);
	}

	/**
	 * @param client
	 * @param objectId
	 */
	public static final void enterWorld(AionConnection client, int objectId) {
		Account account = client.getAccount();
		PlayerAccountData playerAccData = client.getAccount().getPlayerAccountData(objectId);

		if (playerAccData == null) {
			// Somebody wanted to login on character that is not at his account
			return;
		}
		Player player = PlayerService.getPlayer(objectId, account);

		if (player != null && client.setActivePlayer(player)) {
			player.setClientConnection(client);

			log.info("[MAC_AUDIT] Player " + player.getName() + " (account " + account.getName()
					+ ") has entered world with " + client.getMacAddress() + " MAC.");
			World.getInstance().storeObject(player);

			StigmaService.onPlayerLogin(player);

			/**
			 * Energy of Repose must be calculated before sending SM_STATS_INFO
			 */
			if (playerAccData.getPlayerCommonData().getLastOnline() != null) {
				long lastOnline = playerAccData.getPlayerCommonData().getLastOnline().getTime();
				PlayerCommonData pcd = player.getCommonData();
				long secondsOffline = (System.currentTimeMillis() / 1000) - lastOnline / 1000;
				if (pcd.isReadyForSalvationPoints()) {
					// 10 mins offline = 0 salvation points.
					if (secondsOffline > 10 * 60) {
						player.getCommonData().resetSalvationPoints();
					}
				}
				if (pcd.isReadyForReposteEnergy()) {
					pcd.updateMaxReposte();
					// more than 4 hours offline = start counting Reposte Energy addition.
					if (secondsOffline > 4 * 3600) {
						double hours = secondsOffline / 3600d;
						long maxRespose = player.getCommonData().getMaxReposteEnergy();
						if (hours > 24)
							hours = 24;
						// 24 hours offline = 100% Reposte Energy
						long addResposeEnergy = (long) ((hours / 24) * maxRespose);
						pcd.addReposteEnergy(addResposeEnergy);
					}
				}
				if (((System.currentTimeMillis() / 1000) - lastOnline) > 300)
					player.getCommonData().setDp(0);
			}

			client.sendPacket(new SM_SKILL_LIST(player));
			AbyssSkillService.onEnterWorld(player);

			if (player.getSkillCoolDowns() != null)
				client.sendPacket(new SM_SKILL_COOLDOWN(player.getSkillCoolDowns()));

			if (player.getItemCoolDowns() != null)
				client.sendPacket(new SM_ITEM_COOLDOWN(player.getItemCoolDowns()));

			FastList<QuestState> questList = FastList.newInstance();
			FastList<QuestState> completeQuestList = FastList.newInstance();
			for (QuestState qs : player.getQuestStateList().getAllQuestState()) {
				if (qs.getStatus() == QuestStatus.NONE)
					continue;
				if (qs.getStatus() == QuestStatus.COMPLETE)
					completeQuestList.add(qs);
				else
					questList.add(qs);
			}
			client.sendPacket(new SM_QUEST_COMPLETED_LIST(completeQuestList));
			client.sendPacket(new SM_QUEST_LIST(questList));
			client.sendPacket(new SM_RECIPE_LIST(player.getRecipeList().getRecipeList()));
			client.sendPacket(new SM_ENTER_WORLD_CHECK());

			byte[] uiSettings = player.getPlayerSettings().getUiSettings();
			byte[] shortcuts = player.getPlayerSettings().getShortcuts();

			if (uiSettings != null)
				client.sendPacket(new SM_UI_SETTINGS(uiSettings, 0));

			if (shortcuts != null)
				client.sendPacket(new SM_UI_SETTINGS(shortcuts, 1));

			sendItemInfos(client, player);
			playerLoggedIn(player);

			client.sendPacket(new SM_INSTANCE_INFO(player, false, false));

			sendMacroList(client, player);

			client.sendPacket(new SM_GAME_TIME());

			client.sendPacket(new SM_MOTION(player.getMotions().getMotions().values()));

			client.sendPacket(new SM_TITLE_INFO(player));
			client.sendPacket(new SM_CHANNEL_INFO(player.getPosition()));
			client.sendPacket(new SM_PLAYER_SPAWN(player));

			client.sendPacket(new SM_EMOTION_LIST((byte) 0, player.getEmotions().getEmotions()));

			client.sendPacket(new SM_INFLUENCE_RATIO());
			client.sendPacket(new SM_SIEGE_LOCATION_INFO());
			// TODO: Send Rift Announce Here
			client.sendPacket(new SM_PRICES());
			client.sendPacket(new SM_ABYSS_RANK(player.getAbyssRank()));

			// Intro message
			PacketSendUtility.sendYellowMessage(player, serverName);
			PacketSendUtility.sendYellowMessage(player, serverIntro);
			PacketSendUtility.sendBrightYellowMessage(player, serverInfo);
			PacketSendUtility.sendWhiteMessage(player, alInfo);

			player.setRates(Rates.getRatesFor(client.getAccount().getMembership()));
			if (CustomConfig.PREMIUM_NOTIFY) {
				showPremiumAccountInfo(client, account);
			}

			//GM addskill 1904	
            if (player.getAccessLevel() >= AdminConfig.COMMAND_SPECIAL_SKILL)
			{
                PlayerSkillEntry skill = new PlayerSkillEntry(1904, true, 1, PersistentState.NOACTION);
                player.getSkillList().addStigmaSkill(player, skill.getSkillId(), skill.getSkillLevel(), true);
            }

			if (player.isGM()) {
				if (AdminConfig.INVULNERABLE_GM_CONNECTION || AdminConfig.INVISIBLE_GM_CONNECTION
						|| AdminConfig.ENEMITY_MODE_GM_CONNECTION.equalsIgnoreCase("Neutral")
						|| AdminConfig.ENEMITY_MODE_GM_CONNECTION.equalsIgnoreCase("Enemy")
						|| AdminConfig.VISION_GM_CONNECTION || AdminConfig.WHISPER_GM_CONNECTION) {
					PacketSendUtility.sendMessage(player, "=============================");
					if (AdminConfig.INVULNERABLE_GM_CONNECTION) {
						player.setInvul(true);
						PacketSendUtility.sendMessage(player, ">> Connection in Invulnerable mode <<");
					}
					if (AdminConfig.INVISIBLE_GM_CONNECTION) {
						player.getEffectController().setAbnormal(AbnormalState.HIDE.getId());
						player.setVisualState(CreatureVisualState.HIDE3);
						PacketSendUtility.broadcastPacket(player, new SM_PLAYER_STATE(player), true);
						PacketSendUtility.sendMessage(player, ">> Connection in Invisible mode <<");
					}
					if (AdminConfig.ENEMITY_MODE_GM_CONNECTION.equalsIgnoreCase("Neutral")) {
						player.setAdminNeutral(3);
						player.setAdminEnmity(0);
						PacketSendUtility.sendMessage(player, ">> Connection in Neutral mode <<");
					}
					if (AdminConfig.ENEMITY_MODE_GM_CONNECTION.equalsIgnoreCase("Enemy")) {
						player.setAdminNeutral(0);
						player.setAdminEnmity(3);
						PacketSendUtility.sendMessage(player, ">> Connection in Enemy mode <<");
					}
					if (AdminConfig.VISION_GM_CONNECTION) {
						player.setSeeState(CreatureSeeState.SEARCH2);
						PacketSendUtility.broadcastPacket(player, new SM_PLAYER_STATE(player), true);
						PacketSendUtility.sendMessage(player, ">> Connection in Vision mode <<");
					}
					if (AdminConfig.WHISPER_GM_CONNECTION) {
						player.setUnWispable();
						PacketSendUtility.sendMessage(player, ">> Accepting Whisper : OFF <<");
					}
					PacketSendUtility.sendMessage(player, "=============================");
				}
			}

			KiskService.onLogin(player);
			TeleportService.sendSetBindPoint(player);

			// Alliance Packet after SetBindPoint
			PlayerAllianceService.onPlayerLogin(player);

			if (player.isInPrison())
			{
				//@author GoodT
				//fix prisonbreak - if player log on different map as prison will be teleported back to prison
				if(player.getWorldId() != 510010000 || player.getWorldId() == 520010000)
					TeleportService.teleportToPrison(player);
				
				PunishmentService.updatePrisonStatus(player);
			}

			if (player.isNotGatherable())
				PunishmentService.updateGatherableStatus(player);

			if (player.isLegionMember())
				LegionService.getInstance().onLogin(player);

			PlayerGroupService.onPlayerLogin(player);
			PetService.getInstance().onPlayerLogin(player);
			MailService.getInstance().onPlayerLogin(player);
			BrokerService.getInstance().onPlayerLogin(player);
			PetitionService.getInstance().onPlayerLogin(player);
			SiegeService.getInstance().onPlayerLogin(player);
			AutoGroupService2.getInstance().onPlayerLogin(player);
			ClassChangeService.showClassChangeDialog(player);

			GMService.getInstance().onPlayerLogin(player);
			/**
			 * Trigger restore services on login.
			 */
			player.getLifeStats().updateCurrentStats();

			if (HTMLConfig.ENABLE_HTML_WELCOME)
				HTMLService.showHTML(player, HTMLCache.getInstance().getHTML("welcome.xhtml"));

			player.getNpcFactions().sendDailyQuest();

			if (HTMLConfig.ENABLE_GUIDES)
				HTMLService.onPlayerLogin(player);

			for (StorageType st : StorageType.values()) {
				if (st == StorageType.LEGION_WAREHOUSE)
					continue;
				IStorage storage = player.getStorage(st.getId());
				if (storage != null) {
					for (Item item : storage.getItemsWithKinah())
						if (item.getExpireTime() > 0)
							ExpireTimerTask.getInstance().addTask(item, player);
				}
			}

			for (Item item : player.getEquipment().getEquippedItems())
				if (item.getExpireTime() > 0)
					ExpireTimerTask.getInstance().addTask(item, player);

			for (Motion motion : player.getMotions().getMotions().values()) {
				if (motion.getExpireTime() != 0) {
					ExpireTimerTask.getInstance().addTask(motion, player);
				}
			}

			for (Emotion emotion : player.getEmotions().getEmotions()) {
				if (emotion.getExpireTime() != 0) {
					ExpireTimerTask.getInstance().addTask(emotion, player);
				}
			}

			for (Title title : player.getTitleList().getTitles()) {
				if (title.getExpireTime() != 0) {
					ExpireTimerTask.getInstance().addTask(title, player);
				}
			}
			// scheduler periodic update
			player.getController().addTask(
					TaskId.PLAYER_UPDATE,
					ThreadPoolManager.getInstance().scheduleAtFixedRate(new GeneralUpdateTask(player.getObjectId()),
					PeriodicSaveConfig.PLAYER_GENERAL * 1000, PeriodicSaveConfig.PLAYER_GENERAL * 1000));
			player.getController().addTask(
					TaskId.INVENTORY_UPDATE,
					ThreadPoolManager.getInstance().scheduleAtFixedRate(new ItemUpdateTask(player.getObjectId()),
					PeriodicSaveConfig.PLAYER_ITEMS * 1000, PeriodicSaveConfig.PLAYER_ITEMS * 1000));

			SurveyService.getInstance().showAvailable(player);

			if (CustomConfig.ENABLE_REWARD_SERVICE)
				RewardService.getInstance().verify(player);

			if (EventsConfig.ENABLE_EVENT_SERVICE)
				EventService.getInstance().onPlayerLogin(player);

			PlayerTransferService.getInstance().onEnterWorld(player);
			player.setPartnerId(DAOManager.getDAO(WeddingDAO.class).loadPartnerId(player));
		}
		else {
			log.info("[DEBUG] enter world" + objectId + ", Player: " + player);
		}
	}

	/**
	 * @param client
	 * @param player
	 */
	// TODO! this method code is really odd [Nemesiss]
	private static void sendItemInfos(AionConnection client, Player player) {
		// Cubesize limit set in inventory.
		int questExpands = player.getQuestExpands();
		int npcExpands = player.getNpcExpands();
		player.getInventory().setLimit(StorageType.CUBE.getLimit() + (questExpands + npcExpands) * 9);
		player.getWarehouse().setLimit(StorageType.REGULAR_WAREHOUSE.getLimit() + player.getWarehouseSize() * 8);

		// items
		Storage inventory = player.getInventory();
		List<Item> equipedItems = player.getEquipment().getEquippedItems();
		if (equipedItems.size() != 0) {
			client.sendPacket(new SM_INVENTORY_INFO(player.getEquipment().getEquippedItems(), npcExpands, questExpands,
					player));
		}

		List<Item> unequipedItems = inventory.getItemsWithKinah();
		int itemsSize = unequipedItems.size();
		if (itemsSize != 0) {
			int index = 0;
			while (index + 10 < itemsSize) {
				client.sendPacket(new SM_INVENTORY_INFO(unequipedItems.subList(index, index + 10), npcExpands, questExpands,
						player));
				index += 10;
			}
			client.sendPacket(new SM_INVENTORY_INFO(unequipedItems.subList(index, itemsSize), npcExpands, questExpands,
					player));
		}
		client.sendPacket(new SM_INVENTORY_INFO());
		client.sendPacket(new SM_STATS_INFO(player));
		client.sendPacket(SM_CUBE_UPDATE.stigmaSlots(player.getCommonData().getAdvancedStigmaSlotSize()));
	}

	private static void sendMacroList(AionConnection client, Player player) {
		client.sendPacket(new SM_MACRO_LIST(player, false));
		if (player.getMacroList().getSize() > 7)
			client.sendPacket(new SM_MACRO_LIST(player, true));
	}

	/**
	 * @param player
	 */
	private static void playerLoggedIn(Player player) {
		log.info("Player logged in: " + player.getName() + " Account: "
				+ player.getClientConnection().getAccount().getName());
		player.getCommonData().setOnline(true);
		DAOManager.getDAO(PlayerDAO.class).onlinePlayer(player, true);
		player.onLoggedIn();
		player.setOnlineTime();
	}

	private static void showPremiumAccountInfo(AionConnection client, Account account) {
		byte membership = account.getMembership();
		if (membership > 0) {
			String accountType = "";
			switch (account.getMembership()) {
				case 1:
					accountType = "premium";
					break;
				case 2:
					accountType = "VIP";
					break;
			}
			client.sendPacket(new SM_MESSAGE(0, null, "Your account is " + accountType, ChatType.GOLDEN_YELLOW));
		}
	}

}

class GeneralUpdateTask implements Runnable {

	private static final Logger log = LoggerFactory.getLogger(GeneralUpdateTask.class);
	private final int playerId;

	GeneralUpdateTask(int playerId) {
		this.playerId = playerId;
	}

	@Override
	public void run() {
		Player player = World.getInstance().findPlayer(playerId);
		if (player != null) {
			try {
				DAOManager.getDAO(AbyssRankDAO.class).storeAbyssRank(player);
				DAOManager.getDAO(PlayerSkillListDAO.class).storeSkills(player);
				DAOManager.getDAO(PlayerQuestListDAO.class).store(player);
				DAOManager.getDAO(PlayerDAO.class).storePlayer(player);
			}
			catch (Exception ex) {
				log.error("Exception during periodic saving of player " + player.getName(), ex);
			}
		}

	}

}

class ItemUpdateTask implements Runnable {

	private static final Logger log = LoggerFactory.getLogger(ItemUpdateTask.class);
	private final int playerId;

	ItemUpdateTask(int playerId) {
		this.playerId = playerId;
	}

	@Override
	public void run() {
		Player player = World.getInstance().findPlayer(playerId);
		if (player != null) {
			try {
				DAOManager.getDAO(InventoryDAO.class).store(player);
				DAOManager.getDAO(ItemStoneListDAO.class).save(player);
			}
			catch (Exception ex) {
				log.error("Exception during periodic saving of player items " + player.getName(), ex);
			}
		}
	}

}
