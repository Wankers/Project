package loginserver.controller;

import java.sql.Timestamp;
import java.util.Map;

import javolution.util.FastMap;

import commons.database.dao.DAOManager;
import loginserver.dao.BannedMacDAO;
import loginserver.model.base.BannedMacEntry;

/**
 * 
 * @author KID
 * 
 */
public class BannedMacManager {
	private static BannedMacManager manager = new BannedMacManager();

	public static BannedMacManager getInstance() {
		return manager;
	}

	private BannedMacDAO dao;

	public BannedMacManager() {
		if(dao == null)
			dao = DAOManager.getDAO(BannedMacDAO.class);
		
		bannedList = dao.load();
	}

	private Map<String, BannedMacEntry> bannedList = new FastMap<String, BannedMacEntry>();

	public void unban(String address, String details) {
		if(bannedList.containsKey(address)) {
			bannedList.remove(address);
			dao.remove(address);
		}
	}

	public void ban(String address, long time, String details) {
		BannedMacEntry mac = new BannedMacEntry(address, new Timestamp(time), details);
		this.bannedList.put(address, mac);
		this.dao.update(mac);
	}

	public final Map<String, BannedMacEntry> getMap() {
		return this.bannedList;
	}
}
