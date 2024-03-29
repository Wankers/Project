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
package gameserver.model.instance;

/**
 *
 * @author xTz
 */
public enum InstanceScoreType {
	PREPARING(1 * 1024 * 1024), // 1048576
	START_PROGRESS(2 * 1024 * 1024), // 2097152
	END_PROGRESS(3 * 1024 * 1024); // 3145728

	private int id;

	private InstanceScoreType(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public boolean isPreparing() {
		return id == 1048576;
	}

	public boolean isStartProgress() {
		return id == 2097152;
	}

	public boolean isEndProgress() {
		return id == 3145728;
	}
}
