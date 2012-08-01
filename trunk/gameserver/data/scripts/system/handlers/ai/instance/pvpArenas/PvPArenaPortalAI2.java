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
package ai.instance.pvpArenas;

import ai.PortalDialogAI2;
import gameserver.ai2.AIName;
import gameserver.model.autogroup.AutoGroupsType;
import gameserver.model.gameobjects.player.Player;
import gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import gameserver.utils.PacketSendUtility;
import org.joda.time.DateTime;

/**
 *
 * @author xTz
 */
@AIName("pvparenaportal")
public class PvPArenaPortalAI2 extends PortalDialogAI2 {

	@Override
	protected void handleDialogStart(Player player) {
		if (player.getLevel() < 46) {
			return;
		}
		if (!isPvPArenaAvailable()) {
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1401306, AutoGroupsType.
					getAutoGroup(player.getLevel(), getNpcId()).getInstanceMapId()));
			return;
		}
		super.handleDialogStart(player);
	}

	private boolean isPvPArenaAvailable() {
		DateTime now = DateTime.now();
		int hour = now.getHourOfDay();
		int day = now.getDayOfWeek();
		if (day == 6 || day == 7) {
			if (hour == 0 || hour == 1 || (hour >= 10 && hour <= 23)) {
				return true;
			}
		}
		else {
			if (hour == 0 || hour == 1 || hour == 12 || hour == 13 || (hour >= 18 && hour <= 23)) {
				return true;
			}
		}
		return false;
	}
}
