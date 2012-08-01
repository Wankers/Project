package gameserver.network.loginserver.serverpackets;

import gameserver.network.loginserver.LoginServerConnection;
import gameserver.network.loginserver.LsServerPacket;

/**
 * 
 * @author nrg
 *
 */
public class SM_MAC extends LsServerPacket {

	private int accountId;
	private String address;
	
	public SM_MAC(int accountId, String address)
	{
		super(13);
		this.accountId = accountId;
		this.address = address;
	}

	@Override
	protected void writeImpl(LoginServerConnection con) {
		writeD(accountId);
		writeS(address);
	}
}