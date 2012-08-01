/*
 * This file is part of aion-unique <aion-unique.com>.
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

import gameserver.model.gameobjects.Npc;
import gameserver.model.gameobjects.VisibleObject;
import gameserver.model.gameobjects.player.Player;
import gameserver.network.aion.AionClientPacket;
import gameserver.network.aion.AionConnection.State;
import gameserver.network.aion.serverpackets.SM_LOOKATOBJECT;
import gameserver.utils.MathUtil;
import gameserver.utils.PacketSendUtility;
import gameserver.world.World;

/**
 * @author alexa026, Avol modified by ATracer, GoodT
 */
public class CM_SHOW_DIALOG extends AionClientPacket {

	private int targetObjectId;

	/**
	 * Constructs new instance of <tt>CM_SHOW_DIALOG </tt> packet
	 * 
	 * @param opcode
	 */
	public CM_SHOW_DIALOG(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	@Override
	protected void readImpl() {
		targetObjectId = readD();
	}

	@Override
	protected void runImpl() {
		Player player = getConnection().getActivePlayer();
		if (player.isTrading())
			return;
		
		VisibleObject obj = player.getKnownList().getObject(targetObjectId);
		if(obj == null || player == null || !(obj instanceof Npc))
			return;

		if(!MathUtil.isIn3dRange((Npc)obj, player, 30))
		{
			return;
		}
		
		if (obj instanceof Npc)
		{
			((Npc) obj).setTarget(player);

			//TODO this is not needed for all dialog requests
			PacketSendUtility.broadcastPacket((Npc) obj, new SM_LOOKATOBJECT((Npc) obj));

			((Npc) obj).getController().onDialogRequest(player);
		}		
	}
}
