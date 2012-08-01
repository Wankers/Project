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
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gameserver.model.Race;
import gameserver.ShutdownHook;
import gameserver.ShutdownHook.ShutdownMode;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.gameobjects.VisibleObject;
import gameserver.model.templates.spawns.SpawnTemplate;
import gameserver.network.aion.serverpackets.SM_GAME_TIME;
import gameserver.services.teleport.TeleportService;
import gameserver.spawnengine.SpawnEngine;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.ThreadPoolManager;
import gameserver.world.knownlist.Visitor;
import gameserver.world.World;

import java.util.Date;
/**
 * @author ATracer
 */
public class GameTimeService {

	private static Logger log = LoggerFactory.getLogger(GameTimeService.class);

	public static final GameTimeService getInstance() {
		return SingletonHolder.instance;
	}

	private final static int GAMETIME_UPDATE = 3 * 60 * 1000;

	private GameTimeService() {
		/**
		 * Update players with current game time
		 */
		ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				log.info("Sending current game time to all players");
				Iterator<Player> iterator = World.getInstance().getPlayersIterator();
				while (iterator.hasNext()) {
					Player next = iterator.next();
					PacketSendUtility.sendPacket(next, new SM_GAME_TIME());
				}
			}
		}, GAMETIME_UPDATE, GAMETIME_UPDATE);

		log.info("GameTimeService started. Update interval:" + GAMETIME_UPDATE);
	}

	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder {

		protected static final GameTimeService instance = new GameTimeService();
	}
}
