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
package gameserver.network.aion.clientpackets;

import gameserver.model.gameobjects.player.Friend;
import gameserver.model.gameobjects.player.Player;
import gameserver.network.aion.AionClientPacket;
import gameserver.network.aion.AionConnection.State;
import gameserver.network.aion.serverpackets.SM_FRIEND_LIST;

/**
 * Received when a player sets his note
 * 
 * @author Ben
 */
public class CM_SET_NOTE extends AionClientPacket {

	private String note;

	public CM_SET_NOTE(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void readImpl() {
		note = readS();

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl() {
		Player activePlayer = getConnection().getActivePlayer();

		if (!note.equals(activePlayer.getCommonData().getNote())) {

			activePlayer.getCommonData().setNote(note);

			for (Friend friend : activePlayer.getFriendList()) // For all my friends
			{
				Player frienPlayer = friend.getPlayer();
				if (friend.isOnline() && frienPlayer != null) // If the player is online
				{
					friend.getPlayer().getClientConnection().sendPacket(new SM_FRIEND_LIST()); // Send him a new friend list
																																											// packet
				}
			}

		}
	}
}
