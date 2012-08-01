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

import gameserver.model.gameobjects.Gatherable;
import gameserver.model.gameobjects.VisibleObject;
import gameserver.model.gameobjects.player.Player;
import gameserver.network.aion.AionClientPacket;
import gameserver.network.aion.AionConnection.State;

/**
 * @author ATracer
 */
public class CM_GATHER extends AionClientPacket {

	boolean isStartGather = false;

	public CM_GATHER(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	@Override
	protected void readImpl() {
		int action = readD();
		if (action == 0)
			isStartGather = true;
	}

	@Override
	protected void runImpl() {
		Player player = getConnection().getActivePlayer();
		VisibleObject target = player.getTarget();
		if (target != null && target.getPosition().isSpawned() && target instanceof Gatherable)
			if (isStartGather)
				((Gatherable) target).getController().onStartUse(player);
			else
				((Gatherable) target).getController().finishGathering(player);
	}
}
