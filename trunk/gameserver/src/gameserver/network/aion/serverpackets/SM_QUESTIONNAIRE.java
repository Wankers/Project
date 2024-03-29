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


import gameserver.network.aion.AionConnection;
import gameserver.network.aion.AionServerPacket;

/**
 * Sends Survey HTML data to the client. This packet can be splitted over max 255 packets The max length of the HTML may
 * therefore be 255 * 65525 byte
 * 
 * @author lhw and Kaipo
 */
public class SM_QUESTIONNAIRE extends AionServerPacket {

	private int messageId;
	private byte chunk;
	private byte count;
	private String html;

	public SM_QUESTIONNAIRE(int messageId, byte chunk, byte count, String html) {
		this.messageId = messageId;
		this.chunk = chunk;
		this.count = count;
		this.html = html;
	}

	@Override
	protected void writeImpl(AionConnection con) {
		writeD(messageId);
		writeC(chunk);
		writeC(count);
		writeH(html.length() * 2);
		writeS(html);
	}
}
