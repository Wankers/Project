/*
 * This file is part of Aion Extreme  Emulator <aion-core.net>.
 *
 *  This is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This software is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser Public License
 *  along with this software.  If not, see <http://www.gnu.org/licenses/>.
 */
package commons.network;

/**
 * This class represents ServerCfg for configuring NioServer
 * 
 * @author -Nemesiss-
 * @see commons.network.ConnectionFactory
 * @see commons.network.AConnection
 */
public class ServerCfg {

	/**
	 * Host Name on wich we will listen for connections.
	 */
	public final String hostName;
	/**
	 * Port number on wich we will listen for connections.
	 */
	public final int port;
	/**
	 * Connection Name only for logging purposes.
	 */
	public final String connectionName;
	/**
	 * <code>ConnectionFactory</code> that will create <code>AConection</code> object<br>
	 * representing new socket connection.
	 * 
	 * @see commons.network.ConnectionFactory
	 * @see commons.network.AConnection
	 */
	public final ConnectionFactory factory;

	/**
	 * Constructor
	 * 
	 * @param hostName
	 *          - Host Name on witch we will listen for connections.
	 * @param port
	 *          - Port number on witch we will listen for connections.
	 * @param connectionName
	 *          - only for logging purposes.
	 * @param factory
	 *          <code>ConnectionFactory</code> that will create <code>AConection</code> object
	 */
	public ServerCfg(String hostName, int port, String connectionName, ConnectionFactory factory) {
		this.hostName = hostName;
		this.port = port;
		this.connectionName = connectionName;
		this.factory = factory;
	}
}
