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
package gameserver.network.aion.clientpackets;

import gameserver.model.gameobjects.Letter;
import gameserver.model.gameobjects.player.Player;
import gameserver.network.aion.AionClientPacket;
import gameserver.network.aion.AionConnection.State;
import gameserver.network.aion.serverpackets.SM_MAIL_SERVICE;
import gameserver.spawnengine.VisibleObjectSpawner;
import gameserver.utils.PacketSendUtility;

/**
 * @author antness thx to Guapo for sniffing
 */
public class CM_READ_EXPRESS_MAIL extends AionClientPacket {

	private int action;

	public CM_READ_EXPRESS_MAIL(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	@Override
	protected void readImpl() {
		this.action = readC();
	}

	@Override
	protected void runImpl() {
		
		final Player player = getConnection().getActivePlayer();
		boolean haveUnread = player.getMailbox().haveUnread();
		boolean haveUnreadExpress = player.getMailbox().haveUnreadExpress();
		switch (this.action) {
			case 0:
				// window is closed
				if (player.getPostman() != null && !haveUnread) {
					player.getPostman().getController().onDelete();
					player.setPostman(null);
				}
				break;
			case 1:
				// spawn postman
				if (player.getPostman() != null) {
					player.getPostman().getController().onDelete();
					player.setPostman(null);
				}
				if (haveUnreadExpress) {
					VisibleObjectSpawner.spawnPostman(player);

					for (Letter letter : player.getMailbox().getLetters())
						letter.setExpress(false);
				}
				break;
		}
		PacketSendUtility.sendPacket(player, new SM_MAIL_SERVICE(player.getMailbox()));
	}
}
