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
package loginserver;

import commons.services.CronService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import commons.database.DatabaseFactory;
import loginserver.network.NetConnector;
import loginserver.utils.ThreadPoolManager;

/**
 * @author -Nemesiss-
 */
public class Shutdown extends Thread {

	/**
	 * Logger for this class
	 */
	private static final Logger log = LoggerFactory.getLogger(Shutdown.class);
	/**
	 * Instance of Shutdown.
	 */
	private static Shutdown instance = new Shutdown();

	/**
	 * get the shutdown-hook instance the shutdown-hook instance is created by the first call of this function, but it has
	 * to be registrered externaly.
	 * 
	 * @return instance of Shutdown, to be used as shutdown hook
	 */
	public static Shutdown getInstance() {
		return instance;
	}

	/**
	 * this function is called, when a new thread starts if this thread is the thread of getInstance, then this is the
	 * shutdown hook and we save all data and disconnect all clients. after this thread ends, the server will completely
	 * exit if this is not the thread of getInstance, then this is a countdown thread. we start the countdown, and when we
	 * finished it, and it was not aborted, we tell the shutdown-hook why we call exit, and then call exit when the exit
	 * status of the server is 1, startServer.sh / startServer.bat will restart the server.
	 */
	@Override
	public void run() {
		try
		{
			NetConnector.getInstance().shutdown();
		}
		catch (Throwable t)
		{
			log.error("Can't shutdown NetConnector", t);
		}
		/* Shuting down DB connections */
		try {
			DatabaseFactory.shutdown();
		}
		catch (Throwable t) {
			log.error("Can't shutdown DatabaseFactory", t);
		}

		// shutdown cron service prior to threadpool shutdown
		CronService.getInstance().shutdown();

		/* Shuting down threadpools */
		try {
			ThreadPoolManager.getInstance().shutdown();
		}
		catch (Throwable t) {
			log.error("Can't shutdown ThreadPoolManager", t);
		}
	}
}
