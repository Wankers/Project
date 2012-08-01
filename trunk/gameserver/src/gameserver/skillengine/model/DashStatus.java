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
package gameserver.skillengine.model;

/**
 * @author weiwei
 * @modified VladimirZ
 */
public enum DashStatus {
	NONE(0),
	RANDOMMOVELOC(1),
	DASH(2),
	BACKDASH(3),
	MOVEBEHIND(4);

	private int id;

	private DashStatus(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

}
