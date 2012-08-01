/*
 * This file is part of Aion Extreme Emulator  <aion-core.net>
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
package instance.pvparenas;

import commons.network.util.ThreadPoolManager;
import gameserver.controllers.SummonController.UnsummonType;
import gameserver.controllers.attack.AggroInfo;
import gameserver.dataholders.DataManager;
import gameserver.instance.handlers.GeneralInstanceHandler;
import gameserver.model.DescriptionId;
import gameserver.model.EmotionType;
import gameserver.model.gameobjects.Creature;
import gameserver.model.gameobjects.Npc;
import gameserver.model.gameobjects.StaticDoor;
import gameserver.model.gameobjects.Summon;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.instance.InstanceScoreType;
import gameserver.model.instance.instancereward.InstanceReward;
import gameserver.model.instance.instancereward.PvPArenaReward;
import gameserver.model.instance.playerreward.InstancePlayerReward;
import gameserver.model.instance.playerreward.PvPArenaPlayerReward;
import gameserver.model.templates.portal.EntryPoint;
import gameserver.model.templates.portal.PortalTemplate;
import gameserver.model.templates.spawns.SpawnTemplate;
import gameserver.network.aion.AionServerPacket;
import gameserver.network.aion.serverpackets.SM_DIE;
import gameserver.network.aion.serverpackets.SM_EMOTION;
import gameserver.network.aion.serverpackets.SM_INSTANCE_SCORE;
import gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import gameserver.services.abyss.AbyssPointsService;
import gameserver.services.item.ItemService;
import gameserver.services.player.PlayerReviveService;
import gameserver.services.teleport.TeleportService;
import gameserver.utils.PacketSendUtility;
import gameserver.world.WorldMapInstance;
import gameserver.world.knownlist.Visitor;

/**
 *
 * @author xTz
 */
public class PvPArenaInstance extends GeneralInstanceHandler {

	private boolean isInstanceDestroyed;
	private long instanceTime;
	private long instanceStartTime = 0;
	protected PvPArenaReward instanceReward;
	private boolean isInstanceStarted = false;
	protected int killBonus;
	protected int deathFine;

