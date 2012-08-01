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

import gameserver.model.drop.DropItem;
import gameserver.model.gameobjects.Npc;
import gameserver.model.gameobjects.VisibleObject;
import gameserver.model.templates.spawns.SpawnTemplate;
import gameserver.model.templates.spawns.SpawnTime;
import gameserver.services.drop.DropRegistrationService;
import gameserver.services.instance.InstanceService;
import gameserver.spawnengine.SpawnEngine;
import gameserver.utils.ThreadPoolManager;
import gameserver.utils.gametime.DayTime;
import gameserver.utils.gametime.GameTimeManager;
import gameserver.world.World;
import java.util.Set;
import java.util.concurrent.Future;

/**
 * @author ATracer, Source, xTz
 */
public class RespawnService {
	private static final int IMMEDIATE_DECAY = 5 * 1000;
	private static final int WITHOUT_DROP_DECAY = (int)(1.5 * 60 * 1000);
	private static final int WITH_DROP_DECAY = 5 * 60 * 1000;

	/**
	 * @param npc
	 * @return Future<?>
	 */
	public static Future<?> scheduleDecayTask(Npc npc) {
		int decayInterval;
		Set<DropItem> drop = DropRegistrationService.getInstance().geCurrentDropMap().get(npc.getObjectId());

		if(drop == null)
			decayInterval = IMMEDIATE_DECAY;
		else if(drop.isEmpty())
			decayInterval = WITHOUT_DROP_DECAY;
		else
			decayInterval = WITH_DROP_DECAY;

		return scheduleDecayTask(npc, decayInterval);
	}

	public static Future<?> scheduleDecayTask(Npc npc, long decayInterval) {
		return ThreadPoolManager.getInstance().schedule(new DecayTask(npc.getObjectId()), decayInterval);
	}

	/**
	 * @param visibleObject
	 */
	public static final Future<?> scheduleRespawnTask(VisibleObject visibleObject) {
		final int interval = visibleObject.getSpawn().getRespawnTime();
		SpawnTemplate spawnTemplate = visibleObject.getSpawn();
		int instanceId = visibleObject.getInstanceId();
		return ThreadPoolManager.getInstance().schedule(new RespawnTask(spawnTemplate, instanceId), interval * 1000);
	}

	/**
	 * @param spawnTemplate
	 * @param instanceId
	 */
	private static final VisibleObject respawn(SpawnTemplate spawnTemplate, final int instanceId) {
		SpawnTime spawnTime = spawnTemplate.getSpawnTime();
		DayTime dayTime = GameTimeManager.getGameTime().getDayTime();
		if (!spawnTime.isAllowedDuring(dayTime))
			return null;

		int worldId = spawnTemplate.getWorldId();
		boolean instanceExists = InstanceService.isInstanceExist(worldId, instanceId);
		if (spawnTemplate.isNoRespawn() || !instanceExists) {
			return null;
		}

		if (spawnTemplate.hasPool()) {
			spawnTemplate = spawnTemplate.changeTemplate();
		}
		return SpawnEngine.spawnObject(spawnTemplate, instanceId);
	}

	private static class DecayTask implements Runnable {

		private final int npcId;

		DecayTask(int npcId) {
			this.npcId = npcId;
		}

		@Override
		public void run() {
			VisibleObject visibleObject = World.getInstance().findVisibleObject(npcId);
			if (visibleObject != null) {
				visibleObject.getController().onDelete();
			}
		}

	}

	private static class RespawnTask implements Runnable {

		private final SpawnTemplate spawn;
		private final int instanceId;

		RespawnTask(SpawnTemplate spawn, int instanceId) {
			this.spawn = spawn;
			this.instanceId = instanceId;
		}

		@Override
		public void run() {
			respawn(spawn, instanceId);
		}

	}

}
