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
package gameserver.model.team.legion;

/**
 * @author Simple
 */
public enum LegionHistoryType {
	CREATE(0), // No parameters
	JOIN(1), // Parameter: name
	KICK(2), // Parameter: name
	LEVEL_UP(3), // Parameter: legion level
	APPOINTED(4), // Parameter: legion level
	EMBLEM_REGISTER(5), // No parameters
	EMBLEM_MODIFIED(6),
        ADD_ITEM_WAREHOUSE(15),
        TAKE_ITEM_WAREHOUSE(16);// No parameters

	private byte historyType;

	private LegionHistoryType(int historyType) {
		this.historyType = (byte) historyType;
	}

	/**
	 * Returns client-side id for this
	 * 
	 * @return byte
	 */
	public byte getHistoryId() {
		return this.historyType;
	}
}