	@Override
	public boolean onDie(Player player, Creature lastAttacker) {
		Summon summon = player.getSummon();
		if (summon != null) {
			summon.getController().release(UnsummonType.UNSPECIFIED);
		}

		PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.DIE, 0, lastAttacker == null ? 0
				: lastAttacker.getObjectId()), true);

		PacketSendUtility.sendPacket(player, new SM_DIE(false, false, 0, 8));

		if (lastAttacker != null) {
			if (lastAttacker instanceof Player) {
				Player winner = (Player) lastAttacker;
				PvPArenaPlayerReward reward = getPlayerReward(winner);
				reward.addPvPKillToPlayer();
			}
		}

		updatePoints(player);

		return true;
	}

	private void updatePoints(Creature victim) {

		if (!instanceReward.isStartProgress()) {
			return;
		}

		int bonus = 0;
		int rank = 0;
		int nameId = victim.getObjectTemplate().getNameId();
		DescriptionId name = new DescriptionId(nameId * 2 + 1);

		// Decrease victim points
		if (victim instanceof Player) {
			PvPArenaPlayerReward victimFine = getPlayerReward((Player) victim);
			victimFine.addPoints(deathFine);
			bonus = killBonus;
			rank = instanceReward.getRank(victimFine.getPoints());
		}
		else
			bonus = getNpcBonus(((Npc) victim).getNpcId());

		if (bonus == 0) {
			return;
		}

		// Reward all damagers
		for (AggroInfo damager : victim.getAggroList().getList()) {
			if (!(damager.getAttacker() instanceof Creature)) {
				continue;
			}
			Creature master = ((Creature) damager.getAttacker()).getMaster();
			if (master == null) {
				continue;
			}
			if (master instanceof Player) {
				Player attaker = (Player) master;
				int rewardPoints = (instanceReward.getRound() == 3 && rank == 1 ? bonus * 3 : bonus)
						* damager.getDamage() / victim.getAggroList().getTotalDamage();
				getPlayerReward(attaker).addPoints(rewardPoints);
				PacketSendUtility.sendPacket(attaker, new SM_SYSTEM_MESSAGE(1400237, nameId == 0
						? victim.getName() : name, rewardPoints));
			}
		}
		if (instanceReward.hasCapPoints()) {
			instanceReward.setInstanceScoreType(InstanceScoreType.END_PROGRESS);
			reward();
		}
		sendPacket();
	}

	public long getStartTime() {
		return instanceStartTime != 0 ? instanceStartTime : 0;
	}

	@Override
	public void onDie(Npc npc) {
		if (npc.getAggroList().getMostPlayerDamage() == null) {
			return;
		}
		updatePoints(npc);
	}

	@Override
	public void onEnterInstance(Player player) {
		if (!isInstanceStarted) {
			isInstanceStarted = true;
			instanceTime = System.currentTimeMillis();
			ThreadPoolManager.getInstance().schedule(new Runnable() {

				@Override
				public void run() {
					if (!isInstanceDestroyed && !instanceReward.isRewarded()) {
						sendPacket(new SM_SYSTEM_MESSAGE(1401058));
					}
				}

			}, 111000);
			ThreadPoolManager.getInstance().schedule(new Runnable() {

				@Override
				public void run() {
					// start round 1
					if (!isInstanceDestroyed && !instanceReward.isRewarded()) {
						openDoors();
						sendPacket(new SM_SYSTEM_MESSAGE(1401058));
						instanceReward.setInstanceScoreType(InstanceScoreType.START_PROGRESS);
						instanceStartTime = System.currentTimeMillis();
						sendPacket();
					}
				}

			}, 120000);
			ThreadPoolManager.getInstance().schedule(new Runnable() {

				@Override
				public void run() {
					// start round 2
					if (!isInstanceDestroyed && !instanceReward.isRewarded()) {
						instanceReward.setRound(2);
						instanceReward.setRndZone();
						sendPacket();
						changeZone();
					}
				}

			}, 300000);
			ThreadPoolManager.getInstance().schedule(new Runnable() {

				@Override
				public void run() {
					// start round 3
					if (!isInstanceDestroyed && !instanceReward.isRewarded()) {
						instanceReward.setRound(3);
						instanceReward.setRndZone();
						sendPacket(new SM_SYSTEM_MESSAGE(1401203));
						sendPacket();
						changeZone();
					}
				}

			}, 480000);
			ThreadPoolManager.getInstance().schedule(new Runnable() {

				@Override
				public void run() {
					// end
					if (!isInstanceDestroyed && !instanceReward.isRewarded()) {
						instanceReward.setInstanceScoreType(InstanceScoreType.END_PROGRESS);
						// to do reward
						reward();
						sendPacket();
					}
				}

			}, 660000);
		}
		if (!containPlayer(player.getObjectId())) {
			addPlayerToReward(player);
			instanceReward.setRndPosition(player.getObjectId());
		}
		else {
			getPlayerReward(player).setPlayer(player);
		}
		sendPacket();
	}

	private void sendPacket(final AionServerPacket packet) {
		instance.doOnAllPlayers(new Visitor<Player>() {

			@Override
			public void visit(Player player) {
				PacketSendUtility.sendPacket(player, packet);
			}

		});
	}

	private int getTime() {
		long result = System.currentTimeMillis() - instanceTime;
		if (instanceReward.isRewarded()) {
			return 0;
		}
		if (result < 120000) {
			return (int) (120000 - result);
		}
		else {
			return (int) (180000 * instanceReward.getRound() - (result - 120000));
		}
	}

	private int getNpcBonus(int npcId) {
		return instanceReward.getNpcBonus(npcId);
	}

	@Override
	public InstanceReward<?> getInstanceReward() {
		return instanceReward;
	}

	@Override
	public void onPlayerLogOut(Player player) {
		getPlayerReward(player).updateBonusTime(instanceStartTime);
	}

	@Override
	public void onPlayerLogin(Player player) {
		getPlayerReward(player).updateBonusTime(instanceStartTime);
	}

	@Override
	public void onInstanceCreate(WorldMapInstance instance) {
		super.onInstanceCreate(instance);
		instanceReward = new PvPArenaReward(mapId, instanceId);
		instanceReward.setInstanceScoreType(InstanceScoreType.PREPARING);
		spawnRings();
	}

	@Override
	public void onExitInstance(Player player) {
		PortalTemplate portal = DataManager.PORTAL_DATA.getInstancePortalTemplate(mapId, player.getRace());
		EntryPoint entryPoint = TeleportService.getEntryPointByRace(portal, player.getRace());
		TeleportService.teleportTo(player, entryPoint.getMapId(), 1, entryPoint.getX(), entryPoint.getY(), entryPoint.getZ(), 3000, true);
	}

	private void openDoors() {
		for (StaticDoor door : instance.getDoors().values()) {
			if (door != null) {
				door.setOpen(true);
			}
		}
	}

	private void addPlayerToReward(Player player) {
		instanceReward.addPlayerReward(new PvPArenaPlayerReward(player, (mapId == 300430000 || mapId == 300360000) ? 8100 : 12000));
	}

	private boolean containPlayer(Integer object) {
		return instanceReward.containPlayer(object);
	}

	protected PvPArenaPlayerReward getPlayerReward(Player player) {
		Integer object = player.getObjectId();
		if (instanceReward.getPlayerReward(object) == null) {
			addPlayerToReward(player);
		}
		return (PvPArenaPlayerReward) instanceReward.getPlayerReward(object);
	}

	@Override
	public boolean onReviveEvent(Player player) {
		player.setTelEffect(4);
		PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_REBIRTH_MASSAGE_ME);
		PlayerReviveService.revive(player, 100, 100, false);
		player.getGameStats().updateStatsAndSpeedVisually();
		instanceReward.portToPosition(player);
		return true;
	}

	protected void sendPacket() {
		instance.doOnAllPlayers(new Visitor<Player>() {

			@Override
			public void visit(Player player) {
				PacketSendUtility.sendPacket(player, new SM_INSTANCE_SCORE(getTime(), instanceReward));
			}

		});
	}

	@Override
	public void onInstanceDestroy() {
		isInstanceDestroyed = true;
	}

	private void changeZone() {
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				for (Player player : instance.getPlayersInside()) {
					instanceReward.portToPosition(player);
				}
			}

		}, 1000);
	}

	protected void reward() {
		if (instanceReward.canRewarded()) {
			for (InstancePlayerReward playerReward : instanceReward.getPlayersInside()) {
				PvPArenaPlayerReward reward = (PvPArenaPlayerReward) playerReward;
				if (!reward.isRewarded()) {
					reward.setRewarded();
					int points = reward.getPoints();
					int totalPoints = points + reward.getTimeBonus() + instanceReward.getRankBonus(instanceReward.getRank(points));
					int abyssPoints = (int) (0.0125 * totalPoints + 300);
					int crucibleInsignia = (int) (0.006 * totalPoints + 170);
					int courageInsignia = (int) (0.0008 * totalPoints+ 2.5);
					reward.setAbyssPoints(abyssPoints);
					reward.setCrucibleInsignia(crucibleInsignia);
					reward.setCourageInsignia(courageInsignia);
					Player player = reward.getPlayer();
					AbyssPointsService.addAp(player, abyssPoints);
					ItemService.addItem(player, 186000130, crucibleInsignia);
					ItemService.addItem(player, 186000137, courageInsignia);
				}
			}
		}
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				if (!isInstanceDestroyed) {
					for (Player player : instance.getPlayersInside()) {
						onExitInstance(player);
					}
				}
			}

		}, 10000);
	}

	protected void spawnRings() {
	}

	protected Npc getNpc(float x, float y, float z) {
		if (!isInstanceDestroyed) {
			for (Npc npc : instance.getNpcs()) {
				SpawnTemplate st = npc.getSpawn();
				if (st.getX() == x && st.getY() == y && st.getZ() == z) {
					return npc;
				}
			}
		}
		return null;
	}

	@Override
	public void handleUseItemFinish(Player player, int npcId) {
		getPlayerReward(player).addPoints(instanceReward.getNpcBonus(npcId));
		sendPacket();
	}
}