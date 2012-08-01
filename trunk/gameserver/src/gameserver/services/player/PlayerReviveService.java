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

import gameserver.configs.administration.AdminConfig;
import gameserver.model.EmotionType;
import gameserver.model.gameobjects.Item;
import gameserver.model.gameobjects.Kisk;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.gameobjects.state.CreatureState;
import gameserver.network.aion.serverpackets.SM_EMOTION;
import gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import gameserver.network.aion.serverpackets.SM_MOTION;
import gameserver.network.aion.serverpackets.SM_PLAYER_INFO;
import gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import gameserver.services.teleport.TeleportService;
import gameserver.taskmanager.tasks.TeamMoveUpdater;
import gameserver.utils.audit.AuditLogger;
import gameserver.utils.PacketSendUtility;
import gameserver.world.World;
import gameserver.world.WorldMap;
import gameserver.world.WorldPosition;

/**
 * @author Jego, xTz
 */
public class PlayerReviveService {

	public static final void duelRevive(Player player) {
		revive(player, 30, 30, false);
		PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.RESURRECT), true);
		player.getGameStats().updateStatsAndSpeedVisually();
		player.unsetResPosState();
	}

	public static final void skillRevive(Player player) {
		if (!(player.getResStatus())) {
			cancelRes(player);
			return;
		}


		boolean isFlyingBeforeDeath = player.getIsFlyingBeforeDeath();
		revive(player, 10, 10, true);
		
		if (isFlyingBeforeDeath)
			player.setState(CreatureState.FLYING);
		
		PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.RESURRECT), true);
		PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_REBIRTH_MASSAGE_ME);

		player.getGameStats().updateStatsAndSpeedVisually();
		
		if (player.isInPrison())
			TeleportService.teleportToPrison(player);

		if (player.isInResPostState())
			TeleportService.teleportTo(player, player.getWorldId(), player.getInstanceId(), player.getResPosX(), player.getResPosY(), player.getResPosZ(), 0, true);
		player.unsetResPosState();
		
		//if player was flying before res, start flying
		if (isFlyingBeforeDeath) {
			player.getFlyController().startFly();
		}
	}

	public static final void rebirthRevive(Player player) {
		if (!player.canUseRebirthRevive()) {
			return;
		}

		if (player.getRebirthResurrectPercent() <= 0) {
			PacketSendUtility.sendMessage(player, "Error: Rebirth effect missing percent.");
			player.setRebirthResurrectPercent(5);
		}
		boolean soulSickness = true;
		int rebirthResurrectPercent = player.getRebirthResurrectPercent();
		if(player.getAccessLevel() >= AdminConfig.ADMIN_AUTO_RES) {
			rebirthResurrectPercent = 100;
			soulSickness = false;
		}
		boolean isFlyingBeforeDeath = player.getIsFlyingBeforeDeath();
		revive(player, rebirthResurrectPercent, rebirthResurrectPercent, soulSickness);
		
		if (isFlyingBeforeDeath)
			player.setState(CreatureState.FLYING);
		PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.RESURRECT), true);
		PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_REBIRTH_MASSAGE_ME);
	
		player.getGameStats().updateStatsAndSpeedVisually();
		
		if (player.isInPrison())
			TeleportService.teleportToPrison(player);
		player.unsetResPosState();
		
		//if player was flying before res, start flying
		if (isFlyingBeforeDeath) {
			player.getFlyController().startFly();
		}
	}
	
	public static final void bindRevive(Player player) {
		player.setTelEffect(4);
		if(player.getLevel() < 10)
		{
			revive(player, 75, 75, false);
                }
		else
			revive(player, 25, 25, true);

		PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_REBIRTH_MASSAGE_ME);
		// TODO: It is not always necessary.
		// sendPacket(new SM_QUEST_LIST(activePlayer));
		player.getGameStats().updateStatsAndSpeedVisually();
		PacketSendUtility.sendPacket(player, new SM_PLAYER_INFO(player, false));
		PacketSendUtility.sendPacket(player, new SM_MOTION(player.getObjectId(), player.getMotions().getActiveMotions()));
		if (player.isInPrison())
			TeleportService.teleportToPrison(player);
		else
			TeleportService.moveToBindLocation(player, true);
		player.unsetResPosState();
	}

	public static final void kiskRevive(Player player) {
		Kisk kisk = player.getKisk();
		if (kisk == null) {
			return;
		}
		player.setTelEffect(2);
		if (player.isInPrison())
			TeleportService.teleportToPrison(player);
		else if (kisk.isActive()) {
			WorldPosition bind = kisk.getPosition();
			kisk.resurrectionUsed(player);
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_REBIRTH_MASSAGE_ME);
			revive(player, 25, 25, false);
			player.getGameStats().updateStatsAndSpeedVisually();
			//PacketSendUtility.sendPacket(player, new SM_PLAYER_INFO(player, false)); // send by TeleportService
			//PacketSendUtility.sendPacket(player, new SM_MOTION(player.getObjectId(), player.getMotions().getActiveMotions())); // send by TeleportService
			player.unsetResPosState();
			TeleportService.moveToKiskLocation(player, bind);
		}
	}

	public static final void instanceRevive(Player player) {
                player.setTelEffect(4);
		// Revive in Instances
		if (player.getPosition().getWorldMapInstance().getInstanceHandler().onReviveEvent(player)) {
			return;
		}
		WorldMap map = World.getInstance().getWorldMap(player.getWorldId());
		if (map == null) {
			bindRevive(player);
			return;
		}
		revive(player, 25, 25, true);
		PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_REBIRTH_MASSAGE_ME);
		player.getGameStats().updateStatsAndSpeedVisually();
		PacketSendUtility.sendPacket(player, new SM_PLAYER_INFO(player, false));
		PacketSendUtility.sendPacket(player, new SM_MOTION(player.getObjectId(), player.getMotions().getActiveMotions()));
		if (map.isInstanceType()
			&& (player.getInstanceStartPosX() != 0 && player.getInstanceStartPosY() != 0 && player.getInstanceStartPosZ() != 0)) {
			TeleportService.teleportTo(player, player.getWorldId(), player.getInstanceStartPosX(),
				player.getInstanceStartPosY(), player.getInstanceStartPosZ(), 0);
		}
		else {
			bindRevive(player);
		}
		player.unsetResPosState();
	}

	public static final void revive(Player player, int hpPercent, int mpPercent, boolean setSoulsickness) {
		player.setPlayerResActivate(false);
		player.getLifeStats().setCurrentHpPercent(hpPercent);
		player.getLifeStats().setCurrentMpPercent(mpPercent);
		if (player.getCommonData().getDp() > 0)
			player.getCommonData().setDp(0);
		player.getLifeStats().triggerRestoreOnRevive();
		if (setSoulsickness) {
			player.getController().updateSoulSickness();
		}
		player.getAggroList().clear();
		player.getController().onBeforeSpawn();
		//unset isflyingbeforedeath
		player.setIsFlyingBeforeDeath(false);
		if(player.isInGroup2()){
			TeamMoveUpdater.getInstance().startTask(player);
		}
	}

	public static final void itemSelfRevive(Player player) {
		Item item = player.getSelfRezStone();
		if (item == null) {
			cancelRes(player);
			return;
		}

		// Add Cooldown and use item
		int useDelay = item.getItemTemplate().getDelayTime();
		player.addItemCoolDown(item.getItemTemplate().getDelayId(), System.currentTimeMillis() + useDelay, useDelay / 1000);
		player.getController().cancelUseItem();
		PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), item.getObjectId(),
			item.getItemTemplate().getTemplateId()), true);
		if (!player.getInventory().decreaseByObjectId(item.getObjectId(), 1)) {
			cancelRes(player);
			return;
		}
		boolean isFlyingBeforeDeath = player.getIsFlyingBeforeDeath();		
		// Tombstone Self-Rez retail verified 15%
		revive(player, 15, 15, true);
		
		if (isFlyingBeforeDeath)
			player.setState(CreatureState.FLYING);

		PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.RESURRECT), true);
		PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_REBIRTH_MASSAGE_ME);
		
		player.getGameStats().updateStatsAndSpeedVisually();
		
		if (player.isInPrison())
			TeleportService.teleportToPrison(player);
		player.unsetResPosState();
		
		//if player was flying before res, start flying
		if (isFlyingBeforeDeath) {
			player.getFlyController().startFly();
		}
	}

	private static final void cancelRes(Player player) {
		AuditLogger.info(player, "[Global|Audit]:Possible selfres hack.");
		player.getController().sendDie();
	}
}
