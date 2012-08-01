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
package gameserver.skillengine.properties;

import java.util.SortedMap;
import java.util.TreeMap;

import gameserver.model.gameobjects.Creature;
import gameserver.skillengine.model.Skill;
import gameserver.utils.MathUtil;

/**
 * @author MrPoke
 */
public class MaxCountProperty {

	public static final boolean set(final Skill skill, Properties properties) {
		TargetRangeAttribute value = properties.getTargetType();
		int maxcount = properties.getTargetMaxCount();
		
		switch (value){
			case AREA:
				int areaCounter = 0;
				final Creature firstTarget = skill.getFirstTarget();
				if (firstTarget == null) {
					return false;
				}
				SortedMap<Double, Creature> sortedMap = new TreeMap<Double, Creature>();
				for (Creature creature : skill.getEffectedList()){
					sortedMap.put(MathUtil.getDistance(firstTarget, creature), creature);
				}
				skill.getEffectedList().clear();
				for (Creature creature : sortedMap.values()) {
					if (areaCounter >= maxcount)
						break;
					skill.getEffectedList().add(creature);
					areaCounter++;
				}
		}
		return true;
	}
}
