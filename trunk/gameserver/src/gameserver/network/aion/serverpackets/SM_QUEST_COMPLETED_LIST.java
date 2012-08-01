/*
 * This file is part of aion-unique <aionu-unique.org>.
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

import javolution.util.FastList;

import gameserver.dataholders.DataManager;
import gameserver.dataholders.QuestsData;
import gameserver.network.aion.AionConnection;
import gameserver.network.aion.AionServerPacket;
import gameserver.questEngine.model.QuestState;

/**
 * @author MrPoke
 */
public class SM_QUEST_COMPLETED_LIST extends AionServerPacket {

	private FastList<QuestState> questState;

	public SM_QUEST_COMPLETED_LIST(FastList<QuestState> questState) {
		this.questState = questState;
	}

	@Override
	protected void writeImpl(AionConnection con) {
		writeH(0x01); // 2.1
		writeH(-questState.size() & 0xFFFF);
		QuestsData QUEST_DATA = DataManager.QUEST_DATA;
		for (QuestState qs : questState) {
			writeH(qs.getQuestId());
			writeH(QUEST_DATA.getQuestById(qs.getQuestId()).getCategory().getId());
			writeC(qs.getCompleteCount());
		}
		FastList.recycle(questState);
		questState = null;
	}
}
