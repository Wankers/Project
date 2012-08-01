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
package gameserver.network.aion.serverpackets;

import java.util.Collection;

import javolution.util.FastList;

import gameserver.model.siege.ArtifactLocation;
import gameserver.model.siege.SiegeLocation;
import gameserver.network.aion.AionConnection;
import gameserver.network.aion.AionServerPacket;

public class SM_ABYSS_ARTIFACT_INFO3 extends AionServerPacket {

	private Collection<SiegeLocation> locations;
	private int loc;
	private int state;

	public SM_ABYSS_ARTIFACT_INFO3(Collection<SiegeLocation> collection) {
		this.locations = collection;
	}

	public SM_ABYSS_ARTIFACT_INFO3(int loc, int state) {
		this.loc = loc;
		this.state = state;
	}

	@Override
	protected void writeImpl(AionConnection con) {
		if (locations != null) {
			FastList<ArtifactLocation> validLocations = new FastList<ArtifactLocation>();
			for (SiegeLocation loc : locations) {
				if ((loc.getLocationId() >= 1011) && (loc.getLocationId() < 2000)) {
					if (loc instanceof ArtifactLocation)
						validLocations.add((ArtifactLocation) loc);
				}
			}
			writeH(validLocations.size());
			for (ArtifactLocation loc : validLocations) {
				writeD(loc.getLocationId() * 10 + 1);
				writeC(0);
				writeD(0);
			}
		}
		else {
			writeH(1);
			writeD(loc * 10 + 1);
			writeC(state);
			writeD(0);
		}
	}
}
