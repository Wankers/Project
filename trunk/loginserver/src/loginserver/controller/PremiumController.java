package loginserver.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import commons.database.dao.DAOManager;
import loginserver.GameServerInfo;
import loginserver.GameServerTable;
import loginserver.dao.PremiumDAO;
import loginserver.network.gameserver.serverpackets.SM_PREMIUM_RESPONSE;

/**
 * @author KID
 */
public class PremiumController {
	private Logger log = LoggerFactory.getLogger("PREMIUM_CTRL");
	private static PremiumController controller = new PremiumController();
	public static PremiumController getController() {
		return controller;
	}
	
	public static byte RESULT_FAIL = 1;
	public static byte RESULT_LOW_POINTS = 2;
	public static byte RESULT_OK = 3;
	public static byte RESULT_ADD = 4;
	
	private PremiumDAO dao;
	
	public PremiumController() {
		dao = DAOManager.getDAO(PremiumDAO.class);
		log.info("PremiumController is ready for requests.");
	}
	
	public void requestBuy(int accountId, int requestId, int cost, byte serverId) {
		int points = this.dao.getPoints(accountId);
		
		GameServerInfo server = GameServerTable.getGameServerInfo(serverId);
		if(server == null || server.getConnection() == null || !server.isAccountOnGameServer(accountId)) {
			log.error("Account "+accountId+" requested "+requestId+" from gs #"+serverId+" and server is down.");
			return;
		}
		
		//adding new tolls
		if(cost < 0) {
			int ncnt =  points + (cost *-1);
			dao.updatePoints(accountId, ncnt, 0);
			server.getConnection().sendPacket(new SM_PREMIUM_RESPONSE(requestId, RESULT_ADD, ncnt));
			return;
		}
		
		if(points < cost) {
			server.getConnection().sendPacket(new SM_PREMIUM_RESPONSE(requestId, RESULT_LOW_POINTS, points));
			return;
		}
		
		if(dao.updatePoints(accountId, points, cost)) {
			points -= cost;
			server.getConnection().sendPacket(new SM_PREMIUM_RESPONSE(requestId, RESULT_OK, points));
			log.info("Acount "+accountId+" succed in purchasing lot #"+requestId+" for "+cost+" from server #"+serverId);
		}
		else {
			server.getConnection().sendPacket(new SM_PREMIUM_RESPONSE(requestId, RESULT_FAIL, points));
			log.info("Acount "+accountId+" failed in purchasing lot #"+requestId+" for "+cost+" from server #"+serverId+". !updatePoints");
		}
	}
}
