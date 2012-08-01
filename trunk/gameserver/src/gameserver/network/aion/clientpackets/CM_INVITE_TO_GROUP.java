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

import gameserver.configs.administration.AdminConfig;
import gameserver.model.gameobjects.player.DeniedStatus;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.team2.alliance.PlayerAllianceService;
import gameserver.model.team2.group.PlayerGroupService;
import gameserver.model.team2.league.LeagueService;
import gameserver.network.aion.AionClientPacket;
import gameserver.network.aion.AionConnection.State;
import gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.Util;
import gameserver.world.World;

/**
 * @author Lyahim, ATracer Modified by Simple
 */
public class CM_INVITE_TO_GROUP extends AionClientPacket {

	private String name;
	private int inviteType;

	public CM_INVITE_TO_GROUP(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void readImpl() {
		inviteType = readC();
		name = readS();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl() {
		//GM Tags by Khaos
		name = name.replace("\uE042" + AdminConfig.CUSTOMTAG_ACCESS1 +"\uE043", "");
		name = name.replace("\uE042" + AdminConfig.CUSTOMTAG_ACCESS2 +"\uE043", "");
		name = name.replace("\uE042" + AdminConfig.CUSTOMTAG_ACCESS3 +"\uE043", "");
		name = name.replace("\uE042" + AdminConfig.CUSTOMTAG_ACCESS4 +"\uE043", "");
		name = name.replace("\uE042" + AdminConfig.CUSTOMTAG_ACCESS5 +"\uE043", "");

		//Account Premium & VIP by Khaos
		name = name.replace("\uE024", "");
		name = name.replace("\uE023", "");
		final String playerName = Util.convertName(name);

		final Player inviter = getConnection().getActivePlayer();
		if (inviter.getLifeStats().isAlreadyDead()) {
			// You cannot issue an invitation while you are dead.
			PacketSendUtility.sendPacket(inviter, new SM_SYSTEM_MESSAGE(1300163));
			return;
		}

		final Player invited = World.getInstance().findPlayer(playerName);
		if (invited != null) {
			if (invited.getPlayerSettings().isInDeniedStatus(DeniedStatus.GROUP)) {
				sendPacket(SM_SYSTEM_MESSAGE.STR_MSG_REJECTED_INVITE_PARTY(invited.getName()));
				return;
			}
			switch (inviteType) {
				case 0:
					PlayerGroupService.inviteToGroup(inviter, invited);
					break;
				case 12: // 2.5
					PlayerAllianceService.inviteToAlliance(inviter, invited);
					break;
				case 28:
					LeagueService.inviteToLeague(inviter, invited);
					break;
				default:
					PacketSendUtility.sendMessage(inviter, "You used an unknown invite type: " + inviteType);
					break;
			}
		}
		else
			inviter.getClientConnection().sendPacket(SM_SYSTEM_MESSAGE.STR_NO_SUCH_USER(name));
	}
}
