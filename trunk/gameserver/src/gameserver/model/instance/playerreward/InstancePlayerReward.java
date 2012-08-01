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
package gameserver.model.instance.playerreward;

import gameserver.model.gameobjects.player.Player;

/**
 *
 * @author xTz
 */
public class InstancePlayerReward {
	private Integer obj;
	private int points;
	private int playerPvPKills;
	private int playerMonsterKills;
	private Player player;

	public InstancePlayerReward(Player player) {
		this.player = player;
		this.obj = player.getObjectId();
	}

	public Integer getOwner() {
		return obj;
	}

	public int getPoints() {
		return points;
	}

	public int getPvPKills() {
		return playerPvPKills;
	}

	public int getMonsterKills() {
		return playerMonsterKills;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public Player getPlayer() {
		return player;
	}

	public void addPoints(int points) {
		this.points += points;
		if (this.points < 0) {
			this.points = 0;
		}
	}

	public void addPvPKillToPlayer() {
		playerPvPKills ++;
	}

	public void addMonsterKillToPlayer() {
		playerMonsterKills ++;
	}
}
