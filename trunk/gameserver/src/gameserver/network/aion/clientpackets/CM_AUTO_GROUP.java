/**
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

import gameserver.model.autogroup.EntryRequestType;
import gameserver.model.gameobjects.player.Player;
import gameserver.network.aion.AionClientPacket;
import gameserver.network.aion.AionConnection.State;
import gameserver.services.AutoGroupService2;
import gameserver.services.instance.DredgionService2;

/**
 * @author Shepper, Guapo, nrg
 */
public class CM_AUTO_GROUP extends AionClientPacket {

	private byte instanceMaskId;
	private byte windowId;
	private byte entryRequestId;

	public CM_AUTO_GROUP(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	@Override
	protected void readImpl() {
		instanceMaskId = (byte) readD();
		windowId = (byte) readC();
		entryRequestId = (byte) readC();
	}

	@Override
	protected void runImpl() {
		Player player = getConnection().getActivePlayer();

		switch (windowId) {
			case 100:
				EntryRequestType ert = EntryRequestType.getTypeById(entryRequestId);
				if (ert == null) {
					return;
				}
				AutoGroupService2.getInstance().startLooking(player, instanceMaskId, ert);
				break;
			case 101:
				AutoGroupService2.getInstance().unregisterLooking(player, instanceMaskId);
				break;
			case 102:
				AutoGroupService2.getInstance().enterToInstance(player, instanceMaskId);
				break;
			case 103:
				AutoGroupService2.getInstance().cancelEnter(player, instanceMaskId);
				break;
			case 104:
				DredgionService2.getInstance().showWindow(player, instanceMaskId);
				break;
			case 105:
				// DredgionRegService.getInstance().failedEnterDredgion(player);
				break;
		}
	}
}
