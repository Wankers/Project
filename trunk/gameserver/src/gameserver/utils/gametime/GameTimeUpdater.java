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
package gameserver.utils.gametime;

/**
 * Responsible for updating the clock
 * 
 * @author Ben
 */
public class GameTimeUpdater implements Runnable {

	private GameTime time;

	/**
	 * Constructs GameTimeUpdater to update the given GameTime
	 * 
	 * @param time
	 *          GameTime to update
	 */
	public GameTimeUpdater(GameTime time) {
		this.time = time;
	}

	/**
	 * Increases the time by one minute
	 */
	@Override
	public void run() {
		time.increase();
	}
}
