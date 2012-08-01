package loginserver.network;

import commons.network.NioServer;
import commons.network.ServerCfg;
import loginserver.configs.Config;
import loginserver.network.aion.AionConnectionFactoryImpl;
import loginserver.network.gameserver.GsConnectionFactoryImpl;

/**
 * 
 * @author KID
 *
 */
public class NetConnector {
	/**
	 * NioServer instance that will handle io.
	 */
	private final static NioServer instance;

	static {
		ServerCfg aion = new ServerCfg(Config.LOGIN_BIND_ADDRESS, Config.LOGIN_PORT, "Aion Connections",
			new AionConnectionFactoryImpl());

		ServerCfg gs = new ServerCfg(Config.GAME_BIND_ADDRESS, Config.GAME_PORT, "Gs Connections",
			new GsConnectionFactoryImpl());

		instance = new NioServer(Config.NIO_READ_THREADS, gs, aion);
	}

	/**
	 * @return NioServer instance.
	 */
	public static NioServer getInstance() {
		return instance;
	}
}
