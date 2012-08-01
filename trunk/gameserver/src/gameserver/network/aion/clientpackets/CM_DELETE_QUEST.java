/**
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
package gameserver.network.aion.clientpackets;

import gameserver.dataholders.DataManager;
import gameserver.dataholders.QuestsData;
import gameserver.model.TaskId;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.templates.QuestTemplate;
import gameserver.network.aion.AionClientPacket;
import gameserver.network.aion.AionConnection.State;
import gameserver.network.aion.serverpackets.SM_QUEST_ACTION;
import gameserver.services.QuestService;

public class CM_DELETE_QUEST extends AionClientPacket {

	static QuestsData questsData = DataManager.QUEST_DATA;
	public int questId;

	public CM_DELETE_QUEST(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	@Override
	protected void readImpl() {
		questId = readH();
	}

	@Override
	protected void runImpl() {
		Player player = getConnection().getActivePlayer();
		QuestTemplate qt = questsData.getQuestById(questId);

		if (qt != null && qt.isTimer()) {
			player.getController().cancelTask(TaskId.QUEST_TIMER);
			sendPacket(new SM_QUEST_ACTION(questId, 0));
		}
		if (!QuestService.abandonQuest(player, questId))
			return;
		player.getController().updateNearbyQuests();
	}
}
