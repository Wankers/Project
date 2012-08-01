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
package gameserver.model;

/**
 * @author zdead
 */
public class RankCount {

	private int playerId;
	private int ap;
	private Race race;

	public RankCount(int playerId, int ap, Race race) {
		this.playerId = playerId;
		this.ap = ap;
		this.race = race;
	}

	public int getPlayerId() {
		return playerId;
	}

	public int getPlayerAP() {
		return ap;
	}

	public Race getPlayerRace() {
		return race;
	}
}
