package com.aionemu.chatserver.network.gameserver;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.network.packet.BaseClientPacket;

/**
 * 
 * @author KID
 *
 */
public abstract class GsClientPacket extends BaseClientPacket<GsConnection> {
	public GsClientPacket(ByteBuffer buffer, GsConnection connection, int opCode) {
		super(opCode);
	}

	private static final Logger log = LoggerFactory.getLogger(GsClientPacket.class);

	@Override
	public final void run() {
		try {
			runImpl();
		}
		catch (Throwable e) {
			log.warn("error handling gs (" + getConnection().getIP() + ") message " + this, e);
		}
	}

	protected void sendPacket(GsServerPacket msg) {
		getConnection().sendPacket(msg);
	}
}
