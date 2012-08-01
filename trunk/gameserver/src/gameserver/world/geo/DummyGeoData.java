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

import org.apache.commons.lang.StringUtils;

import gameserver.geoEngine.models.GeoMap;

/**
 * @author ATracer
 */
public class DummyGeoData implements GeoData {

	public static final DummyGeoMap DUMMY_MAP = new DummyGeoMap(StringUtils.EMPTY, 0);

	@Override
	public void loadGeoMaps() {
	}

	@Override
	public GeoMap getMap(int worldId) {
		return DUMMY_MAP;
	}
}
