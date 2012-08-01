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
package gameserver.network.aion.serverpackets;


import gameserver.model.gameobjects.Creature;
import gameserver.network.aion.AionConnection;
import gameserver.network.aion.AionServerPacket;

/**
 * @author Sweetkr, xTz
 */
public class SM_TRANSFORM extends AionServerPacket {

	private Creature creature;
	private int state;
	private int modelId;
	private boolean applyEffect;
	private int panelId;

	public SM_TRANSFORM(Creature creature, boolean applyEffect) {
		this.creature = creature;
		this.state = creature.getState();
		modelId = creature.getTransformedModelId();
		this.applyEffect = applyEffect;
	}

	public SM_TRANSFORM(Creature creature, int panelId, boolean applyEffect) {
		this.creature = creature;
		this.state = creature.getState();
		modelId = creature.getTransformedModelId();
		this.panelId = panelId;
		this.applyEffect = applyEffect;
	}

	@Override
	protected void writeImpl(AionConnection con) {
		writeD(creature.getObjectId());
		writeD(modelId);
		writeH(state);
		writeF(0.25f);
		writeF(2.0f);
		writeC(applyEffect ? 0 : 1);
		if (applyEffect) {
			writeD(panelId == 0 ? (creature.getEffectController().checkAvatar() ? 2 : 1) : 3); // 3 is active panel
			writeC(0);
			writeC(0);
			writeC(0);
			writeD(panelId); // display panel
		}
		else {
			writeB(new byte[11]);
		}
	}
}
