/*
 * This file is part of the requirements for the Illusion Gate Skill.
 * Code References from ATracer's SummonTrapEffect.java of Aion-Unique
 */
package gameserver.skillengine.effect;

import java.util.concurrent.Future;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import gameserver.model.TaskId;
import gameserver.model.gameobjects.Creature;
import gameserver.model.gameobjects.GroupGate;
import gameserver.model.templates.spawns.SpawnTemplate;
import gameserver.skillengine.model.Effect;
import gameserver.spawnengine.SpawnEngine;
import gameserver.spawnengine.VisibleObjectSpawner;
import gameserver.utils.ThreadPoolManager;

/**
 * @author LokiReborn
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SummonGroupGateEffect")
public class SummonGroupGateEffect extends SummonEffect {

	@Override
	public void applyEffect(Effect effect) {

		Creature effector = effect.getEffector();
		float x = effector.getX();
		float y = effector.getY();
		float z = effector.getZ();
		byte heading = effector.getHeading();
		int worldId = effector.getWorldId();
		int instanceId = effector.getInstanceId();

		SpawnTemplate spawn = SpawnEngine.addNewSingleTimeSpawn(worldId, npcId, x, y, z, heading);
		final GroupGate groupgate = VisibleObjectSpawner.spawnGroupGate(spawn, instanceId, effector);

		Future<?> task = ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {

				groupgate.getController().onDelete();
			}
		}, time * 1000);
		groupgate.getController().addTask(TaskId.DESPAWN, task);
	}
}
