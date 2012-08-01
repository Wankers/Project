
/**
 * @author Angelis,Thenice and TheLink
 */

package gameserver.dao;

import commons.database.dao.DAO;
import gameserver.model.gameobjects.player.Player;


public abstract class ShopDAO implements DAO {
	
	public abstract boolean getAllItem(Player player);

        @Override
	public String getClassName() {
		return ShopDAO.class.getName();
	}
	
        @Override
	public boolean supports(String databaseName, int majorVersion, int minorVersion) {
		return true;
	}
	
}
