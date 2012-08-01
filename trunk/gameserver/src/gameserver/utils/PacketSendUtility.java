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
package gameserver.utils;

import commons.objects.filter.ObjectFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import gameserver.model.ChatType;
import gameserver.model.team2.alliance.PlayerAlliance;
import gameserver.model.team2.alliance.PlayerAllianceMember;
import gameserver.model.gameobjects.VisibleObject;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.team2.group.PlayerGroup;
import gameserver.model.team.legion.Legion;
import gameserver.network.aion.AionConnection;
import gameserver.network.aion.AionServerPacket;
import gameserver.network.aion.serverpackets.SM_MESSAGE;
import gameserver.world.World;
import gameserver.world.knownlist.Visitor;
import gameserver.world.zone.SiegeZoneInstance;

/**
 * This class contains static methods, which are utility methods, all of them are interacting only with objects passed
 * as parameters.<br>
 * These methods could be placed directly into Player class, but we want to keep Player class as a pure data holder.<br>
 * 
 * @author Luno
 */
public class PacketSendUtility {

	/**
	 * Global message sending
	 */
	public static void sendMessage(Player player, String msg) {
		sendPacket(player, new SM_MESSAGE(0, null, msg, ChatType.GOLDEN_YELLOW));
	}
	
	public static void sendWhiteMessage(Player player, String msg) {
		sendPacket(player, new SM_MESSAGE(0, null, msg, ChatType.WHITE));
	}
	public static void sendWhiteMessageOnCenter(Player player, String msg) {
		sendPacket(player, new SM_MESSAGE(0, null, msg, ChatType.WHITE_CENTER));
	}
	
	public static void sendYellowMessage(Player player, String msg) {
		sendPacket(player, new SM_MESSAGE(0, null, msg, ChatType.YELLOW));
	}
	public static void sendYellowMessageOnCenter(Player player, String msg) {
		sendPacket(player, new SM_MESSAGE(0, null, msg, ChatType.YELLOW_CENTER));
	}
	
	public static void sendBrightYellowMessage(Player player, String msg) {
		sendPacket(player, new SM_MESSAGE(0, null, msg, ChatType.BRIGHT_YELLOW));
	}
	public static void sendBrightYellowMessageOnCenter(Player player, String msg) {
		sendPacket(player, new SM_MESSAGE(0, null, msg, ChatType.BRIGHT_YELLOW_CENTER));
	}

	/**
	 * Send packet to this player
	 */
	public static void sendPacket(Player player, AionServerPacket packet) {
		if (player.getClientConnection() != null) {
			player.getClientConnection().sendPacket(packet);
		}
	}

	/**
	 * Broadcast packet to all visible players.
	 * 
	 * @param player
	 * @param packet
	 *          ServerPacket that will be broadcast
	 * @param toSelf
	 *          true if packet should also be sent to this player
	 */
	public static void broadcastPacket(Player player, AionServerPacket packet, boolean toSelf) {
		if (toSelf)
			sendPacket(player, packet);

		broadcastPacket(player, packet);
	}

	/**
	 * Broadcast packet to all visible players.
	 * 
	 * @param visibleObject
	 * @param packet
	 */
	public static void broadcastPacketAndReceive(VisibleObject visibleObject, AionServerPacket packet) {
		if (visibleObject instanceof Player)
			sendPacket((Player) visibleObject, packet);

		broadcastPacket(visibleObject, packet);
	}

	/**
	 * Broadcast packet to all Players from knownList of the given visible object.
	 * 
	 * @param visibleObject
	 * @param packet
	 */
	public static void broadcastPacket(VisibleObject visibleObject, final AionServerPacket packet) {
		visibleObject.getKnownList().doOnAllPlayers(new Visitor<Player>() {

			@Override
			public void visit(Player player) {
				if (player.isOnline()) {
					sendPacket(player, packet);
				}
			}
		});
	}

