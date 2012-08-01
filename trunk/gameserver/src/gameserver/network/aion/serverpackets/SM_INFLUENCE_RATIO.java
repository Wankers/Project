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

import gameserver.model.siege.Influence;
import gameserver.network.aion.AionConnection;
import gameserver.network.aion.AionServerPacket;
import gameserver.services.SiegeService;

/**
 * @author Nemiroff
 */
public class SM_INFLUENCE_RATIO extends AionServerPacket {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeImpl(AionConnection con) {
		Influence inf = Influence.getInstance();

		writeD(SiegeService.getInstance().getSecondsBeforeHourEnd());
		writeF(inf.getElyos());
		writeF(inf.getAsmos());
		writeF(inf.getBalaur());

		// TODO: 1.9 has writeH(3) with balauria values
		writeH(1);

		writeD(400010000);
		writeF(inf.getElyos());
		writeF(inf.getAsmos());
		writeF(inf.getBalaur());

	}

}