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
package instance.crucible;

import java.util.List;

import gameserver.controllers.SummonController.UnsummonType;
import gameserver.instance.handlers.GeneralInstanceHandler;
import gameserver.model.EmotionType;
import gameserver.model.gameobjects.Creature;
import gameserver.model.gameobjects.Npc;
import gameserver.model.gameobjects.Summon;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.instance.StageType;
import gameserver.model.instance.instancereward.InstanceReward;
import gameserver.model.instance.playerreward.CruciblePlayerReward;
import gameserver.network.aion.serverpackets.SM_DIE;
import gameserver.network.aion.serverpackets.SM_EMOTION;
import gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import gameserver.services.player.PlayerReviveService;
import gameserver.services.teleport.TeleportService;
import gameserver.utils.PacketSendUtility;
import gameserver.world.WorldMapInstance;
import gameserver.world.knownlist.Visitor;
import gameserver.world.zone.ZoneName;

/**
 * @author xTz
 */
@SuppressWarnings("rawtypes")
public class CrucibleInstance extends GeneralInstanceHandler {

	protected boolean isInstanceDestroyed = false;
	protected StageType stageType = StageType.DEFAULT;
	protected InstanceReward instanceReward;

	@Override
	public void onEnterInstance(Player player) {
		if (!instanceReward.containPlayer(player.getObjectId())) {
			addPlayerReward(player);
		}
		else {
			getPlayerReward(player.getObjectId()).setPlayer(player);
		}
	}

	@Override
	public void onInstanceCreate(WorldMapInstance instance) {
		super.onInstanceCreate(instance);
		instanceReward = new InstanceReward(mapId, instanceId);
	}

	@SuppressWarnings("unchecked")
	protected void addPlayerReward(Player player) {
		instanceReward.addPlayerReward(new CruciblePlayerReward(player));
	}

	protected CruciblePlayerReward getPlayerReward(Integer object) {
		return (CruciblePlayerReward) instanceReward.getPlayerReward(object);
	}

	protected Npc getNpc(int npcId) {
		if (!isInstanceDestroyed) {
			return instance.getNpc(npcId);
		}
		return null;
	}

	protected List<Npc> getNpcs(int npcId) {
		if (!isInstanceDestroyed) {
			return instance.getNpcs(npcId);
		}
		return null;
	}

	protected boolean isInZone(ZoneName zone, Player player) {
		return player.isInsideZone(zone);
	}

	protected void sendMsg(final int msg, final int Obj, final boolean isShout, final int color) {
		instance.doOnAllPlayers(new Visitor<Player>() {

			@Override
			public void visit(Player player) {
				if (player.isOnline()) {
					PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(isShout, msg, Obj, color));
				}
			}
		});

	}

	protected void sendMsg(int msg, int Obj, int color) {
		sendMsg(msg, Obj, false, color);
	}

	protected void sendMsg(int msg) {
		sendMsg(msg, 0, false, 0);
	}

	@Override
	public boolean onDie(Player player, Creature lastAttacker) {
		Summon summon = player.getSummon();
		if (summon != null) {
			summon.getController().release(UnsummonType.UNSPECIFIED);
		}

		PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.DIE, 0, lastAttacker == null ? 0
			: lastAttacker.getObjectId()), true);

		PacketSendUtility.sendPacket(player, new SM_DIE(false, false, 0, 8));
		return true;
	}

	protected void despawnNpc(Npc npc) {
		if (npc != null) {
			npc.getController().onDelete();
		}
	}

	protected void despawnNpcs(List<Npc> npcs) {
		for (Npc npc : npcs) {
			npc.getController().onDelete();
		}
	}

	protected void teleport(Player player, float x, float y, float z, byte h) {
		if (player != null) {
			TeleportService.teleportTo(player, mapId, instanceId, x, y, z, h, 0, true);
		}
		else {
			for (Player playerInside : instance.getPlayersInside()) {
				if (playerInside.isOnline()) {
					TeleportService.teleportTo(playerInside, mapId, instanceId, x, y, z, h, 0, true);
				}
			}
		}
	}

	protected void teleport(float x, float y, float z, byte h) {
		teleport(null, x, y, z, h);
	}

	@Override
	public StageType getStage() {
		return stageType;
	}

	@Override
	public boolean onReviveEvent(Player player) {
		PlayerReviveService.revive(player, 100, 100, false);
		player.getGameStats().updateStatsAndSpeedVisually();
		return true;
	}
}
