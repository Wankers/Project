package gameserver.dao;

import commons.database.dao.DAO;
import gameserver.model.gameobjects.player.Player;

public abstract class PortalCooldownsDAO implements DAO {

	/**
	 * Returns unique identifier for PortalCooldownsDAO
	 * 
	 * @return unique identifier for PortalCooldownsDAO
	 */
	@Override
	public final String getClassName() {
		return PortalCooldownsDAO.class.getName();
	}

	/**
	 * @param player
	 */
	public abstract void loadPortalCooldowns(Player player);

	/**
	 * @param player
	 */
	public abstract void storePortalCooldowns(Player player);

}
