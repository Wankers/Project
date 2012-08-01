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

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * This class represents an <code>Acceptor</code> that will accept sockets<br>
 * connections dispatched by Accept <code>Dispatcher</code>. <code>Acceptor</code> is attachment<br>
 * of <code>ServerSocketChannel</code> <code>SelectionKey</code> registered on Accept <code>Dispatcher</code>
 * <code>Selector</code>.<br>
 * <code>Acceptor</code> will create new <code>AConnection</code> object using
 * <code>ConnectionFactory.create(SocketChannel socket)</code><br>
 * representing accepted socket, register it into one of ReadWrite <code>Dispatcher</code><br>
 * <code>Selector</code> as ready for io read operations.<br>
 * 
 * @author -Nemesiss-
 * @see commons.network.Dispatcher
 * @see java.nio.channels.ServerSocketChannel
 * @see java.nio.channels.SelectionKey
 * @see java.nio.channels.SocketChannel
 * @see java.nio.channels.Selector
 * @see commons.network.AConnection
 * @see commons.network.ConnectionFactory
 * @see commons.network.NioServer
 */
public class Acceptor {

	/**
	 * <code>ConnectionFactory</code> that will create new <code>AConnection</code>
	 * 
	 * @see commons.network.ConnectionFactory
	 * @see commons.network.AConnection
	 */
	private final ConnectionFactory factory;

	/**
	 * <code>NioServer</code> that created this Acceptor.
	 * 
	 * @see commons.network.NioServer
	 */
	private final NioServer nioServer;

	/**
	 * Constructor that accept <code>ConnectionFactory</code> and <code>NioServer</code> as parameter<br>
	 * 
	 * @param factory
	 *          <code>ConnectionFactory</code> that will be used to<br>
	 * @param nioServer
	 *          <code>NioServer</code> that created this Acceptor object<br>
	 *          creating new <code>AConnection</code> instances.
	 * @see commons.network.ConnectionFactory
	 * @see commons.network.NioServer
	 * @see commons.network.AConnection
	 */
	Acceptor(ConnectionFactory factory, NioServer nioServer) {
		this.factory = factory;
		this.nioServer = nioServer;
	}

	/**
	 * Method called by Accept <code>Dispatcher</code> <code>Selector</code> when socket<br>
	 * connects to <code>ServerSocketChannel</code> listening for connections.<br>
	 * New instance of <code>AConnection</code> will be created by <code>ConnectionFactory</code>,<br>
	 * socket representing accepted connection will be register into<br>
	 * one of ReadWrite <code>Dispatchers</code> <code>Selector as ready for io read operations.<br>
	 * 
	 * @param key
	 *          <code>SelectionKey</code> representing <code>ServerSocketChannel</code> that is accepting<br>
	 *          new socket connection.
	 * @throws IOException
	 * @see commons.network.Dispatcher
	 * @see java.nio.channels.ServerSocketChannel
	 * @see java.nio.channels.SelectionKey
	 * @see java.nio.channels.SocketChannel
	 * @see java.nio.channels.Selector
	 * @see commons.network.AConnection
	 * @see commons.network.ConnectionFactory
	 */
	public final void accept(SelectionKey key) throws IOException {
		/** For an accept to be pending the channel must be a server socket channel. */
		ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
		/** Accept the connection and make it non-blocking */
		SocketChannel socketChannel = serverSocketChannel.accept();
		socketChannel.configureBlocking(false);

		Dispatcher dispatcher = nioServer.getReadWriteDispatcher();
		AConnection con = factory.create(socketChannel, dispatcher);

		// register
		dispatcher.register(socketChannel, SelectionKey.OP_READ, con);
		// notify initialized :)
		con.initialized();
	}
}
