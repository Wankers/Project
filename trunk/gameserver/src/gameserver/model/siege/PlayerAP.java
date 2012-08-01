/*
 * This file is part of Aion Extreme Emulator  <aion-core.net>.
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
package gameserver.model.siege;

import gameserver.model.Race;
import gameserver.model.gameobjects.player.Player;

/**
 * @author antness
 */
public class PlayerAP implements Comparable<PlayerAP> {

	private Player player;
	private Race race;
	private int ap;

	public PlayerAP(Player player) {
		this.player = player;
		this.race = player.getRace();
		this.ap = 0;
	}

	public Player getPlayer() {
		return this.player;
	}

	public Race getRace() {
		return this.race;
	}

	public int getAP() {
		return this.ap;
	}

	public void increaseAP(int ap) {
		this.ap += ap;
	}

	@Override
	public int compareTo(PlayerAP pl) {
		return this.ap - pl.ap;
	}
}
