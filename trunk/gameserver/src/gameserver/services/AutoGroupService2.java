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
package gameserver.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import commons.network.util.ThreadPoolManager;
import gameserver.configs.main.LoggingConfig;
import gameserver.dataholders.DataManager;
import gameserver.instance.InstanceEngine;
import gameserver.model.Race;
import gameserver.model.autogroup.AutoGroupsType;
import gameserver.model.autogroup.AutoInstance;
import gameserver.model.autogroup.EntryRequestType;
import gameserver.model.autogroup.LookingForParty;
import gameserver.model.autogroup.SearchInstance;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.instance.instancereward.DredgionReward;
import gameserver.model.instance.instancereward.PvPArenaReward;
import gameserver.model.team2.group.PlayerGroup;
import gameserver.model.team2.group.PlayerGroupService;
import gameserver.model.templates.portal.ExitPoint;
import gameserver.model.templates.portal.PortalTemplate;
import gameserver.network.aion.serverpackets.SM_AUTO_GROUP;
import gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import gameserver.services.instance.DredgionService2;
import gameserver.services.instance.InstanceService;
import gameserver.services.teleport.TeleportService;
import gameserver.spawnengine.SpawnEngine;
import gameserver.utils.PacketSendUtility;
import gameserver.world.World;
import gameserver.world.WorldMap;
import gameserver.world.WorldMapInstance;
import gameserver.world.WorldMapInstanceFactory;
import javolution.util.FastMap;

/**
 *
 * @author xTz
 */
public class AutoGroupService2 {

	private static final Logger log = LoggerFactory.getLogger(AutoGroupService2.class);
	private FastMap<Integer, LookingForParty> playersSearcher = new FastMap<Integer, LookingForParty>().shared();
	private FastMap<Integer, AutoInstance> playersInInstances = new FastMap<Integer, AutoInstance>().shared();

