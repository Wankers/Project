package gameserver.network.loginserver.clientpackets;

import java.lang.management.ManagementFactory;

import gameserver.network.loginserver.LoginServer;
import gameserver.network.loginserver.LsClientPacket;
import gameserver.network.loginserver.serverpackets.SM_LS_PONG;

/**
 * @author KID
 */
public class CM_LS_PING extends LsClientPacket {
	public CM_LS_PING(int opCode) {
		super(opCode);
	}

	@Override
	protected void readImpl() {
		//trigger
	}

	@Override
	protected void runImpl() {
		int pid = -1;
		try{
			 pid = Integer.parseInt(ManagementFactory.getRuntimeMXBean().getName().split("@")[0]);
		} catch(Exception ex) {  }
		
		LoginServer.getInstance().sendPacket(new SM_LS_PONG(pid));
	}
}
