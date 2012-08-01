/*
 * This file is part of aion-unique <aion-unique.smfnew.com>.
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import gameserver.model.AbyssRankingResult;
import gameserver.model.Race;
import gameserver.network.aion.AionConnection;
import gameserver.network.aion.AionServerPacket;

/**
 * @author zdead, LokiReborn
 */
public class SM_ABYSS_RANKING_LEGIONS extends AionServerPacket {

	private List<AbyssRankingResult> data;
	private Race race;
	private int updateTime;
	private int sendData = 0;
	
	public SM_ABYSS_RANKING_LEGIONS(int updateTime, ArrayList<AbyssRankingResult> data, Race race) {
		this.updateTime = updateTime;
		this.data = data;
		this.race = race;
		this.sendData = 1;
	}

	public SM_ABYSS_RANKING_LEGIONS(int updateTime, Race race) {
		this.updateTime = updateTime;
		this.data = Collections.emptyList();
		this.race = race;
	}

	@Override
	protected void writeImpl(AionConnection con) {
		writeD(race.getRaceId());// 0:Elyos 1:Asmo
		writeD(updateTime);// Date
		writeD(sendData);// 0:Nothing 1:Update Table
		writeD(sendData);// 0:Nothing 1:Update Table
		writeH(data.size());// list size
		for (AbyssRankingResult rs : data) {
			writeD(rs.getRankPos());// Current Rank
			writeD((rs.getOldRankPos() == 0) ? 76 : rs.getOldRankPos());// Old Rank
			writeD(rs.getLegionId());// Legion Id
			writeD(race.getRaceId());// 0:Elyos 1:Asmo
			writeC(rs.getLegionLevel());// Legion Level
			writeD(rs.getLegionMembers());// Legion Members
			writeQ(rs.getLegionCP());// Contribution Points
			writeS(rs.getLegionName(), 82);// Legion Name
		}
	}
}
