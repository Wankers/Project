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
package ai;

import commons.network.util.ThreadPoolManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import commons.utils.Rnd;
import gameserver.ai2.AI2Actions;
import gameserver.ai2.AIName;
import gameserver.dataholders.DataManager;
import gameserver.model.ai.Percentage;
import gameserver.model.ai.SummonGroup;
import gameserver.model.gameobjects.Creature;
import gameserver.model.gameobjects.VisibleObject;
import gameserver.model.templates.spawns.SpawnTemplate;
import gameserver.spawnengine.SpawnEngine;
import gameserver.world.World;

/**
 * @author xTz
 */
@AIName("summoner")
public class SummonerAI2 extends AggressiveNpcAI2 {

	private List<Integer> spawnedNpc = new ArrayList<Integer>();

	private List<Percentage> percentage = Collections.emptyList();

	private int spawnedPercent = 0;

	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
		checkPercentage(getLifeStats().getHpPercentage());
	}

	@Override
	protected void handleDespawned() {
		super.handleDespawned();
		removeHelpersSpawn();
		spawnedNpc.clear();
		percentage.clear();
	}

	@Override
	protected void handleBackHome() {
		super.handleBackHome();
		removeHelpersSpawn();
		spawnedNpc.clear();
		spawnedPercent = 0;
	}

	@Override
	protected void handleSpawned() {
		super.handleSpawned();
		percentage = DataManager.AI_DATA.getAiTemplate().get(getNpcId()).getSummons().getPercentage();
	}

	@Override
	protected void handleDied() {
		super.handleDied();
		removeHelpersSpawn();
		spawnedNpc.clear();
		percentage.clear();
	}

	private void removeHelpersSpawn() {
		for (Integer object : spawnedNpc) {
			VisibleObject npc = World.getInstance().findVisibleObject(object);
			if (npc != null && npc.isSpawned()) {
				npc.getController().onDelete();
			}
		}
	}

	private void checkPercentage(int hpPercentage) {
		for (Percentage percent : percentage) {
			if (spawnedPercent != 0 && spawnedPercent <= percent.getPercent()) {
				continue;
			}

			if (hpPercentage <= percent.getPercent()) {
				int skill = percent.getSkillId();
				if (skill != 0) {
					AI2Actions.useSkill(this, skill);
				}

				if (percent.isIndividual()) {
					handleIndividualSpawnedSummons(percent);
				}
				else if (percent.getSummons() != null) {
					handleSpawnedSummons(percent);
					for (SummonGroup summonGroup : percent.getSummons()) {
						int count = 0;
						if (summonGroup.getCount() != 0) {
							count = summonGroup.getCount();
						}
						else {
							count = Rnd.get(summonGroup.getMinCount(), summonGroup.getMaxCount());
						}

						for (int i = 0; i < count; i++) {
							spawnHelper(summonGroup);
						}
					}
				}
				spawnedPercent = percent.getPercent();
			}
		}
	}

	public void spawnHelper(final SummonGroup summonGroup) {

		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				if (!isAlreadyDead()) {
					SpawnTemplate summon = null;
					if (summonGroup.getDistance() != 0) {
						summon = rndSpawnInRange(summonGroup.getNpcId(), summonGroup.getDistance());
					}
					else {
						summon = SpawnEngine.addNewSingleTimeSpawn(getPosition().getMapId(), summonGroup.getNpcId(),
								summonGroup.getX(), summonGroup.getY(), summonGroup.getZ(), summonGroup.getH());
					}
					VisibleObject npc = SpawnEngine.spawnObject(summon, getPosition().getInstanceId());
					spawnedNpc.add(npc.getObjectId());
				}
			}
		}, summonGroup.getSchedule());
	}

	private SpawnTemplate rndSpawnInRange(int npcId, float distance) {
		float direction = Rnd.get(0, 199) / 100f;
		float x = (float) (Math.cos(Math.PI * direction) * distance);
		float y = (float) (Math.sin(Math.PI * direction) * distance);
		return SpawnEngine.addNewSingleTimeSpawn(getPosition().getMapId(), npcId, getPosition().getX() + x, getPosition().getY()
			+ y, getPosition().getZ(), getPosition().getHeading());
	}

	protected void handleSpawnedSummons(Percentage percent) {
	}

	protected void handleIndividualSpawnedSummons(Percentage percent) {
	}

}
