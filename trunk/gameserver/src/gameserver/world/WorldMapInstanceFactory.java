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
package gameserver.world;

import gameserver.instance.InstanceEngine;
import gameserver.instance.handlers.InstanceHandler;

/**
 * @author ATracer
 */
public class WorldMapInstanceFactory {

	/**
	 * @param parent
	 * @param instanceId
	 * @return
	 */
	public static WorldMapInstance createWorldMapInstance(WorldMap parent, int instanceId) {
		WorldMapInstance worldMapInstance = null;
		if (parent.getMapId() == WorldMapType.RESHANTA.getId()) {
			worldMapInstance = new WorldMap3DInstance(parent, instanceId);
		}
		else {
			worldMapInstance = new WorldMap2DInstance(parent, instanceId);
		}
		InstanceHandler instanceHandler = InstanceEngine.getInstance().getNewInstanceHandler(parent.getMapId());
		worldMapInstance.setInstanceHandler(instanceHandler);
		return worldMapInstance;
	}
}
