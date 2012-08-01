/*
 * This file is part of Aion Extreme Emulator  <aion-core.net>
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
package ai;

import gameserver.ai2.AIName;
import gameserver.model.gameobjects.Creature;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.team2.group.PlayerFilters.SameInstanceFilter;
import gameserver.network.aion.serverpackets.SM_QUEST_ACTION;
import gameserver.utils.PacketSendUtility;

/**
 * @author xTz
 */
@AIName("instancetimer")
public class InstanceTimerAI2 extends AggressiveNpcAI2 {

	private boolean isInTimer = false;
	private long curentTime;

	protected void setInstanceTimer(Creature creature) {

		int time = 0;
		switch (getNpcId()) {
			case 215222:
			case 215221:
			case 215179:
			case 215178:
			case 215136:
			case 215135:
				time = 600000;
				break;
		}

		Player player;
		if (creature instanceof Player) {
			player = (Player) creature;
		}
		else {
			return;
		}
		isInTimer = true;
		curentTime = System.currentTimeMillis();
		sendTime(player, time);
	}

	private void sendTime(Player player, int time) {		
		if (player.isInTeam()) {
			player.getCurrentTeam().sendPacket(new SM_QUEST_ACTION(0, time / 1000), new SameInstanceFilter(player));
		}
		else {
			PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(0, time / 1000));
		}
	}

	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
		if(!isInTimer)
			setInstanceTimer(creature);
	}

	@Override
	public long getRemainigTime() {
		long time = System.currentTimeMillis() - curentTime;
		if (time < 0) {
			return 0;
		}
		return time > 600000 ? 0 : time;
	}
}