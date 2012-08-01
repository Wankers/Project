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

import java.sql.Timestamp;


/**
 * @author Simple
 */
public class LegionHistory {

	private LegionHistoryType legionHistoryType;
	private String name = "";
        private String item = "";
        private Timestamp time;

    public LegionHistory(LegionHistoryType legionHistoryType, String name, String item, Timestamp time) {
        this.legionHistoryType = legionHistoryType;
        this.name = name;
        this.time = time;
        this.item = item;
    }

	/**
	 * @return the legionHistoryType
	 */
	public LegionHistoryType getLegionHistoryType() {
		return legionHistoryType;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
       /**
        * @return the item
        */
        public String getItem() {
                 return item;
        }

	/**
	 * @return the time
	 */
	public Timestamp getTime() {
		return time;
	}
}
