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

import gameserver.dataholders.DataManager;
import gameserver.model.templates.spawns.SpawnSearchResult;
import gameserver.network.aion.AionClientPacket;
import gameserver.network.aion.AionConnection.State;
import gameserver.network.aion.serverpackets.SM_SHOW_NPC_ON_MAP;

/**
 * @author Lyahim
 */
public class CM_OBJECT_SEARCH extends AionClientPacket {

	private int npcId;

	/**
	 * Constructs new client packet instance.
	 * 
	 * @param opcode
	 */
	public CM_OBJECT_SEARCH(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);

	}

	/**
	 * Nothing to do
	 */
	@Override
	protected void readImpl() {
		this.npcId = readD();
	}

	/**
	 * Logging
	 */
	@Override
	protected void runImpl() {
		SpawnSearchResult searchResult = DataManager.SPAWNS_DATA2.getFirstSpawnByNpcId(0, npcId);
		if (searchResult != null) {
			sendPacket(new SM_SHOW_NPC_ON_MAP(npcId, searchResult.getWorldId(), searchResult.getSpot().getX(),
				searchResult.getSpot().getY(), searchResult.getSpot().getZ()));
		}
	}
}
