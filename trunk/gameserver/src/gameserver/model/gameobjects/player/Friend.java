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
package gameserver.model.gameobjects.player;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gameserver.model.PlayerClass;
import gameserver.model.gameobjects.player.FriendList.Status;
import gameserver.world.WorldPosition;

/**
 * @author Ben
 */
public class Friend {

	private static final Logger log = LoggerFactory.getLogger(Friend.class);
	private final PlayerCommonData pcd;

	public Friend(PlayerCommonData pcd) {
		this.pcd = pcd;
	}

	/**
	 * Returns the status of this player
	 * 
	 * @return Friend's status
	 */
	public Status getStatus() {
		// second check is temporary
		if (pcd.getPlayer() == null || !pcd.isOnline()) {
			return FriendList.Status.OFFLINE;
		}
		return pcd.getPlayer().getFriendList().getStatus();
	}

	/**
	 * Returns this friend's name
	 * 
	 * @return Friend's name
	 */
	public String getName() {
		return pcd.getName();
	}

	public int getLevel() {
		return pcd.getLevel();
	}

	public String getNote() {
		return pcd.getNote();
	}

	public PlayerClass getPlayerClass() {
		return pcd.getPlayerClass();
	}

	public int getMapId() {
		WorldPosition position = pcd.getPosition();
		if (position == null) {
			// doubt its possible, but need check warnings
			log.warn("Null friend position: {}", pcd.getPlayerObjId());
			return 0;
		}
		return position.getMapId();
	}

	/**
	 * Gets the last time this player was online as a unix timestamp<br />
	 * Returns 0 if the player is online now
	 * 
	 * @return Unix timestamp the player was last online
	 */
	public int getLastOnlineTime() {
		if (pcd.getLastOnline() == null || isOnline())
			return 0;

		return (int) (pcd.getLastOnline().getTime() / 1000); // Convert to int, unix time format (ms -> seconds)
	}

	public int getOid() {
		return pcd.getPlayerObjId();
	}

	public Player getPlayer() {
		return pcd.getPlayer();
	}

	public boolean isOnline() {
		return pcd.isOnline();
	}
}