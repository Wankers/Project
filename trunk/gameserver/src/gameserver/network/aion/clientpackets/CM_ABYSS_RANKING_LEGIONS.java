/**
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

package gameserver.network.aion.clientpackets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gameserver.model.Race;
import gameserver.model.gameobjects.player.AbyssRank.AbyssRankUpdateType;
import gameserver.model.gameobjects.player.Player;
import gameserver.network.aion.AionClientPacket;
import gameserver.network.aion.AionConnection.State;
import gameserver.network.aion.serverpackets.SM_ABYSS_RANKING_LEGIONS;
import gameserver.services.abyss.AbyssRankingCache;

/**
 * @author SheppeR
 */
public class CM_ABYSS_RANKING_LEGIONS extends AionClientPacket {

	private Race queriedRace;
	private AbyssRankUpdateType updateType;
	private int raceId;

	private static final Logger log = LoggerFactory.getLogger(CM_ABYSS_RANKING_LEGIONS.class);
	public CM_ABYSS_RANKING_LEGIONS(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void readImpl() {
		raceId = readC();
		switch (raceId) {
			case 0:
				queriedRace = Race.ELYOS;
				updateType = AbyssRankUpdateType.LEGION_ELYOS;
				break;
			case 1:
				queriedRace = Race.ASMODIANS;
				updateType = AbyssRankUpdateType.LEGION_ASMODIANS;
				break;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl() {
		// calculate rankings and send packet
		if (queriedRace != null) {
			Player player = this.getConnection().getActivePlayer();
			if (player.isAbyssRankListUpdated(updateType)){
				sendPacket(new SM_ABYSS_RANKING_LEGIONS(AbyssRankingCache.getInstance().getLastUpdate(), queriedRace));
			}
			else {
				SM_ABYSS_RANKING_LEGIONS results = AbyssRankingCache.getInstance().getLegions(queriedRace);
				sendPacket(results);
				player.setAbyssRankListUpdated(updateType);
			}
		}
		else {
			log.warn("Received invalid raceId: " + raceId);
		}
	}
}
