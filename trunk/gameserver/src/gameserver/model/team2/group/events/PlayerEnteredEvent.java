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

import gameserver.model.gameobjects.player.Player;
import gameserver.model.team2.TeamEvent;
import gameserver.model.team2.common.legacy.GroupEvent;
import gameserver.model.team2.group.PlayerGroup;
import gameserver.model.team2.group.PlayerGroupService;
import gameserver.network.aion.serverpackets.SM_GROUP_INFO;
import gameserver.network.aion.serverpackets.SM_GROUP_MEMBER_INFO;
import gameserver.network.aion.serverpackets.SM_INSTANCE_INFO;
import gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import gameserver.utils.PacketSendUtility;
import com.google.common.base.Predicate;

/**
 * @author ATracer
 */
public class PlayerEnteredEvent implements Predicate<Player>, TeamEvent {

	private final PlayerGroup group;
	private final Player enteredPlayer;

	public PlayerEnteredEvent(PlayerGroup group, Player enteredPlayer) {
		this.group = group;
		this.enteredPlayer = enteredPlayer;
	}

	/**
	 * Entered player should not be in group yet
	 */
	@Override
	public boolean checkCondition() {
		return !group.hasMember(enteredPlayer.getObjectId());
	}

	@Override
	public void handleEvent() {
		PlayerGroupService.addPlayerToGroup(group, enteredPlayer);
		PacketSendUtility.sendPacket(enteredPlayer, new SM_GROUP_INFO(group));
		PacketSendUtility.sendPacket(enteredPlayer, new SM_GROUP_MEMBER_INFO(group, enteredPlayer, GroupEvent.JOIN));
		PacketSendUtility.sendPacket(enteredPlayer, SM_SYSTEM_MESSAGE.STR_PARTY_ENTERED_PARTY);
		group.applyOnMembers(this);
	}

	@Override
	public boolean apply(Player player) {
		if (!player.getObjectId().equals(enteredPlayer.getObjectId())) {
			// TODO probably here JOIN event
			PacketSendUtility.sendPacket(player, new SM_GROUP_MEMBER_INFO(group, enteredPlayer, GroupEvent.ENTER));
			PacketSendUtility.sendPacket(player, new SM_INSTANCE_INFO(enteredPlayer, true, true));
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_PARTY_HE_ENTERED_PARTY(enteredPlayer.getName()));

			PacketSendUtility.sendPacket(enteredPlayer, new SM_GROUP_MEMBER_INFO(group, player, GroupEvent.ENTER));
			PacketSendUtility.sendPacket(enteredPlayer, new SM_INSTANCE_INFO(player, true, true));
		}
		return true;
	}

}
