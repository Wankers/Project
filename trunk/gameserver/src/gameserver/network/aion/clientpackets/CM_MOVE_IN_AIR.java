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

import gameserver.model.gameobjects.player.Player;
import gameserver.model.gameobjects.state.CreatureState;
import gameserver.network.aion.AionClientPacket;
import gameserver.network.aion.AionConnection.State;
import gameserver.network.aion.serverpackets.SM_WINDSTREAM;
import gameserver.utils.PacketSendUtility;
import gameserver.world.World;

/**
 * Packet about player flying teleport movement.
 * 
 * @author -Nemesiss-, Sweetkr, KID
 */
public class CM_MOVE_IN_AIR extends AionClientPacket {

	float x, y, z;
	int distance;
	@SuppressWarnings("unused")
	private byte locationId;
	@SuppressWarnings("unused")
	private int worldId;

	/**
	 * Constructs new instance of <tt>CM_MOVE_IN_AIR </tt> packet
	 * 
	 * @param opcode
	 */
	public CM_MOVE_IN_AIR(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void readImpl() {
		worldId = readD();
		x = readF();
		y = readF();
		z = readF();
		locationId = (byte)readC();
		distance = readD();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl() {
		Player player = getConnection().getActivePlayer();
		
		if(player.getEnterWindstream() > 0)
		{
			PacketSendUtility.sendPacket(player, new SM_WINDSTREAM(player.getEnterWindstream(), 1));
			player.setEnterWindstream(0);
		}
		
		if (player.isInState(CreatureState.FLIGHT_TELEPORT)) {
			player.setFlightDistance(distance);
			World.getInstance().updatePosition(player, x, y, z, (byte) 0);
			player.getMoveController().updateLastMove();
		}
	}
}
