package gameserver.network.loginserver.serverpackets;

import gameserver.network.loginserver.LoginServerConnection;
import gameserver.network.loginserver.LsServerPacket;

/**
 * 
 * @author KID
 *
 */
public class SM_MACBAN_CONTROL extends LsServerPacket {

	private byte type;
	private String address;
	private String details;
	private long time;
	
	public SM_MACBAN_CONTROL(byte type, String address, long time, String details)
	{
		super(10);
		this.type = type;
		this.address = address;
		this.time = time;
		this.details = details;
	}

	@Override
	protected void writeImpl(LoginServerConnection con) {
		writeC(type);
		writeS(address);
		writeS(details);
		writeQ(time);
	}
}
