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
package gameserver.questEngine.handlers.models;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import javolution.util.FastMap;

import gameserver.questEngine.QuestEngine;
import gameserver.questEngine.handlers.template.KillSpawned;

/**
 * @author vlog
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "KillSpawnedData")
public class KillSpawnedData extends MonsterHuntData {

	@XmlElement(name = "spawned_monster", required = true)
	protected List<SpawnedMonster> spawnedMonster;

	@Override
	public void register(QuestEngine questEngine) {
		FastMap<Integer, SpawnedMonster> spawnedMonsters = new FastMap<Integer, SpawnedMonster>();
		for (SpawnedMonster m : spawnedMonster) {
			spawnedMonsters.put(m.getNpcId(), m);
		}
		KillSpawned template = new KillSpawned(id, startNpcId, startNpcId2, endNpcId, endNpcId2, spawnedMonsters);
		questEngine.addQuestHandler(template);
	}
}