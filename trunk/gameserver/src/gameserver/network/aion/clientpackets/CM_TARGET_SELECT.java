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

import gameserver.model.gameobjects.VisibleObject;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.team2.TeamMember;
import gameserver.network.aion.AionClientPacket;
import gameserver.network.aion.AionConnection.State;
import gameserver.network.aion.serverpackets.SM_TARGET_SELECTED;
import gameserver.network.aion.serverpackets.SM_TARGET_UPDATE;
import gameserver.utils.PacketSendUtility;

/**
 * Client Sends this packet when /Select NAME is typed.<br>
 * I believe it's the same as mouse click on a character.<br>
 * If client want's to select target - d is object id.<br>
 * If client unselects target - d is 0;
 * 
 * @author SoulKeeper, Sweetkr, KID
 */
public class CM_TARGET_SELECT extends AionClientPacket {

	/**
	 * Target object id that client wants to select or 0 if wants to unselect
	 */
	private int targetObjectId;
	private int type;

	/**
	 * Constructs new client packet instance.
	 * 
	 * @param opcode
	 */
	public CM_TARGET_SELECT(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	/**
	 * Read packet.<br>
	 * d - object id; c - selection type;
	 */
	@Override
	protected void readImpl() {
		targetObjectId = readD();
		type = readC();
	}

	/**
	 * Do logging
	 */
	@Override
	protected void runImpl() {
		Player player = getConnection().getActivePlayer();

		VisibleObject obj = null;;
		if (targetObjectId == player.getObjectId())
			obj = player;
		else {
			obj = player.getKnownList().getObject(targetObjectId);

			if(obj == null && player.isInTeam()){
				TeamMember<Player> member = player.getCurrentTeam().getMember(targetObjectId);
				if(member != null){
					obj = member.getObject();
				}
			}
		}

		if (obj != null && obj instanceof VisibleObject) {
			if (type == 1) {
				if (obj.getTarget() == null)
					return;
				player.setTarget(obj.getTarget());
			}
			else {
				player.setTarget(obj);
			}
		}
		else {
			player.setTarget(null);
		}
		sendPacket(new SM_TARGET_SELECTED(player));
		PacketSendUtility.broadcastPacket(player, new SM_TARGET_UPDATE(player));
	}
}
