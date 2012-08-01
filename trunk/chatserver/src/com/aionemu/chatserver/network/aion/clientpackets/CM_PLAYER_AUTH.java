/*
 * This file is part of aion-unique <aion-unique.org>.
 *
 *  aion-unique is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-unique is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.chatserver.network.aion.clientpackets;

import java.io.UnsupportedEncodingException;

import org.jboss.netty.buffer.ChannelBuffer;

import com.aionemu.chatserver.network.aion.AbstractClientPacket;
import com.aionemu.chatserver.network.netty.handler.ClientChannelHandler;
import com.aionemu.chatserver.service.ChatService;

/**
 * @author ATracer
 */
public class CM_PLAYER_AUTH extends AbstractClientPacket {

	private ChatService chatService;

	private int playerId;
	private byte[] token;
	private byte[] identifier;
	@SuppressWarnings("unused")
	private byte[] accountName;
	private String realName;

	/**
	 * @param channelBuffer
	 * @param gameChannelHandler
	 * @param opCode
	 */
	public CM_PLAYER_AUTH(ChannelBuffer channelBuffer, ClientChannelHandler clientChannelHandler, ChatService chatService) {
		super(channelBuffer, clientChannelHandler, 0x05);
		this.chatService = chatService;
	}

	@Override
	protected void readImpl() {
		readB(23); //AION stuff
		this.playerId = readD();
		readD(); // 0x00
		readD(); // 0x00
		int length = readH() * 2;
		identifier = readB(length);
		int accountLenght = readH() * 2;
		accountName = readB(accountLenght);
		int tokenLength = readH();
		token = readB(tokenLength);
		byte[] realid = new byte[identifier.length/2];
		int a2 = 0;
		for(int a = 0; a < identifier.length/2; a += 2) {
			realid[a2] = identifier[a]; a2++;
		}
		realName = new String(realid).split("@")[0]; 
		String after = new String(realid).split("@")[1]; 
		try {
			identifier = after.getBytes("utf-16le");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void runImpl() {
		try {
			chatService.registerPlayerConnection(playerId, token, identifier, clientChannelHandler, realName);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
}
