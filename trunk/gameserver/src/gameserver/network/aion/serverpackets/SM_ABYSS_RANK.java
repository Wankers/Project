/*
 * This file is part of Aion Extreme Emulator <www.aion-core.net>.
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


import gameserver.model.gameobjects.player.AbyssRank;
import gameserver.network.aion.AionConnection;
import gameserver.network.aion.AionServerPacket;
import gameserver.utils.stats.AbyssRankEnum;

/**
 * @author Nemiroff Date: 25.01.2010
 */
public class SM_ABYSS_RANK extends AionServerPacket {

	private AbyssRank rank;
	private int currentRankId;

	public SM_ABYSS_RANK(AbyssRank rank) {
		this.rank = rank;
		this.currentRankId = rank.getRank().getId();
	}

	@Override
	protected void writeImpl(AionConnection con) {
		writeQ(rank.getAp()); // curAP
		writeD(currentRankId); // curRank
		writeD(rank.getTopRanking()); // curRating

		int nextRankId = currentRankId < AbyssRankEnum.values().length ? currentRankId + 1 : currentRankId;
		writeD(100 * rank.getAp() / AbyssRankEnum.getRankById(nextRankId).getRequired()); // exp %

		writeD(rank.getAllKill()); // allKill
		writeD(rank.getMaxRank()); // maxRank

		writeD(rank.getDailyKill()); // dayKill
		writeQ(rank.getDailyAP()); // dayAP

		writeD(rank.getWeeklyKill()); // weekKill
		writeQ(rank.getWeeklyAP()); // weekAP

		writeD(rank.getLastKill()); // laterKill
		writeQ(rank.getLastAP()); // laterAP

		writeC(0x00); // unk
	}
}
