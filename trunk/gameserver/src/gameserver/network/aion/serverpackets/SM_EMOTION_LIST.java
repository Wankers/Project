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
package gameserver.network.aion.serverpackets;


import java.util.Collection;

import gameserver.configs.main.MembershipConfig;
import gameserver.model.gameobjects.player.emotion.Emotion;
import gameserver.network.aion.AionConnection;
import gameserver.network.aion.AionServerPacket;

public class SM_EMOTION_LIST extends AionServerPacket {

	byte action;
	Collection<Emotion> emotions;

	/**
	 * @param action
	 */
	public SM_EMOTION_LIST(byte action, Collection<Emotion> emotions) {
		this.action = action;
		this.emotions = emotions;
	}

	@Override
	protected void writeImpl(AionConnection con) {
		writeC(action);
		if (con.getActivePlayer().havePermission(MembershipConfig.EMOTIONS_ALL)) {
			writeH(66);
			for (int i = 0; i < 66; i++) {
				writeH(64 + i);
				writeD(0x00);
			}
		}
		else if (emotions == null || emotions.isEmpty()) {
			writeH(0);
		}
		else {
			writeH(emotions.size());
			for (Emotion emotion : emotions) {
				writeH(emotion.getId());
				writeD(emotion.getRemainingTime());//remaining time
			}
		}
	}
}
