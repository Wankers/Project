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
package gameserver.services;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gameserver.model.gameobjects.player.Player;
import gameserver.network.aion.AionConnection;
import gameserver.utils.ThreadPoolManager;
import gameserver.world.World;

/**
 * @author ATracer 
 */
public class DebugService {

	private static final Logger log = LoggerFactory.getLogger(DebugService.class);

	private static final int ANALYZE_PLAYERS_INTERVAL = 30 * 60 * 1000;

	public static final DebugService getInstance() {
		return SingletonHolder.instance;
	}

	private DebugService() {
		ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				analyzeWorldPlayers();
			}

		}, ANALYZE_PLAYERS_INTERVAL, ANALYZE_PLAYERS_INTERVAL);
		log.info("DebugService started. Analyze iterval: " + ANALYZE_PLAYERS_INTERVAL);
	}

	private void analyzeWorldPlayers() {
		log.info("Starting analysis of world players at " + System.currentTimeMillis());

		Iterator<Player> playersIterator = World.getInstance().getPlayersIterator();
		while (playersIterator.hasNext()) {
			Player player = playersIterator.next();

			/**
			 * Check connection
			 */
			AionConnection connection = player.getClientConnection();
			if (connection == null) {
				log.warn(String.format("[DEBUG SERVICE] Player without connection: "
					+ "detected: ObjId %d, Name %s, Spawned %s", player.getObjectId(), player.getName(), player.isSpawned()));
				continue;
			}

			/**
			 * Check CM_PING packet
			 */
			long lastPingTimeMS = connection.getLastPingTimeMS();
			long pingInterval = System.currentTimeMillis() - lastPingTimeMS;
			if (lastPingTimeMS > 0 && pingInterval > 300000) {
				log.warn(String.format("[DEBUG SERVICE] Player with large ping interval: "
					+ "ObjId %d, Name %s, Spawned %s, PingMS %d", player.getObjectId(), player.getName(), player.isSpawned(),
					pingInterval));
			}
		}
	}

	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder {

		protected static final DebugService instance = new DebugService();
	}
}
