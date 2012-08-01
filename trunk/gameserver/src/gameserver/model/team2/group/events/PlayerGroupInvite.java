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
package gameserver.model.team2.group.events;

import gameserver.model.gameobjects.Creature;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.gameobjects.player.RequestResponseHandler;
import gameserver.model.team2.group.PlayerGroup;
import gameserver.model.team2.group.PlayerGroupService;
import gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import gameserver.utils.PacketSendUtility;

/**
 * @author ATracer
 */
public class PlayerGroupInvite extends RequestResponseHandler {

	private final Player inviter;
	private final Player invited;

	public PlayerGroupInvite(Player inviter, Player invited) {
		super(inviter);
		this.inviter = inviter;
		this.invited = invited;
	}

	@Override
	public void acceptRequest(Creature requester, Player responder) {
		if (PlayerGroupService.canInvite(inviter, invited)) {
			PacketSendUtility.sendPacket(inviter, SM_SYSTEM_MESSAGE.STR_PARTY_INVITED_HIM(invited.getName()));
			PlayerGroup group = inviter.getPlayerGroup2();
			if (group != null) {
				PlayerGroupService.addPlayer(group, invited);
			}
			else {
				PlayerGroupService.createGroup(inviter, invited);
			}
		}
	}

	@Override
	public void denyRequest(Creature requester, Player responder) {
		PacketSendUtility.sendPacket(inviter, SM_SYSTEM_MESSAGE.STR_PARTY_HE_REJECT_INVITATION(responder.getName()));
	}

}
