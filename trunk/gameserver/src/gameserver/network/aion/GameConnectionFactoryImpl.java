/**
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
package gameserver.network.aion;

import java.io.IOException;
import java.nio.channels.SocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import commons.network.AConnection;
import commons.network.ConnectionFactory;
import commons.network.Dispatcher;
import gameserver.configs.network.NetworkConfig;
import gameserver.network.sequrity.FloodManager;
import gameserver.network.sequrity.FloodManager.Result;

/**
 * ConnectionFactory implementation that will be creating AionConnections
 * 
 * @author -Nemesiss-
 */
public class GameConnectionFactoryImpl implements ConnectionFactory {

	private final Logger log = LoggerFactory.getLogger(GameConnectionFactoryImpl.class);
	private FloodManager floodAcceptor;

	/**
	 * Create a new {@link commons.network.AConnection AConnection} instance.<br>
	 * 
	 * @param socket
	 *          that new {@link commons.network.AConnection AConnection} instance will represent.<br>
	 * @param dispatcher
	 *          to witch new connection will be registered.<br>
	 * @return a new instance of {@link commons.network.AConnection AConnection}<br>
	 * @throws IOException
	 * @see commons.network.AConnection
	 * @see commons.network.Dispatcher
	 */
	
	public GameConnectionFactoryImpl()
	{
		if(NetworkConfig.ENABLE_FLOOD_CONNECTIONS)
		{
			floodAcceptor = new FloodManager(NetworkConfig.Flood_Tick,
					new FloodManager.FloodFilter(NetworkConfig.Flood_SWARN, NetworkConfig.Flood_SReject, NetworkConfig.Flood_STick), // short period
					new FloodManager.FloodFilter(NetworkConfig.Flood_LWARN, NetworkConfig.Flood_LReject, NetworkConfig.Flood_LTick)); // long period
		}
	}

	/*
	 * (non-Javadoc)
	 * @see commons.network.ConnectionFactory#create(java.nio.channels.SocketChannel,
	 * commons.network.Dispatcher)
	 */
	@Override
	public AConnection create(SocketChannel socket, Dispatcher dispatcher) throws IOException {
		if(NetworkConfig.ENABLE_FLOOD_CONNECTIONS)
		{
			String host = socket.socket().getInetAddress().getHostAddress();
			final Result isFlooding = floodAcceptor.isFlooding(host, true);
			switch (isFlooding)
			{
				case REJECTED:
				{
					log.warn("Rejected connection from " + host);
					socket.close();
					return null;
				}
				case WARNED:
				{
					log.warn("Connection over warn limit from " + host);
					break;
				}
			}
		}
		
		return new AionConnection(socket, dispatcher);
	}
}