	/**
	 * Broadcasts packet to all visible players matching a filter
	 * 
	 * @param player
	 * @param packet
	 *          ServerPacket to be broadcast
	 * @param toSelf
	 *          true if packet should also be sent to this player
	 * @param filter
	 *          filter determining who should be messaged
	 */
	public static void broadcastPacket(Player player, final AionServerPacket packet, boolean toSelf,
		final ObjectFilter<Player> filter) {
		if (toSelf) {
			sendPacket(player, packet);
		}

		player.getKnownList().doOnAllPlayers(new Visitor<Player>() {

			@Override
			public void visit(Player object) {
				if (filter.acceptObject(object))
					sendPacket(object, packet);
			}
		});
	}
	
	/**
	 * Broadcasts packet to all Players from knownList of the given visible object within the specified distance in meters
	 * 
	 * @param visibleObject
	 * @param packet
	 * @param distance
	 */
	public static void broadcastPacket(final VisibleObject visibleObject, final AionServerPacket packet, final int distance)
	{
		visibleObject.getKnownList().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player p)
			{
				if (MathUtil.isIn3dRange(visibleObject, p, distance))
					sendPacket(p, packet);
			}
		});
	}
	
	/**
	 * Broadcasts packet to ALL players matching a filter
	 * 
	 * @param player
	 * @param packet
	 *          ServerPacket to be broadcast
	 * @param filter
	 *          filter determining who should be messaged
	 */
	public static void broadcastFilteredPacket(final AionServerPacket packet,
		final ObjectFilter<Player> filter) {
		World.getInstance().doOnAllPlayers(new Visitor<Player>() {

			@Override
			public void visit(Player object) {
				if (filter.acceptObject(object))
					sendPacket(object, packet);
			}
		});
	}

	/**
	 * Broadcasts packet to all legion members of a legion
	 * 
	 * @param legion
	 *          Legion to broadcast packet to
	 * @param packet
	 *          ServerPacket to be broadcast
	 */
	public static void broadcastPacketToLegion(Legion legion, AionServerPacket packet) {
		for (Player onlineLegionMember : legion.getOnlineLegionMembers()) {
			sendPacket(onlineLegionMember, packet);
		}
	}

	public static void broadcastPacketToLegion(Legion legion, AionServerPacket packet, int playerObjId) {
		for (Player onlineLegionMember : legion.getOnlineLegionMembers()) {
			if (onlineLegionMember.getObjectId() != playerObjId)
				sendPacket(onlineLegionMember, packet);
		}
	}

	public static void broadcastPacketToGroup(Player paramPlayer, AionServerPacket paramAionServerPacket, boolean paramBoolean)
	{
		PlayerGroup localPlayerGroup = paramPlayer.getPlayerGroup2();
		if (localPlayerGroup == null)
			return;
		Iterator localIterator = localPlayerGroup.getMembers().iterator();
		while (localIterator.hasNext())
		{
			Player localPlayer = (Player)localIterator.next();
			if ((localPlayer == paramPlayer) && (!paramBoolean))
			continue;
				sendPacket(localPlayer, paramAionServerPacket);
		}
	}

	public static void broadcastPacketToAlliance(Player paramPlayer, AionServerPacket paramAionServerPacket, boolean paramBoolean)
	{
		PlayerAlliance localPlayerAlliance = paramPlayer.getPlayerAlliance2();
		if (localPlayerAlliance == null)
			return;
		Iterator localIterator = localPlayerAlliance.getMembers().iterator();
		while (localIterator.hasNext())
		{
			PlayerAllianceMember localPlayerAllianceMember = (PlayerAllianceMember)localIterator.next();
			if ((localPlayerAllianceMember.getPlayer() == paramPlayer) && (!paramBoolean))
        	continue;
				sendPacket(localPlayerAllianceMember.getPlayer(), paramAionServerPacket);
		}
	}
	
	public static void broadcastPacketToZone(SiegeZoneInstance zone, final AionServerPacket packet) {
		zone.doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				sendPacket(player, packet);
				
			}
		});
	}
}
