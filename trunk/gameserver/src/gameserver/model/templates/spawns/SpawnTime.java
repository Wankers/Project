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
package gameserver.model.templates.spawns;

import gameserver.utils.gametime.DayTime;

/**
 * @author ATracer
 */
public enum SpawnTime {
	ALL,
	DAY,
	NIGHT;

	public boolean isAllowedDuring(DayTime dayTime) {
		switch (this) {
			case ALL:
				return true;
			case DAY:
				return dayTime == DayTime.AFTERNOON || dayTime == DayTime.MORNING || dayTime == DayTime.EVENING;
			case NIGHT:
				return dayTime == DayTime.NIGHT;
		}
		return true;
	}

	public boolean isNeedUpdate(DayTime dayTime) {
		switch (this) {
			case ALL:
				return false;
			case DAY:
				return dayTime == DayTime.MORNING;
			case NIGHT:
				return dayTime == DayTime.NIGHT;
		}
		return true;
	}
}
