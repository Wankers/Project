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
package gameserver.skillengine.effect;

import java.util.concurrent.Future;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import gameserver.ai2.event.AIEventType;
import gameserver.controllers.observer.ActionObserver;
import gameserver.controllers.observer.ObserverType;
import gameserver.model.TaskId;
import gameserver.model.gameobjects.Creature;
import gameserver.model.gameobjects.Homing;
import gameserver.model.templates.spawns.SpawnTemplate;
import gameserver.skillengine.model.Effect;
import gameserver.spawnengine.SpawnEngine;
import gameserver.spawnengine.VisibleObjectSpawner;
import gameserver.utils.ThreadPoolManager;

/**
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SummonHomingEffect")
public class SummonHomingEffect extends SummonEffect {

	@XmlAttribute(name = "npc_count", required = true)
	protected int npcCount;
	@XmlAttribute(name = "attack_count", required = true)
	protected int attackCount;
	
	//TODO homing that uses skills (skillId:629)

	@Override
	public void applyEffect(Effect effect) {
		Creature effector = effect.getEffector();
		float x = effector.getX();
		float y = effector.getY();
		float z = effector.getZ();
		byte heading = effector.getHeading();
		int worldId = effector.getWorldId();
		int instanceId = effector.getInstanceId();

		for (int i = 0; i < npcCount; i++) {
			SpawnTemplate spawn = SpawnEngine.addNewSingleTimeSpawn(worldId, npcId, x, y, z, heading);
			final Homing homing = VisibleObjectSpawner.spawnHoming(spawn, instanceId, effector, attackCount,
				effect.getSkillId(), effect.getSkillLevel());

			if (attackCount > 0) {
				ActionObserver observer = new ActionObserver(ObserverType.ATTACK) {

					@Override
					public void attack(Creature creature) {
						homing.setAttackCount(homing.getAttackCount() - 1);
						if (homing.getAttackCount() <= 0)
							homing.getController().onDelete();
					}
				};

				homing.getObserveController().addObserver(observer);
				effect.setActionObserver(observer, position);
			}
			// Schedule a despawn just in case
			Future<?> task = ThreadPoolManager.getInstance().schedule(new Runnable() {

				@Override
				public void run() {
					if ((homing != null) && (homing.isSpawned()))
						homing.getController().onDelete();
				}
			}, 15 * 1000);
			homing.getController().addTask(TaskId.DESPAWN, task);
			homing.getAi2().onCreatureEvent(AIEventType.ATTACK, effect.getEffected());
		}
	}

}