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
package gameserver.network.aion.clientpackets;

import commons.objects.filter.ObjectFilter;
import gameserver.configs.main.CustomConfig;
import gameserver.configs.main.LoggingConfig;
import gameserver.model.ChatType;
import gameserver.model.gameobjects.player.Player;
import gameserver.network.aion.AionClientPacket;
import gameserver.network.aion.AionConnection.State;
import gameserver.network.aion.serverpackets.SM_MESSAGE;
import gameserver.restrictions.RestrictionsManager;
import gameserver.services.player.PlayerChatService;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.audit.AuditLogger;
import gameserver.utils.chathandlers.ChatProcessor;

/**
 * Packet that reads normal chat messages.<br>
 *
 * @author SoulKeeper
 */
public class CM_CHAT_MESSAGE_PUBLIC extends AionClientPacket {

	/**
	 * Chat type
	 */
	private ChatType type;

	/**
	 * Chat message
	 */
	private String message;

	public CM_CHAT_MESSAGE_PUBLIC(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	@Override
	protected void readImpl() {
		type = ChatType.getChatTypeByInt(readC());
		message = readS();
	}

	@Override
	protected void runImpl() {

		final Player player = getConnection().getActivePlayer();

		if (ChatProcessor.getInstance().handleChatCommand(player, message))
			return;

		if (LoggingConfig.LOG_CHAT)
			PlayerChatService.chatLogging(player, type, message);

		if (RestrictionsManager.canChat(player) && !PlayerChatService.isFlooding(player)) {
			switch (this.type) {
				case GROUP:
					if (!player.isInTeam())
						return;
					broadcastToGroupMembers(player);
					break;
				case ALLIANCE:
					if (!player.isInAlliance2())
						return;
					broadcastToAllianceMembers(player);
					break;
				case GROUP_LEADER:
					if (!player.isInTeam())
						return;
					// Alert must go to entire group or alliance.
					if (player.isInGroup2())
						broadcastToGroupMembers(player);
					else
						broadcastToAllianceMembers(player);
					break;
				case LEGION:
					broadcastToLegionMembers(player);
					break;
				case LEAGUE:
				case LEAGUE_ALERT:
					if (!player.isInLeague()) {
						return;
					}
					broadcastToLeagueMembers(player);
					break;
				case NORMAL:
				case SHOUT:
					if (player.isGM()) {
						broadcastFromGm(player);
					}
					else {
						if (CustomConfig.SPEAKING_BETWEEN_FACTIONS) {
							broadcastToNonBlockedPlayers(player);
						}
						else {
							broadcastToNonBlockedRacePlayers(player);
						}
					}
					break;
				default:
					if (player.isGM()) {
						broadcastFromGm(player);
					}
					else {
						AuditLogger.info(player, String.format("Send message type %s. Message: %s", type, message));
					}
					break;
			}
		}
	}

	/**
	 * Sends message to all players from admin
	 *
	 * @param player
	 */
	private void broadcastFromGm(final Player player) {
		PacketSendUtility.broadcastPacket(player, new SM_MESSAGE(player, message, type), true);
	}

	/**
	 * Sends message to all players that are not in blocklist
	 *
	 * @param player
	 */
	private void broadcastToNonBlockedPlayers(final Player player) {
		PacketSendUtility.broadcastPacket(player, new SM_MESSAGE(player, message, type), true, new ObjectFilter<Player>() {

			@Override
			public boolean acceptObject(Player object) {
				return !object.getBlockList().contains(player.getObjectId());
			}

		});
	}

	/**
	 * Sends message to races members (other race will receive an unknown message)
	 *
	 * @param player
	 */
	private void broadcastToNonBlockedRacePlayers(final Player player) {
		final int senderRace = player.getRace().getRaceId();
		PacketSendUtility.broadcastPacket(player, new SM_MESSAGE(player, message, type), true, new ObjectFilter<Player>() {

			@Override
			public boolean acceptObject(Player object) {
				return ((senderRace == object.getRace().getRaceId() && !object.getBlockList().contains(player.getObjectId())) || object.isGM());
			}

		});
		PacketSendUtility.broadcastPacket(player, new SM_MESSAGE(player, "Unknow Message", type), false, new ObjectFilter<Player>() {

			@Override
			public boolean acceptObject(Player object) {
				return senderRace != object.getRace().getRaceId() && !object.getBlockList().contains(player.getObjectId()) && !object.isGM();
			}

		});
	}

	/**
	 * Sends message to all group members (regular player group, or alliance
	 * sub-group Error 105, random value for players to report. Should never
	 * happen.
	 *
	 * @param player
	 */
	private void broadcastToGroupMembers(final Player player) {
		if (player.isInTeam()) {
			player.getCurrentGroup().sendPacket(new SM_MESSAGE(player, message, type));
		}
		else {
			PacketSendUtility.sendMessage(player, "You are not in an alliance or group. (Error 105)");
		}
	}

	/**
	 * Sends message to all alliance members
	 *
	 * @param player
	 */
	private void broadcastToAllianceMembers(final Player player) {
		player.getPlayerAlliance2().sendPacket(new SM_MESSAGE(player, message, type));
	}

	/**
	 * Sends message to all league members
	 *
	 * @param player
	 */
	private void broadcastToLeagueMembers(final Player player) {
		player.getPlayerAlliance2().getLeague().sendPacket(new SM_MESSAGE(player, message, type));
	}

	/**
	 * Sends message to all legion members
	 *
	 * @param player
	 */
	private void broadcastToLegionMembers(final Player player) {
		if (player.isLegionMember()) {
			PacketSendUtility.broadcastPacketToLegion(player.getLegion(), new SM_MESSAGE(player, message, type));
		}
	}

}