	public void startLooking(Player player, byte instanceMaskId, EntryRequestType ert) {
		AutoGroupsType agt = AutoGroupsType.getAutoGroupByInstanceMaskId(instanceMaskId);
		if (agt == null) {
			return;
		}
		if (!canEnter(player, ert, agt)) {
			return;
		}
		LookingForParty lfp = getLookingForParty(player.getObjectId());
		if (lfp == null) {
			playersSearcher.put(player.getObjectId(), new LookingForParty(player, instanceMaskId, ert));
		}
		else if (!lfp.canRegister() || lfp.getInstanceMaskIds().contains(instanceMaskId)) {
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1400181, agt.getInstanceMapId()));
			return;
		}
		else {
			lfp.addInstanceMaskId(instanceMaskId, ert);
		}
		if (ert.isGroupEntry()) {
			for (Player member : player.getPlayerGroup2().getOnlineMembers()) {
				if (agt.isDredgion()) {
					PacketSendUtility.sendPacket(member, new SM_AUTO_GROUP(instanceMaskId, 6, true));
				}
				PacketSendUtility.sendPacket(member, new SM_SYSTEM_MESSAGE(1400194, agt.getInstanceMapId()));
				PacketSendUtility.sendPacket(member, new SM_AUTO_GROUP(instanceMaskId, 1, ert.getId(), player.getName()));
			}
		}
		else {
			if (agt.isDredgion()) {
				PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(instanceMaskId, 6, true));
			}
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1400194, agt.getInstanceMapId()));
			PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(instanceMaskId, 1, ert.getId(), player.getName()));
		}
		if (LoggingConfig.LOG_AUTOGROUP) {	
			log.info("[AutoGroupService] > Register playerName: " + player.getName() + " class: " + player.getPlayerClass() + " race: " + player.getRace());
			log.info("[AutoGroupService] > Register instanceMaskId: " + instanceMaskId + " type: " + ert);
		}
		startSort(ert, instanceMaskId, player.getRace());
	}

	public void unregisterLooking(Player player, byte instanceMaskId) {
		if (LoggingConfig.LOG_AUTOGROUP)	
			log.info("[AutoGroupService] > unregisterLooking instanceMaskId: " + instanceMaskId + " player: " + player.getName());
		AutoGroupsType agt = AutoGroupsType.getAutoGroupByInstanceMaskId(instanceMaskId);
		LookingForParty lfp = getLookingForParty(player.getObjectId());
		if (agt == null || lfp == null) {
			return;
		}
		SearchInstance searchInstance = lfp.getSearchInstance(instanceMaskId);
		if (searchInstance == null) {
			return;
		}
		if (searchInstance.getEntryRequestType().isGroupEntry() && player.isInGroup2()) {
			for (Player member : player.getPlayerGroup2().getOnlineMembers()) {
				if (agt.isDredgion() && DredgionService2.getInstance().isDredgionAvialable()) {
					PacketSendUtility.sendPacket(member, new SM_AUTO_GROUP(instanceMaskId, 6));
				}
				PacketSendUtility.sendPacket(member, new SM_AUTO_GROUP(instanceMaskId, 2));
			}
		}
		else {
			if (agt.isDredgion() && DredgionService2.getInstance().isDredgionAvialable()) {
				PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(instanceMaskId, 6));
			}
			PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(instanceMaskId, 2));
		}
		startRejectTask(player.getObjectId(), instanceMaskId);
	}

	public void cancelEnter(Player player, byte instanceMaskId) {
		if (LoggingConfig.LOG_AUTOGROUP)	
			log.info("[AutoGroupService] > cancelEnter requestEntryId: " + instanceMaskId + " player: " + player.getName());
		AutoGroupsType agt = AutoGroupsType.getAutoGroupByInstanceMaskId(instanceMaskId);
		LookingForParty lfp = getLookingForParty(player.getObjectId());
		AutoInstance autoInstance = getAutoInstance(player, instanceMaskId);
		if (agt == null || autoInstance == null) {
			return;
		}

		if (lfp != null) {
			SearchInstance searchInstance = lfp.getSearchInstance(instanceMaskId);
			if (searchInstance == null) {
				return;
			}
			if (agt.isDredgion() && DredgionService2.getInstance().isDredgionAvialable()) {
				PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(instanceMaskId, 6));
			}
			if (searchInstance.getEntryRequestType().isGroupEntry()) {
				for (Player member : autoInstance.getPlayers()) {
					if (!member.equals(player) && member.getRace().equals(player.getRace())) {
						autoInstance.unregisterPlayer(player);
						if (agt.isDredgion() && DredgionService2.getInstance().isDredgionAvialable()) {
							PacketSendUtility.sendPacket(member, new SM_AUTO_GROUP(instanceMaskId, 6));
						}
						PacketSendUtility.sendPacket(member, new SM_AUTO_GROUP(instanceMaskId, 2));
					}
				}
			}
			startRejectTask(player.getObjectId(), instanceMaskId);
		}
		else {
			autoInstance.unregisterPlayer(player);
		}
	}

	public void onPlayerLogin(Player player) {
		if (DredgionService2.getInstance().isDredgionAvialable() && player.getLevel() > 45 && !DredgionService2.getInstance().hasCoolDown(player)) {
			PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(DredgionService2.getInstance().getInstanceMaskId(player), 6));
		}
		LookingForParty lfp = getLookingForParty(player.getObjectId());
		if (lfp != null) {
			lfp.setPlayer(player);
			if (!DredgionService2.getInstance().isDredgionAvialable()) {
				lfp.unregisterInstance((byte) 1);
				lfp.unregisterInstance((byte) 2);
				if (lfp.getSearchInstances().isEmpty()) {
					playersSearcher.remove(player.getObjectId());
					return;
				}
			}
			for (SearchInstance searchInstance : lfp.getSearchInstances()) {
				PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(searchInstance.getInstanceMaskId(), 8,
						searchInstance.getRemainingTime() + searchInstance.getEntryRequestType().getId(), player.getName()));
				if (searchInstance.isDredgion()) {
					PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(searchInstance.getInstanceMaskId(), 6, true));
				}
				startSort(searchInstance.getEntryRequestType(), searchInstance.getInstanceMaskId(), player.getRace());
			}
		}
		if (player.isInGroup2()) {
			Player leader = player.getPlayerGroup2().getLeaderObject();
			LookingForParty groupLfp = playersSearcher.get(leader.getObjectId());
			if (groupLfp == null) {
				return;
			}
			for (SearchInstance searchInstance : groupLfp.getSearchInstances()) {
				if (!searchInstance.getEntryRequestType().isGroupEntry()) {
					continue;
				}
				PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(searchInstance.getInstanceMaskId(), 8,
						searchInstance.getRemainingTime() + searchInstance.getEntryRequestType().getId(), leader.getName()));
			}
		}
	}

	public void onPlayerLogOut(Player player) {
		LookingForParty lfp = getLookingForParty(player.getObjectId());
		if (lfp != null) {
			lfp.setPlayer(null);
		}
		for (AutoInstance autoInstance : playersInInstances.values()) {
			if (autoInstance.containPlayer(player) && autoInstance.getInstanceId() == player.getInstanceId()) {
				if (autoInstance.getWorldMapInstance().getPlayersInside().size() < 2) {
					playersInInstances.remove(autoInstance.getInstanceId());
					InstanceService.destroyInstance(autoInstance.getWorldMapInstance());
				}
			}
		}
	}

	public void onLeaveInstance(Player player) {
		if (player.isInInstance()) {
			AutoInstance autoInstance = playersInInstances.get(player.getInstanceId());
			if (autoInstance == null) {
				return;
			}
			autoInstance.unregisterPlayer(player);
			destroyAutoInstance(autoInstance);
			PlayerGroupService.removePlayer(player);
			startSort(EntryRequestType.QUICK_GROUP_ENTRY, autoInstance.getInstanceMaskId(), player.getRace());
		}
	}

	private void destroyAutoInstance(AutoInstance autoInstance ) {
		if (autoInstance.getPlayers().isEmpty()) {
			playersInInstances.remove(autoInstance.getInstanceId());
			InstanceService.destroyInstance(autoInstance.getWorldMapInstance());
		}
	}

	private AutoInstance getAutoInstance(Player player, byte instanceMaskId) {
		for (AutoInstance autoInstance : playersInInstances.values()) {
			if (autoInstance.hasInstanceMask(instanceMaskId) && autoInstance.containPlayer(player)) {
				return autoInstance;
			}
		}
		return null;
	}

	public void enterToInstance(Player player, byte instanceMaskId) {
		if (player.isAttackMode()) {
			// to do msg
			return;
		}
		AutoGroupsType agt = AutoGroupsType.getAutoGroupByInstanceMaskId(instanceMaskId);
		if (agt == null) {
			return;
		}
		AutoInstance autoInstance = getAutoInstance(player, instanceMaskId);
		if (autoInstance == null) {
			return;
		}
		LookingForParty lfp = getLookingForParty(player.getObjectId());
		if (lfp != null) {
			if (!lfp.isRegistredInstance(instanceMaskId) || !lfp.isInvited(instanceMaskId) || !lfp.isOnStartEnterTask()) {
				return;
			}
			SearchInstance searchInstance = lfp.getSearchInstance(instanceMaskId);
			if (searchInstance.getEntryRequestType().isGroupEntry()) {
				if (!player.isInGroup2()) {
					playersSearcher.remove(player.getObjectId());
					autoInstance.unregisterPlayer(player);
					return;
				}
				if (!autoInstance.getPlayersInside().contains(player)) {
					sendEnter(player.getPlayerGroup2(), instanceMaskId, autoInstance);
				}
			}
		}
		if (autoInstance.getPlayersInside().contains(player)) {
			log.warn("[AutoGroupService] > is inside player: " + player.getName() +  " instanceMaskId " + instanceMaskId);
			return;
		}
		int worldId = agt.getInstanceMapId();
		int instanceId = autoInstance.getInstanceId();
		if (agt.isDredgion()) {
			DredgionService2.getInstance().addCoolDown(player);
			((DredgionReward) autoInstance.getWorldMapInstance().getInstanceHandler().getInstanceReward()).portToPosition(player);
			autoInstance.getWorldMapInstance().register(player.getObjectId());
		}
		else if (agt.isPvpArena()) {
			if (agt.isPvPFFAArena() || agt.isPvPSoloArena()) {
				if (!player.getInventory().decreaseByItemId(186000135, 1)) {
					return;
				}
			}
			((PvPArenaReward) autoInstance.getWorldMapInstance().getInstanceHandler().getInstanceReward()).portToPosition(player);
			//to do add cool down
		}		
		else {
			PortalTemplate portal = DataManager.PORTAL_DATA.getInstancePortalTemplate(worldId, player.getRace());
			ExitPoint exit = TeleportService.getExitPointByRace(portal, player.getRace());
			if (exit == null) {
				return;
			}
			TeleportService.teleportTo(player, worldId, instanceId, exit.getX(), exit.getY(), exit.getZ(), (byte) 0, 3000, true);
			int instanceCooldownRate = InstanceService.getInstanceRate(player, worldId);
			int instanceCoolTime = DataManager.INSTANCE_COOLTIME_DATA.getInstanceEntranceCooltime(worldId);
			player.getPortalCooldownList().addPortalCooldown(worldId, instanceCoolTime * 60 * 1000 / instanceCooldownRate);		
		}

		if (player.isInGroup2()) {
			PlayerGroupService.removePlayer(player);
		}
		if (!agt.isPvpArena()) {
			autoInstance.enterToGroup(player);
		}
		if (lfp != null && lfp.unregisterInstance(instanceMaskId) == 0) {
			playersSearcher.remove(player.getObjectId());
		}
		PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(instanceMaskId, 5));
	}

	private void startRejectTask(final Integer object, final byte instanceMaskId) {
		final LookingForParty lfp = getLookingForParty(object);
		if (lfp == null) {
			return;
		}
		lfp.setRejecRegistration(false);
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				lfp.setRejecRegistration(true);
				removeLooking(object, instanceMaskId);
			}
		}, 10000);
	}

	private void removeLooking(Integer object, byte instanceMaskId) {
		LookingForParty lfp = getLookingForParty(object);
		if (lfp == null) {
			return;
		}
		Player player = lfp.getPlayer();
		if (lfp.getPlayer() != null) {
			unregisterPlayerFromAutoInstance(player, instanceMaskId);
		}

		if (lfp.isRegistredInstance(instanceMaskId) && lfp.unregisterInstance(instanceMaskId) == 0) {
			playersSearcher.remove(object);
		}
		startSort(EntryRequestType.QUICK_GROUP_ENTRY, instanceMaskId, null);
	}

	private void unregisterPlayerFromAutoInstance(Player player, byte instanceMaskId) {
		AutoInstance autoInstance = getAutoInstance(player, instanceMaskId);
		if (autoInstance == null) {
			return;
		}
		autoInstance.unregisterPlayer(player);
		destroyAutoInstance(autoInstance);
	}

	private synchronized void startSort(EntryRequestType ert, byte instanceMaskId, Race race) {
		AutoGroupsType agt = AutoGroupsType.getAutoGroupByInstanceMaskId(instanceMaskId);
		AutoInstance autoInstance;
		switch (ert) {
			case NEW_GROUP_ENTRY:
			case QUICK_GROUP_ENTRY:
				if (ert.isQuickGroupEntry()) {
					for (LookingForParty lfp : playersSearcher.values()) {
						Player player = lfp.getPlayer();
						if (player == null || lfp.isOnStartEnterTask()) {
							continue;
						}
						lab:
						for (SearchInstance searchInstance : lfp.getSearchInstances()) {
							if (!searchInstance.getEntryRequestType().isQuickGroupEntry()) {
								continue;
							}
							for (AutoInstance instance : playersInInstances.values()) {
								if (searchInstance.getInstanceMaskId() != instance.getInstanceMaskId() ||
									!instance.hasRacePermit(player.getRace()) || !instance.satisfyTime()) {
									continue;
								}
								if (instance.canAddPlayer(player)) {
									if (LoggingConfig.LOG_AUTOGROUP)
										log.info("[AutoGroupService] > sort QUICK_GROUP_ENTRY player: " + player.getName());
									lfp.setInvited(instance.getInstanceMaskId(), true);
									lfp.setStartEnterTime();
									sendEnter(player, instance.getInstanceMaskId());
									break lab;
								}
							}
						}
					}
				}
				if (race == null) {
					return;
				}
				autoInstance = new AutoInstance((agt.isDredgion() || agt.isPvpArena()) ? Race.PC_ALL : race, instanceMaskId, null);
				for (LookingForParty lfp : playersSearcher.values()) {
					Player player = lfp.getPlayer();
					SearchInstance searchInstance = lfp.getSearchInstance(instanceMaskId);
					if (searchInstance == null || searchInstance.getEntryRequestType().isGroupEntry()) {
						continue;
					}
					if (player != null && !lfp.isInvited(instanceMaskId) && autoInstance.hasRacePermit(player.getRace()) &&
							!lfp.isOnStartEnterTask()) {
						if (autoInstance.canAddPlayer(player) && autoInstance.hasSizePermit()) {
							break;
						}
					}
				}
				if (autoInstance.hasSizePermit()) {
					WorldMapInstance instance = createInstance(agt.getInstanceMapId());
					autoInstance.setWorldMapInstance(instance);
					playersInInstances.put(instance.getInstanceId(), autoInstance);
					for (Player player : autoInstance.getPlayers()) {
						if (LoggingConfig.LOG_AUTOGROUP)
							log.info("[AutoGroupService] > sort NEW_GROUP_ENTRY player: " + player.getName());
						LookingForParty lfp = getLookingForParty(player.getObjectId());
						if (lfp != null) {
							lfp.setInvited(instanceMaskId, true);
							lfp.setStartEnterTime();
							sendEnter(player, instanceMaskId);
						}
					}
				}
				else {
					autoInstance = null;
				}
				break;
			case GROUP_ENTRY:
				if (agt.isDredgion()) {
					LookingForParty asmodiansLfp = getLookingForParty(Race.ASMODIANS, instanceMaskId, ert);
					LookingForParty elyosLfp = getLookingForParty(Race.ELYOS, instanceMaskId, ert);
					if (asmodiansLfp != null && elyosLfp != null) {
						if (LoggingConfig.LOG_AUTOGROUP)
							log.info("[AutoGroupService] > sort GROUP_ENTRY instance: " + agt);
						Player asmodiansLeader = asmodiansLfp.getPlayer();
						Player elyosLeader = elyosLfp.getPlayer();
						WorldMapInstance instance = createInstance(agt.getInstanceMapId());
						autoInstance = new AutoInstance(Race.PC_ALL, instanceMaskId, instance);
						playersInInstances.put(instance.getInstanceId(), autoInstance);
						asmodiansLfp.setStartEnterTime();
						asmodiansLfp.setInvited(instanceMaskId, true);
						autoInstance.addPlayer(asmodiansLeader);
						sendEnter(asmodiansLfp.getPlayer(), instanceMaskId);
						elyosLfp.setStartEnterTime();
						elyosLfp.setInvited(instanceMaskId, true);
						autoInstance.addPlayer(elyosLeader);
						sendEnter(elyosLfp.getPlayer(), instanceMaskId);
					}
				}
				else {
					// mb in future
				}
				break;
		}
	}

	private LookingForParty getLookingForParty(Race race, byte instanceMaskId, EntryRequestType ert) {
		for (LookingForParty lfp : playersSearcher.values()) {
			Player player = lfp.getPlayer();
			SearchInstance searchInstance = lfp.getSearchInstance(instanceMaskId);
			if (searchInstance == null || searchInstance.getEntryRequestType().getId() != ert.getId()) {
				continue;
			}
			if (player != null && (player.getRace().equals(race) ||
					race.equals(Race.PC_ALL) && !lfp.isInvited(instanceMaskId)) && !lfp.isOnStartEnterTask()) {
				return lfp;
			}
		}
		return null;
	}

	public void unregisterInstance(AutoGroupsType agt) {
		for (LookingForParty lfp : playersSearcher.values()) {
			Player player = lfp.getPlayer();
			byte maskId = agt.getInstanceMaskId();
			if (player != null) {
				PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(maskId, 2));
			}
			if (!lfp.isInvited(maskId) && lfp.unregisterInstance(maskId) == 0) {
				playersSearcher.values().remove(lfp);
			}
		}
	}

	private void sendEnter(PlayerGroup group, byte instanceMaskId, AutoInstance autoInstance) {
		for (Player player : group.getOnlineMembers()) {
			if (!autoInstance.containPlayer(player)) {
				autoInstance.addPlayer(player);
				sendEnter(player, instanceMaskId);
			}
		}
	}

	private void sendEnter(Player player, byte instanceMaskId) {
		if (LoggingConfig.LOG_AUTOGROUP)
			log.info("[AutoGroupService] > sendEnter player: " + player.getName() + " instanceMaskId: " + instanceMaskId);
		PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(instanceMaskId, 4));
	}

	public void sendRequestEntry(Player player, int npcId) {
		AutoGroupsType agt = AutoGroupsType.getAutoGroup(player.getLevel(), npcId);
		if (agt != null) {
			PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(agt.getInstanceMaskId()));
		}
	}

	private LookingForParty getLookingForParty(Integer object) {
		return playersSearcher.get(object);
	}

	private boolean canEnter(Player player, EntryRequestType ert, AutoGroupsType agt) {
		if (!agt.hasLevelPermit(player.getLevel())) {
			return false;
		}

		if (agt.isDredgion()) {
			if (!DredgionService2.getInstance().isDredgionAvialable()) {
				return false;
			}
		}
		else if (agt.isPvPFFAArena() || agt.isPvPSoloArena()) {
			// to do check 186000136
			if (player.getInventory().getFirstItemByItemId(186000135) == null) {
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1400219, agt.getInstanceMapId()));
				// to do check time
				return false;
			}
		}
		else {
			if (hasCoolDown(player, agt.getInstanceMapId())) {
				return false;
			}
		}
		switch (ert) {
			case NEW_GROUP_ENTRY:
				break;
			case QUICK_GROUP_ENTRY:
				if (!agt.hasRegisterFast()) {
					PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1400179, agt.getInstanceMapId()));
					return false;
				}
				break;
			case GROUP_ENTRY:
				if (!agt.hasRegisterGroup()) {
					return false;
				}
				PlayerGroup group = player.getPlayerGroup2();
				if (group == null || !group.isLeader(player)) {
					PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1400182));
					return false;
				}
				if (!group.isFull()) {
					return false;
				}

				for (Player member : group.getMembers()) {
					if (agt.isDredgion()) {
						if (DredgionService2.getInstance().hasCoolDown(member)) {
							return false;
						}
					}
					else {
						if (hasCoolDown(member, agt.getInstanceMapId())) {
							return false;
						}
					}
					if (!agt.hasLevelPermit(member.getLevel())) {
						PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1400179, agt.getInstanceMapId()));
						return false;
					}
				}
				break;
		}
		return true;
	}

	private WorldMapInstance createInstance(int worldId) {
		if (LoggingConfig.LOG_AUTOGROUP)
			log.info("[AutoGroupService] > createInstance: " + worldId);
		WorldMap map = World.getInstance().getWorldMap(worldId);
		int nextInstanceId = map.getNextInstanceId();
		WorldMapInstance worldMapInstance = WorldMapInstanceFactory.createWorldMapInstance(map, nextInstanceId);
		map.addInstance(nextInstanceId, worldMapInstance);
		SpawnEngine.spawnInstance(worldId, worldMapInstance.getInstanceId());
		InstanceEngine.getInstance().onInstanceCreate(worldMapInstance);
		return worldMapInstance;
	}

	private boolean hasCoolDown(Player player, int worldId) {
		int instanceCooldownRate = InstanceService.getInstanceRate(player, worldId);
		int useDelay = 0;
		int instanceCooldown = DataManager.INSTANCE_COOLTIME_DATA.getInstanceEntranceCooltime(worldId);
		if (instanceCooldownRate > 0) {
			useDelay = instanceCooldown / instanceCooldownRate;
		}
		return player.getPortalCooldownList().isPortalUseDisabled(worldId) && useDelay > 0;
	}

	public static AutoGroupService2 getInstance() {
		return SingletonHolder.instance;
	}

	private static class SingletonHolder {

		protected static final AutoGroupService2 instance = new AutoGroupService2();
	}
}
