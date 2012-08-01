package gameserver.network.loginserver.serverpackets;

import gameserver.configs.network.NetworkConfig;
import gameserver.network.loginserver.LoginServerConnection;
import gameserver.network.loginserver.LsServerPacket;

/**
 * 
 * @author KID
 *
 */
public class SM_LS_PONG extends LsServerPacket {
	private int pid;
	
	public SM_LS_PONG(int pid) {
		super(12);
		this.pid = pid;
	}

	@Override
	protected void writeImpl(LoginServerConnection con) {
		writeC(NetworkConfig.GAMESERVER_ID);
		writeD(pid);
	}
}
