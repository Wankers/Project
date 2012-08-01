/*
 *  This file is part of Aion Extreme Emulator <aion-core.net>.
 *
 *  Aion Extreme Emulator is a free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Aion Extreme Emulator is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Aion Extreme Emulator. If not, see <http://www.gnu.org/licenses/>.
 */
package gameserver.dao;

import java.util.ArrayList;
import java.util.Map;

import commons.database.dao.DAO;
import gameserver.model.AbyssRankingResult;
import gameserver.model.Race;
import gameserver.model.gameobjects.player.AbyssRank;
import gameserver.model.gameobjects.player.Player;
import gameserver.utils.stats.AbyssRankEnum;

/**
 * @author ATracer
 */
public abstract class AbyssRankDAO implements DAO {

	@Override
	public final String getClassName() {
		return AbyssRankDAO.class.getName();
	}

	public abstract void loadAbyssRank(Player player);

	public abstract AbyssRank loadAbyssRank(int playerId);

	public abstract boolean storeAbyssRank(Player player);

	public abstract ArrayList<AbyssRankingResult> getAbyssRankingPlayers(Race race);

	public abstract ArrayList<AbyssRankingResult> getAbyssRankingLegions(Race race);

	public abstract Map<Integer, Integer> loadPlayersAp(Race race, final int lowerApLimit);

	public abstract void updateAbyssRank(int playerId, AbyssRankEnum rankEnum);

	public abstract void updateRankList();
}
