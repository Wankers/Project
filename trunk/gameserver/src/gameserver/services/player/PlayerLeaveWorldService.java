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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import commons.database.dao.DAOManager;
import gameserver.configs.main.GSConfig;
import gameserver.controllers.SummonController.UnsummonType;
import gameserver.dao.ItemCooldownsDAO;
import gameserver.dao.PlayerCooldownsDAO;
import gameserver.dao.PlayerDAO;
import gameserver.dao.PlayerEffectsDAO;
import gameserver.dao.PlayerLifeStatsDAO;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.items.storage.StorageType;
import gameserver.model.team2.alliance.PlayerAllianceService;
import gameserver.model.team2.group.PlayerGroupService;
import gameserver.network.aion.clientpackets.CM_QUIT;
import gameserver.questEngine.QuestEngine;
import gameserver.questEngine.model.QuestEnv;
import gameserver.services.AutoGroupService2;
import gameserver.services.BrokerService;
import gameserver.services.ChatService;
import gameserver.services.DuelService;
import gameserver.services.ExchangeService;
import gameserver.services.FindGroupService;
import gameserver.services.LegionService;
import gameserver.services.PunishmentService;
import gameserver.services.RepurchaseService;
import gameserver.services.drop.DropService;
import gameserver.services.instance.InstanceService;
import gameserver.services.teleport.TeleportService;
import gameserver.services.toypet.PetSpawnService;
import gameserver.services.tvt.TvtService;
import gameserver.taskmanager.tasks.ExpireTimerTask;
import gameserver.utils.ThreadPoolManager;
import gameserver.utils.audit.GMService;

/**
 * @author ATracer
 */
public class PlayerLeaveWorldService {

	private static final Logger log = LoggerFactory.getLogger(PlayerLeaveWorldService.class);

	/**
	 * @param player
	 * @param delay
	 */
	public static final void startLeaveWorldDelay(final Player player, int delay) {
		// force stop movement of player
		player.getController().stopMoving();

		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				startLeaveWorld(player);
			}
		}, delay);
	}

	/**
	 * This method is called when player leaves the game, which includes just two cases: either player goes back to char
	 * selection screen or it's leaving the game [closing client].<br>
	 * <br>
	 * <b><font color='red'>NOTICE: </font> This method is called only from {@link GameConnection} and {@link CM_QUIT} and
	 * must not be called from anywhere else</b>
	 */
	public static final void startLeaveWorld(Player player) {
		log.info("Player logged out: " + player.getName() + " Account: "
			+ (player.getClientConnection() != null ? player.getClientConnection().getAccount().getName() : "disconnected"));

		FindGroupService.getInstance().removeFindGroup(player.getRace(), 0x00, player.getObjectId());
		FindGroupService.getInstance().removeFindGroup(player.getRace(), 0x04, player.getObjectId());
		player.onLoggedOut();
		BrokerService.getInstance().removePlayerCache(player);
		ExchangeService.getInstance().cancelExchange(player);
		RepurchaseService.getInstance().removeRepurchaseItems(player);
		AutoGroupService2.getInstance().onPlayerLogOut(player);
		InstanceService.onLogOut(player);
		GMService.getInstance().onPlayerLogedOut(player);

		if(player.isLooting())
			DropService.getInstance().closeDropList(player, player.getLootingNpcOid());

		// Update prison timer
		if (player.isInPrison()) {
			long prisonTimer = System.currentTimeMillis() - player.getStartPrison();
			prisonTimer = player.getPrisonTimer() - prisonTimer;
			player.setPrisonTimer(prisonTimer);
			log.debug("Update prison timer to " + prisonTimer / 1000 + " seconds !");
		}
		// store current effects
		DAOManager.getDAO(PlayerEffectsDAO.class).storePlayerEffects(player);
		DAOManager.getDAO(PlayerCooldownsDAO.class).storePlayerCooldowns(player);
		DAOManager.getDAO(ItemCooldownsDAO.class).storeItemCooldowns(player);
		DAOManager.getDAO(PlayerLifeStatsDAO.class).updatePlayerLifeStat(player);
		// fix legion warehouse exploits
		LegionService.getInstance().LegionWhUpdate(player);
		player.getEffectController().removeAllEffects(true);
		player.getLifeStats().cancelAllTasks();

		if (player.getLifeStats().isAlreadyDead() && !player.isInInstance())
			TeleportService.moveToBindLocation(player, false);

		else if (DuelService.getInstance().isDueling(player.getObjectId())) {
			DuelService.getInstance().loseDuel(player);
		}
                /*
                if (TvtService.getInstance().getTvtByLevel(player.getLevel()).getHolders().getPlayer(player)) {
                   TvtService.getInstance().unRegPlayer(player);
                }
                */
		if (player.getSummon() != null)
			player.getSummon().getController().release(UnsummonType.LOGOUT);

		PetSpawnService.dismissPet(player, true);

		if (player.getPostman() != null)
			player.getPostman().getController().onDelete();
		player.setPostman(null);

		PunishmentService.stopPrisonTask(player, true);
		PunishmentService.stopGatherableTask(player, true);

		if (player.isLegionMember())
			LegionService.getInstance().onLogout(player);

		PlayerGroupService.onPlayerLogout(player);
		PlayerAllianceService.onPlayerLogout(player);

		QuestEngine.getInstance().onLogOut(new QuestEnv(null, player, 0, 0));

		player.getController().delete();
		player.getCommonData().setOnline(false);
		player.getCommonData().setLastOnline(new Timestamp(System.currentTimeMillis()));
		player.setClientConnection(null);

		DAOManager.getDAO(PlayerDAO.class).onlinePlayer(player, false);

		if (GSConfig.ENABLE_CHAT_SERVER)
			ChatService.onPlayerLogout(player);

		PlayerService.storePlayer(player);

		ExpireTimerTask.getInstance().removePlayer(player);
		player.getEquipment().setOwner(null);
		player.getInventory().setOwner(null);
		player.getWarehouse().setOwner(null);
		player.getStorage(StorageType.ACCOUNT_WAREHOUSE.getId()).setOwner(null);
	}

	/**
	 * @param player
	 */
	public static void tryLeaveWorld(Player player) {
		player.getMoveController().abortMove();
		if (player.getController().isInShutdownProgress())
			PlayerLeaveWorldService.startLeaveWorld(player);

		// prevent ctrl+alt+del / close window exploit
		else {
			int delay = 15;
			PlayerLeaveWorldService.startLeaveWorldDelay(player, (delay * 1000));
		}
	}
}
