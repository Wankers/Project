package loginserver.network.gameserver.serverpackets;

import loginserver.network.gameserver.GsConnection;
import loginserver.network.gameserver.GsServerPacket;

/**
 * @author KID
 */
public class SM_PING extends GsServerPacket {
	@Override
	protected void writeImpl(GsConnection con) {
		writeC(11);
	}
}
