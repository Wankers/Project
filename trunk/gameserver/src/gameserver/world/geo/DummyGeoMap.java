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
package gameserver.world.geo;

import gameserver.geoEngine.math.Vector3f;
import gameserver.geoEngine.models.GeoMap;
import gameserver.geoEngine.scene.Spatial;

/**
 * @author ATracer
 */
public class DummyGeoMap extends GeoMap {

	public DummyGeoMap(String name, int worldSize) {
		super(name, worldSize);
	}

	@Override
	public final float getZ(float x, float y, float z, int instanceId) {
		return z;
	}

	@Override
	public final boolean canSee(float x, float y, float z, float targetX, float targetY, float targetZ, float limit, int instanceId) {
		return true;
	}

	@Override
	public Vector3f getClosestCollision(float x, float y, float z, float targetX, float targetY, float targetZ,
		boolean changeDirction, boolean fly, int instanceId) {
		return new Vector3f(targetX, targetY, targetZ);
	}

	@Override
	public void setDoorState(int instanceId, String name, boolean state){
		
	}
	

	@Override
	public int attachChild(Spatial child) {
		return 0;
	}
}