/*
 * This file is part of aion-unique <www.aion-unique.com>.
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
package gameserver.network.aion.serverpackets;

import java.util.Collection;

import gameserver.network.aion.AionConnection;
import gameserver.network.aion.AionServerPacket;
import gameserver.skillengine.model.Effect;

/**
 * @author Avol, ATracer
 */
public class SM_ABNORMAL_STATE extends AionServerPacket {

	private Collection<Effect> effects;
	private int abnormals;

	public SM_ABNORMAL_STATE(Collection<Effect> effects, int abnormals) {
		this.effects = effects;
		this.abnormals = abnormals;
	}

	@Override
	protected void writeImpl(AionConnection con) {
		writeD(abnormals);
		writeD(0);
		writeH(effects.size());

		for (Effect effect : effects) {
			writeD(effect.getEffectorId());
			writeH(effect.getSkillId());
			writeC(effect.getSkillLevel());
			writeC(effect.getTargetSlot());
			writeD(effect.getRemainingTime());
		}
	}
}
