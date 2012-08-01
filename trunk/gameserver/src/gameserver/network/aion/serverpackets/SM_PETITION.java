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


import gameserver.model.Petition;
import gameserver.network.aion.AionConnection;
import gameserver.network.aion.AionServerPacket;
import gameserver.services.PetitionService;

/**
 * @author zdead
 */
public class SM_PETITION extends AionServerPacket {

	private Petition petition;

	public SM_PETITION() {
		this.petition = null;
	}

	public SM_PETITION(Petition petition) {
		this.petition = petition;
	}

	@Override
	protected void writeImpl(AionConnection con) {
		if (petition == null) {
			writeD(0x00);
			writeD(0x00);
			writeD(0x00);
			writeD(0x00);
			writeH(0x00);
			writeC(0x00);
		}
		else {
			writeC(0x01); // Action ID ?
			writeD(100); // unk (total online players ?)
			writeH(PetitionService.getInstance().getWaitingPlayers(con.getActivePlayer().getObjectId())); // Users
																																																					// waiting for
																																																					// Support
			writeS(Integer.toString(petition.getPetitionId())); // Ticket ID
			writeH(0x00);
			writeC(50); // Total Petitions
			writeC(49); // Remaining Petitions
			writeH(PetitionService.getInstance().calculateWaitTime(petition.getPlayerObjId())); // Estimated minutes
																																																// before GM reply
			writeD(0x00);
		}
	}
}
