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

import gameserver.controllers.SummonController.UnsummonType;
import gameserver.model.gameobjects.Creature;
import gameserver.model.gameobjects.Summon;
import gameserver.model.gameobjects.VisibleObject;
import gameserver.model.gameobjects.player.Player;
import gameserver.network.aion.AionClientPacket;
import gameserver.network.aion.AionConnection.State;

/**
 * @author ATracer
 */
public class CM_SUMMON_COMMAND extends AionClientPacket {

	private int mode;
	private int targetObjId;

	public CM_SUMMON_COMMAND(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	@Override
	protected void readImpl() {
		mode = readC();
		readD();
		readD();
		targetObjId = readD();
	}

	@Override
	protected void runImpl() {
		Player activePlayer = getConnection().getActivePlayer();
		Summon summon = activePlayer.getSummon();
		if (summon != null) {
			switch (mode) {
				case 0:
					VisibleObject obj = summon.getKnownList().getObject(targetObjId);
					if (obj != null && obj instanceof Creature) {
						summon.getController().attackMode();
					}
					break;
				case 1:
					summon.getController().guardMode();
					break;
				case 2:
					summon.getController().restMode();
					break;
				case 3:
					summon.getController().release(UnsummonType.COMMAND);
					break;

			}
		}
	}

}
