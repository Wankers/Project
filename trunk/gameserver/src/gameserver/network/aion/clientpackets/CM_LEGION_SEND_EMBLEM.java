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

import gameserver.model.team.legion.Legion;
import gameserver.model.team.legion.LegionEmblem;
import gameserver.model.team.legion.LegionEmblemType;
import gameserver.network.aion.AionClientPacket;
import gameserver.network.aion.AionConnection.State;
import gameserver.network.aion.serverpackets.SM_LEGION_SEND_EMBLEM;
import gameserver.services.LegionService;

/**
 * @author Simple
 * @modified cura
 */
public class CM_LEGION_SEND_EMBLEM extends AionClientPacket {

	private int legionId;

	public CM_LEGION_SEND_EMBLEM(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void readImpl() {
		legionId = readD();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl() {
		Legion legion = LegionService.getInstance().getLegion(legionId);

		if (legion != null) {
			LegionEmblem legionEmblem = legion.getLegionEmblem();
			if (legionEmblem.getEmblemType() == LegionEmblemType.DEFAULT) {
				sendPacket(new SM_LEGION_SEND_EMBLEM(legionId, legionEmblem.getEmblemId(), legionEmblem.getColor_r(),
					legionEmblem.getColor_g(), legionEmblem.getColor_b(), legion.getLegionName(), legionEmblem.getEmblemType(), 0));
			}
			else {
				sendPacket(new SM_LEGION_SEND_EMBLEM(legionId, legionEmblem.getEmblemId(), legionEmblem.getColor_r(),
					legionEmblem.getColor_g(), legionEmblem.getColor_b(), legion.getLegionName(), legionEmblem.getEmblemType(),
					legionEmblem.getCustomEmblemData().length));
			}
		}
	}
}
