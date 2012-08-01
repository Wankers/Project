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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gameserver.model.gameobjects.player.Player;
import gameserver.network.aion.serverpackets.SM_CHAT_INIT;
import gameserver.network.chatserver.ChatServer;
import gameserver.utils.PacketSendUtility;
import gameserver.world.World;

/** 
 * @author ATracer
 */
public class ChatService {
	private static final Logger log = LoggerFactory.getLogger(ChatService.class);
	
	private static byte[] ip = { 127, 0, 0, 1 };
	private static int port = 10241;

	/**
	 * Disonnect from chat server
	 * 
	 * @param player
	 */
	public static void onPlayerLogout(Player player) {
		ChatServer.getInstance().sendPlayerLogout(player);
	}

	/**
	 * @param playerId
	 * @param token
	 * @param account 
	 * @param nick 
	 */
	public static void playerAuthed(int playerId, byte[] token) {
		Player player = World.getInstance().findPlayer(playerId);
		if (player != null) {
			PacketSendUtility.sendPacket(player, new SM_CHAT_INIT(token));
		}
	}

	/**
	 * @return the ip
	 */
	public static byte[] getIp() {
		return ip;
	}

	/**
	 * @return the port
	 */
	public static int getPort() {
		return port;
	}

	/**
	 * @param ip
	 *          the ip to set
	 */
	public static void setIp(byte[] _ip) {
		ip = _ip;
	}

	/**
	 * @param port
	 *          the port to set
	 */
	public static void setPort(int _port) {
		port = _port;
	}

	/**
	 * @return the log
	 */
	public static Logger getLog() {
		return log;
	}
}
