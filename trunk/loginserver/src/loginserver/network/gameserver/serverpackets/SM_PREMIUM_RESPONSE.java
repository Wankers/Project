package loginserver.network.gameserver.serverpackets;

import loginserver.network.gameserver.GsConnection;
import loginserver.network.gameserver.GsServerPacket;

/**
 * @author KID
 */
public class SM_PREMIUM_RESPONSE extends GsServerPacket {

	private int requestId;
	private int result;
	private int points;

	public SM_PREMIUM_RESPONSE(int requestId, int result, int points) {
		this.requestId = requestId;
		this.result = result;
		this.points = points;
	}

	@Override
	protected void writeImpl(GsConnection con) {
		writeC(10);
		writeD(requestId);
		writeD(result);
		writeD(points);
	}
}
