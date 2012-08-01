

package mysql5;

import gameserver.dao.ShopDAO;
import gameserver.model.gameobjects.player.Player;
import gameserver.services.item.ItemService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import commons.database.DatabaseFactory;

/**
 * @author Angelis,Thenice and TheLink
 */
public class MySQL5ShopDAO extends ShopDAO {
	
	private static final Logger log = LoggerFactory.getLogger(MySQL5ShopDAO.class);
	private static final String UPDATE_QUERY = "UPDATE inventory_shop SET `taken` = 1 WHERE `id` = ?";
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean supports(String databaseName, int majorVersion, int minorVersion) {
		return MySQL5DAOUtils.supports(databaseName, majorVersion, minorVersion);
	}

	@Override
	public boolean getAllItem(Player player) {
		Connection con = null;
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement pstmt = con.prepareStatement("SELECT id, item_id, quantity FROM inventory_shop WHERE `player_id` = ? AND `taken` = ?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			pstmt.setInt(1, player.getObjectId());
			pstmt.setInt(2, 0);
			ResultSet resultSet = pstmt.executeQuery();
			while (resultSet.next()) {
				if (player.getInventory().isFull()) {
					return false;
				}
				
				ItemService.addItem(player, resultSet.getInt("item_id"), resultSet.getInt("quantity"));
				PreparedStatement updateStatement = con.prepareStatement(UPDATE_QUERY);
				updateStatement.setInt(1, resultSet.getInt("id"));
				updateStatement.executeUpdate();
				updateStatement.close();
			}
		}
		catch (SQLException e) {
			log.error("Can't load item from inventory_shop for player " + player.getName(), e);
			return false;
		}
		finally {
			DatabaseFactory.close(con);
		}
		return true;
	}
}
