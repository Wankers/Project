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
package gameserver.geoEngine.scene.mesh;

import java.util.BitSet;

import gameserver.geoEngine.collision.Collidable;
import gameserver.geoEngine.collision.CollisionResults;
import gameserver.geoEngine.scene.Geometry;


/**
 * @author MrPoke
 *
 */
public class DoorGeometry extends Geometry {
	BitSet instances = new BitSet();
	
	
	public void setDoorState(int instanceId, boolean state){
		instances.set(instanceId, state);
	}

	@Override
	public int collideWith(Collidable other, CollisionResults results, int instanceId) {
		if (!instances.get(instanceId))
			return 0;
		return super.collideWith(other, results, instanceId);
	}
}
