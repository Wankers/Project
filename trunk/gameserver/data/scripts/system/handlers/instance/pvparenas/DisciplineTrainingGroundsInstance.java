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

import gameserver.instance.handlers.InstanceID;
import gameserver.world.WorldMapInstance;

/**
 * @author xTz
 */
@InstanceID(300430000)
public class DisciplineTrainingGroundsInstance extends PvPArenaInstance {

	@Override
	public void onInstanceCreate(WorldMapInstance instance) {
		killBonus = 200;
		deathFine = -100;
		super.onInstanceCreate(instance);
	}

}